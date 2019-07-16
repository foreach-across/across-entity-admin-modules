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

package com.foreach.across.modules.entity.query.collections;

import com.foreach.across.modules.entity.query.EntityQuery;
import com.foreach.across.modules.entity.query.EntityQueryCondition;
import com.foreach.across.modules.entity.query.EntityQueryExecutor;
import com.foreach.across.modules.entity.query.EntityQueryOps;
import com.foreach.across.modules.entity.registry.properties.DefaultEntityPropertyRegistry;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptorFactoryImpl;
import com.foreach.across.modules.entity.registry.properties.registrars.DefaultPropertiesRegistrar;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;

import java.util.*;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Arne Vandamme
 * @since 3.1.0
 */
public class TestCollectionEntityQueryExecutor
{
	private Entry john = new Entry( 1, "John" );
	private Entry jane = new Entry( 2, "Jane" );
	private Entry george = new Entry( 1, "George" );
	private Collection<Entry> entries = new ArrayList<>( Arrays.asList( john, george, jane ) );

	private DefaultEntityPropertyRegistry propertyRegistry = new DefaultEntityPropertyRegistry();

	private EntityQueryExecutor<Entry> executor;

	@Before
	public void before() {
		new DefaultPropertiesRegistrar( new EntityPropertyDescriptorFactoryImpl() ).accept( Entry.class, propertyRegistry );

		executor = new CollectionEntityQueryExecutor<>( entries, propertyRegistry );
	}

	@Test
	public void allQueryReturnsAllItemsInOriginalOrder() {
		assertThat( executor.findAll( EntityQuery.all() ) ).containsExactly( john, george, jane );
	}

	@Test
	@SuppressWarnings("unchecked")
	public void supplierIsUsedOnEveryFind() {
		Supplier<Collection<Entry>> supplier = mock( Supplier.class );
		when( supplier.get() ).thenReturn( Arrays.asList( john, george ) )
		                      .thenReturn( Arrays.asList( john, jane ) );

		executor = new CollectionEntityQueryExecutor<>( supplier, propertyRegistry );
		assertThat( executor.findAll( EntityQuery.all() ) ).containsExactly( john, george );
		assertThat( executor.findAll( EntityQuery.all() ) ).containsExactly( john, jane );
	}

	@Test
	public void noResults() {
		entries.clear();

		Page<Entry> page = executor.findAll( EntityQuery.all(), new PageRequest( 1, 2 ) );
		assertThat( page.getTotalPages() ).isEqualTo( 0 );
		assertThat( page.getTotalElements() ).isEqualTo( 0 );
	}

	@Test
	public void pagingOnly() {
		Page<Entry> page = executor.findAll( EntityQuery.all(), new PageRequest( 0, 2 ) );
		assertThat( page ).isNotNull();
		assertThat( page.getTotalPages() ).isEqualTo( 2 );
		assertThat( page.getTotalElements() ).isEqualTo( 3 );
		assertThat( page.getContent() ).containsExactly( john, george );

		page = executor.findAll( EntityQuery.all(), new PageRequest( 1, 2 ) );
		assertThat( page ).isNotNull();
		assertThat( page.getTotalPages() ).isEqualTo( 2 );
		assertThat( page.getTotalElements() ).isEqualTo( 3 );
		assertThat( page.getContent() ).containsExactly( jane );
	}

	@Test
	public void filteringOnly() {
		List<Entry> found = executor.findAll( EntityQuery.and( new EntityQueryCondition( "name", EntityQueryOps.EQ, "John" ) ) );
		assertThat( found ).containsExactly( john );

		found = executor.findAll( EntityQuery.or( new EntityQueryCondition( "name", EntityQueryOps.EQ, "Jane" ),
		                                          new EntityQueryCondition( "name", EntityQueryOps.EQ, "George" ) ) );
		assertThat( found ).containsExactly( george, jane );

		found = executor.findAll(
				EntityQuery.and(
						EntityQuery.or( new EntityQueryCondition( "name", EntityQueryOps.EQ, "Jane" ),
						                new EntityQueryCondition( "name", EntityQueryOps.EQ, "George" ) ),
						new EntityQueryCondition( "group", EntityQueryOps.EQ, 1 ) )
		);
		assertThat( found ).containsExactly( george );
	}

	@Test
	public void filteringAndPaging() {
		EntityQuery query = EntityQuery.or( new EntityQueryCondition( "name", EntityQueryOps.EQ, "Jane" ),
		                                    new EntityQueryCondition( "name", EntityQueryOps.EQ, "George" ) );

		Page<Entry> page = executor.findAll( query, new PageRequest( 0, 1 ) );
		assertThat( page.getTotalPages() ).isEqualTo( 2 );
		assertThat( page.getTotalElements() ).isEqualTo( 2 );
		assertThat( page.getContent() ).containsExactly( george );

		page = executor.findAll( query, new PageRequest( 1, 1 ) );
		assertThat( page.getTotalPages() ).isEqualTo( 2 );
		assertThat( page.getTotalElements() ).isEqualTo( 2 );
		assertThat( page.getContent() ).containsExactly( jane );
	}

	@Test
	public void sortingOnly() {
		assertThat( executor.findAll( EntityQuery.all(), new Sort( Direction.ASC, "name" ) ) )
				.containsExactly( george, jane, john );
		assertThat( executor.findAll( EntityQuery.all(), new Sort( Direction.DESC, "name" ) ) )
				.containsExactly( john, jane, george );

		assertThat( executor.findAll( EntityQuery.all(), new Sort( Direction.ASC, "group", "name" ) ) )
				.containsExactly( george, john, jane );
		assertThat( executor.findAll( EntityQuery.all(), new Sort( Direction.DESC, "group", "name" ) ) )
				.containsExactly( jane, john, george );

		assertThat( executor.findAll( EntityQuery.all(), new Sort( new Order( Direction.ASC, "group" ), new Order( Direction.DESC, "name" ) ) ) )
				.containsExactly( john, george, jane );
		assertThat( executor.findAll( EntityQuery.all(), new Sort( new Order( Direction.DESC, "group" ), new Order( Direction.ASC, "name" ) ) ) )
				.containsExactly( jane, george, john );
	}

	@Test
	public void sortingAndPaging() {
		Page<Entry> page = executor.findAll( EntityQuery.all(), new PageRequest( 0, 2, Direction.ASC, "name" ) );
		assertThat( page.getTotalPages() ).isEqualTo( 2 );
		assertThat( page.getTotalElements() ).isEqualTo( 3 );
		assertThat( page.getContent() ).containsExactly( george, jane );

		page = executor.findAll( EntityQuery.all(), new PageRequest( 1, 2, Direction.ASC, "name" ) );
		assertThat( page.getTotalPages() ).isEqualTo( 2 );
		assertThat( page.getTotalElements() ).isEqualTo( 3 );
		assertThat( page.getContent() ).containsExactly( john );
	}

	@Test
	public void filteringAndSortingAndPaging() {
		EntityQuery query = EntityQuery.or( new EntityQueryCondition( "name", EntityQueryOps.EQ, "Jane" ),
		                                    new EntityQueryCondition( "name", EntityQueryOps.EQ, "George" ) );

		Page<Entry> page = executor.findAll( query, new PageRequest( 0, 1, Direction.DESC, "name" ) );
		assertThat( page.getTotalPages() ).isEqualTo( 2 );
		assertThat( page.getTotalElements() ).isEqualTo( 2 );
		assertThat( page.getContent() ).containsExactly( jane );

		page = executor.findAll( query, new PageRequest( 1, 1, Direction.DESC, "name" ) );
		assertThat( page.getTotalPages() ).isEqualTo( 2 );
		assertThat( page.getTotalElements() ).isEqualTo( 2 );
		assertThat( page.getContent() ).containsExactly( george );
	}

	@Test
	public void nullReturnsFalseByDefaultUnlessIsNullOrIsNotNull() {
		DefaultEntityPropertyRegistry propertyRegistry = new DefaultEntityPropertyRegistry();
		new DefaultPropertiesRegistrar( new EntityPropertyDescriptorFactoryImpl() ).accept( Entry.class, propertyRegistry );
		List<Entry> entries = Collections.singletonList( new Entry( -1, null ) );
		EntityQueryExecutor<Entry> executor = new CollectionEntityQueryExecutor<>( entries, propertyRegistry );

		assertThat( executor.findAll( EntityQuery.and( new EntityQueryCondition( "name", EntityQueryOps.EQ, "Jane" ) ) ) ).isEmpty();
		assertThat( executor.findAll( EntityQuery.and( new EntityQueryCondition( "name", EntityQueryOps.NEQ, "Jane" ) ) ) ).isEmpty();
		assertThat( executor.findAll( EntityQuery.and( new EntityQueryCondition( "name", EntityQueryOps.CONTAINS, "Jane" ) ) ) ).isEmpty();
		assertThat( executor.findAll( EntityQuery.and( new EntityQueryCondition( "name", EntityQueryOps.NOT_CONTAINS, "Jane" ) ) ) ).isEmpty();
		assertThat( executor.findAll( EntityQuery.and( new EntityQueryCondition( "name", EntityQueryOps.LIKE, "Jane" ) ) ) ).isEmpty();
		assertThat( executor.findAll( EntityQuery.and( new EntityQueryCondition( "name", EntityQueryOps.LIKE_IC, "Jane" ) ) ) ).isEmpty();
		assertThat( executor.findAll( EntityQuery.and( new EntityQueryCondition( "name", EntityQueryOps.NOT_LIKE, "Jane" ) ) ) ).isEmpty();
		assertThat( executor.findAll( EntityQuery.and( new EntityQueryCondition( "name", EntityQueryOps.NOT_LIKE_IC, "Jane" ) ) ) ).isEmpty();
		assertThat( executor.findAll( EntityQuery.and( new EntityQueryCondition( "name", EntityQueryOps.IN, "Jane" ) ) ) ).isEmpty();
		assertThat( executor.findAll( EntityQuery.and( new EntityQueryCondition( "name", EntityQueryOps.NOT_IN, "Jane" ) ) ) ).isEmpty();
		assertThat( executor.findAll( EntityQuery.and( new EntityQueryCondition( "name", EntityQueryOps.IS_EMPTY ) ) ) ).isEmpty();
		assertThat( executor.findAll( EntityQuery.and( new EntityQueryCondition( "name", EntityQueryOps.IS_NOT_EMPTY ) ) ) ).isEmpty();
		assertThat( executor.findAll( EntityQuery.and( new EntityQueryCondition( "name", EntityQueryOps.IS_NULL ) ) ) ).hasSize( 1 );
		assertThat( executor.findAll( EntityQuery.and( new EntityQueryCondition( "name", EntityQueryOps.IS_NOT_NULL ) ) ) ).isEmpty();
		assertThat( executor.findAll( EntityQuery.and( new EntityQueryCondition( "name", EntityQueryOps.GT, "Jane" ) ) ) ).isEmpty();
		assertThat( executor.findAll( EntityQuery.and( new EntityQueryCondition( "name", EntityQueryOps.LT, "Jane" ) ) ) ).isEmpty();
		assertThat( executor.findAll( EntityQuery.and( new EntityQueryCondition( "name", EntityQueryOps.GE, "Jane" ) ) ) ).isEmpty();
		assertThat( executor.findAll( EntityQuery.and( new EntityQueryCondition( "name", EntityQueryOps.LE, "Jane" ) ) ) ).isEmpty();
	}

	@SuppressWarnings("WeakerAccess")
	@Data
	@RequiredArgsConstructor
	public static class Entry
	{
		private final int group;
		private final String name;
	}
}
