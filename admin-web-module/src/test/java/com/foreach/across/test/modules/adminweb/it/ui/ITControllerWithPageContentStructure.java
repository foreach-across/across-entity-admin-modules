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

import com.foreach.across.modules.adminweb.AdminWebModule;
import com.foreach.across.modules.adminweb.annotations.AdminWebController;
import com.foreach.across.modules.adminweb.ui.PageContentStructure;
import com.foreach.across.modules.web.ui.elements.TextViewElement;
import com.foreach.across.test.AcrossTestConfiguration;
import com.foreach.across.test.AcrossWebAppConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Arne Vandamme
 * @since 2.0.0
 */
@ExtendWith(SpringExtension.class)
@AcrossWebAppConfiguration
public class ITControllerWithPageContentStructure
{
	@Autowired
	private MockMvc mockMvc;

	@Test
	public void defaultPageContentStructure() throws Exception {
		getUrl( "/admin/pcs/default" )
				.andExpect( status().isOk() )
				.andExpect(
						content().string( containsString( "<header class=\"pcs-header\">default page</header>" ) ) );

		// Call twice to ensure request-bound
		getUrl( "/admin/pcs/default" )
				.andExpect( status().isOk() )
				.andExpect(
						content().string( containsString( "<header class=\"pcs-header\">default page</header>" ) ) );
	}

	@Test
	public void customPageContentStructure() throws Exception {
		getUrl( "/admin/pcs/custom" )
				.andExpect( status().isOk() )
				.andExpect( content().string( containsString( "<header class=\"pcs-header\">custom page</header>" ) ) );
	}

	private ResultActions getUrl( String path ) throws Exception {
		return mockMvc.perform( get( path ).with(
				user( "user" ).authorities( new SimpleGrantedAuthority( "access administration" ) ) )
		);
	}

	@AcrossTestConfiguration(modules = AdminWebModule.NAME)
	protected static class Config
	{
		@Lazy
		@AdminWebController
		@RequestMapping("/pcs")
		public static class PageContentStructureController
		{
			@Autowired
			private PageContentStructure page;

			@RequestMapping("/default")
			public String defaultPageContentStructure() {
				page.addToHeader( TextViewElement.text( "default page" ) );
				return PageContentStructure.TEMPLATE;
			}

			@RequestMapping("/custom")
			public String customPageContentStructure( @ModelAttribute PageContentStructure customPage ) {
				defaultPageContentStructure();
				assertNotSame( customPage, page );
				customPage.addToHeader( TextViewElement.text( "custom page" ) );
				return PageContentStructure.TEMPLATE;
			}
		}
	}
}
