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

package com.foreach.across.modules.bootstrapui.elements;

import com.foreach.across.modules.web.ui.elements.AbstractNodeViewElement;
import com.foreach.across.modules.web.ui.elements.ConfigurableTextViewElement;

/**
 * @author Arne Vandamme
 * @since 1.0.0
 */
public class LinkViewElement extends AbstractNodeViewElement implements ConfigurableTextViewElement
{
	public static final String ELEMENT_TYPE = BootstrapUiElements.LINK;

	private String text;

	public LinkViewElement() {
		super( ELEMENT_TYPE );
		setTagName( "a" );
		setUrl( "#" );
	}

	@Override
	public String getText() {
		return text;
	}

	@Override
	public void setText( String text ) {
		this.text = text;
	}

	public String getTitle() {
		return getAttribute( "title", String.class );
	}

	/**
	 * Set the title attribute for the element.
	 *
	 * @param title text
	 */
	public void setTitle( String title ) {
		setAttribute( "title", title );
	}

	public String getUrl() {
		return getAttribute( "href", String.class );
	}

	public void setUrl( String url ) {
		setAttribute( "href", url );
	}
}
