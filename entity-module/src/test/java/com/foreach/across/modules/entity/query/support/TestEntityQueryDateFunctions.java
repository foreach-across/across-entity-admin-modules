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

import java.time.*;
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

	private static Date getStartOfDay() {
		Calendar calendar = Calendar.getInstance();
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
		assertTrue( entityQueryDateFunctions.accepts( "now", TypeDescriptor.valueOf( LocalDateTime.class ) ) );
		assertTrue( functions.accepts( "now", TypeDescriptor.valueOf( LocalDate.class ) ) );
		assertTrue( functions.accepts( "now", TypeDescriptor.valueOf( LocalTime.class ) ) );
		assertTrue( functions.accepts( "now", TypeDescriptor.valueOf( ZonedDateTime.class ) ) );
		assertTrue( functions.accepts( "today", TypeDescriptor.valueOf( Date.class ) ) );
		assertTrue( entityQueryDateFunctions.accepts( "today", TypeDescriptor.valueOf( Long.class ) ) );
		assertTrue( functions.accepts( "today", TypeDescriptor.valueOf( LocalDateTime.class ) ) );
		assertTrue( functions.accepts( "today", TypeDescriptor.valueOf( LocalDate.class ) ) );
		assertTrue( functions.accepts( "today", TypeDescriptor.valueOf( LocalTime.class ) ) );
		assertTrue( functions.accepts( "today", TypeDescriptor.valueOf( ZonedDateTime.class ) ) );

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
		Date moreThenOneHourAgo = DateUtils.addSeconds(DateUtils.addMinutes( DateUtils.addHours( new Date(), -1 ), -3 ), 5);

		assertThat( eqf( "now('1h 3m')", Date.class ) )
				.isBefore( DateUtils.addSeconds( nextHourWithThreeMinutes, 1 ) )
				.isAfterOrEqualsTo( nextHourWithThreeMinutes );

		assertThat( eqf( "now('+1h +3m')", Date.class ) )
				.isBefore( DateUtils.addSeconds( nextHourWithThreeMinutes, 1 ) )
				.isAfterOrEqualsTo( nextHourWithThreeMinutes );

		start = new Date();
		Object result = functions.apply( NOW, new EQType[0], TypeDescriptor.valueOf( Long.class ), null );
		assertTrue( result instanceof Long );
		Long time = (Long) result;
		assertTrue( time >= start.getTime() && time < ( start.getTime() + 1000 ) );

		assertThat( eqf( "now('-1h3m 5s')", Date.class ) )
				.isBefore( DateUtils.addSeconds( moreThenOneHourAgo, 1 ) )
				.isAfterOrEqualsTo( moreThenOneHourAgo );
	}

	@Test
	public void nowWithLocalDateAsReturnType() {
		LocalDate startLocalDate = LocalDate.now();
		Object result = functions.apply( NOW, new EQType[0], TypeDescriptor.valueOf( LocalDate.class ), null );
		assertTrue( result instanceof LocalDate );

		LocalDate calculatedLocalDate = (LocalDate) result;
		assertEquals( calculatedLocalDate.getDayOfYear(), startLocalDate.getDayOfYear() );
	}

	@Test
	public void nowWithLocalTimeAsReturnType() {
		LocalTime startLocalTime = LocalTime.now();
		Object result = functions.apply( NOW, new EQType[0], TypeDescriptor.valueOf( LocalTime.class ), null );
		assertTrue( result instanceof LocalTime );

		LocalTime calculatedLocalTime = (LocalTime) result;
		assertTrue( startLocalTime.getNano() <= calculatedLocalTime.getNano() );
	}

	@Test
	public void nowWithZoneDateTimeAsReturnType() {
		ZonedDateTime start = ZonedDateTime.now();
		Object result = functions.apply( NOW, new EQType[0], TypeDescriptor.valueOf( ZonedDateTime.class ), null );
		assertTrue( result instanceof ZonedDateTime );

		ZonedDateTime calculatedLocalTime = (ZonedDateTime) result;
		assertTrue( start.getNano() <= calculatedLocalTime.getNano()  );
	}

	@Test
	public void nowWithLocalDateTimeAsReturnType() {
		LocalDateTime start = LocalDateTime.now();
		Object result = functions.apply( NOW, new EQType[0], TypeDescriptor.valueOf( LocalDateTime.class ), null );
		assertTrue( result instanceof LocalDateTime );

		LocalDateTime calculatedLocalDateTime = (LocalDateTime) result;
		assertNotNull( calculatedLocalDateTime );

		assertTrue( start.getNano() <= calculatedLocalDateTime.getNano()  );

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

		Date yesterday = DateUtils.addDays( getStartOfDay(), -1 );
		Date tomorrow = DateUtils.addDays( getStartOfDay(), 1 );

		Date withSpecialPeriod = DateUtils.addDays( getStartOfDay(), 7 );
		withSpecialPeriod = DateUtils.addHours( withSpecialPeriod, 13 );
		withSpecialPeriod = DateUtils.addMinutes( withSpecialPeriod, 30 );


		assertThat( eqf( "today('-1h')", Date.class ) )
				.isEqualTo( yesterdayAt2300 );

		assertThat( eqf( "today('-1d')", Date.class ) )
				.isEqualTo( yesterday );

		assertThat( eqf( "today('1d')", Date.class ) )
				.isEqualTo( tomorrow );

		assertThat( eqf( "today('+7d +13h +30m')", Date.class ) )
				.isEqualTo( withSpecialPeriod );

		assertThat( eqf( "today('+7d at 13:30')", Date.class ) )
				.isEqualTo( withSpecialPeriod );

		assertThat( eqf( "today('+7d at 13:30')", Date.class ) )
				.isEqualTo( withSpecialPeriod );
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

		assertThat( eqf( "startOfMonth(now())", Date.class ) )
				.isEqualTo( expectedDate );

		assertThat( eqf( "thisMonth()", Date.class ) )
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
		Date towMonthsLater = DateUtils.addMonths( expectedDate, 2 );

		assertThat( eqf( "startOfYear()", Date.class ) )
				.isEqualTo( expectedDate );

		assertThat( eqf( "startOfYear(now(), 2M)", Date.class ) )
				.isEqualTo( towMonthsLater );

		assertThat( eqf( "thisYear('2M')", Date.class ) )
				.isEqualTo( towMonthsLater );
	}

	@Test
	public void atTimestamp() {
		Calendar calendar = Calendar.getInstance();
		calendar.set( Calendar.HOUR_OF_DAY, 15 );
		calendar.set( Calendar.MINUTE, 20 );
		calendar.set( Calendar.SECOND, 1 );
		calendar.set( Calendar.MILLISECOND, 0 );

		assertThat( eqf( "today('+1s at 15:20')", Date.class ) )
				.isEqualTo( calendar.getTime() );
	}

	@Test
	public void atTimestampWithSeconds() {
		Calendar calendar = Calendar.getInstance();
		calendar.set( Calendar.HOUR_OF_DAY, 15 );
		calendar.set( Calendar.MINUTE, 20 );
		calendar.set( Calendar.SECOND, 3 );
		calendar.set( Calendar.MILLISECOND, 0 );

		assertThat( eqf( "today('+1s at 15:20:02')", Date.class ) )
				.isEqualTo( calendar.getTime() );
	}

	@Test
	public void offsetWithFunctionAndModifier() {
		Calendar calendar = Calendar.getInstance();
		calendar.set( Calendar.HOUR_OF_DAY, 15 );
		calendar.set( Calendar.MINUTE, 0 );
		calendar.set( Calendar.SECOND, 0 );
		calendar.set( Calendar.MILLISECOND, 0 );
		Date expectedDate = DateUtils.addDays( calendar.getTime(), 1 );

		assertThat( eqf( "offset(today(), '+1d at 15:00')", Date.class ) )
				.isEqualTo( expectedDate );
	}

	@Test
	public void offsetWithDateTimestamp() {
		Calendar calendar = Calendar.getInstance();
		calendar.set( Calendar.YEAR, 2019 );
		calendar.set( Calendar.MONTH, 2 );
		calendar.set( Calendar.DAY_OF_MONTH, 2 );
		calendar.set( Calendar.HOUR_OF_DAY, 15 );
		calendar.set( Calendar.MINUTE, 5 );
		calendar.set( Calendar.SECOND, 0 );
		calendar.set( Calendar.MILLISECOND, 0 );

		assertThat( eqf( "offset(2019-03-02, 'at 15:05')", Date.class ) )
				.isEqualTo( calendar.getTime() );
	}

	@Test
	public void startOfWeek() {
		Date expectedDate = DateUtils.truncate( getWeekStartDate(), Calendar.DATE );

		assertThat( eqf( "startOfWeek()", Date.class ) )
				.isEqualTo( expectedDate );
	}

	@Test
	public void twoWeeksAgo() {
		Date expectedDate = DateUtils.addWeeks( getWeekStartDate(), -2 );

		assertThat( eqf( "startOfWeek(now(), -2w)", Date.class ) )
				.isEqualTo( expectedDate );

	}

	@Test
	public void nestedWeekFunctions() {
		Date expectedDate = DateUtils.addDays( DateUtils.addWeeks( getWeekStartDate(), +1 ), -2 );

		assertThat( eqf( "startOfWeek(nextWeek(), +1D, fri )", Date.class ) )
				.isEqualTo( expectedDate );
	}

	@Test
	public void thisWeekPlusTwoWeeks() {
		Date expectedDate = DateUtils.addWeeks( getWeekStartDate(), 2 );

		assertThat( eqf( "thisWeek(+2w)", Date.class ) )
				.isEqualTo( expectedDate );
	}

	@Test
	public void thisWeekPlusTwoWeeksWhenWeeksStartOnSunday() {
		Date expectedDate = DateUtils.addWeeks( getWeekStartDate(), 2 );
		expectedDate = DateUtils.truncate( DateUtils.addDays( expectedDate, -1 ), Calendar.DATE );

		assertThat( eqf( "thisWeek(+2w, sun)", Date.class ) )
				.isEqualTo( expectedDate );
	}

	@Test
	public void nextWeek() {
		Date expectedDate = DateUtils.addWeeks( getWeekStartDate(), 1 );

		assertThat( eqf( "nextWeek()", Date.class ) )
				.isEqualTo( expectedDate );
	}

	@Test
	public void nextYear() {
		Date expectedDate = DateUtils.addYears( getYearStartDate(), 1 );

		assertThat( eqf( "nextYear()", Date.class ) )
				.isEqualTo( expectedDate );

		assertThat( eqf( "startOfYear(today(+1y))", Date.class ) )
				.isEqualTo( expectedDate );
	}

	@Test
	public void lastYear() {
		Date expectedDate = DateUtils.addYears( getYearStartDate(), -1 );

		assertThat( eqf( "lastYear()", Date.class ) )
				.isEqualTo( expectedDate );

		assertThat( eqf( "startOfYear(today(-1y))", Date.class ) )
				.isEqualTo( expectedDate );
	}

	@Test
	public void nextMonth() {
		Date expectedDate = DateUtils.addMonths( getMonthStartDate(), 1 );

		assertThat( eqf( "nextMonth()", Date.class ) )
				.isEqualTo( expectedDate );

		assertThat( eqf( "startOfMonth(today(+1M))", Date.class ) )
				.isEqualTo( expectedDate );
	}

	@Test
	public void lastWeek() {
		Date expectedDate = DateUtils.addWeeks( getWeekStartDate(), -1 );

		assertThat( eqf( "lastWeek()", Date.class ) )
				.isEqualTo( expectedDate );

		LocalDateTime todayLocalDateTime = today.toInstant().atZone( ZoneId.systemDefault() ).toLocalDateTime();
		assertEquals( todayLocalDateTime, functions.apply( TODAY, new EQType[0], TypeDescriptor.valueOf( LocalDateTime.class ), null ) );
	}

	@Test
	public void todayWithLocalDateAsReturnType() {
		Date today = DateUtils.truncate( new Date(), Calendar.DATE );
		Object result = functions.apply( TODAY, new EQType[0], TypeDescriptor.valueOf( LocalDate.class ), null );

		assertTrue( result instanceof LocalDate );

		LocalDate calculatedLocalDate = (LocalDate) result;
		assertEquals( calculatedLocalDate.getDayOfWeek().getValue(), today.getDay() );
	}

	@Test
	public void todayWithLocalTimeAsReturnType() {
		Date today = DateUtils.truncate( new Date(), Calendar.DATE );
		Object result = functions.apply( NOW, new EQType[0], TypeDescriptor.valueOf( LocalTime.class ), null );
		assertTrue( result instanceof LocalTime );
	}

	@Test
	public void todayWithZoneDateTimeAsReturnType() {
		Date today = DateUtils.truncate( new Date(), Calendar.DATE );
		Object result = functions.apply( NOW, new EQType[0], TypeDescriptor.valueOf( ZonedDateTime.class ), null );
		assertTrue( result instanceof ZonedDateTime );

		ZonedDateTime calculatedLocalTime = (ZonedDateTime) result;
		assertEquals( today.getDay(), calculatedLocalTime.getDayOfWeek().getValue() );
	}

	@Test
	public void todayWithLocalDateTimeAsReturnType() {
		Date today = DateUtils.truncate( new Date(), Calendar.DATE );
		Object result = functions.apply( NOW, new EQType[0], TypeDescriptor.valueOf( LocalDateTime.class ), null );

		assertTrue( result instanceof LocalDateTime );

		LocalDateTime calculatedLocalDateTime = (LocalDateTime) result;
		assertNotNull( calculatedLocalDateTime );

		assertEquals( today.getDay(), calculatedLocalDateTime.getDayOfWeek().getValue() );
	}
}
