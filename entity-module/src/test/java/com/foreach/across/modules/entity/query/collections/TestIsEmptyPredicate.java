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
import org.springframework.core.convert.TypeDescriptor;

import java.awt.*;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

import static com.foreach.across.modules.entity.query.EntityQueryOps.IS_EMPTY;
import static com.foreach.across.modules.entity.query.EntityQueryOps.IS_NOT_EMPTY;
import static com.foreach.across.modules.entity.query.collections.CollectionEntityQueryPredicates.createPredicate;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * @author Steven Gentens
 * @since 3.1.0
 */
@RunWith(MockitoJUnitRunner.class)
@SuppressWarnings("unchecked")
public class TestIsEmptyPredicate
{
	private final Object ARGS = null;

	@Mock
	private EntityPropertyDescriptor descriptor;

	@Mock
	private CollectionEntityQueryItem item;

	@Before
	public void setUp() {
		when( item.getPropertyValue( "emptyCollection" ) ).thenReturn( Collections.emptySet() );
		when( item.getPropertyValue( "emptyMap" ) ).thenReturn( Collections.emptyMap() );
		when( item.getPropertyValue( "emptyArray" ) ).thenReturn( new Object[0] );
		when( item.getPropertyValue( "collection" ) ).thenReturn( Collections.singleton( Color.RED ) );
		when( item.getPropertyValue( "map" ) ).thenReturn( Collections.singletonMap( "red", Color.RED ) );
		when( item.getPropertyValue( "array" ) ).thenReturn( new Object[] { Color.RED } );
	}

	@Test
	public void isEmptyCollection() {
		when( descriptor.getPropertyTypeDescriptor() ).thenReturn( TypeDescriptor.collection( Set.class, TypeDescriptor.valueOf( Object.class ) ) );

		Predicate predicate = createPredicate( new EntityQueryCondition( "emptyCollection", IS_EMPTY, ARGS ), descriptor );
		assertThat( predicate.test( item ) ).isTrue();

		predicate = createPredicate( new EntityQueryCondition( "collection", IS_EMPTY, ARGS ), descriptor );
		assertThat( predicate.test( item ) ).isFalse();
	}

	@Test
	public void isNotEmptyCollection() {
		when( descriptor.getPropertyTypeDescriptor() ).thenReturn( TypeDescriptor.collection( Set.class, TypeDescriptor.valueOf( Object.class ) ) );

		Predicate predicate = createPredicate( new EntityQueryCondition( "emptyCollection", IS_NOT_EMPTY, ARGS ), descriptor );
		assertThat( predicate.test( item ) ).isFalse();

		predicate = createPredicate( new EntityQueryCondition( "collection", IS_NOT_EMPTY, ARGS ), descriptor );
		assertThat( predicate.test( item ) ).isTrue();
	}

	@Test
	public void isEmptyMap() {
		when( descriptor.getPropertyTypeDescriptor() ).thenReturn(
				TypeDescriptor.map( Map.class, TypeDescriptor.valueOf( Object.class ), TypeDescriptor.valueOf( Object.class ) ) );

		Predicate predicate = createPredicate( new EntityQueryCondition( "emptyMap", IS_EMPTY, ARGS ), descriptor );
		assertThat( predicate.test( item ) ).isTrue();

		predicate = createPredicate( new EntityQueryCondition( "map", IS_EMPTY, ARGS ), descriptor );
		assertThat( predicate.test( item ) ).isFalse();
	}

	@Test
	public void isNotEmptyMap() {
		when( descriptor.getPropertyTypeDescriptor() ).thenReturn(
				TypeDescriptor.map( Map.class, TypeDescriptor.valueOf( Object.class ), TypeDescriptor.valueOf( Object.class ) ) );

		Predicate predicate = createPredicate( new EntityQueryCondition( "emptyMap", IS_NOT_EMPTY, ARGS ), descriptor );
		assertThat( predicate.test( item ) ).isFalse();

		predicate = createPredicate( new EntityQueryCondition( "map", IS_NOT_EMPTY, ARGS ), descriptor );
		assertThat( predicate.test( item ) ).isTrue();
	}

	@Test
	public void isEmptyArray() {
		when( descriptor.getPropertyTypeDescriptor() ).thenReturn( TypeDescriptor.array( TypeDescriptor.valueOf( Object.class ) ) );

		Predicate predicate = createPredicate( new EntityQueryCondition( "emptyArray", IS_EMPTY, ARGS ), descriptor );
		assertThat( predicate.test( item ) ).isTrue();

		predicate = createPredicate( new EntityQueryCondition( "array", IS_EMPTY, ARGS ), descriptor );
		assertThat( predicate.test( item ) ).isFalse();
	}

	@Test
	public void isNotEmptyArray() {
		when( descriptor.getPropertyTypeDescriptor() ).thenReturn( TypeDescriptor.array( TypeDescriptor.valueOf( Object.class ) ) );

		Predicate predicate = createPredicate( new EntityQueryCondition( "emptyArray", IS_NOT_EMPTY, ARGS ), descriptor );
		assertThat( predicate.test( item ) ).isFalse();

		predicate = createPredicate( new EntityQueryCondition( "array", IS_NOT_EMPTY, ARGS ), descriptor );
		assertThat( predicate.test( item ) ).isTrue();
	}
}
