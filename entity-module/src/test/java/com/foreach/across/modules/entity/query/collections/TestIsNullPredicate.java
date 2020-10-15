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

import java.awt.*;
import java.util.function.Predicate;

import static com.foreach.across.modules.entity.query.EntityQueryOps.IS_NOT_NULL;
import static com.foreach.across.modules.entity.query.EntityQueryOps.IS_NULL;
import static com.foreach.across.modules.entity.query.collections.CollectionEntityQueryPredicates.createPredicate;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * @author Steven Gentens
 * @since 3.1.0
 */
@ExtendWith(MockitoExtension.class)
@SuppressWarnings("unchecked")
public class TestIsNullPredicate
{
	private final Object ARGS = null;

	@Mock
	private EntityPropertyDescriptor descriptor;

	@Mock
	private CollectionEntityQueryItem item;

	@BeforeEach
	public void setUp() {
		when( item.getPropertyValue( "null" ) ).thenReturn( null );
		when( item.getPropertyValue( "notNullObject" ) ).thenReturn( Color.RED );
		when( item.getPropertyValue( "notNullCollection" ) ).thenReturn( Color.RED );
	}

	@Test
	public void isNull() {
		Predicate predicate = createPredicate( new EntityQueryCondition( "null", IS_NULL, ARGS ), descriptor );
		assertThat( predicate.test( item ) ).isTrue();

		predicate = createPredicate( new EntityQueryCondition( "notNullObject", IS_NULL, ARGS ), descriptor );
		assertThat( predicate.test( item ) ).isFalse();

		predicate = createPredicate( new EntityQueryCondition( "notNullCollection", IS_NULL, ARGS ), descriptor );
		assertThat( predicate.test( item ) ).isFalse();
	}

	@Test
	public void isNotNull() {
		Predicate predicate = createPredicate( new EntityQueryCondition( "null", IS_NOT_NULL, ARGS ), descriptor );
		assertThat( predicate.test( item ) ).isFalse();

		predicate = createPredicate( new EntityQueryCondition( "notNullObject", IS_NOT_NULL, ARGS ), descriptor );
		assertThat( predicate.test( item ) ).isTrue();

		predicate = createPredicate( new EntityQueryCondition( "notNullCollection", IS_NOT_NULL, ARGS ), descriptor );
		assertThat( predicate.test( item ) ).isTrue();
	}
}
