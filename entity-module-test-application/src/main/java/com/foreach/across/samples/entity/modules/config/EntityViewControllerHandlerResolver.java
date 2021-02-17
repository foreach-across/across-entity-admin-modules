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

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Contains parts of the {@link org.springframework.web.servlet.DispatcherServlet} to assist in resolving
 * the actual {@link HandlerMethod} or {@link HandlerMapping} for the current request.
 * </p>
 * Used as a component which caches internal state.
 */
@Slf4j
@RequestScope(proxyMode = ScopedProxyMode.TARGET_CLASS)
@Component
public class EntityViewControllerHandlerResolver
{
	private List<HandlerMapping> handlerMappings;
	private List<HandlerAdapter> handlerAdapters;

	HandlerMethod currentHandlerMethod() {
		try {
			HandlerExecutionChain handler = getHandler( getRequest() );
			HandlerAdapter handlerAdapter = getHandlerAdapter( handler.getHandler() );
			if ( handlerAdapter != null ) {
				return ( (HandlerMethod) handler.getHandler() );
			}
		}
		catch ( Exception e ) {
			// todo: break? log? ignore? defaults?
			LOG.warn( "eh", e );
		}
		return null;
	}

	Class<?> currentHandlerClass() {
		return currentHandlerMethod().getMethod().getDeclaringClass();
	}

	boolean isEntityViewControllerSupportHandler() {
		return EntityViewControllerSupport.class.isAssignableFrom( currentHandlerClass() );
	}

	/**
	 * Return the HandlerExecutionChain for this request.
	 * <p>Tries all handler mappings in order.
	 *
	 * @param request current HTTP request
	 * @return the HandlerExecutionChain, or {@code null} if no handler could be found
	 */
	@Nullable
	private HandlerExecutionChain getHandler( HttpServletRequest request ) throws Exception {
		if ( getHandlerMappings() != null ) {
			for ( HandlerMapping mapping : getHandlerMappings() ) {
				HandlerExecutionChain handler = mapping.getHandler( request );
				if ( handler != null ) {
					return handler;
				}
			}
		}
		return null;
	}

	/**
	 * Return the HandlerAdapter for this handler object.
	 *
	 * @param handler the handler object to find an adapter for
	 * @throws ServletException if no HandlerAdapter can be found for the handler. This is a fatal error.
	 */
	private HandlerAdapter getHandlerAdapter( Object handler ) throws ServletException {
		if ( getHandlerAdapters() != null ) {
			for ( HandlerAdapter adapter : getHandlerAdapters() ) {
				if ( adapter.supports( handler ) ) {
					return adapter;
				}
			}
		}
		throw new ServletException( "No adapter for handler [" + handler +
				                            "]: The DispatcherServlet configuration needs to include a HandlerAdapter that supports this handler" );
	}

	private List<HandlerMapping> getHandlerMappings() {
		if ( handlerMappings == null ) {
			initHandlerMappings( WebApplicationContextUtils.getWebApplicationContext( getRequest().getServletContext() ) );
		}
		return handlerMappings;
	}

	private void initHandlerMappings( ListableBeanFactory ctx ) {
		Map<String, HandlerMapping> handlerMappingMap = BeanFactoryUtils.beansOfTypeIncludingAncestors( ctx, HandlerMapping.class, true, false );
		handlerMappings = new ArrayList<>( handlerMappingMap.values() );
		AnnotationAwareOrderComparator.sort( handlerMappings );
	}

	private List<HandlerAdapter> getHandlerAdapters() {
		if ( handlerAdapters == null ) {
			initHandlerAdapters( WebApplicationContextUtils.getWebApplicationContext( getRequest().getServletContext() ) );
		}
		return handlerAdapters;
	}

	private void initHandlerAdapters( ListableBeanFactory ctx ) {
		Map<String, HandlerAdapter> matchingBeans = BeanFactoryUtils.beansOfTypeIncludingAncestors( ctx, HandlerAdapter.class, true, false );
		handlerAdapters = new ArrayList<>( matchingBeans.values() );
		AnnotationAwareOrderComparator.sort( handlerAdapters );
	}

	private HttpServletRequest getRequest() {
		RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
		if ( requestAttributes instanceof ServletRequestAttributes ) {
			return ( (ServletRequestAttributes) requestAttributes ).getRequest();
		}
		throw new IllegalStateException( "No instance of ServletRequest available within the current request context." );
	}
}
