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
package com.foreach.across.modules.entity.registrars.repository.associations;

import com.foreach.across.modules.entity.query.AssociatedEntityQueryExecutor;
import com.foreach.across.modules.entity.query.EntityQueryExecutor;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.MutableEntityAssociation;
import com.foreach.across.modules.entity.registry.MutableEntityConfiguration;
import com.foreach.across.modules.entity.registry.MutableEntityRegistry;
import com.foreach.across.modules.entity.views.*;
import com.foreach.across.modules.entity.views.fetchers.AssociationListViewPageFetcher;
import com.foreach.across.modules.entity.views.processors.*;
import com.foreach.across.modules.entity.views.support.EntityMessages;
import com.foreach.across.modules.spring.security.actions.AllowableAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.data.mapping.PersistentProperty;
import org.springframework.stereotype.Component;

import javax.persistence.ManyToOne;

/**
 * Builds the association in the opposite direction.
 *
 * @author Andy Somers, Arne Vandamme
 */
@Component
class ManyToOneEntityAssociationBuilder implements EntityAssociationBuilder
{
	private static final Logger LOG = LoggerFactory.getLogger( ManyToOneEntityAssociationBuilder.class );

	@Autowired
	private AutowireCapableBeanFactory beanFactory;

	@Override
	public boolean supports( PersistentProperty<?> sourceProperty ) {
		return sourceProperty.isAnnotationPresent( ManyToOne.class );
	}

	@Override
	public void buildAssociation( MutableEntityRegistry entityRegistry,
	                              MutableEntityConfiguration to,
	                              PersistentProperty property ) {
		MutableEntityConfiguration from = entityRegistry.getEntityConfiguration( property.getActualType() );
		if ( from != null && canAssociationBeBuilt( from, to ) ) {
			String associationName = to.getName() + "." + property.getName();

			MutableEntityAssociation association = from.createAssociation( associationName );
			association.setTargetEntityConfiguration( to );
			association.setTargetProperty( to.getPropertyRegistry().getProperty( property.getName() ) );

			EntityQueryExecutor<?> queryExecutor = to.getAttribute( EntityQueryExecutor.class );
			association.setAttribute(
					AssociatedEntityQueryExecutor.class,
					new AssociatedEntityQueryExecutor<>( association.getTargetProperty(), queryExecutor )
			);

//			buildCreateView( association );
//			buildUpdateView( association );
//			buildListView( association, property );
//			buildDeleteView( association );
		}
	}

	private boolean canAssociationBeBuilt( MutableEntityConfiguration from, MutableEntityConfiguration to ) {
		if ( !to.hasAttribute( EntityQueryExecutor.class ) ) {
			LOG.warn(
					"Unable to build association between {} and {} because {} does not provide an EntityQueryExecutor.",
					from.getName(), to.getName(), to.getName() );
			return false;
		}

		return true;
	}

	public void buildListView( MutableEntityAssociation association, final PersistentProperty property ) {
		EntityConfiguration to = association.getTargetEntityConfiguration();

		EntityListViewFactory oldViewFactory = beanFactory.getBean( EntityListViewFactory.class );
		BeanUtils.copyProperties( to.getViewFactory( EntityView.LIST_VIEW_NAME ), oldViewFactory );

		oldViewFactory.setMessagePrefixes( "entityViews.association." + association.getName() + ".listView",
		                                "entityViews.listView",
		                                "entityViews" );

		EntityQueryExecutor<?> queryExecutor = to.getAttribute( EntityQueryExecutor.class );

		if ( queryExecutor != null ) {
			association.setAttribute(
					AssociatedEntityQueryExecutor.class,
					new AssociatedEntityQueryExecutor<>( association.getTargetProperty(), queryExecutor )
			);
			oldViewFactory.setPageFetcher(
					new AssociationListViewPageFetcher( association.getTargetProperty(), queryExecutor )
			);
		}
		else {
			LOG.warn( "Unable to create ManyToOne association {} as there is no EntityQueryExecutor available",
			          association.getName() );
		}

		association.registerView( EntityView.LIST_VIEW_NAME, oldViewFactory );

		DefaultEntityViewFactory viewFactory = beanFactory.createBean( DefaultEntityViewFactory.class );
		viewFactory.addProcessor( new MessagePrefixingViewProcessor( "entityViews.association." + association.getName() + ".listView",
		                                                             "entityViews.listView",
		                                                             "entityViews" ) );

		ActionAllowedAuthorizationViewProcessor actionAllowedAuthorizationViewProcessor = new ActionAllowedAuthorizationViewProcessor();
		actionAllowedAuthorizationViewProcessor.setRequiredAllowableAction( AllowableAction.READ );
		viewFactory.addProcessor( actionAllowedAuthorizationViewProcessor );

		viewFactory.addProcessor( beanFactory.createBean( GlobalPageFeedbackViewProcessor.class ) );

		PageableExtensionViewProcessor pageableExtensionViewProcessor = new PageableExtensionViewProcessor();
		viewFactory.addProcessor( pageableExtensionViewProcessor );

		ListFormViewProcessor listFormViewProcessor = beanFactory.createBean( ListFormViewProcessor.class );
		listFormViewProcessor.setAddDefaultButtons( true );
		viewFactory.addProcessor( listFormViewProcessor );

		DefaultEntityFetchingViewProcessor pageFetcher = new DefaultEntityFetchingViewProcessor();
		viewFactory.addProcessor( pageFetcher );

		SingleEntityPageStructureViewProcessor pageStructureViewProcessor = beanFactory.createBean( SingleEntityPageStructureViewProcessor.class );
		pageStructureViewProcessor.setAddEntityMenu( true );
		pageStructureViewProcessor.setTitleMessageCode( EntityMessages.PAGE_TITLE_UPDATE );
		viewFactory.addProcessor( pageStructureViewProcessor );

		SortableTableRenderingViewProcessor tableRenderingViewProcessor = beanFactory.createBean( SortableTableRenderingViewProcessor.class );
		tableRenderingViewProcessor.setIncludeDefaultActions( true );
		viewFactory.addProcessor( tableRenderingViewProcessor );

		association.registerView( "new-" + EntityView.LIST_VIEW_NAME, viewFactory );
	}

	public void buildCreateView( MutableEntityAssociation association ) {
		EntityConfiguration to = association.getTargetEntityConfiguration();

		EntityFormViewFactory oldViewFactory = beanFactory.getBean( EntityFormViewFactory.class );
		BeanUtils.copyProperties( to.getViewFactory( EntityView.CREATE_VIEW_NAME ), oldViewFactory );
		oldViewFactory.setMessagePrefixes( "entityViews.association." + association.getName() + ".createView",
		                                "entityViews.createView",
		                                "entityViews" );

		association.registerView( EntityView.CREATE_VIEW_NAME, oldViewFactory );

		DefaultEntityViewFactory viewFactory = beanFactory.createBean( DefaultEntityViewFactory.class );
		viewFactory.addProcessor( new MessagePrefixingViewProcessor(  "entityViews.association." + association.getName() + ".createView",
		                                                              "entityViews.createView",
		                                                              "entityViews" ) );

		ActionAllowedAuthorizationViewProcessor actionAllowedAuthorizationViewProcessor = new ActionAllowedAuthorizationViewProcessor();
		actionAllowedAuthorizationViewProcessor.setRequiredAllowableAction( AllowableAction.CREATE );
		viewFactory.addProcessor( actionAllowedAuthorizationViewProcessor );

		viewFactory.addProcessor( beanFactory.createBean( GlobalPageFeedbackViewProcessor.class ) );

		PropertyRenderingViewProcessor propertyRenderingViewProcessor = beanFactory.createBean( PropertyRenderingViewProcessor.class );
		propertyRenderingViewProcessor.setViewElementMode( ViewElementMode.FORM_WRITE );
		viewFactory.addProcessor( propertyRenderingViewProcessor );

		SingleEntityPageStructureViewProcessor pageStructureViewProcessor = beanFactory.createBean( SingleEntityPageStructureViewProcessor.class );
		pageStructureViewProcessor.setAddEntityMenu( true );
		pageStructureViewProcessor.setTitleMessageCode( EntityMessages.PAGE_TITLE_UPDATE );
		viewFactory.addProcessor( pageStructureViewProcessor );

		SingleEntityFormViewProcessor formViewProcessor = beanFactory.createBean( SingleEntityFormViewProcessor.class );
		formViewProcessor.setAddDefaultButtons( true );
		formViewProcessor.setAddGlobalBindingErrors( true );
		viewFactory.addProcessor( formViewProcessor );

		SaveEntityViewProcessor saveEntityViewProcessor = beanFactory.createBean( SaveEntityViewProcessor.class );
		viewFactory.addProcessor( saveEntityViewProcessor );

		association.registerView( "new-" + EntityView.CREATE_VIEW_NAME, viewFactory );
	}

	public void buildUpdateView( MutableEntityAssociation association ) {
		EntityConfiguration to = association.getTargetEntityConfiguration();


		DefaultEntityViewFactory viewFactory = beanFactory.createBean( DefaultEntityViewFactory.class );
		viewFactory.addProcessor( new MessagePrefixingViewProcessor(  "entityViews.association." + association.getName() + ".updateView",
		                                                              "entityViews.updateView",
		                                                              "entityViews" ) );

		ActionAllowedAuthorizationViewProcessor actionAllowedAuthorizationViewProcessor = new ActionAllowedAuthorizationViewProcessor();
		actionAllowedAuthorizationViewProcessor.setRequiredAllowableAction( AllowableAction.UPDATE );
		viewFactory.addProcessor( actionAllowedAuthorizationViewProcessor );

		viewFactory.addProcessor( beanFactory.createBean( GlobalPageFeedbackViewProcessor.class ) );

		PropertyRenderingViewProcessor propertyRenderingViewProcessor = beanFactory.createBean( PropertyRenderingViewProcessor.class );
		propertyRenderingViewProcessor.setViewElementMode( ViewElementMode.FORM_WRITE );
		viewFactory.addProcessor( propertyRenderingViewProcessor );

		SingleEntityPageStructureViewProcessor pageStructureViewProcessor = beanFactory.createBean( SingleEntityPageStructureViewProcessor.class );
		pageStructureViewProcessor.setAddEntityMenu( true );
		pageStructureViewProcessor.setTitleMessageCode( EntityMessages.PAGE_TITLE_UPDATE );
		viewFactory.addProcessor( pageStructureViewProcessor );

		SingleEntityFormViewProcessor formViewProcessor = beanFactory.createBean( SingleEntityFormViewProcessor.class );
		formViewProcessor.setAddDefaultButtons( true );
		formViewProcessor.setAddGlobalBindingErrors( true );
		viewFactory.addProcessor( formViewProcessor );

		SaveEntityViewProcessor saveEntityViewProcessor = beanFactory.createBean( SaveEntityViewProcessor.class );
		viewFactory.addProcessor( saveEntityViewProcessor );

		association.registerView( "new-" + EntityView.UPDATE_VIEW_NAME, viewFactory );
	}

	public void buildDeleteView( MutableEntityAssociation association ) {
		EntityConfiguration to = association.getTargetEntityConfiguration();

		EntityDeleteViewFactory oldViewFactory = beanFactory.getBean( EntityDeleteViewFactory.class );
		BeanUtils.copyProperties( to.getViewFactory( EntityView.DELETE_VIEW_NAME ), oldViewFactory );
		oldViewFactory.setMessagePrefixes( "entityViews.association." + association.getName() + ".deleteView",
		                                "entityViews.deleteView",
		                                "entityViews" );

		association.registerView( EntityView.DELETE_VIEW_NAME, oldViewFactory );

		DefaultEntityViewFactory viewFactory = beanFactory.createBean( DefaultEntityViewFactory.class );
		viewFactory.addProcessor( new MessagePrefixingViewProcessor(  "entityViews.association." + association.getName() + ".deleteView",
		                                                              "entityViews.deleteView",
		                                                              "entityViews" ) );

		ActionAllowedAuthorizationViewProcessor actionAllowedAuthorizationViewProcessor = new ActionAllowedAuthorizationViewProcessor();
		actionAllowedAuthorizationViewProcessor.setRequiredAllowableAction( AllowableAction.DELETE );
		viewFactory.addProcessor( actionAllowedAuthorizationViewProcessor );

		viewFactory.addProcessor( beanFactory.createBean( GlobalPageFeedbackViewProcessor.class ) );

		SingleEntityPageStructureViewProcessor pageStructureViewProcessor = beanFactory.createBean( SingleEntityPageStructureViewProcessor.class );
		pageStructureViewProcessor.setAddEntityMenu( false );
		pageStructureViewProcessor.setTitleMessageCode( EntityMessages.PAGE_TITLE_UPDATE );
		viewFactory.addProcessor( pageStructureViewProcessor );

		SingleEntityFormViewProcessor formViewProcessor = beanFactory.createBean( SingleEntityFormViewProcessor.class );
		formViewProcessor.setAddDefaultButtons( true );
		formViewProcessor.setAddGlobalBindingErrors( true );
		viewFactory.addProcessor( formViewProcessor );

		DeleteEntityViewProcessor deleteEntityViewProcessor = beanFactory.createBean( DeleteEntityViewProcessor.class );
		viewFactory.addProcessor( deleteEntityViewProcessor );

		association.registerView( "new-" + EntityView.DELETE_VIEW_NAME, viewFactory );
	}
}
