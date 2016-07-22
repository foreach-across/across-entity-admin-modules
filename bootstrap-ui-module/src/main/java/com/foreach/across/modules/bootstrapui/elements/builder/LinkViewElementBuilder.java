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

import com.foreach.across.modules.bootstrapui.elements.LinkViewElement;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.elements.builder.AbstractNodeViewElementBuilder;

/**
 * @author Arne Vandamme
 * @since 1.0.0
 */
public class LinkViewElementBuilder extends AbstractNodeViewElementBuilder<LinkViewElement, LinkViewElementBuilder>
{
	private String text, title, url;

	/**
	 * Set the link text.
	 *
	 * @param text for the button
	 * @return builder instance
	 */
	public LinkViewElementBuilder text( String text ) {
		this.text = text;
		return this;
	}

	/**
	 * Set the link title attribute.
	 *
	 * @param title attribute
	 * @return builder instance
	 */
	public LinkViewElementBuilder title( String title ) {
		this.title = title;
		return this;
	}

	public LinkViewElementBuilder url( String url ) {
		this.url = url;
		return this;
	}

	@Override
	protected LinkViewElement createElement( ViewElementBuilderContext builderContext ) {
		LinkViewElement link = new LinkViewElement();

		if ( text != null ) {
			link.setText( text );
		}
		if ( url != null ) {
			link.setUrl( url );
		}
		if ( title != null ) {
			link.setTitle( title );
		}

		return apply( link, builderContext );
	}
}
