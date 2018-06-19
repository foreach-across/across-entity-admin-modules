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

package com.foreach.across.modules.entity.query;

import com.foreach.across.modules.entity.registry.EntityAssociation;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.EntityRegistry;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistry;
import com.foreach.across.modules.entity.views.EntityViewFactory;
import com.foreach.across.modules.entity.views.context.EntityViewContext;
import com.foreach.across.modules.entity.views.request.EntityViewRequest;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

/**
 * Resolver service to find the most appropriate {@link EntityQueryFacade} to use for a given context.
 * <p/>
 * Will inspect the {@link EntityConfiguration}, {@link EntityAssociation} or {@link EntityViewFactory} attributes
 * for the presence of an {@link EntityQueryFacadeFactory}. If there is one, it will always use that instance to
 * create an {@link EntityQueryFacade}.
 * <p/>
 * If no {@link EntityQueryFacadeFactory} is found, an attribute of type {@link EntityQueryFacade} will be used instead.
 * <p/>
 * If no {@link EntityQueryFacadeFactory} or {@link EntityQueryFacade} is available, a default instance will be created using
 * the {@link EntityPropertyRegistry} of the root {@link EntityConfiguration} that is being requested.
 * In order to automatically create an instance, a {@link EntityQueryExecutor} attribute must be present in one of the collections.
 * <p/>
 * The attribute collections will always be inspected in order of specificity, where the most specific collection having
 * one of the expected values will end up being used. The order of specificity is
 * {@link EntityViewFactory} > {@link EntityAssociation} > {@link EntityConfiguration}.
 *
 * @author Arne Vandamme
 * @since 3.1.0
 */
@SuppressWarnings("unchecked")
@Service
@RequiredArgsConstructor
public class EntityQueryFacadeResolver
{
	private final EntityRegistry entityRegistry;
	private final EntityQueryParserFactory entityQueryParserFactory;

	public <U> EntityQueryFacade<U> forEntityType( Class<U> entityType ) {
		return forEntityConfiguration( entityRegistry.getEntityConfiguration( entityType ) );
	}

	public <U> EntityQueryFacade<U> forEntityViewRequest( EntityViewRequest viewRequest ) {
		EntityViewContext viewContext = viewRequest.getEntityViewContext();
		Map[] attributes = { viewRequest.getConfigurationAttributes(),
		                     viewContext.isForAssociation() ? viewContext.getEntityAssociation().attributeMap() : Collections.emptyMap(),
		                     viewContext.getEntityConfiguration().attributeMap() };
		val factory = findFactory( attributes );

		if ( factory != null ) {
			return factory.createForEntityViewRequest( viewRequest );
		}

		return resolve( viewContext.getEntityConfiguration().getPropertyRegistry(), attributes );
	}

	public <U> EntityQueryFacade<U> forEntityViewContext( EntityViewContext viewContext ) {
		Map[] attributes = { viewContext.isForAssociation() ? viewContext.getEntityAssociation().attributeMap() : Collections.emptyMap(),
		                     viewContext.getEntityConfiguration().attributeMap() };
		val factory = findFactory( attributes );

		if ( factory != null ) {
			return factory.createForEntityViewContext( viewContext );
		}

		return resolve( viewContext.getEntityConfiguration().getPropertyRegistry(), attributes );
	}

	public <U> EntityQueryFacade<U> forEntityAssociation( EntityAssociation association ) {
		Map[] attributes = { association.attributeMap(), association.getTargetEntityConfiguration().attributeMap() };
		val factory = findFactory( attributes );

		if ( factory != null ) {
			return factory.createForEntityAssociation( association );
		}

		return resolve( association.getTargetEntityConfiguration().getPropertyRegistry(), attributes );
	}

	public <U> EntityQueryFacade<U> forEntityConfiguration( EntityConfiguration entityConfiguration ) {
		val factory = findFactory( entityConfiguration.attributeMap() );

		if ( factory != null ) {
			return factory.createForEntityConfiguration( entityConfiguration );
		}

		return resolve( entityConfiguration.getPropertyRegistry(), entityConfiguration.attributeMap() );
	}

	private EntityQueryFacadeFactory findFactory( Map<String, Object>... attributesList ) {
		for ( val attributes : attributesList ) {
			EntityQueryFacadeFactory resolver = (EntityQueryFacadeFactory) attributes.get( EntityQueryFacadeFactory.class.getName() );
			if ( resolver != null ) {
				return resolver;
			}
		}

		return null;
	}

	private EntityQueryFacade resolve( EntityPropertyRegistry propertyRegistry, Map<String, Object>... attributesList ) {
		for ( val attributes : attributesList ) {
			EntityQueryFacade facade = (EntityQueryFacade) attributes.get( EntityQueryFacade.class.getName() );
			if ( facade != null ) {
				return facade;
			}
		}

		return createIfPossible( propertyRegistry, attributesList );
	}

	private EntityQueryFacade createIfPossible( EntityPropertyRegistry propertyRegistry, Map<String, Object>... attributesList ) {
		for ( val attributes : attributesList ) {
			EntityQueryExecutor executor = (EntityQueryExecutor) attributes.get( EntityQueryExecutor.class.getName() );

			if ( executor != null ) {
				EntityQueryParser parser = (EntityQueryParser) attributes.get( EntityQueryParser.class.getName() );

				if ( parser == null ) {
					parser = entityQueryParserFactory.createParser( propertyRegistry );
				}

				return new SimpleEntityQueryFacade( parser, executor );
			}
		}

		return null;
	}
}
