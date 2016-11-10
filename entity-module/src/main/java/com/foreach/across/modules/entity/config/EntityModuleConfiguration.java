/*
 * Copyright 2014 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.foreach.across.modules.entity.config;

import com.foreach.across.core.annotations.Exposed;
import com.foreach.across.core.context.support.AcrossModuleMessageSource;
import com.foreach.across.modules.entity.EntityModule;
import com.foreach.across.modules.entity.converters.EntityConverter;
import com.foreach.across.modules.entity.converters.EntityToStringConverter;
import com.foreach.across.modules.entity.converters.StringToEntityConfigurationConverter;
import com.foreach.across.modules.entity.formatters.DateFormatter;
import com.foreach.across.modules.entity.formatters.TemporalFormatterFactory;
import com.foreach.across.modules.entity.registrars.ModuleEntityRegistration;
import com.foreach.across.modules.entity.registry.EntityRegistry;
import com.foreach.across.modules.entity.registry.EntityRegistryImpl;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptorFactory;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptorFactoryImpl;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistryFactory;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistryFactoryImpl;
import com.foreach.across.modules.entity.views.EntityDeleteViewFactory;
import com.foreach.across.modules.entity.views.EntityFormViewFactory;
import com.foreach.across.modules.entity.views.EntityListViewFactory;
import com.foreach.across.modules.entity.views.EntityViewViewFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.format.datetime.DateFormatterRegistrar;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.validation.SmartValidator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import javax.annotation.PostConstruct;

@Configuration
public class EntityModuleConfiguration
{
	private static final Logger LOG = LoggerFactory.getLogger( EntityModuleConfiguration.class );

	@Autowired
	private FormattingConversionService mvcConversionService;

	@PostConstruct
	public void init() {
		EntityRegistry entityRegistry = entityRegistry();

		mvcConversionService.addConverter( new StringToEntityConfigurationConverter( entityRegistry ) );
		mvcConversionService.addConverter( new EntityConverter<>( mvcConversionService, entityRegistry ) );
		mvcConversionService.addConverter( new EntityToStringConverter( entityRegistry ) );

		DateFormatterRegistrar dateFormatterRegistrar = new DateFormatterRegistrar();
		dateFormatterRegistrar.setFormatter( new DateFormatter() );
		dateFormatterRegistrar.registerFormatters( mvcConversionService );
		mvcConversionService.addFormatterForFieldAnnotation( new TemporalFormatterFactory() );
	}

	@Bean
	public EntityRegistryImpl entityRegistry() {
		return new EntityRegistryImpl();
	}

	@Bean(name = EntityModule.VALIDATOR)
	@Exposed
	@ConditionalOnMissingBean(name = EntityModule.VALIDATOR)
	public SmartValidator entityValidator() {
		LocalValidatorFactoryBean localValidatorFactoryBean = new LocalValidatorFactoryBean();
		localValidatorFactoryBean.setValidationMessageSource( messageSource() );
		return localValidatorFactoryBean;
	}

	/**
	 * Ensures modules can configure entities through either EntityRegistrar or EntityConfigurer beans.
	 */
	@Bean
	public ModuleEntityRegistration moduleEntityRegistration() {
		return new ModuleEntityRegistration();
	}

	@Bean
	@Exposed
	public EntityPropertyRegistryFactory entityPropertyRegistryFactory() {
		return new EntityPropertyRegistryFactoryImpl();
	}

	@Bean
	public EntityPropertyDescriptorFactory entityPropertyDescriptorFactory() {
		return new EntityPropertyDescriptorFactoryImpl();
	}

	@Bean
	public MessageSource messageSource() {
		return new AcrossModuleMessageSource();
	}

	@Bean
	@Exposed
	@Scope("prototype")
	public EntityViewViewFactory entityViewViewFactory() {
		return new EntityViewViewFactory();
	}

	@Bean
	@Exposed
	@Scope("prototype")
	public EntityListViewFactory entityListViewFactory() {
		return new EntityListViewFactory();
	}

	@Bean
	@Exposed
	@Scope("prototype")
	public EntityFormViewFactory entityFormViewFactory() {
		return new EntityFormViewFactory();
	}

	@Bean
	@Exposed
	@Scope("prototype")
	public EntityDeleteViewFactory entityDeleteViewFactory() {
		return new EntityDeleteViewFactory();
	}
}
