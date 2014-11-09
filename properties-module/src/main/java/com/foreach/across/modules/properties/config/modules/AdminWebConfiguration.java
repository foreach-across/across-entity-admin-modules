package com.foreach.across.modules.properties.config.modules;

import com.foreach.across.core.annotations.AcrossDepends;
import com.foreach.across.modules.properties.controllers.EntityPropertiesController;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@AcrossDepends(required = "AdminWebModule")
@Configuration
public class AdminWebConfiguration
{
	@Bean
	public EntityPropertiesController entityPropertiesController() {
		return new EntityPropertiesController();
	}
}
