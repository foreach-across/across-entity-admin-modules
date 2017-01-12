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

package com.foreach.across.modules.bootstrapui.components.builder;

import com.foreach.across.modules.bootstrapui.elements.BootstrapUiFactory;
import com.foreach.across.modules.bootstrapui.elements.LinkViewElement;
import com.foreach.across.modules.web.menu.Menu;
import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.ViewElementBuilder;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.elements.ContainerViewElement;
import com.foreach.across.modules.web.ui.elements.NodeViewElement;
import com.foreach.across.modules.web.ui.elements.TextViewElement;
import com.foreach.across.modules.web.ui.elements.builder.AbstractNodeViewElementBuilder;
import lombok.RequiredArgsConstructor;

/**
 * Builds a Bootstrap nav list structure for a {@link Menu} instance.
 * Supports selected items and dropdowns.
 *
 * @author Arne Vandamme
 * @since 1.0.0
 */
@RequiredArgsConstructor
public class NavComponentBuilder extends AbstractNodeViewElementBuilder<NodeViewElement, NavComponentBuilder>
{
	/**
	 * If this attribute exists its value must be a {@link ViewElement}
	 * or {@link com.foreach.across.modules.web.ui.ViewElementBuilder}.  If so the resulting {@link ViewElement}
	 * will be added before the item text.
	 */
	public static final String ICON_ATTRIBUTE = "nav.icon";
	public static final String LIST_ELEMENT_ATTRIBUTE = "nav.li.viewElement";
	public static final String VIEW_ELEMENT_ATTRIBUTE = "nav.viewElement";

	public static final String CTX_CURRENT_MENU_ITEM = "NavComponentBuilder.currentMenuItem";

	private final BootstrapUiFactory bootstrapUi;

	private Menu menu;
	private String menuName;

	private String navStyle = "";

	/**
	 * Set the name of the menu to render.  The {@link Menu} should be available as an
	 * attribute with that name on the {@link ViewElementBuilderContext}.
	 * <p/>
	 * NOTE: this value will be ignored if {@link #menu(Menu)} was set.
	 *
	 * @param menuName name of the menu attribute
	 * @return current builder
	 */
	public NavComponentBuilder menu( String menuName ) {
		this.menuName = menuName;
		return this;
	}

	/**
	 * Set the fixed menu to render.
	 *
	 * @param menu to render
	 * @return current builder
	 */
	public NavComponentBuilder menu( Menu menu ) {
		this.menu = menu;
		return this;
	}

	/**
	 * Render menu with no specific nav style.
	 *
	 * @return current builder
	 */
	public NavComponentBuilder simple() {
		navStyle = "";
		return this;
	}

	/**
	 * Render menu as tabs.
	 *
	 * @return current builder
	 */
	public NavComponentBuilder tabs() {
		navStyle = "nav-tabs";
		return this;
	}

	/**
	 * Render menu as pills.
	 *
	 * @return current builder
	 */
	public NavComponentBuilder pills() {
		navStyle = "nav-pills";
		return this;
	}

	/**
	 * Render menu as stacked pills.
	 *
	 * @return current builder
	 */
	public NavComponentBuilder stacked() {
		navStyle = "nav-pills nav-stacked";
		return this;
	}

	/**
	 * Render menu as navbar links.
	 *
	 * @return current builder
	 */
	public NavComponentBuilder navbar() {
		navStyle = "navbar-nav";
		return this;
	}

	@Override
	protected NodeViewElement createElement( ViewElementBuilderContext builderContext ) {
		NodeViewElement list = apply( new NodeViewElement( "ul" ), builderContext );
		list.addCssClass( "nav", navStyle );

		Menu menuToRender = retrieveMenu( builderContext );

		if ( menuToRender != null ) {
			menuToRender.getItems()
			            .stream()
			            .filter( i -> !i.isDisabled() )
			            .forEach( item -> addMenuItemToList( list, item, builderContext ) );
		}

		return list;
	}

	private void addMenuItemToList( NodeViewElement list, Menu item, ViewElementBuilderContext builderContext ) {
		NodeViewElement li = new NodeViewElement( "li" );
		if ( item.isSelected() ) {
			li.addCssClass( "active" );
		}

		boolean hasChildren = hasNonDisabledItems( item );

		if ( !item.isGroup() || hasChildren ) {
			if ( hasChildren ) {
				buildDropDownItem( li, item, builderContext );
			}
			else {
				addItemLink( li, item, builderContext );
			}

			list.addChild( li );
		}
	}

	private void buildDropDownItem( NodeViewElement li, Menu item, ViewElementBuilderContext builderContext ) {
		li.addCssClass( "dropdown" );

		LinkViewElement link = new LinkViewElement();
		link.setUrl( "#" );
		link.addCssClass( "dropdown-toggle" );
		link.setAttribute( "data-toggle", "dropdown" );
		link.setText( item.getTitle() + " " );

		NodeViewElement caret = new NodeViewElement( "b" );
		caret.addCssClass( "caret" );
		link.addChild( caret );

		li.addChild( link );

		NodeViewElement children = new NodeViewElement( "ul" );
		children.addCssClass( "dropdown-menu" );
		item.getItems()
		    .stream()
		    .filter( i -> !i.isDisabled() )
		    .forEach( child -> {
			    NodeViewElement childLi = new NodeViewElement( "li" );
			    if ( child.isSelected() ) {
				    childLi.addCssClass( "active" );
			    }
			    addItemLink( childLi, child, builderContext );
			    children.addChild( childLi );
		    } );

		li.addChild( children );
	}

	private void addItemLink( NodeViewElement li, Menu item, ViewElementBuilderContext builderContext ) {
		LinkViewElement link = new LinkViewElement();
		link.setUrl( item.getUrl() );
		addViewElementIfAttributeExists( item, ICON_ATTRIBUTE, link, builderContext );
		link.addChild( new TextViewElement( item.getTitle() ) );

		li.addChild( link );
	}

	/**
	 * @return true if a fixed ViewElement was returned that should not be modified
	 */
	private boolean addViewElementIfAttributeExists( Menu item,
	                                                 String attributeName,
	                                                 ContainerViewElement container,
	                                                 ViewElementBuilderContext builderContext ) {
		Object attributeValue = item.getAttribute( attributeName );
		if ( attributeValue instanceof ViewElement ) {
			container.addChild( (ViewElement) attributeValue );
			return true;
		}
		if ( attributeValue instanceof ViewElementBuilder ) {
			try {
				builderContext.setAttribute( CTX_CURRENT_MENU_ITEM, item );
				container.addChild( ( (ViewElementBuilder) attributeValue ).build( builderContext ) );
			}
			finally {
				builderContext.removeAttribute( CTX_CURRENT_MENU_ITEM );
			}
		}
		return false;
	}

	private boolean hasNonDisabledItems( Menu menu ) {
		return menu.getItems().stream()
		           .anyMatch( i -> !i.isDisabled() );
	}

	private Menu retrieveMenu( ViewElementBuilderContext builderContext ) {
		if ( menu != null ) {
			return menu;
		}

		return menuName != null ? builderContext.getAttribute( menuName, Menu.class ) : null;
	}
}
