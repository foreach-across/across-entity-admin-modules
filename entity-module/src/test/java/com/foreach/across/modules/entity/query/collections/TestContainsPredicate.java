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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.core.convert.TypeDescriptor;

import java.awt.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.Predicate;

import static com.foreach.across.modules.entity.query.EntityQueryOps.CONTAINS;
import static com.foreach.across.modules.entity.query.EntityQueryOps.NOT_CONTAINS;
import static com.foreach.across.modules.entity.query.collections.CollectionEntityQueryPredicates.createPredicate;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * @author Steven Gentens
 * @since 3.1.0
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@SuppressWarnings("unchecked")
public class TestContainsPredicate
{
	@Mock
	private EntityPropertyDescriptor descriptor;

	@Mock
	private CollectionEntityQueryItem item;

	@BeforeEach
	public void setUp() {
		when( item.getPropertyValue( "colors" ) ).thenReturn( Arrays.asList( Color.RED, Color.CYAN ) );
		when( item.getPropertyValue( "sports" ) ).thenReturn( new String[] { "swimming", "running" } );
	}

	@Test
	public void collectionContains() {
		when( descriptor.getPropertyTypeDescriptor() ).thenReturn( TypeDescriptor.collection( Collection.class, TypeDescriptor.valueOf( Color.class ) ) );
		Predicate predicate = createPredicate( new EntityQueryCondition( "colors", CONTAINS, Color.BLUE ), descriptor );
		assertThat( predicate.test( item ) ).isFalse();

		predicate = createPredicate( new EntityQueryCondition( "colors", CONTAINS, Color.RED ), descriptor );
		assertThat( predicate.test( item ) ).isTrue();
	}

	@Test
	public void collectionNotContains() {
		when( descriptor.getPropertyTypeDescriptor() ).thenReturn( TypeDescriptor.collection( Collection.class, TypeDescriptor.valueOf( Color.class ) ) );
		Predicate predicate = createPredicate( new EntityQueryCondition( "colors", NOT_CONTAINS, Color.BLUE ), descriptor );
		assertThat( predicate.test( item ) ).isTrue();

		predicate = createPredicate( new EntityQueryCondition( "colors", NOT_CONTAINS, Color.RED ), descriptor );
		assertThat( predicate.test( item ) ).isFalse();
	}

	@Test
	public void arrayContains() {
		when( descriptor.getPropertyTypeDescriptor() ).thenReturn( TypeDescriptor.array( TypeDescriptor.valueOf( String.class ) ) );
		Predicate predicate = createPredicate( new EntityQueryCondition( "sports", CONTAINS, "cycling" ), descriptor );
		assertThat( predicate.test( item ) ).isFalse();

		predicate = createPredicate( new EntityQueryCondition( "sports", CONTAINS, "swimming" ), descriptor );
		assertThat( predicate.test( item ) ).isTrue();
	}

	@Test
	public void arrayNotContains() {
		when( descriptor.getPropertyTypeDescriptor() ).thenReturn( TypeDescriptor.array( TypeDescriptor.valueOf( String.class ) ) );
		Predicate predicate = createPredicate( new EntityQueryCondition( "sports", NOT_CONTAINS, "cycling" ), descriptor );
		assertThat( predicate.test( item ) ).isTrue();

		predicate = createPredicate( new EntityQueryCondition( "sports", NOT_CONTAINS, "swimming" ), descriptor );
		assertThat( predicate.test( item ) ).isFalse();
	}
}
