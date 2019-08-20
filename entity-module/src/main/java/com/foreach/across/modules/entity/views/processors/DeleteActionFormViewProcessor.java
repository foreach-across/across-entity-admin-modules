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

import com.foreach.across.modules.bootstrapui.elements.BootstrapUiBuilders;
import com.foreach.across.modules.bootstrapui.elements.Style;
import com.foreach.across.modules.bootstrapui.elements.builder.ButtonViewElementBuilder;
import com.foreach.across.modules.entity.EntityModule;
import com.foreach.across.modules.entity.conditionals.ConditionalOnAdminWeb;
import com.foreach.across.modules.entity.views.EntityView;
import com.foreach.across.modules.entity.views.context.EntityViewContext;
import com.foreach.across.modules.entity.views.processors.support.ViewElementBuilderMap;
import com.foreach.across.modules.entity.views.request.EntityViewRequest;
import com.foreach.across.modules.entity.views.support.EntityMessages;
import com.foreach.across.modules.entity.web.links.SingleEntityViewLinkBuilder;
import com.foreach.across.modules.spring.security.actions.AllowableAction;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.elements.builder.ContainerViewElementBuilderSupport;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import static com.foreach.across.modules.bootstrapui.elements.icons.IconSet.iconSet;
import static com.foreach.across.modules.entity.config.EntityModuleIcons.DELETE_BUTTON;

/**
 * Adds a delete button to the existing {@link SingleEntityFormViewProcessor#FORM_BUTTONS} if the {@link AllowableAction#DELETE} is present.
 *
 * @author Steven Gentens
 * @since 3.2.0
 */
@Component
@ConditionalOnAdminWeb
public class DeleteActionFormViewProcessor extends EntityViewProcessorAdapter
{
	@SuppressWarnings("unchecked")
	@Override
	protected void render( EntityViewRequest entityViewRequest,
	                       EntityView entityView,
	                       ContainerViewElementBuilderSupport<?, ?> containerBuilder,
	                       ViewElementBuilderMap builderMap,
	                       ViewElementBuilderContext builderContext ) {
		EntityViewContext entityViewContext = entityViewRequest.getEntityViewContext();

		if ( entityViewContext.getAllowableActions().contains( AllowableAction.DELETE ) ) {
			ContainerViewElementBuilderSupport buttonsContainer = builderMap.get( SingleEntityFormViewProcessor.FORM_BUTTONS,
			                                                                      ContainerViewElementBuilderSupport.class );

			if ( buttonsContainer != null ) {
				buttonsContainer.add( createDeleteButton( entityViewContext, entityViewRequest.getViewName() ).css( "pull-right" ) );
			}
		}
	}

	/**
	 * Helper function to create a default delete button builder for a given entity.
	 * Will generate a link to the delete view for that entity, taking the optional current view name into account to generate
	 * an appropriate from url.
	 * <p/>
	 * By default the delete button is styled as an actual button with {@link Style#DANGER}.
	 *
	 * @param entityViewContext representing the current entity
	 * @param currentViewName   current view being rendered - can be {@code null} but might impact from url if present
	 * @return button builder
	 */
	public static ButtonViewElementBuilder createDeleteButton( @NonNull EntityViewContext entityViewContext, String currentViewName ) {
		Object entity = entityViewContext.getEntity();

		EntityMessages messages = entityViewContext.getEntityMessages();
		SingleEntityViewLinkBuilder links = entityViewContext.getLinkBuilder().forInstance( entity );
		SingleEntityViewLinkBuilder linkToDeleteView = links.deleteView();

		if ( StringUtils.equals( EntityView.UPDATE_VIEW_NAME, currentViewName ) ) {
			linkToDeleteView = linkToDeleteView.withFromUrl( links.updateView().toUriString() );
		}
		else if ( StringUtils.equals( EntityView.DETAIL_VIEW_NAME, currentViewName ) ) {
			linkToDeleteView = linkToDeleteView.withFromUrl( links.toUriString() );
		}

		return BootstrapUiBuilders.button()
		                          .name( "btn-delete" )
		                          .link( linkToDeleteView.toUriString() )
		                          .style( Style.DANGER )
		                          .icon( iconSet( EntityModule.NAME).icon( DELETE_BUTTON ) )
		                          .title( messages.messageWithFallback( "buttons.delete" ) );
	}
}
