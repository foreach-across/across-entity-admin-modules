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
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.convert.TypeDescriptor;

import java.time.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
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
	private static final String NOW = "now";
	private static final String OFFSET = "offset";
	private static final String START_OF_DAY = "startOfDay";
	private static final String START_OF_WEEK = "startOfWeek";
	private static final String START_OF_MONTH = "startOfMonth";
	private static final String START_OF_YEAR = "startOfYear";

	private static final String TODAY = "today";
	private static final String THIS_YEAR = "thisYear";
	private static final String NEXT_YEAR = "nextYear";
	private static final String LAST_YEAR = "lastYear";
	private static final String THIS_MONTH = "thisMonth";
	private static final String NEXT_MONTH = "nextMonth";
	private static final String LAST_MONTH = "lastMonth";
	private static final String THIS_WEEK = "thisWeek";
	private static final String NEXT_WEEK = "nextWeek";
	private static final String LAST_WEEK = "lastWeek";

	private static final String[] FUNCTION_NAMES =
			new String[] { NOW, OFFSET, TODAY, START_OF_DAY, START_OF_WEEK, START_OF_MONTH, START_OF_YEAR, THIS_YEAR, THIS_MONTH, THIS_WEEK, NEXT_MONTH,
			               NEXT_WEEK, NEXT_YEAR, LAST_MONTH, LAST_WEEK, LAST_YEAR };

	@Override
	public boolean accepts( String functionName, TypeDescriptor desiredType ) {
		return ArrayUtils.contains( FUNCTION_NAMES, functionName ) && (
				Date.class.equals( desiredType.getObjectType() )
						|| LocalDate.class.equals( desiredType.getObjectType() )
						|| LocalTime.class.equals( desiredType.getObjectType() )
						|| LocalDateTime.class.equals( desiredType.getObjectType() )
						|| ZonedDateTime.class.equals( desiredType.getObjectType() )
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
			case START_OF_DAY:
				return WithTemporalAndPeriod.of( this::startOfDay ).call( arguments, argumentConverter );

			case START_OF_MONTH:
				return WithTemporalAndPeriod.of( this::startOfMonth ).call( arguments, argumentConverter );
			case START_OF_YEAR:
				return WithTemporalAndPeriod.of( this::startOfYear ).call( arguments, argumentConverter );
			case TODAY:
				return WithPeriod.of( this::today ).call( arguments, argumentConverter, 0 );
			case THIS_YEAR:
				return WithPeriod.of( this::thisYear ).call( arguments, argumentConverter, 0 );
			case NEXT_YEAR:
				return WithPeriod.of( this::nextYear ).call( arguments, argumentConverter, 0 );
			case LAST_YEAR:
				return WithPeriod.of( this::lastYear ).call( arguments, argumentConverter, 0 );
			case THIS_MONTH:
				return WithPeriod.of( this::thisMonth ).call( arguments, argumentConverter, 0 );
			case NEXT_MONTH:
				return WithPeriod.of( this::nextMonth ).call( arguments, argumentConverter, 0 );
			case LAST_MONTH:
				return WithPeriod.of( this::lastMonth ).call( arguments, argumentConverter, 0 );
			case OFFSET:
				return WithTemporalAndPeriod.of( this::offset ).call( arguments, argumentConverter );
			case START_OF_WEEK:
				return WithTemporalPeriodAndFirstDayOfWeek.of( this::startOfWeek ).call( arguments, argumentConverter );
			case THIS_WEEK:
				return WithPeriodAndFirstDayOfWeek.of( this::thisWeek ).call( arguments, argumentConverter );
			case NEXT_WEEK:
				return WithPeriodAndFirstDayOfWeek.of( this::nextWeek ).call( arguments, argumentConverter );
			case LAST_WEEK:
				return WithPeriodAndFirstDayOfWeek.of( this::lastWeek ).call( arguments, argumentConverter );
		}

		return LocalDateTime.now();
	}

	private LocalDateTime now( DurationWithPeriod period ) {
		return addDurationWithPeriodTooDateTime( LocalDateTime.now(), period );
	}

	private LocalDateTime offset( LocalDateTime temporal, DurationWithPeriod period ) {
		return addDurationWithPeriodTooDateTime( temporal, period );
	}

	private LocalDateTime today( DurationWithPeriod period ) {
		return addDurationWithPeriodTooDateTime( LocalDateTime.now().toLocalDate().atStartOfDay(), period );
	}

	private LocalDateTime thisYear( DurationWithPeriod period ) {
		return addDurationWithPeriodTooDateTime( LocalDateTime.now().withDayOfYear( 1 ).toLocalDate().atStartOfDay(), period );
	}

	private LocalDateTime nextYear( DurationWithPeriod period ) {
		return addDurationWithPeriodTooDateTime( LocalDateTime.now().plusYears( 1 ).withDayOfYear( 1 ).toLocalDate().atStartOfDay(), period );
	}

	private LocalDateTime lastYear( DurationWithPeriod period ) {
		return addDurationWithPeriodTooDateTime( LocalDateTime.now().plusYears( -1 ).withDayOfYear( 1 ).toLocalDate().atStartOfDay(), period );
	}

	private LocalDateTime thisMonth( DurationWithPeriod period ) {
		return addDurationWithPeriodTooDateTime( LocalDateTime.now().withDayOfMonth( 1 ).toLocalDate().atStartOfDay(), period );
	}

	private LocalDateTime nextMonth( DurationWithPeriod period ) {
		return addDurationWithPeriodTooDateTime( LocalDateTime.now().plusMonths( 1 ).withDayOfMonth( 1 ).toLocalDate().atStartOfDay(), period );
	}

	private LocalDateTime lastMonth( DurationWithPeriod period ) {
		return addDurationWithPeriodTooDateTime( LocalDateTime.now().plusMonths( -1 ).withDayOfMonth( 1 ).toLocalDate().atStartOfDay(), period );
	}

	private LocalDateTime startOfDay( LocalDateTime temporal, DurationWithPeriod period ) {
		return addDurationWithPeriodTooDateTime( temporal.toLocalDate().atStartOfDay(), period );
	}

	private LocalDateTime startOfMonth( LocalDateTime temporal, DurationWithPeriod period ) {
		return addDurationWithPeriodTooDateTime( temporal.withDayOfMonth( 1 ).toLocalDate().atStartOfDay(), period );
	}

	private LocalDateTime startOfYear( LocalDateTime temporal, DurationWithPeriod period ) {
		return addDurationWithPeriodTooDateTime( temporal.withDayOfYear( 1 ).toLocalDate().atStartOfDay(), period );
	}

	private LocalDateTime startOfWeek( LocalDateTime temporal, DurationWithPeriod period, DayOfWeek firstDayOfWeek ) {
		return addDurationWithPeriodTooDateTime( temporal.with( TemporalAdjusters.previousOrSame( firstDayOfWeek ) ).toLocalDate().atStartOfDay(), period );
	}

	private LocalDateTime thisWeek( DurationWithPeriod period, DayOfWeek firstDayOfWeek ) {
		return addDurationWithPeriodTooDateTime( LocalDateTime.now().with( TemporalAdjusters.previousOrSame(firstDayOfWeek )).toLocalDate().atStartOfDay(), period );
	}

	private LocalDateTime nextWeek( DurationWithPeriod period, DayOfWeek firstDayOfWeek ) {
		return addDurationWithPeriodTooDateTime( LocalDateTime.now().plusWeeks( 1 ).with( firstDayOfWeek ).toLocalDate().atStartOfDay(), period );
	}

	private LocalDateTime lastWeek( DurationWithPeriod period, DayOfWeek firstDayOfWeek ) {
		return addDurationWithPeriodTooDateTime( LocalDateTime.now().plusWeeks( -1 ).with( firstDayOfWeek ).toLocalDate().atStartOfDay(), period );
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

	@FunctionalInterface
	interface WithPeriodAndFirstDayOfWeek
	{
		default LocalDateTime call( EQType[] arguments, EQTypeConverter argumentConverter ) {
			DurationWithPeriod durationWithPeriod = validateAndGetPeriodWithDurationFromArguments( arguments, argumentConverter, 0 );
			DayOfWeek firstDayOfWeek = validateAndGetFirstDayOfWeekFromArguments( arguments, argumentConverter, 1 );

			return execute(
					durationWithPeriod,
					firstDayOfWeek
			);
		}

		static WithPeriodAndFirstDayOfWeek of( WithPeriodAndFirstDayOfWeek withPeriodAndFirstDayOfWeek ) {
			return withPeriodAndFirstDayOfWeek;
		}

		LocalDateTime execute( DurationWithPeriod period, DayOfWeek firstDayOfWeek );
	}

	@FunctionalInterface
	interface WithTemporalPeriodAndFirstDayOfWeek
	{
		default LocalDateTime call( EQType[] arguments, EQTypeConverter argumentConverter ) {
			LocalDateTime localDateTimeFromEQFunction = validateAndGetTemporalFromArguments( arguments, argumentConverter );
			DurationWithPeriod durationWithPeriod = validateAndGetPeriodWithDurationFromArguments( arguments, argumentConverter, 1 );
			DayOfWeek firstDayOfWeek = validateAndGetFirstDayOfWeekFromArguments( arguments, argumentConverter, 2 );

			return execute(
					localDateTimeFromEQFunction,
					durationWithPeriod,
					firstDayOfWeek
			);
		}

		static WithTemporalPeriodAndFirstDayOfWeek of( WithTemporalPeriodAndFirstDayOfWeek withTemporalPeriodAndFirstDayOfWeek ) {
			return withTemporalPeriodAndFirstDayOfWeek;
		}

		LocalDateTime execute( LocalDateTime temporal, DurationWithPeriod period, DayOfWeek firstDayOfWeek );
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
		if ( arguments.length > 0 && argumentIndex < arguments.length ) {
			return (DurationWithPeriod) argumentConverter.convert( TypeDescriptor.valueOf( DurationWithPeriod.class ), arguments[argumentIndex] );
		}

		return DurationWithPeriod.builder().period( Period.ZERO ).duration( Duration.ZERO ).build();
	}

	/**
	 * Convert the first argument into a {@link DurationWithPeriod}
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

		if ( EQFunction.class.isAssignableFrom( arguments[0].getClass() ) ) {
			EQFunction temporal = (EQFunction) arguments[0];
			return (LocalDateTime) argumentConverter.convert( TypeDescriptor.valueOf( LocalDateTime.class ), temporal );
		}
		else {
			if ( EQValue.class.isAssignableFrom( arguments[0].getClass() ) ) {
				try {
					LocalDate tryParseDate = LocalDate.parse( arguments[0].toString(), DateTimeFormatter.ISO_DATE );
					return tryParseDate.atStartOfDay();
				}
				catch ( Exception ex ) {
					throw new IllegalArgumentException( "Invalid temporal argument specified: '" + arguments[0].toString() + "'." );
				}
			}
			else {
				// No EQString allowed for a Temporal
				throw new IllegalArgumentException( "Invalid temporal argument specified: '" + arguments[0].toString() + "'." );
			}
		}
	}

	/**
	 * Convert the third argument into a {@link DayOfWeek}
	 * If no first day of week argument is specified, use the configured {@link Locale} to determine the first {@link DayOfWeek} that will be used.
	 *
	 * @param arguments to parse trough
	 * @return a {@link DurationWithPeriod}
	 */
	private static DayOfWeek validateAndGetFirstDayOfWeekFromArguments( EQType[] arguments, EQTypeConverter argumentConverter, Integer argumentIndex ) {
		DayOfWeek firstDayOfWeek;

		if ( arguments.length < argumentIndex + 1 ) {
			Locale locale = LocaleContextHolder.getLocale();
			firstDayOfWeek = WeekFields.of( locale ).getFirstDayOfWeek();
		}
		else {
			switch ( arguments[argumentIndex].toString().toLowerCase() ) {
				case "monday":
				case "mon":
					firstDayOfWeek = DayOfWeek.MONDAY;
					break;
				case "tue":
				case "tuesday":
					firstDayOfWeek = DayOfWeek.TUESDAY;
					break;
				case "wed":
				case "wednesday":
					firstDayOfWeek = DayOfWeek.WEDNESDAY;
					break;
				case "thu":
				case "thursday":
					firstDayOfWeek = DayOfWeek.THURSDAY;
					break;
				case "fri":
				case "friday":
					firstDayOfWeek = DayOfWeek.FRIDAY;
					break;
				case "sat":
				case "saturday":
					firstDayOfWeek = DayOfWeek.SATURDAY;
					break;
				case "sun":
				case "sunday":
					firstDayOfWeek = DayOfWeek.SUNDAY;
					break;
				default:
					firstDayOfWeek = DayOfWeek.MONDAY;
			}
		}

		return firstDayOfWeek;
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

		if(LocalTime.class.equals( desiredType )) {
			return date.toInstant().atZone( ZoneId.systemDefault() ).toLocalTime();
		}

		if(LocalDate.class.equals( desiredType )) {
			return date.toInstant().atZone( ZoneId.systemDefault() ).toLocalDate();
		}

		if ( LocalDateTime.class.equals( desiredType ) ) {
			return date.toInstant().atZone( ZoneId.systemDefault() ).toLocalDateTime();
		}

		if ( ZonedDateTime.class.equals( desiredType ) ) {
			return date.toInstant().atZone( ZoneId.systemDefault() );
		}

		return dateTime;
	}
}



