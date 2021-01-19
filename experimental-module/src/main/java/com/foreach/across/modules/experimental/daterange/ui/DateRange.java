package com.foreach.across.modules.experimental.daterange.ui;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.function.Function;

import static lombok.AccessLevel.PUBLIC;

/**
 * Object representing a date range using a {@link DateRangeFunctionRegistry} for specifying the type of date range
 * When setting the {@param type} to {@link DateRange#DATE_RANGE} the {@param dateFrom} & {@param dateTo} parameters must
 * be provided. In all other cases it is expected the {@param dateFrom} & {@param dateTo} are calculated.
 * <p>
 * Static functiions are provided to use this class.
 *
 * @author Stijn Vanhoof
 */
@Data
@RequiredArgsConstructor(access = PUBLIC)
public class DateRange {

    public static final DateRangeItem DATE_RANGE = DateRange.DateRangeItem.of("dateRange", DateRange::dateRange);
    public static final DateRangeItem TODAY = DateRange.DateRangeItem.of("today", args -> new DateRange(startOfDay(LocalDateTime.now()), endOfDay(LocalDateTime.now())));
    public static final DateRangeItem YESTERDAY = DateRange.DateRangeItem.of("yesterday", args -> new DateRange(startOfDay(LocalDateTime.now().plusDays(-1)), endOfDay(LocalDateTime.now().plusDays(-1))));
    public static final DateRangeItem LAST_WEEK = DateRange.DateRangeItem.of("lastWeek", args -> new DateRange(startOfDay(LocalDateTime.now().plusWeeks(-1)), LocalDateTime.now()));
    public static final DateRangeItem LAST_MONTH = DateRange.DateRangeItem.of("lastMonth", args -> new DateRange(startOfDay(LocalDateTime.now().plusMonths(-1)), LocalDateTime.now()));
    public static final DateRangeItem LAST_YEAR = DateRange.DateRangeItem.of("lastYear", args -> new DateRange(startOfDay(LocalDateTime.now().plusYears(-1)), LocalDateTime.now()));

    public static DateRange dateRange(LocalDateTime[] args) {
        return new DateRange(args[0], args[1]);
    }

    private final LocalDateTime dateFrom;
    private final LocalDateTime dateTo;

    @Getter
    private String type;

    @AllArgsConstructor(staticName = "of")
    @Getter
    public static class DateRangeItem {
        private final String name;
        private final Function<LocalDateTime[], DateRange> dateRange;
    }

    private static LocalDateTime startOfDay(LocalDateTime givenDate) {
        return givenDate.toLocalDate().atStartOfDay();
    }

    private static LocalDateTime endOfDay(LocalDateTime givenDate) {
        return givenDate.toLocalDate().atTime(LocalTime.MAX);
    }
}
