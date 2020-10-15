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

import com.foreach.across.modules.entity.query.EntityQueryCondition;
import com.foreach.across.modules.entity.query.EntityQueryConditionTranslator;
import com.foreach.across.modules.entity.query.EntityQueryOps;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * @author Arne Vandamme
 * @since 2.1.0
 */
public class TestIgnoringCaseEntityQueryConditionTranslator
{
	private EntityQueryConditionTranslator translator = EntityQueryConditionTranslator.ignoreCase();

	@Test
	public void equalsShouldBeConvertedToLikeIC() {
		assertEquals(
				new EntityQueryCondition( "title", EntityQueryOps.LIKE_IC, "My Text" ),
				translator.translate( new EntityQueryCondition( "title", EntityQueryOps.EQ, "My Text" ) )
		);
		assertEquals(
				new EntityQueryCondition( "title", EntityQueryOps.NOT_LIKE_IC, "My Text" ),
				translator.translate( new EntityQueryCondition( "title", EntityQueryOps.NEQ, "My Text" ) )
		);
	}

	@Test
	public void likeShouldBeConvertedToLikeIC() {
		assertEquals(
				new EntityQueryCondition( "title", EntityQueryOps.LIKE_IC, "My Text" ),
				translator.translate( new EntityQueryCondition( "title", EntityQueryOps.LIKE, "My Text" ) )
		);
		assertEquals(
				new EntityQueryCondition( "title", EntityQueryOps.NOT_LIKE_IC, "My Text" ),
				translator.translate( new EntityQueryCondition( "title", EntityQueryOps.NOT_LIKE, "My Text" ) )
		);
	}

	@Test
	public void charactersShouldBeEscaped() {
		assertEquals(
				new EntityQueryCondition( "name", EntityQueryOps.LIKE_IC, "john \\% surname" ),
				translator.translate( new EntityQueryCondition( "name", EntityQueryOps.EQ, "john % surname" ) )
		);
		assertEquals(
				new EntityQueryCondition( "name", EntityQueryOps.LIKE_IC, "peter \\\\ surname" ),
				translator.translate( new EntityQueryCondition( "name", EntityQueryOps.EQ, "peter \\ surname" ) )
		);
	}

	@Test
	public void likeICShouldNotBeTranslated() {
		assertNotTranslated( new EntityQueryCondition( "title", EntityQueryOps.LIKE_IC, "My Text" ) );
		assertNotTranslated( new EntityQueryCondition( "title", EntityQueryOps.NOT_LIKE_IC, "My Text" ) );
	}

	@Test
	public void nonTextValuesShouldNotBeTranslated() {
		assertNotTranslated( new EntityQueryCondition( "title", EntityQueryOps.EQ, 123 ) );
		assertNotTranslated( new EntityQueryCondition( "title", EntityQueryOps.NEQ, 456 ) );
		assertNotTranslated( new EntityQueryCondition( "title", EntityQueryOps.LIKE, 123 ) );
		assertNotTranslated( new EntityQueryCondition( "title", EntityQueryOps.NOT_LIKE, 456 ) );
	}

	@Test
	public void nonTextOperandsShouldNotBeTranslated() {
		assertNotTranslated( new EntityQueryCondition( "title", EntityQueryOps.CONTAINS, "My Text" ) );
		assertNotTranslated( new EntityQueryCondition( "title", EntityQueryOps.NOT_CONTAINS, "My Text" ) );
		assertNotTranslated( new EntityQueryCondition( "title", EntityQueryOps.IS_EMPTY ) );
		assertNotTranslated( new EntityQueryCondition( "title", EntityQueryOps.IS_NOT_NULL ) );
		assertNotTranslated( new EntityQueryCondition( "title", EntityQueryOps.GE, "My Text" ) );
		assertNotTranslated( new EntityQueryCondition( "title", EntityQueryOps.LT, "My Text" ) );
	}

	private void assertNotTranslated( EntityQueryCondition condition ) {
		assertSame( condition, translator.translate( condition ) );
	}
}
