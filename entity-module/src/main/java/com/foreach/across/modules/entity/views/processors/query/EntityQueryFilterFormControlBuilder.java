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

package com.foreach.across.modules.entity.views.processors.query;

import com.foreach.across.modules.bootstrapui.elements.builder.ButtonViewElementBuilder;
import com.foreach.across.modules.entity.EntityModule;
import com.foreach.across.modules.entity.query.EntityQuery;
import com.foreach.across.modules.web.resource.WebResource;
import com.foreach.across.modules.web.resource.WebResourceRegistry;
import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.ViewElementBuilderSupport;
import com.foreach.across.modules.web.ui.elements.NodeViewElement;
import com.foreach.across.modules.web.ui.elements.builder.NodeViewElementBuilder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;

import static com.foreach.across.modules.bootstrapui.elements.BootstrapUiBuilders.*;
import static com.foreach.across.modules.bootstrapui.elements.icons.IconSet.iconSet;
import static com.foreach.across.modules.entity.config.EntityModuleIcons.ENTITY_QUERY_SEARCH;
import static com.foreach.across.modules.entity.views.processors.EntityQueryFilterProcessor.ENTITY_QUERY_REQUEST;
import static com.foreach.across.modules.web.resource.WebResource.JAVASCRIPT;
import static com.foreach.across.modules.web.resource.WebResource.JAVASCRIPT_PAGE_END;
import static com.foreach.across.modules.web.resource.WebResourceRule.add;

/**
 * Builds the configured form controls for an {@link com.foreach.across.modules.entity.query.EntityQuery} based approach.
 * Offers a basic (controls) and advanced (eql) mode or a combination.
 *
 * @author Steven Gentens
 * @since 2.2.0
 */
public class EntityQueryFilterFormControlBuilder extends ViewElementBuilderSupport<NodeViewElement, EntityQueryFilterFormControlBuilder>
{
	public static final String ATTRIBUTE_ENTITY_QUERY_FILTER_FORM = "data-entity-query-filter-form";

	private static final String ENTITY_QUERY_RESOURCE_KEY = "entityQueryFilterForm";

	private String eqlControlName;
	private String eqlStatement = "";
	private boolean basicFilter = true;
	private boolean advancedFilter = true;
	private boolean showBasicFilter = true;
	private boolean convertibleToBasicMode = true;

	private List<ViewElement> basicFilterControls = Collections.emptyList();

	/**
	 * Whether or not basic mode is allowed. In basic mode, the provided controls will be rendered.
	 * If used in combination with the advanced mode, the option to switch between them will be provided.
	 *
	 * @param basicFilterEnabled mode is allowed
	 * @return current builder
	 */
	public EntityQueryFilterFormControlBuilder basicFilter( boolean basicFilterEnabled ) {
		this.basicFilter = basicFilterEnabled;
		return this;
	}

	/**
	 * Whether or not advanced mode is allowed. In advanced mode, the user is able to search using of EQL statements.
	 * If used in combination with the basic mode, the option to switch between them will be provided.
	 *
	 * @param advancedFilterEnabled mode is allowed
	 * @return current builder
	 */
	public EntityQueryFilterFormControlBuilder advancedFilter( boolean advancedFilterEnabled ) {
		this.advancedFilter = advancedFilterEnabled;
		return this;
	}

	/**
	 * Call this method to show the basic filter when rendering the control.
	 * If basic filter is not enabled, the advanced filter will be shown instead.
	 *
	 * @return current builder
	 */
	public EntityQueryFilterFormControlBuilder showBasicFilter() {
		showBasicFilter = true;
		return this;
	}

	/**
	 * Call this method to show the advanced filter when rendering the control.
	 * If advanced filter is not enabled, the basic filter will be shown instead.
	 *
	 * @return current builder
	 */
	public EntityQueryFilterFormControlBuilder showAdvancedFilter() {
		showBasicFilter = false;
		return this;
	}

	/**
	 * Sets the controls that should be rendered in basic mode.
	 *
	 * @param controls to be rendered
	 * @return current builder
	 */
	public EntityQueryFilterFormControlBuilder basicControls( List<ViewElement> controls ) {
		basicFilterControls = controls;
		return this;
	}

	/**
	 * Sets the current query on the advanced control.
	 *
	 * @param query to be set
	 * @return current builder
	 */
	public EntityQueryFilterFormControlBuilder query( EntityQuery query ) {
		return query( query.toString() );
	}

	/**
	 * Sets the current query on the advanced control.
	 *
	 * @param query to be set
	 * @return current builder
	 */
	public EntityQueryFilterFormControlBuilder query( String query ) {
		eqlStatement = query;
		return this;
	}

	/**
	 * Sets the control name for the eql statement.
	 *
	 * @param controlName to be set
	 * @return current builder
	 */
	public EntityQueryFilterFormControlBuilder eqlControlName( String controlName ) {
		eqlControlName = controlName;
		return this;
	}

	/**
	 * Sets whether the query is convertible to basic mode.
	 *
	 * @param convertibleToBasicMode if possible to convert
	 * @return current builder
	 */
	public EntityQueryFilterFormControlBuilder convertibleToBasicMode( boolean convertibleToBasicMode ) {
		this.convertibleToBasicMode = convertibleToBasicMode;
		return this;
	}

	@Override
	protected NodeViewElement createElement( ViewElementBuilderContext builderContext ) {
		NodeViewElementBuilder container = div()
				.name( "entity-query-filter-form" )
				.css( "entity-query-filter-form flex-grow-1" )
				.attribute( ATTRIBUTE_ENTITY_QUERY_FILTER_FORM, "default" );

		boolean basicFilterEnabled = isBasicModeActive();
		boolean advancedFilterEnabled = advancedFilter;
		boolean showBasicFilter = ( this.showBasicFilter && basicFilterEnabled ) || !advancedFilterEnabled;

		if ( basicFilterEnabled || advancedFilterEnabled ) {
			ButtonViewElementBuilder searchButton = button()
					.link()
					.submit()
					.text( builderContext.resolveText( "#{entityQueryFilter.searchButton}" ) )
					.icon( iconSet( EntityModule.NAME ).icon( ENTITY_QUERY_SEARCH ) );

			if ( basicFilterEnabled ) {
				NodeViewElementBuilder basicFilter = div().name( "entity-query-filter-form-basic" )
				                                          .css( "entity-query-filter-form-basic", showBasicFilter ? "" : "d-none" );
				basicFilter.addAll( basicFilterControls )
				           .add( searchButton );

				if ( advancedFilterEnabled ) {
					basicFilter.add(
							button()
									.attribute( "data-entity-query-filter-form-link", "advanced" )
									.text( builderContext.resolveText( "#{entityQueryFilter.linkToAdvancedMode}" ) )
									.link()
					);
				}

				container.add( basicFilter );
			}

			if ( !advancedFilterEnabled ) {
				container.add( hidden().controlName( eqlControlName ).value( eqlStatement ) );
			}
			else {
				NodeViewElementBuilder advancedFilter = div().name( "entity-query-filter-form-advanced" )
				                                             .css( "entity-query-filter-form-advanced ", showBasicFilter ? "d-none" : "d-flex" );

				NodeViewElementBuilder actions = div().css( "list-header-actions" )
				                                      .add( searchButton );

				advancedFilter
						.add(
								formGroup()
										.css( "form-group flex-grow-1" )
										.control( textbox()
												          .controlName( eqlControlName )
												          .text( eqlStatement )
												          .placeholder( builderContext.resolveText( "#{entityQueryFilter.eqlPlaceholder}" ) ) )
										.postProcessor( ( ctx, group ) -> {
											                String helpText = ctx.resolveText( "#{entityQueryFilter.eqlDescription}" );
											                if ( StringUtils.isNotEmpty( helpText ) ) {
												                group.setHelpBlock( helpBlock( helpText ).build( ctx ) );
											                }
										                }
										) )
						.add( actions );

				if ( basicFilterEnabled ) {
					ButtonViewElementBuilder button = button()
							.attribute( "data-entity-query-filter-form-link", "basic" )
							.text( builderContext.resolveText( "#{entityQueryFilter.linkToBasicMode}" ) )
							.link();

					if ( !convertibleToBasicMode ) {
						button.disable();
						actions.add( div().add( button )
						                  .attribute( "title", builderContext.resolveText( "#{entityQueryFilter.linkToBasicMode[impossibleTooltip]}" ) )
						                  .css( "disabled-button-wrapper" )
						);
					}
					else {
						actions.add( button );
					}
					container.add( hidden().css( "js-entity-query-filter-form-show-basic-filter" )
					                       .controlName( "extensions[" + ENTITY_QUERY_REQUEST + "].showBasicFilter" )
					                       .value( this.showBasicFilter ) );
				}

				container.add( advancedFilter );
			}

		}

		return container.build( builderContext );
	}

	private boolean isBasicModeActive() {
		return basicFilter && !CollectionUtils.isEmpty( basicFilterControls );
	}

	@Override
	protected void registerWebResources( WebResourceRegistry webResourceRegistry ) {
		if ( isBasicModeActive() ) {
			webResourceRegistry.apply(
					// Lodash
					add( WebResource.javascript( "@webjars:/lodash/4.17.4/lodash.min.js" ) )
							.withKey( "lodash" )
							.toBucket( JAVASCRIPT ),

					add( WebResource.javascript( "@static:/entity/js/entity-query.js" ) )
							.withKey( ENTITY_QUERY_RESOURCE_KEY )
							.toBucket( JAVASCRIPT_PAGE_END )
			);
		}
	}
}
