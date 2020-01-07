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
import lombok.Data;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.convert.TypeDescriptor;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Arne Vandamme
 * @since 3.2.0
 */
@ExtendWith(MockitoExtension.class)
class TestMergingEntityPropertyRegistry
{
	private DefaultEntityPropertyRegistry parentRegistry;

	private MergingEntityPropertyRegistry mergingRegistry;

	@BeforeEach
	void createMergingRegistry() {
		parentRegistry = new DefaultEntityPropertyRegistry( DefaultEntityPropertyRegistryProvider.INSTANCE );

		new EntityPropertyRegistryBuilder()
				.property( "users" ).propertyType( TypeDescriptor.collection( ArrayList.class, TypeDescriptor.valueOf( String.class ) ) ).and()
				.apply( parentRegistry );

		mergingRegistry = new MergingEntityPropertyRegistry( parentRegistry, DefaultEntityPropertyRegistryProvider.INSTANCE,
		                                                     new EntityPropertyDescriptorFactoryImpl() );
	}

	@Test
	void propertiesAreResolvedFromParentIfNotLocal() {
		MutableEntityPropertyDescriptor users = mergingRegistry.getProperty( "users" );
		assertThat( users ).isNotNull();

		MutableEntityPropertyDescriptor user = mergingRegistry.getProperty( "users[]" );
		assertThat( user ).isNotNull();
	}

	@Test
	@DisplayName("AXEUM-199 - indexer property must be resolved from type registry if available")
	void indexerPropertiesShouldBeResolvedViaTypeRegistry() {
		DefaultEntityPropertyRegistryProvider registryProvider = DefaultEntityPropertyRegistryProvider.newInstance();

		MutableEntityPropertyRegistry targetTypeRegistry = registryProvider.get( Target.class );
		targetTypeRegistry.configure( props -> props.property( "fields[]" )
		                                            .attribute( "available", true ) );

		DefaultEntityPropertyRegistry rootRegistry = new DefaultEntityPropertyRegistry( registryProvider );
		MutableEntityPropertyRegistry viewRegistry = registryProvider.createForParentRegistry( rootRegistry );
		viewRegistry.configure( props -> props.property( "Target" )
		                                      .propertyType( Target.class ) );

		assertThat( viewRegistry.getProperty( "Target" ) )
				.isNotNull()
				.matches( p -> p.getPropertyType().equals( Target.class ) );

		assertThat( viewRegistry.getProperty( "Target.fields" ) )
				.isNotNull()
				.matches( p -> p.getPropertyTypeDescriptor().isCollection() );

		assertThat( viewRegistry.getProperty( "Target.fields[]" ) )
				.isNotNull()
				.matches( p -> Boolean.TRUE.equals( p.getAttribute( "available", Boolean.class ) ) );
	}

	@Data
	private static class Target
	{
		private List<String> fields;
	}

}
