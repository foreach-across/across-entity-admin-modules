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

import com.foreach.across.modules.bootstrapui.components.builder.NavComponentBuilder;
import com.foreach.across.modules.bootstrapui.elements.AbstractBootstrapViewElementTest;
import com.foreach.across.modules.bootstrapui.elements.GlyphIcon;
import com.foreach.across.modules.web.menu.Menu;
import com.foreach.across.modules.web.menu.PathBasedMenuBuilder;
import com.foreach.across.modules.web.ui.DefaultViewElementBuilderContext;
import com.foreach.across.modules.web.ui.ViewElementBuilder;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.elements.TextViewElement;
import org.junit.Before;
import org.junit.Test;

import static com.foreach.across.modules.bootstrapui.components.builder.NavComponentBuilder.*;

/**
 * @author Arne Vandamme
 * @since 1.0.0
 */
public class TestNavComponentBuilder extends AbstractBootstrapViewElementTest
{
	private PathBasedMenuBuilder menu;
	private ViewElementBuilderContext builderContext;
	private NavComponentBuilder builder;

	@Before
	public void setUp() throws Exception {
		menu = new PathBasedMenuBuilder();
		builderContext = new DefaultViewElementBuilderContext();
		builder = new NavComponentBuilder();
	}

	@Test
	public void noMenuRendersEmptyList() {
		renderAndExpect( builder.htmlId( "menu" ), "<ul id='menu' class='nav'></ul>" );
	}

	@Test
	public void renderingModes() {
		renderAndExpect( builder.tabs(), "<ul class='nav nav-tabs'></ul>" );
		renderAndExpect( builder.pills(), "<ul class='nav nav-pills'></ul>" );
		renderAndExpect( builder.stacked(), "<ul class='nav nav-pills nav-stacked'></ul>" );
		renderAndExpect( builder.navbar(), "<ul class='nav navbar-nav'></ul>" );
		renderAndExpect( builder.simple(), "<ul class='nav'></ul>" );
	}

	@Test
	public void namedMenuRendering() {
		builderContext.setAttribute( "zeMenu", new PathBasedMenuBuilder().item( "two", "two" ).and().build() );
		renderAndExpect( builder.menu( "zeMenu" ), "<ul class='nav'><li><a href='two'>two</a></li></ul>" );
	}

	@Test
	public void specificMenuRenderingTakesPrecedence() {
		builderContext.setAttribute( "zeMenu", new PathBasedMenuBuilder().item( "two", "two" ).and().build() );
		menu.item( "one", "one" );

		renderAndExpect(
				builder.menu( menu.build() ).menu( "zeMenu" ),
				"<ul class='nav'><li><a href='one'>one</a></li></ul>"
		);
	}

	@Test
	public void simpleNavWithDisabledItem() {
		menu.item( "one", "one" ).and()
		    .item( "two", "two" ).disable();

		renderAndExpect(
				builder.menu( menu.build() ),
				"<ul class='nav'><li><a href='one'>one</a></li></ul>"
		);
	}

	@Test
	public void navItemsAndGroupsWithIcon() {
		ViewElementBuilder customBuilder = ( ctx )
				-> new TextViewElement( ctx.getAttribute( CTX_CURRENT_MENU_ITEM, Menu.class ).getPath() );

		menu.item( "one", "one" )
		    .attribute( ATTR_ICON, new GlyphIcon( GlyphIcon.APPLE ) )
		    .and()
		    .item( "two", "two" )
		    .attribute( ATTR_ICON, customBuilder );

		renderAndExpect(
				builder.menu( menu.build() ),
				"<ul class='nav'>" +
						"<li><a href='one'><span aria-hidden='true' class='glyphicon glyphicon-apple' />one</a></li>" +
						"<li><a href='two'>twotwo</a></li>" +
						"</ul>"
		);
	}

	@Test
	public void childElementsFromRegularItemAreIgnored() {
		menu.item( "/one", "one" ).and()
		    .item( "/one/sub", "sub one" ).and()
		    .item( "/one/sub2", "sub one 2" ).and()
		    .item( "/two", "two" );

		renderAndExpect(
				builder.menu( menu.build() ),
				"<ul class='nav'>" +
						"<li><a href='/one'>one</a></li>" +
						"<li><a href='/two'>two</a></li>" +
						"</ul>"
		);
	}

	@Test
	public void childElementsFromGroupAreRendered() {
		menu.item( "/one", "one" ).group( true ).and()
		    .item( "/one/sub", "sub one" ).and()
		    .item( "/one/sub2", "sub one 2" ).and()
		    .group( "/two", "two" ).and()
		    .item( "/two/sub", "sub two" ).and()
		    .item( "/two/sub2", "sub two 2" );

		Menu built = menu.build();
		built.select( Menu.byTitle( "sub one 2" ) );

		renderAndExpect(
				builder.menu( built ),
				"<ul class='nav'>" +
						"<li class='active dropdown'>" +
						"<a class='dropdown-toggle' data-toggle='dropdown' href='#'>one <span class='caret' /></a>" +
						"<ul class='dropdown-menu'>" +
						"<li><a href='/one/sub'>sub one</a></li>" +
						"<li class='active'><a href='/one/sub2'>sub one 2</a></li>" +
						"</ul>" +
						"</li>" +
						"<li class='dropdown'>" +
						"<a class='dropdown-toggle' data-toggle='dropdown' href='#'>two <span class='caret' /></a>" +
						"<ul class='dropdown-menu'>" +
						"<li><a href='/two/sub'>sub two</a></li>" +
						"<li><a href='/two/sub2'>sub two 2</a></li>" +
						"</ul>" +
						"</li>" +
						"</ul>"
		);
	}

	@Test
	public void groupsWithoutActiveItemsAreNotRendered() {
		menu.group( "/one", "one" ).and()
		    .item( "/one/sub", "sub one" ).disable().and()
		    .group( "/two", "two" );

		renderAndExpect( builder.menu( menu.build() ), "<ul class='nav'></ul>" );
	}

	@Test
	public void customViewElementForItem() {
		ViewElementBuilder linkBuilder = ctx
				-> new TextViewElement( ctx.getAttribute( CTX_CURRENT_MENU_ITEM, Menu.class ).getPath() );
		ViewElementBuilder itemBuilder = ctx
				-> new TextViewElement( ctx.getAttribute( CTX_CURRENT_MENU_ITEM, Menu.class ).getTitle() );

		menu.item( "one", "fixed item" ).order( 1 )
		    .attribute( NavComponentBuilder.ATTR_ITEM_VIEW_ELEMENT, new TextViewElement( "custom text" ) )
		    .attribute( NavComponentBuilder.ATTR_LINK_VIEW_ELEMENT, linkBuilder )   // ignored
		    .and()
		    .group( "/group", "group title" ).order( 2 )
		    .attribute( NavComponentBuilder.ATTR_ITEM_VIEW_ELEMENT, itemBuilder )
		    .and()
		    .item( "/group/one", "group item" )
		    .and()
		    .group( "/group2", "empty group is skipped" )
		    .attribute( NavComponentBuilder.ATTR_ITEM_VIEW_ELEMENT, itemBuilder );

		Menu built = menu.build();
		built.sort();

		renderAndExpect(
				builder.menu( built ),
				"<ul class='nav'>" +
						"custom text" +
						"group title" +
						"</ul>"
		);
	}

	@Test
	public void customViewElementForLink() {
		ViewElementBuilder linkBuilder = ctx
				-> new TextViewElement( ctx.getAttribute( CTX_CURRENT_MENU_ITEM, Menu.class ).getTitle() );

		menu.item( "one", "fixed item" ).order( 1 )
		    .attribute( NavComponentBuilder.ATTR_LINK_VIEW_ELEMENT, linkBuilder )
		    .and()
		    .group( "/group", "group title" ).order( 2 )
		    .attribute( NavComponentBuilder.ATTR_LINK_VIEW_ELEMENT, new TextViewElement( "fixed group" ) )
		    .and()
		    .item( "/group/one", "group item" )
		    .attribute( NavComponentBuilder.ATTR_LINK_VIEW_ELEMENT, linkBuilder )
		    .and()
		    .item( "/group/two", "group item 2" )
		    .and()
		    .group( "/group2", "empty group is skipped" )
		    .attribute( NavComponentBuilder.ATTR_LINK_VIEW_ELEMENT, linkBuilder );

		Menu built = menu.build();
		built.sort();
		built.select( Menu.byTitle( "group item" ) );

		renderAndExpect(
				builder.menu( built ),
				"<ul class='nav'>" +
						"<li>fixed item</li>" +
						"<li class='active dropdown'>" +
						"fixed group" +
						"<ul class='dropdown-menu'>" +
						"<li class='active'>group item</li>" +
						"<li><a href='/group/two'>group item 2</a></li>" +
						"</ul>" +
						"</li>" +
						"</ul>"
		);
	}

	@Test
	public void customHtmlAttributesAddedOnListItems() {
		menu.item( "/one", "one" )
		    .attribute( htmlAttribute( "class" ), "bold" )
		    .attribute( htmlAttribute( "xlink" ), "url" )
		    .and()
		    .group( "/two", "two" )
		    .attribute( htmlAttribute( "href" ), "test" )
		    .and()
		    .item( "/two/one", "group item" )
		    .attribute( htmlAttribute( "data-url" ), "test" )
		    .attribute( htmlAttribute( "class" ), "pull-right" )
		    .and()
		    .item( "/two/two", "group item 2" );

		Menu built = menu.build();
		built.select( Menu.byTitle( "group item" ) );

		renderAndExpect(
				builder.menu( built ),
				"<ul class='nav'>" +
						"<li class='bold' xlink='url'><a href='/one'>one</a></li>" +
						"<li class='active dropdown' href='test'>" +
						"<a class='dropdown-toggle' data-toggle='dropdown' href='#'>two <span class='caret' /></a>" +
						"<ul class='dropdown-menu'>" +
						"<li class='pull-right active' data-url='test'><a href='/two/one'>group item</a></li>" +
						"<li><a href='/two/two'>group item 2</a></li>" +
						"</ul>" +
						"</li>" +
						"</ul>"
		);
	}

	@Test
	public void groupWithSingleItemIsRenderedAsItem() {
		menu.group( "/two", "two" ).and()
		    .item( "/two/item", "two item" );

		renderAndExpect(
				builder.menu( menu.build() ),
				"<ul class='nav'>" +
						"<li><a href='/two/item'>two item</a></li>" +
						"</ul>"
		);
	}

	@Test
	public void groupWithSingleItemIsRenderedAsGroupIfKeepAsGroupIsSet() {
		menu.group( "/two", "two" )
		    .attribute( ATTR_KEEP_AS_GROUP, true )
		    .and()
		    .item( "/two/item", "two item" );

		renderAndExpect(
				builder.menu( menu.build() ),
				"<ul class='nav'>" +
						"<li class='dropdown'>" +
						"<a class='dropdown-toggle' data-toggle='dropdown' href='#'>two <span class='caret' /></a>" +
						"<ul class='dropdown-menu'>" +
						"<li><a href='/two/item'>two item</a></li>" +
						"</ul>" +
						"</li>" +
						"</ul>"
		);
	}

	public void replaceGroupBySelectedItemRendersItemTextAsTheGroupLink() {

	}

	public void replaceGroupBySelectedItemRendersGroupIfKeepGroupItemIsSet() {

	}

	public void iconOnlyGroupWithMultipleItems() {

	}

	public void iconOnlyGroupWithSingleItem() {

	}

	public void iconOnlyGroupWithoutIcon() {

	}

	public void subGroupsAreRenderedWithADividerAndHeading() {

	}

	public void itemsCanSpecifyToForceADivider() {

	}

	private void renderAndExpect( NavComponentBuilder builder, String output ) {
		renderAndExpect( builder.build( builderContext ), output );
	}
}
