package com.foreach.across.modules.entity.config.modules;

import com.foreach.across.core.annotations.AcrossDepends;
import com.foreach.across.modules.entity.controllers.EntityController;
import com.foreach.across.modules.entity.controllers.EntitySaveController;
import com.foreach.across.modules.entity.handlers.MenuEventsHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@AcrossDepends(required = "AdminWebModule")
@Configuration
public class AdminWebConfiguration
{
	@Bean
	public MenuEventsHandler menuEventsHandler() {
		return new MenuEventsHandler();
	}

	@Bean
	public EntityController entityController() {
		return new EntityController();
	}

	@Bean
	public EntitySaveController entitySaveController() {
		return new EntitySaveController();
	}
}
