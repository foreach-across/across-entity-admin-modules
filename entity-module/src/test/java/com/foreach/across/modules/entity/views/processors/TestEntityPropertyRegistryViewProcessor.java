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

import com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistry;
import com.foreach.across.modules.entity.views.context.ConfigurableEntityViewContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * @author Arne Vandamme
 * @since 2.0.0
 */
@ExtendWith(MockitoExtension.class)
public class TestEntityPropertyRegistryViewProcessor
{
	@Mock
	private ConfigurableEntityViewContext viewContext;

	@Test
	public void propertyRegistryIsRequired() {
		Assertions.assertThrows( IllegalArgumentException.class, () -> {
			new EntityPropertyRegistryViewProcessor( null );
		} );
	}

	@Test
	public void registryShouldBeReplaced() {
		EntityPropertyRegistry propertyRegistry = mock( EntityPropertyRegistry.class );
		new EntityPropertyRegistryViewProcessor( propertyRegistry ).prepareEntityViewContext( viewContext );
		verify( viewContext ).setPropertyRegistry( propertyRegistry );
	}

	@Test
	public void equalsIfSameRegistry() {
		EntityPropertyRegistry propertyRegistry = mock( EntityPropertyRegistry.class );
		assertEquals(
				new EntityPropertyRegistryViewProcessor( propertyRegistry ),
				new EntityPropertyRegistryViewProcessor( propertyRegistry )
		);
		assertNotEquals(
				new EntityPropertyRegistryViewProcessor( propertyRegistry ),
				new EntityPropertyRegistryViewProcessor( mock( EntityPropertyRegistry.class ) )
		);
	}
}
