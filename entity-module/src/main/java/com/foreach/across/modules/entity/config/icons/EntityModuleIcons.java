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

package com.foreach.across.modules.entity.config.icons;

import com.foreach.across.modules.bootstrapui.elements.icons.IconSet;
import com.foreach.across.modules.bootstrapui.elements.icons.IconSetRegistry;
import com.foreach.across.modules.bootstrapui.elements.icons.SimpleIconSet;
import com.foreach.across.modules.entity.EntityModule;
import com.foreach.across.modules.entity.conditionals.ConditionalOnBootstrapUI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import static com.foreach.across.modules.bootstrapui.config.FontAwesomeIconSetConfiguration.FONT_AWESOME_SOLID_ICON_SET;
import static com.foreach.across.modules.bootstrapui.styles.BootstrapStyles.css;

@Configuration
@ConditionalOnBootstrapUI
public class EntityModuleIcons
{
	public static final EntityModuleIcons entityModuleIcons = new EntityModuleIcons();

	public EntityModuleEmbeddedCollectionIcons embeddedCollection = new EntityModuleEmbeddedCollectionIcons();

	public EntityModuleFormViewIcons formView = new EntityModuleFormViewIcons();

	public EntityModuleListViewIcons listView = new EntityModuleListViewIcons();

	@Autowired
	public void registerAdminWebIconSet() {
		SimpleIconSet adminWebIconSet = new SimpleIconSet();

		adminWebIconSet.add( EntityModuleFormViewIcons.DELETE, ( imageName ) -> IconSet.iconSet( FONT_AWESOME_SOLID_ICON_SET ).icon( "trash" ) );
		adminWebIconSet.add( EntityModuleFormViewIcons.ADVANCED_SETTINGS, ( imageName ) -> IconSet.iconSet( FONT_AWESOME_SOLID_ICON_SET ).icon( "cog" ) );

		adminWebIconSet.add( EntityModuleEmbeddedCollectionIcons.ITEM_HANDLE, ( imageName ) -> IconSet.iconSet( FONT_AWESOME_SOLID_ICON_SET ).icon( "bars" ) );
		adminWebIconSet.add( EntityModuleEmbeddedCollectionIcons.ITEM_ADD, ( imageName ) -> IconSet.iconSet( FONT_AWESOME_SOLID_ICON_SET ).icon( "plus" ) );
		adminWebIconSet.add( EntityModuleEmbeddedCollectionIcons.ITEM_REMOVE, ( imageName ) -> IconSet.iconSet( FONT_AWESOME_SOLID_ICON_SET ).icon( "times" )
		                                                                                              .set( css.text.danger ) );

		adminWebIconSet.add( EntityModuleListViewIcons.ENTITY_QUERY_SEARCH, ( imageName ) -> IconSet.iconSet( FONT_AWESOME_SOLID_ICON_SET ).icon( "search" ) );
		adminWebIconSet.add( EntityModuleListViewIcons.LINK_DETAIL, ( imageName ) -> IconSet.iconSet( FONT_AWESOME_SOLID_ICON_SET ).icon( "eye" ) );
		adminWebIconSet.add( EntityModuleListViewIcons.LINK_EDIT, ( imageName ) -> IconSet.iconSet( FONT_AWESOME_SOLID_ICON_SET ).icon( "edit" ) );
		adminWebIconSet.add( EntityModuleListViewIcons.LINK_DELETE, ( imageName ) -> IconSet.iconSet( FONT_AWESOME_SOLID_ICON_SET ).icon( "times" )
		                                                                                    .set( css.text.danger ) );
		adminWebIconSet.add( EntityModuleListViewIcons.PREVIOUS_PAGE, ( imageName ) -> IconSet.iconSet( FONT_AWESOME_SOLID_ICON_SET ).icon( "step-backward" ) );
		adminWebIconSet.add( EntityModuleListViewIcons.NEXT_PAGE, ( imageName ) -> IconSet.iconSet( FONT_AWESOME_SOLID_ICON_SET ).icon( "step-forward" ) );

		adminWebIconSet.add( EntityModuleSummaryViewIcons.EXPAND, ( imageName ) -> IconSet.iconSet( FONT_AWESOME_SOLID_ICON_SET ).icon( "plus-circle" )
		                                                                                  .set( css.margin.right.s2, css.text.success ) );
		adminWebIconSet.add( EntityModuleSummaryViewIcons.COLLAPSE, ( imageName ) -> IconSet.iconSet( FONT_AWESOME_SOLID_ICON_SET ).icon( "minus-circle" )
		                                                                                    .set( css.margin.right.s2, css.text.danger ) );

		IconSetRegistry.addIconSet( EntityModule.NAME, adminWebIconSet );
	}
}
