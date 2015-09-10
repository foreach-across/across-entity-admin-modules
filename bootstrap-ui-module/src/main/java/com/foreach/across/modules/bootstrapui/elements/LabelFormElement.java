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

/**
 * @author Arne Vandamme
 */
public class LabelFormElement extends AbstractNodeViewElement implements ConfigurableTextViewElement
{
	public static final String ELEMENT_TYPE = BootstrapUiElements.LABEL;

	private String text;
	private Object forTarget;

	public LabelFormElement() {
		super( "label" );
		setElementType( ELEMENT_TYPE );
	}

	@Override
	public String getText() {
		return text;
	}

	@Override
	public void setText( String text ) {
		this.text = text;
	}

	public void setTarget( String fixedId ) {
		forTarget = fixedId;
	}

	public void setTarget( ViewElement viewElement ) {
		forTarget = viewElement;
	}

	/**
	 * @return True if the target of this label is specified manually with an id.
	 * False if a {@link com.foreach.across.modules.web.ui.ViewElement} was provided or target is not set.
	 */
	public boolean isTargetId() {
		return forTarget instanceof String;
	}

	public boolean hasTarget() {
		return forTarget != null;
	}

	public String getTargetAsId() {
		return (String) forTarget;
	}

	public ViewElement getTargetAsElement() {
		return (ViewElement) forTarget;
	}
}
