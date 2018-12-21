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
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.convert.TypeDescriptor;

import java.util.Calendar;
import java.util.Date;

import static com.foreach.across.modules.entity.query.support.EntityQueryDateFunctions.*;
import static org.junit.Assert.*;

/**
 * @author Arne Vandamme
 * @since 2.0.0
 */
public class TestEntityQueryDateFunctions
{
	private EntityQueryDateFunctions functions;

	@Before
	public void reset() {
		functions = new EntityQueryDateFunctions();
	}

	@Test
	public void accepts() {
		assertTrue( functions.accepts( "now", TypeDescriptor.valueOf( Date.class ) ) );
		assertTrue( functions.accepts( "now", TypeDescriptor.valueOf( Long.class ) ) );
		assertTrue( functions.accepts( "today", TypeDescriptor.valueOf( Date.class ) ) );
		assertTrue( functions.accepts( "today", TypeDescriptor.valueOf( Long.class ) ) );

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
		long time = (Long) functions.apply( NOW, new EQType[0], TypeDescriptor.valueOf( Long.class ), null );
		assertTrue( time >= start.getTime() && time < ( start.getTime() + 1000 ) );
	}

	@Test
	public void today() {
		Date today = DateUtils.truncate( new Date(), Calendar.DATE );
		assertEquals( today, functions.apply( TODAY, new EQType[0], TypeDescriptor.valueOf( Date.class ), null ) );
		assertEquals( today.getTime(),
		              functions.apply( TODAY, new EQType[0], TypeDescriptor.valueOf( Long.class ), null ) );
	}

	@Test
	public void startOfDay() {
		Date startOfDay = DateUtils.truncate( new Date(), Calendar.DATE );
		assertEquals( startOfDay, functions.apply( START_OF_DAY, new EQType[0], TypeDescriptor.valueOf( Date.class ), null ) );
	}

	@Test
	public void startOfWeek() {
		Date startOfWeek = DateUtils.truncate( getWeekStartDate(), Calendar.DATE );
		assertEquals( startOfWeek, functions.apply( START_OF_WEEK, new EQType[0], TypeDescriptor.valueOf( Date.class ), null ) );
	}

	@Test
	public void endOfWeek() {
		Date endOfWeek = getWeekEndDate();
		assertEquals( endOfWeek, functions.apply( END_OF_WEEK, new EQType[0], TypeDescriptor.valueOf( Date.class ), null ) );
	}

	private static Date getWeekStartDate() {
		Calendar calendar = Calendar.getInstance();
		while ( calendar.get( Calendar.DAY_OF_WEEK ) != Calendar.MONDAY ) {
			calendar.add( Calendar.DATE, -1 );
		}
		return calendar.getTime();
	}

	private static Date getWeekEndDate() {
		Calendar calendar = Calendar.getInstance();
		while ( calendar.get( Calendar.DAY_OF_WEEK ) != Calendar.MONDAY ) {
			calendar.add( Calendar.DATE, 1 );
		}
		calendar.add( Calendar.DATE, -1 );

		Date endOfWeek = calendar.getTime();
		endOfWeek.setHours( 23 );
		endOfWeek.setMinutes( 59 );
		endOfWeek.setSeconds( 59 );

		return endOfWeek;
	}
}
