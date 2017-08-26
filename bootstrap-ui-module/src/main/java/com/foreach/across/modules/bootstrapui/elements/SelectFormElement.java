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
 * Represents a HTML select element.  Supports both a default HTML select, and the more advanced bootstrap-select.
 * The latter is activated by setting a {@link SelectFormElementConfiguration} using {@link #setConfiguration(SelectFormElementConfiguration)}.
 *
 * @author Arne Vandamme
 * @see SelectFormElementConfiguration
 */
public class SelectFormElement extends FormControlElementSupport
{
	public static final String ELEMENT_TYPE = BootstrapUiElements.SELECT;
	public static final String ATTRIBUTE_DATA_SELECT = "data-bootstrapui-select";

	private boolean multiple;

	public SelectFormElement() {
		super( ELEMENT_TYPE );
		setTagName( "select" );
	}

	/**
	 * Get the attached bootstrap-select configuration if there is any.
	 *
	 * @return configuration or {@code null} in case of a simple HTML select
	 */
	public SelectFormElementConfiguration getConfiguration() {
		return getAttribute( ATTRIBUTE_DATA_SELECT, SelectFormElementConfiguration.class );
	}

	/**
	 * Set a bootstrap-select configuration.  If a non-null value is provided, the select
	 * will be converted into a bootstrap-select if the client-side resources have been registered.
	 *
	 * @param configuration to use
	 */
	public void setConfiguration( SelectFormElementConfiguration configuration ) {
		setAttribute( ATTRIBUTE_DATA_SELECT, configuration );
	}

	public boolean isMultiple() {
		return multiple;
	}

	public void setMultiple( boolean multiple ) {
		this.multiple = multiple;
	}

	/**
	 * Single option.
	 */
	public static class Option extends FormControlElementSupport implements ConfigurableTextViewElement
	{
		private boolean selected;
		private String text, label;
		private Object value;

		public Option() {
			super( SelectFormElement.ELEMENT_TYPE + ".option" );
			setTagName( "option" );
		}

		public boolean isSelected() {
			return selected;
		}

		public void setSelected( boolean selected ) {
			this.selected = selected;
		}

		@Override
		public String getText() {
			return text;
		}

		@Override
		public void setText( String text ) {
			this.text = text;
		}

		public String getLabel() {
			return label;
		}

		public void setLabel( String label ) {
			this.label = label;
		}

		public Object getValue() {
			return value;
		}

		public void setValue( Object value ) {
			this.value = value;
		}
	}

	/**
	 * Optgroup.
	 */
	public static class OptionGroup extends AbstractNodeViewElement
	{
		private boolean disabled;
		private String label;

		public OptionGroup() {
			super( "optgroup" );
			setElementType( SelectFormElement.ELEMENT_TYPE + ".optgroup" );
		}

		public boolean isDisabled() {
			return disabled;
		}

		public void setDisabled( boolean disabled ) {
			this.disabled = disabled;
		}

		public String getLabel() {
			return label;
		}

		public void setLabel( String label ) {
			this.label = label;
		}
	}
}
