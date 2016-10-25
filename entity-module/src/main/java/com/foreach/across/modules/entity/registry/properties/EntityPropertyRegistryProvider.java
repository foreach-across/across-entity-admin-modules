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
package com.foreach.across.modules.entity.registry.properties;

/**
 * Central API for retrieving a {@link MutableEntityPropertyRegistry} for a particular entity type.
 *
 * @author Arne Vandamme
 * @see DefaultEntityPropertyRegistryProvider
 * @since 2.0.0
 */
public interface EntityPropertyRegistryProvider
{
	/**
	 * Retrieve the property registry for the given entity type.  Usually the registry
	 * will be created upon the first call.  Calls to this method should always return
	 * the same instance.
	 *
	 * @param entityType for which to retrieve the property registry
	 * @return registry instance
	 * @see #create(Class)
	 */
	MutableEntityPropertyRegistry get( Class<?> entityType );

	/**
	 * Create a new registry that uses this provider for nested lookups
	 * The newly created registry will not be added to the provider itself,
	 * calls to this method will always return a new instance.
	 *
	 * @param entityType for which to create a new property registry
	 * @return new registry instance
	 * @see #get(Class)
	 */
	MutableEntityPropertyRegistry create( Class<?> entityType );

	/**
	 * Creates a new registry that uses the existing as parent.  Will usually return a
	 * {@link MergingEntityPropertyRegistry} where the property descriptors are also inherited.
	 *
	 * @param entityPropertyRegistry that is the parent
	 * @return new registry instance for the parent source
	 */
	MutableEntityPropertyRegistry createForParentRegistry( EntityPropertyRegistry entityPropertyRegistry );
}
