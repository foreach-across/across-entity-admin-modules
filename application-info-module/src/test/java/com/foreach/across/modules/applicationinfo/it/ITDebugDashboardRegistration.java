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
package com.foreach.across.modules.applicationinfo.it;

import com.foreach.across.modules.applicationinfo.ApplicationInfoModule;
import com.foreach.across.modules.applicationinfo.controllers.ApplicationInfoController;
import com.foreach.across.modules.debugweb.DebugWebModule;
import com.foreach.across.modules.debugweb.DebugWebModuleSettings;
import com.foreach.across.test.AcrossTestWebContext;
import com.foreach.across.test.support.AcrossTestWebContextBuilder;
import org.junit.Test;

import static com.foreach.across.test.support.AcrossTestBuilders.web;
import static org.junit.Assert.assertEquals;

/**
 * @author Arne Vandamme
 */
public class ITDebugDashboardRegistration
{
	@Test
	public void debugDashboardSetByApplicationInfoIfNoneConfigured() {
		assertDashboardPath(
				ApplicationInfoController.PATH,
				web( false ).modules( ApplicationInfoModule.NAME, DebugWebModule.NAME )
		);
	}

	@Test
	public void explicitDebugDashboardOnParentIsKept() {
		assertDashboardPath(
				"/custom",
				web( false )
						.property( DebugWebModuleSettings.DASHBOARD_PATH, "/custom" )
						.modules( ApplicationInfoModule.NAME, DebugWebModule.NAME )
		);
	}

	@Test
	public void explicitDebugDashboardOnDebugWebModuleDirectlyIsKept() {
		DebugWebModule debugWebModule = new DebugWebModule();
		debugWebModule.setProperty( DebugWebModuleSettings.DASHBOARD_PATH, "/other/custom" );

		assertDashboardPath(
				"/other/custom",
				web( false )
						.modules( ApplicationInfoModule.NAME )
						.modules( debugWebModule )
		);
	}

	private void assertDashboardPath( String expectedPath, AcrossTestWebContextBuilder builder ) {
		try (AcrossTestWebContext ctx = builder.build()) {
			DebugWebModuleSettings settings = ctx.getBeanOfTypeFromModule( "DebugWebModule",
			                                                               DebugWebModuleSettings.class );

			assertEquals( expectedPath, settings.getDashboard() );
		}
	}
}
