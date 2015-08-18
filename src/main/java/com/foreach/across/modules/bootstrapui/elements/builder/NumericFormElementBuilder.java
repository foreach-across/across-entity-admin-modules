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
package com.foreach.across.modules.bootstrapui.elements.builder;

import com.foreach.across.modules.bootstrapui.elements.NumericFormElement;
import com.foreach.across.modules.bootstrapui.elements.NumericFormElementConfiguration;
import com.foreach.across.modules.bootstrapui.resource.BootstrapUiFormElementsWebResources;
import com.foreach.across.modules.web.resource.WebResourceRegistry;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Currency;
import java.util.Locale;

/**
 * @author Arne Vandamme
 */
public class NumericFormElementBuilder extends FormControlElementBuilderSupport<NumericFormElement, NumericFormElementBuilder>
{
	private Number value;
	private NumericFormElementConfiguration configuration;

	public NumericFormElementConfiguration getConfiguration() {
		return configuration;
	}

	public NumericFormElementBuilder value( Number value ) {
		this.value = value;
		return this;
	}

	public NumericFormElementBuilder percent() {
		NumericFormElementConfiguration configuration = new NumericFormElementConfiguration();
		configuration.setFormat( NumericFormElementConfiguration.Format.PERCENT );
		return this;
	}

	public NumericFormElementBuilder currency() {
		NumericFormElementConfiguration configuration = new NumericFormElementConfiguration();
		configuration.setFormat( NumericFormElementConfiguration.Format.CURRENCY );
		return this;
	}

	public NumericFormElementBuilder decimal( int fractionDigits ) {
		NumericFormElementConfiguration configuration = new NumericFormElementConfiguration();
		configuration.setDecimalPositions( fractionDigits );
		return configuration( configuration );
	}

	/**
	 * Create an unformatted textbox for input.
	 */
	public NumericFormElementBuilder simple() {
		return configuration( null );
	}

	/**
	 * Create a formatted version for an integer.
	 */
	public NumericFormElementBuilder integer() {
		NumericFormElementConfiguration configuration = new NumericFormElementConfiguration();
		configuration.setDecimalPositions( 0 );
		return configuration( configuration );
	}

	public NumericFormElementBuilder currency( Currency currency ) {
		return configuration( new NumericFormElementConfiguration( currency ) );
	}

	public NumericFormElementBuilder currency( Locale locale ) {
		return configuration( new NumericFormElementConfiguration( locale ) );
	}

	public NumericFormElementBuilder currency( Currency currency, Locale locale ) {
		NumericFormElementConfiguration configuration = new NumericFormElementConfiguration();
		configuration.setCurrency( currency, locale );
		return configuration( configuration );
	}

	public NumericFormElementBuilder configuration( NumericFormElementConfiguration configuration ) {
		this.configuration = configuration;
		return this;
	}

	@Override
	protected NumericFormElement createElement( ViewElementBuilderContext builderContext ) {
		NumericFormElement numeric = apply( new NumericFormElement(), builderContext );

		if ( configuration != null ) {
			numeric.setConfiguration( configuration.localize( LocaleContextHolder.getLocale() ) );
		}
		if ( value != null ) {
			numeric.setValue( value );
		}

		registerWebResources( builderContext );

		return numeric;
	}

	protected void registerWebResources( ViewElementBuilderContext builderContext ) {
		WebResourceRegistry webResourceRegistry = builderContext.getAttribute( WebResourceRegistry.class );

		if ( webResourceRegistry != null ) {
			webResourceRegistry.addPackage( BootstrapUiFormElementsWebResources.NAME );
		}
	}
}
