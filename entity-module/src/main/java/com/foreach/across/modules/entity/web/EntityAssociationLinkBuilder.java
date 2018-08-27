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
package com.foreach.across.modules.entity.web;

import com.foreach.across.modules.entity.registry.EntityAssociation;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.convert.ConversionService;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * <p>Generates links for an {@link com.foreach.across.modules.entity.registry.EntityAssociation}.
 * This link builder is the base for scoped versions created with {@link #asAssociationFor(EntityLinkBuilder, Object)}.</p>
 *
 * @author Arne Vandamme
 * @deprecated since 3.0.0 - use {@link com.foreach.across.modules.entity.web.links.EntityViewLinks} instead
 */
@Deprecated
public class EntityAssociationLinkBuilder extends EntityConfigurationLinkBuilder
{
	private final EntityAssociation association;

	public EntityAssociationLinkBuilder( EntityAssociation association, ConversionService conversionService ) {
		super( StringUtils.EMPTY, association.getTargetEntityConfiguration(), conversionService );

		this.association = association;
	}

	@Override
	protected String getEntityConfigurationPath() {
		return association.getName();
	}

	@Override
	public EntityLinkBuilder asAssociationFor( EntityLinkBuilder sourceLinkBuilder, Object sourceEntity ) {
		if ( association.getAssociationType() == EntityAssociation.Type.EMBEDDED ) {
			return super.asAssociationFor( sourceLinkBuilder, sourceEntity );
		}

		return new LinkedAssociationLinkBuilder( sourceLinkBuilder.associations( sourceEntity ), sourceEntity, this );
	}

	class LinkedAssociationLinkBuilder extends PrefixingLinkBuilder
	{
		private final Object sourceEntity;

		LinkedAssociationLinkBuilder( String prefixPath, Object sourceEntity, EntityLinkBuilder linkBuilder ) {
			super( prefixPath, linkBuilder );
			this.sourceEntity = sourceEntity;
		}

		@Override
		@SuppressWarnings("unchecked")
		public String create() {
			UriComponentsBuilder uri = UriComponentsBuilder.fromUriString(
					association.getTargetEntityConfiguration().getAttribute( EntityLinkBuilder.class ).create()
			);

			EntityPropertyDescriptor targetProperty = association.getTargetProperty();
			if ( targetProperty != null && sourceEntity != null ) {
				uri.queryParam(
						"entity." + targetProperty.getName(),
						getIdAsString( association.getSourceEntityConfiguration(), sourceEntity )
				);
			}

			return uri.queryParam( "from", "{from}" ).build( overview() ).toString();
		}

		@Override
		public String update( Object entity ) {
			UriComponentsBuilder uri = UriComponentsBuilder.fromUriString(
					association.getTargetEntityConfiguration().getAttribute( EntityLinkBuilder.class ).update( entity )
			);

			return uri.queryParam( "from", "{from}" ).build( overview() ).toString();
		}

		@Override
		public String view( Object entity ) {
			UriComponentsBuilder uri = UriComponentsBuilder.fromUriString(
					association.getTargetEntityConfiguration().getAttribute( EntityLinkBuilder.class ).view( entity )
			);

			return uri.queryParam( "from", "{from}" ).build( overview() ).toString();
		}
	}
}
