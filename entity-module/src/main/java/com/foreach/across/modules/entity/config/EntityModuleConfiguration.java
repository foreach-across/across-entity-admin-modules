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
import com.foreach.across.modules.entity.EntityModule;
import com.foreach.across.modules.entity.converters.EntityToStringConverter;
import com.foreach.across.modules.entity.converters.StringToEntityConfigurationConverter;
import com.foreach.across.modules.entity.formatters.DateFormatter;
import com.foreach.across.modules.entity.formatters.TemporalFormatterFactory;
import com.foreach.across.modules.entity.query.support.EQStringToDateConverter;
import com.foreach.across.modules.entity.registry.EntityRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.datetime.DateFormatterRegistrar;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.validation.SmartValidator;

@Configuration
@Slf4j
public class EntityModuleConfiguration
{
	@Autowired
	public void registerConverters( FormattingConversionService mvcConversionService, EntityRegistry entityRegistry ) {
		mvcConversionService.addConverter( new StringToEntityConfigurationConverter( entityRegistry ) );
		mvcConversionService.addConverter( new EntityToStringConverter( entityRegistry ) );

		DateFormatterRegistrar dateFormatterRegistrar = new DateFormatterRegistrar();
		dateFormatterRegistrar.setFormatter( new DateFormatter() );
		dateFormatterRegistrar.registerFormatters( mvcConversionService );
		mvcConversionService.addFormatterForFieldAnnotation( new TemporalFormatterFactory() );

		mvcConversionService.addConverter( new EQStringToDateConverter( mvcConversionService ) );
	}

	/**
	 * Expose the default validator as the validator to be used for entity validation.
	 * Use of {@link com.foreach.across.modules.entity.annotations.EntityValidator} in external modules is discouraged.
	 */
	@Bean(name = EntityModule.VALIDATOR)
	@Exposed
	@ConditionalOnMissingBean(name = EntityModule.VALIDATOR)
	public SmartValidator entityValidator( SmartValidator defaultValidator ) {
		return defaultValidator;
	}
}
