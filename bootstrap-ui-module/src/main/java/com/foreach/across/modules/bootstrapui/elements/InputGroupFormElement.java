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
import com.foreach.across.modules.web.ui.elements.ContainerViewElement;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

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
@Accessors(chain = true)
@Getter
@Setter
public class InputGroupFormElement extends AbstractNodeViewElement implements FormControlElement.Proxy, ConfigurablePlaceholderText
{
	private ViewElement addonBefore, addonAfter, control;

	public InputGroupFormElement() {
		super( "div" );
		addCssClass( "input-group" );

		setControl( new TextboxFormElement() );
	}

	public <V extends ViewElement> V getAddonBefore( Class<V> addonType ) {
		return returnIfType( addonBefore, addonType );
	}

	public <V extends ViewElement> V getAddonAfter( Class<V> addonType ) {
		return returnIfType( addonAfter, addonType );
	}

	@Override
	public InputGroupFormElement setPlaceholder( String placeholder ) {
		getControl( TextboxFormElement.class ).setPlaceholder( placeholder );
		return this;
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
	public InputGroupFormElement setDisabled( boolean disabled ) {
		getControl( FormControlElement.class ).setDisabled( disabled );
		return this;
	}

	@Override
	public boolean isReadonly() {
		return getControl( FormControlElement.class ).isReadonly();
	}

	@Override
	public InputGroupFormElement setReadonly( boolean readonly ) {
		getControl( FormControlElement.class ).setReadonly( readonly );
		return this;
	}

	@Override
	public boolean isRequired() {
		return getControl( FormControlElement.class ).isRequired();
	}

	@Override
	public InputGroupFormElement setRequired( boolean required ) {
		getControl( FormControlElement.class ).setRequired( required );
		return this;
	}

	@Override
	public String getControlName() {
		return getControl( FormControlElement.class ).getControlName();
	}

	@Override
	public InputGroupFormElement setControlName( String controlName ) {
		getControl( FormControlElement.class ).setControlName( controlName );
		return this;
	}

	@Override
	public List<ViewElement> getChildren() {
		List<ViewElement> children = super.getChildren();

		if ( control == null && addonBefore == null && addonAfter == null ) {
			return children;
		}

		List<ViewElement> extended = new ArrayList<>();
		if ( addonBefore != null ) {
			extended.add( createAddon( addonBefore ) );
		}
		if ( control != null ) {
			extended.add( control );
		}
		extended.addAll( children );
		if ( addonAfter != null ) {
			extended.add( createAddon( addonAfter ) );
		}

		return extended;
	}

	@Override
	public boolean hasChildren() {
		return super.hasChildren() || addonBefore != null || addonAfter != null || control != null;
	}

	private ViewElement createAddon( ViewElement child ) {
		Addon addon = new Addon( child instanceof ButtonViewElement );
		addon.addChild( child );
		return addon;
	}

	@Override
	public InputGroupFormElement addCssClass( String... cssClass ) {
		super.addCssClass( cssClass );
		return this;
	}

	@Override
	public InputGroupFormElement removeCssClass( String... cssClass ) {
		super.removeCssClass( cssClass );
		return this;
	}

	@Override
	public InputGroupFormElement setAttributes( Map<String, Object> attributes ) {
		super.setAttributes( attributes );
		return this;
	}

	@Override
	public InputGroupFormElement setAttribute( String attributeName, Object attributeValue ) {
		super.setAttribute( attributeName, attributeValue );
		return this;
	}

	@Override
	public InputGroupFormElement addAttributes( Map<String, Object> attributes ) {
		super.addAttributes( attributes );
		return this;
	}

	@Override
	public InputGroupFormElement removeAttribute( String attributeName ) {
		super.removeAttribute( attributeName );
		return this;
	}

	@Override
	public InputGroupFormElement setName( String name ) {
		super.setName( name );
		return this;
	}

	@Override
	public InputGroupFormElement setCustomTemplate( String customTemplate ) {
		super.setCustomTemplate( customTemplate );
		return this;
	}

	@Override
	protected InputGroupFormElement setElementType( String elementType ) {
		super.setElementType( elementType );
		return this;
	}

	@Override
	public InputGroupFormElement addChild( ViewElement element ) {
		super.addChild( element );
		return this;
	}

	@Override
	public InputGroupFormElement addChildren( Collection<? extends ViewElement> elements ) {
		super.addChildren( elements );
		return this;
	}

	@Override
	public InputGroupFormElement addFirstChild( ViewElement element ) {
		super.addFirstChild( element );
		return this;
	}

	@Override
	public InputGroupFormElement clearChildren() {
		super.clearChildren();
		return this;
	}

	@Override
	public InputGroupFormElement apply( Consumer<ContainerViewElement> consumer ) {
		super.apply( consumer );
		return this;
	}

	@Override
	public <U extends ViewElement> InputGroupFormElement applyUnsafe( Consumer<U> consumer ) {
		super.applyUnsafe( consumer );
		return this;
	}

	@Override
	protected InputGroupFormElement setTagName( String tagName ) {
		super.setTagName( tagName );
		return this;
	}

	@Override
	public InputGroupFormElement setHtmlId( String htmlId ) {
		super.setHtmlId( htmlId );
		return this;
	}

	public static class Addon extends AbstractNodeViewElement
	{
		public Addon( boolean forButton ) {
			super( "span" );
			addCssClass( forButton ? "input-group-btn" : "input-group-addon" );
		}
	}
}
