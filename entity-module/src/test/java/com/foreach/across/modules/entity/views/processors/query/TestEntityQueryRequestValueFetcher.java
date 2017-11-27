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

package com.foreach.across.modules.entity.views.processors.query;

import com.foreach.across.modules.entity.query.EntityQuery;
import com.foreach.across.modules.entity.query.EntityQueryCondition;
import com.foreach.across.modules.entity.query.EntityQueryOps;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import lombok.val;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collections;

import static com.foreach.across.modules.entity.query.EntityQueryOps.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

/**
 * @author Steven Gentens
 * @since 2.2.0
 */
@RunWith(MockitoJUnitRunner.class)
public class TestEntityQueryRequestValueFetcher
{
	private EntityQueryRequest entityQueryRequest = new EntityQueryRequest();

	@Mock
	private EntityPropertyDescriptor propertyDescriptor;

	@Before
	public void initMocks() {
		when( propertyDescriptor.getName() ).thenReturn( "myProperty" );
	}

	@Test
	public void notFilteredValueIfPropertyNotPresent() {
		val valueFetcher = fetcher( EQ, true );
		assertEquals( EntityQueryRequestValueFetcher.NOT_FILTERED, valueFetcher.getValue( entityQueryRequest ) );
		assertTrue( entityQueryRequest.isConvertibleToBasicMode() );
	}

	@Test
	public void notFilteredValueIfPropertyValueCouldNotBeDetermined() {
		val valueFetcher = fetcher( EQ, false );

		entityQueryRequest.setRawQuery( EntityQuery.parse( "myProperty = 123 and myProperty > 1000" ) );
		entityQueryRequest.setTranslatedRawQuery(
				EntityQuery.and(
						new EntityQueryCondition( "myProperty", EQ, 123 ),
						new EntityQueryCondition( "myProperty", GT, 1000 )
				)
		);

		assertEquals( EntityQueryRequestValueFetcher.NOT_FILTERED, valueFetcher.getValue( entityQueryRequest ) );
		assertFalse( entityQueryRequest.isConvertibleToBasicMode() );
	}

	@Test
	public void equalsConditionOnNumber() {
		val valueFetcher = fetcher( EQ, false );
		entityQueryRequest.setRawQuery( EntityQuery.parse( "myProperty = 123" ) );
		entityQueryRequest.setTranslatedRawQuery(
				EntityQuery.and( new EntityQueryCondition( "myProperty", EQ, 123 ) )
		);

		Object value = valueFetcher.getValue( entityQueryRequest );
		assertEquals( 123, value );
	}

	@Test
	public void equalsConditionOnText() {
		val valueFetcher = fetcher( EQ, false );
		entityQueryRequest.setRawQuery( EntityQuery.parse( "myProperty = '123'" ) );
		entityQueryRequest.setTranslatedRawQuery(
				EntityQuery.and( new EntityQueryCondition( "myProperty", EQ, "123" ) )
		);

		Object value = valueFetcher.getValue( entityQueryRequest );
		assertEquals( "123", value );
	}

	@Test
	public void containsConditionOnText() {
		val valueFetcher = fetcher( CONTAINS, false );
		entityQueryRequest.setRawQuery( EntityQuery.parse( "myProperty contains '%123'" ) );
		entityQueryRequest.setTranslatedRawQuery(
				EntityQuery.and( new EntityQueryCondition( "myProperty", LIKE, "%\\%123%" ) )
		);

		Object value = valueFetcher.getValue( entityQueryRequest );
		assertEquals( "%123", value );
	}

	@Test
	public void fallbackConditionOnText() {
		val valueFetcher = fetcher( CONTAINS, false );
		entityQueryRequest.setRawQuery( EntityQuery.parse( "myProperty contains '%123'" ) );
		entityQueryRequest.setTranslatedRawQuery(
				EntityQuery.and( new EntityQueryCondition( "name", LIKE, "%\\%123%" ) )
		);

		Object value = valueFetcher.getValue( entityQueryRequest );
		assertEquals( "%123", value );
	}

	@Test
	public void collectionValueWithoutValuesResultsInInconvertibleQuery() {
		val valueFetcher = fetcher( IN, true );
		entityQueryRequest.setRawQuery( EntityQuery.parse( "myProperty in (123)" ) );
		entityQueryRequest.setTranslatedRawQuery( EntityQuery.and( new EntityQueryCondition( "myProperty", IN ) ) );

		assertEquals( EntityQueryRequestValueFetcher.NOT_FILTERED, valueFetcher.getValue( entityQueryRequest ) );
		assertFalse( entityQueryRequest.isConvertibleToBasicMode() );
	}

	@Test
	public void collectionValuesAlwaysReturnsCollectionEvenIfSingleValue() {
		val valueFetcher = fetcher( IN, true );
		entityQueryRequest.setRawQuery( EntityQuery.parse( "myProperty in (123)" ) );
		entityQueryRequest.setTranslatedRawQuery(
				EntityQuery.and( new EntityQueryCondition( "myProperty", IN, 123 ) )
		);

		Object value = valueFetcher.getValue( entityQueryRequest );
		assertEquals( Collections.singletonList( 123 ), value );
	}

	@Test
	public void equalsConditionNullValue() {
		val valueFetcher = fetcher( IS_NULL, false );
		entityQueryRequest.setRawQuery( EntityQuery.parse( "myProperty is NULL" ) );
		entityQueryRequest.setTranslatedRawQuery(
				EntityQuery.and( new EntityQueryCondition( "myProperty", IS_NULL ) )
		);

		Object value = valueFetcher.getValue( entityQueryRequest );
		assertEquals( null, value );
	}

	@Test
	public void collectionValuesContainsNullReturnedAsFirstItem() {
		val valueFetcher = fetcher( IN, true );
		entityQueryRequest.setRawQuery( EntityQuery.parse( "myProperty in (123, NULL)" ) );
		entityQueryRequest.setTranslatedRawQuery(
				EntityQuery.and( new EntityQueryCondition( "myProperty", IN, 123 ) )
		);

		Object value = valueFetcher.getValue( entityQueryRequest );
		assertEquals( Arrays.asList( null, 123 ), value );
	}

	@Test
	public void propertyWithoutRawConditionsDoesNotMakeTheQueryInconvertibleToBasicMode() {
		val valueFetcher = fetcher( CONTAINS, false );
		entityQueryRequest.setRawQuery( EntityQuery.parse( "text contains 'john'" ) );
		entityQueryRequest.setTranslatedRawQuery(
				EntityQuery.and( EntityQuery.or( new EntityQueryCondition( "name", CONTAINS, "john" ),
				                                 new EntityQueryCondition( "note", CONTAINS, "john" )
				) )
		);

		Object value = valueFetcher.getValue( entityQueryRequest );
		assertTrue( entityQueryRequest.isConvertibleToBasicMode() );
		assertEquals( EntityQueryRequestValueFetcher.NOT_FILTERED, value );
	}

	private EntityQueryRequestValueFetcher fetcher( EntityQueryOps operand, boolean multiple ) {
		return new EntityQueryRequestValueFetcher( propertyDescriptor, operand, multiple );
	}

}
