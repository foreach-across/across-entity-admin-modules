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

import com.foreach.across.modules.entity.query.EQType;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.convert.TypeDescriptor;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;

import static com.foreach.across.modules.entity.query.support.EntityQueryDateFunctions.NOW;
import static com.foreach.across.modules.entity.query.support.EntityQueryDateFunctions.TODAY;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Arne Vandamme
 * @since 2.0.0
 */
public class TestEntityQueryDateFunctions
{
	private EntityQueryDateFunctions functions;

	@BeforeEach
	public void reset() {
		functions = new EntityQueryDateFunctions();
	}

	@Test
	public void accepts() {
		assertTrue( functions.accepts( "now", TypeDescriptor.valueOf( Date.class ) ) );
		assertTrue( functions.accepts( "now", TypeDescriptor.valueOf( Long.class ) ) );
		assertTrue( functions.accepts( "now", TypeDescriptor.valueOf( LocalDateTime.class ) ) );
		assertTrue( functions.accepts( "now", TypeDescriptor.valueOf( LocalDate.class ) ) );
		assertTrue( functions.accepts( "now", TypeDescriptor.valueOf( LocalTime.class ) ) );
		assertTrue( functions.accepts( "now", TypeDescriptor.valueOf( ZonedDateTime.class ) ) );
		assertTrue( functions.accepts( "today", TypeDescriptor.valueOf( Date.class ) ) );
		assertTrue( functions.accepts( "today", TypeDescriptor.valueOf( Long.class ) ) );
		assertTrue( functions.accepts( "today", TypeDescriptor.valueOf( LocalDateTime.class ) ) );
		assertTrue( functions.accepts( "today", TypeDescriptor.valueOf( LocalDate.class ) ) );
		assertTrue( functions.accepts( "today", TypeDescriptor.valueOf( LocalTime.class ) ) );
		assertTrue( functions.accepts( "today", TypeDescriptor.valueOf( ZonedDateTime.class ) ) );

		assertFalse( functions.accepts( "unknown", TypeDescriptor.valueOf( Date.class ) ) );
		assertFalse( functions.accepts( "now", TypeDescriptor.valueOf( String.class ) ) );
	}

	@Test
	public void now() {
		Date start = new Date();

		Date calculated = (Date) functions.apply( NOW, new EQType[0], TypeDescriptor.valueOf( Date.class ), null );
		assertNotNull( calculated );
		assertTrue( calculated.getTime() >= start.getTime() && calculated.getTime() < ( start.getTime() + 1000 ) );

		start = new Date();
		Object result = functions.apply( NOW, new EQType[0], TypeDescriptor.valueOf( Long.class ), null );
		assertTrue( result instanceof Long );
		Long time = (Long) result;
		assertTrue( time >= start.getTime() && time < ( start.getTime() + 1000 ) );
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
		assertEquals( today, functions.apply( TODAY, new EQType[0], TypeDescriptor.valueOf( Date.class ), null ) );
		assertEquals( today.getTime(),
		              functions.apply( TODAY, new EQType[0], TypeDescriptor.valueOf( Long.class ), null ) );

		LocalDateTime todayLocalDateTime = today.toInstant().atZone( ZoneId.systemDefault() ).toLocalDateTime();
		assertEquals( todayLocalDateTime, functions.apply( TODAY, new EQType[0], TypeDescriptor.valueOf( LocalDateTime.class ), null ) );
	}

	@Test
	public void todayWithLocalDateAsReturnType() {
		Date today = DateUtils.truncate( new Date(), Calendar.DATE );
		Object result = functions.apply( TODAY, new EQType[0], TypeDescriptor.valueOf( LocalDate.class ), null );

		assertTrue( result instanceof LocalDate );

		LocalDate calculatedLocalDate = (LocalDate) result;
		assertEquals( calculatedLocalDate.getDayOfWeek().getValue(), dayOfWeekForToday() );
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
		assertEquals( dayOfWeekForToday(), calculatedLocalTime.getDayOfWeek().getValue() );
	}

	@Test
	public void todayWithLocalDateTimeAsReturnType() {
		Object result = functions.apply( NOW, new EQType[0], TypeDescriptor.valueOf( LocalDateTime.class ), null );

		assertTrue( result instanceof LocalDateTime );

		LocalDateTime calculatedLocalDateTime = (LocalDateTime) result;
		assertNotNull( calculatedLocalDateTime );

		assertEquals( dayOfWeekForToday(), calculatedLocalDateTime.getDayOfWeek().getValue() );
	}

	private int dayOfWeekForToday() {
		return DayOfWeek.from( LocalDateTime.now().truncatedTo( ChronoUnit.DAYS ) ).getValue();
	}
}
