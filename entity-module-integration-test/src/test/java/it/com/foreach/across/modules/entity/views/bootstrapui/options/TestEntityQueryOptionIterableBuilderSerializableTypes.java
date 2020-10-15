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

package it.com.foreach.across.modules.entity.views.bootstrapui.options;

import com.foreach.across.modules.bootstrapui.elements.builder.OptionFormElementBuilder;
import com.foreach.across.modules.entity.query.EntityQuery;
import com.foreach.across.modules.entity.query.EntityQueryExecutor;
import com.foreach.across.modules.entity.query.EntityQueryParser;
import com.foreach.across.modules.entity.registry.EntityModel;
import com.foreach.across.modules.entity.views.bootstrapui.options.EntityQueryOptionIterableBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.convert.support.GenericConversionService;

import java.io.Serializable;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/*
 * @author Marc Vanbrabant
 * @since 3.2.0
 */
@ExtendWith(MockitoExtension.class)
public class TestEntityQueryOptionIterableBuilderSerializableTypes
{
	@Mock
	private EntityQueryParser entityQueryParser;

	@Mock
	private EntityQueryExecutor entityQueryExecutor;

	private EntityQueryOptionIterableBuilder builder;
	private EntityQuery entityQuery;

	@BeforeEach
	public void setup() {
		builder = new EntityQueryOptionIterableBuilder();
		builder.setEntityQueryParser( entityQueryParser );
		builder.setEntityQuery( "" );
		builder.setEntityQueryExecutor( entityQueryExecutor );

		entityQuery = new EntityQuery();
		when( entityQueryParser.prepare( EntityQuery.all() ) ).thenReturn( entityQuery );

	}

	@Test
	public void entityWithUUID() {
		Entity<UUID> entity = new Entity<>( UUID.fromString( "6e395342-642a-4762-8ef8-59b47d9dd41c" ) );
		EntityModel<Entity<UUID>, UUID> entityModel = mock( EntityModel.class );
		builder.setEntityModel( entityModel );
		when( entityModel.getId( entity ) ).thenReturn( entity.id );

		List<Entity> items = Collections.singletonList( entity );
		when( entityQueryExecutor.findAll( entityQuery ) ).thenReturn( items );
		Iterable<OptionFormElementBuilder> iterable = builder.buildOptions( null );
		assertNotNull( iterable );
		assertTrue( iterable.iterator().hasNext() );
		assertEquals( "6e395342-642a-4762-8ef8-59b47d9dd41c", iterable.iterator().next().getValue() );
	}

	@Test
	public void entityWithCustomSerializableField() {
		LocalDateTime date = LocalDateTime.of( 2018, Month.SEPTEMBER, 26, 10, 40, 2, 154 );
		Entity<SpecialSerializableId> entity = new Entity<>( new SpecialSerializableId( date ) );
		EntityModel<Entity<SpecialSerializableId>, SpecialSerializableId> entityModel = mock( EntityModel.class );
		builder.setEntityModel( entityModel );

		GenericConversionService customConversionService = new GenericConversionService();
		customConversionService.addConverter( SpecialSerializableId.class, String.class, source -> Objects.toString( source.dateAsInt ) );

		builder.setConversionService( customConversionService );
		when( entityModel.getId( entity ) ).thenReturn( entity.id );

		List<Entity> items = Collections.singletonList( entity );
		when( entityQueryExecutor.findAll( entityQuery ) ).thenReturn( items );
		Iterable<OptionFormElementBuilder> iterable = builder.buildOptions( null );
		assertNotNull( iterable );
		assertTrue( iterable.iterator().hasNext() );
		assertEquals( "20180926104002000000154", iterable.iterator().next().getValue() );
	}

	static class Entity<T>
	{
		private T id;

		public Entity( T name ) {
			this.id = name;
		}
	}

	static class SpecialSerializableId implements Serializable
	{
		private BigInteger dateAsInt;

		SpecialSerializableId( LocalDateTime localDateTime ) {
			this.dateAsInt = new BigInteger( localDateTime.format( DateTimeFormatter.ofPattern( "yyyyMMddhhmmssSSSSSSSSS" ) ) );
		}
	}
}
