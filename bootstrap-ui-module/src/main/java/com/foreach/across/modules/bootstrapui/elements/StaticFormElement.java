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

import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.elements.AbstractNodeViewElement;
import com.foreach.across.modules.web.ui.elements.ConfigurableTextViewElement;
import com.foreach.across.modules.web.ui.elements.ContainerViewElement;

import java.util.Collection;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author Arne Vandamme
 */
public class StaticFormElement extends AbstractNodeViewElement implements ConfigurableTextViewElement
{
	public static final String ELEMENT_TYPE = BootstrapUiElements.STATIC_CONTROL;

	private String text;

	public StaticFormElement() {
		super( "p" );
		setElementType( ELEMENT_TYPE );
	}

	@Override
	public String getText() {
		return text;
	}

	@Override
	public StaticFormElement setText( String text ) {
		this.text = text;
		return this;
	}

	@Override
	public StaticFormElement addCssClass( String... cssClass ) {
		super.addCssClass( cssClass );
		return this;
	}

	@Override
	public StaticFormElement removeCssClass( String... cssClass ) {
		super.removeCssClass( cssClass );
		return this;
	}

	@Override
	public StaticFormElement setAttributes( Map<String, Object> attributes ) {
		super.setAttributes( attributes );
		return this;
	}

	@Override
	public StaticFormElement setAttribute( String attributeName, Object attributeValue ) {
		super.setAttribute( attributeName, attributeValue );
		return this;
	}

	@Override
	public StaticFormElement addAttributes( Map<String, Object> attributes ) {
		super.addAttributes( attributes );
		return this;
	}

	@Override
	public StaticFormElement removeAttribute( String attributeName ) {
		super.removeAttribute( attributeName );
		return this;
	}

	@Override
	public StaticFormElement setName( String name ) {
		super.setName( name );
		return this;
	}

	@Override
	public StaticFormElement setCustomTemplate( String customTemplate ) {
		super.setCustomTemplate( customTemplate );
		return this;
	}

	@Override
	protected StaticFormElement setElementType( String elementType ) {
		super.setElementType( elementType );
		return this;
	}

	@Override
	public StaticFormElement addChild( ViewElement element ) {
		super.addChild( element );
		return this;
	}

	@Override
	public StaticFormElement addChildren( Collection<? extends ViewElement> elements ) {
		super.addChildren( elements );
		return this;
	}

	@Override
	public StaticFormElement addFirstChild( ViewElement element ) {
		super.addFirstChild( element );
		return this;
	}

	@Override
	public StaticFormElement clearChildren() {
		super.clearChildren();
		return this;
	}

	@Override
	public StaticFormElement apply( Consumer<ContainerViewElement> consumer ) {
		super.apply( consumer );
		return this;
	}

	@Override
	public <U extends ViewElement> StaticFormElement applyUnsafe( Consumer<U> consumer ) {
		super.applyUnsafe( consumer );
		return this;
	}

	@Override
	protected StaticFormElement setTagName( String tagName ) {
		super.setTagName( tagName );
		return this;
	}

	@Override
	public StaticFormElement setHtmlId( String htmlId ) {
		super.setHtmlId( htmlId );
		return this;
	}

	@Override
	public StaticFormElement set( WitherSetter... setters ) {
		super.set( setters );
		return this;
	}

	@Override
	public StaticFormElement remove( WitherRemover... functions ) {
		super.remove( functions );
		return this;
	}
}
