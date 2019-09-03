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

import com.foreach.across.modules.bootstrapui.components.builder.DefaultNavComponentBuilder;
import com.foreach.across.modules.bootstrapui.components.builder.NavComponentBuilder;
import com.foreach.across.modules.bootstrapui.elements.AbstractBootstrapViewElementTest;
import com.foreach.across.modules.web.context.WebAppLinkBuilder;
import com.foreach.across.modules.web.menu.Menu;
import com.foreach.across.modules.web.menu.MenuSelector;
import com.foreach.across.modules.web.menu.PathBasedMenuBuilder;
import com.foreach.across.modules.web.ui.DefaultViewElementBuilderContext;
import com.foreach.across.modules.web.ui.ViewElementBuilder;
import com.foreach.across.modules.web.ui.elements.TextViewElement;
import org.junit.Before;
import org.junit.Test;

import static com.foreach.across.modules.bootstrapui.components.builder.NavComponentBuilder.*;
import static com.foreach.across.modules.bootstrapui.styles.BootstrapStyles.css;
import static com.foreach.across.modules.web.ui.elements.HtmlViewElements.html;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Arne Vandamme
 * @since 1.0.0
 */
public class TestDefaultNavComponentBuilder extends AbstractBootstrapViewElementTest
{
	private PathBasedMenuBuilder menu;
	private DefaultViewElementBuilderContext builderContext;
	private DefaultNavComponentBuilder builder;

	@Before
	public void setUp() {
		menu = new PathBasedMenuBuilder();
		builderContext = new DefaultViewElementBuilderContext();
		builder = new DefaultNavComponentBuilder();
	}

	@Test
	public void noMenuRendersEmptyList() {
		renderAndExpect( builder.htmlId( "menu" ), "<ul id='menu' class='nav'></ul>" );
	}

	@Test
	public void renderingModes() {
		renderAndExpect( builder.tabs(), "<ul class='nav nav-tabs'></ul>" );
		renderAndExpect( builder.pills(), "<ul class='nav nav-pills'></ul>" );
		renderAndExpect( builder.stacked(), "<ul class=\"nav nav-pills flex-column\"></ul>" );
		renderAndExpect( builder.navbar(), "<ul class=\"navbar-nav\"></ul>" );
		renderAndExpect( builder.simple(), "<ul class='nav'></ul>" );
	}

	@Test
	public void namedMenuRendering() {
		builderContext.setAttribute( "zeMenu", new PathBasedMenuBuilder().item( "two", "#{my.code=two}" ).and().build() );
		renderAndExpect( builder.menu( "zeMenu" ), "<ul class='nav'><li class='nav-item'><a href='two' class='nav-link' title='two'>two</a></li></ul>" );
	}

	@Test
	public void specificMenuRenderingTakesPrecedence() {
		builderContext.setAttribute( "zeMenu", new PathBasedMenuBuilder().item( "two", "two" ).and().build() );
		menu.item( "one", "one" );

		renderAndExpect(
				builder.menu( menu.build() ).menu( "zeMenu" ),
				"<ul class='nav'><li class='nav-item'><a href='one' class='nav-link' title='one'>one</a></li></ul>"
		);
	}

	@Test
	public void defaultLinkBuilding() {
		menu.item( "one", "one" );

		WebAppLinkBuilder lb = mock( WebAppLinkBuilder.class );
		when( lb.buildLink( "one" ) ).thenReturn( "context-url" );
		builderContext.setWebAppLinkBuilder( lb );

		renderAndExpect(
				builder.menu( menu.build() ),
				"<ul class='nav'><li class='nav-item'><a class='nav-link' href='context-url' title='one'>one</a></li></ul>"
		);
	}

	@Test
	public void customLinkBuilder() {
		menu.item( "one", "one" );

		WebAppLinkBuilder lb = mock( WebAppLinkBuilder.class );
		when( lb.buildLink( "one" ) ).thenReturn( "context-url" );
		builderContext.setWebAppLinkBuilder( lb );

		renderAndExpect(
				builder.linkBuilder( url -> "other-url" ).menu( menu.build() ),
				"<ul class='nav'><li class='nav-item'><a href='other-url' class='nav-link' title='one'>one</a></li></ul>"
		);
	}

	@Test
	public void simpleNavWithDisabledItem() {
		menu.item( "one", "one" ).and()
		    .item( "two", "two" ).disable();

		renderAndExpect(
				builder.menu( menu.build() ),
				"<ul class=\"nav\"><li class=\"nav-item\"><a href=\"one\" title=\"one\" class=\"nav-link\">one</a></li></ul>"
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
						"<li class=\"nav-item\"><a href=\"/one\" title=\"one\" class=\"nav-link\">one</a></li>" +
						"<li class=\"nav-item\"><a href=\"/two\" title=\"two\" class=\"nav-link\">two</a></li>" +
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
		built.select( MenuSelector.byTitle( "sub one 2" ) );

		renderAndExpect(
				builder.menu( built ),
				"<ul class='nav'>" +
						"<li class='active dropdown'>" +
						"<a class='dropdown-toggle' data-toggle='dropdown' href='#' title='one'>one <span class='caret' /></a>" +
						"<div class=\"dropdown-menu\">" +
						"<li><a href='/one/sub' title='sub one'>sub one</a></li>" +
						"<li class='active'><a href='/one/sub2' title='sub one 2'>sub one 2</a></li>" +
						"</ul>" +
						"</li>" +
						"<li class='dropdown'>" +
						"<a class='dropdown-toggle' data-toggle='dropdown' href='#' title='two'>two <span class='caret' /></a>" +
						"<div class=\"dropdown-menu\">" +
						"<li><a href='/two/sub' title='sub two'>sub two</a></li>" +
						"<li><a href='/two/sub2' title='sub two 2'>sub two 2</a></li>" +
						"</ul>" +
						"</li>" +
						"</ul>"
		);
	}

	@Test
	public void navItemsAndGroupsWithIcon() {
		ViewElementBuilder customBuilder = ( ctx )
				-> new TextViewElement( ctx.getAttribute( CTX_CURRENT_MENU_ITEM, Menu.class ).getPath() );

		menu.item( "one", "one" )
		    .attribute( ATTR_ICON, html.i( css.fa.brands( "apple" ) ) )
		    .and()
		    .group( "two", "two" )
		    .attribute( ATTR_ICON, customBuilder ).and()
		    .item( "two/sub", "sub two" ).and()
		    .item( "two/sub2", "sub two 2" );

		renderAndExpect(
				builder.menu( menu.build() ),
				"<ul class='nav'>" +
						"<li class=\"nav-item\"><a href=\"one\" title=\"one\" class=\"nav-link\"><i class=\"fab fa-apple\"></i> one</a></li>" +
						"<li class=\"nav-item dropdown\">" +
						"<a data-toggle=\"dropdown\" role=\"button\" aria-expanded=\"false\" aria-haspopup=\"true\" href=\"#\" title=\"two\" class=\"nav-link dropdown-toggle\">two two </a>" +
						"<div class=\"dropdown-menu\">" +
						"<a href=\"two/sub\" title=\"sub two\" class=\"dropdown-item\">sub two</a>" +
						"<a href=\"two/sub2\" title=\"sub two 2\" class=\"dropdown-item\">sub two 2</a>" +
						"</div>" +
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
		built.select( MenuSelector.byTitle( "group item" ) );

		renderAndExpect(
				builder.menu( built ),
				"<ul class='nav'>" +
						"<li>fixed item</li>" +
						"<li class='active dropdown'>" +
						"fixed group" +
						"<ul class='dropdown-menu'>" +
						"<li class='active'>group item</li>" +
						"<li><a href='/group/two' title='group item 2'>group item 2</a></li>" +
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
		built.select( MenuSelector.byTitle( "group item" ) );

		renderAndExpect(
				builder.menu( built ),
				"<ul class='nav'>" +
						"<li class='bold' xlink='url'><a href='/one' title='one'>one</a></li>" +
						"<li class='active dropdown' href='test'>" +
						"<a class='dropdown-toggle' data-toggle='dropdown' href='#' title='two'>two <span class='caret' /></a>" +
						"<div class=\"dropdown-menu\">" +
						"<li class='pull-right active' data-url='test'><a href='/two/one' title='group item'>group item</a></li>" +
						"<li><a href='/two/two' title='group item 2'>group item 2</a></li>" +
						"</ul>" +
						"</li>" +
						"</ul>"
		);
	}

	@Test
	public void groupWithSingleItemIsRenderedAsItem() {
		menu.group( "/two", "two" )
		    .attribute( "html:class", "custom-class" )
		    .and()
		    .item( "/two/item", "two item" );

		renderAndExpect(
				builder.menu( menu.build() ),
				"<ul class='nav'>" +
						"<li class=\"nav-item\"><a href=\"/two/item\" title=\"two item\" class=\"nav-link\">two item</a></li>" +
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
						"<a class='dropdown-toggle' data-toggle='dropdown' href='#' title='two'>two <span class='caret' /></a>" +
						"<div class=\"dropdown-menu\">" +
						"<li><a href='/two/item' title='two item'>two item</a></li>" +
						"</ul>" +
						"</li>" +
						"</ul>"
		);
	}

	@Test
	public void groupWithSingleItemIsRenderedAsGroupIfDefaultKeepAsGroupIsActive() {
		menu.group( "/two", "two" )
		    .and()
		    .item( "/two/item", "two item" );

		renderAndExpect(
				builder.menu( menu.build() ).keepGroupsAsGroup( true ),
				"<ul class='nav'>" +
						"<li class='dropdown'>" +
						"<a class='dropdown-toggle' data-toggle='dropdown' href='#' title='two'>two <span class='caret' /></a>" +
						"<div class=\"dropdown-menu\">" +
						"<li><a href='/two/item' title='two item'>two item</a></li>" +
						"</ul>" +
						"</li>" +
						"</ul>"
		);
	}

	@Test
	public void groupWithSingleItemIsRenderedAsItemIfDefaultKeepIsActiveButExplicitlyConfiguredToCollapse() {
		menu.group( "/two", "two" )
		    .attribute( "html:class", "custom-class" )
		    .attribute( ATTR_KEEP_AS_GROUP, false )
		    .and()
		    .item( "/two/item", "two item" );

		renderAndExpect(
				builder.menu( menu.build() ).keepGroupsAsGroup( true ),
				"<ul class='nav'>" +
						"<li class='custom-class'><a href='/two/item' title='two item'>two item</a></li>" +
						"</ul>"
		);
	}

	@Test
	public void replaceGroupBySelectedItemRendersItemTextAsTheGroupLink() {
		menu.group( "/one", "one" ).and()
		    .item( "/one/item", "one item" ).and()
		    .item( "/one/item2", "one item 2" )
		    .attribute( ATTR_ICON, html.i( css.fa.brands( "apple" ) ) );

		Menu built = menu.build();
		builder.replaceGroupBySelectedItem().menu( built );

		renderAndExpect(
				builder,
				"<ul class='nav'>" +
						"<li class=\"nav-item dropdown\">" +
						"a data-toggle=\"dropdown\" role=\"button\" aria-expanded=\"false\" aria-haspopup=\"true\" href=\"#\" title=\"one\" class=\"nav-link dropdown-toggle\">one </a>" +
						"<div class=\"dropdown-menu\"><a href=\"/one/item\" title=\"one item\" class=\"dropdown-item\">one item</a><a href=\"/one/item2\" title=\"one item 2\" class=\"dropdown-item\"><i class=\"fab fa-apple\"></i> one item 2</a></div>" +
						"</li>" +
						"</ul>"
		);

		built.select( MenuSelector.byTitle( "one item 2" ) );
		renderAndExpect(
				builder,
				"<ul class='nav'>" +
						"<li class='active dropdown'>" +
						"<a class='dropdown-toggle' data-toggle='dropdown' href='#' title='one item 2'>" +
						"<span aria-hidden='true' class='glyphicon glyphicon-apple' />one item 2 <span class='caret' /></a>" +
						"<div class=\"dropdown-menu\">" +
						"<li><a href='/one/item' title='one item'>one item</a></li>" +
						"<li class='active'><a href='/one/item2' title='one item 2'><span aria-hidden='true' class='glyphicon glyphicon-apple' />one item 2</a></li>" +
						"</ul>" +
						"</li>" +
						"</ul>"
		);
	}

	@Test
	public void replaceGroupBySelectedItemRendersGroupIfKeepGroupItemIsSet() {
		menu.group( "/one", "one" )
		    .attribute( ATTR_KEEP_GROUP_ITEM, true )
		    .and()
		    .item( "/one/item", "one item" ).and()
		    .item( "/one/item2", "one item 2" )
		    .attribute( ATTR_ICON, html.i( css.fa.brands( "apple" ) ) );

		Menu built = menu.build();
		built.select( MenuSelector.byTitle( "one item 2" ) );
		builder.replaceGroupBySelectedItem().menu( built );

		renderAndExpect(
				builder,
				"<ul class='nav'>" +
						"<li class=\"nav-item dropdown\">" +
						"<a data-toggle=\"dropdown\" role=\"button\" aria-expanded=\"false\" aria-haspopup=\"true\" href=\"#\" title=\"one\" class=\"nav-link dropdown-toggle active\">one </a>" +
						"<div class=\"dropdown-menu\"><a href=\"/one/item\" title=\"one item\" class=\"dropdown-item\">one item</a><a href=\"/one/item2\" title=\"one item 2\" class=\"dropdown-item active\"><i class=\"fab fa-apple\"></i> one item 2</a></div>" +
						"</li>" +
						"</ul>"
		);
	}

	@Test
	public void subGroupsAreRenderedWithADividerAndHeading() {
		menu.group( "/one", "one" ).and()
		    .group( "/one/item", "one group" ).order( 1 )
		    .attribute( ATTR_ICON, html.i( css.fa.brands( "apple" ) ) )
		    .and()
		    .item( "/one/item/sub", "one sub item 1" ).and()
		    .item( "/one/item/sub2", "one sub item 2" ).and()
		    .item( "/one/item3", "one item 3" ).order( 2 ).and()
		    .group( "/one/item2", "one group 2" ).order( 3 ).and()
		    .item( "/one/item2/single", "single sub item" ).and()
		    .group( "/one/item4", "one group 3" ).and()
		    .item( "/one/item4/sub", "one sub item 3" ).and()
		    .item( "/one/item4/sub2", "one sub item 4" );

		Menu built = menu.build();
		built.sort();

		renderAndExpect(
				builder.menu( built ),
				"<ul class='nav'>" +
						"<li class=\"nav-item dropdown\">" +
						"<a data-toggle=\"dropdown\" role=\"button\" aria-expanded=\"false\" aria-haspopup=\"true\" href=\"#\" title=\"one\" class=\"nav-link dropdown-toggle\">one </a>" +
						"<div class=\"dropdown-menu\">" +
						"<h6 class=\"dropdown-header\"><i class=\"fab fa-apple\"></i> one group</h6>" +
						"<a href=\"/one/item/sub\" title=\"one sub item 1\" class=\"dropdown-item\">one sub item 1</a><a href=\"/one/item/sub2\" title=\"one sub item 2\" class=\"dropdown-item\">one sub item 2</a>" +
						"<div class=\"dropdown-divider\"></div>" +
						"<a href=\"/one/item3\" title=\"one item 3\" class=\"dropdown-item\">one item 3</a><a href=\"/one/item2/single\" title=\"single sub item\" class=\"dropdown-item\">single sub item</a>" +
						"<div class=\"dropdown-divider\"></div>" +
						"<h6 class=\"dropdown-header\">one group 3</h6>" +
						"<a href=\"/one/item4/sub\" title=\"one sub item 3\" class=\"dropdown-item\">one sub item 3</a><a href=\"/one/item4/sub2\" title=\"one sub item 4\" class=\"dropdown-item\">one sub item 4</a>" +
						"</div>" +
						"</li>" +
						"</ul>"
		);
	}

	@Test
	public void itemsCanForceADivider() {
		menu.group( "/one", "one" ).and()
		    .item( "/one/item1", "item 1" )
		    .attribute( ATTR_INSERT_SEPARATOR, Separator.AROUND )
		    .order( 1 )
		    .and()
		    .group( "/one/item2", "item 2" ).order( 2 ).and()
		    .item( "/one/item2/sub", "one sub item 1" ).and()
		    .item( "/one/item2/sub2", "one sub item 2" )
		    .attribute( ATTR_INSERT_SEPARATOR, Separator.BEFORE )
		    .and()
		    .item( "/one/item3", "one item 3" ).order( 3 ).and()
		    .item( "/one/item4", "one item 4" )
		    .attribute( ATTR_INSERT_SEPARATOR, "AROUND" )
		    .order( 4 )
		    .and()
		    .item( "/one/item5", "one item 5" )
		    .attribute( ATTR_INSERT_SEPARATOR, "after" )
		    .order( 5 )
		    .and()
		    .item( "/one/item6", "one item 6" ).order( 6 );

		Menu built = menu.build();
		built.sort();

		renderAndExpect(
				builder.menu( built ),
				"<ul class='nav'>" +
						"<li class='dropdown'>" +
						"<a class='dropdown-toggle' data-toggle='dropdown' href='#' title='one'>one <span class='caret' /></a>" +
						"<div class=\"dropdown-menu\">" +
						"<li><a href='/one/item1' title='item 1'>item 1</a></li>" +
						"<li role='separator' class='divider'></li>" +
						"<li class='dropdown-header'>item 2</li>" +
						"<li><a href='/one/item2/sub' title='one sub item 1'>one sub item 1</a></li>" +
						"<li role='separator' class='divider'></li>" +
						"<li><a href='/one/item2/sub2' title='one sub item 2'>one sub item 2</a></li>" +
						"<li role='separator' class='divider'></li>" +
						"<li><a href='/one/item3' title='one item 3'>one item 3</a></li>" +
						"<li role='separator' class='divider'></li>" +
						"<li><a href='/one/item4' title='one item 4'>one item 4</a></li>" +
						"<li role='separator' class='divider'></li>" +
						"<li><a href='/one/item5' title='one item 5'>one item 5</a></li>" +
						"<li role='separator' class='divider'></li>" +
						"<li><a href='/one/item6' title='one item 6'>one item 6</a></li>" +
						"</ul>" +
						"</li>" +
						"</ul>"
		);
	}

	@Test
	public void iconOnlyItems() {
		menu.item( "one", "one" )
		    .order( 1 )
		    .attribute( ATTR_ICON, html.i( css.fa.brands( "apple" ) ) )
		    .attribute( ATTR_ICON_ONLY, true )
		    .and()
		    .item( "two", "two" )
		    .order( 2 )
		    .attribute( ATTR_ICON_ONLY, true )
		    .and()
		    .group( "three", "three" )
		    .order( 3 )
		    .attribute( ATTR_ICON_ONLY, true )
		    .and()
		    .item( "three/one", "sub three" )
		    .attribute( ATTR_ICON, html.i( css.fa.brands( "apple" ) ) )
		    .and()
		    .group( "four", "four" )
		    .order( 4 )
		    .attribute( ATTR_ICON_ONLY, true )
		    .attribute( ATTR_ICON, html.i( css.fa.brands( "apple" ) ) )
		    .and()
		    .item( "four/one", "sub four 1" )
		    .attribute( ATTR_ICON, html.i( css.fa.solid( "trash" ) ) )
		    .and()
		    .item( "four/two", "sub four 2" )
		    .attribute( ATTR_ICON, html.i( css.fa.solid( "download" ) ) );

		Menu built = menu.build();
		built.sort();

		renderAndExpect(
				builder.menu( built ),
				"<ul class='nav'>" +
						"<li class=\"nav-item\"><a href=\"one\" title=\"one\" class=\"nav-link\"><i class=\"fab fa-apple\"></i> <span class=\"sr-only\">one</span></a></li>" +
						"<li class=\"nav-item\"><a href=\"two\" title=\"two\" class=\"nav-link\">two</a></li>" +
						"<li class=\"nav-item\"><a href=\"three/one\" title=\"sub three\" class=\"nav-link\"><i class=\"fab fa-apple\"></i> <span class=\"sr-only\">sub three</span></a></li>" +
						"<li class=\"nav-item dropdown\">" +
						"<a class='dropdown-toggle' data-toggle='dropdown' href='#' title='four'>" +
						"<span aria-hidden='true' class='glyphicon glyphicon-apple' /> <span class='nav-item-title'>four</span><span class='caret' /></a>" +
						"<div class=\"dropdown-menu\">" +
						"<li><a href='four/one' title='sub four 1'><span aria-hidden='true' class='glyphicon glyphicon-trash' />sub four 1</a></li>" +
						"<li><a href='four/two' title='sub four 2'><span aria-hidden='true' class='glyphicon glyphicon-download' />sub four 2</a></li>" +
						"</ul>" +
						"</li>" +
						"</ul>"
		);
	}

	@Test
	public void onlyItemsMatchingThePredicateAreRenderedIfFilterConfigured() {
		menu.item( "one", "one" )
		    .order( 1 )
		    .attribute( ATTR_ICON, html.i( css.fa.brands( "apple" ) ) )
		    .and()
		    .item( "two", "two" )
		    .order( 2 )
		    .and()
		    .group( "three", "three" )
		    .order( 3 )
		    .and()
		    .item( "three/one", "sub three" )
		    .attribute( ATTR_ICON, html.i( css.fa.brands( "apple" ) ) )
		    .and()
		    .group( "four", "four" )
		    .order( 4 )
		    .attribute( ATTR_ICON, html.i( css.fa.brands( "apple" ) ) )
		    .and()
		    .item( "four/one", "sub four 1" )
		    .and()
		    .item( "four/two", "sub four 2" )
		    .attribute( ATTR_ICON, html.i( css.fa.solid( "download" ) ) );

		Menu built = menu.build();
		built.sort();

		// keep as group
		renderAndExpect(
				builder.menu( built ).keepGroupsAsGroup( true ).filter( item -> item.hasAttribute( ATTR_ICON ) ),
				"<ul class='nav'>" +
						"<li><a href='one' title='one'><span aria-hidden='true' class='glyphicon glyphicon-apple' /> one</a></li>" +
						"<li class='dropdown'>" +
						"<a class='dropdown-toggle' data-toggle='dropdown' href='#' title='four'>" +
						"<span aria-hidden='true' class='glyphicon glyphicon-apple' /> four<span class='caret' /></a>" +
						"<div class=\"dropdown-menu\">" +
						"<li><a href='four/two' title='sub four 2'><span aria-hidden='true' class='glyphicon glyphicon-download' />sub four 2</a></li>" +
						"</ul>" +
						"</li>" +
						"</ul>"
		);

		// collapse group
		renderAndExpect(
				builder.keepGroupsAsGroup( false ),
				"<ul class='nav'>" +
						"<li class=\"nav-item\"><a href=\"one\" title=\"one\" class=\"nav-link\"><i class=\"fab fa-apple\"></i> one</a></li>" +
						"<li class=\"nav-item dropdown\">" +
						"<a data-toggle=\"dropdown\" role=\"button\" aria-expanded=\"false\" aria-haspopup=\"true\" href=\"#\" title=\"four\" class=\"nav-link dropdown-toggle\"><i class=\"fab fa-apple\"></i> four </a>" +
						"div class=\"dropdown-menu\"><a href=\"four/two\" title=\"sub four 2\" class=\"dropdown-item\"><i class=\"fas fa-download\"></i> sub four 2</a></div>" +
						"</li>" +
						"</ul>"
		);
	}

	@Test
	public void filteredItemsInSubGroup() {
		menu.group( "/one", "one" ).and()
		    .group( "/one/item", "one group" ).order( 1 )
		    .attribute( ATTR_ICON, html.i( css.fa.brands( "apple" ) ) )
		    .and()
		    .item( "/one/item/sub", "one sub item 1" ).and()
		    .item( "/one/item/sub2", "one sub item 2" ).and();

		Menu built = menu.build();
		built.sort();

		renderAndExpect(
				builder.menu( built ).keepGroupsAsGroup( true ).filter( item -> !item.getTitle().endsWith( "item 1" ) ),
				"<ul class='nav'>" +
						"<li class=\"nav-item dropdown\">" +
						"<a data-toggle=\"dropdown\" role=\"button\" aria-expanded=\"false\" aria-haspopup=\"true\" href=\"#\" title=\"one\" class=\"nav-link dropdown-toggle\">one </a>" +
						"<div class=\"dropdown-menu\">" +
						"<h6 class=\"dropdown-header\"><i class=\"fab fa-apple\"></i> one group</h6>" +
						"<a href=\"/one/item/sub2\" title=\"one sub item 2\" class=\"dropdown-item\">one sub item 2</a>" +
						"</div>" +
						"</li>" +
						"</ul>"
		);
	}

	private void renderAndExpect( NavComponentBuilder builder, String output ) {
		renderAndExpect( builder.build( builderContext ), output );
	}
}
