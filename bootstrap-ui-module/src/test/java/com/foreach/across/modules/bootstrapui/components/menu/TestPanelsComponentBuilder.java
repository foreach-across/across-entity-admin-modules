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
import com.foreach.across.modules.bootstrapui.components.builder.PanelsNavComponentBuilder;
import com.foreach.across.modules.bootstrapui.elements.AbstractBootstrapViewElementTest;
import com.foreach.across.modules.bootstrapui.elements.GlyphIcon;
import com.foreach.across.modules.web.context.WebAppLinkBuilder;
import com.foreach.across.modules.web.menu.Menu;
import com.foreach.across.modules.web.menu.PathBasedMenuBuilder;
import com.foreach.across.modules.web.ui.DefaultViewElementBuilderContext;
import com.foreach.across.modules.web.ui.ViewElementBuilder;
import com.foreach.across.modules.web.ui.elements.TextViewElement;
import org.junit.Before;
import org.junit.Test;

import static com.foreach.across.modules.bootstrapui.components.builder.NavComponentBuilder.ATTR_ICON;
import static com.foreach.across.modules.bootstrapui.components.builder.NavComponentBuilder.CTX_CURRENT_MENU_ITEM;
import static com.foreach.across.modules.bootstrapui.components.builder.PanelsNavComponentBuilder.ATTR_RENDER_AS_PANEL;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Arne Vandamme
 * @since 1.1.0
 */
public class TestPanelsComponentBuilder extends AbstractBootstrapViewElementTest
{
	private PathBasedMenuBuilder menu;
	private DefaultViewElementBuilderContext builderContext;
	private PanelsNavComponentBuilder builder;

	@Before
	public void setUp() throws Exception {
		menu = new PathBasedMenuBuilder();
		builderContext = new DefaultViewElementBuilderContext();
		builder = new PanelsNavComponentBuilder();
	}

	@Test
	public void noMenuRendersEmptyList() {
		renderAndExpect( builder.htmlId( "menu" ), "<nav id='menu' class='nav nav-panels'></nav>" );
	}

	@Test
	public void namedMenuRendering() {
		builderContext.setAttribute( "zeMenu", new PathBasedMenuBuilder().item( "two", "two" ).and().build() );
		renderAndExpect( builder.menu( "zeMenu" ),
		                 "<nav class='nav nav-panels'><ul class='nav nav-sidebar'><li><a href='two' title='two'>two</a></li></ul></nav>" );
	}

	@Test
	public void specificMenuRenderingTakesPrecedence() {
		builderContext.setAttribute( "zeMenu", new PathBasedMenuBuilder().item( "two", "two" ).and().build() );
		menu.item( "one", "one" );

		renderAndExpect(
				builder.menu( menu.build() ).menu( "zeMenu" ),
				"<nav class='nav nav-panels'><ul class='nav nav-sidebar'><li><a href='one' title='one'>one</a></li></ul></nav>"
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
				"<nav class='nav nav-panels'><ul class='nav nav-sidebar'><li><a href='context-url' title='one'>one</a></li></ul></nav>"
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
				"<nav class='nav nav-panels'><ul class='nav nav-sidebar'><li><a href='other-url' title='one'>one</a></li></ul></nav>"
		);
	}

	@Test
	public void simpleNavWithDisabledItem() {
		menu.item( "one", "one" ).and()
		    .item( "two", "two" ).disable();

		renderAndExpect(
				builder.menu( menu.build() ),
				"<nav class='nav nav-panels'><ul class='nav nav-sidebar'><li><a href='one' title='one'>one</a></li></ul></nav>"
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
				"<nav class='nav nav-panels'><ul class='nav nav-sidebar'>" +
						"<li><a href='/one' title='one'>one</a></li>" +
						"<li><a href='/two' title='two'>two</a></li>" +
						"</ul></nav>"
		);
	}

	@Test
	public void groupIsRenderedAsPanel() {
		menu.item( "/one", "one" ).group( true ).and()
		    .item( "/one/sub", "sub one" ).and()
		    .item( "/one/sub2", "sub one 2" ).and();

		renderAndExpect(
				builder.menu( menu.build() ),
				"<nav class='nav nav-panels'>" +
						"<div class='panel panel-default'>" +
						"<div class='panel-heading'><h3 class='panel-title'>one</h3></div>" +
						"<ul class='nav nav-sidebar list-group'>" +
						"<li class='list-group-item'><a href='/one/sub' title='sub one'>sub one</a></li>" +
						"<li class='list-group-item'><a href='/one/sub2' title='sub one 2'>sub one 2</a></li>" +
						"</ul>" +
						"</div>" +
						"</nav>"
		);
	}

	@Test
	public void iconsIncludedIsRenderedAsPanel() {
		ViewElementBuilder customBuilder = ( ctx )
				-> new TextViewElement( ctx.getAttribute( CTX_CURRENT_MENU_ITEM, Menu.class ).getPath() );

		menu.item( "/one", "one" ).attribute( ATTR_ICON, new GlyphIcon( GlyphIcon.APPLE ) ).group( true ).and()
		    .item( "/one/sub", "sub one" ).attribute( ATTR_ICON, customBuilder ).and()
		    .item( "/one/sub2", "sub one 2" ).and();

		renderAndExpect(
				builder.menu( menu.build() ),
				"<nav class='nav nav-panels'>" +
						"<div class='panel panel-default'>" +
						"<div class='panel-heading'>" +
						"<h3 class='panel-title'><span aria-hidden='true' class='glyphicon glyphicon-apple' />one</h3>" +
						"</div>" +
						"<ul class='nav nav-sidebar list-group'>" +
						"<li class='list-group-item'><a href='/one/sub' title='sub one'>/one/sub sub one</a></li>" +
						"<li class='list-group-item'><a href='/one/sub2' title='sub one 2'>sub one 2</a></li>" +
						"</ul>" +
						"</div>" +
						"</nav>"
		);
	}

	@Test
	public void allItemsFilteredOutResultsInNoPanels() {
		menu.item( "/one", "one" ).group( true ).and()
		    .item( "/one/sub", "sub one" ).and()
		    .item( "/one/sub2", "sub one 2" ).and();

		renderAndExpect(
				builder.menu( menu.build() ).filter( item -> !item.getTitle().startsWith( "sub" ) ),
				"<nav class='nav nav-panels'></nav>"
		);
	}

	@Test
	public void groupWithoutTitleResultsInPanelWithoutHeading() {
		menu.item( "/one", "" ).group( true ).and()
		    .item( "/one/sub", "sub one" ).and()
		    .item( "/one/sub2", "sub one 2" ).and();

		renderAndExpect(
				builder.menu( menu.build() ),
				"<nav class='nav nav-panels'>" +
						"<div class='panel panel-default'>" +
						"<ul class='nav nav-sidebar list-group'>" +
						"<li class='list-group-item'><a href='/one/sub' title='sub one'>sub one</a></li>" +
						"<li class='list-group-item'><a href='/one/sub2' title='sub one 2'>sub one 2</a></li>" +
						"</ul>" +
						"</div>" +
						"</nav>"
		);
	}

	@Test
	public void panelStyling() {
		menu.item( "/one", "" ).group( true ).attribute( PanelsNavComponentBuilder.ATTR_PANEL_STYLE, "panel-danger" ).and()
		    .item( "/one/sub", "sub one" ).and()
		    .item( "/one/sub2", "sub one 2" ).and();

		renderAndExpect(
				builder.menu( menu.build() ),
				"<nav class='nav nav-panels'>" +
						"<div class='panel panel-danger'>" +
						"<ul class='nav nav-sidebar list-group'>" +
						"<li class='list-group-item'><a href='/one/sub' title='sub one'>sub one</a></li>" +
						"<li class='list-group-item'><a href='/one/sub2' title='sub one 2'>sub one 2</a></li>" +
						"</ul>" +
						"</div>" +
						"</nav>"
		);
	}

	@Test
	public void groupWithoutTitleNotRenderedAsPanel() {
		menu.item( "/one", "" ).group( true ).attribute( ATTR_RENDER_AS_PANEL, false ).and()
		    .item( "/one/sub", "sub one" ).and()
		    .item( "/one/sub2", "sub one 2" ).and();

		renderAndExpect(
				builder.menu( menu.build() ),
				"<nav class='nav nav-panels'>" +
						"<ul class='nav nav-sidebar'>" +
						"<li><a href='/one/sub' title='sub one'>sub one</a></li>" +
						"<li><a href='/one/sub2' title='sub one 2'>sub one 2</a></li>" +
						"</ul>" +
						"</nav>"
		);
	}

	@Test
	public void groupWithTitleNotRenderedAsPanel() {
		menu.item( "/one", "one" ).group( true ).attribute( ATTR_RENDER_AS_PANEL, false ).and()
		    .item( "/one/sub", "sub one" ).and()
		    .item( "/one/sub2", "sub one 2" ).and();

		renderAndExpect(
				builder.menu( menu.build() ),
				"<nav class='nav nav-panels'>" +
						"<ul class='nav nav-sidebar'>" +
						"<li class='nav-title'>one</li>" +
						"<li><a href='/one/sub' title='sub one'>sub one</a></li>" +
						"<li><a href='/one/sub2' title='sub one 2'>sub one 2</a></li>" +
						"</ul>" +
						"</nav>"
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
				"<nav class='nav nav-panels'>" +
						"<div class='panel panel-default'>" +
						"<div class='panel-heading'><h3 class='panel-title'>one</h3></div>" +
						"<ul class='nav nav-sidebar list-group'>" +
						"<li class='list-group-item'><a href='/one/sub' title='sub one'>sub one</a></li>" +
						"<li class='list-group-item active'><a href='/one/sub2' title='sub one 2'>sub one 2</a></li>" +
						"</ul>" +
						"</div>" +
						"<div class='panel panel-default'>" +
						"<div class='panel-heading'><h3 class='panel-title'>two</h3></div>" +
						"<ul class='nav nav-sidebar list-group'>" +
						"<li class='list-group-item'><a href='/two/sub' title='sub two'>sub two</a></li>" +
						"<li class='list-group-item'><a href='/two/sub2' title='sub two 2'>sub two 2</a></li>" +
						"</ul>" +
						"</div>" +
						"</nav>"
		);
	}

	@Test
	public void subMenuRendering() {
		menu.item( "/one", "one" ).group( true ).and()
		    .item( "/one/one", "one one" ).and()
		    .group( "/one/sub", "one sub" ).and()
		    .item( "/one/sub/one", "one sub one" ).and()
		    .group( "/one/sub/sub", "one sub sub" ).and()
		    .item( "/one/sub/sub/one", "one sub sub one" ).and()
		    .item( "/one/sub/sub/two", "one sub sub two" );

		renderAndExpect(
				builder.menu( menu.build() ).subMenuBaseId( "sidebarMenu" ).keepGroupsAsGroup( true ),
				"<nav class='nav nav-panels'>" +
						"<div class='panel panel-default'>" +
						"<div class='panel-heading'><h3 class='panel-title'>one</h3></div>" +
						"<ul class='nav nav-sidebar list-group'>" +
						"<li class='list-group-item'><a href='/one/one' title='one one'>one one</a></li>" +
						"<li class='list-group-item'>" +
						"<a data-toggle='collapse' href='#sidebarMenu-1' class='collapsed' title='one sub'>one sub</a>" +
						"<ul class='nav nav-sidebar submenu list-group collapse' id='sidebarMenu-1'>" +
						"<li class='list-group-item'><a href='/one/sub/one' title='one sub one'>one sub one</a></li>" +
						"<li class='list-group-item'>" +
						"<a data-toggle='collapse' href='#sidebarMenu-2' class='collapsed' title='one sub sub'>one sub sub</a>" +
						"<ul class='nav nav-sidebar submenu list-group collapse' id='sidebarMenu-2'>" +
						"<li class='list-group-item'><a href='/one/sub/sub/one' title='one sub sub one'>one sub sub one</a></li>" +
						"<li class='list-group-item'><a href='/one/sub/sub/two' title='one sub sub two'>one sub sub two</a></li>" +
						"</ul>" +
						"</li>" +
						"</ul>" +
						"</li>" +
						"</ul>" +
						"</div>" +
						"</nav>"
		);
	}

	@Test
	public void subMenuRenderingWithSelectedItem() {
		menu.item( "/one", "one" ).group( true ).and()
		    .item( "/one/one", "one one" ).and()
		    .group( "/one/sub", "one sub" ).and()
		    .item( "/one/sub/one", "one sub one" ).and()
		    .group( "/one/sub/sub", "one sub sub" ).and()
		    .item( "/one/sub/sub/one", "one sub sub one" ).and()
		    .item( "/one/sub/sub/two", "one sub sub two" );

		Menu built = menu.build();
		built.select( Menu.byTitle( "one sub sub one" ) );

		renderAndExpect(
				builder.menu( built ).subMenuBaseId( "sidebarMenu" ).keepGroupsAsGroup( true ),
				"<nav class='nav nav-panels'>" +
						"<div class='panel panel-default'>" +
						"<div class='panel-heading'><h3 class='panel-title'>one</h3></div>" +
						"<ul class='nav nav-sidebar list-group'>" +
						"<li class='list-group-item'><a href='/one/one' title='one one'>one one</a></li>" +
						"<li class='list-group-item'>" +
						"<a data-toggle='collapse' href='#sidebarMenu-1' title='one sub' aria-expanded='true'>one sub</a>" +
						"<ul class='nav nav-sidebar submenu list-group in' id='sidebarMenu-1' aria-expanded='true'>" +
						"<li class='list-group-item'><a href='/one/sub/one' title='one sub one'>one sub one</a></li>" +
						"<li class='list-group-item'>" +
						"<a data-toggle='collapse' href='#sidebarMenu-2' title='one sub sub' aria-expanded='true'>one sub sub</a>" +
						"<ul class='nav nav-sidebar submenu list-group in' id='sidebarMenu-2' aria-expanded='true'>" +
						"<li class='list-group-item active'><a href='/one/sub/sub/one' title='one sub sub one'>one sub sub one</a></li>" +
						"<li class='list-group-item'><a href='/one/sub/sub/two' title='one sub sub two'>one sub sub two</a></li>" +
						"</ul>" +
						"</li>" +
						"</ul>" +
						"</li>" +
						"</ul>" +
						"</div>" +
						"</nav>"
		);
	}

	private void renderAndExpect( NavComponentBuilder builder, String output ) {
		renderAndExpect( builder.build( builderContext ), output );
	}
}
