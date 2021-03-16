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

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Contains parts of the {@link org.springframework.web.servlet.DispatcherServlet} to assist in resolving
 * the actual {@link HandlerMethod} or {@link HandlerMapping} for the current request.
 * </p>
 * Used as a component which caches internal state.
 */
@Slf4j
@UtilityClass
public class EntityViewControllerHandlerResolver
{
	public static final String CURRENT_HANDLER_METHOD_ATTRIBUTE = "currentHandlerMethod";
	public static final HandlerInterceptor HANDLER_INTERCEPTOR = new HandlerInterceptor()
	{
		@Override
		public boolean preHandle( HttpServletRequest request, HttpServletResponse response, Object handler ) {
			request.setAttribute( CURRENT_HANDLER_METHOD_ATTRIBUTE, handler );
			return true;
		}
	};

	HandlerMethod currentHandlerMethod() {
		try {
			return (HandlerMethod) getRequest().getAttribute( CURRENT_HANDLER_METHOD_ATTRIBUTE );
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

	private HttpServletRequest getRequest() {
		RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
		if ( requestAttributes instanceof ServletRequestAttributes ) {
			return ( (ServletRequestAttributes) requestAttributes ).getRequest();
		}
		throw new IllegalStateException( "No instance of ServletRequest available within the current request context." );
	}
}
