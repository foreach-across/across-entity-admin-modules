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

import com.foreach.across.modules.entity.views.processors.support.EntityViewProcessorRegistry;

/**
 * Extension of the default {@link EntityViewFactory} interface for implementations that support an {@link EntityViewProcessorRegistry}.
 * These factories usually dispatch some or all of their calls to the registered {@link EntityViewProcessor}s.
 * <p/>
 * <p>
 * The default {@link com.foreach.across.modules.entity.config.builders.EntityViewFactoryBuilder} implementations support configuration
 * of factories adhering to this interface.
 *
 * @author Arne Vandamme
 * @see DefaultEntityViewFactory
 * @see EntityViewProcessorRegistry
 * @see com.foreach.across.modules.entity.views.processors.support.TransactionalEntityViewProcessorRegistry
 * @since 2.0.0
 */
public interface DispatchingEntityViewFactory<T extends ViewCreationContext> extends EntityViewFactory<T>
{
	/**
	 * @return the configurable registry of view processors
	 */
	EntityViewProcessorRegistry getProcessorRegistry();
}
