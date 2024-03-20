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

package com.foreach.across.samples.entity.application.extensions;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.configuration.EnableGlobalAuthentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

/**
 * @author Arne Vandamme
 * @since 2.0.0
 */
//@ModuleConfiguration(SpringSecurityModule.NAME)
@Configuration
@EnableGlobalAuthentication
public class SecurityConfiguration
{

	// https://spring.io/blog/2022/02/21/spring-security-without-the-websecurityconfigureradapter#in-memory-authentication
	@Bean
	public InMemoryUserDetailsManager userDetailsService() {
		UserDetails user = User.builder()
		                       .username( "admin" )
		                       .password( "{noop}admin" )
		                       .roles( "access administration" )
		                       .build();
		return new InMemoryUserDetailsManager( user );
	}

}
