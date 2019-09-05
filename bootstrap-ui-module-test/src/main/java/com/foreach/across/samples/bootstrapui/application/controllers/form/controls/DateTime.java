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

package com.foreach.across.samples.bootstrapui.application.controllers.form.controls;

import com.foreach.across.modules.bootstrapui.elements.DateTimeFormElement;
import com.foreach.across.modules.bootstrapui.elements.DateTimeFormElementConfiguration;
import com.foreach.across.modules.bootstrapui.elements.builder.DateTimeFormElementBuilder;
import com.foreach.across.modules.web.menu.PathBasedMenuBuilder;
import com.foreach.across.samples.bootstrapui.application.controllers.ExampleController;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.Locale;

import static com.foreach.across.modules.bootstrapui.elements.BootstrapUiBuilders.datetime;

/**
 * @author Steven Gentens
 * @since 2.0.0
 */
@RequiredArgsConstructor
@Controller
@RequestMapping("/form-controls/datetime")
class DateTime extends ExampleController
{
	@Override
	protected void menuItems( PathBasedMenuBuilder menu ) {
		menu.item( "/form-controls/datetime", "Date/time" );
	}

	@RequestMapping(method = RequestMethod.GET)
	String renderDatepickers() {
		return render(
				panel( "Null value LocalDateTime", datetime().controlName( "dp1" ).value( (LocalDateTime) null ).build() ),
				panel( "Null value LocalDate", datetime().controlName( "dp2" ).value( (LocalDate) null ).build() ),
				panel( "Null value LocalTime", datetime().controlName( "dp3" ).value( (LocalTime) null ).build() ),
				panel( "Null value Date", datetime().controlName( "dp4" ).value( (Date) null ).build() ),
				panel( "Simple datepicker (Date)", simpleDatepicker_Date().setControlName( "dp5" ) ),
				panel( "Simple datepicker (LocalDate)", simpleDatepicker_LocalDate().setControlName( "dp6" ) ),
				panel( "Simple datepicker (LocalTime)", simpleDatepicker_LocalTime().setControlName( "dp7" ) ),
				panel( "Simple datepicker (LocalDateTime)", simpleDatepicker_LocalDateTime().setControlName( "dp8" ) ),
				panel( "Time format", datepickerWithTimeFormat().setControlName( "dp9" ) ),
				panel( "Date format", datepickerWithDateFormat().setControlName( "dp10" ) ),
				panel( "Datepicker with control buttons", datepickerWithAllButtons().setControlName( "dp11" ) ),
				panel( "Localized fr_FR (Date)", datepickerLocalizedfrFR_Date().setControlName( "dp12" ) ),
				panel( "Localized fr_FR (LocalDateTime)", datepickerLocalizedfrFR_LocalDateTime().setControlName( "dp13" ) ),
				panel( "Localized ja_JP (Date)", datepickerLocalizedjaJP_Date().setControlName( "dp14" ) ),
				panel( "Localized ja_JP (LocalDateTime)", datepickerLocalizedjaJP_LocalDateTime().setControlName( "dp15" ) )
		);
	}

	private DateTimeFormElement simpleDatepicker_Date() {
		return datetime()
				.value( new Date() )
				.build();
	}

	private DateTimeFormElement simpleDatepicker_LocalDate() {
		return datetime()
				.value( LocalDate.now() )
				.build();
	}

	private DateTimeFormElement simpleDatepicker_LocalTime() {
		return datetime()
				.value( LocalTime.now() )
				.build();
	}

	private DateTimeFormElement simpleDatepicker_LocalDateTime() {
		return datetime()
				.value( LocalDateTime.now() )
				.build();
	}

	private DateTimeFormElement datepickerWithTimeFormat() {
		return datetime()
				.value( LocalDateTime.now() )
				.format( DateTimeFormElementConfiguration.Format.TIME )
				.build();
	}

	private DateTimeFormElement datepickerWithDateFormat() {
		return datetime()
				.value( LocalDateTime.now() )
				.format( DateTimeFormElementConfiguration.Format.DATE )
				.build();
	}

	private DateTimeFormElement datepickerWithAllButtons() {
		DateTimeFormElementBuilder builder = datetime();
		DateTimeFormElementConfiguration config = builder.getConfiguration();
		config.setShowClearButton( true );
		config.setShowCloseButton( true );
		config.setShowTodayButton( true );
		config.setFormat( DateTimeFormElementConfiguration.Format.DATETIME_FULL );
		config.setLocalizePatterns( false );
		return builder
				.configuration( config )
				.build();
	}

	private DateTimeFormElement datepickerLocalizedfrFR_Date() {
		DateTimeFormElementBuilder builder = datetime();
		DateTimeFormElementConfiguration config = builder.getConfiguration();
		config.setLocale( Locale.forLanguageTag( "fr-FR" ) );
		config.setFormat( DateTimeFormElementConfiguration.Format.DATETIME_FULL );
		config.setLocalizePatterns( false );
		return builder
				.configuration( config )
				.value( LocalDateTime.now() )
				.build();
	}

	private DateTimeFormElement datepickerLocalizedfrFR_LocalDateTime() {
		DateTimeFormElementBuilder builder = datetime();
		DateTimeFormElementConfiguration config = builder.getConfiguration();
		config.setLocale( Locale.forLanguageTag( "fr-FR" ) );
		config.setFormat( DateTimeFormElementConfiguration.Format.DATETIME_FULL );
		config.setLocalizePatterns( false );
		return builder
				.configuration( config )
				.value( LocalDateTime.now() )
				.build();
	}

	private DateTimeFormElement datepickerLocalizedjaJP_Date() {
		DateTimeFormElementBuilder builder = datetime();
		DateTimeFormElementConfiguration config = builder.getConfiguration();
		config.setLocale( Locale.forLanguageTag( "ja-JP" ) );
		config.setFormat( DateTimeFormElementConfiguration.Format.DATETIME_FULL );
		config.setLocalizePatterns( false );
		return builder
				.configuration( config )
				.value( LocalDateTime.now() )
				.build();
	}

	private DateTimeFormElement datepickerLocalizedjaJP_LocalDateTime() {
		DateTimeFormElementBuilder builder = datetime();
		DateTimeFormElementConfiguration config = builder.getConfiguration();
		config.setLocale( Locale.forLanguageTag( "ja-JP" ) );
		config.setFormat( DateTimeFormElementConfiguration.Format.DATETIME_FULL );
		config.setLocalizePatterns( false );
		return builder
				.value( LocalDateTime.now() )
				.configuration( config )
				.build();
	}
}
