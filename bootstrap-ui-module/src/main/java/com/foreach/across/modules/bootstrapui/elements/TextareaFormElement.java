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
import com.foreach.across.modules.web.ui.elements.ContainerViewElement;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Collection;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Represents HTML textarea.
 *
 * @author Arne Vandamme
 */
@Accessors(chain = true)
@Getter
@Setter
public class TextareaFormElement extends TextboxFormElement
{
	public static final String ELEMENT_TYPE = BootstrapUiElements.TEXTAREA;
	public static final String CSS_AUTOSIZE = "js-autosize";

	public static class Type
	{
		public static TextboxFormElement.Type TEXTAREA = new TextboxFormElement.Type( "textarea" );
	}

	private int rows = 3;
	private boolean autoSize = true;

	public TextareaFormElement() {
		setTagName( "textarea" );
		setElementType( ELEMENT_TYPE );
		setType( Type.TEXTAREA );
		setAttribute( BootstrapUiViewElementAttributes.CONTROL_ADAPTER_TYPE, "basic" );
	}

	@Override
	public TextareaFormElement setType( TextboxFormElement.Type type ) {
		super.setType( type );
		return this;
	}

	@Override
	public TextareaFormElement setPlaceholder( String placeholder ) {
		super.setPlaceholder( placeholder );
		return this;
	}

	@Override
	public TextareaFormElement setText( String text ) {
		super.setText( text );
		return this;
	}

	@Override
	public TextareaFormElement setMaxLength( Integer maxLength ) {
		super.setMaxLength( maxLength );
		return this;
	}

	@Override
	public TextareaFormElement setDisableLineBreaks( boolean disableLineBreaks ) {
		super.setDisableLineBreaks( disableLineBreaks );
		return this;
	}

	@Override
	public TextareaFormElement setDisabled( boolean disabled ) {
		super.setDisabled( disabled );
		return this;
	}

	@Override
	public TextareaFormElement setReadonly( boolean readonly ) {
		super.setReadonly( readonly );
		return this;
	}

	@Override
	public TextareaFormElement setRequired( boolean required ) {
		super.setRequired( required );
		return this;
	}

	@Override
	public TextareaFormElement setName( String name ) {
		super.setName( name );
		return this;
	}

	@Override
	public TextareaFormElement setControlName( String controlName ) {
		super.setControlName( controlName );
		return this;
	}

	@Override
	public TextareaFormElement setHtmlId( String htmlId ) {
		super.setHtmlId( htmlId );
		return this;
	}

	@Override
	public TextareaFormElement addCssClass( String... cssClass ) {
		super.addCssClass( cssClass );
		return this;
	}

	@Override
	public TextareaFormElement removeCssClass( String... cssClass ) {
		super.removeCssClass( cssClass );
		return this;
	}

	@Override
	public TextareaFormElement setAttributes( Map<String, Object> attributes ) {
		super.setAttributes( attributes );
		return this;
	}

	@Override
	public TextareaFormElement setAttribute( String attributeName, Object attributeValue ) {
		super.setAttribute( attributeName, attributeValue );
		return this;
	}

	@Override
	public TextareaFormElement addAttributes( Map<String, Object> attributes ) {
		super.addAttributes( attributes );
		return this;
	}

	@Override
	public TextareaFormElement removeAttribute( String attributeName ) {
		super.removeAttribute( attributeName );
		return this;
	}

	@Override
	public TextareaFormElement setCustomTemplate( String customTemplate ) {
		super.setCustomTemplate( customTemplate );
		return this;
	}

	@Override
	protected TextareaFormElement setElementType( String elementType ) {
		super.setElementType( elementType );
		return this;
	}

	@Override
	public TextareaFormElement addChild( ViewElement element ) {
		super.addChild( element );
		return this;
	}

	@Override
	public TextareaFormElement addChildren( Collection<? extends ViewElement> elements ) {
		super.addChildren( elements );
		return this;
	}

	@Override
	public TextareaFormElement addFirstChild( ViewElement element ) {
		super.addFirstChild( element );
		return this;
	}

	@Override
	public TextareaFormElement clearChildren() {
		super.clearChildren();
		return this;
	}

	@Override
	public TextareaFormElement apply( Consumer<ContainerViewElement> consumer ) {
		super.apply( consumer );
		return this;
	}

	@Override
	public <U extends ViewElement> TextareaFormElement applyUnsafe( Consumer<U> consumer ) {
		super.applyUnsafe( consumer );
		return this;
	}

	@Override
	protected TextareaFormElement setTagName( String tagName ) {
		super.setTagName( tagName );
		return this;
	}
}
