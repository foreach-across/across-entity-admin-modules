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

/**
 * @author Arne Vandamme
 */
public class HiddenFormElement extends AbstractVoidNodeViewElement implements FormInputElement
{
	public static final String ELEMENT_TYPE = BootstrapUiElements.HIDDEN;

	public HiddenFormElement() {
		super( "input" );
		setAttribute( "type", "hidden" );
	}

	@Override
	public String getControlName() {
		return getAttribute( "name", String.class );
	}

	@Override
	public void setControlName( String controlName ) {
		setAttribute( "name", controlName );
	}

	@Override
	public boolean isDisabled() {
		return hasAttribute( "disabled" );
	}

	@Override
	public void setDisabled( boolean disabled ) {
		if ( disabled ) {
			setAttribute( "disabled", "disabled" );
		}
		else {
			removeAttribute( "disabled" );
		}
	}

	public Object getValue() {
		return getAttribute( "value" );
	}

	public <V> V getValue( Class<V> expectedType ) {
		return getAttribute( "value", expectedType );
	}

	public void setValue( Object value ) {
		setAttribute( "value", value );
	}
}
