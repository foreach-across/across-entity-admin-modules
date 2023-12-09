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

import com.foreach.across.modules.entity.registry.EntityAssociation;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import lombok.NonNull;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * @author Arne Vandamme
 * @since 3.0.0
 */
public abstract class SingleEntityViewLinkBuilder extends EntityViewLinkBuilderSupport<SingleEntityViewLinkBuilder>
{
	SingleEntityViewLinkBuilder( UriComponentsBuilder uriComponents, EntityViewLinks links ) {
		super( uriComponents, links );
	}

	/**
	 * @return update view for the current instance
	 */
	public SingleEntityViewLinkBuilder updateView() {
		return cloneLinkBuilder( toUriComponentsBuilder().pathSegment( "update" ) );
	}

	/**
	 * @return delete view for the current instance
	 */
	public SingleEntityViewLinkBuilder deleteView() {
		return cloneLinkBuilder( toUriComponentsBuilder().pathSegment( "delete" ) );
	}

	/**
	 * Create a link builder for an association.
	 * If there is no actual {@link EntityAssociation} available, the name will be
	 * interpreted as a regular {@link EntityConfiguration} instead and a link builder
	 * with a {@code from} parameter will be returned to the target entity configuration.
	 *
	 * @param associationName name of the association
	 * @return new link builder
	 */
	public abstract EntityViewLinkBuilder association( String associationName );

	/**
	 * Create a link builder for an association.
	 * Will use the first non-hidden association for the target type.
	 * If there is more than one, you should use {@link #association(String)} instead.
	 * <p/>
	 * If there is no actual {@link EntityAssociation} available, the name will be
	 * interpreted as a regular {@link EntityConfiguration} instead and a link builder
	 * with a {@code from} parameter will be returned to the target entity configuration.
	 *
	 * @param targetType of the association
	 * @return new link builder
	 */
	public final EntityViewLinkBuilder association( @NonNull Class<?> targetType ) {
		return association( links.getEntityConfiguration( targetType ) );
	}

	/**
	 * Create a link builder for an association.
	 * Will use the first non-hidden association for the target configuration.
	 * If there is more than one, you should use {@link #association(String)} instead.
	 * <p/>
	 * If there is no actual {@link EntityAssociation} available, the name will be
	 * interpreted as a regular {@link EntityConfiguration} instead and a link builder
	 * with a {@code from} parameter will be returned to the target entity configuration.
	 *
	 * @param targetConfiguration of the association
	 * @return new link builder
	 */
	public abstract EntityViewLinkBuilder association( @NonNull EntityConfiguration targetConfiguration );

	/**
	 * Create a link builder for an associated entity.
	 * Will use the first non-hidden association for the target type.
	 * If there is more than one, you should use {@code association(String).forInstance(Object)} instead.
	 * <p/>
	 * If there is no actual {@link EntityAssociation} available, the name will be
	 * interpreted as a regular {@link EntityConfiguration} instead and a link builder
	 * with a {@code from} parameter will be returned to the target entity configuration.
	 *
	 * @param target associated entity
	 * @return new link builder
	 */
	public abstract SingleEntityViewLinkBuilder association( Object target );

	public static class ForEntityConfiguration extends SingleEntityViewLinkBuilder
	{
		private final EntityConfiguration<Object> entityConfiguration;
		private final String instanceId;

		ForEntityConfiguration( UriComponentsBuilder uriComponents,
		                        EntityConfiguration<Object> entityConfiguration,
		                        String instanceId,
		                        EntityViewLinks links ) {
			super( uriComponents, links );

			this.entityConfiguration = entityConfiguration;
			this.instanceId = instanceId;

			uriComponents.pathSegment( instanceId );
		}

		private ForEntityConfiguration( UriComponentsBuilder uriComponents, ForEntityConfiguration original ) {
			super( uriComponents, original.links );

			entityConfiguration = original.entityConfiguration;
			instanceId = original.instanceId;
		}

		@Override
		public EntityViewLinkBuilder association( @NonNull String associationName ) {
			EntityAssociation association = entityConfiguration.association( associationName );
			if ( association == null ) {
				try {
					return links.linkTo( associationName ).withFromUrl( toUriString() );
				}
				catch ( IllegalArgumentException iae ) {
					throw new IllegalArgumentException( "No EntityAssociation nor EntityConfiguration registered with name " + associationName );
				}
			}
			return new EntityViewLinkBuilder.ForEntityAssociation( toUriComponentsBuilder(), association, this.instanceId, this.links );
		}

		@Override
		public EntityViewLinkBuilder association( @NonNull EntityConfiguration targetConfiguration ) {
			EntityAssociation association = findAssociation( targetConfiguration );

			if ( association == null ) {
				return links.linkTo( targetConfiguration ).withFromUrl( toUriString() );
			}

			return new EntityViewLinkBuilder.ForEntityAssociation( toUriComponentsBuilder(), association, this.instanceId, this.links );
		}

		@Override
		public SingleEntityViewLinkBuilder association( @NonNull Object target ) {
			EntityConfiguration targetConfiguration = links.getEntityConfiguration( target );
			EntityAssociation association = findAssociation( targetConfiguration );

			if ( association == null ) {
				return links.linkTo( targetConfiguration ).forInstance( target ).withFromUrl( toUriString() );
			}

			return new EntityViewLinkBuilder.ForEntityAssociation( toUriComponentsBuilder(), association, this.instanceId, this.links )
					.forInstance( target );
		}

		private EntityAssociation findAssociation( EntityConfiguration targetConfiguration ) {
			EntityAssociation hiddenAssociation = null;

			for ( EntityAssociation association : entityConfiguration.getAssociations() ) {
				if ( targetConfiguration.equals( association.getTargetEntityConfiguration() ) ) {
					if ( !association.isHidden() ) {
						return association;
					}
					else if ( hiddenAssociation == null ) {
						hiddenAssociation = association;
					}
				}
			}

			return hiddenAssociation;
		}

		@Override
		protected ForEntityConfiguration cloneLinkBuilder( UriComponentsBuilder uriComponents ) {
			return new ForEntityConfiguration( uriComponents, this );
		}
	}

	public static class ForEntityAssociation extends SingleEntityViewLinkBuilder
	{
		ForEntityAssociation( UriComponentsBuilder uriComponents, EntityViewLinks links ) {
			super( uriComponents, links );
		}

		@Override
		public EntityViewLinkBuilder association( String associationName ) {
			return links.linkTo( associationName ).withFromUrl( toUriString() );
		}

		@Override
		public EntityViewLinkBuilder association( EntityConfiguration targetConfiguration ) {
			return links.linkTo( targetConfiguration ).withFromUrl( toUriString() );
		}

		@Override
		public SingleEntityViewLinkBuilder association( Object target ) {
			return links.linkTo( target ).withFromUrl( toUriString() );
		}

		@Override
		protected ForEntityAssociation cloneLinkBuilder( UriComponentsBuilder uriComponents ) {
			return new ForEntityAssociation( uriComponents, this.links );
		}
	}
}
