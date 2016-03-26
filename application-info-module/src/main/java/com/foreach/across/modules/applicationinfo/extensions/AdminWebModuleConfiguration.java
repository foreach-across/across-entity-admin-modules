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

import com.foreach.across.core.annotations.ModuleConfiguration;
import com.foreach.across.modules.adminweb.AdminWebModuleSettings;
import com.foreach.across.modules.adminweb.config.RememberMeProperties;
import com.foreach.common.spring.context.ApplicationInfo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Set default settings on AdminWebModule based on configured application info.
 */
@ModuleConfiguration("AdminWebModule")
public class AdminWebModuleConfiguration
{
	private static final Logger LOG = LoggerFactory.getLogger( AdminWebModuleConfiguration.class );

	@Autowired
	public void registerAdminWebTitle( AdminWebModuleSettings adminWebModuleSettings,
	                                   ApplicationInfo applicationInfo ) {
		if ( adminWebModuleSettings.getTitle() == null ) {
			LOG.trace( "Registering application name as AdminWeb title" );
			adminWebModuleSettings.setTitle( applicationInfo.getApplicationName() );
		}
	}

	@Autowired
	public void registerRememberMeCookie( RememberMeProperties rememberMeProperties,
	                                      ApplicationInfo applicationInfo ) {
		if ( StringUtils.equals( RememberMeProperties.DEFAULT_COOKIE, rememberMeProperties.getCookie() ) ) {
			LOG.trace( "Registering application id as name for the AdminWeb remember me cookie" );
			rememberMeProperties.setCookie(
					StringUtils.deleteWhitespace( "rm-admin-" + applicationInfo.getApplicationId() )
			);
		}
	}
}
