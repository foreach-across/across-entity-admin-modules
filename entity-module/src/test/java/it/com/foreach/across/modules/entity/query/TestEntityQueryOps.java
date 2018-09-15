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

package it.com.foreach.across.modules.entity.query;

import com.foreach.across.modules.entity.query.EntityQueryOps;
import org.junit.Test;

import static com.foreach.across.modules.entity.query.EntityQueryOps.*;
import static org.junit.Assert.*;

/**
 * @author Arne Vandamme
 * @since 2.0.0
 */
public class TestEntityQueryOps
{
	@Test
	public void isTokenReturnsNullByDefault() {
		assertEquals( IS_NULL, EntityQueryOps.forToken( "is" ) );
		assertEquals( IS_NOT_NULL, EntityQueryOps.forToken( "is not" ) );
	}

	@Test
	public void characterEscaping() {
		assertEquals( "name = 'my \\' name'", EQ.toString( "name", "my ' name" ) );
		assertEquals( "name != 'my \\\\ name'", NEQ.toString( "name", "my \\ name" ) );
	}

	@Test
	public void multiValueEquivalents() {
		assertEquals( IN, EntityQueryOps.resolveMultiValueOperand( EQ ) );
		assertEquals( NOT_IN, EntityQueryOps.resolveMultiValueOperand( NEQ ) );
		assertEquals( CONTAINS, EntityQueryOps.resolveMultiValueOperand( CONTAINS ) );
		assertEquals( NOT_CONTAINS, EntityQueryOps.resolveMultiValueOperand( NOT_CONTAINS ) );
		assertEquals( IN, EntityQueryOps.resolveMultiValueOperand( IN ) );
		assertEquals( NOT_IN, EntityQueryOps.resolveMultiValueOperand( NOT_IN ) );

		assertNull( EntityQueryOps.resolveMultiValueOperand( LIKE ) );
		assertNull( EntityQueryOps.resolveMultiValueOperand( AND ) );
		assertNull( EntityQueryOps.resolveMultiValueOperand( GE ) );
	}

	@Test
	public void negation() {
		assertFalse( EQ.isNegation() );
		assertTrue( NEQ.isNegation() );
		assertFalse( CONTAINS.isNegation() );
		assertTrue( NOT_CONTAINS.isNegation() );
		assertFalse( IN.isNegation() );
		assertTrue( NOT_IN.isNegation() );
		assertFalse( LIKE.isNegation() );
		assertTrue( NOT_LIKE.isNegation() );
		assertFalse( LIKE_IC.isNegation() );
		assertTrue( NOT_LIKE_IC.isNegation() );
		assertFalse( IS_NULL.isNegation() );
		assertTrue( IS_NOT_NULL.isNegation() );
		assertFalse( IS_EMPTY.isNegation() );
		assertTrue( IS_NOT_EMPTY.isNegation() );
		assertFalse( GT.isNegation() );
		assertFalse( GE.isNegation() );
		assertFalse( LT.isNegation() );
		assertFalse( LE.isNegation() );
	}

	@Test
	public void reverseOperands() {
		assertReverse( AND, OR );
		assertReverse( EQ, NEQ );
		assertReverse( CONTAINS, NOT_CONTAINS );
		assertReverse( IN, NOT_IN );
		assertReverse( LIKE, NOT_LIKE );
		assertReverse( LIKE_IC, NOT_LIKE_IC );
		assertReverse( IS_NULL, IS_NOT_NULL );
		assertReverse( IS_EMPTY, IS_NOT_EMPTY );
		assertReverse( GT, LT );
		assertReverse( GE, LE );
	}

	private void assertReverse( EntityQueryOps initial, EntityQueryOps reversed ) {
		assertEquals( reversed, initial.reverse() );
		assertEquals( initial, reversed.reverse() );
	}
}
