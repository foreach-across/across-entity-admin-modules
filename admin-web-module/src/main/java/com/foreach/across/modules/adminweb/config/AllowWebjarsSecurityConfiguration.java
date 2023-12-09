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

package com.foreach.across.modules.adminweb.config;

import com.foreach.across.core.context.registry.AcrossContextBeanRegistry;
import com.foreach.across.modules.spring.security.configuration.AcrossWebSecurityConfigurer;
import com.foreach.across.modules.web.AcrossWebModule;
import com.foreach.across.modules.web.config.resources.ResourceConfigurationProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

/**
 * AdminWeb requires the static resources to be available, so it permits all of them by default.
 * Registered with a global 0 order so it would be before all default security configuration of regular modules.
 */
@Configuration
@Order(0)
@RequiredArgsConstructor
public class AllowWebjarsSecurityConfiguration implements AcrossWebSecurityConfigurer
{
	private final AcrossContextBeanRegistry beanRegistry;

	@Override
	public void configure( HttpSecurity http ) throws Exception {
		ResourceConfigurationProperties resourceConfigurationProperties
				= beanRegistry.getBeanOfTypeFromModule( AcrossWebModule.NAME, ResourceConfigurationProperties.class );

		http.antMatcher( resourceConfigurationProperties.getWebjars() + "/**" )
		    .authorizeRequests().anyRequest().permitAll();
	}
}
