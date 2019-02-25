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

package com.foreach.across.modules.entity.query.support;

import com.foreach.across.modules.entity.query.EQFunction;
import com.foreach.across.modules.entity.query.EQTypeConverter;
import com.foreach.across.modules.entity.query.EntityQuery;
import com.foreach.across.modules.entity.query.EntityQueryCondition;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.format.support.DefaultFormattingConversionService;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Arne Vandamme
 * @since 2.0.0
 */
public class TestEntityQueryDateFunctions
{
	private EntityQueryDateFunctions entityQueryDateFunctions;
	private EQTypeConverter typeConverter;
	private Date startDate;

	private static Date getWeekStartDate() {
		Calendar calendar = Calendar.getInstance();
		while ( calendar.get( Calendar.DAY_OF_WEEK ) != Calendar.MONDAY ) {
			calendar.add( Calendar.DATE, -1 );
		}
		calendar.set( Calendar.HOUR_OF_DAY, 0 );
		calendar.set( Calendar.MINUTE, 0 );
		calendar.set( Calendar.SECOND, 0 );
		calendar.set( Calendar.MILLISECOND, 0 );

		return calendar.getTime();
	}

	private static Date getMonthStartDate() {
		Calendar calendar = Calendar.getInstance();

		calendar.set( Calendar.DAY_OF_MONTH, 1 );
		calendar.set( Calendar.HOUR_OF_DAY, 0 );
		calendar.set( Calendar.MINUTE, 0 );
		calendar.set( Calendar.SECOND, 0 );
		calendar.set( Calendar.MILLISECOND, 0 );

		return calendar.getTime();
	}

	private static Date getYearStartDate() {
		Calendar calendar = Calendar.getInstance();

		calendar.set( Calendar.DAY_OF_YEAR, 1 );
		calendar.set( Calendar.MONTH, 1 );
		calendar.set( Calendar.HOUR_OF_DAY, 0 );
		calendar.set( Calendar.MINUTE, 0 );
		calendar.set( Calendar.SECOND, 0 );
		calendar.set( Calendar.MILLISECOND, 0 );

		return calendar.getTime();
	}

	private static Date getEndOfDay() {
		Calendar calendar = Calendar.getInstance();
		calendar.set( Calendar.HOUR_OF_DAY, 23 );
		calendar.set( Calendar.MINUTE, 59 );
		calendar.set( Calendar.SECOND, 59 );
		calendar.set( Calendar.MILLISECOND, 999 );

		return calendar.getTime();
	}

	private static Date getWeekEndDate() {
		Calendar calendar = Calendar.getInstance();
		while ( calendar.get( Calendar.DAY_OF_WEEK ) != Calendar.MONDAY ) {
			calendar.add( Calendar.DATE, 1 );
		}

		calendar.add( Calendar.DATE, -1 );
		calendar.set( Calendar.HOUR_OF_DAY, 23 );
		calendar.set( Calendar.MINUTE, 59 );
		calendar.set( Calendar.SECOND, 59 );
		calendar.set( Calendar.MILLISECOND, 999 );

		return calendar.getTime();
	}

	private static Date getMonthEndDate() {
		Calendar calendar = Calendar.getInstance();
		LocalDate today = LocalDate.now();

		calendar.set( Calendar.DAY_OF_MONTH, today.lengthOfMonth() );
		calendar.set( Calendar.HOUR_OF_DAY, 23 );
		calendar.set( Calendar.MINUTE, 59 );
		calendar.set( Calendar.SECOND, 59 );
		calendar.set( Calendar.MILLISECOND, 999 );

		return calendar.getTime();
	}

	private static Date getYearEndDate() {
		Calendar calendar = Calendar.getInstance();

		calendar.set( Calendar.DAY_OF_YEAR, 365 );
		calendar.set( Calendar.MONTH, 12 );
		calendar.set( Calendar.HOUR_OF_DAY, 23 );
		calendar.set( Calendar.MINUTE, 59 );
		calendar.set( Calendar.SECOND, 59 );
		calendar.set( Calendar.MILLISECOND, 999 );

		return calendar.getTime();
	}

	private <T> T eqf( String eqf, Class<T> targetClass ) {
		EQFunction eqFunction = (EQFunction) ( (EntityQueryCondition) EntityQuery.parse( "x = " + eqf ).getExpressions().get( 0 ) ).getArguments()[0];

		return (T) typeConverter.convert( TypeDescriptor.valueOf( targetClass ), eqFunction );
	}

	@Before
	public void reset() {
		entityQueryDateFunctions = new EntityQueryDateFunctions();
		Locale.setDefault( Locale.forLanguageTag( "nl" ) );

		typeConverter = new EQTypeConverter();
		typeConverter.setConversionService( new DefaultFormattingConversionService() );
		typeConverter.setFunctionHandlers( Arrays.asList( entityQueryDateFunctions ) );

		startDate = new Date();
	}

	@Test
	public void accepts() {
		assertTrue( entityQueryDateFunctions.accepts( "now", TypeDescriptor.valueOf( Date.class ) ) );
		assertTrue( entityQueryDateFunctions.accepts( "now", TypeDescriptor.valueOf( Long.class ) ) );
		assertTrue( entityQueryDateFunctions.accepts( "today", TypeDescriptor.valueOf( Date.class ) ) );
		assertTrue( entityQueryDateFunctions.accepts( "today", TypeDescriptor.valueOf( Long.class ) ) );

		assertFalse( entityQueryDateFunctions.accepts( "unknown", TypeDescriptor.valueOf( Date.class ) ) );
		assertFalse( entityQueryDateFunctions.accepts( "now", TypeDescriptor.valueOf( String.class ) ) );
	}

	@Test
	public void now() {
		assertThat( eqf( "now()", Date.class ) )
				.isBefore( DateUtils.addSeconds( startDate, 2 ) )
				.isAfterOrEqualsTo( startDate );

		assertThat( eqf( "now()", Long.class ) )
				.isLessThan( DateUtils.addSeconds( startDate, 2 ).getTime() )
				.isGreaterThanOrEqualTo( startDate.getTime() );
	}

	@Test
	public void nowWithPeriod() {
		Date nextHourWithThreeMinutes = DateUtils.addMinutes( DateUtils.addHours( new Date(), 1 ), 3 );

		assertThat( eqf( "now('1h 3m')", Date.class ) )
				.isBefore( DateUtils.addSeconds( nextHourWithThreeMinutes, 1 ) )
				.isAfterOrEqualsTo( nextHourWithThreeMinutes );

	}

	@Test
	public void today() {
		Date today = DateUtils.truncate( new Date(), Calendar.DATE );
		assertThat( eqf( "today()", Date.class ) )
				.isEqualTo( today );
	}

	@Test
	public void todayWithPeriod() {
		Calendar calendar = Calendar.getInstance();
		calendar.set( Calendar.HOUR_OF_DAY, 23 );
		calendar.set( Calendar.MINUTE, 0 );
		calendar.set( Calendar.SECOND, 0 );
		calendar.set( Calendar.MILLISECOND, 0 );
		Date yesterdayAt2300 = DateUtils.addDays( calendar.getTime(), -1 );

		assertThat( eqf( "today('-1h')", Date.class ) )
				.isEqualTo( yesterdayAt2300 );
	}

	@Test
	public void startOfDayWithoutArgumentsThrowsException() {
		Date startOfDay = DateUtils.truncate( new Date(), Calendar.DATE );

		assertThat( eqf( "startOfDay()", Date.class ) )
				.isEqualTo( startOfDay );
	}

	@Test
	public void startOfDay() {
		Date expectedDate = DateUtils.truncate(
				DateUtils.addDays( new Date(), 1 ),
				Calendar.DATE
		);

		assertThat( eqf( "startOfDay(now('+1d'))", Date.class ) )
				.isEqualTo( expectedDate );

	}

	@Test
	public void startOfDayWithPeriodAndTemporal() {
		Date expectedDate = DateUtils.truncate( DateUtils.addDays( new Date(), 2 ), Calendar.DATE );
		assertThat( eqf( "startOfDay(now('+1d'), '1d')", Date.class ) )
				.isEqualTo( expectedDate );
	}

	@Test
	public void startOfMonth() {
		Date expectedDate = DateUtils.truncate( getMonthStartDate(), Calendar.DATE );
		assertThat( eqf( "startOfMonth()", Date.class ) )
				.isEqualTo( expectedDate );
	}

	@Test(expected = IllegalArgumentException.class)
	public void startOfMonthWithOnlyPeriodThrowsException() {
		eqf( "startOfMonth('1M')", Date.class );
	}

	@Test
	public void startOfMonthWithTemporal() {
		Date expectedDate = DateUtils.truncate( DateUtils.addMonths( getMonthStartDate(), 1 ), Calendar.DATE );
		assertThat( eqf( "startOfMonth(now('1M'))", Date.class ) )
				.isEqualTo( expectedDate );
	}

	@Test
	public void startOfMonthWithTemporalAndPeriod() {
		Date expectedDate = DateUtils.truncate( DateUtils.addMonths( getMonthStartDate(), 3 ), Calendar.DATE );
		assertThat( eqf( "startOfMonth(now('1M'), '2M')", Date.class ) )
				.isEqualTo( expectedDate );
	}

	@Test
	public void startOfYear() {
		Date expectedDate = DateUtils.truncate( getYearStartDate(), Calendar.DATE );
		assertThat( eqf( "startOfYear()", Date.class ) )
				.isEqualTo( expectedDate );
	}

	public void startOfWeek() {
		Date startOfWeek = DateUtils.truncate( getWeekStartDate(), Calendar.DATE );
		Date twoWeeksAgo = DateUtils.addWeeks( getWeekStartDate(), -2 );
	}

	@Test
	public void atTimestamp() {
		Calendar calendar = Calendar.getInstance();
		calendar.set( Calendar.HOUR_OF_DAY, 15 );
		calendar.set( Calendar.MINUTE, 20 );
		calendar.set( Calendar.SECOND, 0 );
		calendar.set( Calendar.MILLISECOND, 0 );
		Date expectedDate = DateUtils.addDays( calendar.getTime(), 1 );

		assertThat( eqf( "today('+1s at 15:20')", Date.class ) )
				.isEqualTo( expectedDate );
	}

	public void offset() {
		Date expectedDate = DateUtils.truncate( getYearStartDate(), Calendar.DATE );
		assertThat( eqf( "offset(today(), '+1d at 15:00')", Date.class ) )
				.isEqualTo( expectedDate );

		assertThat( eqf( "offset('2019-01-01', 'at 15:00')", Date.class ) )
				.isEqualTo( expectedDate );
	}


}
