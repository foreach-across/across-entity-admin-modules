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
	private static final String NOW = "now";
	private static final String OFFSET = "offset";
	private static final String TODAY = "today";
	private static final String START_OF_DAY = "startOfDay";
	private static final String START_OF_WEEK = "startOfWeek";
	private static final String START_OF_MONTH = "startOfMonth";
	private static final String START_OF_YEAR = "startOfYear";

	private static final String[] FUNCTION_NAMES = new String[] { NOW, OFFSET, TODAY, START_OF_DAY, START_OF_WEEK, START_OF_MONTH, START_OF_YEAR };

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

		return convertToDesiredType(
				calculateDate( functionName, arguments, argumentConverter ), desiredType.getObjectType()
		);
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
				return WithPeriod.of( this::now ).call( arguments, argumentConverter, 0 );
			case OFFSET:
				return WithTemporalAndPeriod.of( this::offset ).call( arguments, argumentConverter );
			case START_OF_DAY:
				return WithTemporalAndPeriod.of( this::startOfDay ).call( arguments, argumentConverter );
			case START_OF_WEEK:
				DayOfWeek firstDayOfWeek = validateAndGetFirstDayOfTheWeekParameter( arguments, 2 );
				return today.with( firstDayOfWeek ).atStartOfDay();
			case START_OF_MONTH:
				return WithTemporalAndPeriod.of( this::startOfMonth ).call( arguments, argumentConverter );
			case START_OF_YEAR:
				return WithTemporalAndPeriod.of( this::startOfYear ).call( arguments, argumentConverter );

			case TODAY:
				return WithPeriod.of( this::today ).call( arguments, argumentConverter,0 );

		}

		return LocalDateTime.now();
	}

	LocalDateTime now( DurationWithPeriod period ) {
		return addDurationWithPeriodTooDateTime( LocalDateTime.now(), period );
	}

	LocalDateTime offset( LocalDateTime temporal, DurationWithPeriod period ) {
		return addDurationWithPeriodTooDateTime( temporal, period ).toLocalDate().atStartOfDay();
	}

	LocalDateTime today( DurationWithPeriod period ) {
		return addDurationWithPeriodTooDateTime( LocalDateTime.now().toLocalDate().atStartOfDay(), period );
	}

	LocalDateTime startOfDay( LocalDateTime temporal, DurationWithPeriod period ) {
		return addDurationWithPeriodTooDateTime( temporal, period ).toLocalDate().atStartOfDay();
	}

	LocalDateTime startOfMonth( LocalDateTime temporal, DurationWithPeriod period ) {
		return addDurationWithPeriodTooDateTime( temporal, period ).withDayOfMonth( 1 ).toLocalDate().atStartOfDay();
	}

	LocalDateTime startOfYear( LocalDateTime temporal, DurationWithPeriod period ) {
		return addDurationWithPeriodTooDateTime( temporal, period ).withDayOfYear( 1 ).toLocalDate().atStartOfDay();
	}

	@FunctionalInterface
	interface WithPeriod
	{
		default LocalDateTime call( EQType[] arguments, EQTypeConverter argumentConverter, Integer argumentIndex ) {
			return execute(
					validateAndGetPeriodWithDurationFromArguments( arguments, argumentConverter, argumentIndex )
			);
		}

		static WithPeriod of( WithPeriod wp ) {
			return wp;
		}

		LocalDateTime execute( DurationWithPeriod period );
	}

	@FunctionalInterface
	interface WithTemporalAndPeriod
	{
		default LocalDateTime call( EQType[] arguments, EQTypeConverter argumentConverter ) {
			LocalDateTime localDateTimeFromEQFunction = validateAndGetTemporalFromArguments( arguments, argumentConverter );
			DurationWithPeriod durationWithPeriod = validateAndGetPeriodWithDurationFromArguments( arguments, argumentConverter, 1 );

			return execute(
					localDateTimeFromEQFunction,
					durationWithPeriod
			);
		}

		static WithTemporalAndPeriod of( WithTemporalAndPeriod withTemporalAndPeriod ) {
			return withTemporalAndPeriod;
		}

		LocalDateTime execute( LocalDateTime temporal, DurationWithPeriod period );
	}

	/**
	 * Get the argument according to the argumentIndex provided and convert it into a {@link DurationWithPeriod}
	 * If no period argument is specified return {@link DurationWithPeriod} with no period nor duration
	 *
	 * @param arguments     to parse trough
	 * @param argumentIndex idicating the index position where the period argument resides in the arguments array
	 * @return a {@link DurationWithPeriod}
	 */
	private static DurationWithPeriod validateAndGetPeriodWithDurationFromArguments( EQType[] arguments,
	                                                                                 EQTypeConverter argumentConverter,
	                                                                                 Integer argumentIndex ) {
		if ( arguments.length <= argumentIndex ) {
			return DurationWithPeriod.builder().period( Period.ZERO ).duration( Duration.ZERO ).build();
		}

		return (DurationWithPeriod) argumentConverter.convert( TypeDescriptor.valueOf( DurationWithPeriod.class ), arguments[argumentIndex] );
	}

	/**
	 * Get the argument according to the argumentIndex provided and convert it into a {@link DurationWithPeriod}
	 * If no period argument is specified return {@link DurationWithPeriod} with no period nor duration
	 *
	 * @param arguments to parse trough
	 * @return a {@link DurationWithPeriod}
	 */
	private static LocalDateTime validateAndGetTemporalFromArguments( EQType[] arguments,
	                                                                  EQTypeConverter argumentConverter
	) {
		if ( arguments.length <= 0 ) {
			return LocalDateTime.now();
		}
		if ( !EQFunction.class.isAssignableFrom( arguments[0].getClass() ) ) {
			throw new IllegalArgumentException( "Invalid temporal argument specified: '" + arguments[0].toString() + "'." );
		}
		EQFunction temporal = (EQFunction) arguments[0];
		return (LocalDateTime) argumentConverter.convert( TypeDescriptor.valueOf( LocalDateTime.class ), temporal );
	}

	private static DayOfWeek validateAndGetFirstDayOfTheWeekParameter( EQType[] arguments, int argumentIndex ) {
		if ( arguments.length <= argumentIndex ) {
			return WeekFields.of( Locale.getDefault() ).getFirstDayOfWeek();
		}

		if ( !EQString.class.isAssignableFrom( arguments[argumentIndex].getClass() ) ) {
			throw new IllegalArgumentException( "Invalid first day of week argument specified:  '" + arguments[argumentIndex].toString() + "'." );
		}

		EQString dayOfWeek = (EQString) arguments[argumentIndex];

		// todo: implement
		return DayOfWeek.MONDAY;
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



