package com.foreach.across.modules.experimental.daterange.ui;

import com.foreach.across.core.annotations.Exposed;
import com.foreach.across.modules.entity.config.builders.EntityPropertyRegistryBuilder;
import com.foreach.across.modules.experimental.daterange.support.DateRangeEntityTranslator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

/**
 * The entry point to setup a
 */
@RequiredArgsConstructor
@Component
@Exposed
public class DateRangeControlBuilder
{

	private final DateRangeFunctionRegistry dateRangeFunctionRegistry;
	private final DateRangeEntityTranslator dateRangeEntityTranslator;

	/**
	 * Builds a {@link DateRangeControl} with default date ranges.
	 */
	public Consumer<EntityPropertyRegistryBuilder.PropertyDescriptorBuilder> build() {
		return build( null );
	}

	/**
	 * Builds a {@link DateRangeControl} and registers the date ranges on the {@link DateRangeFunctionRegistry}
	 */
	public Consumer<EntityPropertyRegistryBuilder.PropertyDescriptorBuilder> build( DateRange.DateRangeItem... dateRanges ) {
		final List<DateRange.DateRangeItem> ranges = dateRanges == null ? new ArrayList<>() : Arrays.asList( dateRanges );
		if ( ranges.isEmpty() ) {
			ranges.addAll( DateRangeControl.DEFAULT_DATE_RANGES );
		}
		for ( DateRange.DateRangeItem item : ranges ) {
			dateRangeFunctionRegistry.register( item.getName(), item.getDateRange() );
		}
		return DateRangeControl.build( dateRangeEntityTranslator, ranges );
	}

}
