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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Arne Vandamme
 * @since 2.0.0
 */
@RunWith(MockitoJUnitRunner.class)
public class TestEntityQueryParser
{
	@Mock
	private EntityQueryMetadataProvider metadataProvider;

	@Mock
	private EntityQueryTranslator queryTranslator;

	private EntityQueryParser parser;

	@Before
	public void before() {
		parser = new EntityQueryParser();
		parser.setMetadataProvider( metadataProvider );
		parser.setQueryTranslator( queryTranslator );

		parser.validateProperties();
	}

	@Test(expected = IllegalArgumentException.class)
	public void validatePropertiesRequiresMetadataProvider() {
		parser.setMetadataProvider( null );
		parser.validateProperties();
	}

	@Test(expected = IllegalArgumentException.class)
	public void validatePropertiesRequiresQueryTranslator() {
		parser.setQueryTranslator( null );
		parser.validateProperties();
	}

	@Test(expected = IllegalArgumentException.class)
	public void invalidQuery() {
		parser.parse( "id = 123 or name contains 'boe' or name = 'bla' or name != 'meh'" );
	}

	@Test
	public void simpleValidQuery() {
		when( metadataProvider.isValidProperty( "id" ) ).thenReturn( true );
		when( metadataProvider.isValidOperatorForProperty( EntityQueryOps.EQ, "id" ) ).thenReturn( true );
		when( metadataProvider.isValidProperty( "name" ) ).thenReturn( true );
		when( metadataProvider.isValidOperatorForProperty( EntityQueryOps.EQ, "name" ) ).thenReturn( true );
		when( metadataProvider.isValidOperatorForProperty( EntityQueryOps.NEQ, "name" ) ).thenReturn( true );
		when( metadataProvider.isValidOperatorForProperty( EntityQueryOps.CONTAINS, "name" ) ).thenReturn( true );
		when( metadataProvider.isValidValueForPropertyAndOperator( new EQValue( "123" ), "id", EntityQueryOps.EQ ) )
				.thenReturn( true );
		when( metadataProvider.isValidValueForPropertyAndOperator( new EQString( "bla" ), "name", EntityQueryOps.EQ ) )
				.thenReturn( true );
		when( metadataProvider
				      .isValidValueForPropertyAndOperator( new EQString( "boe" ), "name", EntityQueryOps.CONTAINS ) )
				.thenReturn( true );
		when( metadataProvider.isValidValueForPropertyAndOperator( new EQString( "meh" ), "name", EntityQueryOps.NEQ ) )
				.thenReturn( true );

		EntityQuery translated = mock( EntityQuery.class );

		EntityQuery rawQuery = EntityQuery.or(
				new EntityQueryCondition( "id", EntityQueryOps.EQ, new EQValue( "123" ) ),
				new EntityQueryCondition( "name", EntityQueryOps.CONTAINS, new EQString( "boe" ) ),
				new EntityQueryCondition( "name", EntityQueryOps.EQ, new EQString( "bla" ) ),
				new EntityQueryCondition( "name", EntityQueryOps.NEQ, new EQString( "meh" ) )
		);

		when( queryTranslator.translate( rawQuery ) ).thenReturn( translated );

		EntityQuery query = parser.parse( "id = 123 or name contains 'boe' or name = 'bla' or name != 'meh'" );
		assertSame( translated, query );
	}
}
