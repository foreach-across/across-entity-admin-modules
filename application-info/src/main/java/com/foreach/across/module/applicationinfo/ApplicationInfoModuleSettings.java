package com.foreach.across.module.applicationinfo;

import com.foreach.across.core.AcrossModuleSettings;
import com.foreach.across.core.AcrossModuleSettingsRegistry;

import java.util.Date;

public class ApplicationInfoModuleSettings extends AcrossModuleSettings
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

	@Override
	protected void registerSettings( AcrossModuleSettingsRegistry registry ) {
		registry.register( APPLICATION_ID, String.class, UNKNOWN_VALUE,
		                   "Internal id of the application." );
		registry.register( APPLICATION_NAME, String.class, null,
		                   "Descriptive name of the application." );
		registry.register( ENVIRONMENT_ID, String.class, UNKNOWN_VALUE,
		                   "Internal id of the environment the application is running in." );
		registry.register( ENVIRONMENT_NAME, String.class, null,
		                   "Descriptive name of the environment the application is running in." );
		registry.register( BUILD_ID, String.class, UNKNOWN_VALUE,
		                   "Id of the running build of the application." );
		registry.register( BUILD_DATE, Date.class, null,
		                   "Date when this build was created." );
		registry.register( HOSTNAME, String.class, null,
		                   "Name for the infrastructure hosting the application." );
		registry.register( STARTUP_DATE, Date.class, null,
		                   "Timestamp when the application should be considered started." );
	}
}
