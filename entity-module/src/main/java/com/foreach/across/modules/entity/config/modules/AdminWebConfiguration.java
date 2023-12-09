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

package com.foreach.across.modules.entity.config.modules;

import com.foreach.across.core.annotations.Exposed;
import com.foreach.across.modules.entity.autosuggest.AutoSuggestDataController;
import com.foreach.across.modules.entity.autosuggest.AutoSuggestDataEndpoint;
import com.foreach.across.modules.entity.conditionals.ConditionalOnAdminWeb;
import com.foreach.across.modules.entity.config.EntityConfigurer;
import com.foreach.across.modules.entity.config.builders.EntitiesConfigurationBuilder;
import com.foreach.across.modules.entity.controllers.admin.GenericEntityViewController;
import com.foreach.across.modules.entity.registry.EntityAssociation;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.EntityRegistry;
import com.foreach.across.modules.entity.registry.MutableEntityAssociation;
import com.foreach.across.modules.entity.web.EntityLinkBuilder;
import com.foreach.across.modules.entity.web.EntityModuleWebResources;
import com.foreach.across.modules.entity.web.links.EntityViewLinkBuilder;
import com.foreach.across.modules.entity.web.links.EntityViewLinks;
import com.foreach.across.modules.web.resource.WebResourcePackageManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;

@ConditionalOnAdminWeb
@Configuration
@Slf4j
class AdminWebConfiguration
{
	@Autowired
	public void registerEntityModuleWebResources( WebResourcePackageManager adminWebResourcePackageManager ) {
		adminWebResourcePackageManager.register( EntityModuleWebResources.NAME, new EntityModuleWebResources() );
	}

	@Bean
	@Exposed
	public AutoSuggestDataEndpoint autoSuggestDataEndpoint() {
		return new AutoSuggestDataEndpoint( "@adminWeb:" + AutoSuggestDataController.DEFAULT_REQUEST_MAPPING );
	}

	@Bean
	@Exposed
	public EntityViewLinks entityViewLinks( EntityRegistry entityRegistry ) {
		return new EntityViewLinks( "@adminWeb:" + GenericEntityViewController.ROOT_PATH, entityRegistry );
	}

	@ConditionalOnAdminWeb
	@Configuration
	@RequiredArgsConstructor
	static class EntityLinkBuilderRegistrar implements EntityConfigurer
	{
		private final ConversionService mvcConversionService;
		private final EntityViewLinks entityViewLinks;

		@Override
		public void configure( EntitiesConfigurationBuilder entities ) {
			entities
					.all()
					.postProcessor( entityConfiguration -> {
						EntityViewLinkBuilder.ForEntityConfiguration linkBuilder = entityViewLinks.linkTo( entityConfiguration );
						entityConfiguration.setAttribute( EntityViewLinkBuilder.class, linkBuilder );

						// set deprecated EntityLinkBuilder for compatibility
						entityConfiguration.setAttribute( EntityLinkBuilder.class, linkBuilder );

						for ( EntityAssociation association : entityConfiguration.getAssociations() ) {
							MutableEntityAssociation mutable = entityConfiguration.association( association.getName() );
							mutable.setAttribute( EntityLinkBuilder.class, entityViewLinks.linkTo( association.getTargetEntityConfiguration() ) );
						}

						if ( entityConfiguration.hasEntityModel() ) {
							verifyIdTypeCanBeConverted( entityConfiguration, mvcConversionService );
						}
					} );
		}

		private void verifyIdTypeCanBeConverted( EntityConfiguration<?> entityConfiguration, ConversionService conversionService ) {
			Class<?> idType = entityConfiguration.getIdType();

			if ( idType != null ) {
				LOG.trace( "Checking if conversion between {} and String can be performed", idType.getName() );

				if ( !conversionService.canConvert( idType, String.class ) ) {
					LOG.error(
							"The mvcConversionService is unable to convert from {} to String: this conversion is required for managing entities '{}' in AdminWebModule.  " +
									"Possibly you are using a composite id in which case you should manually register a converter.",
							idType.getName(), entityConfiguration.getName()
					);
				}
				if ( !conversionService.canConvert( String.class, idType ) ) {
					LOG.error(
							"The mvcConversionService is unable to convert from String to {}: this conversion is required for managing entity '{}' in AdminWebModule.  " +
									"Possibly you are using a composite id in which case you should manually register a converter.",
							idType.getName(), entityConfiguration.getName()
					);
				}
			}
		}
	}
}
