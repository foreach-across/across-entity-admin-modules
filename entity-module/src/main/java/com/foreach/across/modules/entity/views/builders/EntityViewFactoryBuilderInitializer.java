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

package com.foreach.across.modules.entity.views.builders;

import com.foreach.across.modules.entity.config.builders.EntityViewFactoryBuilder;
import com.foreach.across.modules.entity.registry.EntityAssociation;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;

/**
 * @author Arne Vandamme
 * @since 2.0.0
 */
@SuppressWarnings("unchecked")
@Service
public final class EntityViewFactoryBuilderInitializer
{
	private final Map<String, BiConsumer<EntityConfiguration<?>, EntityViewFactoryBuilder>> configurationInitializers = new HashMap<>();
	private final Map<String, BiConsumer<EntityAssociation, EntityViewFactoryBuilder>> associationInitializers = new HashMap<>();

	public void registerConfigurationInitializer( String templateName, BiConsumer<EntityConfiguration<?>, ? extends EntityViewFactoryBuilder> initializer ) {
		configurationInitializers.put( templateName, (BiConsumer<EntityConfiguration<?>, EntityViewFactoryBuilder>) initializer );
	}

	public void registerAssociationInitializer( String templateName, BiConsumer<EntityAssociation, ? extends EntityViewFactoryBuilder> initializer ) {
		associationInitializers.put( templateName, (BiConsumer<EntityAssociation, EntityViewFactoryBuilder>) initializer );
	}

	public void initialize( String viewName,
	                        String templateName,
	                        EntityConfiguration<?> entityConfiguration,
	                        EntityViewFactoryBuilder builder ) {
		Optional.ofNullable( configurationInitializers.getOrDefault( viewName, configurationInitializers.get( templateName ) ) )
		        .ifPresent( i -> i.accept( entityConfiguration, builder ) );
	}

	public void initialize( String viewName,
	                        String templateName,
	                        EntityAssociation entityAssociation,
	                        EntityViewFactoryBuilder builder ) {
		initialize( viewName, templateName, entityAssociation.getTargetEntityConfiguration(), builder );

		Optional.ofNullable( associationInitializers.getOrDefault( viewName, associationInitializers.get( templateName ) ) )
		        .ifPresent( i -> i.accept( entityAssociation, builder ) );
	}
}
