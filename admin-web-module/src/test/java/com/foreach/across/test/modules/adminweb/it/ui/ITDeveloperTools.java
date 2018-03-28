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

package com.foreach.across.test.modules.adminweb.it.ui;

import com.foreach.across.core.EmptyAcrossModule;
import com.foreach.across.modules.adminweb.AdminWebModule;
import com.foreach.across.modules.adminweb.annotations.AdminWebController;
import com.foreach.across.modules.adminweb.menu.AdminMenu;
import com.foreach.across.modules.web.menu.Menu;
import com.foreach.across.modules.web.menu.MenuSelector;
import com.foreach.across.test.AcrossTestWebContext;
import org.junit.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import static com.foreach.across.test.support.AcrossTestBuilders.web;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Arne Vandamme
 * @since 2.0.0
 */
public class ITDeveloperTools
{
	@Test
	public void noDeveloperToolsInNonDevMode() throws Exception {
		try (AcrossTestWebContext ctx = web( false )
				.modules( AdminWebModule.NAME )
				.modules( new EmptyAcrossModule( "Test", DeveloperToolsController.class ) )
				.build()) {
			ctx.mockMvc()
			   .perform(
					   get( "/admin/devtools" ).with(
							   user( "user" ).authorities( new SimpleGrantedAuthority( "access administration" ) )
					   )
			   )
			   .andExpect( status().isOk() )
			   .andExpect( content().string( "notFound" ) );
		}
	}

	@Test
	public void developerToolsInDevMode() throws Exception {
		try (AcrossTestWebContext ctx = web( false )
				.developmentMode( true )
				.modules( AdminWebModule.NAME )
				.modules( new EmptyAcrossModule( "Test", DeveloperToolsController.class ) )
				.build()) {
			ctx.mockMvc()
			   .perform(
					   get( "/admin/devtools" ).with(
							   user( "user" ).authorities( new SimpleGrantedAuthority( "access administration" ) )
					   )
			   )
			   .andExpect( status().isOk() )
			   .andExpect( content().string( "/ax/developer" ) );
		}
	}

	@AdminWebController
	public static class DeveloperToolsController
	{
		@ResponseBody
		@GetMapping("/devtools")
		public String developerToolsMenuItemFound( AdminMenu adminMenu ) {
			Menu menu = adminMenu.getItem( MenuSelector.byTitle( "Developer tools" ) );
			return menu == null ? "notFound" : menu.getPath();
		}
	}
}
