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
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

import static com.foreach.across.test.support.AcrossTestBuilders.web;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Arne Vandamme
 * @since 1.1.1
 */
public class ITAccessPermissions
{
	@Test
	public void defaultAccessPermissions() throws Exception {
		try (
				AcrossTestWebContext ctx = web()
						.modules( AdminWebModule.NAME )
						.build()
		) {
			MockMvc mvc = ctx.mockMvc();

			assertAllowed( mvc, "access administration" );
			assertNotAllowed( mvc, "custom permission" );
		}
	}

	@Test
	public void customAccessPermissions() throws Exception {
		try (
				AcrossTestWebContext ctx = web()
						.property( AdminWebModuleSettings.ADMIN_ACCESS_PERMISSIONS, "custom permission" )
						.modules( AdminWebModule.NAME )
						.build()
		) {
			MockMvc mvc = ctx.mockMvc();

			assertNotAllowed( mvc, "access administration" );
			assertAllowed( mvc, "custom permission" );
		}
	}

	@Test
	public void combinedAccessPermissions() throws Exception {
		try (
				AcrossTestWebContext ctx = web()
						.property( AdminWebModuleSettings.ADMIN_ACCESS_PERMISSIONS,
						           "access administration,custom permission" )
						.modules( AdminWebModule.NAME )
						.build()
		) {
			MockMvc mvc = ctx.mockMvc();

			assertAllowed( mvc, "access administration" );
			assertAllowed( mvc, "custom permission" );
		}
	}

	private void assertAllowed( MockMvc mvc, String permission ) throws Exception {
		mvc.perform( get( "/admin/" ).with( user( "user" ).authorities( new SimpleGrantedAuthority( permission ) ) ) )
		   .andExpect( status().isOk() );
	}

	private void assertNotAllowed( MockMvc mvc, String permission ) throws Exception {
		mvc.perform( get( "/admin/" ).with( user( "user" ).authorities( new SimpleGrantedAuthority( permission ) ) ) )
		   .andExpect( status().isForbidden() );
	}
}
