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
import com.foreach.across.test.AcrossTestConfiguration;
import com.foreach.across.test.AcrossWebAppConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Arne Vandamme
 */
@RunWith(SpringJUnit4ClassRunner.class)
@AcrossWebAppConfiguration
@TestPropertySource(properties = {
		"admin-web-module.root-path=/",
		"across.web.resources.path=/static-resources"
})
public class ITAdminWebOnRootPath
{
	@Autowired
	private MockMvc mvc;

	@Test
	public void staticResourcesShouldNotBeSecured() throws Exception {
		mvc.perform( get( "/static-resources/static/adminweb/css/adminweb.css" ) )
		   .andExpect( status().isOk() );
	}

	@Test
	public void securedAdminControllerShouldRedirectToLogin() throws Exception {
		mvc.perform( get( "/somePage" ) )
		   .andExpect( redirectedUrl( "http://localhost/login" ) );
	}

	@Test
	public void loginPageShouldBeUnsecured() throws Exception {
		mvc.perform( get( "/login" ) )
		   .andExpect( status().isOk() );
	}

	@AcrossTestConfiguration(modules = AdminWebModule.NAME)
	protected static class Config
	{
	}
}
