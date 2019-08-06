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

package com.foreach.across.modules.bootstrapui.components.builder;

import com.foreach.across.modules.bootstrapui.elements.LinkViewElement;
import com.foreach.across.modules.web.menu.Menu;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.elements.NodeViewElement;
import com.foreach.across.modules.web.ui.elements.TextViewElement;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Builds a Bootstrap nav list structure for a {@link Menu} instance.
 * Supports several attributes for influencing how a menu gets translated, see
 * {@link #ATTR_ICON}, {@link #ATTR_ITEM_VIEW_ELEMENT}, {@link #ATTR_LINK_VIEW_ELEMENT}.
 *
 * @author Arne Vandamme
 * @since 1.1.0
 */
public class DefaultNavComponentBuilder extends NavComponentBuilder<DefaultNavComponentBuilder>
{
	private String navStyle = "";
	private boolean replaceGroupBySelectedItem = false;

	/**
	 * Render menu with no specific nav style.
	 *
	 * @return current builder
	 */
	public DefaultNavComponentBuilder simple() {
		navStyle = "";
		return this;
	}

	/**
	 * Render menu as tabs.
	 *
	 * @return current builder
	 */
	public DefaultNavComponentBuilder tabs() {
		navStyle = "nav-tabs";
		return this;
	}

	/**
	 * Render menu as pills.
	 *
	 * @return current builder
	 */
	public DefaultNavComponentBuilder pills() {
		navStyle = "nav-pills";
		return this;
	}

	/**
	 * Render menu as stacked pills.
	 *
	 * @return current builder
	 */
	public DefaultNavComponentBuilder stacked() {
		navStyle = "nav-pills nav-stacked";
		return this;
	}

	/**
	 * Render menu as navbar links.
	 *
	 * @return current builder
	 */
	public DefaultNavComponentBuilder navbar() {
		navStyle = "navbar-nav";
		return this;
	}

	/**
	 * Shorthand for <code>replaceGroupBySelectedItem(true)</code>.
	 *
	 * @return current builder
	 */
	public DefaultNavComponentBuilder replaceGroupBySelectedItem() {
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
	public DefaultNavComponentBuilder replaceGroupBySelectedItem( boolean replaceGroup ) {
		this.replaceGroupBySelectedItem = replaceGroup;
		return this;
	}

	protected NodeViewElement buildMenu( Menu menuToRender, ViewElementBuilderContext builderContext ) {
		NodeViewElement list = apply( new NodeViewElement( "ul" ), builderContext );
		list.addCssClass( "nav", navStyle );

		if ( menuToRender != null ) {
			includedItems( menuToRender ).forEach( item -> addMenuItemToList( list, item, builderContext ) );
		}

		return list;
	}

	private void addMenuItemToList( NodeViewElement list, Menu item, ViewElementBuilderContext builderContext ) {
		Menu itemToRender = findItemToRender( item );

		if ( itemToRender != null ) {
			boolean iconOnly = item.isGroup()
					? Boolean.TRUE.equals( item.getAttribute( ATTR_ICON_ONLY ) )
					: Boolean.TRUE.equals( itemToRender.getAttribute( ATTR_ICON_ONLY ) );

			if ( iconOnly || !addViewElementIfAttributeExists( item, ATTR_ITEM_VIEW_ELEMENT, list, builderContext ) ) {
				NodeViewElement li = new NodeViewElement( "li" );
				li.addCssClass( "nav-item" );
				addHtmlAttributes( li, item.getAttributes() );

				if ( itemToRender.isGroup() ) {
					buildDropDownItem( li, itemToRender, iconOnly, builderContext );
				}
				else {
					addItemLink( li, itemToRender, true, iconOnly, builderContext );
				}

				list.addChild( li );

			}
		}
	}

	private void buildDropDownItem( NodeViewElement li,
	                                Menu item,
	                                boolean iconOnly,
	                                ViewElementBuilderContext builderContext ) {
		li.addCssClass( "dropdown" );

		if ( !addViewElementIfAttributeExists( item, ATTR_LINK_VIEW_ELEMENT, li, builderContext ) ) {
			LinkViewElement link = new LinkViewElement();
			link.setUrl( "#" );
			link.addCssClass( "nav-link", "dropdown-toggle" );
			if ( item.isSelected() ) {
				link.addCssClass( "active" );
			}
			link.setAttribute( "data-toggle", "dropdown" );
			link.setAttribute( "role", "button" );
			link.setAttribute( "aria-haspopup", true );
			link.setAttribute( "aria-expanded", false );

			if ( item.isSelected() && replaceGroupBySelectedItem
					&& !Boolean.TRUE.equals( item.getAttribute( ATTR_KEEP_GROUP_ITEM ) ) ) {
				Menu selected = getFirstNonGroupSelectedItem( item );
				String resolvedTitle = builderContext.resolveText( selected.getTitle() );
				link.setTitle( resolvedTitle );
				addIconAndText( link, selected, resolvedTitle, true, iconOnly, builderContext );
			}
			else {
				String resolvedTitle = builderContext.resolveText( item.getTitle() );
				link.setTitle( resolvedTitle );
				addIconAndText( link, item, resolvedTitle, true, iconOnly, builderContext );
			}

			link.addChild( new TextViewElement( " " ) );
			li.addChild( link );
		}

		NodeViewElement children = new NodeViewElement( "div" );
		AtomicBoolean nextChildShouldBeSeparator = new AtomicBoolean( false );
		children.addCssClass( "dropdown-menu" );

		includedItems( item ).forEach( child -> addDropDownChildItem( children, child, nextChildShouldBeSeparator, builderContext ) );

		li.addChild( children );
	}

	private void addDropDownChildItem(
			NodeViewElement dropDown,
			Menu item,
			AtomicBoolean nextChildShouldBeSeparator,
			ViewElementBuilderContext builderContext
	) {
		Menu itemToRender = findItemToRender( item );

		if ( itemToRender != null ) {
			boolean shouldInsertSeparator =
					nextChildShouldBeSeparator.get()
							|| ( dropDown.hasChildren()
							&& ( itemToRender.isGroup() || Separator.insertBefore( itemToRender ) ) );

			if ( shouldInsertSeparator ) {
				NodeViewElement divider = new NodeViewElement( "div" );
				divider.addCssClass( "dropdown-divider" );
				dropDown.addChild( divider );

				nextChildShouldBeSeparator.set( false );
			}

			if ( itemToRender.isGroup() ) {
				NodeViewElement header = new NodeViewElement( "h6" );
				header.addCssClass( "dropdown-header" );
				addIconAndText( header, itemToRender, builderContext.resolveText( itemToRender.getTitle() ), true, false, builderContext );
				dropDown.addChild( header );

				includedItems( itemToRender )
						.filter( i -> !i.isGroup() )
						.forEach( child -> addDropDownChildItem( dropDown, child, nextChildShouldBeSeparator, builderContext ) );

				nextChildShouldBeSeparator.set( true );
			}
			else {
				LinkViewElement link = addItemLink( dropDown, itemToRender, true, false, builderContext );
				link.addCssClass( "dropdown-item" );
				link.removeCssClass( "nav-link" );
				addHtmlAttributes( link, itemToRender.getAttributes() );
				if ( itemToRender.isSelected() ) {
					link.addCssClass( "active" );
				}

				nextChildShouldBeSeparator.set( Separator.insertAfter( itemToRender ) );
			}
		}
	}
}
