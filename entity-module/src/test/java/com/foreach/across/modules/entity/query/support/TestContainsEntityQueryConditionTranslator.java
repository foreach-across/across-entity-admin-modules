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
public class TestContainsEntityQueryConditionTranslator
{
	private EntityQueryConditionTranslator translator = ContainsEntityQueryConditionTranslator.INSTANCE;

	@Test
	public void nonContainsIsLeftUnmodified() {
		EntityQueryCondition condition = new EntityQueryCondition( "test", EntityQueryOps.IN, 1 );
		assertSame( condition, translator.translate( condition ) );
	}

	@Test
	public void singleNonNullIsNotModified() {
		assertEquals(
				new EntityQueryCondition( "ids", EntityQueryOps.CONTAINS, 1 ),
				translator.translate( new EntityQueryCondition( "ids", EntityQueryOps.CONTAINS, 1 ) )
		);
		assertEquals(
				new EntityQueryCondition( "ids", EntityQueryOps.NOT_CONTAINS, 1 ),
				translator.translate( new EntityQueryCondition( "ids", EntityQueryOps.NOT_CONTAINS, 1 ) )
		);
	}

	@Test
	public void singleNonNullGroupIsUnboxed() {
		assertEquals(
				new EntityQueryCondition( "ids", EntityQueryOps.CONTAINS, new EQValue( "1" ) ),
				translator.translate( new EntityQueryCondition( "ids", EntityQueryOps.CONTAINS, new EQGroup( new EQValue( "1" ) ) ) )
		);
		assertEquals(
				new EntityQueryCondition( "ids", EntityQueryOps.NOT_CONTAINS, new EQValue( "1" ) ),
				translator.translate( new EntityQueryCondition( "ids", EntityQueryOps.NOT_CONTAINS, new EQGroup( new EQValue( "1" ) ) ) )
		);
	}

	@Test
	public void singleNullIsConvertedToEmpty() {
		assertEquals(
				new EntityQueryCondition( "ids", EntityQueryOps.IS_EMPTY ),
				translator.translate( new EntityQueryCondition( "ids", EntityQueryOps.CONTAINS, EQValue.NULL ) )
		);
		assertEquals(
				new EntityQueryCondition( "ids", EntityQueryOps.IS_EMPTY ),
				translator.translate( new EntityQueryCondition( "ids", EntityQueryOps.CONTAINS, new Object[] { null } ) )
		);
		assertEquals(
				new EntityQueryCondition( "ids", EntityQueryOps.IS_NOT_EMPTY ),
				translator.translate( new EntityQueryCondition( "ids", EntityQueryOps.NOT_CONTAINS, EQValue.NULL ) )
		);
		assertEquals(
				new EntityQueryCondition( "ids", EntityQueryOps.IS_NOT_EMPTY ),
				translator.translate( new EntityQueryCondition( "ids", EntityQueryOps.NOT_CONTAINS, new Object[] { null } ) )
		);
	}

	@Test
	public void multipleNonNullAreExpanded() {
		assertEquals(
				EntityQuery.or(
						new EntityQueryCondition( "ids", EntityQueryOps.CONTAINS, 1 ),
						new EntityQueryCondition( "ids", EntityQueryOps.CONTAINS, 2 )
				),
				translator.translate( new EntityQueryCondition( "ids", EntityQueryOps.CONTAINS, 1, 2 ) )
		);
		assertEquals(
				EntityQuery.and(
						new EntityQueryCondition( "ids", EntityQueryOps.NOT_CONTAINS, 1 ),
						new EntityQueryCondition( "ids", EntityQueryOps.NOT_CONTAINS, 2 )
				),
				translator.translate( new EntityQueryCondition( "ids", EntityQueryOps.NOT_CONTAINS, 1, 2 ) )
		);
		assertEquals(
				EntityQuery.or(
						new EntityQueryCondition( "ids", EntityQueryOps.CONTAINS, new EQValue( "1" ) ),
						new EntityQueryCondition( "ids", EntityQueryOps.CONTAINS, new EQValue( "2" ) )
				),
				translator.translate( new EntityQueryCondition( "ids", EntityQueryOps.CONTAINS,
				                                                new EQGroup( new EQValue( "1" ), new EQValue( "2" ) ) ) )
		);
		assertEquals(
				EntityQuery.and(
						new EntityQueryCondition( "ids", EntityQueryOps.NOT_CONTAINS, new EQValue( "1" ) ),
						new EntityQueryCondition( "ids", EntityQueryOps.NOT_CONTAINS, new EQValue( "2" ) )
				),
				translator.translate( new EntityQueryCondition( "ids", EntityQueryOps.NOT_CONTAINS,
				                                                new EQGroup( new EQValue( "1" ), new EQValue( "2" ) ) ) )
		);
	}

	@Test
	public void nullValueAddsEmptyPredicateAlongWithExpansion() {
		assertEquals(
				EntityQuery.or(
						new EntityQueryCondition( "ids", EntityQueryOps.CONTAINS, 1 ),
						new EntityQueryCondition( "ids", EntityQueryOps.CONTAINS, 2 ),
						new EntityQueryCondition( "ids", EntityQueryOps.IS_EMPTY )
				),
				translator.translate( new EntityQueryCondition( "ids", EntityQueryOps.CONTAINS, 1, 2, null ) )
		);
		assertEquals(
				EntityQuery.and(
						new EntityQueryCondition( "ids", EntityQueryOps.NOT_CONTAINS, 1 ),
						new EntityQueryCondition( "ids", EntityQueryOps.NOT_CONTAINS, 2 ),
						new EntityQueryCondition( "ids", EntityQueryOps.IS_NOT_EMPTY )
				),
				translator.translate( new EntityQueryCondition( "ids", EntityQueryOps.NOT_CONTAINS, 1, 2, null ) )
		);
		assertEquals(
				EntityQuery.or(
						new EntityQueryCondition( "ids", EntityQueryOps.CONTAINS, new EQValue( "1" ) ),
						new EntityQueryCondition( "ids", EntityQueryOps.CONTAINS, new EQValue( "2" ) ),
						new EntityQueryCondition( "ids", EntityQueryOps.IS_EMPTY )
				),
				translator.translate( new EntityQueryCondition( "ids", EntityQueryOps.CONTAINS,
				                                                new EQGroup( new EQValue( "1" ), new EQValue( "2" ), EQValue.NULL ) ) )
		);
		assertEquals(
				EntityQuery.and(
						new EntityQueryCondition( "ids", EntityQueryOps.NOT_CONTAINS, new EQValue( "1" ) ),
						new EntityQueryCondition( "ids", EntityQueryOps.NOT_CONTAINS, new EQValue( "2" ) ),
						new EntityQueryCondition( "ids", EntityQueryOps.IS_NOT_EMPTY )

				),
				translator.translate( new EntityQueryCondition( "ids", EntityQueryOps.NOT_CONTAINS,
				                                                new EQGroup( new EQValue( "1" ), new EQValue( "2" ), EQValue.NULL ) ) )
		);
	}
}
