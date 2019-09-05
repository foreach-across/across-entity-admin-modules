/*
 * Copyright 2019 the original author or authors
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

import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.elements.AbstractTextNodeViewElement;
import com.foreach.across.modules.web.ui.elements.ContainerViewElement;

import java.util.Collection;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author Arne Vandamme
 * @since 1.0.0
 */
public class LinkViewElement extends AbstractTextNodeViewElement
{
	public static final String ELEMENT_TYPE = BootstrapUiElements.LINK;

	public LinkViewElement() {
		super( ELEMENT_TYPE );
		setTagName( "a" );
		setUrl( "#" );
	}

	public String getTitle() {
		return getAttribute( "title", String.class );
	}

	/**
	 * Set the title attribute for the element.
	 *
	 * @param title text
	 */
	public LinkViewElement setTitle( String title ) {
		return setAttribute( "title", title );
	}

	public String getUrl() {
		return getAttribute( "href", String.class );
	}

	public LinkViewElement setUrl( String url ) {
		return setAttribute( "href", url );
	}

	@Override
	protected LinkViewElement setTagName( String tagName ) {
		super.setTagName( tagName );
		return this;
	}

	@Override
	public LinkViewElement setHtmlId( String htmlId ) {
		super.setHtmlId( htmlId );
		return this;
	}

	@Override
	public LinkViewElement addCssClass( String... cssClass ) {
		super.addCssClass( cssClass );
		return this;
	}

	@Override
	public LinkViewElement removeCssClass( String... cssClass ) {
		super.removeCssClass( cssClass );
		return this;
	}

	@Override
	public LinkViewElement setAttributes( Map<String, Object> attributes ) {
		super.setAttributes( attributes );
		return this;
	}

	@Override
	public LinkViewElement setAttribute( String attributeName, Object attributeValue ) {
		super.setAttribute( attributeName, attributeValue );
		return this;
	}

	@Override
	public LinkViewElement addAttributes( Map<String, Object> attributes ) {
		super.addAttributes( attributes );
		return this;
	}

	@Override
	public LinkViewElement removeAttribute( String attributeName ) {
		super.removeAttribute( attributeName );
		return this;
	}

	@Override
	public LinkViewElement setName( String name ) {
		super.setName( name );
		return this;
	}

	@Override
	public LinkViewElement setCustomTemplate( String customTemplate ) {
		super.setCustomTemplate( customTemplate );
		return this;
	}

	@Override
	protected LinkViewElement setElementType( String elementType ) {
		super.setElementType( elementType );
		return this;
	}

	@Override
	public LinkViewElement addChild( ViewElement element ) {
		super.addChild( element );
		return this;
	}

	@Override
	public LinkViewElement addChildren( Collection<? extends ViewElement> elements ) {
		super.addChildren( elements );
		return this;
	}

	@Override
	public LinkViewElement addFirstChild( ViewElement element ) {
		super.addFirstChild( element );
		return this;
	}

	@Override
	public LinkViewElement clearChildren() {
		super.clearChildren();
		return this;
	}

	@Override
	public LinkViewElement apply( Consumer<ContainerViewElement> consumer ) {
		super.apply( consumer );
		return this;
	}

	@Override
	public <U extends ViewElement> LinkViewElement applyUnsafe( Consumer<U> consumer ) {
		super.applyUnsafe( consumer );
		return this;
	}

	@Override
	public LinkViewElement setText( String text ) {
		super.setText( text );
		return this;
	}

	@Override
	public LinkViewElement set( WitherSetter... setters ) {
		super.set( setters );
		return this;
	}

	@Override
	public LinkViewElement remove( WitherRemover... functions ) {
		super.remove( functions );
		return this;
	}
}
