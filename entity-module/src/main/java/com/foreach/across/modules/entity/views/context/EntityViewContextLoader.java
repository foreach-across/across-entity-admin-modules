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

package com.foreach.across.modules.entity.views.context;

import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.EntityRegistry;
import com.foreach.across.modules.entity.views.support.EntityMessages;
import com.foreach.across.modules.entity.web.EntityLinkBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

/**
 * Responsible for loading the required properties for a valid {@link ConfigurableEntityViewContext},
 * based on a either {@link com.foreach.across.modules.entity.registry.EntityConfiguration}.
 *
 * @author Arne Vandamme
 * @since 2.0.0
 */
@Service
public class EntityViewContextLoader
{
	private EntityRegistry entityRegistry;

	/**
	 * Load the properties of {@param context} based on the entity configuration available for the entity.
	 * Will throw an exception if the entity is null.
	 *
	 * @param context to set the properties on
	 * @param entity  to use for loading
	 */
	public void loadForEntity( ConfigurableEntityViewContext context, Object entity ) {
		Assert.notNull( entity );
		loadForEntityConfiguration( context, entityRegistry.getEntityConfiguration( entity ) );
	}

	/**
	 * Load the properties of {@param context} based on the entity configuration with the given name.
	 * Will throw an exception if either name or the resulting entity configuration is null.
	 *
	 * @param context    to set the properties on
	 * @param entityName name of the {@link EntityConfiguration} to get the values from
	 */
	public void loadForEntityConfiguration( ConfigurableEntityViewContext context, String entityName ) {
		Assert.notNull( entityName );
		loadForEntityConfiguration( context, entityRegistry.getEntityConfiguration( entityName ) );
	}

	/**
	 * Load the properties of {@param context} based on the entity configuration value.
	 * Will throw an exception if the entity configuration is null.
	 *
	 * @param context             to set the properties on
	 * @param entityConfiguration to get the values from
	 */
	public void loadForEntityConfiguration( ConfigurableEntityViewContext context,
	                                        EntityConfiguration<?> entityConfiguration ) {
		Assert.notNull( entityConfiguration );
		context.setEntityConfiguration( entityConfiguration );
		context.setEntityModel( entityConfiguration.getEntityModel() );
		context.setMessageCodeResolver( entityConfiguration.getEntityMessageCodeResolver() );
		context.setEntityMessages( new EntityMessages( context.getMessageCodeResolver() ) );
		context.setLinkBuilder( entityConfiguration.getAttribute( EntityLinkBuilder.class ) );
		context.setPropertyRegistry( entityConfiguration.getPropertyRegistry() );
	}

	@Autowired
	void setEntityRegistry( EntityRegistry entityRegistry ) {
		this.entityRegistry = entityRegistry;
	}
}
