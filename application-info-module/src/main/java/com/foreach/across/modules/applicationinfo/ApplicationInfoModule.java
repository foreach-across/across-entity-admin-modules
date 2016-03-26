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
import org.springframework.core.Ordered;

import java.util.Date;

@AcrossRole(value = AcrossModuleRole.INFRASTRUCTURE, order = Ordered.HIGHEST_PRECEDENCE)
public class ApplicationInfoModule extends AcrossModule
{
	public final static String NAME = "ApplicationInfoModule";

	private final Date configurationDate;

	public ApplicationInfoModule() {
		configurationDate = new Date();
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public String getDescription() {
		return "Provides support for configuring both the running application and synchronizing remote application information.";
	}

	/**
	 * If no startup date is specified, the configuration timestamp for the module will be considered
	 * the initial startup date.
	 *
	 * @return Timestamp when the module was configured.
	 */
	@SuppressWarnings("all")
	public Date getConfigurationDate() {
		return configurationDate;
	}
}
