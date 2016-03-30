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

package com.foreach.across.test.modules.adminweb.it;

import com.foreach.across.modules.adminweb.AdminWebModule;
import com.foreach.across.modules.adminweb.AdminWebModuleSettings;
import com.foreach.across.test.AcrossTestWebContext;
import org.junit.Test;
import org.springframework.test.web.servlet.MockMvc;

import static com.foreach.across.test.support.AcrossTestBuilders.web;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

/**
 * @author Arne Vandamme
 * @since 1.1.1
 */
public class ITLoginPages
{
	@Test
	public void defaultLoginPage() throws Exception {
		try (AcrossTestWebContext ctx = web().modules( AdminWebModule.NAME ).build()) {
			MockMvc mvc = ctx.mockMvc();

			mvc.perform( get( "/admin/login" ) )
			   .andExpect( content().string( containsString( "loginForm" ) ) )
			   .andExpect( content().string( not( containsString( "Custom login page." ) ) ) );
		}
	}

	@Test
	public void customLoginPage() throws Exception {
		try (
				AcrossTestWebContext ctx = web()
						.property( AdminWebModuleSettings.LOGIN_TEMPLATE, "th/custom-login" )
						.modules( AdminWebModule.NAME )
						.build()
		) {
			MockMvc mvc = ctx.mockMvc();

			mvc.perform( get( "/admin/login" ) )
			   .andExpect( content().string( not( containsString( "loginForm" ) ) ) )
			   .andExpect( content().string( containsString( "Custom login page." ) ) );
		}
	}
}
