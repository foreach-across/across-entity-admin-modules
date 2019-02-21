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

import com.foreach.across.modules.entity.query.support.DurationWithPeriod;
import com.foreach.across.modules.entity.util.StringToDurationWithPeriodConverter;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.Duration;
import java.time.Period;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.Assert.assertEquals;

/**
 * @author Steven Gentens
 * @since 3.1.1
 */
@RunWith(SpringJUnit4ClassRunner.class)
@Slf4j
public class TestStringToDurationWithPeriodConverter
{
	@Test
	public void hours() {
		DurationWithPeriod result = StringToDurationWithPeriodConverter.of( "5h" );
		assertEquals( 5, result.getDuration().toHours() );
		assertEquals( 0, result.getDuration().minusHours( result.getDuration().toHours() ).toMinutes() );

		result = StringToDurationWithPeriodConverter.of( "5 hour" );
		assertEquals( 5, result.getDuration().toHours() );
		assertEquals( 0, result.getDuration().minusHours( result.getDuration().toHours() ).toMinutes() );


		result = StringToDurationWithPeriodConverter.of( "-5h" );
		Duration duration = result.getDuration();
		assertEquals( -5, duration.toHours() );
		assertEquals( 0, duration.minusHours( duration.toHours() ).toMinutes() );
	}

	@Test
	public void minutes() {
		DurationWithPeriod result = StringToDurationWithPeriodConverter.of( "20m" );
		Duration duration = result.getDuration();

		assertEquals( 0, duration.toHours() );
		assertEquals( 20, duration.minusHours( duration.toHours() ).toMinutes() );

		result = StringToDurationWithPeriodConverter.of( "20 minutes" );
		duration = result.getDuration();

		assertEquals( 0, duration.toHours() );
		assertEquals( 20, duration.minusHours( duration.toHours() ).toMinutes() );

		result = StringToDurationWithPeriodConverter.of( "-40m" );
		duration = result.getDuration();

		assertEquals( 0, duration.toHours() );
		assertEquals( -40, duration.minusHours( duration.toHours() ).toMinutes() );
	}

	@Test
	public void seconds() {
		DurationWithPeriod result = StringToDurationWithPeriodConverter.of( "120s" );
		Duration duration = result.getDuration();

		assertEquals( 2, duration.toMinutes() );
		assertEquals( 120, duration.getSeconds() );

		result = StringToDurationWithPeriodConverter.of( "-40 seconds" );
		duration = result.getDuration();

		assertEquals( 0, duration.toMinutes() );
		assertEquals( -40, duration.getSeconds() );

		result = StringToDurationWithPeriodConverter.of( "-40s" );
		duration = result.getDuration();

		assertEquals( 0, duration.toMinutes() );
		assertEquals( -40, duration.getSeconds() );
	}

	@Test
	public void decimalHours() {
		assertThatExceptionOfType( IllegalArgumentException.class ).isThrownBy(
				() -> StringToDurationWithPeriodConverter.of( "1.5h\"" ) );
	}

	@Test
	public void minutesExceeding60() {
		DurationWithPeriod result = StringToDurationWithPeriodConverter.of( "150m" );
		Duration duration = result.getDuration();

		assertEquals( 2, duration.toHours() );
		assertEquals( 30, duration.minusHours( duration.toHours() ).toMinutes() );

		result = StringToDurationWithPeriodConverter.of( "259m" );
		duration = result.getDuration();
		assertEquals( 4, duration.toHours() );
		assertEquals( 19, duration.minusHours( duration.toHours() ).toMinutes() );

		result = StringToDurationWithPeriodConverter.of( "-259m" );
		duration = result.getDuration();

		assertEquals( -4, duration.toHours() );
		assertEquals( -19, duration.minusHours( duration.toHours() ).toMinutes() );
	}

	@Test
	public void hoursAndMinutes() {
		DurationWithPeriod result = StringToDurationWithPeriodConverter.of( "4h5m" );
		Duration duration = result.getDuration();

		assertEquals( 4, duration.toHours() );
		assertEquals( 5, duration.minusHours( duration.toHours() ).toMinutes() );

		result = StringToDurationWithPeriodConverter.of( "4h5 m" );
		duration = result.getDuration();
		assertEquals( 4, duration.toHours() );
		assertEquals( 5, duration.minusHours( duration.toHours() ).toMinutes() );

		result = StringToDurationWithPeriodConverter.of( "4 h5m" );
		duration = result.getDuration();
		assertEquals( 4, duration.toHours() );
		assertEquals( 5, duration.minusHours( duration.toHours() ).toMinutes() );

		result = StringToDurationWithPeriodConverter.of( "-2h60m" );
		duration = result.getDuration();
		assertEquals( -1, duration.toHours() );
		assertEquals( 0, duration.minusHours( duration.toHours() ).toMinutes() );
	}

	@Test
	public void hoursAndMinutesWithSpaces() {
		DurationWithPeriod result = StringToDurationWithPeriodConverter.of( "3h 45m" );
		Duration duration = result.getDuration();

		assertEquals( 3, duration.toHours() );
		assertEquals( 45, duration.minusHours( duration.toHours() ).toMinutes() );

		result = StringToDurationWithPeriodConverter.of( "3 h 45m" );
		duration = result.getDuration();
		assertEquals( 3, duration.toHours() );
		assertEquals( 45, duration.minusHours( duration.toHours() ).toMinutes() );

		result = StringToDurationWithPeriodConverter.of( "3h 45 m" );
		duration = result.getDuration();
		assertEquals( 3, duration.toHours() );
		assertEquals( 45, duration.minusHours( duration.toHours() ).toMinutes() );
	}

	@Test
	public void hoursAndMinutesFullWords() {
		DurationWithPeriod result = StringToDurationWithPeriodConverter.of( "3 hours 45 minutes" );
		Duration duration = result.getDuration();

		assertEquals( 3, duration.toHours() );
		assertEquals( 45, duration.minusHours( duration.toHours() ).toMinutes() );

		result = StringToDurationWithPeriodConverter.of( "3hours 45minutes" );
		duration = result.getDuration();
		assertEquals( 3, duration.toHours() );
		assertEquals( 45, duration.minusHours( duration.toHours() ).toMinutes() );

		result = StringToDurationWithPeriodConverter.of( "3hours45minutes" );
		duration = result.getDuration();
		assertEquals( 3, duration.toHours() );
		assertEquals( 45, duration.minusHours( duration.toHours() ).toMinutes() );

		result = StringToDurationWithPeriodConverter.of( "3 hours 45minutes" );
		duration = result.getDuration();
		assertEquals( 3, duration.toHours() );
		assertEquals( 45, duration.minusHours( duration.toHours() ).toMinutes() );

		result = StringToDurationWithPeriodConverter.of( "3hours 45 minutes" );
		duration = result.getDuration();
		assertEquals( 3, duration.toHours() );
		assertEquals( 45, duration.minusHours( duration.toHours() ).toMinutes() );

		result = StringToDurationWithPeriodConverter.of( "3 hours45minutes" );
		duration = result.getDuration();
		assertEquals( 3, duration.toHours() );
		assertEquals( 45, duration.minusHours( duration.toHours() ).toMinutes() );

		result = StringToDurationWithPeriodConverter.of( "3hours45 minutes" );
		duration = result.getDuration();
		assertEquals( 3, duration.toHours() );
		assertEquals( 45, duration.minusHours( duration.toHours() ).toMinutes() );
	}

	@Test
	public void dayConvertsToHours() {
		DurationWithPeriod result = StringToDurationWithPeriodConverter.of( "1d" );
		Period period = result.getPeriod();
		assertEquals( 1, period.getDays() );

		result = StringToDurationWithPeriodConverter.of( "3d" );
		period = result.getPeriod();
		assertEquals( 3, period.getDays() );
	}

	@Test
	public void dayFullConvertsToHours() {
		DurationWithPeriod result = StringToDurationWithPeriodConverter.of( "1day" );
		Period period = result.getPeriod();
		assertEquals( 1, period.getDays() );

		result = StringToDurationWithPeriodConverter.of( "1 day" );
		period = result.getPeriod();
		assertEquals( 1, period.getDays() );
	}

	@Test
	public void dayFullTranslatedConvertsToHours() {
		DurationWithPeriod result = StringToDurationWithPeriodConverter.of( "1day" );
		Period period = result.getPeriod();
		assertEquals( 1, period.getDays() );

		result = StringToDurationWithPeriodConverter.of( "2 dagen" );
		period = result.getPeriod();
		assertEquals( 2, period.getDays() );
	}

	@Test
	public void convertToDuration() {
		assertThat( StringToDurationWithPeriodConverter.of( "7h25m" ).getDuration().toMinutes() ).isEqualTo( 7 * 60 + 25 );
		assertThat( StringToDurationWithPeriodConverter.of( "20m" ).getDuration().toMinutes() ).isEqualTo( 20 );

		DurationWithPeriod durationWithPeriod = StringToDurationWithPeriodConverter.of( "2d3h47m" );
		assertThat( durationWithPeriod.getPeriod().getDays() ).isEqualTo( 2 );
		assertThat( durationWithPeriod.getDuration().toHours() ).isEqualTo( 3 );
		assertThat( durationWithPeriod.getDuration().minusHours( 3 ).toMinutes() ).isEqualTo( 47 );

		durationWithPeriod = StringToDurationWithPeriodConverter.of( "1d -1h 30m" );
		assertThat( durationWithPeriod.getPeriod().getDays() ).isEqualTo( 1 );
		assertThat( durationWithPeriod.getDuration().toMinutes() ).isEqualTo( -30 );

		durationWithPeriod = StringToDurationWithPeriodConverter.of( "1d -1h -30m" );
		assertThat( durationWithPeriod.getPeriod().getDays() ).isEqualTo( 1 );
		assertThat( durationWithPeriod.getDuration().toMinutes() ).isEqualTo( -60 - 30 );

		durationWithPeriod = StringToDurationWithPeriodConverter.of( "-1d 9h -30m" );
		assertThat( durationWithPeriod.getPeriod().getDays() ).isEqualTo( -1 );
		assertThat( durationWithPeriod.getDuration().toMinutes() ).isEqualTo( 9 * 60 - 30 );

		durationWithPeriod = StringToDurationWithPeriodConverter.of( "1d 9h -30m" );
		assertThat( durationWithPeriod.getPeriod().getDays() ).isEqualTo( 1 );
		assertThat( durationWithPeriod.getDuration().toMinutes() ).isEqualTo( 9 * 60 - 30 );

		durationWithPeriod = StringToDurationWithPeriodConverter.of( "-1d 9h 30m" );
		assertThat( durationWithPeriod.getPeriod().getDays() ).isEqualTo( -1 );
		assertThat( durationWithPeriod.getDuration().toMinutes() ).isEqualTo( 9 * 60 + 30 );

		durationWithPeriod = StringToDurationWithPeriodConverter.of( "2w 3d 50m" );
		assertThat( durationWithPeriod.getPeriod().getDays() ).isEqualTo( 17 );
		assertThat( durationWithPeriod.getDuration().toMinutes() ).isEqualTo( 50 );

		durationWithPeriod = StringToDurationWithPeriodConverter.of( "2M 50m" );
		assertThat( durationWithPeriod.getPeriod().getMonths() ).isEqualTo( 2 );

		durationWithPeriod = StringToDurationWithPeriodConverter.of( "5y 2M" );
		assertThat( durationWithPeriod.getPeriod().getYears() ).isEqualTo( 5 );
		assertThat( durationWithPeriod.getPeriod().getMonths() ).isEqualTo( 2 );
	}

	@Test
	public void decimalMinutesAreNotAllowed() {
		assertThatExceptionOfType( IllegalArgumentException.class ).isThrownBy(
				() -> StringToDurationWithPeriodConverter.of( "25.2m" ) );
		assertThatExceptionOfType( IllegalArgumentException.class ).isThrownBy(
				() -> StringToDurationWithPeriodConverter.of( "1d17.3m" ) );
		assertThatExceptionOfType( IllegalArgumentException.class ).isThrownBy(
				() -> StringToDurationWithPeriodConverter.of( "1h 14.5m" ) );
		assertThatExceptionOfType( IllegalArgumentException.class ).isThrownBy(
				() -> StringToDurationWithPeriodConverter.of( "3 d 4h 47.8m" ) );
	}
}
