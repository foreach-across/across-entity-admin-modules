package com.foreach.across.testapplication.application.domain.student;

import com.foreach.across.modules.entity.config.EntityConfigurer;
import com.foreach.across.modules.entity.config.builders.EntitiesConfigurationBuilder;
import com.foreach.across.modules.experimental.daterange.ui.DateRange;
import com.foreach.across.modules.experimental.daterange.ui.DateRangeControlBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

import static com.foreach.across.modules.experimental.webutility.support.WebUtilityConfigurers.onProperties;

@Configuration
@RequiredArgsConstructor
public class StudentUiConfiguration implements EntityConfigurer {
    private final DateRangeControlBuilder dateRangeControlBuilder;

    @Override
    public void configure(EntitiesConfigurationBuilder entities) {
        entities.withType(Student.class)
                .listView(lvb -> lvb.entityQueryFilter(eqf -> eqf.showProperties("name", "lastModifiedDate", "createdDate", "enrollmentDate")))
                // Create a date range picker with default dropdown values
                .properties(
                        onProperties("createdDate").enable(
                                dateRangeControlBuilder.build()
                        )
                )
                // Create a date range picker with a fixed set of dropdown values
                .properties(
                        onProperties("lastModifiedDate").enable(
                                dateRangeControlBuilder.build(
                                        DateRange.DATE_RANGE,
                                        DateRange.DateRangeItem.of("last14d", args -> new DateRange(LocalDateTime.now().minusDays(14), LocalDateTime.now())),
                                        DateRange.YESTERDAY,
                                        DateRange.LAST_WEEK
                                ))
                )
                .properties(props -> dateRangeControlBuilder.build().accept(props.property("enrollmentDate")));
    }
}
