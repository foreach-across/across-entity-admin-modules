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

package com.foreach.across.modules.entity.web.links;

import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.EntityRegistry;
import com.foreach.across.modules.entity.web.links.EntityViewLinkBuilder.ForEntityConfiguration;
import com.foreach.across.modules.web.context.WebAppLinkBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.ConversionService;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Objects;

/**
 * Central component for building links to EntityModule views.
 * A bean of this type is automatically created when AdminWebModule is active,
 * see {@link com.foreach.across.modules.entity.config.modules.AdminWebConfiguration}.
 *
 * @author Arne Vandamme
 * @since 3.0.0
 */
@SuppressWarnings("SpringJavaAutowiredMembersInspection")
@RequiredArgsConstructor
public class EntityViewLinks
{
	private final String rootPath;
	private final EntityRegistry entityRegistry;

	private ConversionService conversionService;
	private WebAppLinkBuilder webAppLinkBuilder;

	/**
	 * Set the {@link ConversionService} that should be used for converting id objects to string.
	 * If none is set, the {@code toString()} method of an object will be used.
	 *
	 * @param conversionService to use
	 */
	@Autowired(required = false)
	@Qualifier("mvcConversionService")
	public void setConversionService( ConversionService conversionService ) {
		this.conversionService = conversionService;
	}

	/**
	 * Set the {@link WebAppLinkBuilder} to be used for building the URI string of a link.
	 *
	 * @param webAppLinkBuilder builder
	 */
	@Autowired(required = false)
	public void setWebAppLinkBuilder( WebAppLinkBuilder webAppLinkBuilder ) {
		this.webAppLinkBuilder = webAppLinkBuilder;
	}

	/**
	 * Create a link builder to the {@link com.foreach.across.modules.entity.views.EntityView#DEFAULT_VIEW_NAME} view an entity type page.
	 *
	 * @param entityType type of entity
	 * @return link builder
	 */
	@SuppressWarnings("unchecked")
	public ForEntityConfiguration linkTo( Class<?> entityType ) {
		return linkTo( entityRegistry.getEntityConfiguration( entityType ) );
	}

	/**
	 * Create a link builder to the {@link com.foreach.across.modules.entity.views.EntityView#DEFAULT_VIEW_NAME} view for a basic entity type (by name).
	 *
	 * @param entityName of the entity type
	 * @return link builder
	 */
	public EntityViewLinkBuilder.ForEntityConfiguration linkTo( String entityName ) {
		return linkTo( entityRegistry.getEntityConfiguration( entityName ) );
	}

	/**
	 * Create a link builder to the {@link com.foreach.across.modules.entity.views.EntityView#DEFAULT_VIEW_NAME} view for a single entity.
	 *
	 * @param entity instance
	 * @return link builder
	 */
	public SingleEntityViewLinkBuilder.ForEntityConfiguration linkTo( Object entity ) {
		return linkTo( entityRegistry.getEntityConfiguration( entity ) ).forInstance( entity );
	}

	/**
	 * Create a link builder to the {@link com.foreach.across.modules.entity.views.EntityView#DEFAULT_VIEW_NAME} view for an entity configuration.
	 *
	 * @param entityConfiguration type of entity
	 * @return link builder
	 */
	@SuppressWarnings("unchecked")
	public ForEntityConfiguration linkTo( EntityConfiguration<?> entityConfiguration ) {
		return new ForEntityConfiguration( newUriComponents(), (EntityConfiguration<Object>) entityConfiguration, this );
	}

	private UriComponentsBuilder newUriComponents() {
		return UriComponentsBuilder.newInstance().path( rootPath );
	}

	/**
	 * Process possible prefixes still present in the URI string.
	 *
	 * @param uri original uri
	 * @return processed version
	 */
	String buildUriLink( String uri ) {
		boolean inRequest = RequestContextHolder.getRequestAttributes() != null;
		return webAppLinkBuilder != null && inRequest ? webAppLinkBuilder.buildLink( uri, false ) : uri;
	}

	/**
	 * Convert an id parameter to {@code String} for us in URIs.
	 *
	 * @param value to convert
	 * @return string
	 */
	String convertId( Object value ) {
		return conversionService != null ? conversionService.convert( value, String.class ) : Objects.toString( value );
	}

	EntityConfiguration getEntityConfiguration( Class<?> entityType ) {
		EntityConfiguration<?> entityConfiguration = entityRegistry.getEntityConfiguration( entityType );
		if ( entityConfiguration == null ) {
			throw new IllegalArgumentException( "No registered EntityConfiguration for " + entityType );
		}
		return entityConfiguration;
	}

	EntityConfiguration getEntityConfiguration( Object entity ) {
		EntityConfiguration<Object> entityConfiguration = entityRegistry.getEntityConfiguration( entity );
		if ( entityConfiguration == null ) {
			throw new IllegalArgumentException( "No registered EntityConfiguration for entity: " + entity );
		}
		return entityConfiguration;
	}
}
