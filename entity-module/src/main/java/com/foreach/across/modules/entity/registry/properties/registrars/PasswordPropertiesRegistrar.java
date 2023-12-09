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

import com.foreach.across.core.annotations.OrderInModule;
import com.foreach.across.modules.bootstrapui.elements.TextboxFormElement;
import com.foreach.across.modules.entity.registry.properties.DefaultEntityPropertyRegistryProvider;
import com.foreach.across.modules.entity.registry.properties.MutableEntityPropertyDescriptor;
import com.foreach.across.modules.entity.registry.properties.MutableEntityPropertyRegistry;
import org.springframework.stereotype.Component;

/**
 * Attempts to detect password properties and sets them as writable only.
 *
 * @author Arne Vandamme
 * @since 2.0.0
 */
@Component
@OrderInModule(4)
public class PasswordPropertiesRegistrar implements DefaultEntityPropertyRegistryProvider.PropertiesRegistrar
{
	@Override
	public void accept( Class<?> entityType, MutableEntityPropertyRegistry registry ) {
		MutableEntityPropertyDescriptor password = registry.getProperty( "password" );

		if ( password != null ) {
			password.setReadable( false );
			password.setAttribute( TextboxFormElement.Type.class, TextboxFormElement.Type.PASSWORD );
		}
	}
}
