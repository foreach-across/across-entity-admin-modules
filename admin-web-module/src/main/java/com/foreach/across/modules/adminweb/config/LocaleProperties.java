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

package com.foreach.across.modules.adminweb.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * @author Arne Vandamme
 */
@Component
@ConfigurationProperties(prefix = "adminWebModule.locale")
public class LocaleProperties
{
	/**
	 * Default locale that should explicitly be set when accessing the administration interface if no specific locale selected.
	 */
	private Locale defaultLocale = Locale.UK;

	/**
	 * List of locales that can be selected on the login page.
	 */
	private List<Locale> options = Collections.emptyList();

	public Locale getDefaultLocale() {
		return defaultLocale;
	}

	public void setDefaultLocale( Locale defaultLocale ) {
		this.defaultLocale = defaultLocale;
	}

	public List<Locale> getOptions() {
		return options;
	}

	public void setOptions( List<Locale> options ) {
		this.options = options;
	}
}
