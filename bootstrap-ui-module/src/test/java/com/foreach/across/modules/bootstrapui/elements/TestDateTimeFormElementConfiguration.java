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

import org.apache.commons.lang3.time.DateUtils;
import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.foreach.across.modules.bootstrapui.elements.DateTimeFormElementConfiguration.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Arne Vandamme
 */
public class TestDateTimeFormElementConfiguration
{
	private static final Date PRINT_DATE;

	static {
		try {
			PRINT_DATE = DateUtils.parseDate( "2015-08-07 10:31:22", "yyyy-MM-dd HH:mm:ss" );
		}
		catch ( ParseException pe ) {
			throw new RuntimeException( pe );
		}
	}

	@Test
	public void newConfiguration() {
		DateTimeFormElementConfiguration configuration = new DateTimeFormElementConfiguration();
		assertEquals( Format.DATETIME, configuration.getFormat() );
		assertEquals( FMT_PATTERN_DATETIME, configuration.get( "format" ) );
		assertEquals( "en-GB", configuration.get( "locale" ) );
		assertEquals( FMT_EXPORT_MOMENT_DATETIME, configuration.get( "exportFormat" ) );
		assertArrayEquals(
				new String[] { FMT_EXPORT_MOMENT_DATETIME, FMT_PATTERN_DATE, FMT_EXTRA_PATTERN_DATE },
				(String[]) configuration.get( "extraFormats" )
		);

		DateTimeFormElementConfiguration localized = configuration.localize( Locale.forLanguageTag( "nl-BE" ) );
		assertEquals( "nl-BE", localized.get( "locale" ) );

		assertEquals( "07-Aug-2015 10:31", configuration.createDateFormat().format( PRINT_DATE ) );
		assertEquals( "7-aug-2015 10:31", localized.createDateFormat().format( PRINT_DATE ) );

		configuration.setLocalizePatterns( false );
		assertEquals( "en-GB", configuration.localize( Locale.forLanguageTag( "nl-BE" ) ).get( "locale" ) );
	}

	@Test
	public void datetimeFull() {
		DateTimeFormElementConfiguration configuration = new DateTimeFormElementConfiguration(
				Format.DATETIME_FULL
		);
		assertEquals( Format.DATETIME_FULL, configuration.getFormat() );
		assertEquals( FMT_PATTERN_DATETIME_FULL, configuration.get( "format" ) );
		assertEquals( "en-GB", configuration.get( "locale" ) );
		assertEquals( FMT_EXPORT_MOMENT_DATETIME, configuration.get( "exportFormat" ) );
		assertArrayEquals(
				new String[] { FMT_EXPORT_MOMENT_DATETIME, FMT_PATTERN_DATE, FMT_PATTERN_DATETIME,
				               FMT_EXTRA_PATTERN_DATE },
				(String[]) configuration.get( "extraFormats" )
		);

		DateTimeFormElementConfiguration localized = configuration.localize( Locale.forLanguageTag( "nl-BE" ) );
		assertEquals( "07 August 2015 10:31:22", configuration.createDateFormat().format( PRINT_DATE ) );
		assertEquals( "7 augustus 2015 10:31:22", localized.createDateFormat().format( PRINT_DATE ) );
	}

	@Test
	public void date() {
		DateTimeFormElementConfiguration configuration = new DateTimeFormElementConfiguration(
				Format.DATE );
		assertEquals( Format.DATE, configuration.getFormat() );
		assertEquals( FMT_PATTERN_DATE, configuration.get( "format" ) );
		assertEquals( "en-GB", configuration.get( "locale" ) );
		assertEquals( FMT_EXPORT_MOMENT_DATE, configuration.get( "exportFormat" ) );
		assertArrayEquals(
				new String[] { FMT_EXPORT_MOMENT_DATETIME, FMT_EXTRA_PATTERN_DATE },
				(String[]) configuration.get( "extraFormats" )
		);

		DateTimeFormElementConfiguration localized = configuration.localize( Locale.forLanguageTag( "nl-BE" ) );
		assertEquals( "07-Aug-2015", configuration.createDateFormat().format( PRINT_DATE ) );
		assertEquals( "7-aug-2015", localized.createDateFormat().format( PRINT_DATE ) );
	}

	@Test
	public void dateFull() {
		DateTimeFormElementConfiguration configuration = new DateTimeFormElementConfiguration(
				Format.DATE_FULL );
		assertEquals( Format.DATE_FULL, configuration.getFormat() );
		assertEquals( FMT_PATTERN_DATE_FULL, configuration.get( "format" ) );
		assertEquals( "en-GB", configuration.get( "locale" ) );
		assertEquals( FMT_EXPORT_MOMENT_DATE, configuration.get( "exportFormat" ) );
		assertArrayEquals(
				new String[] { FMT_EXPORT_MOMENT_DATETIME, FMT_PATTERN_DATE, FMT_EXTRA_PATTERN_DATE },
				(String[]) configuration.get( "extraFormats" )
		);

		DateTimeFormElementConfiguration localized = configuration.localize( Locale.forLanguageTag( "nl-BE" ) );
		assertEquals( "Friday, 7 August 2015", configuration.createDateFormat().format( PRINT_DATE ) );
		assertEquals( "vrijdag 7 augustus 2015", localized.createDateFormat().format( PRINT_DATE ) );
	}

	@Test
	public void time() {
		DateTimeFormElementConfiguration configuration = new DateTimeFormElementConfiguration(
				Format.TIME );
		assertEquals( Format.TIME, configuration.getFormat() );
		assertEquals( FMT_PATTERN_TIME, configuration.get( "format" ) );
		assertEquals( "en-GB", configuration.get( "locale" ) );
		assertEquals( FMT_EXPORT_MOMENT_TIME, configuration.get( "exportFormat" ) );
		assertArrayEquals(
				new String[] { FMT_EXPORT_MOMENT_DATETIME },
				(String[]) configuration.get( "extraFormats" )
		);

		DateTimeFormElementConfiguration localized = configuration.localize( Locale.forLanguageTag( "nl-BE" ) );
		assertEquals( "10:31", configuration.createDateFormat().format( PRINT_DATE ) );
		assertEquals( "10:31", localized.createDateFormat().format( PRINT_DATE ) );
	}

	@Test
	public void nullValuesDoNotBreak() {
		DateTimeFormElementConfiguration configuration = new DateTimeFormElementConfiguration();

		configuration.setDefaultDate( (LocalDateTime) null );
		assertNull( configuration.get( "defaultDate" ) );

		configuration.setDefaultDate( (LocalDate) null );
		assertNull( configuration.get( "defaultDate" ) );

		configuration.setDefaultDate( (LocalTime) null );
		assertNull( configuration.get( "defaultDate" ) );

		configuration.setDefaultDate( (Date) null );
		assertNull( configuration.get( "defaultDate" ) );
	}

	@Test
	public void customAttributes() throws ParseException {
		Date start = DateUtils.parseDate( "2015-08-07 10:31", "yyyy-MM-dd HH:mm" );
		Date end = DateUtils.parseDate( "2015-08-08 10:31", "yyyy-MM-dd HH:mm" );

		DateTimeFormElementConfiguration configuration = new DateTimeFormElementConfiguration();
		configuration.setMinDate( start );
		configuration.setMaxDate( end );
		configuration.setShowClearButton( true );

		assertEquals( Format.DATETIME, configuration.getFormat() );
		assertEquals( FMT_PATTERN_DATETIME, configuration.get( "format" ) );
		assertEquals( "en-GB", configuration.get( "locale" ) );
		assertEquals( FMT_EXPORT_MOMENT_DATETIME, configuration.get( "exportFormat" ) );
		assertArrayEquals(
				new String[] { FMT_EXPORT_MOMENT_DATETIME, FMT_PATTERN_DATE, FMT_EXTRA_PATTERN_DATE },
				(String[]) configuration.get( "extraFormats" )
		);

		assertEquals( "2015-08-07 10:31", configuration.get( "minDate" ) );
		assertEquals( "2015-08-08 10:31", configuration.get( "maxDate" ) );
		Map<String, Boolean> buttons = (Map<String, Boolean>) configuration.get( "buttons" );
		assertEquals( true, buttons.get( "showClear" ) );
	}

	@Test
	public void dateToLocalDateConversion() throws Exception {
		Date date = DateUtils.parseDate( "2015-08-07 10:31", "yyyy-MM-dd HH:mm" );
		LocalDateTime localDateTime = LocalDateTime.ofInstant( date.toInstant(), ZoneId.systemDefault() );

		assertEquals(
				"2015-08-07 10:31",
				DateTimeFormatter.ofPattern( "yyyy-MM-dd HH:mm" ).format( localDateTime )
		);

		assertEquals( date, Date.from( localDateTime.atZone( ZoneId.systemDefault() ).toInstant() ) );
	}

	@Test
	public void sqlDateAndDateAreSuccessfullyConverted() throws ParseException {
		Date date = DateUtils.parseDate( "2015-08-07 00:00", "yyyy-MM-dd HH:mm" );
		DateTimeFormElementConfiguration dateConfiguration = new DateTimeFormElementConfiguration();
		dateConfiguration.setDateAttribute( "test", date );

		java.sql.Date sqlDate = new java.sql.Date( date.getTime() );
		DateTimeFormElementConfiguration sqlDateConfiguration = new DateTimeFormElementConfiguration();
		sqlDateConfiguration.setDateAttribute( "test", sqlDate );

		assertEquals( dateConfiguration.get( "test" ), sqlDateConfiguration.get( "test" ) );
	}

	@Test
	public void customAttributesForJava8Times() throws ParseException {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern( "yyyy-MM-dd HH:mm" );
		LocalDateTime start = LocalDateTime.parse( "2015-08-07 10:31", formatter );
		LocalDateTime end = LocalDateTime.parse( "2015-08-08 10:31", formatter );

		DateTimeFormElementConfiguration configuration = new DateTimeFormElementConfiguration();
		configuration.setMinDate( start );
		configuration.setMaxDate( end );
		configuration.setShowClearButton( true );

		assertEquals( "2015-08-07 10:31", configuration.get( "minDate" ) );
		assertEquals( "2015-08-08 10:31", configuration.get( "maxDate" ) );

		configuration = new DateTimeFormElementConfiguration();
		configuration.setMinDate( start.toLocalDate() );
		configuration.setMaxDate( end.toLocalDate() );

		assertEquals( "2015-08-07 00:00", configuration.get( "minDate" ) );
		assertEquals( "2015-08-08 00:00", configuration.get( "maxDate" ) );

		configuration = new DateTimeFormElementConfiguration();
		configuration.setMinDate( start.toLocalTime() );
		configuration.setMaxDate( end.toLocalTime() );
		configuration.setShowClearButton( true );

		String localDateAsString = LocalDate.now().format( DateTimeFormatter.ofPattern( "yyyy-MM-dd" ) );

		assertEquals( localDateAsString + " 10:31", configuration.get( "minDate" ) );
		assertEquals( localDateAsString + " 10:31", configuration.get( "maxDate" ) );

		DateTimeFormElementConfiguration configurationWithDate = new DateTimeFormElementConfiguration();
		Date date = DateUtils.parseDate( "2015-08-07 10:31", "yyyy-MM-dd HH:mm" );
		configuration.setDefaultDate( date );

		DateTimeFormElementConfiguration configurationWithJava8Date = new DateTimeFormElementConfiguration();
		configuration.setDefaultDate( LocalDateTime.ofInstant( date.toInstant(), configuration.getZoneId() ) );

		assertEquals( configurationWithDate.get( "defaultDate" ), configurationWithJava8Date.get( "defaultDate" ) );

	}

	@Test
	public void dateTimeFormatterIsEqualToDateFormat() {
		LocalDateTime localDateTime = LocalDateTime.ofInstant( PRINT_DATE.toInstant(), ZoneId.systemDefault() );

		DateTimeFormElementConfiguration configuration = new DateTimeFormElementConfiguration( Format.DATE );
		assertEquals( configuration.createDateFormat().format( PRINT_DATE ), configuration.createDateTimeFormatter().format( localDateTime ) );

		configuration = new DateTimeFormElementConfiguration( Format.DATE_FULL );
		assertEquals( configuration.createDateFormat().format( PRINT_DATE ), configuration.createDateTimeFormatter().format( localDateTime ) );

		configuration = new DateTimeFormElementConfiguration( Format.TIME );
		assertEquals( configuration.createDateFormat().format( PRINT_DATE ), configuration.createDateTimeFormatter().format( localDateTime ) );

		configuration = new DateTimeFormElementConfiguration( Format.DATETIME );
		assertEquals( configuration.createDateFormat().format( PRINT_DATE ), configuration.createDateTimeFormatter().format( localDateTime ) );

		configuration = new DateTimeFormElementConfiguration( Format.DATETIME_FULL );
		assertEquals( configuration.createDateFormat().format( PRINT_DATE ), configuration.createDateTimeFormatter().format( localDateTime ) );
	}

	@Test
	public void localizedDateTimeFormatterIsEqualToDateFormat() {
		Date d1 = Date.from( Instant.EPOCH );
		Date d2 = Date.from( Instant.now() );
		List<String> localesThatAreNotEqual = Arrays.asList( "hi-IN", "fi-FI", "ja-JP-u-ca-japanese-x-lvariant-JP", "th-TH", "fi",
		                                                     "th-TH-u-nu-thai-x-lvariant-TH" );
		List<Date> randomDates = new ArrayList<>();
		randomDates.add( PRINT_DATE );
		for ( int i = 0; i < 2000; i += 1 ) {
			randomDates.add( getRandomDateBetween( d1, d2 ) );
		}
		for ( Date date : randomDates ) {
			for ( Locale locale : Locale.getAvailableLocales() ) {
				if ( !localesThatAreNotEqual.contains( locale.toLanguageTag() ) ) {
					assertLocalizedFormattersReturnEqualValues( date, locale );
				}
			}
		}
	}

	private void assertLocalizedFormattersReturnEqualValues( Date date, Locale locale ) {
		LocalDateTime localDateTime = LocalDateTime.ofInstant( date.toInstant(), ZoneId.systemDefault() );

		DateTimeFormElementConfiguration configuration = new DateTimeFormElementConfiguration( Format.DATE );
		configuration.setLocale( locale );
		assertEquals( configuration.createDateFormat().format( date ), configuration.createDateTimeFormatter().format( localDateTime ) );

		configuration = new DateTimeFormElementConfiguration( Format.DATE_FULL );
		configuration.setLocale( locale );
		assertEquals( configuration.createDateFormat().format( date ), configuration.createDateTimeFormatter().format( localDateTime ) );

		configuration = new DateTimeFormElementConfiguration( Format.TIME );
		configuration.setLocale( locale );
		assertEquals( configuration.createDateFormat().format( date ), configuration.createDateTimeFormatter().format( localDateTime ) );

		configuration = new DateTimeFormElementConfiguration( Format.DATETIME );
		configuration.setLocale( locale );
		assertEquals( configuration.createDateFormat().format( date ), configuration.createDateTimeFormatter().format( localDateTime ) );

		configuration = new DateTimeFormElementConfiguration( Format.DATETIME_FULL );
		configuration.setLocale( locale );
		assertEquals( configuration.createDateFormat().format( date ), configuration.createDateTimeFormatter().format( localDateTime ) );
	}

	private Date getRandomDateBetween( Date start, Date end ) {
		long diff = end.getTime() - start.getTime() + 1;
		return new Date( start.getTime() + (long) ( Math.random() * diff ) );
	}
}
