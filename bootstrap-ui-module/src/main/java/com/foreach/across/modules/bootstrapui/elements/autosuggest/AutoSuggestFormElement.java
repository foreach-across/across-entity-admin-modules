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

package com.foreach.across.modules.bootstrapui.elements.autosuggest;

import com.foreach.across.modules.bootstrapui.elements.*;
import com.foreach.across.modules.bootstrapui.ui.factories.BootstrapViewElements;
import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.elements.AbstractNodeViewElement;
import com.foreach.across.modules.web.ui.elements.ConfigurableTextViewElement;
import com.foreach.across.modules.web.ui.elements.ContainerViewElement;
import lombok.NonNull;

import java.util.Collection;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * Represents an autosuggest textbox field, can be created through {@link BootstrapViewElements#autoSuggest(TextboxFormElement, HiddenFormElement)}  or using a {@link AutoSuggestFormElementBuilder}.
 *
 * @author Arne Vandamme
 * @since 2.0.0
 */
public final class AutoSuggestFormElement extends AbstractNodeViewElement implements FormControlElement, ConfigurableTextViewElement, ConfigurablePlaceholderText
{
	public static final String ELEMENT_TYPE = BootstrapUiElements.AUTOSUGGEST;
	public static final String ATTRIBUTE_DATA_AUTOSUGGEST = "data-bootstrapui-autosuggest";

	private final TextboxFormElement textbox;
	private final HiddenFormElement valueControl;

	public AutoSuggestFormElement( TextboxFormElement textbox, HiddenFormElement valueControl ) {
		super( "div" );
		this.textbox = textbox;
		this.valueControl = valueControl;
		this.setAttribute( BootstrapUiViewElementAttributes.CONTROL_ADAPTER_TYPE, "autosuggest" );
		textbox.removeAttribute( BootstrapUiViewElementAttributes.CONTROL_ADAPTER_TYPE );
	}

	@Override
	public String getHtmlId() {
		String htmlId = super.getHtmlId();

		if ( htmlId != null ) {
			return htmlId;
		}

		htmlId = valueControl.getHtmlId();

		return htmlId != null ? "autosuggest-" + htmlId : null;
	}

	@Override
	public AutoSuggestFormElement setHtmlId( String htmlId ) {
		super.setHtmlId( htmlId );
		return this;
	}

	@Override
	public boolean isReadonly() {
		return textbox.isReadonly();
	}

	@Override
	public AutoSuggestFormElement setReadonly( boolean readonly ) {
		textbox.setReadonly( readonly );
		return this;
	}

	@Override
	public boolean isRequired() {
		return textbox.isRequired();
	}

	@Override
	public AutoSuggestFormElement setRequired( boolean required ) {
		textbox.setRequired( required );
		return this;
	}

	@Override
	public String getControlName() {
		return valueControl.getControlName();
	}

	@Override
	public AutoSuggestFormElement setControlName( String controlName ) {
		valueControl.setControlName( controlName );
		return this;
	}

	@Override
	public boolean isDisabled() {
		return textbox.isDisabled();
	}

	@Override
	public AutoSuggestFormElement setDisabled( boolean disabled ) {
		textbox.setDisabled( disabled );
		return this;
	}

	@Override
	public AutoSuggestFormElement setPlaceholder( String placeholder ) {
		textbox.setPlaceholder( placeholder );
		return this;
	}

	@Override
	public String getPlaceholder() {
		return textbox.getPlaceholder();
	}

	@Override
	public AutoSuggestFormElement setText( String text ) {
		textbox.setText( text );
		return this;
	}

	@Override
	public String getText() {
		return textbox.getText();
	}

	public Object getValue() {
		return valueControl.getValue();
	}

	public <V> V getValue( Class<V> expectedType ) {
		return valueControl.getValue( expectedType );
	}

	public AutoSuggestFormElement setValue( Object value ) {
		valueControl.setValue( value );
		return this;
	}

	public AutoSuggestFormElementConfiguration getConfiguration() {
		return getAttribute( ATTRIBUTE_DATA_AUTOSUGGEST, AutoSuggestFormElementConfiguration.class );
	}

	public AutoSuggestFormElement setConfiguration( @NonNull AutoSuggestFormElementConfiguration configuration ) {
		return setAttribute( ATTRIBUTE_DATA_AUTOSUGGEST, configuration );
	}

	@Override
	public Stream<ViewElement> elementStream() {
		// filter out the textbox and value control from container finding
		return super.elementStream().filter( e -> e != textbox && e != valueControl );
	}

	@Override
	public AutoSuggestFormElement addCssClass( String... cssClass ) {
		super.addCssClass( cssClass );
		return this;
	}

	@Override
	public AutoSuggestFormElement removeCssClass( String... cssClass ) {
		super.removeCssClass( cssClass );
		return this;
	}

	@Override
	public AutoSuggestFormElement setAttributes( Map<String, Object> attributes ) {
		super.setAttributes( attributes );
		return this;
	}

	@Override
	public AutoSuggestFormElement setAttribute( String attributeName, Object attributeValue ) {
		super.setAttribute( attributeName, attributeValue );
		return this;
	}

	@Override
	public AutoSuggestFormElement addAttributes( Map<String, Object> attributes ) {
		super.addAttributes( attributes );
		return this;
	}

	@Override
	public AutoSuggestFormElement removeAttribute( String attributeName ) {
		super.removeAttribute( attributeName );
		return this;
	}

	@Override
	public AutoSuggestFormElement setName( String name ) {
		super.setName( name );
		return this;
	}

	@Override
	public AutoSuggestFormElement setCustomTemplate( String customTemplate ) {
		super.setCustomTemplate( customTemplate );
		return this;
	}

	@Override
	protected AutoSuggestFormElement setElementType( String elementType ) {
		super.setElementType( elementType );
		return this;
	}

	@Override
	public AutoSuggestFormElement addChild( ViewElement element ) {
		super.addChild( element );
		return this;
	}

	@Override
	public AutoSuggestFormElement addChildren( Collection<? extends ViewElement> elements ) {
		super.addChildren( elements );
		return this;
	}

	@Override
	public AutoSuggestFormElement addFirstChild( ViewElement element ) {
		super.addFirstChild( element );
		return this;
	}

	@Override
	public AutoSuggestFormElement clearChildren() {
		super.clearChildren();
		return this;
	}

	@Override
	public AutoSuggestFormElement apply( Consumer<ContainerViewElement> consumer ) {
		super.apply( consumer );
		return this;
	}

	@Override
	public <U extends ViewElement> AutoSuggestFormElement applyUnsafe( Consumer<U> consumer ) {
		super.applyUnsafe( consumer );
		return this;
	}

	@Override
	protected AutoSuggestFormElement setTagName( String tagName ) {
		super.setTagName( tagName );
		return this;
	}

	@Override
	public AutoSuggestFormElement set( WitherSetter... setters ) {
		super.set( setters );
		return this;
	}

	@Override
	public AutoSuggestFormElement remove( WitherRemover... functions ) {
		super.remove( functions );
		return this;
	}
}
