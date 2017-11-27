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
package com.foreach.across.modules.entity.util;

import com.foreach.across.modules.entity.registry.EntityRegistry;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistry;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.data.domain.Sort;
import org.springframework.util.ReflectionUtils;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.core.convert.TypeDescriptor.collection;
import static org.springframework.core.convert.TypeDescriptor.valueOf;

/**
 * @author Arne Vandamme
 */
@RunWith(MockitoJUnitRunner.class)
public class TestEntityUtils
{
	@Mock
	private EntityRegistry entityRegistry;

	@Test
	public void nullTypeDescriptorResultsInGenericObjectType() {
		EntityTypeDescriptor descriptor = EntityUtils.resolveEntityTypeDescriptor( null, entityRegistry );
		assertNotNull( descriptor );
		assertEquals( TypeDescriptor.valueOf( Object.class ), descriptor.getSourceTypeDescriptor() );
		assertFalse( descriptor.isTargetTypeResolved() );
	}

	@Test
	public void nonCollectionTypesAreResolvedAsTheirOwnTarget() {
		EntityTypeDescriptor descriptor = EntityUtils.resolveEntityTypeDescriptor( valueOf( String.class ), entityRegistry );
		assertEquals(
				EntityTypeDescriptor.builder()
				                    .sourceTypeDescriptor( valueOf( String.class ) )
				                    .targetTypeDescriptor( valueOf( String.class ) )
				                    .collection( false )
				                    .build(),
				descriptor
		);

		descriptor = EntityUtils.resolveEntityTypeDescriptor( valueOf( Long.class ), entityRegistry );
		assertEquals(
				EntityTypeDescriptor.builder()
				                    .sourceTypeDescriptor( valueOf( Long.class ) )
				                    .targetTypeDescriptor( valueOf( Long.class ) )
				                    .collection( false )
				                    .build(),
				descriptor
		);

		descriptor = EntityUtils.resolveEntityTypeDescriptor( valueOf( MyClassWithGenericLong.class ), entityRegistry );
		assertEquals(
				EntityTypeDescriptor.builder()
				                    .sourceTypeDescriptor( valueOf( MyClassWithGenericLong.class ) )
				                    .targetTypeDescriptor( valueOf( MyClassWithGenericLong.class ) )
				                    .collection( false )
				                    .build(),
				descriptor
		);
	}

	@Test
	public void optionalTypesHaveADifferentTargetButAreNotACollection() {
		TypeDescriptor optionalField = new TypeDescriptor( ReflectionUtils.findField( MyClassWithGenericLong.class, "myOptional" ) );

		EntityTypeDescriptor descriptor = EntityUtils.resolveEntityTypeDescriptor( optionalField, entityRegistry );
		assertEquals(
				EntityTypeDescriptor.builder()
				                    .sourceTypeDescriptor( optionalField )
				                    .targetTypeDescriptor( valueOf( String.class ) )
				                    .collection( false )
				                    .build(),
				descriptor
		);
	}

	@Test
	public void collectionTypesHaveTheirMemberAsTarget() {
		EntityTypeDescriptor descriptor = EntityUtils.resolveEntityTypeDescriptor(
				TypeDescriptor.array( valueOf( MyClassWithGenericLong.class ) ), entityRegistry
		);

		assertEquals(
				EntityTypeDescriptor.builder()
				                    .sourceTypeDescriptor( TypeDescriptor.array( valueOf( MyClassWithGenericLong.class ) ) )
				                    .targetTypeDescriptor( valueOf( MyClassWithGenericLong.class ) )
				                    .collection( true )
				                    .build(),
				descriptor
		);

		descriptor = EntityUtils.resolveEntityTypeDescriptor(
				collection( LinkedHashSet.class, valueOf( MyClassWithGenericLong.class ) ), entityRegistry
		);

		assertEquals(
				EntityTypeDescriptor.builder()
				                    .sourceTypeDescriptor( collection( LinkedHashSet.class, valueOf( MyClassWithGenericLong.class ) ) )
				                    .targetTypeDescriptor( valueOf( MyClassWithGenericLong.class ) )
				                    .collection( true )
				                    .build(),
				descriptor
		);
	}

	@Test
	public void mapTypesCannotBeResolved() {
		EntityTypeDescriptor descriptor = EntityUtils.resolveEntityTypeDescriptor(
				TypeDescriptor.map( HashMap.class, valueOf( String.class ), valueOf( MyClassWithGenericLong.class ) ), entityRegistry
		);

		assertEquals(
				EntityTypeDescriptor.builder()
				                    .sourceTypeDescriptor(
						                    TypeDescriptor.map( HashMap.class, valueOf( String.class ), valueOf( MyClassWithGenericLong.class ) ) )
				                    .build(),
				descriptor
		);
	}

	@Test
	public void customCollectionTypesHaveTheirMemberAsTargetIfNotAvailableInTheEntityRegistry() {
		EntityTypeDescriptor descriptor = EntityUtils.resolveEntityTypeDescriptor( valueOf( CustomList.class ), entityRegistry );
		assertEquals(
				EntityTypeDescriptor.builder()
				                    .sourceTypeDescriptor( valueOf( CustomList.class ) )
				                    .targetTypeDescriptor( valueOf( MyClassWithGenericLong.class ) )
				                    .collection( true )
				                    .build(),
				descriptor
		);

		descriptor = EntityUtils.resolveEntityTypeDescriptor( valueOf( CustomMap.class ), entityRegistry );
		assertEquals(
				EntityTypeDescriptor.builder()
				                    .sourceTypeDescriptor( valueOf( CustomMap.class ) )
				                    .build(),
				descriptor
		);
	}

	@Test
	public void customCollectionTypesAreUsedAsTargetIfAvailableInTheEntityRegistry() {
		when( entityRegistry.contains( CustomList.class ) ).thenReturn( true );
		when( entityRegistry.contains( CustomMap.class ) ).thenReturn( true );

		EntityTypeDescriptor descriptor = EntityUtils.resolveEntityTypeDescriptor( valueOf( CustomList.class ), entityRegistry );
		assertEquals(
				EntityTypeDescriptor.builder()
				                    .sourceTypeDescriptor( valueOf( CustomList.class ) )
				                    .targetTypeDescriptor( valueOf( CustomList.class ) )
				                    .build(),
				descriptor
		);

		descriptor = EntityUtils.resolveEntityTypeDescriptor( valueOf( CustomMap.class ), entityRegistry );
		assertEquals(
				EntityTypeDescriptor.builder()
				                    .sourceTypeDescriptor( valueOf( CustomMap.class ) )
				                    .targetTypeDescriptor( valueOf( CustomMap.class ) )
				                    .build(),
				descriptor
		);
	}

	@Test
	public void unmodifiedSortIsReturned() {
		Sort sort = new Sort( Arrays.asList(
				new Sort.Order( Sort.Direction.ASC, "nullsFirst" ),
				new Sort.Order( Sort.Direction.DESC, "nullsLast" ),
				new Sort.Order( Sort.Direction.ASC, "nullHandlingFixed" ),
				new Sort.Order( Sort.Direction.DESC, "ignoreCase" ),
				new Sort.Order( Sort.Direction.ASC, "unmodified" )
		) );

		EntityPropertyRegistry propertyRegistry = mock( EntityPropertyRegistry.class );
		assertEquals( sort, EntityUtils.translateSort( sort, propertyRegistry ) );
	}

	@Test
	public void translateSort() {
		Sort sort = new Sort( Arrays.asList(
				new Sort.Order( Sort.Direction.ASC, "nullsFirst" ),
				new Sort.Order( Sort.Direction.DESC, "nullsLast" ),
				new Sort.Order( Sort.Direction.ASC, "nullHandlingFixed" ),
				new Sort.Order( Sort.Direction.DESC, "ignoreCase" ),
				new Sort.Order( Sort.Direction.ASC, "unmodified" )
		) );

		EntityPropertyRegistry propertyRegistry = mock( EntityPropertyRegistry.class );
		register( propertyRegistry, "nullsFirst", new Sort.Order( "prop-nullsFirst" ) );
		register( propertyRegistry, "nullsLast", new Sort.Order( "prop-nullsLast" ) );
		register( propertyRegistry, "nullHandlingFixed", new Sort.Order( "prop-nullHandlingFixed" ).nullsLast() );
		register( propertyRegistry, "ignoreCase", new Sort.Order( "prop-ignoreCase" ).ignoreCase() );

		Sort translated = EntityUtils.translateSort( sort, propertyRegistry );

		assertEquals(
				new Sort( Arrays.asList(
						new Sort.Order( Sort.Direction.ASC, "prop-nullsFirst", Sort.NullHandling.NULLS_FIRST ),
						new Sort.Order( Sort.Direction.DESC, "prop-nullsLast", Sort.NullHandling.NULLS_LAST ),
						new Sort.Order( Sort.Direction.ASC, "prop-nullHandlingFixed", Sort.NullHandling.NULLS_LAST ),
						new Sort.Order( Sort.Direction.DESC, "prop-ignoreCase", Sort.NullHandling.NULLS_LAST )
								.ignoreCase(),
						new Sort.Order( Sort.Direction.ASC, "unmodified" )
				) ),
				translated
		);
	}

	private void register( EntityPropertyRegistry propertyRegistry, String name, Sort.Order order ) {
		EntityPropertyDescriptor descriptor = mock( EntityPropertyDescriptor.class );
		when( descriptor.getAttribute( Sort.Order.class ) ).thenReturn( order );
		when( propertyRegistry.getProperty( name ) ).thenReturn( descriptor );
	}

	@Test
	public void combineSort() {
		Sort one = new Sort( new Sort.Order( Sort.Direction.ASC, "two" ), new Sort.Order( Sort.Direction.DESC, "three" ) );
		Sort two = new Sort( new Sort.Order( Sort.Direction.ASC, "one" ), new Sort.Order( Sort.Direction.DESC, "two" ) );
		Sort three = new Sort( Sort.Direction.ASC, "two", "three", "one", "four" );

		Sort merged = EntityUtils.combineSortSpecifiers( one, two, three );

		assertEquals(
				new Sort(
						new Sort.Order( Sort.Direction.ASC, "two" ),
						new Sort.Order( Sort.Direction.DESC, "three" ),
						new Sort.Order( Sort.Direction.ASC, "one" ),
						new Sort.Order( Sort.Direction.ASC, "four" )

				),
				merged
		);

		assertNull( EntityUtils.combineSortSpecifiers( null, null ) );
		assertEquals( one, EntityUtils.combineSortSpecifiers( one, null ) );
		assertEquals( two, EntityUtils.combineSortSpecifiers( null, two ) );
	}

	@Test
	public void createDisplayName() {
		assertEquals( "Name", EntityUtils.generateDisplayName( "name" ) );
		assertEquals( "Principal name", EntityUtils.generateDisplayName( "principalName" ) );
		assertEquals( "Address street", EntityUtils.generateDisplayName( "address.street" ) );
		assertEquals( "Customer address zip code", EntityUtils.generateDisplayName( "customer.address.zipCode" ) );
		assertEquals( "Groups size", EntityUtils.generateDisplayName( "groups.size()" ) );
		assertEquals( "Text with html", EntityUtils.generateDisplayName( "textWithHTML" ) );
		assertEquals( "Members 0 length", EntityUtils.generateDisplayName( "members[0].length" ) );
		assertEquals( "Generated label", EntityUtils.generateDisplayName( "Generated label" ) );
		assertEquals( "Basic security principal", EntityUtils.generateDisplayName( "BasicSecurityPrincipal" ) );
		assertEquals( "Permission group", EntityUtils.generateDisplayName( "PermissionGroup" ) );
		assertEquals( "Some field name", EntityUtils.generateDisplayName( "_someFieldName" ) );
		assertEquals( "Test for me", EntityUtils.generateDisplayName( "_TEST_FOR_ME" ) );
		assertEquals( "OAuth2 client", EntityUtils.generateDisplayName( "OAuth2Client" ) );
	}

	@Test
	public void mergeDisplayNames() {
		assertEquals( "Name", EntityUtils.combineDisplayNames( "name" ) );
		assertEquals( "Name principal name", EntityUtils.combineDisplayNames( "name", "principalName" ) );
		assertEquals( "Address street customer address zip code", EntityUtils.combineDisplayNames( "address.street",
		                                                                                           "customer.address.zipCode" ) );
		assertEquals( "Groups size text with html members 0 length", EntityUtils.combineDisplayNames( "groups.size()",
		                                                                                              "textWithHTML",
		                                                                                              "members[0].length" ) );
		assertEquals( "Basic security principal permission group some field name test for me",
		              EntityUtils.combineDisplayNames( "BasicSecurityPrincipal", "PermissionGroup", "_someFieldName",
		                                               "_TEST_FOR_ME" ) );
	}

	@Test
	public void generateEntityName() {
		assertEquals( "testEntityUtils", EntityUtils.generateEntityName( TestEntityUtils.class ) );
		assertEquals( "testEntityUtils.subEntity", EntityUtils.generateEntityName( SubEntity.class ) );
		assertEquals( "testEntityUtils.subEntity.secondLevel",
		              EntityUtils.generateEntityName( SubEntity.SecondLevel.class )
		);
	}

	static class MyClassWithGeneric<T>
	{
	}

	static class MyClassWithGenericLong extends MyClassWithGeneric<Long>
	{
		public Optional<String> myOptional;
	}

	static class CustomList extends ArrayList<MyClassWithGenericLong>
	{
	}

	static class CustomMap extends HashMap<String, MyClassWithGenericLong>
	{
	}

	static class SubEntity
	{
		static class SecondLevel
		{
		}
	}
}
