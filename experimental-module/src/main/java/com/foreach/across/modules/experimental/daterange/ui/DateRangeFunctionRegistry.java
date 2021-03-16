package com.foreach.across.modules.experimental.daterange.ui;

import com.foreach.across.core.annotations.Exposed;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Enum containing all possibillites regarding the type of a {@link DateRange}
 * Besides a {@param name} each type also contains a {@param factory} that executes a provided function
 * to calculate the periods.
 *
 * @author Stijn Vanhoof
 */
@RequiredArgsConstructor
@Component
@Exposed
public class DateRangeFunctionRegistry
{

	private final Map<String, Function<Object[], DateRange>> items = new HashMap<>();

	public DateRangeFunctionRegistry register( String name, Function<Object[], DateRange> dateRangeFunction ) {
		Function<Object[], DateRange> existingFunction = items.get( name );
		if ( existingFunction == null ) {
			items.put( name, dateRangeFunction );
		}
		else {
			if ( existingFunction != dateRangeFunction ) {
				throw new RuntimeException(
						"Trying to overwrite an existing function with a different dateRangeFunction. Reuse the existing DateRangeItem instance of use another name for your function." );
			}
		}
		return this;
	}

	public Function<Object[], DateRange> forName( String functionName ) {
		return items.get( functionName );
	}

	public Object createDateRange( String functionName, LocalDateTime[] boundaries, Class<?> objectType ) {
		DateRange dateRange = items.get( functionName ).apply( boundaries );
		if ( objectType == Date.class && ( dateRange.getDateFrom().isLocalDateTime() || dateRange.getDateTo().isLocalDateTime() ) ) {
			// JPA requires the type of the property to match for querying
			dateRange = new DateRange( dateRange.getDateFrom().toDate(), dateRange.getDateTo().toDate() );
		}
		dateRange.setType( functionName );
		return dateRange;
	}

}
