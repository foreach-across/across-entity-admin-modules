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

package com.foreach.across.modules.bootstrapui.elements.builder;

import com.foreach.across.modules.bootstrapui.elements.ScriptViewElement;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.elements.builder.AbstractNodeViewElementBuilder;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.http.MediaType;

/**
 * Builder for a {@link ScriptViewElement}.
 *
 * @author Arne Vandamme
 * @see com.foreach.across.modules.bootstrapui.elements.ScriptViewElement
 * @since 2.1.1
 */
@Setter
@Accessors(chain = true, fluent = true)
public class ScriptViewElementBuilder extends AbstractNodeViewElementBuilder<ScriptViewElement, ScriptViewElementBuilder>
{
	private MediaType type;
	private boolean async;
	private boolean defer;
	private String source;
	private String charset;

	@NonNull
	private String refTagName;

	/**
	 * Set the media type for the script tag.
	 *
	 * @param type to set
	 * @return current builder
	 */
	public ScriptViewElementBuilder type( String type ) {
		return this.type( type != null ? MediaType.parseMediaType( type ) : null );
	}

	/**
	 * Set the media type for the script tag.
	 *
	 * @param type to set
	 * @return current builder
	 */
	public ScriptViewElementBuilder type( MediaType type ) {
		this.type = type;
		return this;
	}

	@Override
	protected ScriptViewElement createElement( ViewElementBuilderContext builderContext ) {
		ScriptViewElement script = new ScriptViewElement();

		if ( refTagName != null ) {
			script.setRefTagName( refTagName );
		}

		if ( source != null ) {
			script.setSource( source );
		}

		if( charset != null ) {
			script.setCharset( charset );
		}

		if ( type != null ) {
			script.setType( type );
		}

		script.setAsync( async );
		script.setDefer( defer );

		return apply( script, builderContext );
	}
}
