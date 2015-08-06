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
import com.foreach.across.modules.web.ui.elements.NodeViewElement;
import com.mysema.commons.lang.Assert;

import java.util.Iterator;

/**
 * Extension of an {@link InputGroupFormElement} that represents a date/time picker.
 * By default this is an input group with a calendar icon after the control element,
 * and a {@link TextboxFormElement} as control.
 *
 * @author Arne Vandamme
 */
public class DateTimeFormElement extends InputGroupFormElement implements FormControlElement
{
	private NodeViewElement hidden;

	public DateTimeFormElement() {
		setControl( new TextboxFormElement() );

		hidden = new NodeViewElement( "input" );
		hidden.setAttribute( "type", "hidden" );

		setAddonAfter( new GlyphIcon( GlyphIcon.CALENDAR ) );
		addCssClass( "date" );
		add( hidden );
	}

	@Override
	public void setControl( ViewElement control ) {
		Assert.isTrue( control instanceof FormControlElement, "Only FormControlElement implementations are allowed." );
		super.setControl( control );
	}

	@Override
	public boolean isDisabled() {
		return getControl( FormControlElement.class ).isDisabled();
	}

	@Override
	public void setDisabled( boolean disabled ) {
		getControl( FormControlElement.class ).setDisabled( disabled );
	}

	@Override
	public boolean isReadonly() {
		return getControl( FormControlElement.class ).isReadonly();
	}

	@Override
	public void setReadonly( boolean readonly ) {
		getControl( FormControlElement.class ).setReadonly( readonly );
	}

	@Override
	public boolean isRequired() {
		return getControl( FormControlElement.class ).isRequired();
	}

	@Override
	public void setRequired( boolean required ) {
		getControl( FormControlElement.class ).setRequired( required );
	}

	@Override
	public String getControlName() {
		return getControl( FormControlElement.class ).getControlName();
	}

	@Override
	public void setControlName( String controlName ) {
		getControl( FormControlElement.class ).setControlName( controlName );
	}

	@Override
	public Iterator<ViewElement> iterator() {
		hidden.setAttribute( "name", getControl( FormControlElement.class ).getControlName() );
		getControl( FormControlElement.class ).setControlName( "_" + hidden.getAttribute( "name" ) );
		return super.iterator();
	}
}
