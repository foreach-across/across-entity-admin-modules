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

package com.foreach.across.modules.entity.views;

import com.foreach.across.core.events.AcrossEventPublisher;
import com.foreach.across.modules.adminweb.menu.EntityAdminMenu;
import com.foreach.across.modules.adminweb.ui.PageContentStructure;
import com.foreach.across.modules.bootstrapui.components.BootstrapUiComponentFactory;
import com.foreach.across.modules.bootstrapui.elements.Grid;
import com.foreach.across.modules.bootstrapui.elements.Style;
import com.foreach.across.modules.entity.controllers.EntityControllerAttributes;
import com.foreach.across.modules.entity.query.AssociatedEntityQueryExecutor;
import com.foreach.across.modules.entity.query.EntityQuery;
import com.foreach.across.modules.entity.registry.EntityAssociation;
import com.foreach.across.modules.entity.registry.EntityAssociation.ParentDeleteMode;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.support.EntityMessageCodeResolver;
import com.foreach.across.modules.entity.views.events.BuildEntityDeleteViewEvent;
import com.foreach.across.modules.entity.views.support.EntityMessages;
import com.foreach.across.modules.entity.web.EntityLinkBuilder;
import com.foreach.across.modules.entity.web.WebViewCreationContext;
import com.foreach.across.modules.web.menu.Menu;
import com.foreach.across.modules.web.menu.MenuFactory;
import com.foreach.across.modules.web.ui.elements.ContainerViewElement;
import com.foreach.across.modules.web.ui.elements.builder.ContainerViewElementBuilder;
import com.foreach.across.modules.web.ui.elements.support.ContainerViewElementUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;

import java.util.Optional;

/**
 * Creates the delete form for an entity.
 *
 * @author Arne Vandamme
 * @since 2.0.0
 */
@Deprecated
public class EntityDeleteViewFactory
		extends SimpleEntityViewFactorySupport
{
	private static final Logger LOG = LoggerFactory.getLogger( EntityDeleteViewFactory.class );

	public static final String FORM_NAME = "entityDeleteForm";

	private AcrossEventPublisher eventPublisher;

	private MenuFactory menuFactory;
	private BootstrapUiComponentFactory bootstrapUiComponentFactory;

	@Autowired
	public void setMenuFactory( MenuFactory menuFactory ) {
		this.menuFactory = menuFactory;
	}

	@Autowired
	public void setBootstrapUiComponentFactory( BootstrapUiComponentFactory bootstrapUiComponentFactory ) {
		this.bootstrapUiComponentFactory = bootstrapUiComponentFactory;
	}

	@Autowired
	public void setEventPublisher( AcrossEventPublisher eventPublisher ) {
		this.eventPublisher = eventPublisher;
	}

	@Override
	protected EntityView createEntityView( ModelMap model ) {
		return new EntityView( model );
	}

	@Override
	protected void preparePageContentStructure( PageContentStructure page, WebViewCreationContext creationContext, EntityView view ) {
		super.preparePageContentStructure( page, creationContext, view );

		EntityMessages entityMessages = view.getEntityMessages();
		EntityConfiguration<Object> entityConfiguration = view.getEntityConfiguration();

		Object entity = view.getEntity();

		if ( creationContext.isForAssociation() ) {
			entity = view.getParentEntity();
			entityConfiguration = creationContext.getEntityAssociation().getSourceEntityConfiguration();
			entityMessages = new EntityMessages( entityConfiguration.getEntityMessageCodeResolver() );
		}

		String entityLabel = entityConfiguration.getLabel( entity );

		if ( creationContext.isForAssociation() ) {

			Class<?> entityType = entityConfiguration.getEntityType();
			Menu menu = menuFactory.buildMenu( new EntityAdminMenu( entityConfiguration.getEntityType(),
			                                                        entityType.cast( entity ) ) );
			page.setPageTitle( entityMessages.updatePageTitle( entityLabel ) );
			page.addToNav(
					bootstrapUiComponentFactory.nav( menu )
					                           .tabs()
					                           .replaceGroupBySelectedItem()
					                           .build()
			);
		}
		else {
			page.setPageTitle( entityMessages.deletePageTitle( entityLabel ) );
		}
	}

	@Override
	protected void buildViewModel( WebViewCreationContext WebViewCreationContext,
	                               EntityConfiguration entityConfiguration,
	                               EntityMessageCodeResolver codeResolver,
	                               EntityView view ) {
		ViewElementBuilderContext<EntityView> builderContext = new ViewElementBuilderContext<>( view );

		EntityLinkBuilder linkBuilder = view.getEntityLinkBuilder();
		EntityMessages messages = view.getEntityMessages();

		BuildEntityDeleteViewEvent viewConfiguration = buildViewConfiguration( entityConfiguration, view,
		                                                                       builderContext, messages );

		ContainerViewElementBuilder buttons = buildButtons( linkBuilder, messages, viewConfiguration,
		                                                    WebViewCreationContext );

		String confirmationMessage = messages.withNameSingular( "views.deleteView.confirmation" );
		if ( viewConfiguration.isDeleteDisabled() ) {
			confirmationMessage = messages.withNameSingular( "views.deleteView.deleteDisabled" );
		}

		view.setViewElements(
				bootstrapUi
						.form()
						.name( EntityFormViewFactory.FORM_NAME )
						.commandAttribute( EntityControllerAttributes.VIEW_REQUEST )
						.post()
						.noValidate()
						.action( linkBuilder.delete( view.getEntity() ) )
						.add(
								bootstrapUi
										.row()
										.add(
												bootstrapUi
														.column( Grid.Device.MD.width( Grid.Width.HALF ) )
														.name( EntityFormViewFactory.FORM_LEFT )
														.add( viewConfiguration.messages() )
														.add(
																bootstrapUi
																		.paragraph()
																		.css(
																				viewConfiguration.isDeleteDisabled()
																						? Style.DANGER.forPrefix(
																						"text" ) : "" )
																		.add( bootstrapUi
																				      .text( confirmationMessage ) ) )
										)
										.add(
												bootstrapUi.column( Grid.Device.MD.width( Grid.Width.HALF ) )
												           .name( EntityFormViewFactory.FORM_RIGHT )
										)
						)
						.add( buttons )
						.build( builderContext )
		);

		// todo: simplify
		page.addChild( view.getViewElements() );
	}

	private ContainerViewElementBuilder buildButtons( EntityLinkBuilder linkBuilder,
	                                                  EntityMessages messages,
	                                                  BuildEntityDeleteViewEvent viewConfiguration,
	                                                  WebViewCreationContext WebViewCreationContext ) {
		ContainerViewElementBuilder buttons = bootstrapUi.container().name( "buttons" );

		Optional<String> fromUrl = Optional.ofNullable( retrieveFromUrl( WebViewCreationContext ) );
		String cancelUrl = fromUrl.orElseGet( linkBuilder::overview );

		if ( !viewConfiguration.isDeleteDisabled() ) {
			buttons.add(
					bootstrapUi.button()
					           .name( "btn-delete" )
					           .style( Style.DANGER )
					           .submit()
					           .text( messages.messageWithFallback( "buttons.delete" ) )
			);
		}
		buttons.add(
				bootstrapUi.button()
				           .name( "btn-cancel" )
				           .link( cancelUrl )
				           .style( viewConfiguration.isDeleteDisabled() ? Style.PRIMARY : Style.Button.LINK )
				           .text( messages.messageWithFallback( "actions.cancel" ) )
		);
		return buttons;
	}

	private String retrieveFromUrl( WebViewCreationContext webViewCreationContext ) {
		return webViewCreationContext.getRequest().getParameter( "from" );
	}

	private BuildEntityDeleteViewEvent buildViewConfiguration( EntityConfiguration<?> entityConfiguration,
	                                                           EntityView view,
	                                                           ViewElementBuilderContext<EntityView> builderContext,
	                                                           EntityMessages messages ) {
		BuildEntityDeleteViewEvent<?> event = new BuildEntityDeleteViewEvent<>( view.getEntity(), builderContext );
		event.setDeleteDisabled( false );

		ContainerViewElement associations = bootstrapUi.node( "ul" ).build( builderContext );

		event.setAssociations( associations );
		event.setMessages(
				bootstrapUi.container()
				           .add(
						           bootstrapUi.container()
						                      .name( "associations" )
						                      .add( bootstrapUi.paragraph()
						                                       .add( bootstrapUi.text( messages.withNameSingular(
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
						if ( ParentDeleteMode.SUPPRESS == association.getParentDeleteMode() ) {
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
				bootstrapUi
						.node( "li" )
						.name( association.getName() )
						.add( bootstrapUi
								      .link()
								      .url( linkBuilder.overview() )
								      .text( title ) )
						.build( viewConfiguration.getBuilderContext() )
		);
	}

	private int countAssociatedItems( EntityAssociation association, Object parent ) {
		if ( ParentDeleteMode.IGNORE != association.getParentDeleteMode() ) {
			AssociatedEntityQueryExecutor executor = association.getAttribute(
					AssociatedEntityQueryExecutor.class );

			if ( executor != null ) {
				return executor.findAll( parent, EntityQuery.all() ).size();
			}
		}

		return 0;
	}
}
