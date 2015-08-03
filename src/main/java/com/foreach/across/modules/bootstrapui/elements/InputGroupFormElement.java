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
 *
 * @author Arne Vandamme
 */
public class InputGroupFormElement extends AbstractNodeViewElement
{
	private ViewElement addonBefore, addonAfter, control;

	public InputGroupFormElement() {
		super( "div" );
		addCssClass( "input-group" );
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

	public ViewElement getControl() {
		return control;
	}

	public void setControl( ViewElement control ) {
		this.control = control;
	}

	public <V extends ViewElement> V getControl( Class<V> controlType ) {
		return returnIfType( control, controlType );
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
