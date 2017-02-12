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

import com.foreach.across.modules.entity.views.EntityView;

/**
 * Simple implementation of {@link EntityFetchingViewProcessorAdapter} that delegates to a {@link java.util.function.BiFunction}
 * for getting the actual items.  The {@link java.util.function.BiFunction} loses the specific {@link EntityView} context,
 * if you really require it you should implement {@link EntityFetchingViewProcessorAdapter} directly.
 *
 * @author Arne Vandamme
 * @since 2.0.0
 */
public final class SimpleEntityFetchingViewProcessor extends EntityFetchingViewProcessorAdapter
{

}
