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
import com.foreach.across.modules.web.ui.elements.ConfigurableTextViewElement;
import com.foreach.across.modules.web.ui.elements.ContainerViewElement;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * @author Arne Vandamme
 */
@Accessors(chain = true)
@Getter
@Setter
public class TextboxFormElement extends FormControlElementSupport implements ConfigurableTextViewElement, ConfigurablePlaceholderText
{
	public static final String ELEMENT_TYPE = BootstrapUiElements.TEXTBOX;
	public static final String CSS_DISABLE_LINE_BREAKS = "js-disable-line-breaks";

	public static class Type
	{
		public static final Type TEXT = new Type( "text" );
		public static final Type PASSWORD = new Type( "password" );
		public static final Type DATETIME = new Type( "datetime" );
		public static final Type DATETIME_LOCAL = new Type( "datetime-local" );
		public static final Type DATE = new Type( "date" );
		public static final Type MONTH = new Type( "month" );
		public static final Type TIME = new Type( "time" );
		public static final Type WEEK = new Type( "week" );
		public static final Type NUMBER = new Type( "number" );
		public static final Type EMAIL = new Type( "email" );
		public static final Type URL = new Type( "url" );
		public static final Type SEARCH = new Type( "search" );
		public static final Type TEL = new Type( "tel" );
		public static final Type COLOR = new Type( "color" );

		private final String name;

		public Type( String name ) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

		@Override
		public boolean equals( Object o ) {
			if ( this == o ) {
				return true;
			}
			if ( o == null || getClass() != o.getClass() ) {
				return false;
			}

			Type type = (Type) o;

			return Objects.equals( name, type.name );
		}

		@Override
		public int hashCode() {
			return name != null ? name.hashCode() : 0;
		}

		@Override
		public String toString() {
			return "Type{" +
					"name='" + name + '\'' +
					'}';
		}
	}

	private Type type = Type.TEXT;
	private String placeholder, text;
	private Integer maxLength;
	private boolean disableLineBreaks;

	public TextboxFormElement() {
		super( ELEMENT_TYPE );
		setAttribute( BootstrapUiViewElementAttributes.CONTROL_ADAPTER_TYPE, "basic" );
	}

	@Override
	public TextboxFormElement setDisabled( boolean disabled ) {
		super.setDisabled( disabled );
		return this;
	}

	@Override
	public TextboxFormElement setReadonly( boolean readonly ) {
		super.setReadonly( readonly );
		return this;
	}

	@Override
	public TextboxFormElement setRequired( boolean required ) {
		super.setRequired( required );
		return this;
	}

	@Override
	public TextboxFormElement setName( String name ) {
		super.setName( name );
		return this;
	}

	@Override
	public TextboxFormElement setControlName( String controlName ) {
		super.setControlName( controlName );
		return this;
	}

	@Override
	public TextboxFormElement setHtmlId( String htmlId ) {
		super.setHtmlId( htmlId );
		return this;
	}

	@Override
	public TextboxFormElement addCssClass( String... cssClass ) {
		super.addCssClass( cssClass );
		return this;
	}

	@Override
	public TextboxFormElement removeCssClass( String... cssClass ) {
		super.removeCssClass( cssClass );
		return this;
	}

	@Override
	public TextboxFormElement setAttributes( Map<String, Object> attributes ) {
		super.setAttributes( attributes );
		return this;
	}

	@Override
	public TextboxFormElement setAttribute( String attributeName, Object attributeValue ) {
		super.setAttribute( attributeName, attributeValue );
		return this;
	}

	@Override
	public TextboxFormElement addAttributes( Map<String, Object> attributes ) {
		super.addAttributes( attributes );
		return this;
	}

	@Override
	public TextboxFormElement removeAttribute( String attributeName ) {
		super.removeAttribute( attributeName );
		return this;
	}

	@Override
	public TextboxFormElement setCustomTemplate( String customTemplate ) {
		super.setCustomTemplate( customTemplate );
		return this;
	}

	@Override
	protected TextboxFormElement setElementType( String elementType ) {
		super.setElementType( elementType );
		return this;
	}

	@Override
	public TextboxFormElement addChild( ViewElement element ) {
		super.addChild( element );
		return this;
	}

	@Override
	public TextboxFormElement addChildren( Collection<? extends ViewElement> elements ) {
		super.addChildren( elements );
		return this;
	}

	@Override
	public TextboxFormElement addFirstChild( ViewElement element ) {
		super.addFirstChild( element );
		return this;
	}

	@Override
	public TextboxFormElement clearChildren() {
		super.clearChildren();
		return this;
	}

	@Override
	public TextboxFormElement apply( Consumer<ContainerViewElement> consumer ) {
		super.apply( consumer );
		return this;
	}

	@Override
	public <U extends ViewElement> TextboxFormElement applyUnsafe( Consumer<U> consumer ) {
		super.applyUnsafe( consumer );
		return this;
	}

	@Override
	protected TextboxFormElement setTagName( String tagName ) {
		super.setTagName( tagName );
		return this;
	}
}
