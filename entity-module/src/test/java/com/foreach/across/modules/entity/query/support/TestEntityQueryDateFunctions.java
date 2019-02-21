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
import com.foreach.across.modules.entity.query.EQString;
import com.foreach.across.modules.entity.query.EQType;
import com.foreach.across.modules.entity.query.EQTypeConverter;
import com.sun.javaws.exceptions.InvalidArgumentException;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.format.support.DefaultFormattingConversionService;

import java.time.LocalDate;
import java.util.*;

import static com.foreach.across.modules.entity.query.support.EntityQueryDateFunctions.*;
import static org.junit.Assert.*;

/**
 * @author Arne Vandamme
 * @since 2.0.0
 */
public class TestEntityQueryDateFunctions
{
	private EntityQueryDateFunctions entityQueryDateFunctions;

	private EQTypeConverter typeConverter;

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

	@Before
	public void reset() {
		entityQueryDateFunctions = new EntityQueryDateFunctions();
		Locale.setDefault( Locale.forLanguageTag( "nl" ) );

		typeConverter = new EQTypeConverter();
		typeConverter.setConversionService( new DefaultFormattingConversionService() );
		typeConverter.setFunctionHandlers( Arrays.asList( entityQueryDateFunctions ) );
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
		Date start = new Date();

		Date calculated = (Date) entityQueryDateFunctions.apply( NOW, new EQType[0], TypeDescriptor.valueOf( Date.class ), typeConverter );
		assertNotNull( calculated );
		assertTrue( calculated.getTime() >= start.getTime() && calculated.getTime() < ( start.getTime() + 1000 ) );

		start = new Date();
		long time = (Long) entityQueryDateFunctions.apply( NOW, new EQType[0], TypeDescriptor.valueOf( Long.class ), typeConverter );
		assertTrue( time >= start.getTime() && time < ( start.getTime() + 1000 ) );
	}

	@Test
	public void nowWithPeriod() {
		Date nextHour = DateUtils.addHours( new Date(), 1 );
		EQType[] nextHourFunctionArguments = { new EQString( "1h" ) };

		Date calculated = (Date) entityQueryDateFunctions.apply( NOW, nextHourFunctionArguments, TypeDescriptor.valueOf( Date.class ), typeConverter );
		assertNotNull( calculated );
		assertTrue( calculated.getTime() >= nextHour.getTime() && calculated.getTime() < ( nextHour.getTime() + 1000 ) );

		Date fiveMinutesAgo = DateUtils.addMinutes( new Date(), -5 );
		EQType[] fiveMinutesAgoFunctionArguments = { new EQString( "-5m" ) };

		calculated = (Date) entityQueryDateFunctions.apply( NOW, fiveMinutesAgoFunctionArguments, TypeDescriptor.valueOf( Date.class ), typeConverter );
		assertNotNull( calculated );
		assertTrue( calculated.getTime() >= fiveMinutesAgo.getTime() && calculated.getTime() < ( fiveMinutesAgo.getTime() + 1000 ) );

		Date theDayAfterTomorrow = DateUtils.addDays( new Date(), 2 );
		EQType[] theDayAfterTomorrowFunctionArguments = { new EQString( "2d" ) };

		calculated = (Date) entityQueryDateFunctions.apply( NOW, theDayAfterTomorrowFunctionArguments, TypeDescriptor.valueOf( Date.class ), typeConverter );
		assertNotNull( calculated );
		assertTrue( calculated.getTime() >= theDayAfterTomorrow.getTime() && calculated.getTime() < ( theDayAfterTomorrow.getTime() + 1000 ) );

		theDayAfterTomorrowFunctionArguments[0] = new EQString( "+2d" );

		calculated = (Date) entityQueryDateFunctions.apply( NOW, theDayAfterTomorrowFunctionArguments, TypeDescriptor.valueOf( Date.class ), typeConverter );
		assertNotNull( calculated );
		assertTrue( calculated.getTime() >= theDayAfterTomorrow.getTime() && calculated.getTime() < ( theDayAfterTomorrow.getTime() + 1000 ) );

		Date aYearAgo = DateUtils.addYears( new Date(), -1 );
		EQType[] aYearAgoFunctionArguments = { new EQString( "-1y" ) };

		calculated = (Date) entityQueryDateFunctions.apply( NOW, aYearAgoFunctionArguments, TypeDescriptor.valueOf( Date.class ), typeConverter );
		assertNotNull( calculated );
		assertTrue( calculated.getTime() >= aYearAgo.getTime() && calculated.getTime() < ( aYearAgo.getTime() + 1000 ) );

	}

	@Test
	public void today() {
		Date today = DateUtils.truncate( new Date(), Calendar.DATE );
		assertEquals( today, entityQueryDateFunctions.apply( TODAY, new EQType[0], TypeDescriptor.valueOf( Date.class ), typeConverter ) );
		assertEquals( today.getTime(),
		              entityQueryDateFunctions.apply( TODAY, new EQType[0], TypeDescriptor.valueOf( Long.class ), typeConverter ) );
	}

	@Test
	public void todayWithPeriod() {
		Date today = DateUtils.truncate( new Date(), Calendar.DATE );
		Date nextHour = DateUtils.addHours( today, 1 );
		EQType[] nextHourFunctionArguments = { new EQString( "1h" ) };

		assertEquals( nextHour, entityQueryDateFunctions.apply( TODAY, nextHourFunctionArguments, TypeDescriptor.valueOf( Date.class ), typeConverter ) );
		assertEquals( nextHour.getTime(),
		              entityQueryDateFunctions.apply( TODAY, nextHourFunctionArguments, TypeDescriptor.valueOf( Long.class ), typeConverter ) );
	}

	@Test(expected = IllegalArgumentException.class)
	public void startOfDayWithoutArgumentsThrowsException() {
		Date startOfDay = DateUtils.truncate( new Date(), Calendar.DATE );
		entityQueryDateFunctions.apply( START_OF_DAY, new EQType[0], TypeDescriptor.valueOf( Date.class ), typeConverter );
	}

	@Test
	public void startOfDay() {
		EQType[] nowFunctionArguments = { new EQFunction( "now" ) };

		Date startOfDay = DateUtils.truncate( new Date(), Calendar.DATE );
		assertEquals( startOfDay, entityQueryDateFunctions.apply( START_OF_DAY, nowFunctionArguments, TypeDescriptor.valueOf( Date.class ), typeConverter ) );
	}

	@Test
	public void startOfDayWithTemporal() {
		EQType[] nowFunctionArguments = { new EQFunction( "now", Collections.singletonList( new EQString( "1d" ) ) ) };

		Date startOfDay = DateUtils.truncate( DateUtils.addDays( new Date(), 1 ), Calendar.DATE );
		assertEquals( startOfDay, entityQueryDateFunctions.apply( START_OF_DAY, nowFunctionArguments, TypeDescriptor.valueOf( Date.class ), typeConverter ) );
	}

	@Test
	public void startOfDayWithPeriodAndTemporal() {
		EQType[] nowFunctionArguments = { new EQFunction( "now", Collections.singletonList( new EQString( "1d" ) ) ), new EQString( "1d" ) };

		Date startOfDay = DateUtils.truncate( DateUtils.addDays( new Date(), 2 ), Calendar.DATE );
		assertEquals( startOfDay, entityQueryDateFunctions.apply( START_OF_DAY, nowFunctionArguments, TypeDescriptor.valueOf( Date.class ), typeConverter ) );
	}

	@Test
	public void startOfWeek() {
		Date startOfWeek = DateUtils.truncate( getWeekStartDate(), Calendar.DATE );
		assertEquals( startOfWeek, entityQueryDateFunctions.apply( START_OF_WEEK, new EQType[0], TypeDescriptor.valueOf( Date.class ), null ) );

		Date twoWeeksAgo = DateUtils.addWeeks( getWeekStartDate(), -2 );
		EQType[] twoWeeksAgoFunctionArguments = { new EQString( "-2w" ) };

		Date calculated = (Date) entityQueryDateFunctions.apply( START_OF_WEEK, twoWeeksAgoFunctionArguments, TypeDescriptor.valueOf( Date.class ), null );
		assertNotNull( calculated );
		assertTrue( calculated.getTime() >= twoWeeksAgo.getTime() && calculated.getTime() < ( twoWeeksAgo.getTime() + 1000 ) );
	}

	@Test(expected = IllegalArgumentException.class)
	public void startOfMonth() {
		Date startOfMonth = DateUtils.truncate( getMonthStartDate(), Calendar.DATE );
		assertEquals( startOfMonth, entityQueryDateFunctions.apply( START_OF_MONTH, new EQType[0], TypeDescriptor.valueOf( Date.class ), null ) );
	}

	@Test
	public void startOfMonthWithTemporal() {
		EQType[] nowFunctionArguments = { new EQFunction( "now", Collections.singletonList( new EQString( "1M" ) ) ) };

		Date startOfMonth = DateUtils.truncate( DateUtils.addMonths( getMonthStartDate(), 1 ), Calendar.DATE );
		assertEquals( startOfMonth,
		              entityQueryDateFunctions.apply( START_OF_MONTH, nowFunctionArguments, TypeDescriptor.valueOf( Date.class ), typeConverter ) );
	}

	@Test
	public void startOfMonthWithTemporalAndPeriod() {
		EQType[] nowFunctionArguments = { new EQFunction( "now", Collections.singletonList( new EQString( "1M" ) ) ), new EQString( "2M" ) };

		Date startOfMonth = DateUtils.truncate( DateUtils.addMonths( getMonthStartDate(), 3 ), Calendar.DATE );
		assertEquals( startOfMonth,
		              entityQueryDateFunctions.apply( START_OF_MONTH, nowFunctionArguments, TypeDescriptor.valueOf( Date.class ), typeConverter ) );
	}

	@Test
	public void startOfYear() {
		Date startOfYear = DateUtils.truncate( getYearStartDate(), Calendar.DATE );
		assertEquals( startOfYear, entityQueryDateFunctions.apply( START_OF_YEAR, new EQType[0], TypeDescriptor.valueOf( Date.class ), null ) );
	}
}
