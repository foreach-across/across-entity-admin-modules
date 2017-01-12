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
import com.foreach.across.modules.bootstrapui.elements.BootstrapUiFactoryImpl;
import com.foreach.across.modules.bootstrapui.elements.GlyphIcon;
import com.foreach.across.modules.web.menu.Menu;
import com.foreach.across.modules.web.menu.PathBasedMenuBuilder;
import com.foreach.across.modules.web.ui.DefaultViewElementBuilderContext;
import com.foreach.across.modules.web.ui.ViewElementBuilder;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.elements.TextViewElement;
import org.junit.Before;
import org.junit.Test;

import static com.foreach.across.modules.bootstrapui.components.builder.NavComponentBuilder.CTX_CURRENT_MENU_ITEM;
import static com.foreach.across.modules.bootstrapui.components.builder.NavComponentBuilder.ICON_ATTRIBUTE;

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
		builder = new NavComponentBuilder( new BootstrapUiFactoryImpl() );
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
	public void navItemsWithIcon() {
		ViewElementBuilder customBuilder = ( ctx )
				-> new TextViewElement( ctx.getAttribute( CTX_CURRENT_MENU_ITEM, Menu.class ).getPath() );

		menu.item( "one", "one" )
		    .attribute( ICON_ATTRIBUTE, new GlyphIcon( GlyphIcon.APPLE ) )
		    .and()
		    .item( "two", "two" )
		    .attribute( ICON_ATTRIBUTE, customBuilder );

		renderAndExpect(
				builder.menu( menu.build() ),
				"<ul class='nav'>" +
						"<li><a href='one'><span aria-hidden='true' class='glyphicon glyphicon-apple' />one</a></li>" +
						"<li><a href='two'>twotwo</a></li>" +
						"</ul>"
		);
	}

	private void renderAndExpect( NavComponentBuilder builder, String output ) {
		renderAndExpect( builder.build( builderContext ), output );
	}
}
