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

import com.foreach.across.config.EnableAcrossContext;
import com.foreach.across.modules.bootstrapui.resource.BootstrapUiFormElementsWebResources;
import com.foreach.across.modules.bootstrapui.resource.BootstrapUiWebResources;
import com.foreach.across.modules.bootstrapui.resource.JQueryWebResources;
import com.foreach.across.modules.web.resource.WebResourcePackageManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.Collection;

import static org.junit.Assert.assertNotNull;

/**
 * @author Arne Vandamme
 */
@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext
@WebAppConfiguration
@ContextConfiguration
public class ITBootstrapUiModule
{
	@Autowired
	private Collection<WebResourcePackageManager> packageManagers;

	@Test
	public void webResourcesShouldBeRegistered() {
		packageManagers.forEach( mgr -> {
			assertNotNull( mgr.getPackage( BootstrapUiWebResources.NAME ) );
			assertNotNull( mgr.getPackage( JQueryWebResources.NAME ) );
			assertNotNull( mgr.getPackage( BootstrapUiFormElementsWebResources.NAME ) );
		} );
	}

	@Configuration
	@EnableAcrossContext(BootstrapUiModule.NAME)
	protected static class Config
	{
	}
}
