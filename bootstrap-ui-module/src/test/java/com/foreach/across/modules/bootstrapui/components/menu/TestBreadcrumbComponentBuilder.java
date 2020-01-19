/*
 * Copyright 2019 the original author or authors
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
import com.foreach.across.modules.web.menu.Menu;
import com.foreach.across.modules.web.menu.MenuSelector;
import com.foreach.across.modules.web.menu.PathBasedMenuBuilder;
import com.foreach.across.modules.web.ui.DefaultViewElementBuilderContext;
import com.foreach.across.modules.web.ui.ViewElementBuilder;
import com.foreach.across.modules.web.ui.elements.NodeViewElement;
import com.foreach.across.modules.web.ui.elements.TextViewElement;
import org.junit.Before;
import org.junit.Test;

import static com.foreach.across.modules.bootstrapui.attributes.BootstrapAttributes.attribute;
import static com.foreach.across.modules.bootstrapui.components.builder.NavComponentBuilder.CTX_CURRENT_MENU_ITEM;
import static com.foreach.across.modules.bootstrapui.components.builder.NavComponentBuilder.customizeViewElement;
import static com.foreach.across.modules.bootstrapui.styles.BootstrapStyles.css;
import static com.foreach.across.modules.web.ui.MutableViewElement.Functions.remove;
import static com.foreach.across.modules.web.ui.elements.HtmlViewElements.html;

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
				"<li class=\"breadcrumb-item\"><a href=\"\" title=\"Root\" class=\"active\">Root</a></li>" +
				"<li class=\"breadcrumb-item\"><a href=\"one\" title=\"one\" class=\"active\">one</a></li>" +
				"<li class=\"breadcrumb-item active\">two</li>" +
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
				"<li class=\"breadcrumb-item\"><a href=\"\" title=\"Root\" class=\"active\">Root</a></li>" +
				"<li class=\"breadcrumb-item\"><a href=\"one\" title=\"one\" class=\"active\">one</a></li>" +
				"<li class=\"breadcrumb-item active\">three</li>" +
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
				"<li class=\"breadcrumb-item\"><a href=\"\" title=\"Root\" class=\"active\">Root</a></li>" +
				"<li class=\"breadcrumb-item\"><a href=\"one\" title=\"one\" class=\"active\">one</a></li>" +
				"<li class=\"breadcrumb-item active\">three</li>" +
				"</ol>" );
	}

	@Test
	public void onlyRenderIconsForTheLevelsSpecified() {
		NodeViewElement apple = html.i( css.fa.brands( "apple" ) );
		menu.item( "one", "one" )
		    .attribute( NavComponentBuilder.ATTR_ICON, apple )
		    .attribute( NavComponentBuilder.ATTR_ICON_ONLY, true )
		    .and()
		    .item( "one/two", "two" ).attribute( NavComponentBuilder.ATTR_ICON, apple )
		    .attribute( NavComponentBuilder.ATTR_ICON_ONLY, true )
		    .and()
		    .item( "one/two/three", "three" ).attribute( NavComponentBuilder.ATTR_ICON, apple );

		Menu built = menu.build();
		built.setTitle( "Root" );
		built.select( MenuSelector.byTitle( "three" ) );

		// by default all levels support icons and icon only
		renderAndExpect( builder.menu( built ), "<ol class='breadcrumb'>" +
				"<li class=\"breadcrumb-item\"><a href=\"\" title=\"Root\" class=\"active\">Root</a></li>" +
				"<li class=\"breadcrumb-item\"><a href=\"one\" title=\"one\" class=\"active\"><i class=\"fab fa-apple\"></i> <span class=\"sr-only\">one</span></a></li>" +
				"<li class=\"breadcrumb-item\"><a href=\"one/two\" title=\"two\" class=\"active\"><i class=\"fab fa-apple\"></i> <span class=\"sr-only\">two</span></a></li>" +
				"<li class=\"breadcrumb-item active\"><i class=\"fab fa-apple\"></i> three</li>" +
				"</ol>" );

		// only first 2 levels supports icon only
		renderAndExpect( builder.menu( built ).iconOnlyLevels( 2 ), "<ol class='breadcrumb'>" +
				"<li class=\"breadcrumb-item\"><a href=\"\" title=\"Root\" class=\"active\">Root</a></li>" +
				"<li class=\"breadcrumb-item\"><a href=\"one\" title=\"one\" class=\"active\"><i class=\"fab fa-apple\"></i> <span class=\"sr-only\">one</span></a></li>" +
				"<li class=\"breadcrumb-item\"><a href=\"one/two\" title=\"two\" class=\"active\"><i class=\"fab fa-apple\"></i> two</a></li>" +
				"<li class=\"breadcrumb-item active\"><i class=\"fab fa-apple\"></i> three</li>" +
				"</ol>" );

		// disable icon only altogether
		renderAndExpect( builder.menu( built ).iconOnlyLevels( 0 ), "<ol class=\"breadcrumb\">" +
				"<li class=\"breadcrumb-item\"><a href=\"\" title=\"Root\" class=\"active\">Root</a></li>" +
				"<li class=\"breadcrumb-item\"><a href=\"one\" title=\"one\" class=\"active\"><i class=\"fab fa-apple\"></i> one</a></li>" +
				"<li class=\"breadcrumb-item\"><a href=\"one/two\" title=\"two\" class=\"active\"><i class=\"fab fa-apple\"></i> two</a></li>" +
				" <li class=\"breadcrumb-item active\"><i class=\"fab fa-apple\"></i> three</li>" +
				"</ol>" );

		// by default only the first level supports icons
		renderAndExpect( builder.menu( built ).iconOnlyLevels( Integer.MAX_VALUE ).iconAllowedLevels( 0 ), "<ol class='breadcrumb'>" +
				"<li class=\"breadcrumb-item\"><a href=\"\" title=\"Root\" class=\"active\">Root</a></li>" +
				"<li class=\"breadcrumb-item\"><a href=\"one\" title=\"one\" class=\"active\">one</a></li>" +
				"<li class=\"breadcrumb-item\"><a href=\"one/two\" title=\"two\" class=\"active\">two</a></li>" +
				"<li class=\"breadcrumb-item active\">three</li>" +
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
				"<li class=\"breadcrumb-item\"><a href='' title='Root' class=\"active\">Root</a></li>" +
				"<li class=\"breadcrumb-item\"><a href='group-one' title='one' class=\"active\">one</a></li>" +
				"<li class=\"breadcrumb-item\"><a href=\"one/two/three\" title=\"two\" class=\"active\">two</a></li>" +
				"<li class=\"breadcrumb-item active\">three</li>" +
				"</ol>" );
	}

	@Test
	public void customizeViewElements() {
		menu.item( "one", "one" )
		    .attribute( customizeViewElement( css.of( "custom-item-css" ) ) )
		    .and()
		    .item( "one/two", "two" )
		    .attribute( customizeViewElement( remove( css.of( "breadcrumb-item" ) ), attribute.data( "value", "123" ) ) );

		Menu built = menu.build();
		built.setTitle( "Root" );
		built.select( MenuSelector.byTitle( "two" ) );

		renderAndExpect( builder.menu( built ), "<ol class='breadcrumb'>" +
				"<li class=\"breadcrumb-item\"><a href=\"\" title=\"Root\" class=\"active\">Root</a></li>" +
				"<li class=\"breadcrumb-item custom-item-css\"><a href=\"one\" title=\"one\" class=\"active\">one</a></li>" +
				"<li class=\"active\" data-value=\"123\">two</li>" +
				"</ol>" );
	}

	@Test
	public void customViewElements() {
		ViewElementBuilder<?> itemBuilder = ctx
				-> new TextViewElement( ctx.getAttribute( CTX_CURRENT_MENU_ITEM, Menu.class ).getPath() );
		ViewElementBuilder<?> linkBuilder = ctx
				-> html.span( html.text( ctx.getAttribute( CTX_CURRENT_MENU_ITEM, Menu.class ).getTitle() ) );

		menu.item( "one", "one" )
		    .attribute( NavComponentBuilder.ATTR_ITEM_VIEW_ELEMENT, itemBuilder )
		    .and()
		    .item( "one/two", "two", "url" )
		    .attribute( NavComponentBuilder.ATTR_LINK_VIEW_ELEMENT, linkBuilder );

		Menu built = menu.build();
		built.setTitle( "Root" );
		built.select( MenuSelector.byTitle( "two" ) );

		renderAndExpect( builder.menu( built ), "<ol class='breadcrumb'>" +
				"<li class=\"breadcrumb-item\"><a href=\"\" title=\"Root\" class=\"active\">Root</a></li>" +
				"one" +
				"<li class=\"breadcrumb-item active\"><span>two</span></li>" +
				"</ol>" );
	}

	private void renderAndExpect( NavComponentBuilder builder, String output ) {
		renderAndExpect( builder.build( builderContext ), output );
	}
}
