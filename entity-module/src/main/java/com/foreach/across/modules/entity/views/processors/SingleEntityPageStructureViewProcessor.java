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
import com.foreach.across.modules.adminweb.menu.EntityAdminMenu;
import com.foreach.across.modules.adminweb.ui.PageContentStructure;
import com.foreach.across.modules.bootstrapui.components.BootstrapUiComponentFactory;
import com.foreach.across.modules.entity.views.EntityView;
import com.foreach.across.modules.entity.views.context.EntityViewContext;
import com.foreach.across.modules.entity.views.processors.support.EntityPageStructureRenderedEvent;
import com.foreach.across.modules.entity.views.processors.support.ViewElementBuilderMap;
import com.foreach.across.modules.entity.views.request.EntityViewRequest;
import com.foreach.across.modules.entity.views.support.EntityMessages;
import com.foreach.across.modules.web.menu.MenuFactory;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.elements.ContainerViewElement;
import com.foreach.across.modules.web.ui.elements.builder.ContainerViewElementBuilderSupport;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Builds the default page structure for a single entity page.  Adds page title, sets page layout and optionally adds the entity menu.
 * <p/>
 * If {@link #setAddEntityMenu(boolean)} is {@code true}, the {@link com.foreach.across.modules.adminweb.menu.EntityAdminMenuEvent} will be published
 * for the entity and the resulting menu will be added to the page.
 * <p/>
 * During {@link #postRender(EntityViewRequest, EntityView, ContainerViewElement, ViewElementBuilderContext)}, an {@link EntityPageStructureRenderedEvent}
 * will be published containing the context of the entity that was the base for the page structure.  This allows for event base extending of the page
 * structure in case of associations being rendered.
 *
 * @author Arne Vandamme
 * @since 2.0.0
 */
@Component
@Exposed
@Scope("prototype")
public class SingleEntityPageStructureViewProcessor extends EntityViewProcessorAdapter
{
	private BootstrapUiComponentFactory bootstrapUiComponentFactory;
	private MenuFactory menuFactory;
	private AcrossEventPublisher eventPublisher;

	/**
	 * Should the entity menu be generated and added to the page nav.
	 */
	@Setter
	private boolean addEntityMenu;

	/**
	 * Message code that should be resolved for the title of the page.
	 */
	@Setter
	private String titleMessageCode = EntityMessages.PAGE_TITLE_VIEW;

	@Override
	protected void render( EntityViewRequest entityViewRequest,
	                       EntityView entityView,
	                       ContainerViewElementBuilderSupport<?, ?> containerBuilder,
	                       ViewElementBuilderMap builderMap,
	                       ViewElementBuilderContext builderContext ) {
		EntityViewContext entityViewContext = resolveEntityViewContext( entityViewRequest );
		PageContentStructure page = entityViewRequest.getPageContentStructure();

		// todo: update breadcrumb adminMenu.breadcrumbLeaf( entityConfiguration.getLabel( original ) );

		page.setPageTitle( entityViewContext.getEntityMessages().withNameSingular( titleMessageCode, entityViewContext.getEntityLabel() ) );

		if ( addEntityMenu ) {
			buildEntityMenu( entityViewContext, page, builderContext );
		}
	}

	@Override
	protected void postRender( EntityViewRequest entityViewRequest,
	                           EntityView entityView,
	                           ContainerViewElement container,
	                           ViewElementBuilderContext builderContext ) {
		EntityViewContext entityViewContext = resolveEntityViewContext( entityViewRequest );

		EntityPageStructureRenderedEvent event = new EntityPageStructureRenderedEvent( entityViewRequest, entityView, entityViewContext, builderContext );
		eventPublisher.publish( event );
	}

	@SuppressWarnings("unchecked")
	private void buildEntityMenu( EntityViewContext entityViewContext, PageContentStructure page, ViewElementBuilderContext builderContext ) {
		EntityAdminMenu<?> entityMenu = new EntityAdminMenu<>( entityViewContext.getEntityConfiguration().getEntityType(),
		                                                       entityViewContext.getEntity( Object.class ) );
		menuFactory.buildMenu( entityMenu );

		page.addToNav(
				bootstrapUiComponentFactory.nav( entityMenu )
				                           .tabs()
				                           .replaceGroupBySelectedItem()
				                           .build( builderContext )
		);
	}

	private EntityViewContext resolveEntityViewContext( EntityViewRequest entityViewRequest ) {
		EntityViewContext entityViewContext = entityViewRequest.getEntityViewContext();

		if ( entityViewContext.isForAssociation() ) {
			entityViewContext = entityViewContext.getParentContext();
		}

		return entityViewContext;
	}

	@Autowired
	void setBootstrapUiComponentFactory( BootstrapUiComponentFactory bootstrapUiComponentFactory ) {
		this.bootstrapUiComponentFactory = bootstrapUiComponentFactory;
	}

	@Autowired
	void setMenuFactory( MenuFactory menuFactory ) {
		this.menuFactory = menuFactory;
	}

	@Autowired
	void setEventPublisher( AcrossEventPublisher eventPublisher ) {
		this.eventPublisher = eventPublisher;
	}
}
