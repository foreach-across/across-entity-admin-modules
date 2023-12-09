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

package com.foreach.across.modules.adminweb;

import com.foreach.across.modules.bootstrapui.BootstrapUiModuleIcons;
import com.foreach.across.modules.bootstrapui.elements.icons.IconSetRegistry;
import com.foreach.across.modules.bootstrapui.elements.icons.SimpleIconSet;

import static com.foreach.across.modules.bootstrapui.elements.icons.IconSet.iconSet;

public class AdminWebModuleIcons
{
	public static final String ICON_SET = AdminWebModule.NAME;

	public static final String DEVELOPER_TOOLS = "developer-tools";
	public static final String ADMINISTRATION_HOME = "administration-home";
	public static final String USER_CONTEXT_MENU = "user-context-menu";

	public static void registerIconSet() {
		SimpleIconSet adminWebIconSet = new SimpleIconSet();
		adminWebIconSet.add( DEVELOPER_TOOLS, ( imageName ) -> iconSet( BootstrapUiModuleIcons.ICON_SET_FONT_AWESOME_SOLID ).icon( "wrench" ) );
		adminWebIconSet.add( ADMINISTRATION_HOME, ( imageName ) -> iconSet( BootstrapUiModuleIcons.ICON_SET_FONT_AWESOME_SOLID ).icon( "home" ) );
		adminWebIconSet.add( USER_CONTEXT_MENU, ( imageName ) -> iconSet( BootstrapUiModuleIcons.ICON_SET_FONT_AWESOME_SOLID ).icon( "user" ) );

		IconSetRegistry.addIconSet( ICON_SET, adminWebIconSet );
	}
}
