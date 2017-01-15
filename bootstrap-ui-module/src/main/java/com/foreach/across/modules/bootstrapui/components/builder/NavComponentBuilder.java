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
import com.foreach.across.modules.bootstrapui.elements.builder.AbstractLinkSupportingNodeViewElementBuilder;
import com.foreach.across.modules.web.menu.Menu;
import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.ViewElementBuilder;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.elements.AbstractNodeViewElement;
import com.foreach.across.modules.web.ui.elements.ContainerViewElement;
import com.foreach.across.modules.web.ui.elements.NodeViewElement;
import com.foreach.across.modules.web.ui.elements.TextViewElement;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Builds a Bootstrap nav list structure for a {@link Menu} instance.
 * Supports several attributes for influencing how a menu gets translated, see
 * {@link #ATTR_ICON}, {@link #ATTR_ITEM_VIEW_ELEMENT}, {@link #ATTR_LINK_VIEW_ELEMENT}
 *
 * @author Arne Vandamme
 * @since 1.0.0
 */
@RequiredArgsConstructor
public class NavComponentBuilder extends AbstractLinkSupportingNodeViewElementBuilder<NodeViewElement, NavComponentBuilder>
{
	private static final String PREFIX_HTML_ATTRIBUTE = "html:";

	/**
	 * Possible values for attribute {@link #ATTR_INSERT_SEPARATOR}.
	 */
	public enum Separator
	{
		BEFORE,
		AFTER,
		AROUND;

		static boolean insertAfter( Menu menu ) {
			Separator separator = parseValue( menu.getAttribute( ATTR_INSERT_SEPARATOR ) );
			return AFTER.equals( separator ) || AROUND.equals( separator );
		}

		static boolean insertBefore( Menu menu ) {
			Separator separator = parseValue( menu.getAttribute( ATTR_INSERT_SEPARATOR ) );
			return BEFORE.equals( separator ) || AROUND.equals( separator );
		}

		static Separator parseValue( Object attributeValue ) {
			if ( attributeValue == null ) {
				return null;
			}
			if ( attributeValue instanceof Separator ) {
				return (Separator) attributeValue;
			}

			return valueOf( StringUtils.upperCase( Objects.toString( attributeValue ) ) );
		}
	}

	/**
	 * If this attribute exists its value must be a {@link ViewElement}
	 * or {@link com.foreach.across.modules.web.ui.ViewElementBuilder}.  If so the resulting {@link ViewElement}
	 * will be added before the item text.
	 */
	public static final String ATTR_ICON = "nav:icon";

	/**
	 * If set to {@code true} and the item has an {@link #ATTR_ICON} attribute set, only the icon element
	 * will be rendered if the menu item is at the top level.  This attribute will be inherited from the group.
	 */
	public static final String ATTR_ICON_ONLY = "nav:iconOnly";

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
	 * If set to {@code true} the label for the group will never be replaced by the label of the selected item.
	 * This attribute is only relevant if the builder is configured with {@link #replaceGroupBySelectedItem()}.
	 */
	public static final String ATTR_KEEP_GROUP_ITEM = "nav:keepGroupItem";

	/**
	 * Can be set to a value of {@link Separator}.  Determines where a separator should be added if the item is
	 * rendered in a dropdown.
	 */
	public static final String ATTR_INSERT_SEPARATOR = "nav:insertSeparator";

	/**
	 * If a custom {@link ViewElementBuilder} is being used for rendering (part of) a {@link Menu}, the
	 * {@link ViewElementBuilderContext} will contain the {@link Menu} being rendered as an attribute with this name.
	 */
	public static final String CTX_CURRENT_MENU_ITEM = "NavComponentBuilder.currentMenuItem";

	private Menu menu;
	private String menuName;

	private String navStyle = "";
	private boolean replaceGroupBySelectedItem = false;

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

	/**
	 * Shorthand for <code>replaceGroupBySelectedItem(true)</code>.
	 *
	 * @return current builder
	 */
	public NavComponentBuilder replaceGroupBySelectedItem() {
		return replaceGroupBySelectedItem( true );
	}

	/**
	 * If {@code true}, whenever a group has one of its items selected,
	 * the link text for the group will be replaced by the text of the selected item.
	 * Only the url and optional {@link #ATTR_ICON} attribute of the selected item will be returned.
	 * Default behaviour is not to do this but it can be user friendly in a tab navigation.
	 *
	 * @param replaceGroup true to replace the group label
	 * @return current builder
	 */
	public NavComponentBuilder replaceGroupBySelectedItem( boolean replaceGroup ) {
		this.replaceGroupBySelectedItem = replaceGroup;
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
			boolean iconOnly = item.isGroup()
					? Boolean.TRUE.equals( item.getAttribute( ATTR_ICON_ONLY ) )
					: Boolean.TRUE.equals( itemToRender.getAttribute( ATTR_ICON_ONLY ) );

			if ( iconOnly
					|| !addViewElementIfAttributeExists( item, ATTR_ITEM_VIEW_ELEMENT, list,
					                                     builderContext ) ) {
				NodeViewElement li = new NodeViewElement( "li" );
				addHtmlAttributes( li, item.getAttributes() );
				if ( itemToRender.isSelected() ) {
					li.addCssClass( "active" );
				}

				if ( itemToRender.isGroup() ) {
					buildDropDownItem( li, itemToRender, iconOnly, builderContext );
				}
				else {
					addItemLink( li, itemToRender, iconOnly, builderContext );
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
			int numberOfChildren = numberOfNonDisabledChildren( item );

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
					return findFirstActiveChild( item );
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

	private void buildDropDownItem( NodeViewElement li,
	                                Menu item,
	                                boolean iconOnly,
	                                ViewElementBuilderContext builderContext ) {
		li.addCssClass( "dropdown" );

		if ( !addViewElementIfAttributeExists( item, ATTR_LINK_VIEW_ELEMENT, li, builderContext ) ) {
			LinkViewElement link = new LinkViewElement();
			link.setUrl( "#" );
			link.addCssClass( "dropdown-toggle" );
			link.setAttribute( "data-toggle", "dropdown" );

			if ( item.isSelected() && replaceGroupBySelectedItem
					&& !Boolean.TRUE.equals( item.getAttribute( ATTR_KEEP_GROUP_ITEM ) ) ) {
				Menu selected = getFirstNonGroupSelectedItem( item );
				link.setTitle( selected.getTitle() );
				addIconAndText( link, selected, iconOnly, builderContext );
			}
			else {
				link.setTitle( item.getTitle() );
				addIconAndText( link, item, iconOnly, builderContext );
			}

			link.addChild( new TextViewElement( " " ) );

			NodeViewElement caret = new NodeViewElement( "span" );
			caret.addCssClass( "caret" );
			link.addChild( caret );

			li.addChild( link );
		}

		NodeViewElement children = new NodeViewElement( "ul" );
		AtomicBoolean nextChildShouldBeSeparator = new AtomicBoolean( false );
		children.addCssClass( "dropdown-menu" );
		item.getItems()
		    .stream()
		    .filter( i -> !i.isDisabled() )
		    .forEach( child -> addDropDownChildItem( children, child, nextChildShouldBeSeparator, builderContext ) );

		li.addChild( children );
	}

	private void addDropDownChildItem(
			NodeViewElement list,
			Menu item,
			AtomicBoolean nextChildShouldBeSeparator,
			ViewElementBuilderContext builderContext
	) {
		Menu itemToRender = findItemToRender( item );

		if ( itemToRender != null ) {
			boolean shouldInsertSeparator =
					nextChildShouldBeSeparator.get()
							|| ( list.hasChildren()
							&& ( itemToRender.isGroup() || Separator.insertBefore( itemToRender ) ) );

			if ( shouldInsertSeparator ) {
				NodeViewElement divider = new NodeViewElement( "li" );
				divider.addCssClass( "divider" );
				divider.setAttribute( "role", "separator" );
				list.addChild( divider );

				nextChildShouldBeSeparator.set( false );
			}

			if ( itemToRender.isGroup() ) {
				NodeViewElement header = new NodeViewElement( "li" );
				header.addCssClass( "dropdown-header" );
				addIconAndText( header, itemToRender, false, builderContext );
				list.addChild( header );

				itemToRender.getItems()
				            .stream()
				            .filter( i -> !i.isGroup() && !i.isDisabled() )
				            .forEach( child -> addDropDownChildItem( list, child, nextChildShouldBeSeparator,
				                                                     builderContext ) );

				nextChildShouldBeSeparator.set( true );
			}
			else {
				NodeViewElement li = new NodeViewElement( "li" );
				addHtmlAttributes( li, itemToRender.getAttributes() );
				if ( itemToRender.isSelected() ) {
					li.addCssClass( "active" );
				}
				addItemLink( li, itemToRender, false, builderContext );
				list.addChild( li );

				nextChildShouldBeSeparator.set( Separator.insertAfter( itemToRender ) );
			}
		}
	}

	private Menu getFirstNonGroupSelectedItem( Menu menu ) {
		Menu selected = menu.getSelectedItem();
		return selected.isGroup() ? getFirstNonGroupSelectedItem( selected ) : selected;
	}

	private void addItemLink( NodeViewElement li,
	                          Menu item,
	                          boolean iconOnly,
	                          ViewElementBuilderContext builderContext ) {
		if ( iconOnly || !addViewElementIfAttributeExists( item, ATTR_LINK_VIEW_ELEMENT, li, builderContext ) ) {
			LinkViewElement link = new LinkViewElement();
			link.setUrl( buildLink( item.getUrl(), builderContext ) );
			link.setTitle( item.getTitle() );
			addIconAndText( link, item, iconOnly, builderContext );

			li.addChild( link );
		}
	}

	private void addIconAndText( AbstractNodeViewElement node,
	                             Menu item,
	                             boolean iconOnly,
	                             ViewElementBuilderContext builderContext ) {
		boolean iconAdded = addViewElementIfAttributeExists( item, ATTR_ICON, node, builderContext );
		if ( !iconAdded || !iconOnly ) {
			node.addChild( new TextViewElement( ( iconAdded ? " " : "" ) + item.getTitle() ) );
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

	private int numberOfNonDisabledChildren( Menu menu ) {
		return menu.getItems()
		           .stream()
		           .filter( i -> !i.isDisabled() )
		           .mapToInt( i -> i.isGroup() ? numberOfNonDisabledChildren( i ) : 1 )
		           .sum();
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
