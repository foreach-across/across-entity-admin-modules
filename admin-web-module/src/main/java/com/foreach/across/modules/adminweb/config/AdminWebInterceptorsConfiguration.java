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
import com.foreach.across.core.development.AcrossDevelopmentMode;
import com.foreach.across.modules.adminweb.AdminWeb;
import com.foreach.across.modules.adminweb.AdminWebModule;
import com.foreach.across.modules.adminweb.AdminWebModuleSettings;
import com.foreach.across.modules.adminweb.config.support.AdminWebConfigurerAdapter;
import com.foreach.across.modules.adminweb.menu.registrars.DefaultAdminMenuRegistrar;
import com.foreach.across.modules.adminweb.resource.AdminBootstrapWebResourcePackage;
import com.foreach.across.modules.adminweb.ui.AdminWebLayoutTemplate;
import com.foreach.across.modules.adminweb.ui.PageContentStructure;
import com.foreach.across.modules.web.context.PrefixingPathRegistry;
import com.foreach.across.modules.web.mvc.InterceptorRegistry;
import com.foreach.across.modules.web.mvc.WebAppPathResolverExposingInterceptor;
import com.foreach.across.modules.web.resource.WebResourcePackageManager;
import com.foreach.across.modules.web.resource.WebResourceRegistryInterceptor;
import com.foreach.across.modules.web.resource.WebResourceTranslator;
import com.foreach.across.modules.web.template.WebTemplateInterceptor;
import com.foreach.across.modules.web.template.WebTemplateRegistry;
import com.foreach.across.modules.web.ui.ViewElementBuilderContextInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Arne Vandamme
 */
@Configuration
@ComponentScan(basePackageClasses = { AdminWebLayoutTemplate.class, DefaultAdminMenuRegistrar.class })
public class AdminWebInterceptorsConfiguration extends AdminWebConfigurerAdapter
{
	private static final Logger LOG = LoggerFactory.getLogger( AdminWebModule.class );

	@Autowired(required = false)
	private WebResourceTranslator viewsWebResourceTranslator;

	@Autowired
	private AcrossDevelopmentMode developmentMode;

	@Autowired
	private AdminWebModuleSettings settings;

	@Autowired
	private PrefixingPathRegistry prefixingPathRegistry;

	@Override
	public void addInterceptors( InterceptorRegistry interceptorRegistry ) {
		interceptorRegistry.addInterceptor( new WebAppPathResolverExposingInterceptor( adminWeb() ) );
		interceptorRegistry.addInterceptor( adminWebResourceRegistryInterceptor() );
		interceptorRegistry.addInterceptor( adminWebViewElementBuilderContextInterceptor() );
		interceptorRegistry.addInterceptor( adminWebTemplateInterceptor() );
	}

	@Bean
	public ViewElementBuilderContextInterceptor adminWebViewElementBuilderContextInterceptor() {
		return new ViewElementBuilderContextInterceptor();
	}

	@Bean(name = AdminWeb.NAME)
	@Exposed
	public AdminWeb adminWeb() {
		AdminWeb adminWeb = new AdminWeb( settings.getRootPath() );
		prefixingPathRegistry.add( AdminWeb.NAME, adminWeb );

		return adminWeb;
	}

	@Bean
	@Exposed
	public WebResourceRegistryInterceptor adminWebResourceRegistryInterceptor() {
		WebResourceRegistryInterceptor interceptor =
				new WebResourceRegistryInterceptor( adminWebResourcePackageManager() );

		if ( viewsWebResourceTranslator != null ) {
			interceptor.addWebResourceTranslator( viewsWebResourceTranslator );
		}
		else {
			LOG.warn( "No default viewsWebResourceTranslator configured - manual translators will be required." );
		}

		return interceptor;
	}

	@Bean
	public WebTemplateInterceptor adminWebTemplateInterceptor() {
		return new WebTemplateInterceptor( adminWebTemplateRegistry() );
	}

	@Bean
	@Exposed
	public WebTemplateRegistry adminWebTemplateRegistry() {
		WebTemplateRegistry webTemplateRegistry = new WebTemplateRegistry();
		webTemplateRegistry.setDefaultTemplateName( AdminWeb.NAME );

		return webTemplateRegistry;
	}

	@Bean
	@Exposed
	public WebResourcePackageManager adminWebResourcePackageManager() {
		WebResourcePackageManager webResourcePackageManager = new WebResourcePackageManager();
		webResourcePackageManager.register( AdminBootstrapWebResourcePackage.NAME, new AdminBootstrapWebResourcePackage() );
		return webResourcePackageManager;
	}

	@Bean
	@Exposed
	@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
	public PageContentStructure pageContentStructure( HttpServletRequest request ) {
		PageContentStructure page = new PageContentStructure();
		request.setAttribute( PageContentStructure.MODEL_ATTRIBUTE, page );
		return page;
	}
}
