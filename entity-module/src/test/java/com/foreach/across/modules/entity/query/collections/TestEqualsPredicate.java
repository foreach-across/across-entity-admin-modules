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
import java.util.function.Predicate;

import static com.foreach.across.modules.entity.query.EntityQueryOps.EQ;
import static com.foreach.across.modules.entity.query.EntityQueryOps.NEQ;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * @author Steven Gentens
 * @since 0.0.1
 */
@RunWith(MockitoJUnitRunner.class)
public class TestEqualsPredicate
{
	@Mock
	private EntityPropertyDescriptor descriptor;

	@Mock
	private CollectionEntityQueryItem item;

	@Before
	public void setUp() {
		when( item.getPropertyValue( "name" ) ).thenReturn( "Jane" );
		when( item.getPropertyValue( "color" ) ).thenReturn( Color.RED );
		when( item.getPropertyValue( "number" ) ).thenReturn( 15 );
	}

	@Test
	@SuppressWarnings("unchecked")
	public void equals() {
		Predicate<CollectionEntityQueryItem<Object>> predicate = CollectionEntityQueryPredicates.createPredicate(
				new EntityQueryCondition( "name", EQ, "Jane" ), descriptor );
		assertThat( predicate.test( item ) ).isTrue();

		predicate = CollectionEntityQueryPredicates.createPredicate(
				new EntityQueryCondition( "name", EQ, "John" ), descriptor );
		assertThat( predicate.test( item ) ).isFalse();

		predicate = CollectionEntityQueryPredicates.createPredicate(
				new EntityQueryCondition( "color", EQ, Color.RED ), descriptor );
		assertThat( predicate.test( item ) ).isTrue();

		predicate = CollectionEntityQueryPredicates.createPredicate(
				new EntityQueryCondition( "color", EQ, Color.BLUE ), descriptor );
		assertThat( predicate.test( item ) ).isFalse();

		predicate = CollectionEntityQueryPredicates.createPredicate(
				new EntityQueryCondition( "number", EQ, 15 ), descriptor );
		assertThat( predicate.test( item ) ).isTrue();

		predicate = CollectionEntityQueryPredicates.createPredicate(
				new EntityQueryCondition( "number", EQ, 15L ), descriptor );
		assertThat( predicate.test( item ) ).isFalse();
	}

	@Test
	@SuppressWarnings("unchecked")
	public void notEquals() {
		Predicate<CollectionEntityQueryItem<Object>> predicate = CollectionEntityQueryPredicates.createPredicate(
				new EntityQueryCondition( "name", NEQ, "Jane" ), descriptor );
		assertThat( predicate.test( item ) ).isFalse();

		predicate = CollectionEntityQueryPredicates.createPredicate(
				new EntityQueryCondition( "name", NEQ, "John" ), descriptor );
		assertThat( predicate.test( item ) ).isTrue();

		predicate = CollectionEntityQueryPredicates.createPredicate(
				new EntityQueryCondition( "color", NEQ, Color.RED ), descriptor );
		assertThat( predicate.test( item ) ).isFalse();

		predicate = CollectionEntityQueryPredicates.createPredicate(
				new EntityQueryCondition( "color", NEQ, Color.BLUE ), descriptor );
		assertThat( predicate.test( item ) ).isTrue();

		predicate = CollectionEntityQueryPredicates.createPredicate(
				new EntityQueryCondition( "number", NEQ, 15 ), descriptor );
		assertThat( predicate.test( item ) ).isFalse();

		predicate = CollectionEntityQueryPredicates.createPredicate(
				new EntityQueryCondition( "number", NEQ, 15L ), descriptor );
		assertThat( predicate.test( item ) ).isTrue();
	}
}
