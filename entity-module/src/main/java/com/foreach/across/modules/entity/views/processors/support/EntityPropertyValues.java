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

package com.foreach.across.modules.entity.views.processors.support;

import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistry;

/**
 * Facade for accessing individual properties of an {@link EntityPropertyRegistry} for a specific entity instance.
 * All properties are required to have a valid {@link EntityPropertyDescriptor} that is used to determine how
 * the property should be handled.
 * <p/>
 * An {@link EntityPropertyValues} represents a detached instance of the properties. Values can be set for a property,
 * but that does not mean it is immediately applied ("flushed") to the target entity. When and how the latter happens
 * is implementation dependent.
 *
 * @author Arne Vandamme
 * @see EntityPropertiesBinder
 * @see EntityPropertyValueHolder
 * @since 3.1.0
 */
public interface EntityPropertyValues
{
}
