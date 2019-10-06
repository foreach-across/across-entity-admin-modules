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

import com.foreach.across.modules.entity.bind.EntityPropertyControlName;
import com.foreach.across.modules.entity.registry.EntityAssociation;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyHandlingType;
import com.foreach.across.modules.entity.web.EntityLinkBuilder;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Core link building entry point for an entity type.
 * Implements the deprecated {@link EntityLinkBuilder} to ensure backwards compatibility.
 *
 * @author Arne Vandamme
 * @since 3.0.0
 */
public abstract class EntityViewLinkBuilder extends EntityViewLinkBuilderSupport<EntityViewLinkBuilder>
		implements EntityLinkBuilder
{
	EntityViewLinkBuilder( UriComponentsBuilder uriComponents, EntityViewLinks links, EntityViewRouterB router ) {
		super( uriComponents, links, router );
	}

	/**
	 * @return default list view for the instance
	 */
	public EntityViewLinkBuilderSupport listView() {
		return this;
	}

	/**
	 * @return create new instance view
	 */
	public EntityViewLinkBuilderSupport createView() {
		return new EntityViewLinkBuilderSupport( toUriComponentsBuilder().pathSegment( "create" ), links, router );
	}

	@Deprecated
	@Override
	public String overview() {
		return listView().toUriString();
	}

	@Deprecated
	@Override
	public String create() {
		return createView().toUriString();
	}

	@Deprecated
	@Override
	public String update( Object entity ) {
		return forInstance( entity ).updateView().toUriString();
	}

	@Deprecated
	@Override
	public String delete( Object entity ) {
		return forInstance( entity ).deleteView().toUriString();
	}

	@Deprecated
	@Override
	public String view( Object entity ) {
		return forInstance( entity ).toUriString();
	}

	@Deprecated
	@Override
	public String associations( Object entity ) {
		return forInstance( entity ).slash( "associations" ).toUriString();
	}

	@Deprecated
	@Override
	public abstract EntityLinkBuilder asAssociationFor( EntityLinkBuilder sourceLinkBuilder, Object sourceEntity );

	/**
	 * @param entity of the entity that is the base of the uri path
	 * @return existing entity view
	 */
	public abstract SingleEntityViewLinkBuilder forInstance( @NonNull Object entity );

	/**
	 * @param id of the entity that is the base of the uri path
	 * @return existing entity view
	 */
	public abstract SingleEntityViewLinkBuilder withId( @NonNull Object id );

	public static class ForEntityConfiguration extends EntityViewLinkBuilder
	{
		private final EntityConfiguration<Object> entityConfiguration;

		ForEntityConfiguration( UriComponentsBuilder uriComponents,
		                        EntityConfiguration<Object> entityConfiguration,
		                        EntityViewLinks links,
		                        EntityViewRouterB router ) {
			super( router.applyEntityRoot( uriComponents, entityConfiguration ), links, router );
			this.entityConfiguration = entityConfiguration;
		}

		ForEntityConfiguration( UriComponentsBuilder uriComponents, ForEntityConfiguration original ) {
			super( uriComponents, original.links, original.router );
			this.entityConfiguration = original.entityConfiguration;
		}

		@Override
		public SingleEntityViewLinkBuilder.ForEntityConfiguration forInstance( @NonNull Object entity ) {
			return withId( entityConfiguration.getId( entity ) );
		}

		@Override
		public SingleEntityViewLinkBuilder.ForEntityConfiguration withId( @NonNull Object id ) {
			return new SingleEntityViewLinkBuilder.ForEntityConfiguration( toUriComponentsBuilder(), entityConfiguration, links.convertId( id ), links,
			                                                               router );
		}

		@Override
		public EntityLinkBuilder asAssociationFor( EntityLinkBuilder sourceLinkBuilder, @NonNull Object sourceEntity ) {
			if ( sourceLinkBuilder instanceof ForEntityConfiguration ) {
				return links.linkTo( ( (ForEntityConfiguration) sourceLinkBuilder ).entityConfiguration )
				            .forInstance( sourceEntity )
				            .association( entityConfiguration );
			}

			return links.linkTo( sourceEntity ).association( entityConfiguration );
		}

		@Override
		protected ForEntityConfiguration cloneLinkBuilder( UriComponentsBuilder uriComponents ) {
			return new ForEntityConfiguration( uriComponents, this );
		}
	}

	public static class ForEntityAssociation extends EntityViewLinkBuilder
	{
		private final EntityAssociation entityAssociation;
		private final String parentEntityId;

		ForEntityAssociation( UriComponentsBuilder uriComponents,
		                      EntityAssociation entityAssociation,
		                      String parentEntityId,
		                      EntityViewLinks links,
		                      EntityViewRouterB router ) {
			super( router.applyAssociationPath( uriComponents, entityAssociation ), links, router );
			this.entityAssociation = entityAssociation;
			this.parentEntityId = parentEntityId;
		}

		ForEntityAssociation( UriComponentsBuilder uriComponents, ForEntityAssociation original ) {
			super( uriComponents, original.links, original.router );
			entityAssociation = original.entityAssociation;
			parentEntityId = original.parentEntityId;
		}

		@Override
		public EntityViewLinkBuilderSupport createView() {
			switch ( entityAssociation.getAssociationType() ) {
				case LINKED:
					EntityViewLinkBuilderSupport linkBuilder = links.linkTo( entityAssociation.getTargetEntityConfiguration() )
					                                                .createView()
					                                                .withFromUrl( toUriString() );
					EntityPropertyDescriptor targetProperty = entityAssociation.getTargetProperty();

					if ( targetProperty != null ) {
						String targetPropertyName = EntityPropertyControlName.root( "entity" )
						                                                     .forChildProperty( targetProperty )
						                                                     .forHandlingType( EntityPropertyHandlingType.forProperty( targetProperty ) )
						                                                     .toString();
						// todo: fix actual query parameter encoding
						return linkBuilder.withQueryParam(
								StringUtils.replaceEach( targetPropertyName, new String[] { "[", "]" }, new String[] { "%5B", "%5D" } ),
								parentEntityId
						);
					}

					return linkBuilder;
				default:
					return super.createView();
			}
		}

		@Override
		@SuppressWarnings("unchecked")
		public SingleEntityViewLinkBuilder forInstance( @NonNull Object entity ) {
			return withId( entityAssociation.getTargetEntityConfiguration().getId( entity ) );
		}

		@Override
		public SingleEntityViewLinkBuilder withId( @NonNull Object id ) {
			switch ( entityAssociation.getAssociationType() ) {
				case LINKED:
					return links.linkTo( entityAssociation.getTargetEntityConfiguration() ).withId( id ).withFromUrl( toUriString() );
				default:
					return new SingleEntityViewLinkBuilder.ForEntityAssociation( toUriComponentsBuilder().pathSegment( links.convertId( id ) ), links, router );
			}
		}

		@Override
		protected ForEntityAssociation cloneLinkBuilder( UriComponentsBuilder uriComponents ) {
			return new ForEntityAssociation( uriComponents, this );
		}

		@Override
		public EntityLinkBuilder asAssociationFor( EntityLinkBuilder sourceLinkBuilder, Object sourceEntity ) {
			throw new UnsupportedOperationException( "Unable to create an association for an already existing association." );
		}
	}
}
