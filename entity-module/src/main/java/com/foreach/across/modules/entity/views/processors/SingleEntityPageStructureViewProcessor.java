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
import com.foreach.across.modules.adminweb.menu.AdminMenu;
import com.foreach.across.modules.adminweb.ui.PageContentStructure;
import com.foreach.across.modules.bootstrapui.components.builder.DefaultNavComponentBuilder;
import com.foreach.across.modules.bootstrapui.elements.BootstrapUiBuilders;
import com.foreach.across.modules.entity.conditionals.ConditionalOnAdminWeb;
import com.foreach.across.modules.entity.registry.EntityAssociation;
import com.foreach.across.modules.entity.views.EntityView;
import com.foreach.across.modules.entity.views.context.EntityViewContext;
import com.foreach.across.modules.entity.views.menu.EntityAdminMenu;
import com.foreach.across.modules.entity.views.processors.support.EntityPageStructureRenderedEvent;
import com.foreach.across.modules.entity.views.processors.support.ViewElementBuilderMap;
import com.foreach.across.modules.entity.views.request.EntityViewRequest;
import com.foreach.across.modules.entity.views.support.EntityMessages;
import com.foreach.across.modules.web.menu.MenuFactory;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.elements.ContainerViewElement;
import com.foreach.across.modules.web.ui.elements.TextViewElement;
import com.foreach.across.modules.web.ui.elements.builder.ContainerViewElementBuilderSupport;
import com.foreach.across.modules.web.ui.elements.builder.NodeViewElementBuilder;
import com.foreach.across.modules.web.ui.elements.builder.TextViewElementBuilder;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Optional;

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
@ConditionalOnAdminWeb
@Component
@Exposed
@Scope("prototype")
@Accessors(chain = true)
public class SingleEntityPageStructureViewProcessor extends EntityViewProcessorAdapter
{
	private MenuFactory menuFactory;
	private ApplicationEventPublisher eventPublisher;

	/**
	 * Should the entity menu be generated and added to the page nav.
	 */
	@Setter
	private boolean addEntityMenu;

	/**
	 * Message code that should be resolved for the title of the page.
	 * Will also check if there is a <strong>.subText</strong> version to determine if sub text should be added to the title.
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
		page.setRenderAsTabs( true );

		configureBreadcrumb( entityViewContext );

		EntityMessages entityMessages = entityViewContext.getEntityMessages();

		Optional.ofNullable( StringUtils.defaultIfEmpty( entityMessages.withNameSingular( titleMessageCode, entityViewContext.getEntityLabel() ), null ) )
		        .ifPresent( page::setPageTitle );
		Optional.ofNullable(
				StringUtils.defaultIfEmpty( entityMessages.withNameSingular( titleMessageCode + ".subText", entityViewContext.getEntityLabel() ), null ) )
		        .ifPresent( subText -> page.addToPageTitleSubText( TextViewElement.html( subText ) ) );

		if ( addEntityMenu ) {
			buildEntityMenu( entityViewRequest, page, builderContext );
			addTitleForEmbeddedAssociation( entityViewRequest, page, builderContext );
		}
	}

	private void configureBreadcrumb( EntityViewContext entityViewContext ) {
		AdminMenu adminMenu = (AdminMenu) menuFactory.getMenuWithName( AdminMenu.NAME );
		if ( adminMenu != null && entityViewContext.holdsEntity() ) {
			adminMenu.breadcrumbLeaf(
					entityViewContext.isForAssociation()
							? entityViewContext.getParentContext().getEntityLabel()
							: entityViewContext.getEntityLabel()
			);
		}
	}

	private void addTitleForEmbeddedAssociation( EntityViewRequest entityViewRequest,
	                                             PageContentStructure page,
	                                             ViewElementBuilderContext builderContext ) {
		EntityViewContext entityViewContext = entityViewRequest.getEntityViewContext();
		if ( entityViewContext.isForAssociation() && !EntityView.LIST_VIEW_NAME.equals( entityViewRequest.getViewName() ) ) {
			EntityMessages entityMessages = entityViewContext.getEntityMessages();
			String titleMessageCode = getAssociationTitleMessageCode( entityViewRequest.getViewName() );
			Optional.ofNullable( StringUtils.defaultIfEmpty( entityMessages.withNameSingular( titleMessageCode, entityViewContext.getEntityLabel() ), null ) )
			        .ifPresent( title -> page.addFirstChild( new NodeViewElementBuilder( "h4" )
					                                                 .add( new TextViewElementBuilder().content( title ) )
					                                                 .build( builderContext ) ) );
		}
	}

	private String getAssociationTitleMessageCode( String viewName ) {
		switch ( viewName ) {
			case EntityView.CREATE_VIEW_NAME:
				return EntityMessages.PAGE_TITLE_CREATE;
			case EntityView.DELETE_VIEW_NAME:
				return EntityMessages.PAGE_TITLE_DELETE;
			default:
				return EntityMessages.PAGE_TITLE_UPDATE;
		}
	}

	@Override
	protected void postRender( EntityViewRequest entityViewRequest,
	                           EntityView entityView,
	                           ContainerViewElement container,
	                           ViewElementBuilderContext builderContext ) {
		EntityViewContext entityViewContext = resolveEntityViewContext( entityViewRequest );

		EntityPageStructureRenderedEvent event = new EntityPageStructureRenderedEvent( false, entityViewRequest, entityView, entityViewContext,
		                                                                               builderContext );
		eventPublisher.publishEvent( event );
	}

	@SuppressWarnings("unchecked")
	private void buildEntityMenu( EntityViewRequest entityViewRequest, PageContentStructure page, ViewElementBuilderContext builderContext ) {
		String viewName = entityViewRequest.getViewName();
		EntityViewContext entityViewContext = entityViewRequest.getEntityViewContext();
		if ( entityViewContext.isForAssociation() ) {
			page.addToNav( buildMenu( entityViewContext.getParentContext() ).tabs().build( builderContext ) );

			if ( EntityAssociation.Type.EMBEDDED == entityViewContext.getEntityAssociation().getAssociationType()
					&& !( StringUtils.equals( EntityView.CREATE_VIEW_NAME, viewName ) || StringUtils.equals( EntityView.LIST_VIEW_NAME, viewName ) ) ) {
				page.addFirstChild( buildMenu( entityViewContext ).pills().build( builderContext ) );
			}
		}
		else {
			page.addToHeader( buildMenu( entityViewContext ).tabs().build( builderContext ) );
		}
	}

	private DefaultNavComponentBuilder buildMenu( EntityViewContext context ) {
		EntityAdminMenu entityAdminMenu = EntityAdminMenu.create( context );
		menuFactory.buildMenu( entityAdminMenu );
		return BootstrapUiBuilders.nav( entityAdminMenu )
		                          .replaceGroupBySelectedItem();
	}

	private EntityViewContext resolveEntityViewContext( EntityViewRequest entityViewRequest ) {
		EntityViewContext entityViewContext = entityViewRequest.getEntityViewContext();

		if ( entityViewContext.isForAssociation() ) {
			entityViewContext = entityViewContext.getParentContext();
		}

		return entityViewContext;
	}

	@Autowired
	void setMenuFactory( MenuFactory menuFactory ) {
		this.menuFactory = menuFactory;
	}

	@Autowired
	void setEventPublisher( ApplicationEventPublisher eventPublisher ) {
		this.eventPublisher = eventPublisher;
	}
}
