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

package com.foreach.across.modules.entity.registry.properties.registrars;

import com.foreach.across.modules.entity.registry.properties.DefaultEntityPropertyRegistryProvider;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.registry.properties.MutableEntityPropertyDescriptor;
import com.foreach.across.modules.entity.registry.properties.MutableEntityPropertyRegistry;

/**
 * Extension of a regular properties registrar that enhances every individual property registered.
 * Usually used for registration of default attributes on an {@link com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor}.
 *
 * @author Arne Vandamme
 * @since 3.0.0
 */
public abstract class AbstractEntityPropertyDescriptorEnhancer implements DefaultEntityPropertyRegistryProvider.PropertiesRegistrar
{
	@Override
	public final void accept( Class<?> entityType, MutableEntityPropertyRegistry registry ) {
		for ( EntityPropertyDescriptor descriptor : registry.getRegisteredDescriptors() ) {
			enhance( entityType, registry.getProperty( descriptor.getName() ) );
		}
	}

	protected abstract void enhance( Class<?> entityType, MutableEntityPropertyDescriptor descriptor );
}
