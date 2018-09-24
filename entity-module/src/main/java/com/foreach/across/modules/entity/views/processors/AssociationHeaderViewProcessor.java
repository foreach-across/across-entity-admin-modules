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
import com.foreach.across.modules.adminweb.ui.PageContentStructure;
import com.foreach.across.modules.bootstrapui.elements.BootstrapUiBuilders;
import com.foreach.across.modules.entity.conditionals.ConditionalOnAdminWeb;
import com.foreach.across.modules.entity.registry.EntityAssociation;
import com.foreach.across.modules.entity.views.EntityView;
import com.foreach.across.modules.entity.views.context.EntityViewContext;
import com.foreach.across.modules.entity.views.menu.EntityAdminMenu;
import com.foreach.across.modules.entity.views.processors.support.ViewElementBuilderMap;
import com.foreach.across.modules.entity.views.request.EntityViewRequest;
import com.foreach.across.modules.entity.views.support.EntityMessages;
import com.foreach.across.modules.web.menu.MenuFactory;
import com.foreach.across.modules.web.ui.ViewElementBuilder;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.elements.builder.ContainerViewElementBuilderSupport;
import com.foreach.across.modules.web.ui.elements.builder.NodeViewElementBuilder;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static com.foreach.across.modules.bootstrapui.elements.BootstrapUiBuilders.*;

/**
 * Adds a header component to embedded associations.
 *
 * @see SingleEntityPageStructureViewProcessor
 *
 * @author Steven Gentens
 * @since 3.2.0
 */
@ConditionalOnAdminWeb
@Component
@RequiredArgsConstructor
@Exposed
@Scope("prototype")
@Accessors(chain = true)
public class AssociationHeaderViewProcessor extends EntityViewProcessorAdapter
{
	private final MenuFactory menuFactory;

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
		EntityViewContext entityViewContext = entityViewRequest.getEntityViewContext();
		PageContentStructure page = entityViewRequest.getPageContentStructure();

		NodeViewElementBuilder contentHeaderBuilder = div().css( "tab-pane-header" )
		                                                   .add( associatedContentTitle( entityViewContext ) );

		if ( addEntityMenu ) {
			contentHeaderBuilder.add( associatedContentMenu( entityViewContext ) );
		}
		page.addFirstChild( contentHeaderBuilder.build( builderContext ) );
	}

	private ViewElementBuilder associatedContentMenu( EntityViewContext entityViewContext ) {
		if ( EntityAssociation.Type.EMBEDDED == entityViewContext.getEntityAssociation().getAssociationType() ) {
			EntityAdminMenu entityAdminMenu = EntityAdminMenu.create( entityViewContext );
			menuFactory.buildMenu( entityAdminMenu );
			return BootstrapUiBuilders.nav( entityAdminMenu )
			                          .pills()
			                          .replaceGroupBySelectedItem();
		}
		return null;
	}

	private ViewElementBuilder associatedContentTitle( EntityViewContext entityViewContext ) {
		EntityMessages entityMessages = entityViewContext.getEntityMessages();

		NodeViewElementBuilder header = node( "h4" ).name( "tab-pane-title" );

		Optional.ofNullable( StringUtils.defaultIfEmpty( entityMessages.withNameSingular( titleMessageCode, entityViewContext.getEntityLabel() ), null ) )
		        .ifPresent( title -> header.add( text( title ) )
		                                   .add( text( " " ) ) );
		Optional.ofNullable(
				StringUtils.defaultIfEmpty( entityMessages.withNameSingular( titleMessageCode + ".subText", entityViewContext.getEntityLabel() ), null ) )
		        .ifPresent( subText -> header.add( node( "small" ).name( "tab-pane-title-subtext" ).add( html( subText ) ) ) );
		return header;
	}
}
