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

package admin.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.configuration.EnableGlobalAuthentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import java.util.Collections;

/**
 * @author Arne Vandamme
 */
//@ModuleConfiguration(SpringSecurityModule.NAME)
@Configuration
@EnableGlobalAuthentication
@Slf4j
public class SecurityConfiguration
{

	// https://spring.io/blog/2022/02/21/spring-security-without-the-websecurityconfigureradapter#in-memory-authentication
	@Bean
	public InMemoryUserDetailsManager userDetailsService() {
		LOG.info( "Building InMemoryUserDetailsManager" );
		return new InMemoryUserDetailsManager(
				User.builder()
				    .username( "admin" )
				    .password( "{noop}admin" )
				    .authorities( "access administration" )
				    .build(),
				User.builder()
				    .username( "admin2" )
				    .password( "{noop}admin" )
				    .authorities( "access administration" )
				    .build(),
				User.builder()
				    .username( "user" )
				    .password( "{noop}user" )
				    .authorities( Collections.emptyList() )
				    .build()
		);
	}

}
