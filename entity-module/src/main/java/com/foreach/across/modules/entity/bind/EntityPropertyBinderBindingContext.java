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

package com.foreach.across.modules.entity.bind;

import com.foreach.across.modules.entity.registry.properties.EntityPropertyBindingContext;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyController;
import lombok.RequiredArgsConstructor;

/**
 * Implementation of {@link EntityPropertyBindingContext} representing a property on an {@link EntityPropertiesBinder}.
 * Will use the actual values present on the binder.
 *
 * @author Arne Vandamme
 * @since 3.2.0
 */
@RequiredArgsConstructor
class EntityPropertyBinderBindingContext implements EntityPropertyBindingContext
{
	private final EntityPropertyBindingContext parentContext;
	private final EntityPropertyBinder propertyBinder;

	@Override
	@SuppressWarnings("unchecked")
	public <U> U getEntity() {
		return (U) propertyBinder.getOriginalValue();
	}

	@Override
	@SuppressWarnings("unchecked")
	public <U> U getTarget() {
		return isReadonly() ? getEntity() : (U) propertyBinder.getValue();
	}

	@Override
	public boolean isReadonly() {
		return parentContext.isReadonly();
	}

	@Override
	public EntityPropertyBindingContext resolvePropertyBindingContext( String propertyName, EntityPropertyController controller ) {
		if ( propertyBinder instanceof SingleEntityPropertyBinder ) {
			return ( (SingleEntityPropertyBinder) propertyBinder ).getProperties().asBindingContext().resolvePropertyBindingContext( propertyName, controller );
		}

		return null;
	}
}
