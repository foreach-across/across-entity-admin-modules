/*
 * Copyright 2019 the original author or authors
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
import com.foreach.across.modules.web.ui.elements.ContainerViewElement;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.function.Consumer;

/**
 * Form input control that represents a formatted numeric input field, for example currency or percentage.
 *
 * @author Arne Vandamme
 */
public class NumericFormElement extends FormControlElementSupport implements FormControlElement.Proxy, ConfigurablePlaceholderText
{
	public static final String ATTRIBUTE_DATA_NUMERIC = "data-bootstrapui-numeric";

	public static final String CSS_NUMERIC = "numeric";

	private Number value;
	private boolean htmlIdSpecified = false;

	private final TextboxFormElement textbox = new TextboxFormElement();
	private final HiddenFormElement hidden = new HiddenFormElement();

	public NumericFormElement() {
		super( "input" );
		setElementType( ContainerViewElement.ELEMENT_TYPE );
		addCssClass( CSS_NUMERIC );
		addChild( textbox );
	}

	@Override
	public TextboxFormElement getControl() {
		return textbox;
	}

	public NumericFormElementConfiguration getConfiguration() {
		return textbox.getAttribute( ATTRIBUTE_DATA_NUMERIC, NumericFormElementConfiguration.class );
	}

	public NumericFormElement setConfiguration( @NonNull NumericFormElementConfiguration configuration ) {
		textbox.setAttribute( ATTRIBUTE_DATA_NUMERIC, configuration );
		setAttribute( BootstrapUiViewElementAttributes.CONTROL_ADAPTER_TYPE, "numeric" );
		return this;
	}

	@Override
	public NumericFormElement setPlaceholder( String placeholder ) {
		textbox.setPlaceholder( placeholder );
		return this;
	}

	@Override
	public String getPlaceholder() {
		return textbox.getPlaceholder();
	}

	@Override
	public boolean isReadonly() {
		return textbox.isReadonly();
	}

	@Override
	public NumericFormElement setReadonly( boolean readonly ) {
		textbox.setReadonly( readonly );
		return this;
	}

	@Override
	public boolean isRequired() {
		return textbox.isRequired();
	}

	@Override
	public NumericFormElement setRequired( boolean required ) {
		textbox.setRequired( required );
		return this;
	}

	@Override
	public String getControlName() {
		return hasConfiguration() ? hidden.getControlName() : textbox.getControlName();
	}

	@Override
	public NumericFormElement setControlName( String controlName ) {
		hidden.setControlName( controlName );
		textbox.setControlName( controlName );
		return this;
	}

	@Override
	public boolean isDisabled() {
		return textbox.isDisabled();
	}

	@Override
	public NumericFormElement setDisabled( boolean disabled ) {
		textbox.setDisabled( disabled );
		return this;
	}

	@Override
	public String getTagName() {
		return textbox.getTagName();
	}

	@Override
	public NumericFormElement addCssClass( String... cssClass ) {
		textbox.addCssClass( cssClass );
		return this;
	}

	@Override
	public boolean hasCssClass( String cssClass ) {
		return textbox.hasCssClass( cssClass );
	}

	@Override
	public NumericFormElement removeCssClass( String... cssClass ) {
		textbox.removeCssClass( cssClass );
		return this;
	}

	@Override
	public NumericFormElement setHtmlId( String id ) {
		htmlIdSpecified = StringUtils.isNotEmpty( id );
		textbox.setHtmlId( id );
		return this;
	}

	@Override
	public String getHtmlId() {
		return textbox.getHtmlId();
	}

	@Override
	public Map<String, Object> getAttributes() {
		return textbox.getAttributes();
	}

	@Override
	public NumericFormElement setAttributes( Map<String, Object> attributes ) {
		textbox.setAttributes( attributes );
		return this;
	}

	@Override
	public NumericFormElement setAttribute( String attributeName, Object attributeValue ) {
		textbox.setAttribute( attributeName, attributeValue );
		return this;
	}

	@Override
	public NumericFormElement addAttributes( Map<String, Object> attributes ) {
		textbox.addAttributes( attributes );
		return this;
	}

	@Override
	public NumericFormElement removeAttribute( String attributeName ) {
		textbox.removeAttribute( attributeName );
		return this;
	}

	@Override
	public Object getAttribute( String attributeName ) {
		return textbox.getAttribute( attributeName );
	}

	@Override
	public <V, U extends V> U getAttribute( String attributeName, Class<V> expectedType ) {
		return textbox.getAttribute( attributeName, expectedType );
	}

	@Override
	public boolean hasAttribute( String attributeName ) {
		return textbox.hasAttribute( attributeName );
	}

	public Number getValue() {
		return value;
	}

	public NumericFormElement setValue( Number value ) {
		this.value = value;

		textbox.setText( value != null ? Objects.toString( value ) : null );
		return this;
	}

	@Override
	public List<ViewElement> getChildren() {
		List<ViewElement> children = new ArrayList<>( super.getChildren() );

		String controlName = getControlName();

		if ( hasConfiguration() && controlName != null ) {
			if ( !htmlIdSpecified ) {
				textbox.setHtmlId( hidden.getControlName() );
			}
			textbox.setControlName( "_" + hidden.getControlName() );
			hidden.setValue( textbox.getText() );

			children.add( hidden );
		}
		else {
			if ( !htmlIdSpecified ) {
				textbox.setHtmlId( getControlName() );
			}
			textbox.setControlName( getControlName() );
		}

		return children;
	}

	private boolean hasConfiguration() {
		return getConfiguration() != null;
	}

	@Override
	public NumericFormElement setName( String name ) {
		super.setName( name );
		return this;
	}

	@Override
	public NumericFormElement setCustomTemplate( String customTemplate ) {
		super.setCustomTemplate( customTemplate );
		return this;
	}

	@Override
	protected NumericFormElement setElementType( String elementType ) {
		super.setElementType( elementType );
		return this;
	}

	@Override
	public NumericFormElement addChild( ViewElement element ) {
		super.addChild( element );
		return this;
	}

	@Override
	public NumericFormElement addChildren( Collection<? extends ViewElement> elements ) {
		super.addChildren( elements );
		return this;
	}

	@Override
	public NumericFormElement addFirstChild( ViewElement element ) {
		super.addFirstChild( element );
		return this;
	}

	@Override
	public NumericFormElement clearChildren() {
		super.clearChildren();
		return this;
	}

	@Override
	public NumericFormElement apply( Consumer<ContainerViewElement> consumer ) {
		super.apply( consumer );
		return this;
	}

	@Override
	public <U extends ViewElement> NumericFormElement applyUnsafe( Consumer<U> consumer ) {
		super.applyUnsafe( consumer );
		return this;
	}

	@Override
	protected NumericFormElement setTagName( String tagName ) {
		super.setTagName( tagName );
		return this;
	}

	@Override
	public NumericFormElement set( WitherSetter... setters ) {
		super.set( setters );
		return this;
	}

	@Override
	public NumericFormElement remove( WitherRemover... functions ) {
		super.remove( functions );
		return this;
	}
}
