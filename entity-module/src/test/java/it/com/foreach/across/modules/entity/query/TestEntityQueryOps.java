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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @author Arne Vandamme
 * @since 2.0.0
 */
public class TestEntityQueryOps
{
	@Test
	public void isTokenReturnsNullByDefault() {
		assertEquals( EntityQueryOps.IS_NULL, EntityQueryOps.forToken( "is" ) );
		assertEquals( EntityQueryOps.IS_NOT_NULL, EntityQueryOps.forToken( "is not" ) );
	}

	@Test
	public void characterEscaping() {
		assertEquals( "name = 'my \\' name'", EQ.toString( "name", "my ' name" ) );
		assertEquals( "name != 'my \\\\ name'", EntityQueryOps.NEQ.toString( "name", "my \\ name" ) );
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
}
