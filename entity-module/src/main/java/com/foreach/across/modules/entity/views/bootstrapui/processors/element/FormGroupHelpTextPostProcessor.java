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

package com.foreach.across.modules.entity.views.bootstrapui.processors.element;

import com.foreach.across.modules.bootstrapui.elements.FormGroupElement;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.elements.TextViewElement;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.val;
import org.apache.commons.lang3.StringUtils;

import static com.foreach.across.modules.bootstrapui.elements.BootstrapUiBuilders.helpBlock;
import static com.foreach.across.modules.bootstrapui.styles.BootstrapStyles.css;

/**
 * Post-processor that resolves a help text for a current property and a {@link FormGroupElement}.
 * By default supports HTML in the resulting message.
 * <p/>
 * This post processor is usually registered automatically when rendering {@link com.foreach.across.modules.entity.views.ViewElementMode#FORM_WRITE}.
 *
 * @author Arne Vandamme
 * @see FieldsetHelpTextPostProcessor
 * @since 3.0.0
 */
@AllArgsConstructor
@NoArgsConstructor
public class FormGroupHelpTextPostProcessor<T extends ViewElement> extends AbstractPropertyDescriptorAwarePostProcessor<T, FormGroupElement>
{
	/**
	 * -- SETTER --
	 * Should HTML be escaped.
	 */
	private boolean escapeHtml;

	@Override
	protected void postProcess( ViewElementBuilderContext builderContext, FormGroupElement element, EntityPropertyDescriptor propertyDescriptor ) {
		if ( element.getHelpBlock() == null ) {
			val text = builderContext.getMessage( "properties." + propertyDescriptor.getName() + "[help]", "" );

			if ( !StringUtils.isEmpty( text ) ) {
				element.setHelpBlock(
						helpBlock().with( css.form.text.suffix( "help" ) )
						           .add( new TextViewElement( text, escapeHtml ) )
						           .build( builderContext )
				);
			}
		}
	}
}
