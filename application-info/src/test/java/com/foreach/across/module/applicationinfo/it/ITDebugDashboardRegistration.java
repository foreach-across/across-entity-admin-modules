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
package com.foreach.across.module.applicationinfo.it;

import com.foreach.across.config.AcrossContextConfigurer;
import com.foreach.across.core.AcrossContext;
import com.foreach.across.core.installers.InstallerAction;
import com.foreach.across.module.applicationinfo.ApplicationInfoModule;
import com.foreach.across.module.applicationinfo.controllers.ApplicationInfoController;
import com.foreach.across.modules.debugweb.DebugWebModule;
import com.foreach.across.modules.debugweb.DebugWebModuleSettings;
import com.foreach.across.test.AcrossTestWebContext;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Arne Vandamme
 */
public class ITDebugDashboardRegistration
{
	@Test
	public void debugWebBeforeApplicationInfo() {
		AcrossContextConfigurer configurer = new AcrossContextConfigurer()
		{
			@Override
			public void configure( AcrossContext acrossContext ) {
				acrossContext.setInstallerAction( InstallerAction.DISABLED );
				acrossContext.addModule( new DebugWebModule() );
				acrossContext.addModule( new ApplicationInfoModule() );
			}
		};

		assertDashboardPath( ApplicationInfoController.PATH, configurer );
	}

	@Test
	public void debugWebAfterApplicationInfo() {
		AcrossContextConfigurer configurer = new AcrossContextConfigurer()
		{
			@Override
			public void configure( AcrossContext acrossContext ) {
				acrossContext.setInstallerAction( InstallerAction.DISABLED );
				acrossContext.addModule( new ApplicationInfoModule() );
				acrossContext.addModule( new DebugWebModule() );
			}
		};

		assertDashboardPath( ApplicationInfoController.PATH, configurer );
	}

	@Test
	public void debugWebCustomDashboardOnParent() {
		AcrossContextConfigurer configurer = new AcrossContextConfigurer()
		{
			@Override
			public void configure( AcrossContext acrossContext ) {
				acrossContext.setInstallerAction( InstallerAction.DISABLED );
				acrossContext.setProperty( DebugWebModuleSettings.DASHBOARD_PATH, "/custom" );
				acrossContext.addModule( new ApplicationInfoModule() );
				acrossContext.addModule( new DebugWebModule() );
			}
		};

		assertDashboardPath( "/custom", configurer );
	}

	@Test
	public void debugWebCustomDashboardOnDebugWebModule() {
		AcrossContextConfigurer configurer = new AcrossContextConfigurer()
		{
			@Override
			public void configure( AcrossContext acrossContext ) {
				acrossContext.setInstallerAction( InstallerAction.DISABLED );

				DebugWebModule debugWebModule = new DebugWebModule();
				debugWebModule.setProperty( DebugWebModuleSettings.DASHBOARD_PATH, "/other/custom" );
				acrossContext.addModule( debugWebModule );

				acrossContext.addModule( new ApplicationInfoModule() );

			}
		};

		assertDashboardPath( "/other/custom", configurer );
	}

	private void assertDashboardPath( String expectedPath, AcrossContextConfigurer configurer ) {
		try (AcrossTestWebContext ctx = new AcrossTestWebContext( configurer )) {
			DebugWebModuleSettings settings = ctx.beanRegistry().getBeanOfTypeFromModule( "DebugWebModule",
			                                                                              DebugWebModuleSettings.class );

			assertEquals( expectedPath, settings.getDashboardPath() );
		}
	}
}
