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
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.Date;
import java.util.Locale;

/**
 * Provides date related functions to be used in entity queries.
 * <ul>
 * <li>now(): returns the current time</li>
 * <li>offset(TEMPORAL, PERIOD): relative time</li>
 * <li>startOfDay(TEMPORAL): midnight of the date of the temporal</li>
 * <li>startOfWeek(TEMPORAL, FIRST_DAY_OF_WEEK): midnight on first day of week of the temporal, with first day of week being optional third parameter</li>
 * <li>startOfMonth(TEMPORAL): midnight on first day of month of the temporal</li>
 * <li>startOfYear(TEMPORAL): jan 1 at midnight of the year of the temporal</li>
 * <li>today(): today at midnight</li>
 * <li>thisYear(): jan 1 at midnight of the current year</li>
 * <li>nextYear(): jan 1 at midnight of the next year</li>
 * <li>lastYear(): jan 1 at midnight of the previous yea</li>
 * <li>thisMonth(): midnight on the first day of this the current month</li>
 * <li>nextMonth(): midnight on the first day of the next month</li>
 * <li>lastMonth(): midnight on the first day of the previous month</li>
 * <li>thisWeek(FIRST_DAY_OF_WEEK): midnight on the first day of the current week, with the first day of the week being an optional third parameter</li>
 * <li>nextWeek(FIRST_DAY_OF_WEEK): midnight on the first day of the next week, with the first day of the week being an optional third parameter</li>
 * <li>lastWeek(FIRST_DAY_OF_WEEK): midnight on the first day of the previous week, with the first day of the week being an optional third parameter</li>
 * </ul>
 * Supported property types are {@link Date}, {@link Long}, {@link LocalDateTime}, {@link LocalDate}, {@link LocalTime} and {@link ZonedDateTime}, .
 *
 * @author Arne Vandamme
 * @since 2.0.0
 */
public class EntityQueryDateFunctions implements EntityQueryFunctionHandler
{
	protected static final String NOW = "now";
	protected static final String OFFSET = "offset";
	protected static final String START_OF_DAY = "startOfDay";
	protected static final String START_OF_WEEK = "startOfWeek";
	protected static final String START_OF_MONTH = "startOfMonth";
	protected static final String START_OF_YEAR = "startOfYear";

	protected static final String TODAY = "today";
	protected static final String THIS_YEAR = "thisYear";
	protected static final String NEXT_YEAR = "nextYear";
	protected static final String LAST_YEAR = "lastYear";
	protected static final String THIS_MONTH = "thisMonth";
	protected static final String NEXT_MONTH = "nextMonth";
	protected static final String LAST_MONTH = "lastMonth";
	protected static final String THIS_WEEK = "thisWeek";
	protected static final String NEXT_WEEK = "nextWeek";
	protected static final String LAST_WEEK = "lastWeek";

	protected static final String[] FUNCTION_NAMES =
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
	protected LocalDateTime calculateDate( String functionName, EQType[] arguments, EQTypeConverter argumentConverter ) {
		switch ( functionName ) {
			case NOW:
				return now();
			case TODAY:
				return today();
			case START_OF_DAY:
				return WithTemporal.of( this::startOfDay ).call( arguments, argumentConverter );
			case START_OF_MONTH:
				return WithTemporal.of( this::startOfMonth ).call( arguments, argumentConverter );
			case START_OF_YEAR:
				return WithTemporal.of( this::startOfYear ).call( arguments, argumentConverter );
			case THIS_YEAR:
				return thisYear();
			case NEXT_YEAR:
				return nextYear();
			case LAST_YEAR:
				return lastYear();
			case THIS_MONTH:
				return thisMonth();
			case NEXT_MONTH:
				return nextMonth();
			case LAST_MONTH:
				return lastMonth();
			case OFFSET:
				return WithTemporalAndPeriod.of( this::offset ).call( arguments, argumentConverter );
			case START_OF_WEEK:
				return WithTemporalAndFirstDayOfWeek.of( this::startOfWeek ).call( arguments, argumentConverter );
			case THIS_WEEK:
				return WithFirstDayOfWeek.of( this::thisWeek ).call( arguments, argumentConverter );
			case NEXT_WEEK:
				return WithFirstDayOfWeek.of( this::nextWeek ).call( arguments, argumentConverter );
			case LAST_WEEK:
				return WithFirstDayOfWeek.of( this::lastWeek ).call( arguments, argumentConverter );
		}

		return LocalDateTime.now();
	}

	private LocalDateTime offset( LocalDateTime temporal, DurationWithPeriod durationWithPeriod, boolean resetTimeOfTemporal ) {
		if ( resetTimeOfTemporal ) {
			temporal = temporal.with( LocalTime.MIDNIGHT );
		}

		temporal = temporal.plus( durationWithPeriod.getPeriod() );
		temporal = temporal.plus( durationWithPeriod.getDuration() );

		return temporal;
	}

	private LocalDateTime now() {
		return LocalDateTime.now();
	}

	private LocalDateTime today() {
		return LocalDateTime.now().toLocalDate().atStartOfDay();
	}

	private LocalDateTime startOfDay( LocalDateTime temporal ) {
		return temporal.toLocalDate().atStartOfDay();
	}

	private LocalDateTime thisYear() {
		return LocalDateTime.now().withDayOfYear( 1 ).toLocalDate().atStartOfDay();
	}

	private LocalDateTime nextYear() {
		return LocalDateTime.now().plusYears( 1 ).withDayOfYear( 1 ).toLocalDate().atStartOfDay();
	}

	private LocalDateTime lastYear() {
		return LocalDateTime.now().plusYears( -1 ).withDayOfYear( 1 ).toLocalDate().atStartOfDay();
	}

	private LocalDateTime thisMonth() {
		return LocalDateTime.now().withDayOfMonth( 1 ).toLocalDate().atStartOfDay();
	}

	private LocalDateTime nextMonth() {
		return LocalDateTime.now().plusMonths( 1 ).withDayOfMonth( 1 ).toLocalDate().atStartOfDay();
	}

	private LocalDateTime lastMonth() {
		return LocalDateTime.now().plusMonths( -1 ).withDayOfMonth( 1 ).toLocalDate().atStartOfDay();
	}

	private LocalDateTime startOfMonth( LocalDateTime temporal ) {
		return temporal.withDayOfMonth( 1 ).toLocalDate().atStartOfDay();
	}

	private LocalDateTime startOfYear( LocalDateTime temporal ) {
		return temporal.withDayOfYear( 1 ).toLocalDate().atStartOfDay();
	}

	private LocalDateTime startOfWeek( LocalDateTime temporal, DayOfWeek firstDayOfWeek ) {
		return temporal.with( TemporalAdjusters.previousOrSame( firstDayOfWeek ) ).toLocalDate().atStartOfDay();
	}

	private LocalDateTime thisWeek( DayOfWeek firstDayOfWeek ) {
		return LocalDateTime.now().with( TemporalAdjusters.previousOrSame( firstDayOfWeek ) ).toLocalDate().atStartOfDay();
	}

	private LocalDateTime nextWeek( DayOfWeek firstDayOfWeek ) {
		return LocalDateTime.now().plusWeeks( 1 ).with( firstDayOfWeek ).toLocalDate().atStartOfDay();
	}

	private LocalDateTime lastWeek( DayOfWeek firstDayOfWeek ) {
		return LocalDateTime.now().plusWeeks( -1 ).with( firstDayOfWeek ).toLocalDate().atStartOfDay();
	}

	@FunctionalInterface
	interface WithTemporal
	{
		default LocalDateTime call( EQType[] arguments, EQTypeConverter argumentConverter ) {
			if ( arguments.length > 1 ) {
				throw new IllegalArgumentException(
						"Invalid amount of argument specified. Expected one or zero arguments but got '" + arguments.length + "' arguments." );
			}

			LocalDateTime localDateTimeFromEQFunction = validateAndGetTemporalFromArguments( arguments, argumentConverter );

			return execute(
					localDateTimeFromEQFunction
			);
		}

		static WithTemporal of( WithTemporal withTemporal ) {
			return withTemporal;
		}

		LocalDateTime execute( LocalDateTime temporal );
	}

	@FunctionalInterface
	interface WithFirstDayOfWeek
	{
		default LocalDateTime call( EQType[] arguments, EQTypeConverter argumentConverter ) {
			DayOfWeek firstDayOfWeek = validateAndGetFirstDayOfWeekFromArguments( arguments, 0 );

			return execute(
					firstDayOfWeek
			);
		}

		static WithFirstDayOfWeek of( WithFirstDayOfWeek withFirstDayOfWeek ) {
			return withFirstDayOfWeek;
		}

		LocalDateTime execute( DayOfWeek firstDayOfWeek );
	}

	@FunctionalInterface
	interface WithTemporalAndFirstDayOfWeek
	{
		default LocalDateTime call( EQType[] arguments, EQTypeConverter argumentConverter ) {
			LocalDateTime localDateTimeFromEQFunction = validateAndGetTemporalFromArguments( arguments, argumentConverter );
			DayOfWeek firstDayOfWeek = validateAndGetFirstDayOfWeekFromArguments( arguments, 1 );

			return execute(
					localDateTimeFromEQFunction,
					firstDayOfWeek
			);
		}

		static WithTemporalAndFirstDayOfWeek of( WithTemporalAndFirstDayOfWeek withTemporalAndFirstDayOfWeek ) {
			return withTemporalAndFirstDayOfWeek;
		}

		LocalDateTime execute( LocalDateTime temporal, DayOfWeek firstDayOfWeek );
	}

	@FunctionalInterface
	interface WithTemporalAndPeriod
	{
		default LocalDateTime call( EQType[] arguments, EQTypeConverter argumentConverter ) {
			if ( arguments.length != 2 ) {
				throw new IllegalArgumentException(
						"Invalid amount of argument specified. Expected a total number of 2 arguments but got '" + arguments.length + "' arguments." );
			}

			boolean resetTimeOfTemporal = false;
			LocalDateTime localDateTimeFromEQFunction = validateAndGetTemporalFromArguments( arguments, argumentConverter );
			DurationWithPeriod durationWithPeriod = validateAndGetPeriodWithDurationFromArguments( arguments, argumentConverter );

			resetTimeOfTemporal = isASpecificTimestampSpecified( arguments, argumentConverter );

			return execute(
					localDateTimeFromEQFunction,
					durationWithPeriod,
					resetTimeOfTemporal
			);
		}

		static WithTemporalAndPeriod of( WithTemporalAndPeriod withTemporalAndPeriod ) {
			return withTemporalAndPeriod;
		}

		LocalDateTime execute( LocalDateTime temporal, DurationWithPeriod period, boolean resetTimeOfTemporal );
	}

	private static boolean isASpecificTimestampSpecified( EQType[] arguments,
	                                                      EQTypeConverter argumentConverter ) {
		String period = (String) argumentConverter.convert( TypeDescriptor.valueOf( String.class ), arguments[1] );

		return period.toLowerCase().trim().startsWith( "at" );
	}

	/**
	 * Get the argument according to the argumentIndex provided and convert it into a {@link DurationWithPeriod}
	 * If no period argument is specified return {@link DurationWithPeriod} with no period nor duration
	 *
	 * @param arguments to parse trough
	 * @return a {@link DurationWithPeriod}
	 */
	private static DurationWithPeriod validateAndGetPeriodWithDurationFromArguments( EQType[] arguments,
	                                                                                 EQTypeConverter argumentConverter ) {
		return (DurationWithPeriod) argumentConverter.convert( TypeDescriptor.valueOf( DurationWithPeriod.class ), arguments[1] );
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
	 * @param arguments     to parse trough
	 * @param argumentIndex indicates the argument index for the first day of the week
	 * @return a {@link DurationWithPeriod}
	 */
	private static DayOfWeek validateAndGetFirstDayOfWeekFromArguments( EQType[] arguments, Integer argumentIndex ) {
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

		if ( LocalTime.class.equals( desiredType ) ) {
			return dateTime.atZone( ZoneId.systemDefault() ).toLocalTime();
		}

		if ( LocalDate.class.equals( desiredType ) ) {
			return dateTime.atZone( ZoneId.systemDefault() ).toLocalDate();
		}

		if ( LocalDateTime.class.equals( desiredType ) ) {
			return dateTime.atZone( ZoneId.systemDefault() ).toLocalDateTime();
		}

		if ( ZonedDateTime.class.equals( desiredType ) ) {
			return dateTime.atZone( ZoneId.systemDefault() );
		}

		return dateTime;
	}
}



