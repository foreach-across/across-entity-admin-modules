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

package com.foreach.across.modules.entity.registrars;

import com.foreach.across.core.annotations.OrderInModule;
import com.foreach.across.modules.entity.config.EntityConfigurer;
import com.foreach.across.modules.entity.config.builders.EntitiesConfigurationBuilder;
import com.foreach.across.modules.entity.config.builders.EntityConfigurationBuilder;
import com.foreach.across.modules.entity.query.EntityQueryExecutor;
import com.foreach.across.modules.entity.registry.EntityAssociation;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.EntityRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.Repository;
import org.springframework.stereotype.Component;

import java.util.Collection;

/**
 * Registers the default views for all {@link com.foreach.across.modules.entity.registry.EntityConfiguration} classes that have required attributes set.
 * Default views include all CRUD views.  Any configuration with a repository set should have workable default views.
 * <p/>
 * Also registers the default views for associations.
 *
 * @author Arne Vandamme
 * @since 2.0.0
 */
@OrderInModule(0)
@Scope("prototype")
@Component
final class DefaultEntityViewsRegistrar implements EntityConfigurer
{
	private EntityRegistry entityRegistry;

	@Autowired
	public DefaultEntityViewsRegistrar( EntityRegistry entityRegistry ) {
		this.entityRegistry = entityRegistry;
	}

	@Override
	public void configure( EntitiesConfigurationBuilder entities ) {
		configureConfigurationViews( entities );
		configureAssociationViews( entities );
	}

	@SuppressWarnings("unchecked")
	private void configureAssociationViews( EntitiesConfigurationBuilder entities ) {
		entityRegistry.getEntities()
		              .stream()
		              .filter( entityConfiguration -> !entityConfiguration.getAssociations().isEmpty() )
		              .forEach( entityConfiguration -> {
			              EntityConfigurationBuilder<?> builder = entities.withName( entityConfiguration.getName() );

			              Collection<EntityAssociation> associations = entityConfiguration.getAssociations();
			              associations.stream()
			                          .filter( association -> supportsDefaultViews( association.getTargetEntityConfiguration() ) )
			                          .forEach(
					                          association ->
							                          builder.association(
									                          ab ->
											                          ab.name( association.getName() )
											                            .listView()
											                            .createFormView()
											                            .updateFormView()
											                            .detailView()
											                            .deleteFormView()
							                          )
			                          );
		              } );
	}

	private void configureConfigurationViews( EntitiesConfigurationBuilder entities ) {
		entityRegistry.getEntities()
		              .stream()
		              .filter( this::supportsDefaultViews )
		              .forEach( entityConfiguration -> {
			                        entities.withName( entityConfiguration.getName() )
			                                .listView()
			                                .createFormView()
			                                .updateFormView()
			                                .detailView()
			                                .deleteFormView();
		                        }
		              );
	}

	private boolean supportsDefaultViews( EntityConfiguration entityConfiguration ) {
		Repository repository = entityConfiguration.getAttribute( Repository.class );
		return repository instanceof CrudRepository || entityConfiguration.hasAttribute( EntityQueryExecutor.class );
	}
}
