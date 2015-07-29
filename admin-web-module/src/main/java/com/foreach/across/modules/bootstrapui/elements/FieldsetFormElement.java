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
import org.springframework.util.CompositeIterator;

import java.util.Collections;
import java.util.Iterator;

/**
 * @author Arne Vandamme
 */
public class FieldsetFormElement extends AbstractNodeViewElement
{
	private Legend legend = new Legend();
	private String fieldsetName;

	public FieldsetFormElement() {
		super( "fieldset" );
	}

	@Override
	public void setName( String name ) {
		super.setName( name );
		if ( fieldsetName == null ) {
			setFieldsetName( name );
			fieldsetName = null;
		}
	}

	public String getFieldsetName() {
		return getAttribute( "name" );
	}

	public void setFieldsetName( String name ) {
		this.fieldsetName = name;
		if ( name != null ) {
			setAttribute( "name", name );
		}
		else {
			removeAttribute( "name" );
		}
	}

	public boolean isDisabled() {
		return hasAttribute( "disabled" );
	}

	public void setDisabled( boolean disabled ) {
		if ( disabled ) {
			setAttribute( "disabled", "disabled" );
		}
		else {
			removeAttribute( "disabled" );
		}
	}

	public String getFormId() {
		return getAttribute( "form" );
	}

	public void setFormId( String formId ) {
		if ( formId != null ) {
			setAttribute( "form", formId );
		}
		else {
			removeAttribute( "form" );
		}
	}

	/**
	 * @return legend for adding custom children or setting legend text
	 */
	public Legend getLegend() {
		return legend;
	}

	public void setLegend( Legend legend ) {
		this.legend = legend;
	}

	@Override
	public Iterator<ViewElement> iterator() {
		if ( legend != null && legend.isEmpty() ) {
			return super.iterator();
		}

		CompositeIterator<ViewElement> elements = new CompositeIterator<>();
		elements.add( Collections.singleton( (ViewElement) legend ).iterator() );
		elements.add( super.iterator() );

		return elements;
	}

	public static class Legend extends AbstractNodeViewElement implements ConfigurableTextViewElement
	{
		private String text;

		public Legend() {
			super( "legend" );
		}

		@Override
		public String getText() {
			return text;
		}

		@Override
		public void setText( String text ) {
			this.text = text;
		}

		@Override
		public boolean isEmpty() {
			return super.isEmpty() && text == null;
		}
	}
}
