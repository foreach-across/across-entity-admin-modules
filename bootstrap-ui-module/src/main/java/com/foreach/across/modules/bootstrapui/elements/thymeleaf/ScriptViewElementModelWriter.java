/*
 * Copyright 2014 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.foreach.across.modules.bootstrapui.elements.thymeleaf;

import com.foreach.across.modules.bootstrapui.elements.ScriptViewElement;
import com.foreach.across.modules.web.thymeleaf.ThymeleafModelBuilder;
import com.foreach.across.modules.web.ui.elements.thymeleaf.AbstractHtmlViewElementModelWriter;
import liquibase.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.core.NamedThreadLocal;
import org.thymeleaf.model.IModel;
import org.thymeleaf.model.IOpenElementTag;
import org.thymeleaf.model.ITemplateEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Responsible for writing out {@link ScriptViewElement}, representing {@code <script>} HTML nodes.
 * Script nodes may not be safely nested, and this writer will replace any nested script node by a regular
 * HTML node with a {@code data-bum-ref-id} attribute. The replacement node will have most original attributes,
 * but the id attribute holds the {@code HTML id} of an other {@code <script>} element which will contain the
 * actual original content.
 * <p/>
 * Client-side, the {@code BootstrapUiModule.refTarget(node)} can be used to ensure you retrieve the actual target node.
 * <p/>
 * Especially useful for {@link org.springframework.http.MediaType#TEXT_HTML scripts.
 *
 * @author Arne Vandamme
 * @since 2.1.1
 */
public class ScriptViewElementModelWriter extends AbstractHtmlViewElementModelWriter<ScriptViewElement>
{
	private final static ThreadLocal<List<DeferredScript>> writingOrder = new NamedThreadLocal<>( "nestedScriptViewElements" );

	@Override
	public void writeModel( ScriptViewElement viewElement, ThymeleafModelBuilder model ) {
		boolean threadLocalCreated = registerNestedScripts();

		val scripts = writingOrder.get();

		try {
			if ( threadLocalCreated ) {
				super.writeModel( viewElement, model );
			}
			else {
				String id = UUID.randomUUID().toString();
				scripts.add( new DeferredScript( id, viewElement ) );

				String refTagName = viewElement.getRefTagName();

				if ( !StringUtils.isEmpty(refTagName)) {
					model.addOpenElement( viewElement.getRefTagName() );
					model.addAttribute( "style", "display: none; visibility: hidden;" );
					model.addAttribute( "id", model.retrieveHtmlId( viewElement ) );
					viewElement.getAttributes().forEach( model::addAttribute );
					model.addAttribute( "data-bum-ref-id", id );
					model.removeAttribute( "type" );
					model.removeAttribute( "src" );
					model.removeAttribute( "charset" );
					model.removeAttribute( "defer" );
					model.removeAttribute( "async" );
					model.addCloseElement();
				}
			}
		}
		finally {
			if ( threadLocalCreated ) {
				writingOrder.set( null );
			}
		}

		if ( threadLocalCreated ) {
			scripts.forEach( deferredScript -> {
				IModel nestedScriptModel = model.createViewElementModel( deferredScript.script );

				ITemplateEvent event = nestedScriptModel.get( 0 );
				if ( event instanceof IOpenElementTag ) {
					IOpenElementTag elementTag = (IOpenElementTag) event;
					nestedScriptModel.replace( 0, model.getModelFactory().setAttribute( elementTag, "id", deferredScript.id ) );
				}

				model.addModel( nestedScriptModel );
			} );
		}
	}

	private boolean registerNestedScripts() {
		if ( writingOrder.get() == null ) {
			writingOrder.set( new ArrayList<>() );
			return true;
		}

		return false;
	}

	@RequiredArgsConstructor
	private static class DeferredScript
	{
		private final String id;
		private final ScriptViewElement script;
	}
}
