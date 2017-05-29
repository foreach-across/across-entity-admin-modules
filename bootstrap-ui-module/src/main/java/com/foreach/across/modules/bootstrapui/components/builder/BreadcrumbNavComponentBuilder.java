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
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.elements.NodeViewElement;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Renders the selected path of a {@link com.foreach.across.modules.web.menu.Menu} to a breadcrumb list.
 *
 * @author Arne Vandamme
 * @since 1.1.0
 */
public class BreadcrumbNavComponentBuilder extends NavComponentBuilder<BreadcrumbNavComponentBuilder>
{
	private int iconLevels = Integer.MAX_VALUE;
	private int iconOnlyLevels = Integer.MAX_VALUE;

	/**
	 * Set the number of levels (breadcrumb segments) for which the icon will be rendered if there is one.
	 * By default all items will have their icons rendered.
	 * <p/>
	 * Set to 0 if you don't want to render any icons.
	 *
	 * @param iconLevels number of levels for which icon should be rendered
	 * @return current builder
	 * @see #iconOnlyLevels
	 */
	public BreadcrumbNavComponentBuilder iconAllowedLevels( int iconLevels ) {
		this.iconLevels = iconLevels;
		return this;
	}

	/**
	 * Set the number of levels (breadcrumb segments) that will be rendered as just the icon if {@link NavComponentBuilder#ATTR_ICON_ONLY} is {@code true}.
	 * By default all segments might be rendered this way.
	 * <p/>
	 * Set this attribute to 1 if you want to allow only the root item to be rendered as an icon.
	 * Set to 0 if you never want an item to be rendered as only the icon in the breadcrumb.
	 *
	 * @param iconOnlyLevels number of levels for which icon should be rendered
	 * @return current builder
	 * @see #iconAllowedLevels(int)
	 */
	public BreadcrumbNavComponentBuilder iconOnlyLevels( int iconOnlyLevels ) {
		this.iconOnlyLevels = iconOnlyLevels;
		return this;
	}

	@Override
	protected NodeViewElement buildMenu( Menu menu, ViewElementBuilderContext builderContext ) {
		NodeViewElement list = apply( new NodeViewElement( "ol" ), builderContext );
		list.addCssClass( "breadcrumb" );

		if ( menu != null ) {
			List<Menu> segments = menu.getSelectedItemPath()
			                          .stream()
			                          .filter( this::shouldIncludeItem )
			                          .collect( Collectors.toList() );

			for ( int i = 0; i < segments.size(); i++ ) {
				addBreadcrumbSegment( list, segments.get( i ), builderContext, i, i == segments.size() - 1 );
			}
		}

		return list;
	}

	protected void addBreadcrumbSegment( NodeViewElement list, Menu item, ViewElementBuilderContext builderContext, int level, boolean isLastItem ) {
		NodeViewElement li = new NodeViewElement( "li" );

		boolean iconOnly = level < iconOnlyLevels && Boolean.TRUE.equals( item.getAttribute( ATTR_ICON_ONLY ) );
		boolean iconAllowed = level < iconLevels;

		if ( isLastItem ) {
			li.addCssClass( "active" );
			addIconAndText( li, item, builderContext.resolveText( item.getTitle() ), iconAllowed, iconOnly, builderContext );
		}
		else {
			if ( item.hasUrl() || !item.isGroup() ) {
				addItemLink( li, item, iconAllowed, iconOnly, builderContext );
			}
			else {
				String url = findFirstNonDisabledChildUrl( item );

				if ( url != null ) {
					LinkViewElement link = addItemLink( li, item, iconAllowed, iconOnly, builderContext );
					if ( link != null ) {
						link.setUrl( buildLink( url, builderContext ) );
					}
				}
				else {
					addIconAndText( li, item, builderContext.resolveText( item.getTitle() ), iconAllowed, iconOnly, builderContext );
				}
			}
		}

		list.addChild( li );
	}

	private String findFirstNonDisabledChildUrl( Menu menu ) {
		for ( Menu item : menu.getItems() ) {
			if ( !item.isDisabled() ) {
				if ( item.isGroup() ) {
					return findFirstNonDisabledChildUrl( item );
				}
				else {
					return item.getUrl();
				}
			}

		}
		return null;
	}
}
