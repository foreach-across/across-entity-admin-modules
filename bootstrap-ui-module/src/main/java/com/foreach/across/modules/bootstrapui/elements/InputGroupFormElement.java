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

import com.foreach.across.core.support.SingletonIterator;
import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.elements.AbstractNodeViewElement;
import org.springframework.util.CompositeIterator;

import java.util.Iterator;

/**
 * <p>Represents a bootstrap input group, wrapping a control and allowing left or right addon.
 * The main control should be added as a child to the input group, the addons can be set through properties
 * and the corresponding addon class ({@code input-group-addon} of {@code input-group-btn} will be set.</p>
 * <p>A default {@link TextboxFormElement} control is set for the input group as input groups are usually
 * expected to be used with textboxes.  The {@link InputGroupFormElement} itself is also a {@link FormControlElement}
 * that proxies the control.  If the control is not of type {@link FormControlElement} the specific
 * {@link FormControlElement} method calls will fail.</p>
 *
 * @author Arne Vandamme
 */
public class InputGroupFormElement extends AbstractNodeViewElement implements FormControlElement.Proxy, ConfigurablePlaceholderText
{
	private ViewElement addonBefore, addonAfter, control;

	public InputGroupFormElement() {
		super( "div" );
		addCssClass( "input-group" );

		setControl( new TextboxFormElement() );
	}

	public ViewElement getAddonBefore() {
		return addonBefore;
	}

	public void setAddonBefore( ViewElement addonBefore ) {
		this.addonBefore = addonBefore;
	}

	public <V extends ViewElement> V getAddonBefore( Class<V> addonType ) {
		return returnIfType( addonBefore, addonType );
	}

	public ViewElement getAddonAfter() {
		return addonAfter;
	}

	public void setAddonAfter( ViewElement addonAfter ) {
		this.addonAfter = addonAfter;
	}

	public <V extends ViewElement> V getAddonAfter( Class<V> addonType ) {
		return returnIfType( addonAfter, addonType );
	}

	@Override
	public ViewElement getControl() {
		return control;
	}

	public void setControl( ViewElement control ) {
		this.control = control;
	}

	@Override
	public void setPlaceholder( String placeholder ) {
		getControl( TextboxFormElement.class ).setPlaceholder( placeholder );
	}

	@Override
	public String getPlaceholder() {
		return getControl( TextboxFormElement.class ).getPlaceholder();
	}

	public <V extends ViewElement> V getControl( Class<V> controlType ) {
		return returnIfType( control, controlType );
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
		if ( control == null && addonBefore == null && addonAfter == null ) {
			return super.iterator();
		}

		CompositeIterator<ViewElement> elements = new CompositeIterator<>();
		if ( addonBefore != null ) {
			elements.add( new SingletonIterator<>( createAddon( addonBefore ) ) );
		}
		if ( control != null ) {
			elements.add( new SingletonIterator<>( control ) );
		}
		elements.add( super.iterator() );
		if ( addonAfter != null ) {
			elements.add( new SingletonIterator<>( createAddon( addonAfter ) ) );
		}

		return elements;
	}

	@Override
	public boolean isEmpty() {
		return super.isEmpty() && addonBefore == null && addonAfter == null && control == null;
	}

	@Override
	public int size() {
		return super.size()
				+ ( addonBefore != null ? 1 : 0 )
				+ ( control != null ? 1 : 0 )
				+ ( addonAfter != null ? 1 : 0 );
	}

	private ViewElement createAddon( ViewElement child ) {
		Addon addon = new Addon( child instanceof ButtonViewElement );
		addon.add( child );
		return addon;
	}

	public static class Addon extends AbstractNodeViewElement
	{
		public Addon( boolean forButton ) {
			super( "span" );
			addCssClass( forButton ? "input-group-btn" : "input-group-addon" );
		}
	}
}
