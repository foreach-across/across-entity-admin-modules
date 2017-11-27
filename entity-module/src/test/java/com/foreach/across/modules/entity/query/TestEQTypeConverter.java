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
import org.springframework.core.convert.ConversionService;

import java.util.Arrays;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.core.convert.TypeDescriptor.valueOf;

/**
 * @author Arne Vandamme
 * @since 2.0.0
 */
@RunWith(MockitoJUnitRunner.class)
public class TestEQTypeConverter
{
	@Mock
	private ConversionService conversionService;

	@Mock
	private EntityQueryFunctionHandler functionOne;

	@Mock
	private EntityQueryFunctionHandler functionTwo;

	private EQTypeConverter typeConverter;

	@Before
	public void reset() {
		typeConverter = new EQTypeConverter();
		typeConverter.setConversionService( conversionService );
		typeConverter.setFunctionHandlers( Arrays.asList( functionOne, functionTwo ) );
		typeConverter.validateProperties();
	}

	@Test(expected = IllegalArgumentException.class)
	public void validatePropertiesRequiresConversionService() {
		typeConverter.setConversionService( null );
		typeConverter.validateProperties();
	}

	@Test
	public void convertAllWithExpandingGroups() {
		Object[] converted = typeConverter.convertAll(
				valueOf( String.class ), true, "one", new String[] { "two", "three" }
		);
		assertArrayEquals( new Object[] { "one", "two", "three" }, converted );
	}

	@Test
	public void convertAllWithoutExpandingGroups() {
		Object[] converted = typeConverter.convertAll(
				valueOf( String.class ), false, "one", new String[] { "two", "three" }
		);
		assertArrayEquals( new Object[] { "one", new String[] { "two", "three" } }, converted );
	}

	@Test
	public void convertUsingConversionServiceWins() {
		when( conversionService
				      .canConvert( valueOf( EQValue.class ), valueOf( Integer.class ) ) )
				.thenReturn( true );
		when( conversionService.convert( new EQValue( "123" ), valueOf( EQValue.class ),
		                                 valueOf( Integer.class ) ) )
				.thenReturn( 123 );

		assertEquals( 123, typeConverter.convert( valueOf( Integer.class ), new EQValue( "123" ) ) );
	}

	@Test
	public void stringExpectedTypeIsNotDispatchedToConversionService() {
		assertEquals( "text",
		              typeConverter.convert( valueOf( String.class ), new EQString( "text" ) ) );
		verifyNoMoreInteractions( conversionService );
	}

	@Test
	public void eqStringIsReturnedAsString() {
		assertEquals( "text",
		              typeConverter.convert( valueOf( Integer.class ), new EQString( "text" ) ) );
		verify( conversionService ).canConvert( valueOf( EQString.class ),
		                                        valueOf( Integer.class ) );
		verify( conversionService ).canConvert( valueOf( String.class ),
		                                        valueOf( Integer.class ) );
		verifyNoMoreInteractions( conversionService );
	}

	@Test
	public void eqValueDispatchesToInnerValueConversion() {
		when( conversionService.canConvert( valueOf( String.class ), valueOf( Integer.class ) ) ).thenReturn( true );
		when( conversionService.convert( "123", valueOf( String.class ), valueOf( Integer.class ) ) )
				.thenReturn( 123 );

		assertEquals( 123, typeConverter.convert( valueOf( Integer.class ), new EQValue( "123" ) ) );
	}

	@Test
	public void eqGroupConvertsValuesSeparateAndReturnsArray() {
		when( conversionService.canConvert( valueOf( String.class ), valueOf( Integer.class ) ) ).thenReturn( true );
		when( conversionService.convert( "1", valueOf( String.class ), valueOf( Integer.class ) ) )
				.thenReturn( 1 );
		when( conversionService.convert( "2", valueOf( String.class ), valueOf( Integer.class ) ) )
				.thenReturn( 2 );

		assertArrayEquals( new Object[] { 1, 2 },
		                   (Object[]) typeConverter.convert( valueOf( Integer.class ),
		                                                     new EQGroup( new EQValue( "1" ), new EQString( "2" ) ) ) );
	}

	@Test(expected = EntityQueryParsingException.IllegalFunction.class)
	public void eqFunctionThrowsExceptionIfFunctionDoesNotExist() {
		typeConverter.convert( valueOf( Integer.class ), new EQFunction( "hello" ) );
	}

	@Test
	public void convertUsingConversionServiceWinsOverEQString() {
		when( conversionService.canConvert( valueOf( String.class ), valueOf( Integer.class ) ) ).thenReturn( true );
		when( conversionService.convert( "1234", valueOf( String.class ), valueOf( Integer.class ) ) ).thenReturn( 1234 );
		assertEquals( 1234, typeConverter.convert( valueOf( Integer.class ), new EQString( "1234" ) ) );
	}

	@Test
	public void convertUsingConversionServiceEQStringWinsOverString() {
		EQString eqString = new EQString( "1234" );
		when( conversionService.canConvert( valueOf( EQString.class ), valueOf( Integer.class ) ) ).thenReturn( true );
		when( conversionService.convert( eq( eqString ), eq( valueOf( EQString.class ) ), eq( valueOf( Integer.class ) ) ) ).thenReturn( 1234 );

		when( conversionService.canConvert( valueOf( String.class ), valueOf( Integer.class ) ) ).thenReturn( true );
		when( conversionService.convert( "1234", valueOf( String.class ), valueOf( Integer.class ) ) ).thenReturn( 1234 );

		assertEquals( 1234, typeConverter.convert( valueOf( Integer.class ), eqString ) );
		verify( conversionService, times( 0 ) ).convert( "1234", valueOf( String.class ), valueOf( Integer.class ) );

	}

	@Test
	public void validFunction() {
		when( functionTwo.accepts( "hello", valueOf( Integer.class ) ) ).thenReturn( true );
		when( functionTwo.apply( "hello", new EQType[] { new EQValue( "1" ), new EQValue( "2" ) },
		                         valueOf( Integer.class ), typeConverter ) )
				.thenReturn( "hello from function" );

		assertEquals( "hello from function", typeConverter
				.convert( valueOf( Integer.class ),
				          new EQFunction( "hello", new EQType[] { new EQValue( "1" ), new EQValue( "2" ) } ) )
		);
	}
}

