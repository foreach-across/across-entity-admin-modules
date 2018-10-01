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
import com.foreach.across.modules.entity.views.EntityView;
import com.foreach.across.modules.entity.views.context.EntityViewContext;
import com.foreach.across.modules.entity.views.processors.support.ViewElementBuilderMap;
import com.foreach.across.modules.entity.views.request.EntityViewRequest;
import com.foreach.across.modules.entity.views.support.EntityMessages;
import com.foreach.across.modules.entity.web.links.SingleEntityViewLinkBuilder;
import com.foreach.across.modules.spring.security.actions.AllowableAction;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.elements.builder.ContainerViewElementBuilderSupport;
import org.springframework.stereotype.Component;

/**
 * Adds a delete button to the existing {@link SingleEntityFormViewProcessor#FORM_BUTTONS} if the {@link AllowableAction#DELETE} is present.
 *
 * @author Steven Gentens
 * @since 3.2.0
 */
@Component
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
		Object entity = entityViewContext.getEntity();
		if ( entityViewContext.getEntityConfiguration().getAllowableActions( entity ).contains( AllowableAction.DELETE )
				&& builderMap.containsKey( SingleEntityFormViewProcessor.FORM_BUTTONS ) ) {
			ContainerViewElementBuilderSupport buttonsContainer = builderMap.get( SingleEntityFormViewProcessor.FORM_BUTTONS,
			                                                                      ContainerViewElementBuilderSupport.class );
			EntityMessages messages = entityViewContext.getEntityMessages();
			SingleEntityViewLinkBuilder links = entityViewContext.getLinkBuilder()
			                                                     .forInstance( entity ).deleteView();
			buttonsContainer.add(
					BootstrapUiBuilders.button()
					                   .name( "btn-delete" )
					                   .css( "pull-right" )
					                   .link( links.toUriString() )
					                   .style( Style.DANGER )
					                   .text( messages.messageWithFallback( "buttons.delete" ) )
			);
		}
	}
}
