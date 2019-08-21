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

package com.foreach.across.modules.entity.views.processors;

import com.foreach.across.core.annotations.Exposed;
import com.foreach.across.modules.bootstrapui.elements.BootstrapUiBuilders;
import com.foreach.across.modules.bootstrapui.elements.Grid;
import com.foreach.across.modules.bootstrapui.elements.Style;
import com.foreach.across.modules.bootstrapui.elements.builder.ColumnViewElementBuilder;
import com.foreach.across.modules.bootstrapui.elements.builder.FormViewElementBuilder;
import com.foreach.across.modules.entity.views.EntityView;
import com.foreach.across.modules.entity.views.context.EntityViewContext;
import com.foreach.across.modules.entity.views.processors.support.ViewElementBuilderMap;
import com.foreach.across.modules.entity.views.request.EntityViewRequest;
import com.foreach.across.modules.entity.views.support.EntityMessages;
import com.foreach.across.modules.entity.web.EntityLinkBuilder;
import com.foreach.across.modules.spring.security.actions.AllowableAction;
import com.foreach.across.modules.spring.security.actions.AllowableActions;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.elements.builder.ContainerViewElementBuilderSupport;
import com.foreach.across.modules.web.ui.elements.builder.NodeViewElementBuilder;
import lombok.Setter;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Responsible for creating a list view page layout for an entity type.
 * <p/>
 * Will add a basic form on top of the list that can be used for adding filters or action buttons.
 * If {@link #setAddDefaultButtons(boolean)} is {@code true}, the {@link AllowableActions} for the entity type will be inspected and a <strong>create</strong>
 * button will be added if the authenticated principal has the {@link AllowableAction#CREATE} action.
 * <p/>
 * This processor <u>does not fetch the actual items nor renders them</u>.  A fully configured list view would required multiple processors:
 * <ul>
 * <li>{@link PageableExtensionViewProcessor}: for initializing a pageable</li>
 * <li>{@link ListFormViewProcessor}: for building the list form layout</li>
 * <li>{@link AbstractEntityFetchingViewProcessor}: for fetching the items</li>
 * <li>{@link SortableTableRenderingViewProcessor}: for rendering the fetched items</li>
 * </ul>
 *
 * @author Arne Vandamme
 * @see SortableTableRenderingViewProcessor
 * @since 2.0.0
 */
@Component
@Exposed
@Scope("prototype")
public class ListFormViewProcessor extends EntityViewProcessorAdapter
{
	public static final String DEFAULT_FORM_NAME = "entityListForm";

	/**
	 * Should the default create button be added if the principal has the  {@link AllowableAction#CREATE} action?
	 */
	@Setter
	private boolean addDefaultButtons;

	/**
	 * Name of the form.  Will be used as both the actual form element name, as the internal {@link com.foreach.across.modules.web.ui.ViewElement} name.
	 * A builder with that name will be available in the {@link ViewElementBuilderMap} as well, and that's the builder that will be rendered.
	 * Additionally a header row builder will be available as under the formName suffixed with <strong>-header</strong>, optionally a
	 */
	@Setter
	private String formName = DEFAULT_FORM_NAME;

	@Override
	protected void createViewElementBuilders( EntityViewRequest entityViewRequest, EntityView entityView, ViewElementBuilderMap builderMap ) {
		FormViewElementBuilder listForm = BootstrapUiBuilders
				.form()
				.name( formName )
				.formName( formName )
				.noValidate()
				.get();

		String formHeaderRowName = formName + "-header-row";
		NodeViewElementBuilder formHeaderRow = BootstrapUiBuilders.row().name( formHeaderRowName );

		String formHeaderName = formName + "-header";
		ColumnViewElementBuilder formHeader = BootstrapUiBuilders.column( Grid.Device.MD.width( Grid.Width.FULL ) )
		                                                         .name( formHeaderName )
		                                                         .css( "list-header d-flex" );

		formHeaderRow.add( formHeader );
		listForm.add( formHeaderRow );

		builderMap.put( formName, listForm );
		builderMap.put( formHeaderRowName, formHeaderRow );
		builderMap.put( formHeaderName, formHeader );

		addDefaultButtons( entityViewRequest, formHeader, builderMap );
		addViewNameHiddenElement( entityViewRequest, listForm );
	}

	private void addViewNameHiddenElement( EntityViewRequest entityViewRequest, FormViewElementBuilder listForm ) {
		if ( !EntityView.LIST_VIEW_NAME.equals( entityViewRequest.getViewName() ) ) {
			listForm.add( BootstrapUiBuilders.hidden().controlName( "view" ).value( entityViewRequest.getViewName() ) );
		}
	}

	private void addDefaultButtons( EntityViewRequest entityViewRequest, ColumnViewElementBuilder formHeader, ViewElementBuilderMap builderMap ) {
		if ( addDefaultButtons ) {
			EntityViewContext entityViewContext = entityViewRequest.getEntityViewContext();

			EntityMessages entityMessages = entityViewContext.getEntityMessages();
			EntityLinkBuilder linkBuilder = entityViewContext.getLinkBuilder();

			AllowableActions allowableActions = entityViewContext.getAllowableActions();

			if ( allowableActions.contains( AllowableAction.CREATE ) ) {
				String formActionsName = formName + "-actions";
				NodeViewElementBuilder actions = BootstrapUiBuilders.div()
				                                                    .name( formActionsName )
				                                                    .css( "list-header-actions" )
				                                                    .add(
						                                                    BootstrapUiBuilders.button()
						                                                                       .name( "btn-create" )
						                                                                       .link( linkBuilder.create() )
						                                                                       .style( Style.Button.PRIMARY )
						                                                                       .text( entityMessages.createAction() )
				                                                    );
				builderMap.put( formActionsName, actions );
				formHeader.add( actions );
			}
		}
	}

	@Override
	protected void render( EntityViewRequest entityViewRequest,
	                       EntityView entityView,
	                       ContainerViewElementBuilderSupport<?, ?> containerBuilder,
	                       ViewElementBuilderMap builderMap,
	                       ViewElementBuilderContext builderContext ) {
		Optional.ofNullable( builderMap.get( formName ) )
		        .ifPresent( containerBuilder::add );
	}
}
