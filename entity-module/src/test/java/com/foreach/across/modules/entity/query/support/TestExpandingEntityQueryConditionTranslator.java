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

import com.foreach.across.modules.entity.query.EntityQuery;
import com.foreach.across.modules.entity.query.EntityQueryCondition;
import com.foreach.across.modules.entity.query.EntityQueryConditionTranslator;
import com.foreach.across.modules.entity.query.EntityQueryOps;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Arne Vandamme
 * @since 2.2.0
 */
public class TestExpandingEntityQueryConditionTranslator
{
	@Test
	public void noMembersResultsInEmptyQuery() {
		EntityQueryConditionTranslator translator = EntityQueryConditionTranslator.expandingOr();
		assertEquals(
				EntityQuery.or(),
				translator.translate( new EntityQueryCondition( "title", EntityQueryOps.LIKE, "My Text" ) )
		);
	}

	@Test
	public void singleValueSubQuery() {
		EntityQueryConditionTranslator translator = EntityQueryConditionTranslator.expandingOr( "name" );
		assertEquals(
				EntityQuery.or( new EntityQueryCondition( "name", EntityQueryOps.LIKE, "My Text" ) ),
				translator.translate( new EntityQueryCondition( "title", EntityQueryOps.LIKE, "My Text" ) )
		);
	}

	@Test
	public void sameOperandAndValuesAreExpanded() {
		EntityQueryConditionTranslator translator = EntityQueryConditionTranslator.expandingOr( "firstName", "lastName" );
		assertEquals(
				EntityQuery.or( new EntityQueryCondition( "firstName", EntityQueryOps.LIKE, "My Text" ),
				                new EntityQueryCondition( "lastName", EntityQueryOps.LIKE, "My Text" ) ),
				translator.translate( new EntityQueryCondition( "title", EntityQueryOps.LIKE, "My Text" ) )
		);
	}

	@Test
	public void duplicatesAreRemoved() {
		EntityQueryConditionTranslator translator = EntityQueryConditionTranslator.expandingOr( "firstName", "lastName", "firstName" );
		assertEquals(
				EntityQuery.or( new EntityQueryCondition( "firstName", EntityQueryOps.LIKE, "My Text" ),
				                new EntityQueryCondition( "lastName", EntityQueryOps.LIKE, "My Text" ) ),
				translator.translate( new EntityQueryCondition( "title", EntityQueryOps.LIKE, "My Text" ) )
		);
	}

	@Test
	public void negationExpandsIntoAnd() {
		EntityQueryConditionTranslator translator = EntityQueryConditionTranslator.expandingOr( "firstName", "lastName" );
		assertEquals(
				EntityQuery.and( new EntityQueryCondition( "firstName", EntityQueryOps.NOT_LIKE, "My Text" ),
				                 new EntityQueryCondition( "lastName", EntityQueryOps.NOT_LIKE, "My Text" ) ),
				translator.translate( new EntityQueryCondition( "title", EntityQueryOps.NOT_LIKE, "My Text" ) )
		);
	}

	@Test
	public void expandingUsingAnd() {
		EntityQueryConditionTranslator translator = EntityQueryConditionTranslator.expandingAnd( "firstName", "lastName" );
		assertEquals(
				EntityQuery.and( new EntityQueryCondition( "firstName", EntityQueryOps.LIKE, "My Text" ),
				                 new EntityQueryCondition( "lastName", EntityQueryOps.LIKE, "My Text" ) ),
				translator.translate( new EntityQueryCondition( "title", EntityQueryOps.LIKE, "My Text" ) )
		);

		assertEquals(
				EntityQuery.or( new EntityQueryCondition( "firstName", EntityQueryOps.NOT_LIKE, "My Text" ),
				                new EntityQueryCondition( "lastName", EntityQueryOps.NOT_LIKE, "My Text" ) ),
				translator.translate( new EntityQueryCondition( "title", EntityQueryOps.NOT_LIKE, "My Text" ) )
		);
	}
}
