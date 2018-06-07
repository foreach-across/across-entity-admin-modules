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

import java.util.function.Predicate;

import static com.foreach.across.modules.entity.query.EntityQueryOps.*;
import static com.foreach.across.modules.entity.query.collections.CollectionEntityQueryPredicates.createPredicate;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * @author Steven Gentens
 * @since 3.1.0
 */
@RunWith(MockitoJUnitRunner.class)
@SuppressWarnings("unchecked")
public class TestLikePredicate
{
	@Mock
	private EntityPropertyDescriptor descriptor;

	@Mock
	private CollectionEntityQueryItem item;

	@Before
	public void setUp() {
		when( item.getPropertyValue( "name" ) ).thenReturn( "Jane" );
		when( item.getPropertyValue( "title" ) ).thenReturn( "Fun wit%h Dick and Jane" );
	}

	@Test
	public void likeNoWildcard() {
		Predicate predicate = createPredicate( new EntityQueryCondition( "name", LIKE, "Jane" ), descriptor );
		assertThat( predicate.test( item ) ).isTrue();

		predicate = createPredicate( new EntityQueryCondition( "name", LIKE, "John" ), descriptor );
		assertThat( predicate.test( item ) ).isFalse();

		predicate = createPredicate( new EntityQueryCondition( "title", LIKE, "Fun wit\\%h Dick and Jane" ), descriptor );
		assertThat( predicate.test( item ) ).isTrue();
	}

	@Test
	public void notLikeNoWildcard() {
		Predicate predicate = createPredicate( new EntityQueryCondition( "name", NOT_LIKE, "Jane" ), descriptor );
		assertThat( predicate.test( item ) ).isFalse();

		predicate = createPredicate( new EntityQueryCondition( "name", NOT_LIKE, "John" ), descriptor );
		assertThat( predicate.test( item ) ).isTrue();

		predicate = createPredicate( new EntityQueryCondition( "title", NOT_LIKE, "Fun wit\\%h Dick and Jane" ), descriptor );
		assertThat( predicate.test( item ) ).isFalse();
	}

	@Test
	public void likeWildcardLeft() {
		Predicate predicate = createPredicate( new EntityQueryCondition( "name", LIKE, "%ne" ), descriptor );
		assertThat( predicate.test( item ) ).isTrue();

		predicate = createPredicate( new EntityQueryCondition( "name", LIKE, "%hn" ), descriptor );
		assertThat( predicate.test( item ) ).isFalse();
	}

	@Test
	public void notLikeWildcardLeft() {
		Predicate predicate = createPredicate( new EntityQueryCondition( "name", NOT_LIKE, "%ne" ), descriptor );
		assertThat( predicate.test( item ) ).isFalse();

		predicate = createPredicate( new EntityQueryCondition( "name", NOT_LIKE, "%hn" ), descriptor );
		assertThat( predicate.test( item ) ).isTrue();
	}

	@Test
	public void likeWildcardRight() {
		Predicate predicate = createPredicate( new EntityQueryCondition( "name", LIKE, "Ja%" ), descriptor );
		assertThat( predicate.test( item ) ).isTrue();

		predicate = createPredicate( new EntityQueryCondition( "name", LIKE, "Jo%" ), descriptor );
		assertThat( predicate.test( item ) ).isFalse();
	}

	@Test
	public void notLikeWildcardRight() {
		Predicate predicate = createPredicate( new EntityQueryCondition( "name", NOT_LIKE, "Ja%" ), descriptor );
		assertThat( predicate.test( item ) ).isFalse();

		predicate = createPredicate( new EntityQueryCondition( "name", NOT_LIKE, "Jo%" ), descriptor );
		assertThat( predicate.test( item ) ).isTrue();
	}

	@Test
	public void likeInnerWildcard() {
		Predicate predicate = createPredicate( new EntityQueryCondition( "name", LIKE, "J%e" ), descriptor );
		assertThat( predicate.test( item ) ).isTrue();

		predicate = createPredicate( new EntityQueryCondition( "name", LIKE, "J%n" ), descriptor );
		assertThat( predicate.test( item ) ).isFalse();

		predicate = createPredicate( new EntityQueryCondition( "name", LIKE, "J%a%e" ), descriptor );
		assertThat( predicate.test( item ) ).isTrue();
	}

	@Test
	public void notLikeInnerWildcard() {
		Predicate predicate = createPredicate( new EntityQueryCondition( "name", NOT_LIKE, "J%e" ), descriptor );
		assertThat( predicate.test( item ) ).isFalse();

		predicate = createPredicate( new EntityQueryCondition( "name", NOT_LIKE, "J%n" ), descriptor );
		assertThat( predicate.test( item ) ).isTrue();

		predicate = createPredicate( new EntityQueryCondition( "name", NOT_LIKE, "J%a%e" ), descriptor );
		assertThat( predicate.test( item ) ).isFalse();
	}

	@Test
	public void like() {
		Predicate predicate = createPredicate( new EntityQueryCondition( "name", LIKE, "%an%" ), descriptor );
		assertThat( predicate.test( item ) ).isTrue();

		predicate = createPredicate( new EntityQueryCondition( "name", LIKE, "%oh%" ), descriptor );
		assertThat( predicate.test( item ) ).isFalse();

		predicate = createPredicate( new EntityQueryCondition( "name", LIKE, "J%a%n%e" ), descriptor );
		assertThat( predicate.test( item ) ).isTrue();

		predicate = createPredicate( new EntityQueryCondition( "name", LIKE, "%J%n%" ), descriptor );
		assertThat( predicate.test( item ) ).isTrue();

		predicate = createPredicate( new EntityQueryCondition( "name", LIKE, "%J%%%n%%%" ), descriptor );
		assertThat( predicate.test( item ) ).isTrue();
	}

	@Test
	public void notLike() {
		Predicate predicate = createPredicate( new EntityQueryCondition( "name", NOT_LIKE, "%an%" ), descriptor );
		assertThat( predicate.test( item ) ).isFalse();

		predicate = createPredicate( new EntityQueryCondition( "name", NOT_LIKE, "%oh%" ), descriptor );
		assertThat( predicate.test( item ) ).isTrue();

		predicate = createPredicate( new EntityQueryCondition( "name", NOT_LIKE, "J%a%n%e" ), descriptor );
		assertThat( predicate.test( item ) ).isFalse();

		predicate = createPredicate( new EntityQueryCondition( "name", NOT_LIKE, "%J%n%" ), descriptor );
		assertThat( predicate.test( item ) ).isFalse();

		predicate = createPredicate( new EntityQueryCondition( "name", NOT_LIKE, "%J%%%n%%%" ), descriptor );
		assertThat( predicate.test( item ) ).isFalse();
	}

	@Test
	public void wildcardEscaped() {
		when( item.getPropertyValue( "text" ) ).thenReturn( "Last year 100%" );
		Predicate predicate = createPredicate( new EntityQueryCondition( "text", LIKE, "%100\\%%" ), descriptor );
		assertThat( predicate.test( item ) ).isTrue();

		predicate = createPredicate( new EntityQueryCondition( "text", LIKE, "%100\\%" ), descriptor );
		assertThat( predicate.test( item ) ).isTrue();

		predicate = createPredicate( new EntityQueryCondition( "name", LIKE, "J\\%n%" ), descriptor );
		assertThat( predicate.test( item ) ).isFalse();
	}

	@Test
	public void escapeEscapeCharacters() {
		when( item.getPropertyValue( "sarcasm" ) ).thenReturn( "Everybody loves regex \\s" );
		Predicate predicate = createPredicate( new EntityQueryCondition( "sarcasm", LIKE, "%loves regex \\s" ), descriptor );
		assertThat( predicate.test( item ) ).isTrue();
	}

	@Test
	public void likeCaseInsensitive() {
		Predicate predicate = createPredicate( new EntityQueryCondition( "name", LIKE_IC, "%aN%" ), descriptor );
		assertThat( predicate.test( item ) ).isTrue();

		predicate = createPredicate( new EntityQueryCondition( "name", LIKE_IC, "%Oh%" ), descriptor );
		assertThat( predicate.test( item ) ).isFalse();

		predicate = createPredicate( new EntityQueryCondition( "name", LIKE_IC, "j%A%n%E" ), descriptor );
		assertThat( predicate.test( item ) ).isTrue();

		predicate = createPredicate( new EntityQueryCondition( "name", LIKE_IC, "%J%%%N%%%" ), descriptor );
		assertThat( predicate.test( item ) ).isTrue();

		predicate = createPredicate( new EntityQueryCondition( "title", LIKE_IC, "fun wit\\%h dick and jane" ), descriptor );
		assertThat( predicate.test( item ) ).isTrue();

		predicate = createPredicate( new EntityQueryCondition( "title", LIKE_IC, "FUN wit\\%h dick and jane" ), descriptor );
		assertThat( predicate.test( item ) ).isTrue();

		predicate = createPredicate( new EntityQueryCondition( "title", LIKE_IC, "%WIT\\%h%" ), descriptor );
		assertThat( predicate.test( item ) ).isTrue();

		predicate = createPredicate( new EntityQueryCondition( "title", LIKE_IC, "fun%wit\\%h%jane" ), descriptor );
		assertThat( predicate.test( item ) ).isTrue();
	}

	@Test
	public void notLikeCaseInsensitive() {
		Predicate predicate = createPredicate( new EntityQueryCondition( "name", NOT_LIKE_IC, "%aN%" ), descriptor );
		assertThat( predicate.test( item ) ).isFalse();

		predicate = createPredicate( new EntityQueryCondition( "name", NOT_LIKE_IC, "%Oh%" ), descriptor );
		assertThat( predicate.test( item ) ).isTrue();

		predicate = createPredicate( new EntityQueryCondition( "name", NOT_LIKE_IC, "j%A%n%E" ), descriptor );
		assertThat( predicate.test( item ) ).isFalse();

		predicate = createPredicate( new EntityQueryCondition( "name", NOT_LIKE_IC, "%J%%%N%%%" ), descriptor );
		assertThat( predicate.test( item ) ).isFalse();

		predicate = createPredicate( new EntityQueryCondition( "title", NOT_LIKE_IC, "fun wit\\%h dick and jane" ), descriptor );
		assertThat( predicate.test( item ) ).isFalse();

		predicate = createPredicate( new EntityQueryCondition( "title", NOT_LIKE_IC, "FUN wit\\%h dick and jane" ), descriptor );
		assertThat( predicate.test( item ) ).isFalse();

		predicate = createPredicate( new EntityQueryCondition( "title", NOT_LIKE_IC, "%WIT\\%h%" ), descriptor );
		assertThat( predicate.test( item ) ).isFalse();

		predicate = createPredicate( new EntityQueryCondition( "title", NOT_LIKE_IC, "fun%wit\\%h%jane" ), descriptor );
		assertThat( predicate.test( item ) ).isFalse();
	}

}
