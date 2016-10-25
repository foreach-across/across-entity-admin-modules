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
package com.foreach.across.modules.entity.registry.builders;

import com.foreach.across.modules.entity.registry.properties.MutableEntityPropertyRegistry;

import java.util.function.BiConsumer;

/**
 * Central API for auto-registering properties for a class into a
 * {@link com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistry}.
 *
 * @author Arne Vandamme
 * @see com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistryProviderImpl
 * @see DefaultClassBasedPropertiesRegistrar
 * @see LabelPropertiesRegistrar
 * @see PersistenceMetadataPropertiesRegistrar
 * @see ValidationMetadataPropertiesRegistrar
 * @since 2.0.0
 */
@FunctionalInterface
public interface ClassBasedPropertiesRegistrar extends BiConsumer<Class<?>, MutableEntityPropertyRegistry>
{
	void accept( Class<?> entityType, MutableEntityPropertyRegistry registry );
}
