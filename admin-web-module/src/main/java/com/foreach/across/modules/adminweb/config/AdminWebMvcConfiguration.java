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

import com.foreach.across.core.annotations.Exposed;
import com.foreach.across.modules.adminweb.AdminWebModuleIcons;
import com.foreach.across.modules.adminweb.AdminWebModuleSettings;
import com.foreach.across.modules.adminweb.annotations.AdminWebController;
import com.foreach.across.modules.adminweb.controllers.AuthenticationController;
import com.foreach.across.modules.adminweb.menu.AdminMenu;
import com.foreach.across.modules.adminweb.menu.AdminMenuBuilder;
import com.foreach.across.modules.web.config.support.PrefixingHandlerMappingConfiguration;
import com.foreach.across.modules.web.menu.MenuFactory;
import com.foreach.across.modules.web.menu.MenuStore;
import com.foreach.across.modules.web.mvc.PrefixingRequestMappingHandlerMapping;
import lombok.RequiredArgsConstructor;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.support.annotation.AnnotationClassFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.SearchStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;

import javax.annotation.PostConstruct;

@Configuration
@RequiredArgsConstructor
@ComponentScan(basePackageClasses = AuthenticationController.class)
public class AdminWebMvcConfiguration extends PrefixingHandlerMappingConfiguration
{
	//private final MenuFactory menuFactory;
	private final AdminWebModuleSettings settings;
	private final LocaleProperties localeProperties;

	@SuppressWarnings("unchecked")
	@PostConstruct
	public void initialize() {
		//menuFactory.addMenuBuilder( adminMenuBuilder(), AdminMenu.class );
		//menuFactory.addMenuBuilder( entityAdminMenuBuilder(), EntityAdminMenu.class );

		AdminWebModuleIcons.registerIconSet();
	}

	@Bean
	//@ConditionalOnMissingBean(MenuFactory.class)
	public MenuFactory adminWebMenuFactory(MenuStore requestMenuStore) {
		MenuFactory menuFactory = new MenuFactory();
		//menuFactory.setDefaultMenuBuilder( requestMenuBuilder );
		menuFactory.setDefaultMenuStore( requestMenuStore );
		menuFactory.addMenuBuilder( adminMenuBuilder(), AdminMenu.class );
		return menuFactory;
	}

	@Override
	protected String getPrefixPath() {
		return settings.getRootPath();
	}

	@Override
	protected ClassFilter getHandlerMatcher() {
		return new AnnotationClassFilter( AdminWebController.class, true );
	}

	// TODO for some reason, this is working backwards:
	// The bean 'localeResolver', defined in org.springframework.web.servlet.config.annotation.DelegatingWebMvcConfiguration, could not be registered.
	// A bean with that name has already been defined in class path resource [com/foreach/across/modules/adminweb/config/AdminWebMvcConfiguration.class] and overriding is disabled.
/*
	@ConditionalOnMissingBean(name = DispatcherServlet.LOCALE_RESOLVER_BEAN_NAME, search = SearchStrategy.ALL)
	@Bean(name = DispatcherServlet.LOCALE_RESOLVER_BEAN_NAME)
	@Exposed
	public CookieLocaleResolver localeResolver() {
		CookieLocaleResolver cookieLocaleResolver = new CookieLocaleResolver();
		cookieLocaleResolver.setDefaultLocale( localeProperties.getDefaultLocale() );

		return cookieLocaleResolver;
	}
*/

	@Bean(name = "adminWebHandlerMapping")
	@Exposed
	@Override
	public PrefixingRequestMappingHandlerMapping controllerHandlerMapping() {
		return super.controllerHandlerMapping();
	}

	@Bean
	//@ConditionalOnMissingBean(AdminMenuBuilder.class)
	public AdminMenuBuilder adminMenuBuilder() {
		return new AdminMenuBuilder();
	}

/*
	// todo: get out to entity module you!
	@Bean
	public EntityAdminMenuBuilder entityAdminMenuBuilder() {
		return new EntityAdminMenuBuilder();
	}
*/
}
