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
package com.foreach.across.modules.entity.registrars.repository;

import com.foreach.across.modules.entity.registry.MutableEntityConfiguration;
import com.foreach.across.modules.entity.views.*;
import com.foreach.across.modules.entity.views.processors.*;
import com.foreach.across.modules.entity.views.support.EntityMessages;
import com.foreach.across.modules.spring.security.actions.AllowableAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

/**
 * Attempts to create default views for an EntityConfiguration.
 * Creates a list, read, create, update and delete view if possible.
 */
public class RepositoryEntityViewsBuilder
{
	@Autowired
	private EntityViewFactoryProvider entityViewFactoryProvider;

	@Autowired
	private AutowireCapableBeanFactory beanFactory;

	public void buildViews( MutableEntityConfiguration entityConfiguration ) {
		buildCreateView( entityConfiguration );
		buildUpdateView( entityConfiguration );
		buildDeleteView( entityConfiguration );
		buildListView( entityConfiguration );
	}

	private void buildCreateView( MutableEntityConfiguration entityConfiguration ) {
		EntityFormViewFactory oldViewFactory
				= entityViewFactoryProvider.create( entityConfiguration, EntityFormViewFactory.class );
		oldViewFactory.setMessagePrefixes( "entityViews." + EntityView.CREATE_VIEW_NAME, "entityViews" );
		entityConfiguration.registerView( EntityView.CREATE_VIEW_NAME, oldViewFactory );

		DefaultEntityViewFactory viewFactory = beanFactory.createBean( DefaultEntityViewFactory.class );
		viewFactory.addProcessor( new MessagePrefixingViewProcessor( "entityViews." + EntityView.CREATE_VIEW_NAME, "entityViews" ) );

		ActionAllowedAuthorizationViewProcessor actionAllowedAuthorizationViewProcessor = new ActionAllowedAuthorizationViewProcessor();
		actionAllowedAuthorizationViewProcessor.setRequiredAllowableAction( AllowableAction.CREATE );
		viewFactory.addProcessor( actionAllowedAuthorizationViewProcessor );

		viewFactory.addProcessor( beanFactory.createBean( GlobalPageFeedbackViewProcessor.class ) );

		PropertyRenderingViewProcessor propertyRenderingViewProcessor = beanFactory.createBean( PropertyRenderingViewProcessor.class );
		propertyRenderingViewProcessor.setViewElementMode( ViewElementMode.FORM_WRITE );
		viewFactory.addProcessor( propertyRenderingViewProcessor );

		SingleEntityPageStructureViewProcessor pageStructureViewProcessor = beanFactory.createBean( SingleEntityPageStructureViewProcessor.class );
		pageStructureViewProcessor.setAddEntityMenu( true );
		pageStructureViewProcessor.setTitleMessageCode( EntityMessages.PAGE_TITLE_CREATE );
		viewFactory.addProcessor( pageStructureViewProcessor );

		SingleEntityFormViewProcessor formViewProcessor = beanFactory.createBean( SingleEntityFormViewProcessor.class );
		formViewProcessor.setAddDefaultButtons( true );
		formViewProcessor.setAddGlobalBindingErrors( true );
		viewFactory.addProcessor( formViewProcessor );

		SaveEntityViewProcessor saveEntityViewProcessor = beanFactory.createBean( SaveEntityViewProcessor.class );
		viewFactory.addProcessor( saveEntityViewProcessor );

		entityConfiguration.registerView( "new-" + EntityView.CREATE_VIEW_NAME, viewFactory );
	}

	private void buildUpdateView( MutableEntityConfiguration entityConfiguration ) {
		EntityFormViewFactory oldViewFactory
				= entityViewFactoryProvider.create( entityConfiguration, EntityFormViewFactory.class );
		oldViewFactory.setMessagePrefixes( "entityViews." + EntityView.UPDATE_VIEW_NAME, "entityViews" );
		entityConfiguration.registerView( EntityView.UPDATE_VIEW_NAME, oldViewFactory );

		DefaultEntityViewFactory viewFactory = beanFactory.createBean( DefaultEntityViewFactory.class );
		viewFactory.addProcessor( new MessagePrefixingViewProcessor( "entityViews." + EntityView.CREATE_VIEW_NAME, "entityViews" ) );

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

		entityConfiguration.registerView( "new-" + EntityView.UPDATE_VIEW_NAME, viewFactory );
	}

	private void buildDeleteView( MutableEntityConfiguration entityConfiguration ) {
		EntityDeleteViewFactory oldViewFactory
				= entityViewFactoryProvider.create( entityConfiguration, EntityDeleteViewFactory.class );
		oldViewFactory.setMessagePrefixes( "entityViews." + EntityView.DELETE_VIEW_NAME, "entityViews" );
		entityConfiguration.registerView( EntityView.DELETE_VIEW_NAME, oldViewFactory );

		DefaultEntityViewFactory viewFactory = beanFactory.createBean( DefaultEntityViewFactory.class );
		viewFactory.addProcessor( new MessagePrefixingViewProcessor( "entityViews." + EntityView.DELETE_VIEW_NAME, "entityViews" ) );

		ActionAllowedAuthorizationViewProcessor actionAllowedAuthorizationViewProcessor = new ActionAllowedAuthorizationViewProcessor();
		actionAllowedAuthorizationViewProcessor.setRequiredAllowableAction( AllowableAction.DELETE );
		viewFactory.addProcessor( actionAllowedAuthorizationViewProcessor );

		viewFactory.addProcessor( beanFactory.createBean( GlobalPageFeedbackViewProcessor.class ) );

		SingleEntityPageStructureViewProcessor pageStructureViewProcessor = beanFactory.createBean( SingleEntityPageStructureViewProcessor.class );
		pageStructureViewProcessor.setAddEntityMenu( false );
		pageStructureViewProcessor.setTitleMessageCode( EntityMessages.PAGE_TITLE_DELETE );
		viewFactory.addProcessor( pageStructureViewProcessor );

		SingleEntityFormViewProcessor formViewProcessor = beanFactory.createBean( SingleEntityFormViewProcessor.class );
		formViewProcessor.setAddDefaultButtons( true );
		formViewProcessor.setAddGlobalBindingErrors( true );
		viewFactory.addProcessor( formViewProcessor );

		DeleteEntityViewProcessor deleteEntityViewProcessor = beanFactory.createBean( DeleteEntityViewProcessor.class );
		viewFactory.addProcessor( deleteEntityViewProcessor );

		entityConfiguration.registerView( "new-" + EntityView.DELETE_VIEW_NAME, viewFactory );
	}

	private void buildListView( MutableEntityConfiguration entityConfiguration ) {

		EntityListViewFactory oldViewFactory
				= entityViewFactoryProvider.create( entityConfiguration, EntityListViewFactory.class );
		oldViewFactory.setMessagePrefixes( "entityViews." + EntityView.LIST_VIEW_NAME, "entityViews" );
		entityConfiguration.registerView( EntityView.LIST_VIEW_NAME, oldViewFactory );

		DefaultEntityViewFactory viewFactory = beanFactory.createBean( DefaultEntityViewFactory.class );
		viewFactory.addProcessor( new MessagePrefixingViewProcessor( "entityView." + EntityView.LIST_VIEW_NAME, "entityViews" ) );

		ActionAllowedAuthorizationViewProcessor actionAllowedAuthorizationViewProcessor = new ActionAllowedAuthorizationViewProcessor();
		actionAllowedAuthorizationViewProcessor.setRequiredAllowableAction( AllowableAction.READ );
		viewFactory.addProcessor( actionAllowedAuthorizationViewProcessor );

		viewFactory.addProcessor( beanFactory.createBean( GlobalPageFeedbackViewProcessor.class ) );

		PageableExtensionViewProcessor pageableExtensionViewProcessor = new PageableExtensionViewProcessor();
		pageableExtensionViewProcessor.setDefaultPageable( new PageRequest( 0, 20, new Sort( Sort.Direction.DESC, "name" ) ) );
		viewFactory.addProcessor( pageableExtensionViewProcessor );

		ListFormViewProcessor listFormViewProcessor = beanFactory.createBean( ListFormViewProcessor.class );
		listFormViewProcessor.setAddDefaultButtons( true );
		viewFactory.addProcessor( listFormViewProcessor );

		viewFactory.addProcessor( beanFactory.createBean( EntityQueryFilterProcessor.class ) );

		DefaultEntityFetchingViewProcessor pageFetcher = new DefaultEntityFetchingViewProcessor();
		viewFactory.addProcessor( pageFetcher );

		SortableTableRenderingViewProcessor tableRenderingViewProcessor = beanFactory.createBean( SortableTableRenderingViewProcessor.class );
		tableRenderingViewProcessor.setIncludeDefaultActions( true );
		viewFactory.addProcessor( tableRenderingViewProcessor );

		entityConfiguration.registerView( "new-" + EntityView.LIST_VIEW_NAME, viewFactory );
	}
}
