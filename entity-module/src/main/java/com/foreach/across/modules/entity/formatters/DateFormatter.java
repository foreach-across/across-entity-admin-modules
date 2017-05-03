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
package com.foreach.across.modules.entity.formatters;

import com.foreach.across.core.convert.StringToDateConverter;
import liquibase.exception.DateParseException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Stream;

import static org.apache.commons.lang3.time.FastDateFormat.getDateInstance;
import static org.apache.commons.lang3.time.FastDateFormat.getDateTimeInstance;

/**
 * @author Andy Somers
 */
public class DateFormatter extends org.springframework.format.datetime.DateFormatter
{
	private static List<FastDateFormat> dateFormats = new ArrayList<>();

	static {
		dateFormats.add( FastDateFormat.getInstance( "yyyy-MM-dd" ) );
		dateFormats.add( FastDateFormat.getInstance( "yyyy/MM/dd" ) );
		dateFormats.add( FastDateFormat.getInstance( "yyyy-MM-dd HH:mm" ) );
		dateFormats.add( FastDateFormat.getInstance( "yyyy/MM/dd HH:mm" ) );
		dateFormats.add( FastDateFormat.getInstance( "yyyy-MM-dd HH:mm:ss" ) );
		dateFormats.add( FastDateFormat.getInstance( "yyyy/MM/dd HH:mm:ss" ) );
		Stream.of( StringToDateConverter.defaultPatterns() )
		      .forEach( p -> dateFormats.add( FastDateFormat.getInstance( p ) ) );
		Collections.sort( dateFormats,
		                  Collections.reverseOrder( Comparator.comparingInt( f -> f.getPattern().length() ) ) );
	}

	@Override
	public Date parse( String text, Locale locale ) throws ParseException {
		if ( StringUtils.isNotBlank( text ) ) {
			List<FastDateFormat> availableDateFormats = new ArrayList<>();
			availableDateFormats.add( getDateTimeInstance( DateFormat.MEDIUM, DateFormat.MEDIUM, locale ) );
			availableDateFormats.add( getDateTimeInstance( DateFormat.MEDIUM, DateFormat.SHORT, locale ) );
			availableDateFormats.add( getDateTimeInstance( DateFormat.SHORT, DateFormat.MEDIUM, locale ) );
			availableDateFormats.add( getDateTimeInstance( DateFormat.SHORT, DateFormat.SHORT, locale ) );
			availableDateFormats.add( getDateInstance( DateFormat.MEDIUM, locale ) );
			availableDateFormats.add( getDateInstance( DateFormat.SHORT, locale ) );
			availableDateFormats.addAll( dateFormats );

			return parseDate( text, availableDateFormats );
		}
		return null;
	}

	@Override
	public String print( Date object, Locale locale ) {
		if ( object != null ) {
			FastDateFormat instance = getDateTimeInstance( DateFormat.SHORT, DateFormat.MEDIUM, locale );
			return instance.format( object );
		}
		return null;
	}

	private Date parseDate( String text, List<FastDateFormat> availableDateFormats ) throws DateParseException {
		for ( FastDateFormat format : availableDateFormats ) {
			try {
				return format.parse( text );
			}
			catch ( ParseException e ) {
				// do nothing
			}
		}
		throw new DateParseException( "Unable to parse date" );
	}
}
