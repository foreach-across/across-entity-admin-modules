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

package com.foreach.across.modules.entity.query;

import com.foreach.across.modules.spring.security.actions.AllowableAction;
import com.foreach.across.modules.spring.security.actions.AllowableActionSet;
import com.foreach.across.modules.spring.security.actions.AllowableActions;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * @author Steven Gentens
 * @since 3.1.1-SNAPSHOT
 */
@RunWith(MockitoJUnitRunner.class)
public class TestAllowableActionFilteringEntityQueryExecutor
{
	private Entry john = new Entry( 1L, "John" );
	private Entry jane = new Entry( 2L, "Jane" );
	private Entry joe = new Entry( 3L, "Joe" );
	private List<Entry> entries = new ArrayList<>( Arrays.asList( john, joe, jane ) );

	private EntityQueryExecutor<Entry> parentExecutor;
	private AllowableActionFilteringEntityQueryExecutor<Entry> executor;

	@Before
	public void setUp() {
		parentExecutor = mock( EntityQueryExecutor.class );

		when( parentExecutor.findAll( any( EntityQuery.class ) ) ).thenReturn( entries );
		when( parentExecutor.findAll( any( EntityQuery.class ), any( Sort.class ) ) ).thenReturn( entries );
		when( parentExecutor.findAll( any( EntityQuery.class ), (Sort) eq( null ) ) ).thenReturn( entries );

		Function<Entry, AllowableActions> resolver = ( entry ) -> {
			AllowableActionSet allowableActions = new AllowableActionSet();
			if ( entry.getName().startsWith( "Ja" ) ) {
				allowableActions.add( AllowableAction.UPDATE );
			}
			if ( entry.getName().startsWith( "Jo" ) ) {
				allowableActions.add( AllowableAction.READ );
			}
			return allowableActions;
		};

		executor = new AllowableActionFilteringEntityQueryExecutor<>( resolver, parentExecutor, AllowableAction.READ );
	}

	@Test
	public void filter() {
		assertThat( executor.findAll( EntityQuery.all() ) )
				.hasSize( 2 )
				.containsExactlyInAnyOrder( john, joe );
		verify( parentExecutor, times( 1 ) ).findAll( EntityQuery.all() );
	}

	@Test
	public void filterAndSort() {
		Sort sortByName = new Sort( Sort.DEFAULT_DIRECTION, "name" );
		assertThat( executor.findAll( EntityQuery.all(), sortByName ) )
				.hasSize( 2 )
				.containsExactlyInAnyOrder( john, joe );
		verify( parentExecutor, times( 1 ) ).findAll( EntityQuery.all(), sortByName );
	}

	@Test
	public void filterAndPaging() {
		Page<Entry> page = executor.findAll( EntityQuery.all(), new PageRequest( 1, 1 ) );
		verify( parentExecutor, times( 1 ) ).findAll( EntityQuery.all(), (Sort) null );
		assertThat( page ).isNotNull();
		assertThat( page.getTotalPages() ).isEqualTo( 2 );
		assertThat( page.getTotalElements() ).isEqualTo( 2 );
		assertThat( page.iterator() ).containsExactlyInAnyOrder( joe );
	}

	@Test
	public void filterAndPagingAndSorting() {
		Sort sortById = new Sort( Sort.Direction.DESC, "id" );
		Page<Entry> page = executor.findAll( EntityQuery.all(), new PageRequest( 0, 5, sortById ) );
		assertThat( page ).isNotNull();
		assertThat( page.getTotalPages() ).isEqualTo( 1 );
		assertThat( page.getTotalElements() ).isEqualTo( 2 );
		assertThat( page.iterator() ).containsExactly( john, joe );
		verify( parentExecutor, times( 1 ) ).findAll( EntityQuery.all(), sortById );
	}

	@Data
	@RequiredArgsConstructor
	public static class Entry
	{
		private final Long id;
		private final String name;
	}
}
