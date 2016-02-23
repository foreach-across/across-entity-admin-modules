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
package com.foreach.across.modules.adminweb;

import com.foreach.across.core.AcrossModuleSettings;
import com.foreach.across.core.AcrossModuleSettingsRegistry;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("adminWebModule")
//@SuppressWarnings("unused")
public class AdminWebModuleSettings extends AcrossModuleSettings
{
	/**
	 * Key used for creating the remember me cookie.  If this property is not present, remember me will not be enabled.
	 */
	public static final String REMEMBER_ME_KEY = "adminWebModule.login.rememberMe.key";
	public static final String REMEMBER_ME_COOKIE = "adminWebModule.login.rememberMe.cookie";
	public static final String REMEMBER_ME_TOKEN_VALIDITY_SECONDS =
			"adminWebModule.login.rememberMe.tokenValiditySeconds";

	public static final String TITLE = "adminWebModule.title";
	public static final String LOCALE_DEFAULT = "adminWebModule.locale.default";
	public static final String LOCALE_OPTIONS = "adminWebModule.locale.options";

	public static final String ADMIN_ACCESS_PERMISSIONS = "adminWebModule.access-permissions";
	public static final String DASHBOARD_PATH = "adminWebModule.dashboard";
	public static final String LOGIN_TEMPLATE = "adminWebModule.login.template";

	private String title;
	private String dashboardPath = null;

	@Override
	protected void registerSettings( AcrossModuleSettingsRegistry registry ) {

		registry.register( TITLE, String.class, null,
		                   "Name of the application to be shown on the default admin web layout in the top left corner." );
		registry.register( ADMIN_ACCESS_PERMISSIONS, String[].class, new String[] { "access administration" },
		                   "List of permissions that grant access to the administration interface." );
		registry.register( DASHBOARD_PATH, String.class, null,
		                   "Relative path (within admin web) for the landing page of admin web." );
		registry.register( LOGIN_TEMPLATE, String.class, null, "Custom template for the login page." );
	}

	public String getTitle() {
		if ( title == null ) {
			return getProperty( TITLE );
		}

		return title;
	}

	public void setTitle( String title ) {
		this.title = title;
	}

	public String[] getAccessPermissions() {
		return getProperty( ADMIN_ACCESS_PERMISSIONS, String[].class );
	}

	public String getDashboardPath() {
		if ( dashboardPath == null ) {
			String path = getProperty( DASHBOARD_PATH );

			return StringUtils.isEmpty( path ) ? "/" : path;
		}

		return dashboardPath;
	}

	/**
	 * Allows changing the admin web dashboard path at runtime.
	 *
	 * @param dashboardPath Path within the admin web context to the dashboard.
	 */
	public void setDashboardPath( String dashboardPath ) {
		this.dashboardPath = dashboardPath;
	}

	public String getLoginTemplate() {
		return getProperty( LOGIN_TEMPLATE );
	}
}
