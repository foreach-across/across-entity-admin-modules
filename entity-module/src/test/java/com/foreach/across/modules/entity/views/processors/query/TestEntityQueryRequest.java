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

import com.foreach.across.modules.entity.query.*;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static com.foreach.across.modules.entity.query.EntityQueryOps.*;
import static org.junit.Assert.*;

/**
 * @author Steven Gentens
 * @since 2.2.0
 */
public class TestEntityQueryRequest
{
	private EntityQueryRequest entityQueryRequest = new EntityQueryRequest();

	@Test
	public void queryCanBeConvertedToBasicByDefault() {
		assertTrue( entityQueryRequest.isConvertibleToBasicMode() );
	}

	@Test
	public void simpleRawQuery() {
		entityQueryRequest.setRawQuery( EntityQuery.parse( "id = 10" ) );
		EntityQueryRequestProperty property = entityQueryRequest.getSelectedProperty( "id" );
		assertNotNull( property );
		assertEquals( 1, property.getRawConditionCount() );
		assertEquals(
				Collections.singletonList( new EntityQueryCondition( "id", EntityQueryOps.EQ, new EQValue( "10" ) ) ),
				property.getRawConditions()
		);
		assertEquals( Collections.singletonList( new EQValue( "10" ) ), property.getRawValues() );
		assertTrue( property.getTranslatedConditions().isEmpty() );
		assertTrue( property.getTranslatedValues().isEmpty() );
		assertTrue( property.hasSingleRawValue() );
		assertEquals( new EQValue( "10" ), property.getSingleRawValue() );
		assertFalse( property.hasNullValue() );
		assertTrue( entityQueryRequest.isConvertibleToBasicMode() );
	}

	@Test
	public void complexRawQuery() {
		entityQueryRequest.setRawQuery( EntityQuery.parse( "id = 10 and (name like 'test' and id < 5)" ) );
		EntityQueryRequestProperty name = entityQueryRequest.getSelectedProperty( "name" );
		assertNotNull( name );
		assertEquals( 1, name.getRawConditionCount() );
		assertEquals(
				Collections.singletonList( new EntityQueryCondition( "name", EntityQueryOps.LIKE, new EQString( "test" ) ) ),
				name.getRawConditions()
		);
		assertEquals( Collections.singletonList( new EQString( "test" ) ), name.getRawValues() );
		assertTrue( name.hasSingleRawValue() );
		assertEquals( new EQString( "test" ), name.getSingleRawValue() );
		assertFalse( name.hasNullValue() );

		EntityQueryRequestProperty id = entityQueryRequest.getSelectedProperty( "id" );
		assertNotNull( id );
		assertEquals( 2, id.getRawConditionCount() );
		assertEquals(
				Arrays.asList(
						new EntityQueryCondition( "id", EntityQueryOps.EQ, new EQValue( "10" ) ),
						new EntityQueryCondition( "id", EntityQueryOps.LT, new EQValue( "5" ) )
				),
				id.getRawConditions()
		);
		assertEquals( Arrays.asList( new EQValue( "10" ), new EQValue( "5" ) ), id.getRawValues() );
		assertFalse( id.hasSingleRawValue() );
		assertNull( id.getSingleRawValue() );
		assertFalse( id.hasNullValue() );
	}

	@Test
	public void rawQueryWithGroup() {
		entityQueryRequest.setRawQuery( EntityQuery.parse( "group in (1,NULL,15)" ) );
		EntityQueryRequestProperty group = entityQueryRequest.getSelectedProperty( "group" );
		assertNotNull( group );
		assertEquals( 1, group.getRawConditionCount() );
		EQGroup value = new EQGroup( new EQValue( "1" ), EQValue.NULL, new EQValue( "15" ) );
		assertEquals(
				Collections.singletonList(
						new EntityQueryCondition( "group", EntityQueryOps.IN,
						                          value
						)
				),
				group.getRawConditions()
		);
		assertEquals( Collections.singletonList( value ), group.getRawValues() );
		assertTrue( group.hasSingleRawValue() );
		assertEquals( value, group.getSingleRawValue() );
		assertTrue( group.hasNullValue() );
		assertTrue( entityQueryRequest.isConvertibleToBasicMode() );
	}

	@Test
	public void simpleTranslatedQuery() {
		entityQueryRequest.setTranslatedRawQuery( EntityQuery.and( new EntityQueryCondition( "id", EntityQueryOps.EQ, 10 ) ) );
		EntityQueryRequestProperty property = entityQueryRequest.getSelectedProperty( "id" );
		assertNotNull( property );
		assertEquals( 0, property.getRawConditionCount() );
		assertEquals( 1, property.getTranslatedConditionCount() );
		assertEquals(
				Collections.singletonList( new EntityQueryCondition( "id", EntityQueryOps.EQ, 10 ) ),
				property.getTranslatedConditions()
		);
		assertEquals( Collections.singletonList( 10 ), property.getTranslatedValues() );
		assertTrue( property.getRawConditions().isEmpty() );
		assertTrue( property.getRawValues().isEmpty() );
		assertFalse( property.hasSingleRawValue() );
		assertNull( property.getSingleRawValue() );
		assertFalse( property.hasNullValue() );
	}

	@Test
	public void combineRawAndTranslatedQuery() {
		entityQueryRequest.setRawQuery( EntityQuery.parse( "id = 10 and (name like 'test')" ) );
		entityQueryRequest.setTranslatedRawQuery(
				EntityQuery.and(
						new EntityQueryCondition( "id", EntityQueryOps.LT, 5 ),
						new EntityQueryCondition( "group", EntityQueryOps.IN, "one", 2 )
				)
		);
		assertEquals( 3, entityQueryRequest.getSelectedProperties().size() );

		EntityQueryRequestProperty name = entityQueryRequest.getSelectedProperty( "name" );
		assertNotNull( name );
		assertEquals( 1, name.getRawConditionCount() );
		assertEquals(
				Collections.singletonList( new EntityQueryCondition( "name", EntityQueryOps.LIKE, new EQString( "test" ) ) ),
				name.getRawConditions()
		);
		assertEquals( Collections.singletonList( new EQString( "test" ) ), name.getRawValues() );
		assertTrue( name.hasSingleRawValue() );
		assertEquals( new EQString( "test" ), name.getSingleRawValue() );
		assertFalse( name.hasNullValue() );
		assertFalse( name.hasSingleTranslatedValue() );
		assertNull( name.getSingleTranslatedValue() );

		EntityQueryRequestProperty group = entityQueryRequest.getSelectedProperty( "group" );
		assertNotNull( group );
		assertEquals( 0, group.getRawConditionCount() );
		assertEquals( 1, group.getTranslatedConditionCount() );
		assertEquals(
				Collections.singletonList( new EntityQueryCondition( "group", EntityQueryOps.IN, "one", 2 ) ),
				group.getTranslatedConditions()
		);
		assertEquals( Arrays.asList( "one", 2 ), group.getTranslatedValues() );
		assertTrue( group.getRawConditions().isEmpty() );
		assertTrue( group.getRawValues().isEmpty() );
		assertFalse( group.hasSingleRawValue() );
		assertNull( group.getSingleRawValue() );
		assertFalse( group.hasSingleTranslatedValue() );
		assertNull( group.getSingleTranslatedValue() );
		assertFalse( group.hasNullValue() );

		EntityQueryRequestProperty id = entityQueryRequest.getSelectedProperty( "id" );
		assertNotNull( id );
		assertEquals( 1, id.getRawConditionCount() );
		assertEquals( 1, id.getTranslatedConditionCount() );
		assertEquals(
				Collections.singletonList( new EntityQueryCondition( "id", EntityQueryOps.EQ, new EQValue( "10" ) ) ),
				id.getRawConditions()
		);
		assertEquals(
				Collections.singletonList( new EntityQueryCondition( "id", EntityQueryOps.LT, 5 ) ),
				id.getTranslatedConditions()
		);
		assertEquals( Collections.singletonList( new EQValue( "10" ) ), id.getRawValues() );
		assertEquals( Collections.singletonList( 5 ), id.getTranslatedValues() );
		assertTrue( id.hasSingleRawValue() );
		assertTrue( id.hasSingleTranslatedValue() );
		assertEquals( new EQValue( "10" ), id.getSingleRawValue() );
		assertEquals( 5, id.getSingleTranslatedValue() );
		assertFalse( id.hasNullValue() );
		assertTrue( entityQueryRequest.isConvertibleToBasicMode() );
	}

	@Test
	public void isSingleConditionWithOperand() {
		entityQueryRequest.setRawQuery( EntityQuery.parse( "id = 10 and (name like 'test' and id < 5)" ) );
		assertFalse( entityQueryRequest.getSelectedProperty( "id" ).isSingleConditionWithOperand( EntityQueryOps.EQ ) );
		assertFalse( entityQueryRequest.getSelectedProperty( "id" ).isSingleConditionWithOperand( EntityQueryOps.LT ) );
		assertFalse( entityQueryRequest.getSelectedProperty( "name" ).isSingleConditionWithOperand( EntityQueryOps.EQ ) );
		assertTrue( entityQueryRequest.getSelectedProperty( "name" ).isSingleConditionWithOperand( EntityQueryOps.LIKE ) );
	}

	@Test
	public void subQueryWithOrOperandResultsInNotConvertibleToBasicMode() {
		entityQueryRequest.setRawQuery( EntityQuery.parse( "id = 5 or name = 'john'" ) );
		EntityQueryRequestProperty property = entityQueryRequest.getSelectedProperty( "id" );
		assertNotNull( property );
		assertEquals( 1, property.getRawConditionCount() );
		assertEquals(
				Collections.singletonList( new EntityQueryCondition( "id", EntityQueryOps.EQ, new EQValue( "5" ) ) ),
				property.getRawConditions()
		);
		assertEquals( Collections.singletonList( new EQValue( "5" ) ), property.getRawValues() );
		assertTrue( property.getTranslatedConditions().isEmpty() );
		assertTrue( property.getTranslatedValues().isEmpty() );
		assertTrue( property.hasSingleRawValue() );
		assertEquals( new EQValue( "5" ), property.getSingleRawValue() );
		assertFalse( property.hasNullValue() );

		property = entityQueryRequest.getSelectedProperty( "name" );
		assertNotNull( property );
		assertEquals( 1, property.getRawConditionCount() );
		assertEquals(
				Collections.singletonList( new EntityQueryCondition( "name", EntityQueryOps.EQ, new EQString( "john" ) ) ),
				property.getRawConditions()
		);
		assertEquals( Collections.singletonList( new EQString( "john" ) ), property.getRawValues() );
		assertTrue( property.getTranslatedConditions().isEmpty() );
		assertTrue( property.getTranslatedValues().isEmpty() );
		assertTrue( property.hasSingleRawValue() );
		assertEquals( new EQString( "john" ), property.getSingleRawValue() );
		assertFalse( property.hasNullValue() );

		assertFalse( entityQueryRequest.isConvertibleToBasicMode() );
	}

	@Test
	public void queryContainingOrOperandShouldBeInconvertible() {
		entityQueryRequest.setRawQuery( EntityQuery.parse( "id = 5 or name = 'john'" ) );
		assertFalse( entityQueryRequest.isConvertibleToBasicMode() );
	}

	@Test
	public void innerQueryContainingOrOperandShouldBeIncovertible() {
		entityQueryRequest.setRawQuery( EntityQuery.parse( "id = 5 and (name like 'john' or name like 'alfred')" ) );
		assertFalse( entityQueryRequest.isConvertibleToBasicMode() );
	}

	@Test
	public void eqCanConvertToIn() {
		entityQueryRequest.setRawQuery( EntityQuery.parse( "name = 'john'" ) );
		assertTrue( entityQueryRequest.getSelectedProperty( "name" ).isSingleConditionWithConvertibleOperand( IN ) );
	}

	@Test
	public void isNullCanConvertToIn() {
		entityQueryRequest.setRawQuery( EntityQuery.parse( "name is null" ) );
		assertTrue( entityQueryRequest.getSelectedProperty( "name" ).isSingleConditionWithConvertibleOperand( IN ) );
	}

	@Test
	public void likeCanConvertToIn() {
		entityQueryRequest.setRawQuery( EntityQuery.parse( "name like 'john'" ) );
		assertTrue( entityQueryRequest.getSelectedProperty( "name" ).isSingleConditionWithConvertibleOperand( IN ) );

		entityQueryRequest = new EntityQueryRequest();
		entityQueryRequest.setRawQuery( EntityQuery.parse( "name like 'john%'" ) );
		assertFalse( entityQueryRequest.getSelectedProperty( "name" ).isSingleConditionWithConvertibleOperand( IN ) );
	}

	@Test
	public void isEmptyCanConvertToIn() {
		entityQueryRequest.setRawQuery( EntityQuery.parse( "name is empty" ) );
		assertTrue( entityQueryRequest.getSelectedProperty( "name" ).isSingleConditionWithConvertibleOperand( IN ) );
	}

	@Test
	public void isNotNullCanConvertToNotIn() {
		entityQueryRequest.setRawQuery( EntityQuery.parse( "name is not null" ) );
		assertTrue( entityQueryRequest.getSelectedProperty( "name" ).isSingleConditionWithConvertibleOperand( NOT_IN ) );
	}

	@Test
	public void isNotEmptyCanConvertToNotIn() {
		entityQueryRequest.setRawQuery( EntityQuery.parse( "name is not empty" ) );
		assertTrue( entityQueryRequest.getSelectedProperty( "name" ).isSingleConditionWithConvertibleOperand( NOT_IN ) );
	}

	@Test
	public void neqCanConvertToNotIn() {
		entityQueryRequest.setRawQuery( EntityQuery.parse( "name != 'john'" ) );
		assertTrue( entityQueryRequest.getSelectedProperty( "name" ).isSingleConditionWithConvertibleOperand( NOT_IN ) );
	}

	@Test
	public void notLikeCanConvertToNotIn() {
		entityQueryRequest.setRawQuery( EntityQuery.parse( "name not like 'john'" ) );
		assertTrue( entityQueryRequest.getSelectedProperty( "name" ).isSingleConditionWithConvertibleOperand( NOT_IN ) );

		entityQueryRequest = new EntityQueryRequest();
		entityQueryRequest.setRawQuery( EntityQuery.parse( "name not like 'john%'" ) );
		assertFalse( entityQueryRequest.getSelectedProperty( "name" ).isSingleConditionWithConvertibleOperand( NOT_IN ) );
	}

	@Test
	public void likeCanConvertToContains() {
		entityQueryRequest.setRawQuery( EntityQuery.parse( "name like 'john%'" ) );
		assertFalse( entityQueryRequest.getSelectedProperty( "name" ).isSingleConditionWithConvertibleOperand( CONTAINS ) );

		entityQueryRequest = new EntityQueryRequest();
		entityQueryRequest.setRawQuery( EntityQuery.parse( "name like '%john%'" ) );
		assertTrue( entityQueryRequest.getSelectedProperty( "name" ).isSingleConditionWithConvertibleOperand( CONTAINS ) );
	}

	@Test
	public void notLikeCanConvertToNotContains() {
		entityQueryRequest.setRawQuery( EntityQuery.parse( "name not like 'john%'" ) );
		assertFalse( entityQueryRequest.getSelectedProperty( "name" ).isSingleConditionWithConvertibleOperand( NOT_CONTAINS ) );

		entityQueryRequest = new EntityQueryRequest();
		entityQueryRequest.setRawQuery( EntityQuery.parse( "name not like '%john%'" ) );
		assertTrue( entityQueryRequest.getSelectedProperty( "name" ).isSingleConditionWithConvertibleOperand( NOT_CONTAINS ) );
	}

	@Test
	public void likeCanConvertToEq() {
		entityQueryRequest.setRawQuery( EntityQuery.parse( "name like 'john%'" ) );
		assertFalse( entityQueryRequest.getSelectedProperty( "name" ).isSingleConditionWithConvertibleOperand( EQ ) );

		entityQueryRequest = new EntityQueryRequest();
		entityQueryRequest.setRawQuery( EntityQuery.parse( "name like 'john'" ) );
		assertTrue( entityQueryRequest.getSelectedProperty( "name" ).isSingleConditionWithConvertibleOperand( EQ ) );
	}

	@Test
	public void isNullCanConvertToEq() {
		entityQueryRequest.setRawQuery( EntityQuery.parse( "name is null" ) );
		assertTrue( entityQueryRequest.getSelectedProperty( "name" ).isSingleConditionWithConvertibleOperand( EQ ) );
	}

	@Test
	public void notLikeCanConvertToNeq() {
		entityQueryRequest.setRawQuery( EntityQuery.parse( "name not like 'john%'" ) );
		assertFalse( entityQueryRequest.getSelectedProperty( "name" ).isSingleConditionWithConvertibleOperand( NEQ ) );

		entityQueryRequest = new EntityQueryRequest();
		entityQueryRequest.setRawQuery( EntityQuery.parse( "name not like 'john'" ) );
		assertTrue( entityQueryRequest.getSelectedProperty( "name" ).isSingleConditionWithConvertibleOperand( NEQ ) );
	}

	@Test
	public void isNotNullCanConvertToNeq() {
		entityQueryRequest.setRawQuery( EntityQuery.parse( "name is not null" ) );
		assertTrue( entityQueryRequest.getSelectedProperty( "name" ).isSingleConditionWithConvertibleOperand( NEQ ) );
	}

	@Test
	public void eqCanConvertToLike() {
		entityQueryRequest.setRawQuery( EntityQuery.parse( "name = 5" ) );
		assertFalse( entityQueryRequest.getSelectedProperty( "name" ).isSingleConditionWithConvertibleOperand( LIKE ) );

		entityQueryRequest = new EntityQueryRequest();
		entityQueryRequest.setRawQuery( EntityQuery.parse( "name = 'jo%hn'" ) );
		assertFalse( entityQueryRequest.getSelectedProperty( "name" ).isSingleConditionWithConvertibleOperand( LIKE ) );

		entityQueryRequest = new EntityQueryRequest();
		entityQueryRequest.setRawQuery( EntityQuery.parse( "name = 'john'" ) );
		assertTrue( entityQueryRequest.getSelectedProperty( "name" ).isSingleConditionWithConvertibleOperand( LIKE ) );
	}

	@Test
	public void neqCanConvertToNotLike() {
		entityQueryRequest.setRawQuery( EntityQuery.parse( "name != 5" ) );
		assertFalse( entityQueryRequest.getSelectedProperty( "name" ).isSingleConditionWithConvertibleOperand( NOT_LIKE ) );

		entityQueryRequest = new EntityQueryRequest();
		entityQueryRequest.setRawQuery( EntityQuery.parse( "name != 'jo%hn'" ) );
		assertFalse( entityQueryRequest.getSelectedProperty( "name" ).isSingleConditionWithConvertibleOperand( NOT_LIKE ) );

		entityQueryRequest = new EntityQueryRequest();
		entityQueryRequest.setRawQuery( EntityQuery.parse( "name != 'john'" ) );
		assertTrue( entityQueryRequest.getSelectedProperty( "name" ).isSingleConditionWithConvertibleOperand( NOT_LIKE ) );
	}

	@Test
	public void eqCanConvertToGe() {
		entityQueryRequest.setRawQuery( EntityQuery.parse( "number = 5" ) );
		assertTrue( entityQueryRequest.getSelectedProperty( "number" ).isSingleConditionWithConvertibleOperand( GE ) );
	}

	@Test
	public void eqCanConvertToLe() {
		entityQueryRequest.setRawQuery( EntityQuery.parse( "number = 5" ) );
		assertTrue( entityQueryRequest.getSelectedProperty( "number" ).isSingleConditionWithConvertibleOperand( LE ) );
	}

}
