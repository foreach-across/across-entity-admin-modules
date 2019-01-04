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

package com.foreach.across.modules.entity.converters;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.Duration;

import static com.foreach.across.modules.entity.converters.StringToDurationConverter.DAYS_TO_HOURS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.Assert.assertEquals;

/**
 * @author Steven Gentens
 * @since 3.1.1
 */
@RunWith(SpringJUnit4ClassRunner.class)
@Slf4j
public class TestStringToDurationConverter
{
	@Test
	public void hours() {
		Duration result = StringToDurationConverter.convertToDuration( "5h" );
		assertEquals( 5, result.toHours() );
		assertEquals( 0, result.minusHours( result.toHours() ).toMinutes() );

		result = StringToDurationConverter.convertToDuration( "-5h" );
		assertEquals( -5, result.toHours() );
		assertEquals( 0, result.minusHours( result.toHours() ).toMinutes() );
	}

	@Test
	public void minutes() {
		Duration result = StringToDurationConverter.convertToDuration( "20m" );
		assertEquals( 0, result.toHours() );
		assertEquals( 20, result.minusHours( result.toHours() ).toMinutes() );

		result = StringToDurationConverter.convertToDuration( "-40m" );
		assertEquals( 0, result.toHours() );
		assertEquals( -40, result.minusHours( result.toHours() ).toMinutes() );
	}

	@Test
	public void decimalHours() {
		Duration result = StringToDurationConverter.convertToDuration( "1.5h" );
		assertEquals( 1, result.toHours() );
		assertEquals( 30, result.minusHours( result.toHours() ).toMinutes() );

		result = StringToDurationConverter.convertToDuration( "1,5h" );
		assertEquals( 1, result.toHours() );
		assertEquals( 30, result.minusHours( result.toHours() ).toMinutes() );

		result = StringToDurationConverter.convertToDuration( "-1.5h" );
		assertEquals( -1, result.toHours() );
		assertEquals( -30, result.minusHours( result.toHours() ).toMinutes() );
	}

	@Test
	public void minutesExceeding60() {
		Duration result = StringToDurationConverter.convertToDuration( "150m" );
		assertEquals( 2, result.toHours() );
		assertEquals( 30, result.minusHours( result.toHours() ).toMinutes() );

		result = StringToDurationConverter.convertToDuration( "259M" );
		assertEquals( 4, result.toHours() );
		assertEquals( 19, result.minusHours( result.toHours() ).toMinutes() );

		result = StringToDurationConverter.convertToDuration( "-259m" );
		assertEquals( -4, result.toHours() );
		assertEquals( -19, result.minusHours( result.toHours() ).toMinutes() );
	}

	@Test
	public void hoursAndMinutes() {
		Duration result = StringToDurationConverter.convertToDuration( "4h5m" );
		assertEquals( 4, result.toHours() );
		assertEquals( 5, result.minusHours( result.toHours() ).toMinutes() );

		result = StringToDurationConverter.convertToDuration( "4h5 m" );
		assertEquals( 4, result.toHours() );
		assertEquals( 5, result.minusHours( result.toHours() ).toMinutes() );

		result = StringToDurationConverter.convertToDuration( "4 h5m" );
		assertEquals( 4, result.toHours() );
		assertEquals( 5, result.minusHours( result.toHours() ).toMinutes() );

		result = StringToDurationConverter.convertToDuration( "-2h60m" );
		assertEquals( -1, result.toHours() );
		assertEquals( 0, result.minusHours( result.toHours() ).toMinutes() );
	}

	@Test
	public void hoursAndMinutesWithSpaces() {
		Duration result = StringToDurationConverter.convertToDuration( "3h 45m" );
		assertEquals( 3, result.toHours() );
		assertEquals( 45, result.minusHours( result.toHours() ).toMinutes() );

		result = StringToDurationConverter.convertToDuration( "3 h 45m" );
		assertEquals( 3, result.toHours() );
		assertEquals( 45, result.minusHours( result.toHours() ).toMinutes() );

		result = StringToDurationConverter.convertToDuration( "3h 45 m" );
		assertEquals( 3, result.toHours() );
		assertEquals( 45, result.minusHours( result.toHours() ).toMinutes() );
	}

	@Test
	public void hoursAndMinutesFullWords() {
		Duration result = StringToDurationConverter.convertToDuration( "3 hours 45 minutes" );
		assertEquals( 3, result.toHours() );
		assertEquals( 45, result.minusHours( result.toHours() ).toMinutes() );

		result = StringToDurationConverter.convertToDuration( "3hours 45minutes" );
		assertEquals( 3, result.toHours() );
		assertEquals( 45, result.minusHours( result.toHours() ).toMinutes() );

		result = StringToDurationConverter.convertToDuration( "3hours45minutes" );
		assertEquals( 3, result.toHours() );
		assertEquals( 45, result.minusHours( result.toHours() ).toMinutes() );

		result = StringToDurationConverter.convertToDuration( "3 hours 45minutes" );
		assertEquals( 3, result.toHours() );
		assertEquals( 45, result.minusHours( result.toHours() ).toMinutes() );

		result = StringToDurationConverter.convertToDuration( "3hours 45 minutes" );
		assertEquals( 3, result.toHours() );
		assertEquals( 45, result.minusHours( result.toHours() ).toMinutes() );

		result = StringToDurationConverter.convertToDuration( "3 hours45minutes" );
		assertEquals( 3, result.toHours() );
		assertEquals( 45, result.minusHours( result.toHours() ).toMinutes() );

		result = StringToDurationConverter.convertToDuration( "3hours45 minutes" );
		assertEquals( 3, result.toHours() );
		assertEquals( 45, result.minusHours( result.toHours() ).toMinutes() );
	}

	@Test
	public void hoursAndMinutesFullWordsTranslated() {
		Duration result = StringToDurationConverter.convertToDuration( "3 uur 45 minuten" );
		assertEquals( 3, result.toHours() );
		assertEquals( 45, result.minusHours( result.toHours() ).toMinutes() );

		result = StringToDurationConverter.convertToDuration( "3uur 45minuten" );
		assertEquals( 3, result.toHours() );
		assertEquals( 45, result.minusHours( result.toHours() ).toMinutes() );

		result = StringToDurationConverter.convertToDuration( "3uur45minuten" );
		assertEquals( 3, result.toHours() );
		assertEquals( 45, result.minusHours( result.toHours() ).toMinutes() );

		result = StringToDurationConverter.convertToDuration( "3 uur 45minuten" );
		assertEquals( 3, result.toHours() );
		assertEquals( 45, result.minusHours( result.toHours() ).toMinutes() );

		result = StringToDurationConverter.convertToDuration( "3uur 45 minuten" );
		assertEquals( 3, result.toHours() );
		assertEquals( 45, result.minusHours( result.toHours() ).toMinutes() );

		result = StringToDurationConverter.convertToDuration( "3 uur45minuten" );
		assertEquals( 3, result.toHours() );
		assertEquals( 45, result.minusHours( result.toHours() ).toMinutes() );

		result = StringToDurationConverter.convertToDuration( "3uur45 minuten" );
		assertEquals( 3, result.toHours() );
		assertEquals( 45, result.minusHours( result.toHours() ).toMinutes() );
	}

	@Test
	public void dayConvertsToHours() {
		Duration result = StringToDurationConverter.convertToDuration( "1d" );
		assertEquals( DAYS_TO_HOURS, result.toHours() );
		assertEquals( 0, result.minusHours( result.toHours() ).toMinutes() );

		result = StringToDurationConverter.convertToDuration( "1.5d" );
		assertEquals( DAYS_TO_HOURS+DAYS_TO_HOURS/2, result.toHours() );
		assertEquals( 0, result.minusHours( result.toHours() ).toMinutes() );

		result = StringToDurationConverter.convertToDuration( "1,5d" );
		assertEquals( DAYS_TO_HOURS+DAYS_TO_HOURS/2, result.toHours() );
		assertEquals( 0, result.minusHours( result.toHours() ).toMinutes() );
	}

	@Test
	public void dayFullConvertsToHours() {
		Duration result = StringToDurationConverter.convertToDuration( "1day" );
		assertEquals( DAYS_TO_HOURS, result.toHours() );
		assertEquals( 0, result.minusHours( result.toHours() ).toMinutes() );

		result = StringToDurationConverter.convertToDuration( "1 day" );
		assertEquals( DAYS_TO_HOURS, result.toHours() );
		assertEquals( 0, result.minusHours( result.toHours() ).toMinutes() );
	}

	@Test
	public void dayFullTranslatedConvertsToHours() {
		Duration result = StringToDurationConverter.convertToDuration( "1 dag" );
		assertEquals( DAYS_TO_HOURS, result.toHours() );
		assertEquals( 0, result.minusHours( result.toHours() ).toMinutes() );

		result = StringToDurationConverter.convertToDuration( "2 dagen" );
		assertEquals( DAYS_TO_HOURS*2, result.toHours() );
		assertEquals( 0, result.minusHours( result.toHours() ).toMinutes() );
	}

	@Test
	public void lessThan60MinutesConvertsToTimeMinutesOnly() {
		assertThat( StringToDurationConverter.convertToTime( 25 ) ).isEqualTo( "25m" );
	}

	@Test
	public void hoursWithoutLeftoverMinutesConvertsToTimeHoursOnly() {
		assertThat( StringToDurationConverter.convertToTime( 4 * 60 ) ).isEqualTo( "4h" );
	}

	@Test
	public void minutesCanBeConvertedToTimestamp() {
		assertThat( StringToDurationConverter.convertToTime( 7 * 60 + 29 ) ).isEqualTo( "7h 29m" );
	}

	@Test
	public void convertToDuration() {
		assertThat( StringToDurationConverter.convertToDuration( "7h25m" ).toMinutes() ).isEqualTo( 7 * 60 + 25 );
		assertThat( StringToDurationConverter.convertToDuration( "20m" ).toMinutes() ).isEqualTo( 20 );
		assertThat( StringToDurationConverter.convertToDuration( "2d3h47m" ).toMinutes() )
				.isEqualTo( 2 * DAYS_TO_HOURS * 60 + 3 * 60 + 47 );
		assertThat( StringToDurationConverter.convertToDuration( "1d -1h 30m" ).toMinutes() )
				.isEqualTo( DAYS_TO_HOURS * 60 + -1 * 60 + 30 );
		assertThat( StringToDurationConverter.convertToDuration( "1d -1h -30m" ).toMinutes() )
				.isEqualTo( DAYS_TO_HOURS * 60 + -60 - 30 );
		assertThat( StringToDurationConverter.convertToDuration( "-1d 9h -30m" ).toMinutes() )
				.isEqualTo( DAYS_TO_HOURS * -60 + 9 * 60 - 30 );
		assertThat( StringToDurationConverter.convertToDuration( "1d 9h -30m" ).toMinutes() )
				.isEqualTo( DAYS_TO_HOURS * 60 + 9 * 60 - 30 );
		assertThat( StringToDurationConverter.convertToDuration( "-1d 9h 30m" ).toMinutes() )
				.isEqualTo( DAYS_TO_HOURS * -60 + 9 * 60 + 30 );
		assertThat( StringToDurationConverter.convertToDuration( "1234" ).toMinutes() )
				.isEqualTo( 1234 );
	}

	@Test
	public void decimalMinutesAreNotAllowed() {
		assertThatExceptionOfType( IllegalArgumentException.class ).isThrownBy(
				() -> StringToDurationConverter.convertToDuration( "25.2m" ) );
		assertThatExceptionOfType( IllegalArgumentException.class ).isThrownBy(
				() -> StringToDurationConverter.convertToDuration( "1d17.3m" ) );
		assertThatExceptionOfType( IllegalArgumentException.class ).isThrownBy(
				() -> StringToDurationConverter.convertToDuration( "1h 14.5m" ) );
		assertThatExceptionOfType( IllegalArgumentException.class ).isThrownBy(
				() -> StringToDurationConverter.convertToDuration( "3 d 4h 47.8m" ) );
	}
}
