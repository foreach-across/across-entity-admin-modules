package com.foreach.across.testapplication.application.domain.student;

import com.foreach.across.modules.bootstrapui.elements.BootstrapUiElements;
import com.foreach.across.modules.entity.config.EntityConfigurer;
import com.foreach.across.modules.entity.config.builders.EntitiesConfigurationBuilder;
import com.foreach.across.modules.entity.views.ViewElementMode;
import com.foreach.across.modules.experimental.daterange.ui.DateRange;
import com.foreach.across.modules.experimental.daterange.ui.DateRangeControlBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.function.Function;

import static com.foreach.across.modules.experimental.webutility.support.WebUtilityConfigurers.onProperties;

@Configuration
@RequiredArgsConstructor
public class StudentUiConfiguration implements EntityConfigurer
{
	private final DateRangeControlBuilder dateRangeControlBuilder;

	@Override
	public void configure( EntitiesConfigurationBuilder entities ) {
		entities.withType( Student.class )
		        .listView( lvb -> lvb.entityQueryFilter( eqf ->
				                                                 eqf.showProperties( "name", "lastModifiedDate", "createdDate", "enrollmentDate",
				                                                                     "firstClassJoinedDate", "startsStudyingAt", "stopStudingAt" ) ) )
		        .properties(
				        // Also put on some normal datepickers
				        props -> props.property( "enrollmentDate" )
				                      .writable( true )
				                      .viewElementType( ViewElementMode.FILTER_CONTROL, BootstrapUiElements.DATETIME )
				                      .and().property( "firstClassJoinedDate" ).writable( true )
				                      .viewElementType( ViewElementMode.FILTER_CONTROL, BootstrapUiElements.DATETIME )
				                      .and().property( "startsStudyingAt" ).writable( true ).
						                      viewElementType( ViewElementMode.FILTER_CONTROL, BootstrapUiElements.DATETIME )
		        )
		        // Create a date range picker with default dropdown values
		        .properties(
				        onProperties( "createdDate" ).enable(
						        dateRangeControlBuilder.build()
				        )
		        )

		        .properties(
				        onProperties( "stopStudingAt" ).enable(
						        dateRangeControlBuilder.build(
								        DateRange.DATE_RANGE,
								        DateRange.DateRangeItem.of( "lastHour", new Function<Object[], DateRange>()
								        {
									        @Override
									        public DateRange apply( Object[] objects ) {
										        return new DateRange( LocalTime.now().minusHours( 1 ), LocalTime.now() );
									        }
								        } )
						        ) ) )
		        // Create a date range picker with a fixed set of dropdown values
		        .properties(
				        onProperties( "lastModifiedDate" ).enable(
						        dateRangeControlBuilder.build(
								        DateRange.DATE_RANGE,
								        DateRange.DateRangeItem
										        .of( "last14d", args -> new DateRange( LocalDateTime.now().minusDays( 14 ), LocalDateTime.now() ) ),
								        DateRange.YESTERDAY,
								        DateRange.LAST_WEEK
						        ) )
		        )
		        .properties( props -> dateRangeControlBuilder.build().accept( props.property( "enrollmentDate" ) ) );
	}
}
