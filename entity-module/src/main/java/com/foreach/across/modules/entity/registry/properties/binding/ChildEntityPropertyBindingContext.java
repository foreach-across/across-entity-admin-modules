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
import com.foreach.across.modules.entity.registry.properties.EntityPropertyController;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Represents an {@link com.foreach.across.modules.entity.registry.properties.EntityPropertyBindingContext} for a child
 * property of a parent {@link com.foreach.across.modules.entity.registry.properties.EntityPropertyBindingContext}.
 * The property is represented by a {@link com.foreach.across.modules.entity.registry.properties.EntityPropertyController}
 * that will be used to fetch the value from the parent binding context.
 *
 * @author Arne Vandamme
 * @since 3.2.0
 */
@RequiredArgsConstructor
public final class ChildEntityPropertyBindingContext extends AbstractEntityPropertyBindingContext
{
	@NonNull
	private final EntityPropertyBindingContext entityContext;

	@NonNull
	private final EntityPropertyController<?> propertyController;

	private Object cachedValue;
	private boolean valueFetched;

	@Override
	public <U> U getEntity() {
		return getTarget();
	}

	@Override
	@SuppressWarnings("unchecked")
	public <U> U getTarget() {
		if ( !isReadonly() || !valueFetched ) {
			valueFetched = true;
			cachedValue = propertyController.fetchValue( entityContext );
		}

		return (U) cachedValue;
	}

	@Override
	public boolean isReadonly() {
		return entityContext.isReadonly();
	}
}
