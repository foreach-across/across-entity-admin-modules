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

package com.foreach.across.modules.entity.views.processors.query;

import com.foreach.across.modules.entity.query.EntityQuery;
import com.foreach.across.modules.entity.query.EntityQueryCondition;
import com.foreach.across.modules.entity.query.EntityQueryOps;
import com.foreach.across.modules.entity.registry.properties.DefaultEntityPropertyRegistry;
import com.foreach.across.modules.entity.registry.properties.EntityPropertySelector;
import org.junit.jupiter.api.Test;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.data.domain.Sort;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Arne Vandamme
 * @since 2.2.0
 */
public class TestEntityQueryFilterConfiguration
{
	@Test
	public void defaultValues() {
		EntityQueryFilterConfiguration configuration = EntityQueryFilterConfiguration.builder().build();
		assertNull( configuration.getPropertySelector() );
		assertTrue( configuration.isAdvancedMode() );
		assertTrue( configuration.isBasicMode() );
		assertNull( configuration.getBasePredicate() );
		assertNull( configuration.getDefaultQuery() );
		assertFalse( configuration.isDefaultToMultiValue() );

		DefaultEntityPropertyRegistry propertyRegistry = new DefaultEntityPropertyRegistry();
		configuration.getPropertyRegistryBuilder().apply( propertyRegistry );
		assertTrue( propertyRegistry.getRegisteredDescriptors().isEmpty() );

		assertFalse( configuration.isMultiValue( "any" ) );
	}

	@Test
	public void customizingBuilder() {
		EntityQueryFilterConfiguration configuration = EntityQueryFilterConfiguration
				.builder()
				.basicMode( false )
				.advancedMode( false )
				.showProperties( "name", "title" )
				.properties( pb -> pb.property( "name" ).propertyType( TypeDescriptor.valueOf( String.class ) ) )
				.singleValue( "title", "created" )
				.multiValue( "people" )
				.defaultQuery( "order by name ASC" )
				.basePredicate( "domain = 1" )
				.build();

		assertEquals( EntityPropertySelector.of( "name", "title" ), configuration.getPropertySelector() );
		assertFalse( configuration.isBasicMode() );
		assertFalse( configuration.isAdvancedMode() );
		assertEquals( "domain = 1", configuration.getBasePredicate().toString() );
		assertEquals( "order by name ASC", configuration.getDefaultQuery().toString() );

		DefaultEntityPropertyRegistry propertyRegistry = new DefaultEntityPropertyRegistry();
		configuration.getPropertyRegistryBuilder().apply( propertyRegistry );
		assertEquals( 1, propertyRegistry.getRegisteredDescriptors().size() );
		assertTrue( propertyRegistry.contains( "name" ) );

		assertFalse( configuration.isMultiValue( "name" ) );
		assertFalse( configuration.isMultiValue( "title" ) );
		assertFalse( configuration.isMultiValue( "created" ) );
		assertTrue( configuration.isMultiValue( "people" ) );
	}

	@Test
	public void customizeAfterToBuilder() {
		EntityQueryFilterConfiguration configuration = EntityQueryFilterConfiguration
				.builder()
				.basicMode( false )
				.advancedMode( false )
				.showProperties( "name", "title" )
				.properties( pb -> pb.property( "name" ).propertyType( TypeDescriptor.valueOf( String.class ) ) )
				.singleValue( "title", "created" )
				.multiValue( "people" )
				.defaultQuery( "order by name ASC" )
				.basePredicate( "domain = 1" )
				.defaultToMultiValue( true )
				.build();

		EntityQueryFilterConfiguration updated = configuration
				.toBuilder()
				.singleValue( "other" )
				.multiValue( "created" )
				.properties( pb -> pb.property( "title" ) )
				.advancedMode( true )
				.defaultQuery( EntityQuery.and( new EntityQueryCondition( "name", EntityQueryOps.EQ, "john" ) ) )
				.appendBasePredicate( EntityQuery.all( Sort.by( Sort.Direction.DESC, "date" ) ) )
				.build();

		assertEquals( EntityPropertySelector.of( "name", "title" ), updated.getPropertySelector() );
		assertFalse( updated.isBasicMode() );
		assertTrue( updated.isAdvancedMode() );
		assertEquals( "domain = 1 order by date DESC", updated.getBasePredicate().toString() );
		assertEquals( "name = 'john'", updated.getDefaultQuery().toString() );

		DefaultEntityPropertyRegistry propertyRegistry = new DefaultEntityPropertyRegistry();
		updated.getPropertyRegistryBuilder().apply( propertyRegistry );
		assertEquals( 2, propertyRegistry.getRegisteredDescriptors().size() );
		assertTrue( propertyRegistry.contains( "name" ) );
		assertTrue( propertyRegistry.contains( "title" ) );

		assertTrue( configuration.isMultiValue( "name" ) );
		assertFalse( configuration.isMultiValue( "title" ) );
		assertTrue( configuration.isMultiValue( "created" ) );
		assertTrue( configuration.isMultiValue( "people" ) );
		assertFalse( configuration.isMultiValue( "other" ) );
	}
}
