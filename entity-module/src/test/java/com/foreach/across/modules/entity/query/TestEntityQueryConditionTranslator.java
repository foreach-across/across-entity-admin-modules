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

import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.convert.ConversionService;

import static com.foreach.across.modules.entity.query.EntityQueryOps.EQ;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.core.convert.TypeDescriptor.valueOf;

/**
 * @author Arne Vandamme
 * @since 2.0.0
 */
public class TestEntityQueryConditionTranslator
{
	private EntityQueryConditionTranslator translator;

	@Mock
	private EntityPropertyDescriptor descriptor;

	@Mock
	private ConversionService conversionService;

	@Before
	public void before() {
		MockitoAnnotations.initMocks( this );
		translator = new EntityQueryConditionTranslator( descriptor, conversionService );

		when( descriptor.getName() ).thenReturn( "name" );
	}

	@Test
	public void noRawValuesWillResultInNewInstanceWithSameArguments() {
		EntityQueryCondition condition = new EntityQueryCondition( "'Name'", EQ, 123 );

		EntityQueryExpression translated = translator.translate( condition );
		assertEquals( new EntityQueryCondition( "name", EQ, 123 ), translated );
	}

	@Test
	public void eqString() {
		assertEquals(
				new EntityQueryCondition( "name", EQ, "123" ),
				translate( "name", EQ, new EQString( "123" ) )
		);
	}

	@Test
	public void eqValue() {
		when( descriptor.getPropertyTypeDescriptor() ).thenReturn( valueOf( Long.class ) );
		when( conversionService.convert( "123", valueOf( String.class ), valueOf( Long.class ) ) ).thenReturn( 555 );

		assertEquals(
				new EntityQueryCondition( "name", EQ, 555 ),
				translate( "name", EQ, new EQValue( "123" ) )
		);
	}

	private EntityQueryExpression translate( String propertyName, EntityQueryOps operator, Object... arguments ) {
		return translator.translate( condition( propertyName, operator, arguments ) );
	}

	private EntityQueryCondition condition( String propertyName, EntityQueryOps operator, Object... arguments ) {
		return new EntityQueryCondition( propertyName, operator, arguments );
	}
}
