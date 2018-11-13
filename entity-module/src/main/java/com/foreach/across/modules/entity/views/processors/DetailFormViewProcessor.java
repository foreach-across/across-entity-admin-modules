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
import com.foreach.across.modules.bootstrapui.elements.Style;
import com.foreach.across.modules.bootstrapui.elements.builder.ButtonViewElementBuilder;
import com.foreach.across.modules.bootstrapui.elements.builder.FormViewElementBuilder;
import com.foreach.across.modules.entity.conditionals.ConditionalOnAdminWeb;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.views.EntityView;
import com.foreach.across.modules.entity.views.context.EntityViewContext;
import com.foreach.across.modules.entity.views.processors.support.ViewElementBuilderMap;
import com.foreach.across.modules.entity.views.request.EntityViewRequest;
import com.foreach.across.modules.entity.views.support.EntityMessages;
import com.foreach.across.modules.entity.web.links.EntityViewLinkBuilder;
import com.foreach.across.modules.entity.web.links.SingleEntityViewLinkBuilder;
import com.foreach.across.modules.spring.security.actions.AllowableAction;
import com.foreach.across.modules.web.ui.elements.builder.ContainerViewElementBuilder;
import com.foreach.across.modules.web.ui.elements.builder.ContainerViewElementBuilderSupport;
import org.springframework.stereotype.Component;

import static com.foreach.across.modules.bootstrapui.elements.BootstrapUiBuilders.button;
import static com.foreach.across.modules.bootstrapui.elements.BootstrapUiBuilders.container;

/**
 * Provides a default modify and back button for a {@link EntityView#DETAIL_VIEW_NAME} view.
 * The modify button will redirect to the {@link EntityView#UPDATE_VIEW_NAME} view of the entity and
 * the back button will return to the entity overview page, unless a specific <strong>from</strong> request attribute is present.
 *
 * @author Steven Gentens
 * @since 3.2.0
 */
@Component
@Exposed
@ConditionalOnAdminWeb
public class DetailFormViewProcessor extends EntityViewProcessorAdapter
{
	@Override
	protected void createViewElementBuilders( EntityViewRequest entityViewRequest,
	                                          EntityView entityView,
	                                          ViewElementBuilderMap builderMap ) {
		FormViewElementBuilder form = (FormViewElementBuilder) builderMap.get( SingleEntityFormViewProcessor.FORM );
		EntityViewContext entityViewContext = entityViewRequest.getEntityViewContext();

		Object entity = entityViewContext.getEntity();
		EntityViewLinkBuilder linkBuilder = entityViewContext.getLinkBuilder();
		SingleEntityViewLinkBuilder linkToEntity = linkBuilder.forInstance( entity );

		String updateUrl = linkToEntity.updateView().withFromUrl( linkToEntity.toUriString() ).toUriString();

		ContainerViewElementBuilderSupport buttons = buildButtonsContainer( entityViewContext, updateUrl, linkBuilder.listView().toUriString() );
		form.add( buttons );
		builderMap.put( SingleEntityFormViewProcessor.FORM_BUTTONS, buttons );
	}

	@SuppressWarnings("unchecked")
	private ContainerViewElementBuilderSupport buildButtonsContainer( EntityViewContext entityViewContext, String updateUrl, String cancelUrl ) {
		EntityMessages messages = entityViewContext.getEntityMessages();
		ContainerViewElementBuilder container = container().name( "buttons" );
		EntityConfiguration entityConfiguration = entityViewContext.getEntityConfiguration();
		Object entity = entityViewContext.getEntity();

		ButtonViewElementBuilder backButton = button().name( "btn-back" )
		                                              .link( cancelUrl )
		                                              .text( messages.messageWithFallback( "actions.back" ) );
		if ( entityConfiguration.getAllowableActions( entity ).contains( AllowableAction.UPDATE ) ) {
			container.add(
					button().name( "btn-update" )
					        .link( updateUrl )
					        .style( Style.PRIMARY )
					        .text( messages.withNameSingular( "actions.modify", entity ) )
			);
		}
		else {
			backButton.style( Style.DEFAULT );
		}

		container.add( backButton );
		return container;
	}
}
