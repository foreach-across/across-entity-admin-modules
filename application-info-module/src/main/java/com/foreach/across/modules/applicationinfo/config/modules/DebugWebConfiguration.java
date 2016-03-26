package com.foreach.across.modules.applicationinfo.config.modules;

import com.foreach.across.core.annotations.AcrossDepends;
import com.foreach.across.core.annotations.AcrossEventHandler;
import com.foreach.across.core.annotations.Event;
import com.foreach.across.core.events.AcrossModuleBootstrappedEvent;
import com.foreach.across.modules.applicationinfo.controllers.ApplicationInfoController;
import com.foreach.across.modules.applicationinfo.controllers.rest.ApplicationInfoRestController;
import com.foreach.across.modules.debugweb.DebugWebModuleSettings;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Replace the default controller for debugweb.
 */
@AcrossDepends(required = "DebugWebModule")
@Configuration
@AcrossEventHandler
public class DebugWebConfiguration
{
	private static final Logger LOG = LoggerFactory.getLogger( DebugWebConfiguration.class );

	@Event
	protected void registerDebugDashboard( AcrossModuleBootstrappedEvent moduleBootstrappedEvent ) {
		if ( StringUtils.equals( "DebugWebModule", moduleBootstrappedEvent.getModule().getName() ) ) {
			ApplicationContext ctx = moduleBootstrappedEvent.getModule().getApplicationContext();
			DebugWebModuleSettings debugWebModuleSettings = ctx.getBean( DebugWebModuleSettings.class );

			if ( debugWebModuleSettings != null
					&& StringUtils.equals( debugWebModuleSettings.getDashboard(), "/" ) ) {
				LOG.trace( "Registering debug dashboard to application info controller" );
				debugWebModuleSettings.setDashboard( ApplicationInfoController.PATH );
			}
		}
	}

	@Bean
	public ApplicationInfoController applicationInfoController() {
		return new ApplicationInfoController();
	}

	@Bean
	public ApplicationInfoRestController applicationInfoRestController() {
		return new ApplicationInfoRestController();
	}
}
