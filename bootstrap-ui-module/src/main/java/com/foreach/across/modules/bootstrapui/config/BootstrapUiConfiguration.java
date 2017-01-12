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

package com.foreach.across.modules.bootstrapui.config;

import com.foreach.across.core.annotations.PostRefresh;
import com.foreach.across.core.context.registry.AcrossContextBeanRegistry;
import com.foreach.across.modules.bootstrapui.components.BootstrapUiComponentFactory;
import com.foreach.across.modules.bootstrapui.elements.BootstrapUiFactory;
import com.foreach.across.modules.bootstrapui.elements.BootstrapUiFactoryImpl;
import com.foreach.across.modules.bootstrapui.resource.BootstrapUiFormElementsWebResources;
import com.foreach.across.modules.bootstrapui.resource.BootstrapUiWebResources;
import com.foreach.across.modules.bootstrapui.resource.JQueryWebResources;
import com.foreach.across.modules.web.resource.WebResourcePackageManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author Arne Vandamme
 */
@Configuration
@ComponentScan(basePackageClasses = BootstrapUiComponentFactory.class)
public class BootstrapUiConfiguration
{
	@Autowired
	private AcrossContextBeanRegistry contextBeanRegistry;

	@PostRefresh
	protected void registerWebResourcePackages() {
		contextBeanRegistry.getBeansOfType( WebResourcePackageManager.class ).forEach(
				packageManager -> {
					packageManager.register( BootstrapUiFormElementsWebResources.NAME,
					                         new BootstrapUiFormElementsWebResources() );
					packageManager.register( JQueryWebResources.NAME, new JQueryWebResources( true ) );
					packageManager.register( BootstrapUiWebResources.NAME, new BootstrapUiWebResources() );
				}
		);
	}

	@Bean
	public BootstrapUiFactory bootstrapUiFactory() {
		return new BootstrapUiFactoryImpl();
	}
}
