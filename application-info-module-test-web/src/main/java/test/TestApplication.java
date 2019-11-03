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

package test;

import com.foreach.across.AcrossApplicationRunner;
import com.foreach.across.config.AcrossApplication;
import com.foreach.across.modules.adminweb.AdminWebModule;
import com.foreach.across.modules.applicationinfo.ApplicationInfoModule;
import com.foreach.across.modules.applicationinfo.ApplicationInfoModuleSettings;
import com.foreach.across.modules.debugweb.DebugWebModule;
import org.springframework.context.annotation.Bean;

/**
 * @author Arne Vandamme
 * @since 2.0.0
 */
@AcrossApplication(modules = { AdminWebModule.NAME, DebugWebModule.NAME })
public class TestApplication
{
	@Bean
	ApplicationInfoModule applicationInfoModule() {
		ApplicationInfoModule applicationInfoModule = new ApplicationInfoModule();
		applicationInfoModule.setProperty( ApplicationInfoModuleSettings.APPLICATION_NAME, "Test website" );
		applicationInfoModule.setProperty( ApplicationInfoModuleSettings.APPLICATION_ID, "test-website" );
		applicationInfoModule.setProperty( ApplicationInfoModuleSettings.BUILD_ID, "123" );

		return applicationInfoModule;
	}

	public static void main( String[] args ) {
		AcrossApplicationRunner.run( TestApplication.class );
	}
}
