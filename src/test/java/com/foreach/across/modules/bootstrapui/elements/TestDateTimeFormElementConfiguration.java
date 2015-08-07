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

import org.apache.commons.lang3.time.DateUtils;
import org.junit.Test;

import java.text.ParseException;
import java.util.Date;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * @author Arne Vandamme
 */
public class TestDateTimeFormElementConfiguration
{
	@Test
	public void newConfiguration() {
		DateTimeFormElementConfiguration configuration = new DateTimeFormElementConfiguration();
		assertEquals( DateTimeFormElementConfiguration.Format.DATETIME, configuration.getFormat() );
		assertEquals( DateTimeFormElementConfiguration.FMT_PATTERN_DATETIME, configuration.get( "format" ) );
		assertEquals( "en-GB", configuration.get( "locale" ) );
		assertEquals( "input[type=text]", configuration.get( "datepickerInput" ) );
		assertArrayEquals( new String[] { "YYYY-MM-DD HH:mm:ss" }, (String[]) configuration.get( "extraFormats" ) );
	}

	@Test
	public void date() {
		DateTimeFormElementConfiguration configuration = new DateTimeFormElementConfiguration(
				DateTimeFormElementConfiguration.Format.DATE );
		assertEquals( DateTimeFormElementConfiguration.Format.DATE, configuration.getFormat() );
		assertEquals( DateTimeFormElementConfiguration.FMT_PATTERN_DATE, configuration.get( "format" ) );
		assertEquals( "en-GB", configuration.get( "locale" ) );
		assertEquals( "input[type=text]", configuration.get( "datepickerInput" ) );
		assertArrayEquals( new String[] { "YYYY-MM-DD HH:mm:ss" }, (String[]) configuration.get( "extraFormats" ) );
	}

	@Test
	public void time() {
		DateTimeFormElementConfiguration configuration = new DateTimeFormElementConfiguration(
				DateTimeFormElementConfiguration.Format.TIME );
		assertEquals( DateTimeFormElementConfiguration.Format.TIME, configuration.getFormat() );
		assertEquals( DateTimeFormElementConfiguration.FMT_PATTERN_TIME, configuration.get( "format" ) );
		assertEquals( "en-GB", configuration.get( "locale" ) );
		assertEquals( "input[type=text]", configuration.get( "datepickerInput" ) );
		assertArrayEquals( new String[] { "YYYY-MM-DD HH:mm:ss" }, (String[]) configuration.get( "extraFormats" ) );
	}

	@Test
	public void customAttributes() throws ParseException {
		Date start = DateUtils.parseDate( "2015-08-07 10:31", "yyyy-MM-dd HH:mm" );
		Date end = DateUtils.parseDate( "2015-08-08 10:31", "yyyy-MM-dd HH:mm" );

		DateTimeFormElementConfiguration configuration = new DateTimeFormElementConfiguration();
		configuration.setMinDate( start );
		configuration.setMaxDate( end );
		configuration.setShowClearButton( true );

		assertEquals( DateTimeFormElementConfiguration.Format.DATETIME, configuration.getFormat() );
		assertEquals( DateTimeFormElementConfiguration.FMT_PATTERN_DATETIME, configuration.get( "format" ) );
		assertEquals( "en-GB", configuration.get( "locale" ) );
		assertEquals( "input[type=text]", configuration.get( "datepickerInput" ) );
		assertArrayEquals( new String[] { "YYYY-MM-DD HH:mm:ss" }, (String[]) configuration.get( "extraFormats" ) );
		assertEquals( "2015-08-07 10:31:00", configuration.get( "minDate" ) );
		assertEquals( "2015-08-08 10:31:00", configuration.get( "maxDate" ) );
		assertEquals( true, configuration.get( "showClear" ) );
	}
}
