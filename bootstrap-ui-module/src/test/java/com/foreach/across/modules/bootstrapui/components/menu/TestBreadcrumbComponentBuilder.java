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

package com.foreach.across.modules.bootstrapui.components.menu;

import com.foreach.across.modules.bootstrapui.components.builder.BreadcrumbNavComponentBuilder;
import com.foreach.across.modules.bootstrapui.components.builder.NavComponentBuilder;
import com.foreach.across.modules.bootstrapui.elements.AbstractBootstrapViewElementTest;
import com.foreach.across.modules.bootstrapui.elements.GlyphIcon;
import com.foreach.across.modules.web.menu.Menu;
import com.foreach.across.modules.web.menu.MenuSelector;
import com.foreach.across.modules.web.menu.PathBasedMenuBuilder;
import com.foreach.across.modules.web.ui.DefaultViewElementBuilderContext;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Arne Vandamme
 * @since 1.1.0
 */
public class TestBreadcrumbComponentBuilder extends AbstractBootstrapViewElementTest
{
	private PathBasedMenuBuilder menu;
	private DefaultViewElementBuilderContext builderContext;
	private BreadcrumbNavComponentBuilder builder;

	@Before
	public void setUp() throws Exception {
		menu = new PathBasedMenuBuilder();
		builderContext = new DefaultViewElementBuilderContext();
		builder = new BreadcrumbNavComponentBuilder();
	}

	@Test
	public void noMenuRendersEmptyList() {
		renderAndExpect( builder.htmlId( "menu" ), "<ol id='menu' class='breadcrumb'></ol>" );
	}

	@Test
	public void noSelectedItemsRendersEmptyList() {
		menu.item( "one", "one" ).and()
		    .item( "one/two", "two" );

		renderAndExpect( builder.menu( menu.build() ), "<ol class='breadcrumb'></ol>" );
	}

	@Test
	public void simpleBreadcrumb() {
		menu.item( "one", "#{code.one=one}" ).and()
		    .item( "one/two", "two" );

		Menu built = menu.build();
		built.setTitle( "Root" );
		built.select( MenuSelector.byTitle( "two" ) );

		renderAndExpect( builder.menu( built ), "<ol class='breadcrumb'>" +
				"<li><a href='' title='Root'>Root</a></li>" +
				"<li><a href='one' title='one'>one</a></li>" +
				"<li class='active'>two</li>" +
				"</ol>" );
	}

	@Test
	public void disabledItemsAreSkipped() {
		menu.item( "one", "one" ).and()
		    .item( "one/two", "two" ).disable().and()
		    .item( "one/two/three", "three" );

		Menu built = menu.build();
		built.setTitle( "Root" );
		built.select( MenuSelector.byTitle( "three" ) );

		renderAndExpect( builder.menu( built ), "<ol class='breadcrumb'>" +
				"<li><a href='' title='Root'>Root</a></li>" +
				"<li><a href='one' title='one'>one</a></li>" +
				"<li class='active'>three</li>" +
				"</ol>" );
	}

	@Test
	public void filteredItemsAreSkipped() {
		menu.item( "one", "one" ).and()
		    .item( "one/two", "two" ).and()
		    .item( "one/two/three", "three" );

		Menu built = menu.build();
		built.setTitle( "Root" );
		built.select( MenuSelector.byTitle( "three" ) );

		renderAndExpect( builder.menu( built ).filter( m -> !m.getTitle().equals( "two" ) ), "<ol class='breadcrumb'>" +
				"<li><a href='' title='Root'>Root</a></li>" +
				"<li><a href='one' title='one'>one</a></li>" +
				"<li class='active'>three</li>" +
				"</ol>" );
	}

	@Test
	public void onlyRenderIconsForTheLevelsSpecified() {
		menu.item( "one", "one" )
		    .attribute( NavComponentBuilder.ATTR_ICON, new GlyphIcon( GlyphIcon.APPLE ) )
		    .attribute( NavComponentBuilder.ATTR_ICON_ONLY, true )
		    .and()
		    .item( "one/two", "two" ).attribute( NavComponentBuilder.ATTR_ICON, new GlyphIcon( GlyphIcon.APPLE ) )
		    .attribute( NavComponentBuilder.ATTR_ICON_ONLY, true )
		    .and()
		    .item( "one/two/three", "three" ).attribute( NavComponentBuilder.ATTR_ICON, new GlyphIcon( GlyphIcon.APPLE ) );

		Menu built = menu.build();
		built.setTitle( "Root" );
		built.select( MenuSelector.byTitle( "three" ) );

		// by default all levels support icons and icon only
		renderAndExpect( builder.menu( built ), "<ol class='breadcrumb'>" +
				"<li><a href='' title='Root'>Root</a></li>" +
				"<li><a href='one' title='one'><span aria-hidden=\"true\" class=\"glyphicon glyphicon-apple\" /> <span class='nav-item-title'>one</span></a></li>" +
				"<li><a href='one/two' title='two'><span aria-hidden=\"true\" class=\"glyphicon glyphicon-apple\"/> <span class='nav-item-title'>two</span></a></li>" +
				"<li class='active'><span aria-hidden=\"true\" class=\"glyphicon glyphicon-apple\"/> three</li>" +
				"</ol>" );

		// only first 2 levels supports icon only
		renderAndExpect( builder.menu( built ).iconOnlyLevels( 2 ), "<ol class='breadcrumb'>" +
				"<li><a href='' title='Root'>Root</a></li>" +
				"<li><a href='one' title='one'><span aria-hidden=\"true\" class=\"glyphicon glyphicon-apple\" /> <span class='nav-item-title'>one</span></a></li>" +
				"<li><a href='one/two' title='two'><span aria-hidden=\"true\" class=\"glyphicon glyphicon-apple\"/> two</a></li>" +
				"<li class='active'><span aria-hidden=\"true\" class=\"glyphicon glyphicon-apple\"/> three</li>" +
				"</ol>" );

		// disable icon only altogether
		renderAndExpect( builder.menu( built ).iconOnlyLevels( 0 ), "<ol class='breadcrumb'>" +
				"<li><a href='' title='Root'>Root</a></li>" +
				"<li><a href='one' title='one'><span aria-hidden=\"true\" class=\"glyphicon glyphicon-apple\" /> one</a></li>" +
				"<li><a href='one/two' title='two'><span aria-hidden=\"true\" class=\"glyphicon glyphicon-apple\"/> two</a></li>" +
				"<li class='active'><span aria-hidden=\"true\" class=\"glyphicon glyphicon-apple\"/> three</li>" +
				"</ol>" );

		// by default only the first level supports icons
		renderAndExpect( builder.menu( built ).iconOnlyLevels( Integer.MAX_VALUE ).iconAllowedLevels( 0 ), "<ol class='breadcrumb'>" +
				"<li><a href='' title='Root'>Root</a></li>" +
				"<li><a href='one' title='one'>one</a></li>" +
				"<li><a href='one/two' title='two'>two</a></li>" +
				"<li class='active'>three</li>" +
				"</ol>" );
	}

	@Test
	public void groupsWithoutUrlUseTheFirstNonDisabledChildUrl() {
		menu.group( "one", "one" ).url( "group-one" ).and()
		    .group( "one/two", "two" ).and()
		    .item( "one/two/disabled", "disabled" ).disable().and()
		    .item( "one/two/three", "three" );

		Menu built = menu.build();
		built.setTitle( "Root" );
		built.select( MenuSelector.byTitle( "three" ) );

		renderAndExpect( builder.menu( built ), "<ol class='breadcrumb'>" +
				"<li><a href='' title='Root'>Root</a></li>" +
				"<li><a href='group-one' title='one'>one</a></li>" +
				"<li><a href='one/two/three' title='two'>two</a></li>" +
				"<li class='active'>three</li>" +
				"</ol>" );
	}

	private void renderAndExpect( NavComponentBuilder builder, String output ) {
		renderAndExpect( builder.build( builderContext ), output );
	}
}
