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

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Date;

/**
 * @author Arne Vandamme
 */
@SuppressWarnings("all")
@ConfigurationProperties(prefix = "application-info")
@Getter
@Setter
public class ApplicationInfoModuleSettings
{
	public static final String UNKNOWN_VALUE = "unknown";

	public static final String ENVIRONMENT_ID = "application-info.environment-id";
	public static final String ENVIRONMENT_NAME = "application-info.environment-name";
	public static final String APPLICATION_ID = "application-info.application-id";
	public static final String APPLICATION_NAME = "application-info.application-name";
	public static final String BUILD_ID = "application-info.build-id";
	public static final String BUILD_DATE = "application-info.build-date";
	public static final String HOSTNAME = "application-info.host-name";
	public static final String STARTUP_DATE = "application-info.startup-date";

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
}
