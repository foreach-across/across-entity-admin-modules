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

import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyComparators;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistry;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistryProvider;
import com.foreach.across.modules.hibernate.business.Auditable;
import com.foreach.across.modules.spring.security.infrastructure.business.SecurityPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.Repository;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * Default implementation for creating view for a particular {@link EntityConfiguration}.
 *
 * @author Arne Vandamme
 * @see com.foreach.across.modules.entity.config.builders.EntityViewFactoryBuilder
 * @see com.foreach.across.modules.entity.config.builders.EntityListViewFactoryBuilder
 * @since 2.0.0
 */
@Deprecated
@Component
public class DefaultEntityViewFactoryProvider implements EntityViewFactoryProvider
{
	private final AutowireCapableBeanFactory beanFactory;

	private List<BiConsumer<EntityConfiguration, EntityViewFactory>> configurationViewFactoryPostProcessors
			= new ArrayList<>();

	@Autowired
	public DefaultEntityViewFactoryProvider( AutowireCapableBeanFactory beanFactory ) {
		this.beanFactory = beanFactory;

		configurationViewFactoryPostProcessors.add(
				new EntityConfigurationViewPostProcessor( beanFactory.getBean( EntityPropertyRegistryProvider.class ) )
		);
		configurationViewFactoryPostProcessors.add( new EntityConfigurationListViewPostProcessor() );
	}

	@Autowired(required = false)
	public void setConfigurationViewFactoryPostProcessors( Collection<BiConsumer<EntityConfiguration, EntityViewFactory>> configurationViewFactoryPostProcessors ) {
		this.configurationViewFactoryPostProcessors.addAll( configurationViewFactoryPostProcessors );
	}

	@Override
	public <U extends EntityViewFactory> U create( EntityConfiguration<?> entityConfiguration, Class<U> factoryType ) {
		U viewFactory = beanFactory.createBean( factoryType );
		configurationViewFactoryPostProcessors.forEach( c -> c.accept( entityConfiguration, viewFactory ) );
		return viewFactory;
	}

	/**
	 * Configure default message prefixes and create a property registry if necessary.
	 */
	private static class EntityConfigurationViewPostProcessor implements BiConsumer<EntityConfiguration, EntityViewFactory>
	{
		private final EntityPropertyRegistryProvider propertyRegistryProvider;

		public EntityConfigurationViewPostProcessor( EntityPropertyRegistryProvider propertyRegistryProvider ) {
			Assert.notNull( propertyRegistryProvider );
			this.propertyRegistryProvider = propertyRegistryProvider;
		}

		@Override
		public void accept( EntityConfiguration entityConfiguration, EntityViewFactory entityViewFactory ) {
			if ( entityViewFactory instanceof SimpleEntityViewFactorySupport ) {
				SimpleEntityViewFactorySupport viewFactory
						= (SimpleEntityViewFactorySupport) entityViewFactory;
				viewFactory.setTemplate( EntityFormView.VIEW_TEMPLATE );
				viewFactory.setMessagePrefixes( "entityViews" );
			}

			if ( entityViewFactory instanceof ConfigurablePropertiesEntityViewFactorySupport ) {
				ConfigurablePropertiesEntityViewFactorySupport viewFactory
						= (ConfigurablePropertiesEntityViewFactorySupport) entityViewFactory;
				EntityPropertyRegistry registry
						= propertyRegistryProvider.createForParentRegistry( entityConfiguration.getPropertyRegistry() );

				viewFactory.setPropertyRegistry( registry );
			}
		}
	}

	/**
	 * Attempts to fully configure a list view depending on the attributes available on the configuration.
	 */
	private static class EntityConfigurationListViewPostProcessor implements BiConsumer<EntityConfiguration, EntityViewFactory>
	{
		@Override
		public void accept( EntityConfiguration entityConfiguration, EntityViewFactory entityViewFactory ) {
			if ( entityViewFactory instanceof EntityListViewFactory ) {
				EntityListViewFactory viewFactory = (EntityListViewFactory) entityViewFactory;
				viewFactory.setTemplate( EntityListView.VIEW_TEMPLATE );

				Repository repository = entityConfiguration.getAttribute( Repository.class );
				if ( repository instanceof CrudRepository ) {
					viewFactory.setPageFetcher(
							new RepositoryEntityListViewPageFetcher( (CrudRepository) repository )
					);
				}

				LinkedList<String> defaultProperties = new LinkedList<>();
				defaultProperties.add( EntityPropertyRegistry.LABEL );

				if ( SecurityPrincipal.class.isAssignableFrom( entityConfiguration.getEntityType() ) ) {
					defaultProperties.addFirst( "principalName" );
				}

				if ( Auditable.class.isAssignableFrom( entityConfiguration.getEntityType() ) ) {
					defaultProperties.add( "lastModified" );
				}

				//viewFactory.setPropertyFilter( EntityPropertyFilters.include( defaultProperties ) );
				viewFactory.setPropertyComparator( EntityPropertyComparators.ordered( defaultProperties ) );
				viewFactory.setDefaultSort( determineDefaultSort( defaultProperties ) );
			}
		}

		private Sort determineDefaultSort( Collection<String> defaultProperties ) {
			String propertyName = null;

			if ( defaultProperties.contains( "name" ) ) {
				propertyName = "name";
			}
			else if ( defaultProperties.contains( "title" ) ) {
				propertyName = "title";
			}

			if ( propertyName != null ) {
				return new Sort( propertyName );
			}

			return null;
		}
	}
}
