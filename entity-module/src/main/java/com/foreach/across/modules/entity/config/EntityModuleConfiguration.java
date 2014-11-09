package com.foreach.across.modules.entity.config;

import com.foreach.across.modules.entity.services.EntityFormFactory;
import com.foreach.across.modules.entity.services.EntityFormRenderer;
import com.foreach.across.modules.entity.services.EntityRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@Configuration
public class EntityModuleConfiguration
{
	@Bean
	public EntityRegistry entityRegistry() {
		return new EntityRegistry();
	}

	@Bean
	public EntityFormFactory entityFormFactory() {
		return new EntityFormFactory();
	}

	@Bean
	public EntityFormRenderer entityFormRenderer() {
		return new EntityFormRenderer();
	}

	@Bean
	public LocalValidatorFactoryBean entityValidatorFactory() {
		return new LocalValidatorFactoryBean();
	}
}
