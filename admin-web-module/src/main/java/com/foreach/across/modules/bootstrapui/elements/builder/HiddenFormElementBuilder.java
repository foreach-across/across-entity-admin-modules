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
package com.foreach.across.modules.bootstrapui.elements.builder;

import com.foreach.across.modules.bootstrapui.elements.HiddenFormElement;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.elements.builder.AbstractVoidNodeViewElementBuilder;

/**
 * @author Arne Vandamme
 */
public class HiddenFormElementBuilder extends AbstractVoidNodeViewElementBuilder<HiddenFormElement, HiddenFormElementBuilder>
{
	private Boolean disabled;
	private String controlName;
	private Object value;

	public HiddenFormElementBuilder controlName( String controlName ) {
		this.controlName = controlName;
		return this;
	}

	public HiddenFormElementBuilder disabled() {
		return disabled( true );
	}

	public HiddenFormElementBuilder disabled( boolean disabled ) {
		this.disabled = disabled;
		return this;
	}

	public HiddenFormElementBuilder value( Object value ) {
		this.value = value;
		return this;
	}

	@Override
	protected HiddenFormElement createElement( ViewElementBuilderContext builderContext ) {
		HiddenFormElement control = super.apply( new HiddenFormElement(), builderContext );

		if ( controlName != null ) {
			control.setControlName( controlName );
		}
		if ( disabled != null ) {
			control.setDisabled( disabled );
		}
		if ( value != null ) {
			control.setValue( value );
		}

		return control;
	}
}
