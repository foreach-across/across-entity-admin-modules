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

package com.foreach.across.modules.entity.query.support;

import com.foreach.across.modules.entity.query.*;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

/**
 * @author Arne Vandamme
 * @since 3.2.0
 */
public class TestInEntityQueryConditionTranslator
{
	private EntityQueryConditionTranslator translator = InEntityQueryConditionTranslator.INSTANCE;

	@Test
	public void nonInIsLeftUnmodified() {
		EntityQueryCondition condition = new EntityQueryCondition( "test", EntityQueryOps.CONTAINS, 1 );
		assertSame( condition, translator.translate( condition ) );
	}

	@Test
	public void nonNullsAreNotModified() {
		assertEquals(
				new EntityQueryCondition( "id", EntityQueryOps.IN, 1 ),
				translator.translate( new EntityQueryCondition( "id", EntityQueryOps.IN, 1 ) )
		);
		assertEquals(
				new EntityQueryCondition( "id", EntityQueryOps.NOT_IN, 1 ),
				translator.translate( new EntityQueryCondition( "id", EntityQueryOps.NOT_IN, 1 ) )
		);
		assertEquals(
				new EntityQueryCondition( "id", EntityQueryOps.IN, 1, 2 ),
				translator.translate( new EntityQueryCondition( "id", EntityQueryOps.IN, 1, 2 ) )
		);
		assertEquals(
				new EntityQueryCondition( "id", EntityQueryOps.NOT_IN, 1, 2 ),
				translator.translate( new EntityQueryCondition( "id", EntityQueryOps.NOT_IN, 1, 2 ) )
		);
		assertEquals(
				new EntityQueryCondition( "id", EntityQueryOps.IN, new EQGroup( new EQValue( "1" ), new EQValue( "2" ) ) ),
				translator.translate( new EntityQueryCondition( "id", EntityQueryOps.IN, new EQGroup( new EQValue( "1" ), new EQValue( "2" ) ) ) )
		);
		assertEquals(
				new EntityQueryCondition( "id", EntityQueryOps.NOT_IN, new EQGroup( new EQValue( "1" ), new EQValue( "2" ) ) ),
				translator.translate( new EntityQueryCondition( "id", EntityQueryOps.NOT_IN, new EQGroup( new EQValue( "1" ), new EQValue( "2" ) ) ) )
		);
	}

	@Test
	public void singleNullIsConvertedToIsNull() {
		assertEquals(
				new EntityQueryCondition( "id", EntityQueryOps.IS_NULL ),
				translator.translate( new EntityQueryCondition( "id", EntityQueryOps.IN, EQValue.NULL ) )
		);
		assertEquals(
				new EntityQueryCondition( "id", EntityQueryOps.IS_NULL ),
				translator.translate( new EntityQueryCondition( "id", EntityQueryOps.IN, new Object[] { null } ) )
		);
		assertEquals(
				new EntityQueryCondition( "id", EntityQueryOps.IS_NOT_NULL ),
				translator.translate( new EntityQueryCondition( "id", EntityQueryOps.NOT_IN, EQValue.NULL ) )
		);
		assertEquals(
				new EntityQueryCondition( "id", EntityQueryOps.IS_NOT_NULL ),
				translator.translate( new EntityQueryCondition( "id", EntityQueryOps.NOT_IN, new Object[] { null } ) )
		);
	}

	@Test
	public void nullValueAddsIsNullPredicate() {
		assertEquals(
				EntityQuery.or(
						new EntityQueryCondition( "id", EntityQueryOps.IN, 1, 2 ),
						new EntityQueryCondition( "id", EntityQueryOps.IS_NULL )
				),
				translator.translate( new EntityQueryCondition( "id", EntityQueryOps.IN, 1, 2, null ) )
		);
		assertEquals(
				EntityQuery.and(
						new EntityQueryCondition( "id", EntityQueryOps.NOT_IN, 1, 2 ),
						new EntityQueryCondition( "id", EntityQueryOps.IS_NOT_NULL )
				),
				translator.translate( new EntityQueryCondition( "id", EntityQueryOps.NOT_IN, 1, 2, null ) )
		);
		assertEquals(
				EntityQuery.or(
						new EntityQueryCondition( "id", EntityQueryOps.IN, new EQGroup( new EQValue( "1" ), new EQValue( "2" ) ) ),
						new EntityQueryCondition( "id", EntityQueryOps.IS_NULL )
				),
				translator.translate( new EntityQueryCondition( "id", EntityQueryOps.IN,
				                                                new EQGroup( new EQValue( "1" ), new EQValue( "2" ), EQValue.NULL ) ) )
		);
		assertEquals(
				EntityQuery.and(
						new EntityQueryCondition( "id", EntityQueryOps.NOT_IN, new EQGroup( new EQValue( "1" ), new EQValue( "2" ) ) ),
						new EntityQueryCondition( "id", EntityQueryOps.IS_NOT_NULL )

				),
				translator.translate( new EntityQueryCondition( "id", EntityQueryOps.NOT_IN,
				                                                new EQGroup( new EQValue( "1" ), new EQValue( "2" ), EQValue.NULL ) ) )
		);
	}
}
