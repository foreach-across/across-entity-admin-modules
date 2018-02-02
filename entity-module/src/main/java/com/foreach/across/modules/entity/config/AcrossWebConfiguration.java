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

package com.foreach.across.modules.entity.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.data.web.ProxyingHandlerMethodArgumentResolver;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.data.web.config.HateoasAwareSpringDataWebConfiguration;
import org.springframework.data.web.config.SpringDataWebConfiguration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Arne Vandamme
 */
@Configuration
class AcrossWebConfiguration
{
	/**
	 * Activate Spring Data web support if not yet activated on the parent application context.
	 */
	@ConditionalOnMissingBean({ SpringDataWebConfiguration.class, HateoasAwareSpringDataWebConfiguration.class })
	@Configuration
	@EnableSpringDataWebSupport
	public static class SpringDataWebSupportConfiguration
	{
	}

	/***
	 * This is a workaround for https://jira.spring.io/browse/DATACMNS-776
	 */
	@ConditionalOnClass(ProxyingHandlerMethodArgumentResolver.class)
	@Order(Ordered.LOWEST_PRECEDENCE)
	@Configuration
	public static class ProxyingHandlerWebMvcConfiguration extends WebMvcConfigurerAdapter
	{
		@Override
		public void addArgumentResolvers( List<HandlerMethodArgumentResolver> argumentResolvers ) {
			List<HandlerMethodArgumentResolver> proxyingHandlerMethodArgumentResolvers = new ArrayList<>();
			for ( Iterator<HandlerMethodArgumentResolver> it = argumentResolvers.iterator(); it.hasNext(); ) {
				HandlerMethodArgumentResolver argumentResolver = it.next();
				if ( argumentResolver instanceof ProxyingHandlerMethodArgumentResolver ) {
					it.remove();
					proxyingHandlerMethodArgumentResolvers.add( argumentResolver );
				}
			}
			argumentResolvers.addAll( proxyingHandlerMethodArgumentResolvers );
		}
	}
}
