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

import com.foreach.across.modules.bootstrapui.components.builder.NavComponentBuilder;
import com.foreach.across.modules.bootstrapui.components.builder.PanelsNavComponentBuilder;
import com.foreach.across.modules.bootstrapui.elements.AbstractBootstrapViewElementTest;
import com.foreach.across.modules.web.context.WebAppLinkBuilder;
import com.foreach.across.modules.web.menu.Menu;
import com.foreach.across.modules.web.menu.MenuSelector;
import com.foreach.across.modules.web.menu.PathBasedMenuBuilder;
import com.foreach.across.modules.web.ui.DefaultViewElementBuilderContext;
import com.foreach.across.modules.web.ui.ViewElementBuilder;
import com.foreach.across.modules.web.ui.elements.ContainerViewElement;
import com.foreach.across.modules.web.ui.elements.HtmlViewElement;
import com.foreach.across.modules.web.ui.elements.NodeViewElement;
import com.foreach.across.modules.web.ui.elements.TextViewElement;
import org.junit.Before;
import org.junit.Test;

import static com.foreach.across.modules.bootstrapui.components.builder.NavComponentBuilder.*;
import static com.foreach.across.modules.bootstrapui.components.builder.PanelsNavComponentBuilder.ATTR_RENDER_AS_PANEL;
import static com.foreach.across.modules.bootstrapui.styles.BootstrapStyles.css;
import static com.foreach.across.modules.web.ui.MutableViewElement.Functions.remove;
import static com.foreach.across.modules.web.ui.MutableViewElement.Functions.witherFor;
import static com.foreach.across.modules.web.ui.elements.HtmlViewElements.html;
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
		renderAndExpect( builder.htmlId( "menu" ), "<nav id='menu' class='nav nav-panels flex-column'></nav>" );
	}

	@Test
	public void namedMenuRendering() {
		builderContext.setAttribute( "zeMenu", new PathBasedMenuBuilder().item( "two", "two" ).and().build() );
		renderAndExpect( builder.menu( "zeMenu" ),
		                 "<nav class='nav nav-panels flex-column'>" +
				                 "<div class='list-group mb-3'><a href='two' title='two' class='list-group-item list-group-item-action'>two</a></div>" +
				                 "</nav>" );
	}

	@Test
	public void specificMenuRenderingTakesPrecedence() {
		builderContext.setAttribute( "zeMenu", new PathBasedMenuBuilder().item( "two", "two" ).and().build() );
		menu.item( "one", "one" );

		renderAndExpect(
				builder.menu( menu.build() ).menu( "zeMenu" ),
				"<nav class='nav nav-panels flex-column'>" +
						"<div class='list-group mb-3'><a href='one' title='one' class='list-group-item list-group-item-action'>one</a></div>" +
						"</nav>"
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
				"<nav class='nav nav-panels flex-column'>" +
						"<div class='list-group mb-3'><a href='context-url' title='one' class='list-group-item list-group-item-action'>one</a></div>" +
						"</nav>"
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
				"<nav class='nav nav-panels flex-column'>" +
						"<div class='list-group mb-3'><a href='other-url' title='one' class='list-group-item list-group-item-action'>one</a></div>" +
						"</nav>"
		);
	}

	@Test
	public void simpleNavWithDisabledItem() {
		menu.item( "one", "#{my.code=one}" ).and()
		    .item( "two", "two" ).disable();

		renderAndExpect(
				builder.menu( menu.build() ),
				"<nav class='nav nav-panels flex-column'>" +
						"<div class='list-group mb-3'><a href='one' title='one' class='list-group-item list-group-item-action'>one</a></div>" +
						"</nav>"
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
				"<nav class='nav nav-panels flex-column'><div class='list-group mb-3'>" +
						"<a class='list-group-item list-group-item-action' href='/one' title='one'>one</a>" +
						"<a class='list-group-item list-group-item-action' href='/two' title='two'>two</a>" +
						"</div></nav>"
		);
	}

	@Test
	public void groupIsRenderedAsCard() {
		menu.item( "/one", "one" ).group( true ).and()
		    .item( "/one/sub", "sub one" ).and()
		    .item( "/one/sub2", "sub one 2" );

		renderAndExpect(
				builder.menu( menu.build() ),
				"<nav class='nav nav-panels flex-column'>" +
						"<div class='card mb-3'>" +
						"<div class='card-header'>one</div>" +
						"<div class='list-group list-group-flush'>" +
						"<a class='list-group-item list-group-item-action' href='/one/sub' title='sub one'>sub one</a>" +
						"<a class='list-group-item list-group-item-action' href='/one/sub2' title='sub one 2'>sub one 2</a>" +
						"</div>" +
						"</div>" +
						"</nav>"
		);
	}

	@Test
	public void groupIsRenderedAsCardWithPathAsDataAttribute() {
		menu.item( "/one", "one" ).group( true ).and()
		    .item( "/one/sub", "sub one" ).and()
		    .item( "/one/sub2", "sub one 2" );

		renderAndExpect(
				builder.menu( menu.build() ).includePathAsDataAttribute( true ),
				"<nav class='nav nav-panels flex-column'>" +
						"<div class='card mb-3' data-ax-menu-path='/one'>" +
						"<div class='card-header'>one</div>" +
						"<div class='list-group list-group-flush'>" +
						"<a class='list-group-item list-group-item-action' data-ax-menu-path='/one/sub' href='/one/sub' title='sub one'>sub one</a>" +
						"<a class='list-group-item list-group-item-action' data-ax-menu-path='/one/sub2' href='/one/sub2' title='sub one 2'>sub one 2</a>" +
						"</div>" +
						"</div>" +
						"</nav>"
		);
	}

	@Test
	public void iconsIncludedIsRenderedAsPanel() {
		ViewElementBuilder customBuilder = ( ctx )
				-> new TextViewElement( ctx.getAttribute( CTX_CURRENT_MENU_ITEM, Menu.class ).getPath() );

		menu.item( "/one", "one" ).attribute( ATTR_ICON, html.i( css.fa.brands( "apple" ) ) ).group( true ).and()
		    .item( "/one/sub", "sub one" ).attribute( ATTR_ICON, customBuilder ).and()
		    .item( "/one/sub2", "sub one 2" );

		renderAndExpect(
				builder.menu( menu.build() ),
				"<nav class='nav nav-panels flex-column'>" +
						"<div class='card mb-3'>" +
						"<div class='card-header'>" +
						"<i class='fab fa-apple' /> one" +
						"</div>" +
						"<div class='list-group list-group-flush'>" +
						"<a class='list-group-item list-group-item-action' href='/one/sub' title='sub one'>/one/sub sub one</a>" +
						"<a class='list-group-item list-group-item-action' href='/one/sub2' title='sub one 2'>sub one 2</a>" +
						"</div>" +
						"</div>" +
						"</nav>"
		);
	}

	@Test
	public void allItemsFilteredOutResultsInNoPanels() {
		menu.item( "/one", "one" ).group( true ).and()
		    .item( "/one/sub", "sub one" ).and()
		    .item( "/one/sub2", "sub one 2" );

		renderAndExpect(
				builder.menu( menu.build() ).filter( item -> !item.getTitle().startsWith( "sub" ) ),
				"<nav class='nav nav-panels flex-column'></nav>"
		);
	}

	@Test
	public void groupWithoutTitleResultsInPanelWithoutHeading() {
		menu.item( "/one", "" ).group( true ).and()
		    .item( "/one/sub", "sub one" ).and()
		    .item( "/one/sub2", "sub one 2" );

		renderAndExpect(
				builder.menu( menu.build() ),
				"<nav class='nav nav-panels flex-column'>" +
						"<div class='card mb-3'>" +
						"<div class='list-group list-group-flush'>" +
						"<a class='list-group-item list-group-item-action' href='/one/sub' title='sub one'>sub one</a>" +
						"<a class='list-group-item list-group-item-action' href='/one/sub2' title='sub one 2'>sub one 2</a>" +
						"</div>" +
						"</div>" +
						"</nav>"
		);
	}

	@Test
	public void processingTheViewElements() {
		menu.item( "/one", "My group" )
		    .group( true )
		    .attribute( customizeViewElement( css.text.muted, witherFor( NodeViewElement.class, this::addToCardHeader ) ) ).and()
		    .item( "/one/sub", "sub one" ).attribute( customizeViewElement( css.margin.auto ) ).and()
		    .item( "/one/sub2", "sub one 2" ).and()
		    .item( "/two", "two" ).attribute( customizeViewElement( css.disabled ) ).and();
		//.item( "/three", "" ).group( true ).attribute( ATTR_RENDER_AS_PANEL, false ).and()
		//.item( "/three/sub", "sub three" );

		renderAndExpect(
				builder.menu( menu.build() ).with( remove( css.flex.column ) ),
				"<nav class='nav nav-panels'>" +
						"<div class='card mb-3 text-muted'>" +
						"<div class='card-header'>My groupsuffix</div>" +
						"<div class='list-group list-group-flush'>" +
						"<a class='list-group-item list-group-item-action m-auto' href='/one/sub' title='sub one'>sub one</a>" +
						"<a class='list-group-item list-group-item-action' href='/one/sub2' title='sub one 2'>sub one 2</a>" +
						"</div>" +
						"</div>" +
						"<div class='list-group mb-3'>" +
						"<a class='list-group-item list-group-item-action disabled' href='/two' title='two'>two</a>" +
						"</div>" +
						"</nav>"
		);
	}

	private void addToCardHeader( NodeViewElement card ) {
		card.findAll( e -> e instanceof HtmlViewElement && ( (HtmlViewElement) e ).hasCssClass( "card-header" ) )
		    .map( ContainerViewElement.class::cast )
		    .findFirst()
		    .ifPresent( header -> header.addChild( html.text( "suffix" ) ) );
	}

	@Test
	public void groupWithoutTitleNotRenderedAsPanel() {
		menu.item( "/one", "" ).group( true ).attribute( ATTR_RENDER_AS_PANEL, false ).and()
		    .item( "/one/sub", "sub one" ).and()
		    .item( "/one/sub2", "sub one 2" ).and();

		renderAndExpect(
				builder.menu( menu.build() ),
				"<nav class='nav nav-panels flex-column'>" +
						"<div class='card mb-3'>" +
						"<div class='list-group list-group-flush'>" +
						"<a href='/one/sub' title='sub one' class='list-group-item list-group-item-action'>sub one</a>" +
						"<a href='/one/sub2' title='sub one 2' class='list-group-item list-group-item-action'>sub one 2</a></div>" +
						"</div>" +
						"</nav>"
		);
	}

	@Test
	public void groupWithTitleNotRenderedAsCard() {
		menu.item( "/one", "one" ).group( true ).and()
		    .item( "/one/sub", "sub one" ).and()
		    .item( "/one/sub2", "sub one 2" ).and();

		renderAndExpect(
				builder.menu( menu.build() ),
				"<nav class='nav nav-panels flex-column'>" +
						"<div class='card mb-3'>" +
						"<div class='card-header'>one</div>" +
						"<div class='list-group list-group-flush'>" +
						"<a href='/one/sub' title='sub one' class='list-group-item list-group-item-action'>sub one</a>" +
						"<a href='/one/sub2' title='sub one 2' class='list-group-item list-group-item-action'>sub one 2</a>" +
						"</div>" +
						"</div>" +
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
		built.select( MenuSelector.byTitle( "sub one 2" ) );

		renderAndExpect(
				builder.menu( built ),
				"<nav class='nav nav-panels flex-column'>" +
						"<div class='card mb-3'>" +
						"<div class='card-header'>one</div>" +
						"<div class='list-group list-group-flush'>" +
						"<a href='/one/sub' title='sub one' class='list-group-item list-group-item-action'>sub one</a>" +
						"<a href='/one/sub2' title='sub one 2' class='active list-group-item list-group-item-action'>sub one 2</a>" +
						"</div>" +
						"</div" +
						"><div class='card mb-3'>" +
						"<div class='card-header'>two</div" +
						"><div class='list-group list-group-flush'>" +
						"<a href='/two/sub' title='sub two' class='list-group-item list-group-item-action'>sub two</a>" +
						"<a href='/two/sub2' title='sub two 2' class='list-group-item list-group-item-action'>sub two 2</a>" +
						"</div>" +
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
				"<nav class='nav nav-panels flex-column'>" +
						"<div class='card mb-3'>" +
						"<div class='card-header'>one</div>" +
						"<div class='list-group list-group-flush'><a href='/one/one' title='one one' class='list-group-item list-group-item-action'>one one</a><a data-toggle='collapse' href='#asidebarMenu-1' title='one sub' class='list-group-item list-group-item-action list-group-subgroup-toggle collapsed'>one sub</a>" +
						"<div id='asidebarMenu-1' class='list-group-subgroup collapse'><a href='/one/sub/one' title='one sub one' class='list-group-item list-group-item-action'>one sub one</a><a data-toggle='collapse' href='#asidebarMenu-2' title='one sub sub' class='list-group-item list-group-item-action list-group-subgroup-toggle collapsed'>one sub sub</a>" +
						"<div id='asidebarMenu-2' class='list-group-subgroup collapse'><a href='/one/sub/sub/one' title='one sub sub one' class='list-group-item list-group-item-action'>one sub sub one</a><a href='/one/sub/sub/two' title='one sub sub two' class='list-group-item list-group-item-action'>one sub sub two</a></div>" +
						"</div>" +
						"</div>" +
						"</div>" +
						"</nav>"
		);
	}

	@Test
	public void subMenuRenderingWithPathAsDataAttribute() {
		menu.item( "/one", "one" ).group( true ).and()
		    .item( "/one/one", "one one" ).and()
		    .group( "/one/sub", "one sub" ).and()
		    .item( "/one/sub/one", "one sub one" ).and()
		    .group( "/one/sub/sub", "one sub sub" ).and()
		    .item( "/one/sub/sub/one", "one sub sub one" ).and()
		    .item( "/one/sub/sub/two", "one sub sub two" );

		renderAndExpect(
				builder.menu( menu.build() ).subMenuBaseId( "sidebarMenu" ).keepGroupsAsGroup( true ).includePathAsDataAttribute( true ),
				"<nav class='nav nav-panels flex-column'>" +
						"<div class='card mb-3' data-ax-menu-path='/one'>" +
						"<div class='card-header'>one</div>" +
						"<div class='list-group list-group-flush'>" +
						"<a data-ax-menu-path='/one/one' href='/one/one' title='one one' class='list-group-item list-group-item-action'>one one</a>" +
						"<a data-ax-menu-path='/one/sub' data-toggle='collapse' href='#asidebarMenu-1' title='one sub' class='list-group-item list-group-item-action list-group-subgroup-toggle collapsed'>one sub</a>" +
						"<div id='asidebarMenu-1' class='list-group-subgroup collapse'>" +
						"<a data-ax-menu-path='/one/sub/one' href='/one/sub/one' title='one sub one' class='list-group-item list-group-item-action'>one sub one</a>" +
						"<a data-ax-menu-path='/one/sub/sub' data-toggle='collapse' href='#asidebarMenu-2' title='one sub sub' class='list-group-item list-group-item-action list-group-subgroup-toggle collapsed'>one sub sub</a>" +
						"<div id='asidebarMenu-2' class='list-group-subgroup collapse'>" +
						"<a data-ax-menu-path='/one/sub/sub/one' href='/one/sub/sub/one' title='one sub sub one' class='list-group-item list-group-item-action'>one sub sub one</a>" +
						"<a data-ax-menu-path='/one/sub/sub/two' href='/one/sub/sub/two' title='one sub sub two' class='list-group-item list-group-item-action'>one sub sub two</a></div>" +
						"</div>" +
						"</div>" +
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
		built.select( MenuSelector.byTitle( "one sub sub one" ) );

		renderAndExpect(
				builder.menu( built ).subMenuBaseId( "sidebarMenu" ).keepGroupsAsGroup( true ),
				"<nav class='nav nav-panels flex-column'>" +
						"<div class='card mb-3'>" +
						"<div class='card-header'>one</div>" +
						"<div class='list-group list-group-flush'>" +
						"<a href='/one/one' title='one one' class='list-group-item list-group-item-action'>one one</a><a data-toggle='collapse' aria-expanded='true' href='#asidebarMenu-1' title='one sub' class='list-group-item list-group-item-action list-group-subgroup-toggle'>one sub</a>" +
						"<div aria-expanded='true' id='asidebarMenu-1' class='list-group-subgroup collapse show'>" +
						" <a href='/one/sub/one' title='one sub one' class='list-group-item list-group-item-action'>one sub one</a><a data-toggle='collapse' aria-expanded='true' href='#asidebarMenu-2' title='one sub sub' class='list-group-item list-group-item-action list-group-subgroup-toggle'>one sub sub</a>" +
						"<div aria-expanded='true' id='asidebarMenu-2' class='list-group-subgroup collapse show'><a href='/one/sub/sub/one' title='one sub sub one' class='active list-group-item list-group-item-action'>one sub sub one</a><a href='/one/sub/sub/two' title='one sub sub two' class='list-group-item list-group-item-action'>one sub sub two</a></div>" +
						"</div>" +
						"</div>" +
						"</div>" +
						"</nav>"
		);
	}

	private void renderAndExpect( NavComponentBuilder builder, String output ) {
		renderAndExpect( builder.build( builderContext ), output );
	}
}
