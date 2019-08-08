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

import com.foreach.across.modules.web.ui.elements.AbstractVoidNodeViewElement;

import java.util.Map;

/**
 * Represents a simple icon element that does not support child elements.
 *
 * @author Arne Vandamme
 * @see GlyphIcon
 */
public abstract class IconViewElement extends AbstractVoidNodeViewElement
{
	public static final String ELEMENT_TYPE = BootstrapUiElements.ICON;

	private String iconCss;

	public IconViewElement() {
		super( "span" );
		setElementType( ELEMENT_TYPE );
		setAttribute( "aria-hidden", "true" );
	}

	@Override
	public String getTagName() {
		return super.getTagName();
	}

	@Override
	public IconViewElement setTagName( String tagName ) {
		super.setTagName( tagName );
		return this;
	}

	public String getIconCss() {
		return iconCss;
	}

	protected IconViewElement setIconCss( String iconCss ) {
		this.iconCss = iconCss;
		return this;
	}

	@Override
	public IconViewElement addCssClass( String... cssClass ) {
		super.addCssClass( cssClass );
		return this;
	}

	@Override
	public IconViewElement removeCssClass( String... cssClass ) {
		super.removeCssClass( cssClass );
		return this;
	}

	@Override
	public IconViewElement setAttributes( Map<String, Object> attributes ) {
		super.setAttributes( attributes );
		return this;
	}

	@Override
	public IconViewElement setAttribute( String attributeName, Object attributeValue ) {
		super.setAttribute( attributeName, attributeValue );
		return this;
	}

	@Override
	public IconViewElement addAttributes( Map<String, Object> attributes ) {
		super.addAttributes( attributes );
		return this;
	}

	@Override
	public IconViewElement removeAttribute( String attributeName ) {
		super.removeAttribute( attributeName );
		return this;
	}

	@Override
	public IconViewElement setHtmlId( String htmlId ) {
		super.setHtmlId( htmlId );
		return this;
	}

	@Override
	public IconViewElement setName( String name ) {
		super.setName( name );
		return this;
	}

	@Override
	public IconViewElement setCustomTemplate( String customTemplate ) {
		super.setCustomTemplate( customTemplate );
		return this;
	}

	@Override
	protected IconViewElement setElementType( String elementType ) {
		super.setElementType( elementType );
		return this;
	}
}
