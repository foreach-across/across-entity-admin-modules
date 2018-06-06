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

import com.foreach.across.modules.entity.config.entities.EntityQueryParserConfiguration;
import com.foreach.across.modules.entity.registry.EntityAssociation;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistry;
import com.foreach.across.modules.entity.views.EntityViewFactory;
import com.foreach.across.modules.entity.views.context.EntityViewContext;
import com.foreach.across.modules.entity.views.request.EntityViewRequest;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

/**
 * Default implementation of {@link EntityQueryFacadeResolver} that will inspect
 * the {@link EntityConfiguration}, {@link EntityAssociation} or {@link EntityViewFactory} attributes
 * for the presence of an {@link EntityQueryFacadeResolver}. If there is one, it will simply delegate
 * the call to it.
 * <p/>
 * If no custom resolver is found, default logic will be applied:
 * <ul>
 * <li>if a facade is registered on the {@link EntityConfiguration}, it will be used</li>
 * <li>if none is registered, but an {@link EntityQueryExecutor} is present, one will be created</li>
 * </ul>
 * <p>
 * If none is present,
 *
 * @author Arne Vandamme
 * @since 3.1.0
 */
@Primary
@SuppressWarnings("unchecked")
@Service
@RequiredArgsConstructor
public class DefaultEntityQueryFacadeResolver implements EntityQueryFacadeResolver
{
	private final EntityQueryParserConfiguration queryParserConfiguration;

	@Override
	public EntityQueryFacade forEntityViewRequest( EntityViewRequest viewRequest ) {
		EntityViewContext viewContext = viewRequest.getEntityViewContext();
		Map[] attributes = { viewRequest.getConfigurationAttributes(),
		                     viewContext.isForAssociation() ? viewContext.getEntityAssociation().attributeMap() : Collections.emptyMap(),
		                     viewContext.getEntityConfiguration().attributeMap() };
		val customResolver = findCustomResolver( attributes );

		if ( customResolver != null ) {
			return customResolver.forEntityViewRequest( viewRequest );
		}

		return resolve( viewContext.getEntityConfiguration().getPropertyRegistry(), attributes );
	}

	@Override
	public EntityQueryFacade forEntityViewContext( EntityViewContext viewContext ) {
		Map[] attributes = { viewContext.isForAssociation() ? viewContext.getEntityAssociation().attributeMap() : Collections.emptyMap(),
		                     viewContext.getEntityConfiguration().attributeMap() };
		val customResolver = findCustomResolver( attributes );

		if ( customResolver != null ) {
			return customResolver.forEntityViewContext( viewContext );
		}

		return resolve( viewContext.getEntityConfiguration().getPropertyRegistry(), attributes );
	}

	@Override
	public EntityQueryFacade forEntityAssociation( EntityAssociation association ) {
		Map[] attributes = { association.attributeMap(), association.getTargetEntityConfiguration().attributeMap() };
		val customResolver = findCustomResolver( attributes );

		if ( customResolver != null ) {
			return customResolver.forEntityAssociation( association );
		}

		return resolve( association.getTargetEntityConfiguration().getPropertyRegistry(), attributes );
	}

	@Override
	public EntityQueryFacade forEntityConfiguration( EntityConfiguration entityConfiguration ) {
		val customResolver = findCustomResolver( entityConfiguration.attributeMap() );

		if ( customResolver != null ) {
			return customResolver.forEntityConfiguration( entityConfiguration );
		}

		return resolve( entityConfiguration.getPropertyRegistry(), entityConfiguration.attributeMap() );
	}

	private EntityQueryFacadeResolver findCustomResolver( Map<String, Object>... attributes ) {
		for ( val attrs : attributes ) {
			EntityQueryFacadeResolver resolver = (EntityQueryFacadeResolver) attrs.get( EntityQueryFacadeResolver.class.getName() );
			if ( resolver != null ) {
				return resolver;
			}
		}

		return null;
	}

	private EntityQueryFacade resolve( EntityPropertyRegistry propertyRegistry, Map<String, Object>... attributes ) {
		for ( val attrs : attributes ) {
			EntityQueryFacade facade = (EntityQueryFacade) attrs.get( EntityQueryFacade.class.getName() );
			if ( facade != null ) {
				return facade;
			}
		}

		return createIfPossible( propertyRegistry, attributes );
	}

	private EntityQueryFacade createIfPossible( EntityPropertyRegistry propertyRegistry, Map<String, Object>... attributes ) {
		for ( val attrs : attributes ) {
			EntityQueryExecutor executor = (EntityQueryExecutor) attrs.get( EntityQueryExecutor.class.getName() );

			if ( executor != null ) {
				EntityQueryParser parser = (EntityQueryParser) attrs.get( EntityQueryParser.class.getName() );

				if ( parser == null ) {
					parser = createDefaultParser( propertyRegistry );
				}

				return new SimpleEntityQueryFacade( parser, executor );
			}
		}

		return null;
	}

	private EntityQueryParser createDefaultParser( EntityPropertyRegistry propertyRegistry ) {
		EntityQueryParser entityQueryParser = queryParserConfiguration.entityQueryParser();
		entityQueryParser.setMetadataProvider( queryParserConfiguration.entityQueryMetadataProvider( propertyRegistry ) );
		entityQueryParser.setQueryTranslator( queryParserConfiguration.entityQueryTranslator( propertyRegistry ) );
		return entityQueryParser;
	}
}
