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

import java.util.function.Predicate;

import static com.foreach.across.modules.entity.query.EntityQueryOps.LIKE;
import static com.foreach.across.modules.entity.query.collections.CollectionEntityQueryPredicates.createPredicate;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

/**
 * @author Steven Gentens
 * @since 0.0.1
 */
@RunWith(MockitoJUnitRunner.class)
public class TestLikePredicate
{
	@Mock
	private EntityPropertyDescriptor descriptor;

	@Mock
	private CollectionEntityQueryItem item;

	@Before
	public void setUp() {
		doReturn( String.class ).when( descriptor ).getPropertyType();
		when( descriptor.getPropertyTypeDescriptor() ).thenReturn( TypeDescriptor.valueOf( String.class ) );
		when( item.getPropertyValue( "name" ) ).thenReturn( "Jane" );
	}

	@Test
	@SuppressWarnings("unchecked")
	public void like() {
		Predicate predicate = createPredicate( new EntityQueryCondition( "name", LIKE, "Jane" ), descriptor );
		assertThat( predicate.test( item ) ).isTrue();

		predicate = createPredicate( new EntityQueryCondition( "name", LIKE, "John" ), descriptor );
		assertThat( predicate.test( item ) ).isFalse();

		predicate = createPredicate( new EntityQueryCondition( "name", LIKE, "Ja%" ), descriptor );
		assertThat( predicate.test( item ) ).isTrue();

		predicate = createPredicate( new EntityQueryCondition( "name", LIKE, "Jo%" ), descriptor );
		assertThat( predicate.test( item ) ).isFalse();

		predicate = createPredicate( new EntityQueryCondition( "name", LIKE, "%ne" ), descriptor );
		assertThat( predicate.test( item ) ).isTrue();

		predicate = createPredicate( new EntityQueryCondition( "name", LIKE, "%hn" ), descriptor );
		assertThat( predicate.test( item ) ).isFalse();

		predicate = createPredicate( new EntityQueryCondition( "name", LIKE, "%an%" ), descriptor );
		assertThat( predicate.test( item ) ).isTrue();

		predicate = createPredicate( new EntityQueryCondition( "name", LIKE, "ùoh%" ), descriptor );
		assertThat( predicate.test( item ) ).isFalse();
	}

}
