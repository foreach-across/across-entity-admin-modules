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

package com.foreach.across.modules.entity.registry.properties.binding;

import com.foreach.across.modules.entity.registry.properties.EntityPropertyBindingContext;
import lombok.*;

/**
 * Static version of an {@link EntityPropertyBindingContext},
 * with both entity and target being fixed values.
 *
 * @author Arne Vandamme
 * @since 3.2.0
 */
@Builder(toBuilder = true)
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class SimpleEntityPropertyBindingContext extends AbstractEntityPropertyBindingContext
{
	private final Object entity;
	private final Object target;

	@Getter
	@Builder.Default
	private final boolean readonly = true;

	@Override
	@SuppressWarnings("unchecked")
	public <U> U getEntity() {
		return (U) entity;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <U> U getTarget() {
		return (U) target;
	}
}

