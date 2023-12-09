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
package com.foreach.across.modules.adminweb.events;

import com.foreach.across.core.events.AcrossEvent;
import com.foreach.across.modules.adminweb.AdminWeb;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;

public class AdminWebUrlRegistry implements AcrossEvent
{
	private final AdminWeb adminWeb;
	private final ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry registry;

	public AdminWebUrlRegistry( AdminWeb adminWeb,
	                            ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry registry ) {
		this.adminWeb = adminWeb;
		this.registry = registry;
	}

	public ExpressionUrlAuthorizationConfigurer.AuthorizedUrl match( String... antPatterns ) {
		String[] prefixed = new String[antPatterns.length];

		for ( int i = 0; i < antPatterns.length; i++ ) {
			prefixed[i] = adminWeb.path( antPatterns[i] );
		}

		return registry.antMatchers( prefixed );
	}

	public ExpressionUrlAuthorizationConfigurer.AuthorizedUrl match( HttpMethod httpMethod, String... antPatterns ) {
		String[] prefixed = new String[antPatterns.length];

		for ( int i = 0; i < antPatterns.length; i++ ) {
			prefixed[i] = adminWeb.path( antPatterns[i] );
		}

		return registry.antMatchers( httpMethod, prefixed );
	}

	public ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry getRegistry() {
		return registry;
	}
}
