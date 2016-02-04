package com.foreach.across.modules.applicationinfo.config;

import com.foreach.across.core.annotations.AcrossEventHandler;
import com.foreach.across.core.annotations.Event;
import com.foreach.across.core.annotations.Exposed;
import com.foreach.across.core.events.AcrossContextBootstrappedEvent;
import com.foreach.across.modules.applicationinfo.ApplicationInfoModule;
import com.foreach.across.modules.applicationinfo.ApplicationInfoModuleSettings;
import com.foreach.across.modules.applicationinfo.business.AcrossApplicationInfoImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;

@Configuration
@AcrossEventHandler
public class ApplicationInfoConfiguration
{
	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	private ApplicationInfoModule applicationInfoModule;

	@Autowired
	private ApplicationInfoModuleSettings settings;

	@Bean
	@Exposed
	public AcrossApplicationInfoImpl runningApplicationInfo() {
		AcrossApplicationInfoImpl applicationInfo = new AcrossApplicationInfoImpl();

		applicationInfo.setApplicationName( settings.getProperty( ApplicationInfoModuleSettings.APPLICATION_NAME ) );
		applicationInfo.setApplicationId( settings.getProperty( ApplicationInfoModuleSettings.APPLICATION_ID ) );
		applicationInfo.setEnvironmentName( settings.getProperty( ApplicationInfoModuleSettings.ENVIRONMENT_NAME ) );
		applicationInfo.setEnvironmentId( settings.getProperty( ApplicationInfoModuleSettings.ENVIRONMENT_ID ) );

		applicationInfo.setBuildId( settings.getProperty( ApplicationInfoModuleSettings.BUILD_ID ) );
		applicationInfo.setBuildDate( settings.getProperty( ApplicationInfoModuleSettings.BUILD_DATE, Date.class ) );
		applicationInfo.setHostName( determineHostName() );

		applicationInfo.setStartupDate( determineStartupDate() );
		applicationInfo.setBootstrapStartDate( new Date( applicationContext.getParent().getStartupDate() ) );

		return applicationInfo;
	}

	@Event
	protected void bootstrapFinished( AcrossContextBootstrappedEvent event ) {
		runningApplicationInfo().setBootstrapEndDate( new Date() );
	}

	private Date determineStartupDate() {
		Date startupDate = settings.getProperty( ApplicationInfoModuleSettings.STARTUP_DATE, Date.class );

		if ( startupDate == null ) {
			startupDate = applicationInfoModule.getConfigurationDate();
		}

		return startupDate;
	}

	private String determineHostName() {
		String hostName = settings.getProperty( ApplicationInfoModuleSettings.HOSTNAME );

		if ( hostName == null ) {
			hostName = getHostNameFromServer();
		}

		return hostName != null ? hostName : ApplicationInfoModuleSettings.UNKNOWN_VALUE;
	}

	public String getHostNameFromServer() {
		try {
			String result = InetAddress.getLocalHost().getHostName();
			if ( StringUtils.isNotEmpty( result ) ) {
				return result;
			}
		}
		catch ( UnknownHostException e ) {
			// failed;  try alternate means.
		}

		// try environment properties.
		String host = System.getenv( "COMPUTERNAME" );
		if ( host != null ) {
			return host;
		}
		host = System.getenv( "HOSTNAME" );
		if ( host != null ) {
			return host;
		}

		// undetermined.
		return null;
	}
}
