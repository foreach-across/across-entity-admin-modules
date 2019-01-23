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
import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.elements.AbstractNodeViewElement;
import com.foreach.across.modules.web.ui.elements.ConfigurableTextViewElement;
import lombok.NonNull;

import java.util.stream.Stream;

/**
 * Represents an autosuggest textbox field, created only through an {@link AutoSuggestFormElementBuilder}.
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

	AutoSuggestFormElement( TextboxFormElement textbox, HiddenFormElement valueControl ) {
		super( "div" );
		this.textbox = textbox;
		this.valueControl = valueControl;
		this.setAttribute( BootstrapUiAttributes.CONTROL_ADAPTER_TYPE, "autosuggest" );
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
	public void setHtmlId( String htmlId ) {
		super.setHtmlId( htmlId );
	}

	@Override
	public boolean isReadonly() {
		return textbox.isReadonly();
	}

	@Override
	public void setReadonly( boolean readonly ) {
		textbox.setReadonly( readonly );
	}

	@Override
	public boolean isRequired() {
		return textbox.isRequired();
	}

	@Override
	public void setRequired( boolean required ) {
		textbox.setRequired( required );
	}

	@Override
	public String getControlName() {
		return valueControl.getControlName();
	}

	@Override
	public void setControlName( String controlName ) {
		valueControl.setControlName( controlName );
	}

	@Override
	public boolean isDisabled() {
		return textbox.isDisabled();
	}

	@Override
	public void setDisabled( boolean disabled ) {
		textbox.setDisabled( disabled );
	}

	@Override
	public void setPlaceholder( String placeholder ) {
		textbox.setPlaceholder( placeholder );
	}

	@Override
	public String getPlaceholder() {
		return textbox.getPlaceholder();
	}

	@Override
	public void setText( String text ) {
		textbox.setText( text );
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

	public void setValue( Object value ) {
		valueControl.setValue( value );
	}

	public AutoSuggestFormElementConfiguration getConfiguration() {
		return getAttribute( ATTRIBUTE_DATA_AUTOSUGGEST, AutoSuggestFormElementConfiguration.class );
	}

	public void setConfiguration( @NonNull AutoSuggestFormElementConfiguration configuration ) {
		setAttribute( ATTRIBUTE_DATA_AUTOSUGGEST, configuration );
	}

	@Override
	public Stream<ViewElement> elementStream() {
		// filter out the textbox and value control from container finding
		return super.elementStream().filter( e -> e != textbox && e != valueControl );
	}
}
