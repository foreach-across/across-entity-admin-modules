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
import com.foreach.across.modules.bootstrapui.elements.icons.IconSet;
import com.foreach.across.modules.bootstrapui.resource.BootstrapUiFormElementsWebResources;
import com.foreach.across.modules.bootstrapui.resource.BootstrapUiWebResources;
import com.foreach.across.modules.bootstrapui.resource.JQueryWebResources;
import com.foreach.across.modules.web.resource.WebResourcePackageManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Arne Vandamme
 */
@ExtendWith(SpringExtension.class)
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
			assertThat( mgr.getPackage( BootstrapUiWebResources.NAME ) ).isNotNull();
			assertThat( mgr.getPackage( JQueryWebResources.NAME ) ).isNotNull();
			assertThat( mgr.getPackage( BootstrapUiFormElementsWebResources.NAME ) ).isNotNull();
		} );
	}

	@Test
	public void iconSetsShouldBeAvailable() {
		assertThat( IconSet.iconSet( BootstrapUiModuleIcons.ICON_SET_FONT_AWESOME_BRANDS ).icon( "500px" ) ).isNotNull();
		assertThat( IconSet.iconSet( BootstrapUiModuleIcons.ICON_SET_FONT_AWESOME_REGULAR ).icon( "angry" ) ).isNotNull();
		assertThat( IconSet.iconSet( BootstrapUiModuleIcons.ICON_SET_FONT_AWESOME_SOLID ).icon( "ad" ) ).isNotNull();
	}

	@Configuration
	@EnableAcrossContext(BootstrapUiModule.NAME)
	protected static class Config
	{
	}
}
