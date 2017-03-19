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
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.mapping.PersistentProperty;
import org.springframework.stereotype.Component;

import javax.persistence.OneToMany;

/**
 * @author Andy Somers
 */
@Component
class OneToManyEntityAssociationBuilder implements EntityAssociationBuilder
{
	private static final Logger LOG = LoggerFactory.getLogger( OneToManyEntityAssociationBuilder.class );

	@Autowired
	private AutowireCapableBeanFactory beanFactory;

	@Override
	public boolean supports( PersistentProperty<?> sourceProperty ) {
		return sourceProperty.isAnnotationPresent( OneToMany.class );
	}

	@Override
	public void buildAssociation( MutableEntityRegistry entityRegistry,
	                              MutableEntityConfiguration entityConfiguration,
	                              PersistentProperty property ) {
		MutableEntityConfiguration other
				= entityRegistry.getEntityConfiguration( property.getActualType() );

		if ( other != null ) {
			String mappedBy = (String) AnnotationUtils.getValue( property.findAnnotation( OneToMany.class ),
			                                                     "mappedBy" );

			if ( StringUtils.isBlank( mappedBy ) ) {
				LOG.warn( "Unable to process unidirectional @OneToMany relationship." );
			}
			else {
				String associationName = entityConfiguration.getName() + "." + property.getName();

				MutableEntityAssociation association = entityConfiguration.createAssociation( associationName );
				association.setAttribute( PersistentProperty.class, property );
				association.setSourceProperty( entityConfiguration.getPropertyRegistry().getProperty(
						property.getName() ) );
				association.setTargetEntityConfiguration( other );
				association.setTargetProperty( other.getPropertyRegistry().getProperty( mappedBy ) );

				// Hide by default as will be managed through the property
				association.setHidden( true );

//				buildCreateView( association );
//				buildListView( association, property );
//				buildDeleteView( association );
			}
		}
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
			LOG.warn( "Unable to create OneToMany association {} as there is no EntityQueryExecutor available",
			          association.getName() );
		}

		association.registerView( EntityView.LIST_VIEW_NAME, oldViewFactory );

		// new
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

		EntityFormViewFactory viewFactory = beanFactory.getBean( EntityFormViewFactory.class );
		BeanUtils.copyProperties( to.getViewFactory( EntityView.CREATE_VIEW_NAME ), viewFactory );
		viewFactory.setMessagePrefixes( "entityViews.association." + association.getName() + ".createView",
		                                "entityViews.createView",
		                                "entityViews" );

		association.registerView( EntityView.CREATE_VIEW_NAME, viewFactory );
	}

	public void buildDeleteView( MutableEntityAssociation association ) {
		EntityConfiguration to = association.getTargetEntityConfiguration();

		EntityDeleteViewFactory viewFactory = beanFactory.getBean( EntityDeleteViewFactory.class );
		BeanUtils.copyProperties( to.getViewFactory( EntityView.DELETE_VIEW_NAME ), viewFactory );
		viewFactory.setMessagePrefixes( "entityViews.association." + association.getName() + ".deleteView",
		                                "entityViews.deleteView",
		                                "entityViews" );

		association.registerView( EntityView.DELETE_VIEW_NAME, viewFactory );
	}
}
