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
import com.foreach.across.modules.bootstrapui.elements.BootstrapUiFactory;
import com.foreach.across.modules.entity.query.AssociatedEntityQueryExecutor;
import com.foreach.across.modules.entity.query.EntityQuery;
import com.foreach.across.modules.entity.registry.EntityAssociation;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.views.EntityView;
import com.foreach.across.modules.entity.views.ViewElementBuilderContext;
import com.foreach.across.modules.entity.views.events.BuildEntityDeleteViewEvent;
import com.foreach.across.modules.entity.views.processors.support.ViewElementBuilderMap;
import com.foreach.across.modules.entity.views.request.EntityViewCommand;
import com.foreach.across.modules.entity.views.request.EntityViewRequest;
import com.foreach.across.modules.entity.views.support.EntityMessages;
import com.foreach.across.modules.entity.web.EntityLinkBuilder;
import com.foreach.across.modules.web.ui.elements.ContainerViewElement;
import com.foreach.across.modules.web.ui.elements.builder.ContainerViewElementBuilderSupport;
import com.foreach.across.modules.web.ui.elements.support.ContainerViewElementUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

/**
 * Responsible for rendering the actual delete entity page, and performing the delete action if necessary.
 *
 * @author Arne Vandamme
 * @since 2.0.0
 */
@Slf4j
@Component
@Exposed
@Scope("prototype")
public class DeleteEntityViewProcessor extends EntityViewProcessorAdapter
{
	private AcrossEventPublisher eventPublisher;
	private BootstrapUiFactory bootstrapUiFactory;

	@Override
	protected void doPost( EntityViewRequest entityViewRequest, EntityView entityView, EntityViewCommand command, BindingResult bindingResult ) {
		// perform the actual delete
	}

	@Override
	protected void render( EntityViewRequest entityViewRequest,
	                       EntityView entityView,
	                       ContainerViewElementBuilderSupport<?, ?> containerBuilder,
	                       ViewElementBuilderMap builderMap,
	                       com.foreach.across.modules.web.ui.ViewElementBuilderContext builderContext ) {
		// publish the configuration
		// add the messages to the form body
	}

	@Override
	protected void postRender( EntityViewRequest entityViewRequest,
	                           EntityView entityView,
	                           ContainerViewElement container,
	                           com.foreach.across.modules.web.ui.ViewElementBuilderContext builderContext ) {
		// modify the buttons to match the allowed configuration
	}

	private BuildEntityDeleteViewEvent buildViewConfiguration( EntityConfiguration<?> entityConfiguration,
	                                                           EntityView view,
	                                                           ViewElementBuilderContext<EntityView> builderContext,
	                                                           EntityMessages messages ) {
		BuildEntityDeleteViewEvent<?> event = new BuildEntityDeleteViewEvent<>( view.getEntity(), builderContext );
		event.setDeleteDisabled( false );

		ContainerViewElement associations = bootstrapUiFactory.node( "ul" ).build( builderContext );

		event.setAssociations( associations );
		event.setMessages(
				bootstrapUiFactory.container()
				                  .add(
						                  bootstrapUiFactory.container()
						                                    .name( "associations" )
						                                    .add( bootstrapUiFactory.paragraph()
						                                                            .add( bootstrapUiFactory.text( messages.withNameSingular(
								                                                            "views.deleteView.associations" ) ) ) )
						                                    .add( associations )
				                  )
				                  .build( builderContext )
		);

		buildAssociations( entityConfiguration, view, event );

		eventPublisher.publish( event );

		// Remove the associations block if no associations were added
		if ( !event.associations().hasChildren() ) {
			ContainerViewElementUtils.remove( event.messages(), "associations" );
		}

		return event;
	}

	private void buildAssociations(
			EntityConfiguration<?> entityConfiguration,
			EntityView view,
			BuildEntityDeleteViewEvent viewConfiguration ) {
		Object parent = view.getEntity();
		EntityLinkBuilder parentLinkBuilder = view.getEntityLinkBuilder();

		LOG.trace( "Fetching associated items and disabling delete if parent delete mode is SUPPRESS" );

		entityConfiguration.getAssociations().forEach(
				association -> {
					int count = countAssociatedItems( association, parent );

					if ( count > 0 ) {
						if ( EntityAssociation.ParentDeleteMode.SUPPRESS == association.getParentDeleteMode() ) {
							LOG.trace( "Suppressing delete action because association {} has {} items",
							           association.getName(), count );
							viewConfiguration.setDeleteDisabled( true );
						}

						if ( !association.isHidden() ) {
							addAssociationInfo( viewConfiguration, parent, parentLinkBuilder, association, count );
						}
					}
				}
		);

		LOG.trace( "Delete disabled after association check: {}", viewConfiguration.isDeleteDisabled() );
	}

	private void addAssociationInfo( BuildEntityDeleteViewEvent viewConfiguration,
	                                 Object parent,
	                                 EntityLinkBuilder parentLinkBuilder,
	                                 EntityAssociation association,
	                                 int itemCount ) {
		EntityMessages messages = new EntityMessages(
				association.getTargetEntityConfiguration().getEntityMessageCodeResolver()
		);

		EntityLinkBuilder linkBuilder = association
				.getAttribute( EntityLinkBuilder.class )
				.asAssociationFor( parentLinkBuilder, parent );

		String title = messages.withNamePlural( "views.deleteView.associatedResults", itemCount );

		viewConfiguration.associations().addChild(
				bootstrapUiFactory
						.node( "li" )
						.name( association.getName() )
						.add( bootstrapUiFactory
								      .link()
								      .url( linkBuilder.overview() )
								      .text( title ) )
						.build( viewConfiguration.getBuilderContext() )
		);
	}

	private int countAssociatedItems( EntityAssociation association, Object parent ) {
		if ( EntityAssociation.ParentDeleteMode.IGNORE != association.getParentDeleteMode() ) {
			AssociatedEntityQueryExecutor executor = association.getAttribute(
					AssociatedEntityQueryExecutor.class );

			if ( executor != null ) {
				return executor.findAll( parent, EntityQuery.all() ).size();
			}
		}

		return 0;
	}

	@Autowired
	void setEventPublisher( AcrossEventPublisher eventPublisher ) {
		this.eventPublisher = eventPublisher;
	}
}
