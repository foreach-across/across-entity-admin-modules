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
package com.foreach.across.modules.applicationinfo;

import com.foreach.across.core.AcrossModule;
import com.foreach.across.core.annotations.AcrossRole;
import com.foreach.across.core.context.AcrossModuleRole;
import com.foreach.across.core.context.bootstrap.AcrossBootstrapConfig;
import com.foreach.across.core.context.bootstrap.AcrossBootstrapConfigurer;
import com.foreach.across.core.context.bootstrap.ModuleBootstrapConfig;
import com.foreach.across.core.context.configurer.ApplicationContextConfigurer;
import com.foreach.across.core.context.configurer.PropertySourcesConfigurer;
import org.springframework.core.Ordered;

import java.util.Set;

@AcrossRole(value = AcrossModuleRole.INFRASTRUCTURE, order = Ordered.HIGHEST_PRECEDENCE)
public class ApplicationInfoModule extends AcrossModule
{
	public final static String NAME = "ApplicationInfoModule";

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public String getDescription() {
		return "Extension module: adds support for basic application information.";
	}

	@Override
	protected void registerDefaultApplicationContextConfigurers( Set<ApplicationContextConfigurer> contextConfigurers ) {
		// don't bootstrap anything yourself
	}

	@Override
	public void prepareForBootstrap( ModuleBootstrapConfig currentModule, AcrossBootstrapConfig contextConfig ) {
		// attach property sources to the context infrastructure module
		contextConfig.extendModule(
				AcrossBootstrapConfigurer.CONTEXT_INFRASTRUCTURE_MODULE,
				currentModule.getApplicationContextConfigurers()
				             .stream()
				             .filter( PropertySourcesConfigurer.class::isInstance )
				             .toArray( ApplicationContextConfigurer[]::new )
		);
	}
}
