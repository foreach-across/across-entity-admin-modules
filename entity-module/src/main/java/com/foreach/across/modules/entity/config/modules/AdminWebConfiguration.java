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

import com.foreach.across.core.annotations.ConditionalOnAcrossModule;
import com.foreach.across.modules.adminweb.AdminWeb;
import com.foreach.across.modules.adminweb.AdminWebModule;
import com.foreach.across.modules.entity.config.EntityConfigurer;
import com.foreach.across.modules.entity.config.builders.EntitiesConfigurationBuilder;
import com.foreach.across.modules.entity.controllers.admin.EntityOverviewController;
import com.foreach.across.modules.entity.controllers.admin.GenericEntityViewController;
import com.foreach.across.modules.entity.handlers.MenuEventsHandler;
import com.foreach.across.modules.entity.registry.EntityAssociation;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.MutableEntityAssociation;
import com.foreach.across.modules.entity.web.EntityAssociationLinkBuilder;
import com.foreach.across.modules.entity.web.EntityConfigurationLinkBuilder;
import com.foreach.across.modules.entity.web.EntityLinkBuilder;
import com.foreach.across.modules.entity.web.EntityModuleWebResources;
import com.foreach.across.modules.web.context.WebAppPathResolver;
import com.foreach.across.modules.web.resource.WebResourcePackageManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@ConditionalOnAcrossModule(AdminWebModule.NAME)
@Configuration
@ComponentScan(basePackageClasses = EntityOverviewController.class)
@RequiredArgsConstructor
@Slf4j
public class AdminWebConfiguration implements EntityConfigurer
{
	private final ConversionService mvcConversionService;
	private final AdminWeb adminWeb;

	@Autowired
	public void registerEntityModuleWebResources( WebResourcePackageManager adminWebResourcePackageManager ) {
		adminWebResourcePackageManager.register( EntityModuleWebResources.NAME, new EntityModuleWebResources() );
	}

	@Bean
	public MenuEventsHandler menuEventsHandler() {
		return new MenuEventsHandler();
	}

	@Override
	public void configure( EntitiesConfigurationBuilder entities ) {
		// TODO: move to across web
		final WebAppPathResolver servletContextResolver = new WebAppPathResolver()
		{
			@Override
			public String path( String path ) {
				return ServletUriComponentsBuilder.fromCurrentContextPath()
				                                  .path( adminWeb.path( path ) )
				                                  .toUriString();
			}

			@Override
			public String redirect( String path ) {
				return "redirect:" + path( path );
			}
		};

		entities
				.all()
				.postProcessor( entityConfiguration -> {
					entityConfiguration.setAttribute(
							EntityLinkBuilder.class,
							new EntityConfigurationLinkBuilder(
									GenericEntityViewController.ROOT_PATH, entityConfiguration, mvcConversionService,
									servletContextResolver
							)
					);

					for ( EntityAssociation association : entityConfiguration.getAssociations() ) {
						MutableEntityAssociation mutable = entityConfiguration.association( association.getName() );
						mutable.setAttribute( EntityLinkBuilder.class,
						                      new EntityAssociationLinkBuilder( association, mvcConversionService ) );
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
//				throw new IllegalStateException(
//						"The mvcConversionService is unable to convert from " + idType.getName()
//								+ " to String: this conversion is required for managing entity '" + entityConfiguration.getName() + "' in AdminWebModule.  "
//								+ "Possibly you are using a composite id in which case you should manually register a converter." );
			}
			if ( !conversionService.canConvert( String.class, idType ) ) {
				LOG.error(
						"The mvcConversionService is unable to convert from String to {}: this conversion is required for managing entity '{}' in AdminWebModule.  " +
								"Possibly you are using a composite id in which case you should manually register a converter.",
						idType.getName(), entityConfiguration.getName()
				);
//				throw new IllegalStateException(
//						"The mvcConversionService is unable to convert from String to " + idType.getName()
//								+ ": this conversion is required for managing entities '" + entityConfiguration.getName() + "' in AdminWebModule.  "
//								+ "Possibly you are using a composite id in which case you should manually register a converter." );
			}
		}

	}
}
