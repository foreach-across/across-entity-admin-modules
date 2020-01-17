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

package com.foreach.across.modules.entity;

import com.foreach.across.modules.bootstrapui.elements.icons.IconSet;
import com.foreach.across.modules.bootstrapui.elements.icons.IconSetRegistry;
import com.foreach.across.modules.bootstrapui.elements.icons.SimpleIconSet;
import com.foreach.across.modules.entity.icons.*;

import static com.foreach.across.modules.bootstrapui.BootstrapUiModuleIcons.ICON_SET_FONT_AWESOME_SOLID;
import static com.foreach.across.modules.bootstrapui.styles.BootstrapStyles.css;

public class EntityModuleIcons
{
	public static final String ICON_SET = EntityModule.NAME;

	public static final EntityModuleIcons entityModuleIcons = new EntityModuleIcons();

	public final EntityModuleControlIcons controls = new EntityModuleControlIcons();
	public final EntityModuleEmbeddedCollectionIcons embeddedCollection = new EntityModuleEmbeddedCollectionIcons();
	public final EntityModuleFormViewIcons formView = new EntityModuleFormViewIcons();
	public final EntityModuleListViewIcons listView = new EntityModuleListViewIcons();

	public static void registerIconSet() {
		SimpleIconSet iconSet = new SimpleIconSet();

		iconSet.add( EntityModuleFormViewIcons.DELETE, ( imageName ) -> IconSet.iconSet( ICON_SET_FONT_AWESOME_SOLID ).icon( "trash" ) );
		iconSet.add( EntityModuleFormViewIcons.ADVANCED_SETTINGS, ( imageName ) -> IconSet.iconSet( ICON_SET_FONT_AWESOME_SOLID ).icon( "cog" ) );

		iconSet.add( EntityModuleEmbeddedCollectionIcons.ITEM_HANDLE, ( imageName ) -> IconSet.iconSet( ICON_SET_FONT_AWESOME_SOLID ).icon( "bars" ) );
		iconSet.add( EntityModuleEmbeddedCollectionIcons.ITEM_ADD, ( imageName ) -> IconSet.iconSet( ICON_SET_FONT_AWESOME_SOLID ).icon( "plus" ) );
		iconSet.add( EntityModuleEmbeddedCollectionIcons.ITEM_REMOVE, ( imageName ) -> IconSet.iconSet( ICON_SET_FONT_AWESOME_SOLID ).icon( "times" )
		                                                                                      .set( css.text.danger ) );

		iconSet.add( EntityModuleListViewIcons.ENTITY_QUERY_SEARCH, ( imageName ) -> IconSet.iconSet( ICON_SET_FONT_AWESOME_SOLID ).icon( "search" ) );
		iconSet.add( EntityModuleListViewIcons.LINK_DETAIL, ( imageName ) -> IconSet.iconSet( ICON_SET_FONT_AWESOME_SOLID ).icon( "eye" ) );
		iconSet.add( EntityModuleListViewIcons.LINK_EDIT, ( imageName ) -> IconSet.iconSet( ICON_SET_FONT_AWESOME_SOLID ).icon( "edit" ) );
		iconSet.add( EntityModuleListViewIcons.LINK_DELETE, ( imageName ) -> IconSet.iconSet( ICON_SET_FONT_AWESOME_SOLID ).icon( "times" )
		                                                                            .set( css.text.danger ) );
		iconSet.add( EntityModuleListViewIcons.PREVIOUS_PAGE, ( imageName ) -> IconSet.iconSet( ICON_SET_FONT_AWESOME_SOLID ).icon( "step-backward" ).addCssClass( "fa-fw" ) );
		iconSet.add( EntityModuleListViewIcons.NEXT_PAGE, ( imageName ) -> IconSet.iconSet( ICON_SET_FONT_AWESOME_SOLID ).icon( "step-forward" ).addCssClass( "fa-fw" ) );

		iconSet.add( EntityModuleAutoSuggestIcons.REMOVE_ITEM, ( imageName ) -> IconSet.iconSet( ICON_SET_FONT_AWESOME_SOLID ).icon( "times" )
		                                                                               .set( css.text.danger ) );

		IconSetRegistry.addIconSet( ICON_SET, iconSet );
	}
}
