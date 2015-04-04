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
import com.foreach.across.modules.web.ui.elements.NodeViewElementSupport;

/**
 * @author Arne Vandamme
 */
public class FormGroupElement extends NodeViewElementSupport
{
	public static final String ELEMENT_TYPE = BootstrapUiElements.FORM_GROUP;

	private ViewElement label, control;
	private FormLayout formLayout;
	private boolean required;

	public FormGroupElement() {
		super( ELEMENT_TYPE );
	}

	@SuppressWarnings("unchecked")
	public <V extends ViewElement> V getLabel() {
		return (V) label;
	}

	public void setLabel( ViewElement label ) {
		this.label = label;
	}

	@SuppressWarnings("unchecked")
	public <V extends ViewElement> V getControl() {
		return (V) control;
	}

	public void setControl( ViewElement control ) {
		this.control = control;
	}

	public FormLayout getFormLayout() {
		return formLayout;
	}

	public void setFormLayout( FormLayout formLayout ) {
		this.formLayout = formLayout;
	}

	public boolean isRequired() {
		return required;
	}

	public void setRequired( boolean required ) {
		this.required = required;
	}
}
