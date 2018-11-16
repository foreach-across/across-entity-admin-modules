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

package com.foreach.across.modules.entity.registry.properties;

import com.foreach.across.modules.entity.config.builders.EntityPropertyRegistryBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.convert.TypeDescriptor;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Arne Vandamme
 * @since 3.2.0
 */
@RunWith(MockitoJUnitRunner.class)
public class TestMergingEntityPropertyRegistry
{
	private DefaultEntityPropertyRegistry parentRegistry;

	private MergingEntityPropertyRegistry mergingRegistry;

	@Before
	public void createMergingRegistry() {
		parentRegistry = new DefaultEntityPropertyRegistry( DefaultEntityPropertyRegistryProvider.INSTANCE );

		new EntityPropertyRegistryBuilder()
				.property( "users" ).propertyType( TypeDescriptor.collection( ArrayList.class, TypeDescriptor.valueOf( String.class ) ) ).and()
				.apply( parentRegistry );

		mergingRegistry = new MergingEntityPropertyRegistry( parentRegistry, DefaultEntityPropertyRegistryProvider.INSTANCE,
		                                                     new EntityPropertyDescriptorFactoryImpl() );
	}

	@Test
	public void propertiesAreResolvedFromParentIfNotLocal() {
		MutableEntityPropertyDescriptor users = mergingRegistry.getProperty( "users" );
		assertThat( users ).isNotNull();

		MutableEntityPropertyDescriptor user = mergingRegistry.getProperty( "users[]" );
		assertThat( user ).isNotNull();
	}
}
