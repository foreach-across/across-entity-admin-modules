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

/**
 * Default implementation that attempts to fetch the items based on the {@link com.foreach.across.modules.entity.views.context.EntityViewContext}.
 * Will use the repository attached to the {@link com.foreach.across.modules.entity.registry.EntityConfiguration} that is being used, and will
 * attempt to resolve association properties.
 * <p />
 * This implementation will by default only execute if there is no {@link EntityFetchingViewProcessorAdapter#ATTRIBUTE_ITEMS} registered.
 *
 * @author Arne Vandamme
 * @since 2.0.0
 */
public final class DefaultEntityFetchingViewProcessor extends EntityFetchingViewProcessorAdapter
{

}
