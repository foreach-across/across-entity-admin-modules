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

import com.foreach.across.modules.bootstrapui.attributes.BootstrapAttributes;
import com.foreach.across.modules.bootstrapui.elements.LinkViewElement;
import com.foreach.across.modules.bootstrapui.styles.AcrossBootstrapStyles;
import com.foreach.across.modules.web.menu.Menu;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.elements.NodeViewElement;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static com.foreach.across.modules.bootstrapui.styles.BootstrapStyles.css;
import static com.foreach.across.modules.web.ui.elements.HtmlViewElements.html;

/**
 * Renders a {@link com.foreach.across.modules.web.menu.Menu} into a panels structure with list-group items.
 *
 * @author Arne Vandamme
 * @since 1.1.0
 */
public class PanelsNavComponentBuilder extends NavComponentBuilder<PanelsNavComponentBuilder>
{
	/**
	 * Holds the CSS class that determines the panel styling.  If set, the default <strong>panel-default</strong> class will be omitted.
	 * Only relevant on group menu items that would result in a panel being rendered.
	 */
	@Deprecated
	public static final String ATTR_PANEL_STYLE = "nav:panelStyle";

	/**
	 * If set to {@code false} on an group menu item that would be rendered as a panel (a group on the top level), no panel will be
	 * rendered but a sidebar nav list will directly be rendered.  Optionally a title will still be included if the group has one.
	 * <p/>
	 * Note that non-panel lists do not support groups as items, these will be ignored.
	 */
	@Deprecated
	public static final String ATTR_RENDER_AS_PANEL = "nav:renderAsPanel";

	private String subMenuBaseId = UUID.randomUUID().toString();

	/**
	 * Optionally set a base id to be used for generating the unique sub-menu ids.
	 * Defaults to a UUID.
	 *
	 * @param htmlId to use as base
	 * @return current builder
	 */
	public PanelsNavComponentBuilder subMenuBaseId( String htmlId ) {
		subMenuBaseId = htmlId;
		return this;
	}

	@Override
	protected NodeViewElement buildMenu( Menu menu, ViewElementBuilderContext builderContext ) {
		NodeViewElement container = apply( html.nav().set( css.nav, css.of( "nav-panels" ), AcrossBootstrapStyles.css.flex.column, AcrossBootstrapStyles.css.flex.nowrap ), builderContext );

		NodeViewElement nonPanelList = null;
		AtomicInteger subMenuCount = new AtomicInteger( 0 );
		if ( menu != null ) {
			for ( Menu item : menu.getItems() ) {
				if ( shouldIncludeItem( item ) ) {
					Menu itemToRender = findItemToRender( item );

					if ( itemToRender != null ) {
						if ( itemToRender.isGroup() ) {
							nonPanelList = null;
							/*if ( Boolean.FALSE.equals( itemToRender.getAttribute( ATTR_RENDER_AS_PANEL ) ) ) {
								addSidebarList( container, itemToRender, builderContext, subMenuCount );
							}
							else {*/
							addPanel( container, itemToRender, builderContext, subMenuCount );
							//}
						}
						else {
							nonPanelList = nonPanelList != null ? nonPanelList : createList( container ).set( AcrossBootstrapStyles.css.margin.bottom.s3 );
							addItemLink( nonPanelList, item, true, false, builderContext )
									.remove( css.nav.link )
									.set( css.listGroup.item, css.listGroup.item.action, witherAttribute( item, null ) );
						}
					}
				}
			}
		}

		return container;
	}

	private void addPanel( NodeViewElement container, Menu item, ViewElementBuilderContext builderContext, AtomicInteger subMenuCount ) {
		NodeViewElement panel = html.div( css.card, AcrossBootstrapStyles.css.margin.bottom.s3 );
		//panel.addCssClass( "panel", StringUtils.defaultString( item.getAttribute( ATTR_PANEL_STYLE ), "panel-default" ) );

		if ( item.hasTitle() ) {
			NodeViewElement heading = html.div( css.card.header );
			addIconAndText( heading, item, builderContext.resolveText( item.getTitle() ), true, false, builderContext );
			panel.addChild( heading );
		}

		NodeViewElement list = createList( panel ).set( css.listGroup, css.listGroup.flush );

		includedItems( item ).forEach( child -> addMenuItem( list, child, builderContext, true, subMenuCount ) );

		panel.set( witherAttribute( item, null ) );

		container.addChild( panel );
	}

	private NodeViewElement createList( NodeViewElement container ) {
		NodeViewElement list = html.div( css.listGroup );
		//list.addCssClass( "nav", "nav-sidebar" );
		container.addChild( list );
		return list;
	}

	private void addMenuItem( NodeViewElement list, Menu item, ViewElementBuilderContext builderContext, boolean inPanel, AtomicInteger subMenuCount ) {
		Menu itemToRender = findItemToRender( item );

		if ( itemToRender != null ) {
			boolean iconOnly = item.isGroup()
					? Boolean.TRUE.equals( item.getAttribute( ATTR_ICON_ONLY ) )
					: Boolean.TRUE.equals( itemToRender.getAttribute( ATTR_ICON_ONLY ) );

			if ( iconOnly || !addViewElementIfAttributeExists( item, ATTR_ITEM_VIEW_ELEMENT, list, builderContext ) ) {

				NodeViewElement li = new NodeViewElement( "li" );

				if ( inPanel ) {
					li.addCssClass( "list-group-item" );
				}

				if ( inPanel || !itemToRender.isGroup() ) {
					addHtmlAttributes( li, item.getAttributes() );

					if ( itemToRender.isGroup() ) {
						addSubMenu( list, itemToRender, builderContext, subMenuCount );
					}
					else {
						if ( itemToRender.isSelected() ) {
							li.addCssClass( "active" );
						}

						addItemLink( list, itemToRender, true, iconOnly, builderContext )
								.remove( css.nav.link )
								.set( css.listGroup.item, css.listGroup.item.action, witherAttribute( itemToRender, item ) );
					}
				}
			}
		}
	}

	private void addSubMenu( NodeViewElement li, Menu item, ViewElementBuilderContext builderContext, AtomicInteger subMenuCount ) {
		String subMenuId = subMenuBaseId + "-" + subMenuCount.incrementAndGet();

		LinkViewElement link = new LinkViewElement();
		link.setUrl( "#a" + subMenuId );
		link.setAttribute( "data-toggle", "collapse" );
		link.set( css.listGroup.item.action, css.listGroup.suffix( "subgroup-toggle" ), witherAttribute( item, null ) );

		String resolvedTitle = builderContext.resolveText( item.getTitle() );
		link.setText( resolvedTitle );
		link.setTitle( resolvedTitle );
		li.addChild( link );

		NodeViewElement list = html.div().setHtmlId( "a" + subMenuId ).set( css.listGroup.suffix( "subgroup" ) );
		//list.addCssClass( "submenu", "list-group" );
		//list.setHtmlId( subMenuId );

		if ( !item.isSelected() ) {
			list.set( css.collapse );
			link.set( css.collapsed );
			//link.addCssClass( "collapsed" );
			//list.addCssClass( "collapse" );
		}
		else {
			link.set( BootstrapAttributes.attribute.aria.expanded( true ) );
			list.set( css.collapse, css.show );
			//link.setAttribute( "aria-expanded", "true" );
			//list.addCssClass( "in" );
			list.set( BootstrapAttributes.attribute.aria.expanded( true ) );
			//list.setAttribute( "aria-expanded", "true" );
		}

		includedItems( item ).forEach( child -> addMenuItem( list, child, builderContext, true, subMenuCount ) );

		li.addChild( list );
	}
}
