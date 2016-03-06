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
import com.foreach.across.core.events.AcrossEventPublisher;
import com.foreach.across.modules.adminweb.AdminWeb;
import com.foreach.across.modules.adminweb.AdminWebModuleSettings;
import com.foreach.across.modules.adminweb.events.AdminWebUrlRegistry;
import com.foreach.across.modules.spring.security.configuration.SpringSecurityWebConfigurerAdapter;
import com.foreach.across.modules.spring.security.filters.LocaleChangeFilter;
import com.foreach.across.modules.web.AcrossWebModule;
import com.foreach.across.modules.web.config.resources.ResourceConfigurationProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.rememberme.RememberMeAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.LocaleResolver;

@Configuration
public class AdminWebSecurityConfiguration extends SpringSecurityWebConfigurerAdapter
{
	private static final Logger LOG = LoggerFactory.getLogger( AdminWebSecurityConfiguration.class );

	@Autowired
	private AcrossEventPublisher publisher;

	@Autowired
	private AdminWeb adminWeb;

	@Autowired
	private AdminWebModuleSettings settings;

	@Autowired
	private RememberMeProperties rememberMeProperties;

	@Autowired(required = false)
	@Qualifier(DispatcherServlet.LOCALE_RESOLVER_BEAN_NAME)
	private LocaleResolver localeResolver;

	@Override
	@SuppressWarnings("SignatureDeclareThrowsException")
	public void configure( HttpSecurity root ) throws Exception {
		HttpSecurity http = root.antMatcher( adminWeb.path( "/**" ) );

		// Allow locale to be changed before security applied
		if ( localeResolver != null ) {
			http.addFilterBefore( new LocaleChangeFilter( localeResolver ), SecurityContextPersistenceFilter.class );
		}

		ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry urlRegistry =
				http.authorizeRequests();

		publisher.publish( new AdminWebUrlRegistry( adminWeb, urlRegistry ) );

		// Only users with any of the configured admin permissions can login
		urlRegistry.anyRequest().hasAnyAuthority( settings.getAccessPermissions() )
		           .and().formLogin().defaultSuccessUrl( adminWeb.path( "/" ) ).loginPage( adminWeb.path( "/login" ) )
		           .permitAll()
		           .and().logout().permitAll();

		configureRememberMe( http );
		customizeAdminWebSecurity( http );
	}

	@SuppressWarnings("SignatureDeclareThrowsException")
	protected void configureRememberMe( HttpSecurity http ) throws Exception {
		if ( rememberMeProperties.isEnabled() ) {
			String rememberMeKey = rememberMeProperties.getKey();
			int rememberMeValiditySeconds = rememberMeProperties.getTokenValiditySeconds();

			http.rememberMe()
			    .key( rememberMeKey )
			    .tokenValiditySeconds( rememberMeValiditySeconds )
			    .addObjectPostProcessor( new ObjectPostProcessor<RememberMeAuthenticationFilter>()
			    {
				    @Override
				    public RememberMeAuthenticationFilter postProcess( RememberMeAuthenticationFilter object ) {
					    RememberMeServices rememberMeServices = object.getRememberMeServices();

					    if ( rememberMeServices instanceof TokenBasedRememberMeServices ) {
						    String cookieName = rememberMeProperties.getCookie();
						    LOG.debug( "Configuring adminWeb remember me cookie name: {}", cookieName );

						    ( (TokenBasedRememberMeServices) rememberMeServices ).setCookieName( cookieName );
					    }

					    return object;
				    }
			    } );
		}
	}

	/**
	 * Adapter method to customize admin security.
	 *
	 * @param http security element scoped for adminweb urls
	 */
	@SuppressWarnings("all")
	protected void customizeAdminWebSecurity( HttpSecurity http ) throws Exception {
	}

	/**
	 * If the admin web is linked to the root path of the web context, it will also secure all static resources.
	 * Add an additional configuration before admin web that ensures that static resources are served without security.
	 * This configuration is ordered at the default (0) position, and will come before any default ordered module
	 * configurations.
	 */
	@ConditionalOnProperty(prefix = "adminWebModule", name = "root-path", havingValue = "/")
	@Order(0)
	@Configuration
	public static class AllowStaticResourcesSecurityConfiguration extends SpringSecurityWebConfigurerAdapter
	{
		@Autowired
		private AcrossContextBeanRegistry beanRegistry;

		@Override
		public void configure( HttpSecurity http ) throws Exception {
			ResourceConfigurationProperties resourceConfigurationProperties
					= beanRegistry.getBeanOfTypeFromModule( AcrossWebModule.NAME,
					                                        ResourceConfigurationProperties.class );

			http.antMatcher( resourceConfigurationProperties.getPath() + "/**" )
			    .authorizeRequests().anyRequest().permitAll();
		}
	}
}
