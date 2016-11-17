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
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Attempts to create default views for an EntityConfiguration.
 * Creates a list, read, create, update and delete view if possible.
 */
public class RepositoryEntityViewsBuilder
{
	@Autowired
	private EntityViewFactoryProvider entityViewFactoryProvider;

	public void buildViews( MutableEntityConfiguration entityConfiguration ) {
		buildCreateView( entityConfiguration );
		buildUpdateView( entityConfiguration );
		buildDeleteView( entityConfiguration );
		buildListView( entityConfiguration );
	}

	private void buildCreateView( MutableEntityConfiguration entityConfiguration ) {
		EntityFormViewFactory viewFactory
				= entityViewFactoryProvider.create( entityConfiguration, EntityFormViewFactory.class );
		viewFactory.setMessagePrefixes( "entityViews." + EntityFormView.CREATE_VIEW_NAME, "entityViews" );

		entityConfiguration.registerView( EntityFormView.CREATE_VIEW_NAME, viewFactory );
	}

	private void buildUpdateView( MutableEntityConfiguration entityConfiguration ) {
		EntityFormViewFactory viewFactory
				= entityViewFactoryProvider.create( entityConfiguration, EntityFormViewFactory.class );
		viewFactory.setMessagePrefixes( "entityViews." + EntityFormView.UPDATE_VIEW_NAME, "entityViews" );

		entityConfiguration.registerView( EntityFormView.UPDATE_VIEW_NAME, viewFactory );
	}

	private void buildDeleteView( MutableEntityConfiguration entityConfiguration ) {
		EntityDeleteViewFactory viewFactory
				= entityViewFactoryProvider.create( entityConfiguration, EntityDeleteViewFactory.class );
		viewFactory.setMessagePrefixes( "entityViews." + EntityFormView.DELETE_VIEW_NAME, "entityViews" );

		entityConfiguration.registerView( EntityFormView.DELETE_VIEW_NAME, viewFactory );
	}

	private void buildListView( MutableEntityConfiguration entityConfiguration ) {
		EntityListViewFactory viewFactory
				= entityViewFactoryProvider.create( entityConfiguration, EntityListViewFactory.class );
		viewFactory.setMessagePrefixes( "entityViews." + EntityListView.VIEW_NAME, "entityViews" );

		entityConfiguration.registerView( EntityListView.VIEW_NAME, viewFactory );
	}
}
