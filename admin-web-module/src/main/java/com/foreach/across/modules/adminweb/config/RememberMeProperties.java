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

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Configuration options for enabling remember me functionality.
 *
 * @author Arne Vandamme
 */
@Component
@ConfigurationProperties(prefix = "adminWebModule.login.rememberMe")
public class RememberMeProperties
{
	public static final String DEFAULT_COOKIE = "rm-admin-web";

	/**
	 * Should remember me be enabled (will only be the case if a key is also configured).
	 */
	private boolean enabled = true;

	/**
	 * Key used for creating the remember me cookie.
	 */
	private String key;

	/**
	 * Number of seconds a remember me token should be valid. Defaults to 30 days.
	 */
	private String cookie = DEFAULT_COOKIE;

	/**
	 * Number of seconds a remember me token should be valid. Defaults to 30 days.
	 */
	private int tokenValiditySeconds = 2592000;

	public boolean isEnabled() {
		return enabled && !StringUtils.isBlank( key );
	}

	public void setEnabled( boolean enabled ) {
		this.enabled = enabled;
	}

	public String getKey() {
		return key;
	}

	public void setKey( String key ) {
		this.key = key;
	}

	public String getCookie() {
		return cookie;
	}

	public void setCookie( String cookie ) {
		this.cookie = cookie;
	}

	public int getTokenValiditySeconds() {
		return tokenValiditySeconds;
	}

	public void setTokenValiditySeconds( int tokenValiditySeconds ) {
		this.tokenValiditySeconds = tokenValiditySeconds;
	}
}
