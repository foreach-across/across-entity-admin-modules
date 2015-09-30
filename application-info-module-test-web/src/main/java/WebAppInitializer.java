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

import com.foreach.across.config.AcrossContextConfigurer;
import com.foreach.across.config.EnableAcrossContext;
import com.foreach.across.core.AcrossContext;
import com.foreach.across.core.AcrossModule;
import com.foreach.across.module.applicationinfo.ApplicationInfoModule;
import com.foreach.across.module.applicationinfo.ApplicationInfoModuleSettings;
import com.foreach.across.modules.adminweb.AdminWebModule;
import com.foreach.across.modules.debugweb.DebugWebModule;
import com.foreach.across.modules.spring.security.SpringSecurityModule;
import com.foreach.across.modules.web.AcrossWebModule;
import com.foreach.across.modules.web.context.AcrossWebApplicationContext;
import com.foreach.across.modules.web.servlet.AbstractAcrossServletInitializer;
import org.springframework.context.annotation.Configuration;

/**
 * @author Arne Vandamme
 */
public class WebAppInitializer extends AbstractAcrossServletInitializer
{
	@Override
	protected void configure( AcrossWebApplicationContext applicationContext ) {
		applicationContext.register( WebAppConfiguration.class );
	}

	@Configuration
	@EnableAcrossContext
	protected static class WebAppConfiguration implements AcrossContextConfigurer
	{
		@Override
		public void configure( AcrossContext context ) {
			context.addModule( acrossWebModule() );
			context.addModule( debugWebModule() );
			context.addModule( adminWebModule() );
			context.addModule( springSecurityModule() );
			context.addModule( applicationInfoModule() );
		}

		private SpringSecurityModule springSecurityModule() {
			return new SpringSecurityModule();
		}

		private ApplicationInfoModule applicationInfoModule() {
			ApplicationInfoModule applicationInfoModule = new ApplicationInfoModule();
			applicationInfoModule.setProperty( ApplicationInfoModuleSettings.APPLICATION_NAME, "Test website" );

			return applicationInfoModule;
		}

		private AcrossWebModule acrossWebModule() {
			return new AcrossWebModule();
		}

		private DebugWebModule debugWebModule() {
			DebugWebModule debugWebModule = new DebugWebModule();
			debugWebModule.setRootPath( "/debug" );

			return debugWebModule;
		}

		private AdminWebModule adminWebModule() {
			AdminWebModule adminWebModule = new AdminWebModule();
			adminWebModule.setRootPath( "/secure" );

			return adminWebModule;
		}
	}
}
