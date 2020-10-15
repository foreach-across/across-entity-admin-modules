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

package com.foreach.across.modules.entity.support;

import com.foreach.across.core.support.WritableAttributes;
import com.foreach.across.modules.entity.config.AttributeRegistrar;
import com.foreach.across.modules.entity.config.builders.EntityConfigurationBuilder;
import com.foreach.across.modules.entity.query.EntityQuery;
import com.foreach.across.modules.entity.query.EntityQueryExecutor;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.function.Consumer;

import static com.foreach.across.modules.entity.support.EntityConfigurationCustomizers.registerEntityQueryExecutor;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * @author Arne Vandamme
 * @since 3.3.0
 */
@ExtendWith(MockitoExtension.class)
@SuppressWarnings("unchecked")
public class TestEntityConfigurationCustomizers
{
	@Test
	public void registerSpecificEntityQueryExecutor() {
		EntityQueryExecutor<Integer> expected = mock( EntityQueryExecutor.class );
		EntityQueryExecutor actual = applyEntityQueryExecutor( registerEntityQueryExecutor( expected ) );
		assertThat( actual ).isSameAs( expected );
	}

	@Test
	public void registerFixedCollectionEntityQueryExecutor() {
		EntityQueryExecutor actual = applyEntityQueryExecutor( registerEntityQueryExecutor( Arrays.asList( 1, 2 ) ) );
		assertThat( actual ).isNotNull();
		assertThat( actual.findAll( EntityQuery.all() ) ).containsExactly( 1, 2 );
	}

	@Test
	public void registerSourceSupplierEntityQueryExecutor() {
		EntityQueryExecutor actual = applyEntityQueryExecutor( registerEntityQueryExecutor( () -> Arrays.asList( 3, 4 ) ) );
		assertThat( actual ).isNotNull();
		assertThat( actual.findAll( EntityQuery.all() ) ).containsExactly( 3, 4 );
	}

	@Test
	public void registerEntityQueryExecutorFunction() {
		EntityQueryExecutor<Integer> expected = mock( EntityQueryExecutor.class );
		EntityQueryExecutor actual = applyEntityQueryExecutor( registerEntityQueryExecutor( configuration -> {
			assertThat( configuration ).isNotNull();
			return expected;
		} ) );
		assertThat( actual ).isSameAs( expected );

	}

	private EntityQueryExecutor applyEntityQueryExecutor( Consumer<EntityConfigurationBuilder<Integer>> consumer ) {
		EntityConfigurationBuilder<Integer> configurationBuilder = mock( EntityConfigurationBuilder.class );
		EntityConfiguration configuration = mock( EntityConfiguration.class );
		WritableAttributes attributes = mock( WritableAttributes.class );
		EntityPropertyRegistry propertyRegistry = mock( EntityPropertyRegistry.class );
//		when( configuration.getPropertyRegistry() ).thenReturn( propertyRegistry );

		ArgumentCaptor<EntityQueryExecutor> argument = ArgumentCaptor.forClass( EntityQueryExecutor.class );

		Mockito.doAnswer( invocationOnMock -> {
			AttributeRegistrar<EntityConfiguration> registrar = invocationOnMock.getArgument( 0 );
			registrar.accept( configuration, attributes );
			verify( attributes ).setAttribute( eq( EntityQueryExecutor.class ), argument.capture() );
			return null;
		} ).when( configurationBuilder ).attribute( any( AttributeRegistrar.class ) );

		consumer.accept( configurationBuilder );

		return argument.getValue();
	}
}
