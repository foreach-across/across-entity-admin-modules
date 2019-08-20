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

import com.foreach.across.modules.bootstrapui.elements.DateTimeFormElement;
import com.foreach.across.modules.bootstrapui.elements.DateTimeFormElementConfiguration;
import com.foreach.across.modules.bootstrapui.resource.BootstrapUiFormElementsWebResources;
import com.foreach.across.modules.web.resource.WebResourceRegistry;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import org.springframework.context.i18n.LocaleContextHolder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

import static com.foreach.across.modules.bootstrapui.styles.BootstrapStyles.css;
import static com.foreach.across.modules.web.ui.elements.HtmlViewElement.Functions.children;
import static com.foreach.across.modules.web.ui.elements.HtmlViewElements.div;
import static com.foreach.across.modules.web.ui.elements.HtmlViewElements.i;

/**
 * @author Arne Vandamme
 */
public class DateTimeFormElementBuilder extends InputGroupFormElementBuilderSupport<DateTimeFormElement, DateTimeFormElementBuilder>
{
	private Date value;
	private LocalDateTime valueAsLocalDateTime;
	private DateTimeFormElementConfiguration configuration;

	public DateTimeFormElementConfiguration getConfiguration() {
		return configuration;
	}

	/**
	 * Sets the date based on the given {@link Date} object.
	 * If a {@link LocalDateTime} is set, it will override the value.
	 * Prefer the use of {@link #value(LocalDateTime)} instead.
	 *
	 * @param value date to set
	 * @return current builder
	 */
	public DateTimeFormElementBuilder value( Date value ) {
		if ( value != null ) {
			this.value = new Date( value.getTime() );
		}
		return this;
	}

	/**
	 * Sets the date based on the given {@link LocalDate} object.
	 *
	 * @param value date to set
	 * @return current builder
	 */
	public DateTimeFormElementBuilder value( LocalDate value ) {
		value( DateTimeFormElementConfiguration.localDateToLocalDateTime( value ) );
		return this;
	}

	/**
	 * Sets the date based on the given {@link LocalTime} object.
	 *
	 * @param value date to set
	 * @return current builder
	 */
	public DateTimeFormElementBuilder value( LocalTime value ) {
		value( DateTimeFormElementConfiguration.localTimeToLocalDateTime( value ) );
		return this;
	}

	/**
	 * Sets the date based on the given {@link LocalDateTime} object.
	 *
	 * @param value date to set
	 * @return current builder
	 */
	public DateTimeFormElementBuilder value( LocalDateTime value ) {
		valueAsLocalDateTime = value;
		return this;
	}

	public DateTimeFormElementBuilder datetime() {
		return format( DateTimeFormElementConfiguration.Format.DATETIME );
	}

	public DateTimeFormElementBuilder date() {
		return format( DateTimeFormElementConfiguration.Format.DATE );
	}

	public DateTimeFormElementBuilder time() {
		return format( DateTimeFormElementConfiguration.Format.TIME );
	}

	/**
	 * Sets a default configuration for the format.
	 */
	public DateTimeFormElementBuilder format( DateTimeFormElementConfiguration.Format format ) {
		if ( configuration == null ) {
			configuration = new DateTimeFormElementConfiguration( format );
		}
		else {
			configuration.setFormat( format );
		}

		// todo use static icon set
		if ( format == DateTimeFormElementConfiguration.Format.TIME ) {
			append( div( css.inputGroup.text, children( i( css.fa.solid( "clock" ) ) ) ) );
		}
		else {
			append( div( css.inputGroup.text, children( i( css.fa.solid( "calendar" ) ) ) ) );
		}
		return this;
	}

	public DateTimeFormElementBuilder configuration( DateTimeFormElementConfiguration configuration ) {
		this.configuration = configuration;
		return this;
	}

	@Override
	protected DateTimeFormElement createElement( ViewElementBuilderContext builderContext ) {
		DateTimeFormElement datetime = apply( new DateTimeFormElement(), builderContext );

		if ( configuration != null ) {
			datetime.setConfiguration( configuration.localize( LocaleContextHolder.getLocale() ) );
		}
		if ( value != null ) {
			datetime.setValue( value );
		}
		if ( valueAsLocalDateTime != null ) {
			datetime.setLocalDateTime( valueAsLocalDateTime );
		}

		return datetime;
	}

	@Override
	protected void registerWebResources( WebResourceRegistry webResourceRegistry ) {
		webResourceRegistry.addPackage( BootstrapUiFormElementsWebResources.NAME );
	}
}
