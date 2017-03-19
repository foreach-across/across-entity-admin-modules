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
package com.foreach.across.modules.entity.config.modules;

import com.foreach.across.core.annotations.AcrossDepends;
import com.foreach.across.modules.entity.EntityModuleSettings;
import com.foreach.across.modules.entity.annotations.EntityValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.data.web.ProxyingHandlerMethodArgumentResolver;
import org.springframework.data.web.SortHandlerMethodArgumentResolver;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.data.web.config.HateoasAwareSpringDataWebConfiguration;
import org.springframework.data.web.config.SpringDataWebConfiguration;
import org.springframework.util.ClassUtils;
import org.springframework.validation.SmartValidator;
import org.springframework.validation.Validator;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Arne Vandamme
 */
@AcrossDepends(required = "AcrossWebModule")
@Configuration
public class AcrossWebConfiguration extends WebMvcConfigurerAdapter
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

	private static final String CLASS_THYMELEAF_TEMPLATE_ENGINE = "org.thymeleaf.spring4.SpringTemplateEngine";

	private static final Logger LOG = LoggerFactory.getLogger( AcrossWebConfiguration.class );

	@EntityValidator
	@SuppressWarnings({ "unused", "SpringJavaAutowiringInspection" })
	private SmartValidator entityValidator;

	@Autowired
	private EntityModuleSettings settings;

	@Autowired
	private ApplicationContext applicationContext;

	@PostConstruct
	public void registerThymeleaf() {
		// todo cleanup
//		if ( shouldRegisterThymeleafDialect() ) {
//			LOG.debug( "Registering Thymeleaf entity module dialect" );
//
//			Object springTemplateEngine = applicationContext.getBean( "springTemplateEngine" );
//
//			if ( springTemplateEngine instanceof SpringTemplateEngine ) {
//				( (SpringTemplateEngine) springTemplateEngine ).addDialect( new EntityModuleDialect() );
//				LOG.debug( "Thymeleaf entity module dialect registered successfully." );
//			}
//			else {
//				LOG.warn(
//						"Unable to register Thymeleaf entity module dialect as bean springTemplateEngine is not of the right type." );
//			}
//		}
	}

	@Deprecated
	@Autowired(required = false)
	public void registerFallbackSort( SortHandlerMethodArgumentResolver sortHandlerMethodArgumentResolver ) {
		if ( sortHandlerMethodArgumentResolver != null ) {
			// Must return an empty Sort instance or a null-pointer will occur in case of paging without Sort
			sortHandlerMethodArgumentResolver.setFallbackSort( emptySort() );
		}
		else {
			LOG.warn( "No SortHandlerMethodArgumentResolver found - paging in EntityModule might not work.  " +
					          "EntityModule expects an empty Sort to be supported on default Pageable." );
		}
	}

	@Deprecated
	@Autowired(required = false)
	public void registerFallbackPageable( PageableHandlerMethodArgumentResolver pageableHandlerMethodArgumentResolver ) {
		if ( pageableHandlerMethodArgumentResolver != null ) {
			pageableHandlerMethodArgumentResolver.setFallbackPageable( null );
		}
		else {
			LOG.warn( "No PageableHandlerMethodArgumentResolver found - paging in EntityModule might not work.  " +
					          "EntityModule expects a null Pageable to be set as fallback." );
		}
	}

	/**
	 * Register the entity validator as the default web mvc validator if necessary.
	 */
	@Override
	public Validator getValidator() {
		if ( entityValidator != null && settings.isEntityValidatorRegisterForMvc() ) {
			return entityValidator;
		}

		return null;
	}

	private boolean shouldRegisterThymeleafDialect() {
		if ( applicationContext.containsBean( "springTemplateEngine" ) ) {
			ClassLoader threadClassLoader = Thread.currentThread().getContextClassLoader();

			if ( ClassUtils.isPresent( CLASS_THYMELEAF_TEMPLATE_ENGINE, threadClassLoader ) ) {
				return true;
			}

		}

		return false;
	}

	/**
	 * Create an empty sort instance.
	 *
	 * @return empty sort
	 */
	public static Sort emptySort() {
		List<Sort.Order> orders = new ArrayList<>( 1 );
		orders.add( new Sort.Order( "name" ) );
		Sort s = new Sort( orders );
		orders.clear();
		return s;
	}
}
