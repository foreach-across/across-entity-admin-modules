package com.foreach.across.testapplication.application.domain.customer;

import com.foreach.across.modules.entity.config.EntityConfigurer;
import com.foreach.across.modules.entity.config.builders.EntitiesConfigurationBuilder;
import com.foreach.across.modules.entity.views.ViewElementMode;
import org.springframework.context.annotation.Configuration;

import static com.foreach.across.modules.experimental.webutility.viewelements.createselect.CreateSelectViewElementBuilderFactory.CREATE_SELECT;

@Configuration
public class CustomerUiConfiguration implements EntityConfigurer {
    @Override
    public void configure(EntitiesConfigurationBuilder entities) {
        entities.withType(Customer.class)
                .properties(props -> props.property("drink")
                        .viewElementType(ViewElementMode.FORM_WRITE, CREATE_SELECT)
                );
    }
}
