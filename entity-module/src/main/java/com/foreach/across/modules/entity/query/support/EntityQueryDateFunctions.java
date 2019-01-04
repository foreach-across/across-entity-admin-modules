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

import com.foreach.across.modules.entity.converters.StringToDurationConverter;
import com.foreach.across.modules.entity.query.EQString;
import com.foreach.across.modules.entity.query.EQType;
import com.foreach.across.modules.entity.query.EQTypeConverter;
import com.foreach.across.modules.entity.query.EntityQueryFunctionHandler;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.core.convert.TypeDescriptor;

import java.time.*;
import java.time.temporal.WeekFields;
import java.util.Date;
import java.util.Locale;

/**
 * Provides date related functions to be used in entity queries.
 * <ul>
 * <li>now(): returns the current time</li>
 * <li>today(): returns the date of today</li>
 * </ul>
 * Supported property types are {@link Date} and {@link Long}.
 *
 * @author Arne Vandamme
 * @since 2.0.0
 */
public class EntityQueryDateFunctions implements EntityQueryFunctionHandler
{
	public static final String NOW = "now";
	public static final String TODAY = "today";

	public static final String END_OF_DAY = "endOfDay";
	public static final String END_OF_WEEK = "endOfWeek";
	public static final String END_OF_MONTH = "endOfMonth";
	public static final String END_OF_YEAR = "endOfYear";
	public static final String START_OF_DAY = "startOfDay";
	public static final String START_OF_WEEK = "startOfWeek";
	public static final String START_OF_MONTH = "startOfMonth";
	public static final String START_OF_YEAR = "startOfYear";

	private static final String[] FUNCTION_NAMES = new String[] { NOW, TODAY };

	@Override
	public boolean accepts( String functionName, TypeDescriptor desiredType ) {
		return ArrayUtils.contains( FUNCTION_NAMES, functionName ) && (
				Date.class.equals( desiredType.getObjectType() )
						|| LocalDateTime.class.equals( desiredType.getObjectType() )
						|| Long.class.equals( desiredType.getObjectType() )
		);
	}

	@Override
	public Object apply( String functionName,
	                     EQType[] arguments,
	                     TypeDescriptor desiredType,
	                     EQTypeConverter argumentConverter ) {
		LocalDateTime calculated = calculateDate( functionName );
		calculated = addDateTimeModifiers( calculated, arguments );

		return convertToDesiredType( calculated, desiredType.getObjectType() );
	}

	/**
	 * Calculate the right localDateTime that is represented by the functionName
	 *
	 * @param functionName The name of the function
	 * @return The resulting {@link LocalDateTime}
	 */
	private LocalDateTime calculateDate( String functionName ) {
		LocalDate today = LocalDate.now();

		switch ( functionName ) {
			case TODAY:
				return today.atStartOfDay();
			case START_OF_DAY:
				return today.atStartOfDay();
			case START_OF_WEEK:
				DayOfWeek firstDayOfWeek = WeekFields.of( Locale.getDefault() ).getFirstDayOfWeek();

				return today.with( firstDayOfWeek ).atStartOfDay();
			case START_OF_MONTH:
				return today.withDayOfMonth( 1 ).atStartOfDay();
			case START_OF_YEAR:
				return today.withDayOfYear( 1 ).atStartOfDay();
			case END_OF_DAY:
				return today.atTime( LocalTime.MAX );
			case END_OF_WEEK:
				DayOfWeek firstDayOfThisWeek = WeekFields.of( Locale.getDefault() ).getFirstDayOfWeek();
				DayOfWeek lastDayOfWeek = DayOfWeek.of( ( ( firstDayOfThisWeek.getValue() + 5 ) % DayOfWeek.values().length ) + 1 );

				return today.with( lastDayOfWeek ).atTime( LocalTime.MAX );
			case END_OF_MONTH:
				return today.withDayOfMonth( today.lengthOfMonth() ).atTime( LocalTime.MAX );
			case END_OF_YEAR:
				return today.withDayOfYear( today.lengthOfYear() ).atTime( LocalTime.MAX );
		}

		return LocalDateTime.now();
	}

	/**
	 * Parse all string arguments to {@link Duration} objects and modify the date
	 *
	 * @param dateTime  The calculated dateTime
	 * @param arguments An array of modifiers represented by strings y|M|w|d|h|m e.g. +1d, -1y, ...
	 * @return the modified calculated dateTime
	 */
	private LocalDateTime addDateTimeModifiers( LocalDateTime dateTime, EQType[] arguments ) {
		for ( EQType argument : arguments ) {
			if ( EQString.class.isAssignableFrom( argument.getClass() ) ) {
				Duration duration = StringToDurationConverter.convertToDuration( ( (EQString) argument ).getValue() );
				dateTime = dateTime.plus( duration );
			}
		}

		return dateTime;
	}

	/**
	 * Convert the calculated dateTime to the desired object type
	 *
	 * @param dateTime    The calculated dateTime
	 * @param desiredType The type that is expected as output
	 * @return An instance of the desiredType
	 */
	private Object convertToDesiredType( LocalDateTime dateTime, Class<?> desiredType ) {
		if ( Long.class.equals( desiredType ) ) {
			return Date.from( dateTime.atZone( ZoneId.systemDefault() ).toInstant() ).getTime();
		}

		if ( Date.class.equals( desiredType ) ) {
			return Date.from( dateTime.atZone( ZoneId.systemDefault() ).toInstant() );
		}

		return dateTime;
	}
}