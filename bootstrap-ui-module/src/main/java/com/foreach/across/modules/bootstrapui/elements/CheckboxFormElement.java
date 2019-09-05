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
import com.foreach.across.modules.web.ui.elements.ConfigurableTextViewElement;
import com.foreach.across.modules.web.ui.elements.ContainerViewElement;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Collection;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Represents a HTML checkbox element.
 *
 * @author Arne Vandamme
 */
@Accessors(chain = true)
@Getter
@Setter
public class CheckboxFormElement extends FormControlElementSupport implements ConfigurableTextViewElement
{
	public static final String ELEMENT_TYPE = BootstrapUiElements.CHECKBOX;

	private boolean checked;
	private boolean wrapped = true;

	/**
	 * Should the control be rendered as a bootstrap custom control (default) or as a browser default form control.
	 */
	private boolean renderAsCustomControl = true;

	private Object value;

	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	private String label;

	public CheckboxFormElement() {
		super( ELEMENT_TYPE );
		setAttribute( BootstrapUiViewElementAttributes.CONTROL_ADAPTER_TYPE, "checkbox" );
	}

	@Override
	public String getText() {
		return label;
	}

	@Override
	public CheckboxFormElement setText( String label ) {
		this.label = label;
		return this;
	}

	@Override
	public CheckboxFormElement setDisabled( boolean disabled ) {
		super.setDisabled( disabled );
		return this;
	}

	@Override
	public CheckboxFormElement setReadonly( boolean readonly ) {
		super.setReadonly( readonly );
		return this;
	}

	@Override
	public CheckboxFormElement setRequired( boolean required ) {
		super.setRequired( required );
		return this;
	}

	@Override
	public CheckboxFormElement setName( String name ) {
		super.setName( name );
		return this;
	}

	@Override
	public CheckboxFormElement setControlName( String controlName ) {
		super.setControlName( controlName );
		return this;
	}

	@Override
	public CheckboxFormElement setHtmlId( String htmlId ) {
		super.setHtmlId( htmlId );
		return this;
	}

	@Override
	public CheckboxFormElement addCssClass( String... cssClass ) {
		super.addCssClass( cssClass );
		return this;
	}

	@Override
	public CheckboxFormElement removeCssClass( String... cssClass ) {
		super.removeCssClass( cssClass );
		return this;
	}

	@Override
	public CheckboxFormElement setAttributes( Map<String, Object> attributes ) {
		super.setAttributes( attributes );
		return this;
	}

	@Override
	public CheckboxFormElement setAttribute( String attributeName, Object attributeValue ) {
		super.setAttribute( attributeName, attributeValue );
		return this;
	}

	@Override
	public CheckboxFormElement addAttributes( Map<String, Object> attributes ) {
		super.addAttributes( attributes );
		return this;
	}

	@Override
	public CheckboxFormElement removeAttribute( String attributeName ) {
		super.removeAttribute( attributeName );
		return this;
	}

	@Override
	public CheckboxFormElement setCustomTemplate( String customTemplate ) {
		super.setCustomTemplate( customTemplate );
		return this;
	}

	@Override
	protected CheckboxFormElement setElementType( String elementType ) {
		super.setElementType( elementType );
		return this;
	}

	@Override
	public CheckboxFormElement addChild( ViewElement element ) {
		super.addChild( element );
		return this;
	}

	@Override
	public CheckboxFormElement addChildren( Collection<? extends ViewElement> elements ) {
		super.addChildren( elements );
		return this;
	}

	@Override
	public CheckboxFormElement addFirstChild( ViewElement element ) {
		super.addFirstChild( element );
		return this;
	}

	@Override
	public CheckboxFormElement clearChildren() {
		super.clearChildren();
		return this;
	}

	@Override
	public CheckboxFormElement apply( Consumer<ContainerViewElement> consumer ) {
		super.apply( consumer );
		return this;
	}

	@Override
	public <U extends ViewElement> CheckboxFormElement applyUnsafe( Consumer<U> consumer ) {
		super.applyUnsafe( consumer );
		return this;
	}

	@Override
	protected CheckboxFormElement setTagName( String tagName ) {
		super.setTagName( tagName );
		return this;
	}
}
