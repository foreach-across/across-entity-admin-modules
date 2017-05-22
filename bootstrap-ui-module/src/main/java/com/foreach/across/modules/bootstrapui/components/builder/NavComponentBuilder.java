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
import org.springframework.util.Assert;

import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Abstract base class for rendering {@link Menu} items to nav-like structures.
 *
 * @author Arne Vandamme
 * @see DefaultNavComponentBuilder
 * @see PanelsNavComponentBuilder
 * @see BreadcrumbNavComponentBuilder
 * @since 1.0.0
 */
@SuppressWarnings("unchecked")
@RequiredArgsConstructor
public abstract class NavComponentBuilder<SELF extends NavComponentBuilder<SELF>> extends AbstractLinkSupportingNodeViewElementBuilder<NodeViewElement, SELF>
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
	 * <p/>
	 * Note that the actual title will still be added wrapped in a span with class <strong>nav-item-title</strong>.
	 * This supports for example collapsing navbars where the title should be visible anyway.
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
	 * This attribute is only relevant if the builder is @ {@link DefaultNavComponentBuilder} configured with
	 * {@link DefaultNavComponentBuilder#replaceGroupBySelectedItem()}.
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

	private boolean keepGroupsAsGroup = false;

	private Predicate<Menu> predicate = menu -> true;

	/**
	 * Set the name of the menu to render.  The {@link Menu} should be available as an
	 * attribute with that name on the {@link ViewElementBuilderContext}.
	 * <p/>
	 * NOTE: this value will be ignored if {@link #menu(Menu)} was set.
	 *
	 * @param menuName name of the menu attribute
	 * @return current builder
	 */
	public SELF menu( String menuName ) {
		this.menuName = menuName;
		return (SELF) this;
	}

	/**
	 * Set the fixed menu to render.
	 *
	 * @param menu to render
	 * @return current builder
	 */
	public SELF menu( Menu menu ) {
		this.menu = menu;
		return (SELF) this;
	}

	/**
	 * Set a predicate that menu items should match before they will be rendered.
	 * By default all menu items will match.
	 *
	 * @param predicate to match
	 * @return current builder
	 */
	public SELF filter( Predicate<Menu> predicate ) {
		Assert.notNull( predicate );
		this.predicate = predicate;
		return (SELF) this;
	}

	/**
	 * Set to true if the behaviour for groups should be to keep them as group unless they have a {@link #ATTR_KEEP_AS_GROUP} set.
	 * Default is <strong>not</strong> to keep them as group but to replace them by the item if there is only one.
	 *
	 * @return current builder
	 */
	public SELF keepGroupsAsGroup( boolean keepGroupsAsGroup ) {
		this.keepGroupsAsGroup = keepGroupsAsGroup;
		return (SELF) this;
	}

	@Override
	protected NodeViewElement createElement( ViewElementBuilderContext builderContext ) {
		return buildMenu( retrieveMenu( builderContext ), builderContext );
	}

	protected abstract NodeViewElement buildMenu( Menu menu, ViewElementBuilderContext builderContext );

	protected Stream<Menu> includedItems( Menu menu ) {
		return menu.getItems().stream().filter( this::shouldIncludeItem );
	}

	protected boolean shouldIncludeItem( Menu item ) {
		return !item.isDisabled() && predicate.test( item );
	}

	protected Menu findItemToRender( Menu item ) {
		if ( item.isDisabled() ) {
			return null;
		}

		if ( item.isGroup() ) {
			int numberOfChildren = numberOfChildrenToInclude( item );

			if ( numberOfChildren == 1 && !item.hasAttribute( ATTR_ITEM_VIEW_ELEMENT ) && !shouldKeepAsGroup( item ) ) {
				Menu candidate = findFirstIncludedChild( item );
				if ( candidate != null ) {
					return candidate;
				}
			}

			return numberOfChildren > 0 ? item : null;
		}

		return item;
	}

	protected Menu findFirstIncludedChild( Menu menu ) {
		for ( Menu item : menu.getItems() ) {
			if ( shouldIncludeItem( item ) ) {
				if ( item.isGroup() ) {
					return findFirstIncludedChild( item );
				}
				else {
					return item;
				}
			}

		}
		return null;
	}

	protected void addHtmlAttributes( NodeViewElement node, Map<String, Object> attributes ) {
		attributes.forEach( ( name, value ) -> {
			if ( StringUtils.startsWith( name, PREFIX_HTML_ATTRIBUTE ) ) {
				node.setAttribute( StringUtils.removeStart( name, PREFIX_HTML_ATTRIBUTE ), value );
			}
		} );
	}

	protected boolean shouldKeepAsGroup( Menu item ) {
		return ( keepGroupsAsGroup && !Boolean.FALSE.equals( item.getAttribute( ATTR_KEEP_AS_GROUP ) )
				|| Boolean.TRUE.equals( item.getAttribute( ATTR_KEEP_AS_GROUP ) ) );
	}

	protected Menu getFirstNonGroupSelectedItem( Menu menu ) {
		Menu selected = menu.getSelectedItem();
		return selected.isGroup() ? getFirstNonGroupSelectedItem( selected ) : selected;
	}

	protected LinkViewElement addItemLink( NodeViewElement container,
	                            Menu item,
	                            boolean iconAllowed,
	                            boolean iconOnly,
	                            ViewElementBuilderContext builderContext ) {
		if ( iconOnly || !addViewElementIfAttributeExists( item, ATTR_LINK_VIEW_ELEMENT, container, builderContext ) ) {
			LinkViewElement link = new LinkViewElement();
			link.setUrl( buildLink( item.getUrl(), builderContext ) );
			link.setTitle( item.getTitle() );
			addIconAndText( link, item, iconAllowed, iconOnly, builderContext );

			container.addChild( link );

			return link;
		}

		return null;
	}

	protected void addIconAndText( AbstractNodeViewElement node,
	                               Menu item,
	                               boolean iconAllowed,
	                               boolean iconOnly,
	                               ViewElementBuilderContext builderContext ) {
		boolean iconAdded = iconAllowed && addViewElementIfAttributeExists( item, ATTR_ICON, node, builderContext );
		if ( !iconAdded || !iconOnly ) {
			node.addChild( new TextViewElement( ( iconAdded ? " " : "" ) + item.getTitle() ) );
		}

		if ( iconAdded && iconOnly ) {
			node.addChild( TextViewElement.text( " " ) );

			NodeViewElement span = new NodeViewElement( "span" );
			span.addCssClass( "nav-item-title" );
			span.addChild( TextViewElement.text( item.getTitle() ) );
			node.addChild( span );
		}
	}

	/**
	 * @return true if an element was added
	 */
	protected boolean addViewElementIfAttributeExists( Menu item,
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

	protected int numberOfChildrenToInclude( Menu menu ) {
		return menu.getItems()
		           .stream()
		           .filter( this::shouldIncludeItem )
		           .mapToInt( i -> i.isGroup() ? numberOfChildrenToInclude( i ) : 1 )
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
