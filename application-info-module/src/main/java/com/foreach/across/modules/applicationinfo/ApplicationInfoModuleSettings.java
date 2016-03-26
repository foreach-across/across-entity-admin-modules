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
package com.foreach.across.modules.applicationinfo;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Date;

/**
 * @author Arne Vandamme
 */
@ConfigurationProperties(prefix = "applicationInfo")
public class ApplicationInfoModuleSettings
{
	public static final String UNKNOWN_VALUE = "unknown";

	public static final String ENVIRONMENT_ID = "applicationInfo.environmentId";
	public static final String ENVIRONMENT_NAME = "applicationInfo.environmentName";
	public static final String APPLICATION_ID = "applicationInfo.applicationId";
	public static final String APPLICATION_NAME = "applicationInfo.applicationName";
	public static final String BUILD_ID = "applicationInfo.buildId";
	public static final String BUILD_DATE = "applicationInfo.buildDate";
	public static final String HOSTNAME = "applicationInfo.hostName";
	public static final String STARTUP_DATE = "applicationInfo.startupDate";

	/**
	 * Internal id of the application
	 */
	private String applicationId = UNKNOWN_VALUE;

	/**
	 * Descriptive name of the application.
	 */
	private String applicationName;

	/**
	 * Internal id of the environment the application is running in.
	 */
	private String environmentId = UNKNOWN_VALUE;

	/**
	 * Descriptive name of the environment the application is running in.
	 */
	private String environmentName;

	/**
	 * Id of the running build of the application.
	 */
	private String buildId = UNKNOWN_VALUE;

	/**
	 * Date when this build was created.
	 */
	private Date buildDate;

	/**
	 * Name for the infrastructure hosting the application.
	 */
	private String hostName;

	/**
	 * Timestamp when the application should be considered started.
	 */
	private Date startupDate;

	public String getApplicationId() {
		return applicationId;
	}

	public void setApplicationId( String applicationId ) {
		this.applicationId = applicationId;
	}

	public String getApplicationName() {
		return applicationName;
	}

	public void setApplicationName( String applicationName ) {
		this.applicationName = applicationName;
	}

	public String getEnvironmentId() {
		return environmentId;
	}

	public void setEnvironmentId( String environmentId ) {
		this.environmentId = environmentId;
	}

	public String getEnvironmentName() {
		return environmentName;
	}

	public void setEnvironmentName( String environmentName ) {
		this.environmentName = environmentName;
	}

	public String getBuildId() {
		return buildId;
	}

	public void setBuildId( String buildId ) {
		this.buildId = buildId;
	}

	public Date getBuildDate() {
		return buildDate;
	}

	public void setBuildDate( Date buildDate ) {
		this.buildDate = buildDate;
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName( String hostName ) {
		this.hostName = hostName;
	}

	public Date getStartupDate() {
		return startupDate;
	}

	public void setStartupDate( Date startupDate ) {
		this.startupDate = startupDate;
	}
}
