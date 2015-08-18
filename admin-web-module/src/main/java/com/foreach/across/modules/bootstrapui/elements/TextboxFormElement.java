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

import com.foreach.across.modules.web.ui.elements.ConfigurableTextViewElement;

import java.util.Objects;

/**
 * @author Arne Vandamme
 */
public class TextboxFormElement extends FormControlElementSupport implements ConfigurableTextViewElement, ConfigurablePlaceholderText
{
	public static final String ELEMENT_TYPE = BootstrapUiElements.TEXTBOX;

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

	public TextboxFormElement() {
		super( ELEMENT_TYPE );
	}

	@Override
	public String getPlaceholder() {
		return placeholder;
	}

	@Override
	public void setPlaceholder( String placeholder ) {
		this.placeholder = placeholder;
	}

	public Type getType() {
		return type;
	}

	public void setType( Type type ) {
		this.type = type;
	}

	@Override
	public String getText() {
		return text;
	}

	@Override
	public void setText( String text ) {
		this.text = text;
	}

	public Integer getMaxLength() {
		return maxLength;
	}

	public void setMaxLength( Integer maxLength ) {
		this.maxLength = maxLength;
	}
}
