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
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import lombok.NonNull;

/**
 * Default implementation of {@link EntityPropertyTemplateValueResolver} which checks if a resolver is
 * registered as attribute on the property descriptor, and delegates to that one if it is.
 * <p/>
 * This implementation is attached as the default on an {@link EntityPropertiesBinder} and can be used as a base class.
 *
 * @author Arne Vandamme
 * @since 3.3.0
 */
public class DefaultEntityPropertyTemplateValueResolver implements EntityPropertyTemplateValueResolver
{
	@Override
	public Object resolveTemplateValue( EntityPropertyBindingContext bindingContext, @NonNull EntityPropertyDescriptor descriptor ) {
		EntityPropertyTemplateValueResolver resolver = descriptor.getAttribute( EntityPropertyTemplateValueResolver.class );
		return resolver != null ? resolver.resolveTemplateValue( bindingContext, descriptor ) : null;
	}
}
