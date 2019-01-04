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

package com.foreach.across.modules.entity.converters;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Helper to convert text to {@link Duration} objects and vice-versa
 *
 * @author Steven Gentens
 * @since 3.1.1
 */
@Slf4j
public class StringToDurationConverter
{
	private static final int DAYS_INDEX = 2;
	private static final int HOURS_INDEX = 5;
	private static final int MINUTES_INDEX = 8;

	public static final int DAYS_TO_HOURS = 24;
	private static final int HOURS_TO_MINUTES = 60;
	private static final String DURATION_PATTERN = "(([-]?[\\d|\\.]*+)([d|t|j][a-z]*+))?(([-]?[\\d|\\.]*+)([h|u|st|:][a-z]*+))?(([-]?[\\d]*+)([m][a-z]*+)?)";

	/**
	 * Converts a given amount of minutes to a more readable format.
	 *
	 * @param minutes to convert
	 * @return the duration specified in hours and minutes.
	 */
	public static String convertToTime( int minutes ) {
		return convertToTime( Duration.ofMinutes( minutes ) );
	}

	/**
	 * Converts a given duration to a more readable format.
	 *
	 * @param duration to convert
	 * @return the duration specifiedin hours and minutes.
	 */
	public static String convertToTime( Duration duration ) {
		long days = duration.toHours() / DAYS_TO_HOURS;
		Duration durationOfHoursAndMinutes = duration.minusHours( days * DAYS_TO_HOURS );
		long hours = durationOfHoursAndMinutes.toHours();
		long minutes = durationOfHoursAndMinutes.minusHours( hours ).toMinutes();
		StringBuilder result = new StringBuilder();
		if ( days > 0 ) {
			result.append( days )
			      .append( "d " );
		}
		if ( hours > 0 ) {
			result.append( hours )
			      .append( "h " );
		}
		if ( minutes > 0 ) {
			result.append( minutes )
			      .append( "m" );
		}
		return result.toString().trim();
	}

	/**
	 * Converts a given text value representinga duration.
	 * The formatted string has to adhere to the following case-insensitive format:
	 * * days are represented by an integer or decimal followed by (one or more) characters starting with 'd', 't' or 'j'
	 * * hours are represented by an integer or decimal followed by (one or more) characters starting with 'h', 'u' or 'st', ':'
	 * * minutes are represented by an integer optionally followed by (one or more) characters starting with 'm'
	 *
	 * @param duration text to convert
	 * @return the actual duration
	 * @throws IllegalArgumentException if the time is not of a valid pattern.
	 */
	public static Duration convertToDuration( String duration ) {
		String withoutSpaces = duration.replaceAll( " ", "" ).replaceAll( ",", "." );
		Pattern pattern = Pattern.compile( DURATION_PATTERN, Pattern.CASE_INSENSITIVE );
		Matcher m = pattern.matcher( withoutSpaces );
		if ( m.matches() ) {
			return Stream.of(
					convertToDuration( m.group( DAYS_INDEX ), ChronoUnit.DAYS ),
					convertToDuration( m.group( HOURS_INDEX ), ChronoUnit.HOURS ),
					convertToDuration( m.group( MINUTES_INDEX ), ChronoUnit.MINUTES )
			).reduce( Duration::plus ).get();

		}
		throw new IllegalArgumentException( "'" + duration + "' is not a valid format." );
	}

	private static Duration convertToDuration( String number, ChronoUnit unit ) {
		if ( StringUtils.isNotBlank( number ) ) {
			Double valueToUse = Double.parseDouble( number );
			switch ( unit ) {
				case DAYS:
					valueToUse *= DAYS_TO_HOURS;
				case HOURS:
					valueToUse *= HOURS_TO_MINUTES;
					break;
				case MINUTES:
					break;
				default:
					break;
			}
			BigDecimal asMinutes = BigDecimal.valueOf( valueToUse ).setScale( 0, RoundingMode.HALF_UP );
			return Duration.ofMinutes( asMinutes.longValue() );
		}
		return Duration.ZERO;
	}
}
