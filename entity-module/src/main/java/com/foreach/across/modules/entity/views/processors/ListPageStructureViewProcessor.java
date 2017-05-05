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
import com.foreach.across.core.events.AcrossEventPublisher;
import com.foreach.across.modules.adminweb.ui.PageContentStructure;
import com.foreach.across.modules.entity.views.EntityView;
import com.foreach.across.modules.entity.views.context.EntityViewContext;
import com.foreach.across.modules.entity.views.processors.support.EntityPageStructureRenderedEvent;
import com.foreach.across.modules.entity.views.processors.support.ViewElementBuilderMap;
import com.foreach.across.modules.entity.views.request.EntityViewRequest;
import com.foreach.across.modules.entity.views.support.EntityMessages;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.elements.ContainerViewElement;
import com.foreach.across.modules.web.ui.elements.TextViewElement;
import com.foreach.across.modules.web.ui.elements.builder.ContainerViewElementBuilderSupport;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Builds the default page structure for a list view page.  Adds page title and sets the page layout.
 * <p/>
 * During {@link #postRender(EntityViewRequest, EntityView, ContainerViewElement, ViewElementBuilderContext)}, an {@link EntityPageStructureRenderedEvent}
 * will be published containing the context of the entity that was the base for the page structure.
 *
 * @author Arne Vandamme
 * @since 2.1.0
 */
@Component
@Exposed
@Scope("prototype")
public class ListPageStructureViewProcessor extends EntityViewProcessorAdapter
{
	private AcrossEventPublisher eventPublisher;

	/**
	 * Message code that should be resolved for the title of the page.
	 * Will also check if there is a <strong>.subText</strong> version to determine if sub text should be added to the title.
	 */
	@Setter
	private String titleMessageCode = EntityMessages.PAGE_TITLE_LIST;

	@Override
	protected void render( EntityViewRequest entityViewRequest,
	                       EntityView entityView,
	                       ContainerViewElementBuilderSupport<?, ?> containerBuilder,
	                       ViewElementBuilderMap builderMap,
	                       ViewElementBuilderContext builderContext ) {
		EntityViewContext entityViewContext = entityViewRequest.getEntityViewContext();
		PageContentStructure page = entityViewRequest.getPageContentStructure();

		EntityMessages entityMessages = entityViewContext.getEntityMessages();

		Optional.ofNullable( StringUtils.defaultIfEmpty( entityMessages.withNameSingular( titleMessageCode, entityViewContext.getEntityLabel() ), null ) )
		        .ifPresent( page::setPageTitle );
		Optional.ofNullable(
				StringUtils.defaultIfEmpty( entityMessages.withNameSingular( titleMessageCode + ".subText", entityViewContext.getEntityLabel() ), null ) )
		        .ifPresent( subText -> page.addToPageTitleSubText( TextViewElement.html( subText ) ) );
	}

	@Override
	protected void postRender( EntityViewRequest entityViewRequest,
	                           EntityView entityView,
	                           ContainerViewElement container,
	                           ViewElementBuilderContext builderContext ) {
		EntityPageStructureRenderedEvent event = new EntityPageStructureRenderedEvent( true, entityViewRequest, entityView,
		                                                                               entityViewRequest.getEntityViewContext(), builderContext );
		eventPublisher.publish( event );
	}

	@Autowired
	void setEventPublisher( AcrossEventPublisher eventPublisher ) {
		this.eventPublisher = eventPublisher;
	}
}
