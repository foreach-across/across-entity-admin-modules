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

package com.foreach.across.modules.entity.config;

import com.foreach.across.modules.bootstrapui.elements.icons.IconSet;
import com.foreach.across.modules.bootstrapui.elements.icons.IconSetRegistry;
import com.foreach.across.modules.bootstrapui.elements.icons.SimpleIconSet;
import com.foreach.across.modules.entity.EntityModule;
import com.foreach.across.modules.entity.conditionals.ConditionalOnBootstrapUI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import static com.foreach.across.modules.bootstrapui.config.FontAwesomeIconSetConfiguration.FONT_AWESOME_SOLID_ICON_SET;

@Configuration
@ConditionalOnBootstrapUI
public class EntityModuleIcons
{
	public static final String ADVANCED_SETTINGS = "advanced-settings";
	public static final String PREVIOUS_PAGE = "previous-page";
	public static final String NEXT_PAGE = "next-page";
	public static final String DELETE = "delete";
	public static final String DELETE_BUTTON = "delete-button";
	public static final String ENTITY_QUERY_SEARCH = "entity-query-search";
	public static final String DETAIL = "detail";
	public static final String EDIT = "edit";
	public static final String EMBEDDED_COLLECTION_ITEM_HANDLE = "embedded-collection-item-handle";
	public static final String EMBEDDED_COLLECTION_ITEM_ADD = "embedded-collection-item-add";
	public static final String EMBEDDED_COLLECTION_ITEM_DELETE = "embedded-collection-item-delete";
	public static final String EXTENSION_NAVIGATION = "extension-navigation";
	public static final String NEW_WINDOW = "new-window";

	@Autowired
	public void registerAdminWebIconSet() {
		SimpleIconSet adminWebIconSet = new SimpleIconSet();
		adminWebIconSet.add( ADVANCED_SETTINGS, ( imageName ) -> IconSet.iconSet( FONT_AWESOME_SOLID_ICON_SET ).icon( "cog" ) );
		adminWebIconSet.add( PREVIOUS_PAGE, ( imageName ) -> IconSet.iconSet( FONT_AWESOME_SOLID_ICON_SET ).icon( "step-backward" ) );
		adminWebIconSet.add( NEXT_PAGE, ( imageName ) -> IconSet.iconSet( FONT_AWESOME_SOLID_ICON_SET ).icon( "step-forward" ) );
		adminWebIconSet.add( DELETE, ( imageName ) -> IconSet.iconSet( FONT_AWESOME_SOLID_ICON_SET ).icon( "trash" ) );
		adminWebIconSet.add( DELETE_BUTTON, ( imageName ) -> IconSet.iconSet( FONT_AWESOME_SOLID_ICON_SET ).icon( "trash" ) );
		adminWebIconSet.add( ENTITY_QUERY_SEARCH, ( imageName ) -> IconSet.iconSet( FONT_AWESOME_SOLID_ICON_SET ).icon( "search" ) );
		adminWebIconSet.add( DETAIL, ( imageName ) -> IconSet.iconSet( FONT_AWESOME_SOLID_ICON_SET ).icon( "eye" ) );
		adminWebIconSet.add( EDIT, ( imageName ) -> IconSet.iconSet( FONT_AWESOME_SOLID_ICON_SET ).icon( "times" ) );
		adminWebIconSet.add( EMBEDDED_COLLECTION_ITEM_HANDLE, ( imageName ) -> IconSet.iconSet( FONT_AWESOME_SOLID_ICON_SET ).icon( "bars" ) );
		adminWebIconSet.add( EMBEDDED_COLLECTION_ITEM_ADD, ( imageName ) -> IconSet.iconSet( FONT_AWESOME_SOLID_ICON_SET ).icon( "plus" ) );
		adminWebIconSet.add( EMBEDDED_COLLECTION_ITEM_DELETE, ( imageName ) -> IconSet.iconSet( FONT_AWESOME_SOLID_ICON_SET ).icon( "times" ) );
		adminWebIconSet.add( EXTENSION_NAVIGATION, ( imageName ) -> IconSet.iconSet( FONT_AWESOME_SOLID_ICON_SET ).icon( "external-link-alt" ) );

		IconSetRegistry.addIconSet( EntityModule.NAME, adminWebIconSet );
	}
}
