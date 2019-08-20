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

package com.foreach.across.modules.adminweb.resource;

import com.foreach.across.modules.adminweb.AdminWebModule;
import com.foreach.across.modules.bootstrapui.elements.icons.IconSet;
import com.foreach.across.modules.bootstrapui.elements.icons.IconSetRegistry;
import com.foreach.across.modules.bootstrapui.elements.icons.SimpleIconSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import static com.foreach.across.modules.bootstrapui.config.FontAwesomeIconSetConfiguration.FONT_AWESOME_SOLID_ICON_SET;
import static com.foreach.across.modules.bootstrapui.elements.icons.IconSet.iconSet;

@Configuration
public class AdminWebIcons
{
	public static final String DEVELOPER_TOOLS = "developer-tools";
	public static final String ADMINISTRATION_HOME = "administration-home";
	public static final String USER_CONTEXT_MENU = "user-context-menu";

	@Autowired
	public void registerAdminWebIconSet() {
		SimpleIconSet adminWebIconSet = new SimpleIconSet();
		adminWebIconSet.add( DEVELOPER_TOOLS, ( imageName ) -> iconSet( FONT_AWESOME_SOLID_ICON_SET ).icon( "wrench" ) );
		adminWebIconSet.add( ADMINISTRATION_HOME, ( imageName ) -> iconSet( FONT_AWESOME_SOLID_ICON_SET ).icon( "home" ) );
		adminWebIconSet.add( USER_CONTEXT_MENU, ( imageName ) -> iconSet( FONT_AWESOME_SOLID_ICON_SET ).icon( "user" ) );

		IconSetRegistry.addIconSet( AdminWebModule.NAME, adminWebIconSet );
	}
}
