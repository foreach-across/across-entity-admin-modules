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

package admin.application.controllers;

import com.foreach.across.core.annotations.ConditionalOnDevelopmentMode;
import com.foreach.across.core.annotations.Event;
import com.foreach.across.modules.adminweb.annotations.AdminWebController;
import com.foreach.across.modules.adminweb.config.DeveloperToolsMenuRegistrar;
import com.foreach.across.modules.adminweb.menu.AdminMenuEvent;
import com.foreach.across.modules.adminweb.ui.PageContentStructure;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author Arne Vandamme
 * @since 2.0.0
 */
@AdminWebController
@ConditionalOnDevelopmentMode
@RequiredArgsConstructor
public class DevToolsController
{
	private final PageContentStructure pageContentStructure;

	@Event
	void registerDeveloperToolsItem( AdminMenuEvent menuEvent ) {
		menuEvent.builder().item( DeveloperToolsMenuRegistrar.PATH + "/test", "Test controller" );
	}

	@GetMapping("/ax/developer/test")
	public String test() {
		pageContentStructure.setPageTitle( "Test developer tools page..." );
		return PageContentStructure.TEMPLATE;
	}
}
