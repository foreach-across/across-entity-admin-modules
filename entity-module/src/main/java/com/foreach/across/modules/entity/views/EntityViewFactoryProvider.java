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

package com.foreach.across.modules.entity.views;

import com.foreach.across.modules.entity.registry.EntityConfiguration;

/**
 * Provider that creates default {@link EntityViewFactory} implementations for a specific
 * {@link com.foreach.across.modules.entity.registry.EntityConfiguration}
 * or {@link com.foreach.across.modules.entity.registry.EntityAssociation}.
 *
 * @author Arne Vandamme
 * @see com.foreach.across.modules.entity.config.builders.EntityViewFactoryBuilder
 * @since 2.0.0
 */
@Deprecated
public interface EntityViewFactoryProvider
{
	/**
	 * Create a new factory of a specific type for the entity configuration.
	 * The factory will be pre-configured with entity configuration related properties.
	 *
	 * @param entityConfiguration to create a view for
	 * @param factoryType         type of the view
	 * @param <U>                 type of the view
	 * @return view factory instance
	 */
	<U extends EntityViewFactory> U create( EntityConfiguration<?> entityConfiguration, Class<U> factoryType );
}
