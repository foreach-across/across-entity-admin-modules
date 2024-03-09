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

import com.foreach.across.modules.adminweb.AdminWeb;
import com.foreach.across.modules.adminweb.AdminWebModule;
import com.foreach.across.modules.adminweb.AdminWebModuleIcons;
import com.foreach.across.modules.bootstrapui.elements.icons.IconSet;
import com.foreach.across.modules.web.mvc.PrefixingRequestMappingHandlerMapping;
import com.foreach.across.modules.web.resource.WebResourcePackageManager;
import com.foreach.across.modules.web.resource.WebResourceRegistryInterceptor;
import com.foreach.across.modules.web.template.WebTemplateRegistry;
import com.foreach.across.test.AcrossTestConfiguration;
import com.foreach.across.test.AcrossWebAppConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@EnableWebSecurity
@ExtendWith(SpringExtension.class)
@DirtiesContext
@AcrossWebAppConfiguration
@TestPropertySource(properties = "across.web.resources.path=/static")
public class ITAdminWebDefaultSettings
{
	@Autowired
	@Qualifier("adminWebTemplateRegistry")
	private WebTemplateRegistry adminWebTemplateRegistry;

	@Autowired
	@Qualifier("adminWebResourcePackageManager")
	private WebResourcePackageManager adminWebResourcePackageManager;

	@Autowired
	@Qualifier("adminWebResourceRegistryInterceptor")
	private WebResourceRegistryInterceptor adminWebResourceRegistryInterceptor;

	@Autowired
	@Qualifier("adminWebHandlerMapping")
	private PrefixingRequestMappingHandlerMapping adminRequestMappingHandlerMapping;

	@Autowired
	private CookieLocaleResolver localeResolver;

	@Autowired
	private AdminWeb adminWeb;

	@Autowired
	private MockMvc mvc;

	@Test
	public void exposedBeans() {
		assertNotNull( adminWeb );
		assertEquals( "/administration", adminWeb.getPathPrefix() );

		assertNotNull( adminWebTemplateRegistry );
		assertNotNull( adminWebResourcePackageManager );
		assertNotNull( adminWebResourceRegistryInterceptor );
		assertNotNull( adminRequestMappingHandlerMapping );
		assertNotNull( localeResolver );
	}

	@Test
	public void staticResourcesShouldNotBeSecured() throws Exception {
		mvc.perform( get( "/static/static/adminweb/css/adminweb.css" ) )
		   .andExpect( status().isOk() );
		mvc.perform( get( "/webjars/toastr/2.1.2/toastr.min.js" ) )
		   .andExpect( status().isOk() );
	}

	@Test
	public void securedAdminControllerShouldRedirectToLogin() throws Exception {
		mvc.perform( get( "/administration/somePage" ) )
		   .andExpect( redirectedUrl( "http://localhost/administration/login" ) );
	}

	@Test
	public void loginPageShouldBeUnsecuredAndRememberMeDisabled() throws Exception {
		mvc.perform( get( "/administration/login" ) )
		   .andExpect( status().isOk() )
		   .andExpect( content().string( not( containsString( "remember-me" ) ) ) );
	}

	@Test
	public void iconsAreAvailable() {
		assertNotNull( IconSet.iconSet( AdminWebModuleIcons.ICON_SET ).icon( AdminWebModuleIcons.DEVELOPER_TOOLS ) );
	}

	@AcrossTestConfiguration
	protected static class Config
	{
		@Bean
		public AdminWebModule adminWebModule() {
			AdminWebModule adminWebModule = new AdminWebModule();
			adminWebModule.setRootPath( "/administration/" );

			return adminWebModule;
		}
	}
}
