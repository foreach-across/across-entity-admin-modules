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

import com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistry;
import com.foreach.across.modules.entity.registry.properties.SimpleEntityPropertyDescriptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Arne Vandamme
 * @since 2.0.0
 */
@ExtendWith(MockitoExtension.class)
public class TestEntityQueryParser
{
	private EntityQueryMetadataProvider metadataProvider;

	@Mock
	private EntityPropertyRegistry propertyRegistry;

	@Mock
	private EntityQueryTranslator queryTranslator;

	private EntityQueryParser parser;

	@BeforeEach
	public void before() {
		parser = new EntityQueryParser();
		parser.setMetadataProvider( new DefaultEntityQueryMetadataProvider( propertyRegistry ) );
		parser.setQueryTranslator( queryTranslator );

		parser.validateProperties();
	}

	@Test
	public void validatePropertiesRequiresMetadataProvider() {
		assertThrows( IllegalArgumentException.class, () -> {
			parser.setMetadataProvider( null );
			parser.validateProperties();
		} );
	}

	@Test
	public void validatePropertiesRequiresQueryTranslator() {
		assertThrows( IllegalArgumentException.class, () -> {
			parser.setMetadataProvider( null );
			parser.validateProperties();
		} );
	}

	@Test
	public void invalidQuery() {
		assertThrows( EntityQueryParsingException.IllegalField.class, () -> {
			parser.parse( "id = 123 or name contains 'boe' or name = 'bla' or name != 'meh'" );
		} );
	}

	@Test
	public void simpleValidQuery() {
		SimpleEntityPropertyDescriptor id = new SimpleEntityPropertyDescriptor( "id" );
		id.setPropertyType( Integer.class );
		SimpleEntityPropertyDescriptor name = new SimpleEntityPropertyDescriptor( "name" );
		name.setPropertyType( String.class );
		when( propertyRegistry.contains( "id" ) ).thenReturn( true );
		when( propertyRegistry.contains( "name" ) ).thenReturn( true );
		when( propertyRegistry.getProperty( "id" ) ).thenReturn( id );
		when( propertyRegistry.getProperty( "name" ) ).thenReturn( name );

		EntityQuery translated = mock( EntityQuery.class );

		EntityQuery rawQuery = EntityQuery.or(
				new EntityQueryCondition( "id", EntityQueryOps.EQ, new EQValue( "123" ) ),
				new EntityQueryCondition( "name", EntityQueryOps.CONTAINS, new EQString( "boe" ) ),
				new EntityQueryCondition( "name", EntityQueryOps.EQ, new EQString( "bla" ) ),
				new EntityQueryCondition( "name", EntityQueryOps.NEQ, new EQString( "m'eh" ) )
		);

		when( queryTranslator.translate( rawQuery ) ).thenReturn( translated );

		EntityQuery query = parser.parse( "id = 123 or name contains 'boe' or name = 'bla' or name != 'm\\'eh'" );
		assertSame( translated, query );
	}
}
