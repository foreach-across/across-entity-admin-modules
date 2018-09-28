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
import com.foreach.across.modules.web.ui.elements.AbstractVoidNodeViewElement;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;

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

	/**
	 * Returns a generic {@link FormControlElement} matching this hidden element.
	 * Will be detected by {@link com.foreach.across.modules.bootstrapui.utils.BootstrapElementUtils#getFormControl(ViewElement)},
	 * whereas the regular {@link HiddenFormElement} will not.
	 * <p/>
	 * Can be used to provide a (disabled) errors holder in a {@link FormGroupElement} for example.
	 *
	 * @return hidden element as form control
	 */
	public FormControlElement toFormControl() {
		return new FormControl( this );
	}

	private static class FormControl extends FormControlElementSupport
	{
		FormControl( HiddenFormElement hidden ) {
			super( BootstrapUiElements.GENERIC_FORM_CONTROL );

			setCustomTemplate( hidden.getCustomTemplate() );

			if ( StringUtils.isNotEmpty( hidden.getName() ) ) {
				setName( hidden.getName() );
			}
			if ( StringUtils.isNotEmpty( hidden.getHtmlId() ) ) {
				setHtmlId( hidden.getHtmlId() );
			}

			setAttributes( new HashMap<>( hidden.getAttributes() ) );

			removeAttribute( "name" );
			setControlName( hidden.getControlName() );

			removeAttribute( "disabled" );
			setDisabled( hidden.isDisabled() );
		}
	}
}
