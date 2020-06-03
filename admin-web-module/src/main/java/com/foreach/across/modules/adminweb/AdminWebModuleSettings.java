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

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("admin-web-module")
@SuppressWarnings("unused")
public class AdminWebModuleSettings
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

	/**
	 * Root path for all admin web controllers.  All mappings will be relative to this path.
	 */
	private String rootPath = "/admin";

	/**
	 * Name of the application to be shown in the administration UI.
	 */
	private String title;

	/**
	 * Relative path (within admin web) for the landing page of admin web.
	 */
	private String dashboard = "/";

	/**
	 * Set of permissions that grant access to the administration interface.
	 */
	private String[] accessPermissions = new String[] { "access administration" };

	public String getRootPath() {
		return rootPath;
	}

	public void setRootPath( String rootPath ) {
		this.rootPath = rootPath;
	}

	public String getDashboard() {
		return dashboard;
	}

	public void setDashboard( String dashboard ) {
		this.dashboard = dashboard;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle( String title ) {
		this.title = title;
	}

	public String[] getAccessPermissions() {
		return accessPermissions.clone();
	}

	public void setAccessPermissions( String[] accessPermissions ) {
		this.accessPermissions = accessPermissions.clone();
	}
}
