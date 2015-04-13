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
import com.foreach.across.modules.web.ui.elements.NodeViewElementSupport;

/**
 * Represents a HTML select element.
 *
 * @author Arne Vandamme
 */
public class SelectFormElement extends FormControlElementSupport
{
	/**
	 * Single option.
	 */
	public static class Option extends NodeViewElementSupport implements ConfigurableTextViewElement
	{
		private boolean disabled, selected;
		private String text, label;
		private Object value;

		public Option() {
			super( SelectFormElement.ELEMENT_TYPE + ".option" );
		}

		public boolean isDisabled() {
			return disabled;
		}

		public void setDisabled( boolean disabled ) {
			this.disabled = disabled;
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
	public static class OptionGroup extends NodeViewElementSupport
	{
		private boolean disabled;
		private String label;

		public OptionGroup() {
			super( SelectFormElement.ELEMENT_TYPE + ".optgroup" );
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

	public static final String ELEMENT_TYPE = BootstrapUiElements.SELECT;

	private boolean multiple;

	public SelectFormElement() {
		super( ELEMENT_TYPE );
	}

	public boolean isMultiple() {
		return multiple;
	}

	public void setMultiple( boolean multiple ) {
		this.multiple = multiple;
	}
}
