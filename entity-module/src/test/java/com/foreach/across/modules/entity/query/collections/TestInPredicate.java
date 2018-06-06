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

import com.foreach.across.modules.entity.query.EntityQueryCondition;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.Predicate;

import static com.foreach.across.modules.entity.query.EntityQueryOps.IN;
import static com.foreach.across.modules.entity.query.EntityQueryOps.NOT_IN;
import static com.foreach.across.modules.entity.query.collections.CollectionEntityQueryPredicates.createPredicate;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * @author Steven Gentens
 * @since 0.0.1
 */
@RunWith(MockitoJUnitRunner.class)
public class TestInPredicate
{
	@Mock
	private EntityPropertyDescriptor descriptor;

	@Mock
	private CollectionEntityQueryItem item;

	@Before
	public void setUp() {
		when( item.getPropertyValue( "color" ) ).thenReturn( Color.RED );
		when( item.getPropertyValue( "sport" ) ).thenReturn( "swimming" );
	}

	@Test
	@SuppressWarnings("unchecked")
	public void inCollection() {
		Predicate predicate = createPredicate( new EntityQueryCondition( "color", IN, Arrays.asList( Color.RED, Color.CYAN ) ), descriptor );
		assertThat( predicate.test( item ) ).isTrue();

		predicate = createPredicate( new EntityQueryCondition( "color", IN, Arrays.asList( Color.BLUE, Color.CYAN ) ), descriptor );
		assertThat( predicate.test( item ) ).isFalse();
	}

	@Test
	@SuppressWarnings("unchecked")
	public void inArray() {
		Predicate predicate = createPredicate( new EntityQueryCondition( "sport", IN, (Object[]) new String[] { "swimming", "cycling" } ), descriptor );
		assertThat( predicate.test( item ) ).isTrue();

		predicate = createPredicate( new EntityQueryCondition( "sport", IN, (Object[]) new String[] { "running", "cycling" } ), descriptor );
		assertThat( predicate.test( item ) ).isFalse();
	}

	@Test
	@SuppressWarnings("unchecked")
	public void inWithSingleItem() {
		Predicate predicate = createPredicate( new EntityQueryCondition( "sport", IN, (Object[]) new String[] { "swimming" } ), descriptor );
		assertThat( predicate.test( item ) ).isTrue();

		predicate = createPredicate( new EntityQueryCondition( "sport", IN, (Object[]) new String[] { "running" } ), descriptor );
		assertThat( predicate.test( item ) ).isFalse();

		predicate = createPredicate( new EntityQueryCondition( "sport", IN, Collections.singletonList( "swimming" ) ), descriptor );
		assertThat( predicate.test( item ) ).isTrue();

		predicate = createPredicate( new EntityQueryCondition( "sport", IN, Collections.singletonList( "running" ) ), descriptor );
		assertThat( predicate.test( item ) ).isFalse();

		predicate = createPredicate( new EntityQueryCondition( "sport", IN, "swimming" ), descriptor );
		assertThat( predicate.test( item ) ).isTrue();

		predicate = createPredicate( new EntityQueryCondition( "sport", IN, "running" ), descriptor );
		assertThat( predicate.test( item ) ).isFalse();
	}

	@Test
	@SuppressWarnings("unchecked")
	public void in() {
		Predicate predicate = createPredicate( new EntityQueryCondition( "color", IN, Color.RED, Color.BLUE ), descriptor );
		assertThat( predicate.test( item ) ).isTrue();

		predicate = createPredicate( new EntityQueryCondition( "color", IN, Color.CYAN, Color.BLUE ), descriptor );
		assertThat( predicate.test( item ) ).isFalse();

		predicate = createPredicate( new EntityQueryCondition( "sport", IN, "swimming", "cycling" ), descriptor );
		assertThat( predicate.test( item ) ).isTrue();

		predicate = createPredicate( new EntityQueryCondition( "sport", IN, "running", "cycling" ), descriptor );
		assertThat( predicate.test( item ) ).isFalse();
	}

	@Test
	@SuppressWarnings("unchecked")
	public void listIn() {
		List<Object> value = createList( Color.RED, Color.GREEN );
		when( item.getPropertyValue( "list" ) ).thenReturn( value );

		List<Object> listOfLists = createList( value, createList( Color.GREEN, Color.CYAN ), createList( Color.BLACK, Color.BLUE ) );
		Predicate predicate = createPredicate( new EntityQueryCondition( "list", IN, listOfLists ), descriptor );
		assertThat( predicate.test( item ) ).isTrue();

		listOfLists = createList( createList( Color.GREEN, Color.CYAN ), createList( Color.BLACK, Color.BLUE ) );
		predicate = createPredicate( new EntityQueryCondition( "list", IN, listOfLists ), descriptor );
		assertThat( predicate.test( item ) ).isFalse();

		predicate = createPredicate(
				new EntityQueryCondition( "list", IN, value, createList( Color.GREEN, Color.CYAN ), createList( Color.BLACK, Color.BLUE ) ), descriptor );
		assertThat( predicate.test( item ) ).isTrue();

		predicate = createPredicate( new EntityQueryCondition( "list", IN, createList( Color.GREEN, Color.CYAN ), createList( Color.BLACK, Color.BLUE ) ),
		                             descriptor );
		assertThat( predicate.test( item ) ).isFalse();
	}

	@Test
	@SuppressWarnings("unchecked")
	public void listInByValues() {
		when( item.getPropertyValue( "list" ) ).thenReturn( createList( Color.RED, Color.GREEN ) );

		List<Object> listOfLists = createList( createList( Color.RED, Color.GREEN ), createList( Color.GREEN, Color.CYAN ),
		                                       createList( Color.BLACK, Color.BLUE ) );
		Predicate predicate = createPredicate( new EntityQueryCondition( "list", IN, listOfLists ), descriptor );
		assertThat( predicate.test( item ) ).isTrue();

		predicate = createPredicate(
				new EntityQueryCondition( "list", IN, createList( Color.RED, Color.GREEN ), createList( Color.GREEN, Color.CYAN ),
				                          createList( Color.BLACK, Color.BLUE ) ), descriptor );
		assertThat( predicate.test( item ) ).isTrue();
	}

	@Test
	@SuppressWarnings("unchecked")
	public void mapIn() {
		Map<String, Object> value = createMap( Color.RED, Color.GREEN );
		when( item.getPropertyValue( "map" ) ).thenReturn( value );

		List<Object> listOfMaps = createList( value, createMap( Color.GREEN, Color.CYAN ), createMap( Color.BLACK, Color.BLUE ) );
		Predicate predicate = createPredicate( new EntityQueryCondition( "map", IN, listOfMaps ), descriptor );
		assertThat( predicate.test( item ) ).isTrue();

		listOfMaps = createList( createMap( Color.GREEN, Color.CYAN ), createMap( Color.BLACK, Color.BLUE ) );
		predicate = createPredicate( new EntityQueryCondition( "map", IN, listOfMaps ), descriptor );
		assertThat( predicate.test( item ) ).isFalse();

		predicate = createPredicate( new EntityQueryCondition( "map", IN, value, createMap( Color.GREEN, Color.CYAN ), createMap( Color.BLACK, Color.BLUE ) ),
		                             descriptor );
		assertThat( predicate.test( item ) ).isTrue();

		predicate = createPredicate( new EntityQueryCondition( "map", IN, createMap( Color.GREEN, Color.CYAN ), createMap( Color.BLACK, Color.BLUE ) ),
		                             descriptor );
		assertThat( predicate.test( item ) ).isFalse();
	}

	@Test
	@SuppressWarnings("unchecked")
	public void mapInByValues() {
		when( item.getPropertyValue( "map" ) ).thenReturn( createMap( Color.RED, Color.GREEN ) );

		List<Object> listOfMaps = createList( createMap( Color.RED, Color.GREEN ), createMap( Color.GREEN, Color.CYAN ), createMap( Color.BLACK, Color.BLUE ) );
		Predicate predicate = createPredicate( new EntityQueryCondition( "map", IN, listOfMaps ), descriptor );
		assertThat( predicate.test( item ) ).isTrue();

		predicate = createPredicate( new EntityQueryCondition( "map", IN, createMap( Color.RED, Color.GREEN ), createMap( Color.GREEN, Color.CYAN ),
		                                                       createMap( Color.BLACK, Color.BLUE ) ), descriptor );
		assertThat( predicate.test( item ) ).isTrue();
	}

	@Test
	@SuppressWarnings("unchecked")
	public void notInCollection() {
		Predicate predicate = createPredicate( new EntityQueryCondition( "color", NOT_IN, Arrays.asList( Color.RED, Color.CYAN ) ), descriptor );
		assertThat( predicate.test( item ) ).isFalse();

		predicate = createPredicate( new EntityQueryCondition( "color", NOT_IN, Arrays.asList( Color.BLUE, Color.CYAN ) ), descriptor );
		assertThat( predicate.test( item ) ).isTrue();
	}

	@Test
	@SuppressWarnings("unchecked")
	public void notInArray() {
		Predicate predicate = createPredicate( new EntityQueryCondition( "sport", NOT_IN, (Object[]) new String[] { "swimming", "cycling" } ), descriptor );
		assertThat( predicate.test( item ) ).isFalse();

		predicate = createPredicate( new EntityQueryCondition( "sport", NOT_IN, (Object[]) new String[] { "running", "cycling" } ), descriptor );
		assertThat( predicate.test( item ) ).isTrue();
	}

	@Test
	@SuppressWarnings("unchecked")
	public void notInWithSingleItem() {
		Predicate predicate = createPredicate( new EntityQueryCondition( "sport", NOT_IN, (Object[]) new String[] { "swimming" } ), descriptor );
		assertThat( predicate.test( item ) ).isFalse();

		predicate = createPredicate( new EntityQueryCondition( "sport", NOT_IN, (Object[]) new String[] { "running" } ), descriptor );
		assertThat( predicate.test( item ) ).isTrue();

		predicate = createPredicate( new EntityQueryCondition( "sport", NOT_IN, Collections.singletonList( "swimming" ) ), descriptor );
		assertThat( predicate.test( item ) ).isFalse();

		predicate = createPredicate( new EntityQueryCondition( "sport", NOT_IN, Collections.singletonList( "running" ) ), descriptor );
		assertThat( predicate.test( item ) ).isTrue();

		predicate = createPredicate( new EntityQueryCondition( "sport", NOT_IN, "swimming" ), descriptor );
		assertThat( predicate.test( item ) ).isFalse();

		predicate = createPredicate( new EntityQueryCondition( "sport", NOT_IN, "running" ), descriptor );
		assertThat( predicate.test( item ) ).isTrue();
	}

	@Test
	@SuppressWarnings("unchecked")
	public void notIn() {
		Predicate predicate = createPredicate( new EntityQueryCondition( "color", NOT_IN, Color.RED, Color.BLUE ), descriptor );
		assertThat( predicate.test( item ) ).isFalse();

		predicate = createPredicate( new EntityQueryCondition( "color", NOT_IN, Color.CYAN, Color.BLUE ), descriptor );
		assertThat( predicate.test( item ) ).isTrue();

		predicate = createPredicate( new EntityQueryCondition( "sport", NOT_IN, "swimming", "cycling" ), descriptor );
		assertThat( predicate.test( item ) ).isFalse();

		predicate = createPredicate( new EntityQueryCondition( "sport", NOT_IN, "running", "cycling" ), descriptor );
		assertThat( predicate.test( item ) ).isTrue();
	}

	@Test
	@SuppressWarnings("unchecked")
	public void listNotIn() {
		List<Object> value = createList( Color.RED, Color.GREEN );
		when( item.getPropertyValue( "list" ) ).thenReturn( value );

		List<Object> listOfLists = createList( value, createList( Color.GREEN, Color.CYAN ), createList( Color.BLACK, Color.BLUE ) );
		Predicate predicate = createPredicate( new EntityQueryCondition( "list", NOT_IN, listOfLists ), descriptor );
		assertThat( predicate.test( item ) ).isFalse();

		listOfLists = createList( createList( Color.GREEN, Color.CYAN ), createList( Color.BLACK, Color.BLUE ) );
		predicate = createPredicate( new EntityQueryCondition( "list", NOT_IN, listOfLists ), descriptor );
		assertThat( predicate.test( item ) ).isTrue();

		predicate = createPredicate(
				new EntityQueryCondition( "list", NOT_IN, value, createList( Color.GREEN, Color.CYAN ), createList( Color.BLACK, Color.BLUE ) ), descriptor );
		assertThat( predicate.test( item ) ).isFalse();

		predicate = createPredicate( new EntityQueryCondition( "list", NOT_IN, createList( Color.GREEN, Color.CYAN ), createList( Color.BLACK, Color.BLUE ) ),
		                             descriptor );
		assertThat( predicate.test( item ) ).isTrue();
	}

	@Test
	@SuppressWarnings("unchecked")
	public void listNotInByValues() {
		when( item.getPropertyValue( "list" ) ).thenReturn( createList( Color.RED, Color.GREEN ) );

		List<Object> listOfLists = createList( createList( Color.RED, Color.GREEN ), createList( Color.GREEN, Color.CYAN ),
		                                       createList( Color.BLACK, Color.BLUE ) );
		Predicate predicate = createPredicate( new EntityQueryCondition( "list", NOT_IN, listOfLists ), descriptor );
		assertThat( predicate.test( item ) ).isFalse();

		predicate = createPredicate(
				new EntityQueryCondition( "list", NOT_IN, createList( Color.RED, Color.GREEN ), createList( Color.GREEN, Color.CYAN ),
				                          createList( Color.BLACK, Color.BLUE ) ), descriptor );
		assertThat( predicate.test( item ) ).isFalse();
	}

	@Test
	@SuppressWarnings("unchecked")
	public void mapNotIn() {
		Map<String, Object> value = createMap( Color.RED, Color.GREEN );
		when( item.getPropertyValue( "map" ) ).thenReturn( value );

		List<Object> listOfMaps = createList( value, createMap( Color.GREEN, Color.CYAN ), createMap( Color.BLACK, Color.BLUE ) );
		Predicate predicate = createPredicate( new EntityQueryCondition( "map", NOT_IN, listOfMaps ), descriptor );
		assertThat( predicate.test( item ) ).isFalse();

		listOfMaps = createList( createMap( Color.GREEN, Color.CYAN ), createMap( Color.BLACK, Color.BLUE ) );
		predicate = createPredicate( new EntityQueryCondition( "map", NOT_IN, listOfMaps ), descriptor );
		assertThat( predicate.test( item ) ).isTrue();

		predicate = createPredicate(
				new EntityQueryCondition( "map", NOT_IN, value, createMap( Color.GREEN, Color.CYAN ), createMap( Color.BLACK, Color.BLUE ) ),
				descriptor );
		assertThat( predicate.test( item ) ).isFalse();

		predicate = createPredicate( new EntityQueryCondition( "map", NOT_IN, createMap( Color.GREEN, Color.CYAN ), createMap( Color.BLACK, Color.BLUE ) ),
		                             descriptor );
		assertThat( predicate.test( item ) ).isTrue();
	}

	@Test
	@SuppressWarnings("unchecked")
	public void mapNotInByValues() {
		when( item.getPropertyValue( "map" ) ).thenReturn( createMap( Color.RED, Color.GREEN ) );

		List<Object> listOfMaps = createList( createMap( Color.RED, Color.GREEN ), createMap( Color.GREEN, Color.CYAN ), createMap( Color.BLACK, Color.BLUE ) );
		Predicate predicate = createPredicate( new EntityQueryCondition( "map", NOT_IN, listOfMaps ), descriptor );
		assertThat( predicate.test( item ) ).isFalse();

		predicate = createPredicate( new EntityQueryCondition( "map", NOT_IN, createMap( Color.RED, Color.GREEN ), createMap( Color.GREEN, Color.CYAN ),
		                                                       createMap( Color.BLACK, Color.BLUE ) ), descriptor );
		assertThat( predicate.test( item ) ).isFalse();
	}

	private Map<String, Object> createMap( Object... objects ) {
		Map<String, Object> map = new HashMap<>();
		Arrays.stream( objects )
		      .forEach( o -> map.put( o.toString(), o ) );
		return map;
	}

	private List<Object> createList( Object... objects ) {
		return new ArrayList<>( Arrays.asList( objects ) );
	}
}
