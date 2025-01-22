package com.foreach.across.modules.experimental.daterange.ui;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.function.Function;

/**
 * Object representing a date range using a {@link DateRangeFunctionRegistry} for specifying the type of date range
 * When setting the {@param type} to {@link DateRange#DATE_RANGE} the {@param dateFrom} & {@param dateTo} parameters must
 * be provided. In all other cases it is expected the {@param dateFrom} & {@param dateTo} are calculated.
 * <p>
 * Static functions are provided to use this class.
 *
 * @author Stijn Vanhoof
 */
@Data
public class DateRange
{

	public static final DateRangeItem DATE_RANGE = DateRange.DateRangeItem.of( "dateRange", DateRange::dateRange );
	public static final DateRangeItem TODAY = DateRange.DateRangeItem.of( "today", args -> new DateRange( startOfDay( LocalDateTime.now() ),
	                                                                                                      endOfDay( LocalDateTime.now() ) ) );
	public static final DateRangeItem YESTERDAY = DateRange.DateRangeItem.of( "yesterday",
	                                                                          args -> new DateRange( startOfDay( LocalDateTime.now().plusDays( -1 ) ),
	                                                                                                 endOfDay( LocalDateTime.now().plusDays( -1 ) ) ) );
	public static final DateRangeItem LAST_WEEK = DateRange.DateRangeItem.of( "lastWeek",
	                                                                          args -> new DateRange( startOfDay( LocalDateTime.now().plusWeeks( -1 ) ),
	                                                                                                 LocalDateTime.now() ) );
	public static final DateRangeItem LAST_MONTH = DateRange.DateRangeItem.of( "lastMonth",
	                                                                           args -> new DateRange( startOfDay( LocalDateTime.now().plusMonths( -1 ) ),
	                                                                                                  LocalDateTime.now() ) );
	public static final DateRangeItem LAST_YEAR = DateRange.DateRangeItem.of( "lastYear",
	                                                                          args -> new DateRange( startOfDay( LocalDateTime.now().plusYears( -1 ) ),
	                                                                                                 LocalDateTime.now() ) );

	public static DateRange dateRange( Object[] args ) {
		return args.length == 2 ? new DateRange( args[0], args[1] ) : new DateRange();
	}

	private DateOrLocalDateTime dateFrom;
	private DateOrLocalDateTime dateTo;

	public DateRange() {
	}

	public DateRange( Object from, Object to ) {
		dateFrom = DateOrLocalDateTime.wrap( from );
		dateTo = DateOrLocalDateTime.wrap( to );
	}

	@Getter
	private String type;

	@AllArgsConstructor(staticName = "of")
	@Getter
	public static class DateRangeItem
	{
		private final String name;
		private final Function<Object[], DateRange> dateRange;
	}

	private static LocalDateTime startOfDay( LocalDateTime givenDate ) {
		return givenDate.toLocalDate().atStartOfDay();
	}

	private static LocalDateTime endOfDay( LocalDateTime givenDate ) {
		return givenDate.toLocalDate().atTime( LocalTime.MAX );
	}

	public static class DateOrLocalDateTime
	{
		private final Object dateOrLocalDateTime;

		protected DateOrLocalDateTime( Object dateOrLocalDateTime ) {
			this.dateOrLocalDateTime = dateOrLocalDateTime;
		}

		public static DateOrLocalDateTime wrap( Object date ) {
			return new DateOrLocalDateTime( date );
		}

		public Object getSource() {
			return dateOrLocalDateTime;
		}

		public boolean isLocalDateTime() {
			return dateOrLocalDateTime instanceof LocalDateTime;
		}

		public LocalDateTime toLocalDateTime() {
			if ( dateOrLocalDateTime == null ) {
				return null;
			}

			if ( isLocalDateTime() ) {
				return (LocalDateTime) dateOrLocalDateTime;
			}

			Date date = (Date) this.dateOrLocalDateTime;
			return LocalDateTime.ofInstant( date.toInstant(), ZoneId.systemDefault() );
		}

		public Date toDate() {
			if ( dateOrLocalDateTime == null ) {
				return null;
			}

			if ( !isLocalDateTime() ) {
				return (Date) dateOrLocalDateTime;
			}

			LocalDateTime localDateTime = toLocalDateTime();
			return Date.from( ( localDateTime ).toInstant( ZoneId.systemDefault().getRules().getOffset( localDateTime ) ) );
		}

	}
}
