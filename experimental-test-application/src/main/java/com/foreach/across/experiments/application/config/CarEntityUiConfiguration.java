package com.foreach.across.experiments.application.config;

import com.foreach.across.experiments.application.dto.CarResource;
import com.foreach.across.modules.entity.config.EntityConfigurer;
import com.foreach.across.modules.entity.config.builders.EntitiesConfigurationBuilder;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CarEntityUiConfiguration implements EntityConfigurer {
    @Override
    public void configure(EntitiesConfigurationBuilder entities) {
        entities.create().entityType(CarResource.class, true).listView("custom", lvb ->
                lvb.properties(props -> props.property("owner")
                        .propertyType(Integer.class)
                        .valueFetcher(f -> 1959)
                )
        );
    }
}
