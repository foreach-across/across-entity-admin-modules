/*
 * Copyright 2019 the original author or authors
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

package com.foreach.across.modules.bootstrapui;

import com.foreach.across.core.AcrossModule;
import com.foreach.across.core.annotations.AcrossDepends;
import com.foreach.across.core.context.bootstrap.AcrossBootstrapConfig;
import com.foreach.across.core.context.bootstrap.ModuleBootstrapConfig;
import com.foreach.across.core.context.configurer.ApplicationContextConfigurer;
import com.foreach.across.core.context.configurer.ComponentScanConfigurer;
import com.foreach.across.modules.web.AcrossWebModule;

import java.util.Set;

@AcrossDepends(required = AcrossWebModule.NAME)
public class BootstrapUiModule extends AcrossModule
{
	public static final String NAME = "BootstrapUiModule";

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public String getDescription() {
		return "Extension module: extends AcrossWebModule with infrastructure for building a Bootstrap based web interface.";
	}

	@Override
	protected void registerDefaultApplicationContextConfigurers( Set<ApplicationContextConfigurer> contextConfigurers ) {
		// only extend AcrossWebModule
	}

	@Override
	public void prepareForBootstrap( ModuleBootstrapConfig currentModule, AcrossBootstrapConfig contextConfig ) {
		contextConfig.extendModule( AcrossWebModule.NAME, new ComponentScanConfigurer( getClass().getPackage().getName() + ".config" ) );
	}
}
