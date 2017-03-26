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

package com.foreach.across.modules.entity.testmodules.springdata.config;

import com.foreach.across.modules.entity.testmodules.springdata.SpringDataJpaModule;
import com.foreach.across.modules.entity.testmodules.springdata.business.Client;
import com.foreach.across.modules.entity.testmodules.springdata.business.ClientGroupId;
import com.foreach.across.modules.entity.testmodules.springdata.business.Group;
import com.foreach.across.modules.entity.testmodules.springdata.validators.CompanyValidator;
import com.foreach.across.modules.hibernate.jpa.repositories.config.EnableAcrossJpaRepositories;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.convert.support.ConfigurableConversionService;

@Configuration
@EnableAcrossJpaRepositories(basePackageClasses = SpringDataJpaModule.class)
public class ClientConfig
{
	@Bean
	public MessageSource messageSource() {
		ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
		messageSource.setBasename( "client" );

		return messageSource;
	}

	@Bean
	public CompanyValidator companyValidator() {
		return new CompanyValidator();
	}

	@Autowired
	public void registerClientGroupIdConverters( ConfigurableConversionService mvcConversionService ) {
		mvcConversionService.addConverter( ClientGroupId.class, String.class, source -> {
			return source.getClient().getId() + "-" + source.getGroup().getId();
		} );

		mvcConversionService.addConverter( String.class, ClientGroupId.class, source -> {
			String[] parts = source.split( "-" );
			ClientGroupId id = new ClientGroupId();
			id.setClient( mvcConversionService.convert( parts[0], Client.class ) );
			id.setGroup( mvcConversionService.convert( parts[1], Group.class ) );
			return id;
		} );

	}
}
