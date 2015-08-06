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

import com.foreach.across.modules.bootstrapui.elements.FormControlElement;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.elements.AbstractNodeViewElement;
import com.foreach.across.modules.web.ui.elements.builder.AbstractNodeViewElementBuilder;

public abstract class FormControlElementBuilderSupport<T extends AbstractNodeViewElement & FormControlElement, SELF extends FormControlElementBuilderSupport<T, SELF>>
		extends AbstractNodeViewElementBuilder<T, SELF>
{
	private Boolean disabled, readonly, required;
	private String controlName;

	public Boolean getDisabled() {
		return disabled;
	}

	public Boolean getReadonly() {
		return readonly;
	}

	public Boolean getRequired() {
		return required;
	}

	public String getControlName() {
		return controlName;
	}

	@SuppressWarnings("unchecked")
	public SELF controlName( String controlName ) {
		this.controlName = controlName;
		return (SELF) this;
	}

	@SuppressWarnings("unchecked")
	public SELF disabled() {
		return disabled( true );
	}

	@SuppressWarnings("unchecked")
	public SELF disabled( boolean disabled ) {
		this.disabled = disabled;
		return (SELF) this;
	}

	@SuppressWarnings("unchecked")
	public SELF required() {
		return required( true );
	}

	@SuppressWarnings("unchecked")
	public SELF required( boolean required ) {
		this.required = required;
		return (SELF) this;
	}

	@SuppressWarnings("unchecked")
	public SELF readonly() {
		return readonly( true );
	}

	@SuppressWarnings("unchecked")
	public SELF readonly( boolean readonly ) {
		this.readonly = readonly;
		return (SELF) this;
	}

	@Override
	protected T apply( T viewElement, ViewElementBuilderContext builderContext ) {
		T control = super.apply( viewElement, builderContext );

		if ( controlName != null ) {
			control.setControlName( controlName );
		}
		if ( disabled != null ) {
			control.setDisabled( disabled );
		}
		if ( readonly != null ) {
			control.setReadonly( readonly );
		}
		if ( required != null ) {
			control.setRequired( required );
		}

		return control;
	}
}
