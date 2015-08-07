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
import com.foreach.across.modules.bootstrapui.elements.GlyphIcon;
import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.ViewElementBuilder;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;

import java.util.Date;

/**
 * @author Arne Vandamme
 */
public class DateTimeFormElementBuilder extends FormControlElementBuilderSupport<DateTimeFormElement, DateTimeFormElementBuilder>
{
	private ElementOrBuilder addonBefore, addonAfter, control;
	private Date value;
	private DateTimeFormElementConfiguration configuration;

	public DateTimeFormElementConfiguration getConfiguration() {
		return configuration;
	}

	public DateTimeFormElementBuilder addonBefore( ViewElement element ) {
		addonBefore = element != null ? ElementOrBuilder.wrap( element ) : null;
		return this;
	}

	public DateTimeFormElementBuilder addonBefore( ViewElementBuilder element ) {
		addonBefore = element != null ? ElementOrBuilder.wrap( element ) : null;
		return this;
	}

	public DateTimeFormElementBuilder addonAfter( ViewElement element ) {
		addonAfter = element != null ? ElementOrBuilder.wrap( element ) : null;
		return this;
	}

	public DateTimeFormElementBuilder addonAfter( ViewElementBuilder element ) {
		addonAfter = element != null ? ElementOrBuilder.wrap( element ) : null;
		return this;
	}

	public DateTimeFormElementBuilder control( ViewElement element ) {
		control = element != null ? ElementOrBuilder.wrap( element ) : null;
		return this;
	}

	public DateTimeFormElementBuilder control( ViewElementBuilder element ) {
		control = element != null ? ElementOrBuilder.wrap( element ) : null;
		return this;
	}

	public DateTimeFormElementBuilder value( Date value ) {
		this.value = value;
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

		if ( format == DateTimeFormElementConfiguration.Format.TIME ) {
			addonAfter( new GlyphIcon( GlyphIcon.TIME ) );
		}
		else {
			addonAfter( new GlyphIcon( GlyphIcon.CALENDAR ) );
		}
		return this;
	}

	public DateTimeFormElementBuilder configuration( DateTimeFormElementConfiguration configuration ) {
		this.configuration = configuration;
		return this;
	}

	@Override
	protected DateTimeFormElement createElement( ViewElementBuilderContext builderContext ) {
		DateTimeFormElement datetime = new DateTimeFormElement();

		// Set controls first
		if ( control != null ) {
			datetime.setControl( control.get( builderContext ) );
		}
		if ( addonBefore != null ) {
			datetime.setAddonBefore( addonBefore.get( builderContext ) );
		}
		if ( addonAfter != null ) {
			datetime.setAddonAfter( addonAfter.get( builderContext ) );
		}

		// Then apply regular form control properties so they are dispatched to the backing control
		datetime = apply( datetime, builderContext );

		if ( configuration != null ) {
			datetime.setConfiguration( new DateTimeFormElementConfiguration( configuration ) );
		}
		if ( value != null ) {
			datetime.setValue( value );
		}

		return datetime;
	}
}
