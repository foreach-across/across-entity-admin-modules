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

import lombok.*;
import org.apache.commons.lang3.StringUtils;

import java.time.Duration;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;


/**
 * A helper class that holds a {@link Period} and {@link Duration}.
 * The {@link DurationWithPeriod#from(String)} method can be used to convert a {@link String} to a {@link DurationWithPeriod}
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DurationWithPeriod
{
	private Duration duration;
	private Period period;

	/**
	 * The indexes of the different regex capture groups
	 */
	private static final int YEAR_INDEX = 2;
	private static final int MONTH_INDEX = 5;
	private static final int WEEK_INDEX = 8;
	private static final int DAYS_INDEX = 11;
	private static final int HOURS_INDEX = 14;
	private static final int MINUTES_INDEX = 17;
	private static final int SECONDS_INDEX = 20;
	private static final int MILLISECONDS_INDEX = 23;
	private static final int TIMESTAMP_HOURS_INDEX = 27;
	private static final int TIMESTAMP_MINUTES_INDEX = 28;
	private static final int TIMESTAMP_SECONDS_INDEX = 30;
	private static final int TIMESTAMP_MILLISECONDS_INDEX = 32;

	private static final String signRegexPattern = "(?=[-+])";

	private static final String DURATION_REGEX_PATTERN =
			"(([\\d]+?)([y|Y][a-z]*+))?(([\\d]+?)([M][a-z]*+))?" +
					"(([\\d]+?)([W|w][a-z]*+))?(([\\d]+?)([D|d][a-z]*+))?(([\\d]+?)([H|h][a-z]*+))?" +
					"(([\\d]+?)([m][a-z]*+))?(([\\d]+?)([s][a-z]*+))?(([\\d]+?)([S][a-z]*+))?((at)([0-9]{1,2}):([0-9]{1,2})(:?([0-9]{0,2}))(.?([0-9]{0,3}))+?)?";

	/**
	 * Converts a given text value to a representing duration and period.
	 * The formatted string has to adhere to the following format:
	 * <ul>
	 * <li> milliseconds are represented by an integer followed by (one or more) characters starting with 'S'</li>
	 * <li> seconds are represented by an integer followed by (one or more) characters starting with 's'</li>
	 * <li> minutes are represented by an integer followed by (one or more) characters starting with 'm'</li>
	 * <li> hours are represented by an integer or decimal followed by (one or more) characters starting with 'h', 'u'</li>
	 * <li> days are represented by an integer or decimal followed by (one or more) characters starting with 'd', 'D'</li>
	 * <li> weeks are represented by an integer followed by (one or more) characters starting with 'w', 'W'</li>
	 * <li> months are represented by an integer followed by (one or more) characters starting with 'M'</li>
	 * <li> years are represented by an integer followed by (one or more) characters starting with 'y', 'Y'</li>
	 * <li> timestamps are represented in the following format: at 00:00:00.000 </li>
	 * </ul>
	 *
	 * @param period to convert
	 * @return the actual duration
	 * @throws IllegalArgumentException if the time is not of a valid pattern.
	 */
	public static DurationWithPeriod from( String period ) {
		DurationWithPeriod durationWithPeriod = new DurationWithPeriod( Duration.ZERO, Period.ZERO );
		Pattern pattern = Pattern.compile( DURATION_REGEX_PATTERN );

		String durationFromUser = cleanupAndValidateUserInput( period );

		// Split in groups for each sign
		Arrays.stream( durationFromUser.split( signRegexPattern ) ).forEach( periodPart -> {
			Character sign = periodPart.charAt( 0 );
			String periodExpression = periodPart.substring( 1 );

			Matcher regexMatcher = pattern.matcher( periodExpression );

			if ( regexMatcher.matches() ) {
				Stream.of(
						calculateDuration( regexMatcher.group( MILLISECONDS_INDEX ), ChronoUnit.MILLIS, sign ),
						calculateDuration( regexMatcher.group( TIMESTAMP_MILLISECONDS_INDEX ), ChronoUnit.MILLIS, sign ),
						calculateDuration( regexMatcher.group( SECONDS_INDEX ), ChronoUnit.SECONDS, sign ),
						calculateDuration( regexMatcher.group( TIMESTAMP_SECONDS_INDEX ), ChronoUnit.SECONDS, sign ),
						calculateDuration( regexMatcher.group( MINUTES_INDEX ), ChronoUnit.MINUTES, sign ),
						calculateDuration( regexMatcher.group( TIMESTAMP_MINUTES_INDEX ), ChronoUnit.MINUTES, sign ),
						calculateDuration( regexMatcher.group( HOURS_INDEX ), ChronoUnit.HOURS, sign ),
						calculateDuration( regexMatcher.group( TIMESTAMP_HOURS_INDEX ), ChronoUnit.HOURS, sign ),
						calculatePeriod( regexMatcher.group( DAYS_INDEX ), ChronoUnit.DAYS, sign ),
						calculatePeriod( regexMatcher.group( WEEK_INDEX ), ChronoUnit.WEEKS, sign ),
						calculatePeriod( regexMatcher.group( MONTH_INDEX ), ChronoUnit.MONTHS, sign ),
						calculatePeriod( regexMatcher.group( YEAR_INDEX ), ChronoUnit.YEARS, sign )
				).forEach( ( temporalAmount -> {
					// Add the period to the existing period
					if ( temporalAmount instanceof Period && !( (Period) temporalAmount ).isZero() ) {
						durationWithPeriod.setPeriod( durationWithPeriod.getPeriod().plus( (Period) temporalAmount ) );
					}

					// Add the duration to the existing period
					else if ( temporalAmount instanceof Duration && !( (Duration) temporalAmount ).isZero() ) {
						durationWithPeriod.setDuration( durationWithPeriod.getDuration().plus( (Duration) temporalAmount ) );
					}
				} ) );
			}
			else {
				throw new IllegalArgumentException( "'" + period + "' is not a valid format." );
			}
		} );

		return durationWithPeriod;
	}

	private static String cleanupAndValidateUserInput( String period ) {
		String durationFromUser = period.replaceAll( ",", "." )
			                                .replaceAll( " at ", "+at" )
			                                .replaceAll( "at ", "+at" )
	                                        .replaceAll( " ", "" );

		// The default sign is +
		if ( !durationFromUser.startsWith( "-" ) && !durationFromUser.startsWith( "+" ) ) {
			throw new IllegalArgumentException( "'" + period + "' is not a valid format. Must start with a valid period operator (-, + or at)." );
		}

		return durationFromUser;
	}

	private static Period calculatePeriod( String number, ChronoUnit unit, Character sign ) {
		if ( StringUtils.isNotBlank( number ) ) {
			int valueToUse = sign.equals( '-' ) ? -Integer.parseInt( number ) : Integer.parseInt( number );
			switch ( unit ) {
				case YEARS:
					return Period.ofYears( valueToUse );
				case MONTHS:
					return Period.ofMonths( valueToUse );
				case WEEKS:
					return Period.ofWeeks( valueToUse );
				case DAYS:
					return Period.ofDays( valueToUse );
				default:
					break;
			}
		}

		return Period.ZERO;
	}

	private static Duration calculateDuration( String number, ChronoUnit unit, Character sign ) {
		if ( StringUtils.isNotBlank( number ) ) {
			long valueToUse = sign.equals( '-' ) ? -Long.parseLong( number ) : Long.parseLong( number );
			switch ( unit ) {
				case HOURS:
					return Duration.ofHours( valueToUse );
				case MINUTES:
					return Duration.ofMinutes( valueToUse );
				case SECONDS:
					return Duration.ofSeconds( valueToUse );
				case MILLIS:
					return Duration.ofMillis( valueToUse );
				default:
					break;
			}
		}

		return Duration.ZERO;
	}
}
