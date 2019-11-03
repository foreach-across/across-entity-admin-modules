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

package com.foreach.across.modules.applicationinfo.extensions;

import com.foreach.across.core.annotations.Exposed;
import com.foreach.across.core.annotations.ModuleConfiguration;
import com.foreach.across.core.context.bootstrap.AcrossBootstrapConfigurer;
import com.foreach.across.core.events.AcrossContextBootstrappedEvent;
import com.foreach.across.modules.applicationinfo.ApplicationInfoModuleSettings;
import com.foreach.across.modules.applicationinfo.business.AcrossApplicationInfoImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;

/**
 * Configures the actual application info beans.
 *
 * @author Arne Vandamme
 * @since 2.0.0
 */
@ModuleConfiguration(AcrossBootstrapConfigurer.CONTEXT_INFRASTRUCTURE_MODULE)
class AcrossContextInfrastructureExtension
{
	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	private ApplicationInfoModuleSettings settings;

	@Bean
	public ApplicationInfoModuleSettings applicationInfoModuleSettings() {
		return new ApplicationInfoModuleSettings();
	}

	@Bean
	@Exposed
	public AcrossApplicationInfoImpl runningApplicationInfo() {
		AcrossApplicationInfoImpl applicationInfo = new AcrossApplicationInfoImpl();

		applicationInfo.setApplicationName( settings.getApplicationName() );
		applicationInfo.setApplicationId( settings.getApplicationId() );
		applicationInfo.setEnvironmentName( settings.getEnvironmentName() );
		applicationInfo.setEnvironmentId( settings.getEnvironmentId() );

		applicationInfo.setBuildId( settings.getBuildId() );
		applicationInfo.setBuildDate( settings.getBuildDate() );
		applicationInfo.setHostName( determineHostName() );

		applicationInfo.setStartupDate( determineStartupDate() );
		applicationInfo.setBootstrapStartDate( new Date( applicationContext.getParent().getStartupDate() ) );

		return applicationInfo;
	}

	@EventListener
	public void bootstrapFinished( AcrossContextBootstrappedEvent event ) {
		runningApplicationInfo().setBootstrapEndDate( new Date() );
	}

	private Date determineStartupDate() {
		Date startupDate = settings.getStartupDate();

		if ( startupDate == null ) {
			startupDate = new Date( applicationContext.getParent().getStartupDate() );
		}

		return startupDate;
	}

	private String determineHostName() {
		String hostName = settings.getHostName();

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
