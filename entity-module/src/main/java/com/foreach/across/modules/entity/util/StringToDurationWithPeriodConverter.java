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

package com.foreach.across.modules.entity.util;

import com.foreach.across.modules.entity.query.support.DurationWithPeriod;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.time.Duration;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Helper to convert text to {@link Duration} objects and vice-versa
 *
 * @author Stijn Vanhoof
 * @since 3.1.1
 */
@Slf4j
public class StringToDurationWithPeriodConverter
{
	private static final int YEAR_INDEX = 2;
	private static final int MONTH_INDEX = 5;
	private static final int WEEK_INDEX = 8;
	private static final int DAYS_INDEX = 11;
	private static final int HOURS_INDEX = 14;
	private static final int MINUTES_INDEX = 17;
	private static final int SECONDS_INDEX = 20;

	private static final String DURATION_PATTERN =
			"(([-|+]?[\\d]*+)([y|Y][a-z]*+))?(([-|+]?[\\d]*+)([M][a-z]*+))?(([-|+]?[\\d]*+)([w|w][a-z]*+))?(([-|+]?[\\d]*+)([D|d][a-z]*+))?(([-|+]?[\\d]*+)([H|h][a-z]*+))?(([-|+]?[\\d]*+)([m][a-z]*+))?(([-|+]?[\\d]*+)([s|S][a-z]*+))?";

	/**
	 * Converts a given text value to a representing duration and period.
	 * The formatted string has to adhere to the following format:
	 * <ul>
	 * <li> seconds are represented by an integer followed by (one or more) characters starting with 's', 'S'</li>
	 * <li> minutes are represented by an integer followed by (one or more) characters starting with 'm'</li>
	 * <li> hours are represented by an integer or decimal followed by (one or more) characters starting with 'h', 'u'</li>
	 * <li> days are represented by an integer or decimal followed by (one or more) characters starting with 'd', 'D'</li>
	 * <li> weeks are represented by an integer followed by (one or more) characters starting with 'w', 'W'</li>
	 * <li> months are represented by an integer followed by (one or more) characters starting with 'M'</li>
	 * <li> years are represented by an integer followed by (one or more) characters starting with 'y', 'Y'</li>
	 * </ul>
	 *
	 * @param durationText text to convert
	 * @return the actual duration
	 * @throws IllegalArgumentException if the time is not of a valid pattern.
	 */
	public static DurationWithPeriod of( String durationText ) {
		Period period = Period.ZERO;
		Duration duration = Duration.ZERO;
		DurationWithPeriod durationWithPeriod = new DurationWithPeriod( duration, period );

		String withoutSpaces = durationText.replaceAll( " ", "" ).replaceAll( ",", "." );
		Pattern pattern = Pattern.compile( DURATION_PATTERN );
		Matcher m = pattern.matcher( withoutSpaces );

		if ( m.matches() ) {
			Stream.of(
					calculateDuration( m.group( SECONDS_INDEX ), ChronoUnit.SECONDS ),
					calculateDuration( m.group( MINUTES_INDEX ), ChronoUnit.MINUTES ),
					calculateDuration( m.group( HOURS_INDEX ), ChronoUnit.HOURS ),
					calculatePeriod( m.group( DAYS_INDEX ), ChronoUnit.DAYS ),
					calculatePeriod( m.group( WEEK_INDEX ), ChronoUnit.WEEKS ),
					calculatePeriod( m.group( MONTH_INDEX ), ChronoUnit.MONTHS ),
					calculatePeriod( m.group( YEAR_INDEX ), ChronoUnit.YEARS )
			).forEach( ( temporalAmount -> {
				if ( temporalAmount instanceof Period ) {
					durationWithPeriod.setPeriod( durationWithPeriod.getPeriod().plus( (Period) temporalAmount ) );
				}
				else if ( temporalAmount instanceof Duration ) {
					durationWithPeriod.setDuration( durationWithPeriod.getDuration().plus( (Duration) temporalAmount ) );
				}
			} ) );

			return durationWithPeriod;
		}

		throw new IllegalArgumentException( "'" + durationText + "' is not a valid format." );
	}

	private static Period calculatePeriod( String number, ChronoUnit unit ) {
		if ( StringUtils.isNotBlank( number ) ) {
			int valueToUse = Integer.parseInt( number );
			switch ( unit ) {
				case YEARS:
					return Period.ofYears( valueToUse ); // What about 1.5 years
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

	private static Duration calculateDuration( String number, ChronoUnit unit ) {
		if ( StringUtils.isNotBlank( number ) ) {
			long valueToUse = Long.parseLong( number );
			switch ( unit ) {
				case HOURS:
					return Duration.ofHours( valueToUse );
				case MINUTES:
					return Duration.ofMinutes( valueToUse );
				case SECONDS:
					return Duration.ofSeconds( valueToUse );
				default:
					break;
			}
		}

		return Duration.ZERO;
	}
}