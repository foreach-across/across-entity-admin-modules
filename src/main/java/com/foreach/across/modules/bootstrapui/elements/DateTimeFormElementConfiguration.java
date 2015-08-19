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
package com.foreach.across.modules.bootstrapui.elements;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.time.FastDateFormat;
import org.springframework.util.Assert;

import java.text.DateFormat;
import java.util.*;

/**
 * Configuration class for a {@link DateTimeFormElement} based on
 * <a href="https://github.com/Eonasdan/bootstrap-datetimepicker">Eonasdan Bootstrap datepicker</a>.
 *
 * @author Arne Vandamme
 */
public class DateTimeFormElementConfiguration extends HashMap<String, Object>
{
	public static final String FMT_EXPORT_JAVA = "yyyy-MM-dd HH:mm";
	public static final String FMT_EXPORT_MOMENT_DATE = "YYYY-MM-DD";
	public static final String FMT_EXPORT_MOMENT_TIME = "HH:mm";
	public static final String FMT_EXPORT_MOMENT_DATETIME = "YYYY-MM-DD HH:mm";

	public static final String FMT_PATTERN_DATE = "L";
	public static final String FMT_PATTERN_DATE_FULL = "LL";
	public static final String FMT_PATTERN_TIME = "LT";
	public static final String FMT_PATTERN_DATETIME = "L LT";
	public static final String FMT_PATTERN_DATETIME_FULL = "LLL";

	public static final String FMT_EXTRA_PATTERN_DATE = "YYYY-MM-DD";

	public static final Locale DEFAULT_LOCALE = Locale.UK;

	public static final FastDateFormat JAVA_FORMATTER = FastDateFormat.getInstance( FMT_EXPORT_JAVA );

	@JsonIgnore
	private Format format;

	@JsonIgnore
	private Locale locale;

	@JsonIgnore
	private boolean localizePatterns = true;

	public DateTimeFormElementConfiguration() {
		setFormat( Format.DATETIME );
		setLocale( DEFAULT_LOCALE );
		put( "datepickerInput", "input[type=text]" );
	}

	public DateTimeFormElementConfiguration( Format format ) {
		this();
		setFormat( format );
	}

	/**
	 * Create a copy of an existing configuration.
	 *
	 * @param existing configuration to copy
	 */
	public DateTimeFormElementConfiguration( DateTimeFormElementConfiguration existing ) {
		format = existing.format;
		locale = existing.locale;
		localizePatterns = existing.localizePatterns;
		putAll( existing );
	}

	/**
	 * Get the format that this configuration is primarily meant for.  This can be used
	 * for hints on icons to show etc.
	 *
	 * @return format instance
	 */
	public Format getFormat() {
		return format;
	}

	/**
	 * This will automatically set the different attributes and patterns to be used.
	 * If you want to use custom patterns, you should manually set the attributes
	 * *after* a call to format.
	 *
	 * @param format to use
	 */
	public void setFormat( Format format ) {
		Assert.notNull( format );
		this.format = format;

		switch ( format ) {
			case DATE:
				setPattern( FMT_PATTERN_DATE );
				setExtraPatterns( FMT_EXPORT_MOMENT_DATETIME, FMT_EXTRA_PATTERN_DATE );
				setExportPattern( FMT_EXPORT_MOMENT_DATE );
				break;
			case DATE_FULL:
				setPattern( FMT_PATTERN_DATE_FULL );
				setExtraPatterns( FMT_EXPORT_MOMENT_DATETIME, FMT_PATTERN_DATE, FMT_EXTRA_PATTERN_DATE );
				setExportPattern( FMT_EXPORT_MOMENT_DATE );
				break;
			case TIME:
				setPattern( FMT_PATTERN_TIME );
				setExtraPatterns( FMT_EXPORT_MOMENT_DATETIME );
				setExportPattern( FMT_EXPORT_MOMENT_TIME );
				break;
			case DATETIME_FULL:
				setPattern( FMT_PATTERN_DATETIME_FULL );
				setExtraPatterns( FMT_EXPORT_MOMENT_DATETIME, FMT_PATTERN_DATE, FMT_PATTERN_DATETIME,
				                  FMT_EXTRA_PATTERN_DATE );
				setExportPattern( FMT_EXPORT_MOMENT_DATETIME );
				break;
			default:
				setPattern( FMT_PATTERN_DATETIME );
				setExtraPatterns( FMT_EXPORT_MOMENT_DATETIME, FMT_PATTERN_DATE, FMT_EXTRA_PATTERN_DATE );
				setExportPattern( FMT_EXPORT_MOMENT_DATETIME );
				break;
		}
	}

	/**
	 * Set the primary input/output pattern in moment js format.
	 * Only use this method if you manually want to change the patterns, usually
	 * {@link #setFormat(Format)} would suffice.
	 *
	 * @param pattern in moment js format
	 */
	public void setPattern( String pattern ) {
		put( "format", pattern );
	}

	/**
	 * Set extra input patterns that should be supported (in moment js format).
	 * Only use this method if you manually want to change the patterns, usually
	 * {@link #setFormat(Format)} would suffice.  Note that you should always
	 * add the {@link #setExportPattern(String)} to the list of extra patterns as well.
	 *
	 * @param patterns in moment js format
	 */
	public void setExtraPatterns( String... patterns ) {
		put( "extraFormats", patterns );
	}

	/**
	 * Set the serialization format that should be used.  The value posted back
	 * will be in this format.  Only use this method if you manually want to change the patterns,
	 * usually {@link #setFormat(Format)} would suffice.
	 *
	 * @param pattern in moment js format
	 */
	public void setExportPattern( String pattern ) {
		put( "exportFormat", pattern );
	}

	public void setLocale( Locale locale ) {
		Assert.notNull( locale );
		this.locale = locale;
		put( "locale", locale.toLanguageTag() );
	}

	/**
	 * Number of minutes the up/down arrow's will move the minutes value in the time picker
	 *
	 * @param minutes per step
	 */
	public void setStepping( int minutes ) {
		put( "stepping", minutes );
	}

	/**
	 * Prevents date/time selections before this date
	 */
	public void setMinDate( Date date ) {
		setDateAttribute( "minDate", date );
	}

	/**
	 * Prevents date/time selections after this date
	 */
	public void setMaxDate( Date date ) {
		setDateAttribute( "maxDate", date );
	}

	/**
	 * On show, will set the picker to the current date/time
	 */
	public void setUseCurrentDate( boolean useCurrent ) {
		put( "useCurrent", useCurrent );
	}

	/**
	 * Sets the picker default date/time. Overrides useCurrent
	 */
	public void setDefaultDate( Date date ) {
		setDateAttribute( "defaultDate", date );
	}

	/**
	 * This will change the viewDate without changing or setting the selected date.
	 */
	public void setViewDate( Date date ) {
		setDateAttribute( "viewDate", date );
	}

	/**
	 * Disables selection of dates in the array, e.g. holidays
	 */
	public void setDisabledDates( Date... dates ) {
		setDateAttribute( "disabledDates", dates );
	}

	/**
	 * Disables selection of dates NOT in the array, e.g. holidays
	 */
	public void setEnabledDates( Date... dates ) {
		setDateAttribute( "enabledDates", dates );
	}

	/**
	 * Shows the picker side by side when using the time and date together.
	 */
	public void setSideBySide( boolean sideBySide ) {
		put( "sideBySide", sideBySide );
	}

	/**
	 * Disables the section of days of the week, e.g. weekends.
	 */
	public void setDaysOfWeekDisabled( int... daysOfWeek ) {
		put( "daysOfWeekDisabled", daysOfWeek );
	}

	/**
	 * Shows the week of the year to the left of first day of the week.
	 */
	public void setShowCalendarWeeks( boolean showCalendarWeeks ) {
		put( "calendarWeeks", showCalendarWeeks );
	}

	/**
	 * Show the "Today" button in the icon toolbar.  Clicking the "Today" button will
	 * set the calendar view and set the date to now.
	 */
	public void setShowTodayButton( boolean showTodayButton ) {
		put( "showTodayButton", showTodayButton );
	}

	/**
	 * Show the "Clear" button in the icon toolbar.  Clicking the "Clear" button will set the calendar to null.
	 */
	public void setShowClearButton( boolean showClearButton ) {
		put( "showClear", showClearButton );
	}

	/**
	 * Show the "Close" button in the icon toolbar.
	 */
	public void setShowCloseButton( boolean showCloseButton ) {
		put( "showClose", showCloseButton );
	}

	/**
	 * Will cause the date picker to stay open after selecting a date if no time components are being used.
	 */
	public void setKeepOpen( boolean keepOpen ) {
		put( "keepOpen", keepOpen );
	}

	/**
	 * Will display the picker inline without the need of a input field.
	 */
	public void setShowInline( boolean showInline ) {
		put( "inline", showInline );
	}

	/**
	 * Will cause the date picker to not revert or overwrite invalid dates.
	 */
	public void setKeepInvalid( boolean keepInvalid ) {
		put( "keepInvalid", keepInvalid );
	}

	/**
	 * Set any date attribute on the configuration.  Dates will be converted to the generic export format.
	 */
	public void setDateAttribute( String attributeName, Date... dates ) {
		if ( dates == null || ( dates.length == 1 && dates[0] == null ) ) {
			remove( attributeName );
		}
		else {
			if ( dates.length == 1 ) {
				put( attributeName, JAVA_FORMATTER.format( dates[0] ) );
			}
			else {
				List<String> formatted = new ArrayList<>( dates.length );
				for ( Date date : dates ) {
					formatted.add( JAVA_FORMATTER.format( date ) );
				}
				put( attributeName, formatted.toArray() );
			}
		}
	}

	public boolean isLocalizePatterns() {
		return localizePatterns;
	}

	/**
	 * Set to {@code true} if calls to {@link #localize(Locale)} will create a version of the current configuration
	 * adapted to the locale specified.  If {@code false} localize calls will create an exact duplicate of the
	 * configuration, no matter the output locale.  Default value is {@code true}.
	 *
	 * @param localizePatterns true if output locale should be taken into account
	 */
	public void setLocalizePatterns( boolean localizePatterns ) {
		this.localizePatterns = localizePatterns;
	}

	/**
	 * Create a localized copy of the current configuration.
	 *
	 * @param locale for which to create a localized instance
	 * @return clone
	 */
	public DateTimeFormElementConfiguration localize( Locale locale ) {
		DateTimeFormElementConfiguration clone = new DateTimeFormElementConfiguration( this );

		if ( localizePatterns ) {
			clone.setLocale( locale );
		}

		return clone;
	}

	/**
	 * @return A date format representing the current configuration.
	 */
	public DateFormat createDateFormat() {
		DateFormat dateFormat = null;

		switch ( format ) {
			case DATE:
				dateFormat = DateFormat.getDateInstance( DateFormat.MEDIUM, locale );
				break;
			case DATE_FULL:
				dateFormat = DateFormat.getDateInstance( DateFormat.FULL, locale );
				break;
			case TIME:
				dateFormat = DateFormat.getTimeInstance( DateFormat.SHORT, locale );
				break;
			case DATETIME:
				dateFormat = DateFormat.getDateTimeInstance( DateFormat.MEDIUM, DateFormat.SHORT, locale );
				break;
			case DATETIME_FULL:
				dateFormat = DateFormat.getDateTimeInstance( DateFormat.LONG, DateFormat.MEDIUM, locale );
				break;
		}

		return dateFormat;
	}

	/**
	 * Format will automatically set the patterns to be used.
	 */
	public enum Format
	{
		DATE,
		DATE_FULL,
		TIME,
		DATETIME,
		DATETIME_FULL
	}
}
