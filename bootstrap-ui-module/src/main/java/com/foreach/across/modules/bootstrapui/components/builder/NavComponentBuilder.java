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
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * Builds a Bootstrap nav list structure for a {@link Menu} instance.
 * Supports several attributes for influencing how a menu gets translated, see
 * {@link #ATTR_ICON}, {@link #ATTR_ITEM_VIEW_ELEMENT}, {@link #ATTR_LINK_VIEW_ELEMENT}
 *
 * @author Arne Vandamme
 * @since 1.0.0
 */
@RequiredArgsConstructor
public class NavComponentBuilder extends AbstractNodeViewElementBuilder<NodeViewElement, NavComponentBuilder>
{
	private static final String PREFIX_HTML_ATTRIBUTE = "html:";

	/**
	 * If this attribute exists its value must be a {@link ViewElement}
	 * or {@link com.foreach.across.modules.web.ui.ViewElementBuilder}.  If so the resulting {@link ViewElement}
	 * will be added before the item text.
	 */
	public static final String ATTR_ICON = "nav:icon";

	/**
	 * Holds the custom {@link ViewElement} or {@link ViewElementBuilder} that should be used to render
	 * the list item for that {@link Menu}.  The custom element should take care of all possible child menu items.
	 */
	public static final String ATTR_ITEM_VIEW_ELEMENT = "nav:itemViewElement";

	/**
	 * Holds the custom {@link ViewElement} or {@link ViewElementBuilder} that should be used to render the link
	 * inside the list item of that {@link Menu}.  In case of a group item, the custom element should handle
	 * toggling the dropdown.
	 */
	public static final String ATTR_LINK_VIEW_ELEMENT = "nav:viewElement";

	/**
	 * If set to {@code true} this group will always be rendered as a group (dropdown) even if there is only
	 * a single item.  The default behaviour would be to then just render that item.
	 */
	public static final String ATTR_KEEP_AS_GROUP = "nav:keepAsGroup";

	/**
	 * If a custom {@link ViewElementBuilder} is being used for rendering (part of) a {@link Menu}, the
	 * {@link ViewElementBuilderContext} will contain the {@link Menu} being rendered as an attribute with this name.
	 */
	public static final String CTX_CURRENT_MENU_ITEM = "NavComponentBuilder.currentMenuItem";

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
		Menu itemToRender = findItemToRender( item );

		if ( itemToRender != null ) {
			if ( !addViewElementIfAttributeExists( itemToRender, ATTR_ITEM_VIEW_ELEMENT, list, builderContext ) ) {
				NodeViewElement li = new NodeViewElement( "li" );
				addHtmlAttributes( li, itemToRender.getAttributes() );
				if ( itemToRender.isSelected() ) {
					li.addCssClass( "active" );
				}

				if ( itemToRender.isGroup() ) {
					buildDropDownItem( li, itemToRender, builderContext );
				}
				else {
					addItemLink( li, itemToRender, builderContext );
				}

				list.addChild( li );
			}
		}
	}

	private Menu findItemToRender( Menu item ) {
		if ( item.isDisabled() ) {
			return null;
		}

		if ( item.isGroup() ) {
			long numberOfChildren = numberOfNonDisabledChildren( item );

			if ( numberOfChildren == 1
					&& !item.hasAttribute( ATTR_ITEM_VIEW_ELEMENT )
					&& !Boolean.TRUE.equals( item.getAttribute( ATTR_KEEP_AS_GROUP ) ) ) {
				Menu candidate = findFirstActiveChild( item );
				if ( candidate != null ) {
					return candidate;
				}
			}

			return numberOfChildren > 0 ? item : null;
		}

		return item;
	}

	private Menu findFirstActiveChild( Menu menu ) {
		for ( Menu item : menu.getItems() ) {
			if ( !item.isDisabled() ) {
				if ( item.isGroup() ) {
					return findFirstActiveChild( menu );
				}
				else {
					return item;
				}
			}

		}
		return null;
	}

	private void addHtmlAttributes( NodeViewElement node, Map<String, Object> attributes ) {
		attributes.forEach( ( name, value ) -> {
			if ( StringUtils.startsWith( name, PREFIX_HTML_ATTRIBUTE ) ) {
				node.setAttribute( StringUtils.removeStart( name, PREFIX_HTML_ATTRIBUTE ), value );
			}
		} );
	}

	private void buildDropDownItem( NodeViewElement li, Menu item, ViewElementBuilderContext builderContext ) {
		li.addCssClass( "dropdown" );

		if ( !addViewElementIfAttributeExists( item, ATTR_LINK_VIEW_ELEMENT, li, builderContext ) ) {
			LinkViewElement link = new LinkViewElement();
			link.setUrl( "#" );
			link.addCssClass( "dropdown-toggle" );
			link.setAttribute( "data-toggle", "dropdown" );
			link.setText( item.getTitle() + " " );

			NodeViewElement caret = new NodeViewElement( "span" );
			caret.addCssClass( "caret" );
			link.addChild( caret );

			li.addChild( link );
		}

		NodeViewElement children = new NodeViewElement( "ul" );
		children.addCssClass( "dropdown-menu" );
		item.getItems()
		    .stream()
		    .filter( i -> !i.isDisabled() )
		    .forEach( child -> {
			    NodeViewElement childLi = new NodeViewElement( "li" );
			    addHtmlAttributes( childLi, child.getAttributes() );
			    if ( child.isSelected() ) {
				    childLi.addCssClass( "active" );
			    }
			    addItemLink( childLi, child, builderContext );
			    children.addChild( childLi );
		    } );

		li.addChild( children );
	}

	private void addItemLink( NodeViewElement li, Menu item, ViewElementBuilderContext builderContext ) {
		if ( !addViewElementIfAttributeExists( item, ATTR_LINK_VIEW_ELEMENT, li, builderContext ) ) {
			LinkViewElement link = new LinkViewElement();
			link.setUrl( item.getUrl() );
			addViewElementIfAttributeExists( item, ATTR_ICON, link, builderContext );
			link.addChild( new TextViewElement( item.getTitle() ) );

			li.addChild( link );
		}
	}

	/**
	 * @return true if an elemnet was added
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
				return true;
			}
			finally {
				builderContext.removeAttribute( CTX_CURRENT_MENU_ITEM );
			}
		}

		return false;
	}

	private long numberOfNonDisabledChildren( Menu menu ) {
		return menu.getItems()
		           .stream()
		           .filter( i -> !i.isDisabled() && ( !i.isGroup() || numberOfNonDisabledChildren( i ) > 0 ) )
		           .count();
	}

	private Menu retrieveMenu( ViewElementBuilderContext builderContext ) {
		if ( menu != null ) {
			return menu;
		}

		return menuName != null ? builderContext.getAttribute( menuName, Menu.class ) : null;
	}

	/**
	 * Turns this regular attribute name into a HTML attribute name for a {@link Menu} attribute
	 * by adding the right prefix.
	 *
	 * @param attributeName to convert
	 * @return attribute name for HTML attribute
	 */
	public static String htmlAttribute( String attributeName ) {
		return PREFIX_HTML_ATTRIBUTE + attributeName;
	}
}
