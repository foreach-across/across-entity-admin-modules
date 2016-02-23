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

import com.foreach.across.core.AcrossModule;
import com.foreach.across.core.annotations.Exposed;
import com.foreach.across.core.annotations.Module;
import com.foreach.across.modules.adminweb.AdminWebModule;
import com.foreach.across.modules.adminweb.AdminWebModuleSettings;
import com.foreach.across.modules.adminweb.annotations.AdminWebController;
import com.foreach.across.modules.adminweb.controllers.AuthenticationController;
import com.foreach.across.modules.adminweb.menu.AdminMenu;
import com.foreach.across.modules.adminweb.menu.AdminMenuBuilder;
import com.foreach.across.modules.adminweb.menu.EntityAdminMenu;
import com.foreach.across.modules.adminweb.menu.EntityAdminMenuBuilder;
import com.foreach.across.modules.web.config.support.PrefixingHandlerMappingConfiguration;
import com.foreach.across.modules.web.menu.MenuFactory;
import com.foreach.across.modules.web.mvc.PrefixingRequestMappingHandlerMapping;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.support.annotation.AnnotationClassFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.SearchStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;

import javax.annotation.PostConstruct;

@Configuration
public class AdminWebMvcConfiguration extends PrefixingHandlerMappingConfiguration
{
	@Autowired
	@Module(AcrossModule.CURRENT_MODULE)
	private AdminWebModule adminWebModule;

	@Autowired
	private MenuFactory menuFactory;

	@Autowired
	private AdminWebModuleSettings settings;

	@Autowired
	private LocaleProperties localeProperties;

	@SuppressWarnings("unchecked")
	@PostConstruct
	public void initialize() {
		menuFactory.addMenuBuilder( adminMenuBuilder(), AdminMenu.class );
		menuFactory.addMenuBuilder( entityAdminMenuBuilder(), EntityAdminMenu.class );
	}

	@Override
	protected String getPrefixPath() {
		return adminWebModule.getRootPath();
	}

	@Override
	protected ClassFilter getHandlerMatcher() {
		return new AnnotationClassFilter( AdminWebController.class, true );
	}

	@ConditionalOnMissingBean(name = DispatcherServlet.LOCALE_RESOLVER_BEAN_NAME, search = SearchStrategy.ALL)
	@Bean(name = DispatcherServlet.LOCALE_RESOLVER_BEAN_NAME)
	@Exposed
	public CookieLocaleResolver localeResolver() {
		CookieLocaleResolver cookieLocaleResolver = new CookieLocaleResolver();
		cookieLocaleResolver.setDefaultLocale( localeProperties.getDefaultLocale() );

		return cookieLocaleResolver;
	}

	@Bean(name = "adminWebHandlerMapping")
	@Exposed
	@Override
	public PrefixingRequestMappingHandlerMapping controllerHandlerMapping() {
		return super.controllerHandlerMapping();
	}

	@Bean
	public AdminMenuBuilder adminMenuBuilder() {
		return new AdminMenuBuilder();
	}

	// todo: get out to entity module you!
	@Bean
	public EntityAdminMenuBuilder entityAdminMenuBuilder() {
		return new EntityAdminMenuBuilder();
	}

	@Bean
	public AuthenticationController authenticationController() {
		return new AuthenticationController();
	}
}
