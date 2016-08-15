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

package com.foreach.across.modules.entity.query;

/**
 * Responsible for translating a raw {@link EntityQuery} into an actually
 * executable query instance that will be accepted by an {@link EntityQueryExecutor}.
 * <p/>
 * Translating means that all values will be parsed to their expected values (eg. ids will be turned into
 * instances) and the entire query might be optimized (eg. a single condition might be expanded into multiple).
 *
 * @author Arne Vandamme
 * @since 2.0.0
 */
public class EntityQueryTranslator
{
	public EntityQuery translate( EntityQuery rawQuery ) {
		return rawQuery;
	}
}
