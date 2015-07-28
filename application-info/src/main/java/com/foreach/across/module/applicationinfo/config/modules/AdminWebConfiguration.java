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
package com.foreach.across.module.applicationinfo.config.modules;

import com.foreach.across.core.annotations.AcrossDepends;
import com.foreach.across.core.annotations.AcrossEventHandler;
import com.foreach.across.core.annotations.Event;
import com.foreach.across.core.context.info.AcrossModuleInfo;
import com.foreach.across.core.events.AcrossModuleBeforeBootstrapEvent;
import com.foreach.across.module.applicationinfo.ApplicationInfoModuleSettings;
import com.foreach.across.modules.adminweb.AdminWebModule;
import com.foreach.across.modules.adminweb.AdminWebModuleSettings;
import com.foreach.common.spring.context.ApplicationInfo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * Set default settings on AdminWebModule based on configured application info.
 */
@AcrossDepends(required = "AdminWebModule")
@Configuration
@AcrossEventHandler
public class AdminWebConfiguration
{
	private static Logger LOG = LoggerFactory.getLogger( AdminWebConfiguration.class );

	@Autowired
	private ApplicationInfo runningApplicationInfo;

	@Autowired
	private ApplicationInfoModuleSettings settings;

	@Event
	protected void registerAdminWebSettings( AcrossModuleBeforeBootstrapEvent moduleBeforeBootstrapEvent ) {
		AcrossModuleInfo moduleInfo = moduleBeforeBootstrapEvent.getModule();

		if ( AdminWebModule.NAME.equals( moduleInfo.getName() ) ) {
			Properties adminWebProperties = moduleInfo.getModule().getProperties();

			if ( !settings.containsProperty( AdminWebModuleSettings.TITLE )
					&& !adminWebProperties.containsKey( AdminWebModuleSettings.TITLE ) ) {
				LOG.trace( "Registering application name as AdminWeb title" );
				adminWebProperties.setProperty( AdminWebModuleSettings.TITLE,
				                                runningApplicationInfo.getApplicationName() );
			}
			if ( !settings.containsProperty( AdminWebModuleSettings.REMEMBER_ME_COOKIE )
					&& !adminWebProperties.containsKey( AdminWebModuleSettings.REMEMBER_ME_COOKIE ) ) {
				LOG.trace( "Registering application id as name for the AdminWeb remember me cookie" );
				adminWebProperties.setProperty(
						AdminWebModuleSettings.REMEMBER_ME_COOKIE,
						StringUtils.deleteWhitespace( "rm-admin-" + runningApplicationInfo.getApplicationId() )
				);
			}

		}
	}
}
