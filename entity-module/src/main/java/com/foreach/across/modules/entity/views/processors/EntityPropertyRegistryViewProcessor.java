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

package com.foreach.across.modules.entity.views.processors;

import com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistry;
import com.foreach.across.modules.entity.views.context.ConfigurableEntityViewContext;
import org.springframework.util.Assert;

/**
 * Registers a custom {@link com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistry} that should be used for the view.
 *
 * @author Arne Vandamme
 * @since 2.0.0
 */
public class EntityPropertyRegistryViewProcessor extends EntityViewProcessorAdapter
{
	private final EntityPropertyRegistry propertyRegistry;

	public EntityPropertyRegistryViewProcessor( EntityPropertyRegistry propertyRegistry ) {
		Assert.notNull( propertyRegistry );
		this.propertyRegistry = propertyRegistry;
	}

	@Override
	public void prepareEntityViewContext( ConfigurableEntityViewContext entityViewContext ) {
		entityViewContext.setPropertyRegistry( propertyRegistry );
	}
}
