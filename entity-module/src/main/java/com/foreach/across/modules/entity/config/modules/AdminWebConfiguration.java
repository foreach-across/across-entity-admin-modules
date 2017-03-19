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

import com.foreach.across.core.annotations.AcrossDepends;
import com.foreach.across.modules.adminweb.AdminWeb;
import com.foreach.across.modules.entity.config.EntityConfigurer;
import com.foreach.across.modules.entity.config.builders.EntitiesConfigurationBuilder;
import com.foreach.across.modules.entity.controllers.EntityControllerAttributes;
import com.foreach.across.modules.entity.controllers.admin.GenericEntityViewController;
import com.foreach.across.modules.entity.handlers.MenuEventsHandler;
import com.foreach.across.modules.entity.registry.EntityAssociation;
import com.foreach.across.modules.entity.registry.MutableEntityAssociation;
import com.foreach.across.modules.entity.web.EntityAssociationLinkBuilder;
import com.foreach.across.modules.entity.web.EntityConfigurationLinkBuilder;
import com.foreach.across.modules.entity.web.EntityLinkBuilder;
import com.foreach.across.modules.entity.web.EntityModuleWebResources;
import com.foreach.across.modules.web.context.WebAppPathResolver;
import com.foreach.across.modules.web.resource.WebResourcePackageManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@AcrossDepends(required = "AdminWebModule")
@Configuration
public class AdminWebConfiguration implements EntityConfigurer
{
	@Autowired
	private ConversionService mvcConversionService;

	@Autowired
	private AdminWeb adminWeb;

	@Autowired
	public void registerEntityModuleWebResources( WebResourcePackageManager adminWebResourcePackageManager ) {
		adminWebResourcePackageManager.register( EntityModuleWebResources.NAME, new EntityModuleWebResources() );
	}

	@Bean
	public MenuEventsHandler menuEventsHandler() {
		return new MenuEventsHandler();
	}

	@Bean
	public GenericEntityViewController genericEntityViewController() {
		return new GenericEntityViewController();
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
									EntityControllerAttributes.ROOT_PATH, entityConfiguration, mvcConversionService,
									servletContextResolver
							)
					);

					for ( EntityAssociation association : entityConfiguration.getAssociations() ) {
						MutableEntityAssociation mutable = entityConfiguration.association( association.getName() );
						mutable.setAttribute( EntityLinkBuilder.class,
						                      new EntityAssociationLinkBuilder( association, mvcConversionService ) );
					}
				} );

	}
}
