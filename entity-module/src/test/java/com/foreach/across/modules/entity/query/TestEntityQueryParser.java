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

package com.foreach.across.modules.entity.query;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Arne Vandamme
 * @since 2.0.0
 */
@Ignore
public class TestEntityQueryParser
{
	private EntityQueryParser parser;

	private EntityQueryMetadataProvider metadataProvider;

	@Before
	public void before() {
		metadataProvider = mock( EntityQueryMetadataProvider.class );
		parser = new EntityQueryParser();
		parser.setMetadataProvider( metadataProvider );
	}

	@Test
	public void simpleValidQuery() {
		//query
		// .select(
		//      property("value").equalTo(1)
		//          .and( property("123").contains() )
		// )(test=1) or (test=someThing())
		// .orderBy( "value" ).asc()
		//      .and( orderBy("value").desc() )
		// )
		when( metadataProvider.isValidProperty( "value" ) ).thenReturn( true );
		when( metadataProvider.isValidOperatorForProperty( EntityQueryOps.EQ, "value" ) ).thenReturn( true );
		when( metadataProvider.convertStringToTypedValue( "value", EntityQueryOps.EQ, "1" ) )
				.thenReturn( new Object[] { 1 } );

		EntityQuery query = parser.parse( "value = 1" );

		assertEquals(
				EntityQuery.and( new EntityQueryCondition( "value", EntityQueryOps.EQ, 1 ) ),
				query
		);
	}

	@Test
	public void validQueries() {
		when( metadataProvider.isValidProperty( "id" ) ).thenReturn( true );
		when( metadataProvider.isValidOperatorForProperty( EntityQueryOps.EQ, "id" ) ).thenReturn( true );
		when( metadataProvider.isValidProperty( "name" ) ).thenReturn( true );
		when( metadataProvider.isValidOperatorForProperty( EntityQueryOps.EQ, "name" ) ).thenReturn( true );
		when( metadataProvider.isValidOperatorForProperty( EntityQueryOps.NEQ, "name" ) ).thenReturn( true );
		when( metadataProvider.isValidOperatorForProperty( EntityQueryOps.CONTAINS, "name" ) ).thenReturn( true );

		EntityQuery query = parser.parse( "id = 123 or name contains 'boe' or name = 'bla' or name != 'meh'" );
		assertEquals(
				EntityQuery.or(
						new EntityQueryCondition( "id", EntityQueryOps.EQ, 1 ),
						new EntityQueryCondition( "name", EntityQueryOps.CONTAINS, "'boe'" ),
						new EntityQueryCondition( "name", EntityQueryOps.EQ, "'bla'" ),
						new EntityQueryCondition( "name", EntityQueryOps.NEQ, "'meh'" )
				),
				query
		);
	}
}
