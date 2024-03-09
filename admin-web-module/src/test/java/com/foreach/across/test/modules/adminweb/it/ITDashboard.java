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

package com.foreach.across.test.modules.adminweb.it;

import com.foreach.across.config.AcrossContextConfigurer;
import com.foreach.across.core.AcrossContext;
import com.foreach.across.core.annotations.Exposed;
import com.foreach.across.modules.adminweb.AdminWeb;
import com.foreach.across.modules.adminweb.AdminWebModule;
import com.foreach.across.modules.adminweb.AdminWebModuleSettings;
import com.foreach.across.modules.adminweb.config.AdminWebSecurityConfiguration;
import com.foreach.across.modules.spring.security.SpringSecurityModule;
import com.foreach.across.modules.spring.security.configuration.AcrossWebSecurityConfiguration;
import com.foreach.across.test.AcrossTestWebContext;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.configuration.EnableGlobalAuthentication;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static com.foreach.across.test.support.AcrossTestBuilders.web;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Disabled
/**
 * @author Arne Vandamme
 * @since 1.1.1
 */
public class ITDashboard
{
	@Test
	public void defaultDashboard() throws Exception {
		try (
				AcrossTestWebContext ctx = web()
						.modules( AdminWebModule.NAME, SpringSecurityModule.NAME )
						//.moduleConfigurationPackageClasses( DashboardUserConfiguration.class )
						//.register( AdminWebSecurityConfiguration.class )
						//.register( AcrossWebSecurityConfiguration.class )
						.register( DashboardUserConfiguration.class )
						.build()
		) {
			MockMvc mvc = ctx.mockMvc();

			assertLoginRedirectsToRoot( mvc );

			mvc.perform( rootGet() ).andExpect( status().isOk() );
		}
	}

	@Test
	public void customDashboard() throws Exception {
		try (
				AcrossTestWebContext ctx = web()
						.property( AdminWebModuleSettings.DASHBOARD_PATH, "/custom/dashboard" )
						.modules( AdminWebModule.NAME )
						.moduleConfigurationPackageClasses( DashboardUserConfiguration.class )
						.build()
		) {
			MockMvc mvc = ctx.mockMvc();

			assertLoginRedirectsToRoot( mvc );

			mvc.perform( rootGet() ).andExpect( redirectedUrl( "/admin/custom/dashboard" ) );
		}
	}

	private MockHttpServletRequestBuilder rootGet() {
		return get( "/admin/" ).with(
				user( "user" ).authorities( new SimpleGrantedAuthority( "access administration" ) )
		);
	}

	private void assertLoginRedirectsToRoot( MockMvc mvc ) throws Exception {
		mvc.perform(
				   post( "/admin/login" )
						   .with( csrf() )
						   .param( "username", "dashboard" )
						   .param( "password", "dashboard" )
		   )
		   .andExpect( redirectedUrl( "/admin/" ) );
	}

	//	@ModuleConfiguration(SpringSecurityModule.NAME)
	//@EnableGlobalAuthentication
	@EnableWebSecurity
	@Configuration
	public static class DashboardUserConfiguration
	{
/*
		@Autowired
		public void configureGlobal( AuthenticationManagerBuilder auth ) throws Exception {
			auth.inMemoryAuthentication()
			    .withUser( "dashboard" ).password( "{noop}dashboard" )
			    .authorities( new SimpleGrantedAuthority( "access administration" ) );
		}
*/

		@Exposed
		@Bean
		public InMemoryUserDetailsManager userDetailsManager() {
			UserDetails user = User.builder()
			                       .username( "dashboard" )
			                       .password( "{noop}dashboard" )
			                       .authorities( "access administration" )
			                       .build();
			return new InMemoryUserDetailsManager( user );
		}
	}
}
