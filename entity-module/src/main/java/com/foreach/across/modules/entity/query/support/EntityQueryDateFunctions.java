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

import com.foreach.across.modules.entity.query.*;
import com.foreach.across.modules.entity.util.StringToDurationWithPeriodConverter;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.core.convert.TypeDescriptor;

import java.time.*;
import java.time.temporal.TemporalAdjusters;
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

	public static final String START_OF_DAY = "startOfDay";
	public static final String START_OF_WEEK = "startOfWeek";
	public static final String START_OF_MONTH = "startOfMonth";
	public static final String START_OF_YEAR = "startOfYear";

	private static final String[] FUNCTION_NAMES = new String[] { NOW, TODAY, START_OF_DAY };

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

		return convertToDesiredType( calculateDate( functionName, arguments, argumentConverter ), desiredType.getObjectType() );
	}

	/**
	 * Calculate the right localDateTime that is represented by the functionName
	 *
	 * @param functionName The name of the function
	 * @return The resulting {@link LocalDateTime}
	 */
	private LocalDateTime calculateDate( String functionName, EQType[] arguments, EQTypeConverter argumentConverter ) {
		LocalDate today = LocalDate.now();
		DurationWithPeriod durationWithPeriod;

		switch ( functionName ) {
			case NOW:
				return WithPeriod.of( this::now );
				durationWithPeriod = validateAndGetPeriodWithDurationFromArguments( arguments, 0, functionName );
				return addDurationWithPeriodTooDateTime( LocalDateTime.now(), durationWithPeriod );
			case START_OF_DAY:
				return addTemporalAndPeriodModifiers( functionName, arguments, argumentConverter ).toLocalDate().atStartOfDay();
			case START_OF_WEEK:
				addTemporalAndPeriodModifiers( functionName, arguments, argumentConverter ).toLocalDate().atStartOfDay();
				validateAndGetFirstDayOfTheWeekParamter( arguments, 2, functionName );

				DayOfWeek firstDayOfWeek = validateAndGetFirstDayOfTheWeekParamter( arguments, 2, functionName );
				return today.with( firstDayOfWeek ).atStartOfDay();
			case START_OF_MONTH:
				return addTemporalAndPeriodModifiers( functionName, arguments, argumentConverter ).withDayOfMonth( 1 ).toLocalDate().atStartOfDay();
			case START_OF_YEAR:
				return today.withDayOfYear( 1 ).atStartOfDay();

			case TODAY:
				durationWithPeriod = validateAndGetPeriodWithDurationFromArguments( arguments, 0, functionName );
				return addDurationWithPeriodTooDateTime( LocalDate.now().atStartOfDay(), durationWithPeriod );

		}

		return LocalDateTime.now();
	}

	LocalDateTime now( DurationWithPeriod period ) {
		return LocalDateTime.now();
	}

	WithPeriod withPeriod( WithPeriod wp) {
		return wp;
	}

	interface TemporalFunction {
		LocalDateTime call( Object[] arguments, EQTypeConverter argumentConverter );
	}
	@FunctionalInterface
	interface WithPeriod extends TemporalFunction {
		@Override
		default LocalDateTime call( Object[] arguments, EQTypeConverter argumentConverter ) {
			return execute( null );
		}

		static WithPeriod of( WithPeriod wp ) {
			return wp;
		}

		LocalDateTime execute( DurationWithPeriod period );
	}
	private DayOfWeek validateAndGetFirstDayOfTheWeekParamter( EQType[] arguments, int argumentIndex, String functionName ) {
		if ( arguments.length <= argumentIndex ) {
			return WeekFields.of( Locale.getDefault() ).getFirstDayOfWeek();
		}

		if ( !EQString.class.isAssignableFrom( arguments[argumentIndex].getClass() ) ) {
			throw new IllegalArgumentException( "Invalid first day of week argument specified for the function '" + functionName + "'." );
		}

		EQString dayOfWeek = (EQString) arguments[argumentIndex];

		// todo: implement
		return DayOfWeek.MONDAY;
	}

	private DurationWithPeriod validateAndGetPeriodWithDurationFromArguments( EQType[] arguments, Integer argumentIndex, String functionName ) {
		if ( arguments.length <= argumentIndex ) {
			return DurationWithPeriod.builder().period( Period.ZERO ).duration( Duration.ZERO ).build();
		}

		if ( !EQString.class.isAssignableFrom( arguments[argumentIndex].getClass() ) ) {
			throw new IllegalArgumentException( "Invalid period argument specified for the function '" + functionName + "'." );
		}

		EQString period = (EQString) arguments[argumentIndex];
		return StringToDurationWithPeriodConverter.of( ( (EQString) period ).getValue() );
	}

	private LocalDateTime addDurationWithPeriodTooDateTime( LocalDateTime localDateTime, DurationWithPeriod durationWithPeriod ) {
		localDateTime = localDateTime.plus( durationWithPeriod.getPeriod() );
		localDateTime = localDateTime.plus( durationWithPeriod.getDuration() );

		return localDateTime;
	}

	private LocalDate addDurationWithPeriodTooDate( LocalDate localDate, DurationWithPeriod durationWithPeriod ) {
		localDate = localDate.plus( durationWithPeriod.getPeriod() );
		localDate = localDate.plus( durationWithPeriod.getDuration() );

		return localDate;
	}

	private LocalDateTime addTemporalAndPeriodModifiers( String functionName, EQType[] arguments, EQTypeConverter argumentConverter ) {
		if ( arguments.length < 1 ) {
			throw new IllegalArgumentException( "Invalid arguments specified for the function '" + functionName + "'." );
		}
		if ( !EQFunction.class.isAssignableFrom( arguments[0].getClass() ) ) {
			throw new IllegalArgumentException( "Invalid temporal argument specified for the function '" + functionName + "'." );
		}
		DurationWithPeriod durationWithPeriod = validateAndGetPeriodWithDurationFromArguments( arguments, 1, functionName );

		EQFunction temporal = (EQFunction) arguments[0];
		LocalDateTime localDateTimeFromEQFunction = (LocalDateTime) argumentConverter.convert( TypeDescriptor.valueOf( LocalDateTime.class ),
		                                                                                       temporal );
		localDateTimeFromEQFunction = addDurationWithPeriodTooDateTime( localDateTimeFromEQFunction, durationWithPeriod );

		return localDateTimeFromEQFunction;
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



