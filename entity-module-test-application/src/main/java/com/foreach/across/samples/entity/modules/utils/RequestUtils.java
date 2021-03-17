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

package com.foreach.across.samples.entity.modules.utils;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.Optional;

public class RequestUtils
{
	public static boolean isHttpServletRequest() {
		return RequestContextHolder.currentRequestAttributes() instanceof ServletRequestAttributes;
	}

	public static HttpServletRequest getCurrentRequest() {
		return ( (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes() ).getRequest();
	}

	public static HttpServletResponse getCurrentResponse() {
		return ( (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes() ).getResponse();
	}

	public static Optional<Object> getPathVariable( String name ) {
		@SuppressWarnings("unchecked")
		Map<String, String> attribute = (Map<String, String>) getCurrentRequest().getAttribute( HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE );
		if ( attribute != null ) {
			return Optional.ofNullable( attribute.get( name ) );
		}
		return Optional.empty();
	}
}
