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

package com.foreach.across.samples.entity.modules.config;

import com.foreach.across.core.annotations.Exposed;
import com.foreach.across.core.context.info.AcrossContextInfo;
import com.foreach.across.modules.adminweb.AdminWebModule;
import com.foreach.across.modules.adminweb.resource.AdminWebWebResources;
import com.foreach.across.modules.adminweb.ui.AdminWebLayoutTemplate;
import com.foreach.across.modules.bootstrapui.resource.BootstrapUiFormElementsWebResources;
import com.foreach.across.modules.bootstrapui.resource.BootstrapUiWebResources;
import com.foreach.across.modules.bootstrapui.resource.JQueryWebResources;
import com.foreach.across.modules.debugweb.DebugWebModule;
import com.foreach.across.modules.entity.EntityModule;
import com.foreach.across.modules.entity.web.EntityModuleWebResources;
import com.foreach.across.modules.web.config.support.PrefixingHandlerMappingConfigurerAdapter;
import com.foreach.across.modules.web.context.WebAppPathResolver;
import com.foreach.across.modules.web.mvc.InterceptorRegistry;
import com.foreach.across.modules.web.resource.WebResourcePackageManager;
import com.foreach.across.modules.web.resource.WebResourceRegistryInterceptor;
import com.foreach.across.modules.web.template.WebTemplateInterceptor;
import com.foreach.across.modules.web.template.WebTemplateRegistry;
import com.foreach.across.modules.web.ui.ViewElementBuilderContextInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class EntityModuleInterceptorsConfiguration extends PrefixingHandlerMappingConfigurerAdapter
{
	private WebAppPathResolver webAppPathResolver;
	private AcrossContextInfo acrossContextInfo;

	@Override
	public boolean supports( String mapperName ) {
		return !StringUtils.equalsAny( mapperName, AdminWebModule.NAME, DebugWebModule.NAME );
	}

	@Override
	public void addInterceptors( InterceptorRegistry interceptorRegistry ) {
//		interceptorRegistry.addInterceptor( new WebAppPathResolverExposingInterceptor( adminWeb() ) );
		interceptorRegistry.addInterceptor( entityWebResourceRegistryInterceptor() );
		interceptorRegistry.addInterceptor( entityWebViewElementBuilderContextInterceptor() );
//		interceptorRegistry.addInterceptor( entityWebTemplateInterceptor() );
	}

	@Bean
	public ViewElementBuilderContextInterceptor entityWebViewElementBuilderContextInterceptor() {
		return new ViewElementBuilderContextInterceptor();
	}

	@Bean
	@Exposed
	public WebResourceRegistryInterceptor entityWebResourceRegistryInterceptor() {
		WebResourceRegistryInterceptor interceptor =
				new WebResourceRegistryInterceptor( entityWebResourcePackageManager() );

//		if ( viewsWebResourceTranslator != null ) {
//			interceptor.addWebResourceTranslator( viewsWebResourceTranslator );
//		}
//		else {
		LOG.warn( "No default viewsWebResourceTranslator configured - manual translators will be required." );
//		}

		return interceptor;
	}

	@Bean
	// todo template configuration?
	public WebTemplateInterceptor entityWebTemplateInterceptor() {
		return new WebTemplateInterceptor( entityWebTemplateRegistry() );
	}

	@Bean
	@Exposed
	public WebTemplateRegistry entityWebTemplateRegistry() {
		WebTemplateRegistry webTemplateRegistry = new WebTemplateRegistry();
		webTemplateRegistry.setDefaultTemplateName( EntityModule.NAME );
		ApplicationContext applicationContext = acrossContextInfo.getModuleInfo( AdminWebModule.NAME ).getApplicationContext();
		AutowireCapableBeanFactory beanFactory = applicationContext.getAutowireCapableBeanFactory();
		AdminWebLayoutTemplate template = beanFactory.createBean( MyLayoutTemplate.class );
		webTemplateRegistry.register( EntityModule.NAME, template );
		return webTemplateRegistry;
	}

	@Bean
	@Exposed
	public WebResourcePackageManager entityWebResourcePackageManager() {
		WebResourcePackageManager packageManager = new WebResourcePackageManager();
		packageManager.register( EntityModuleWebResources.NAME, new EntityModuleWebResources() );
		packageManager.register( AdminWebWebResources.NAME, new AdminWebWebResources( webAppPathResolver ) );
		packageManager.register( JQueryWebResources.NAME, new JQueryWebResources( false ) );
		packageManager.register( BootstrapUiWebResources.NAME, new BootstrapUiWebResources( false ) );
		packageManager.register( BootstrapUiFormElementsWebResources.NAME, new BootstrapUiFormElementsWebResources( false ) );
		return packageManager;
	}

	@Autowired
	void setWebAppPathResolver( WebAppPathResolver webAppPathResolver ) {
		this.webAppPathResolver = webAppPathResolver;
	}

	@Autowired
	void setAcrossContextInfo( AcrossContextInfo acrossContextInfo ) {
		this.acrossContextInfo = acrossContextInfo;
	}
}
