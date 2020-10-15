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

package it.com.foreach.across.modules.entity.views.bootstrapui;

import com.foreach.across.modules.bootstrapui.elements.HiddenFormElement;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.views.EntityViewElementBuilderFactory;
import com.foreach.across.modules.entity.views.ViewElementMode;
import com.foreach.across.modules.entity.views.bootstrapui.HiddenFormElementBuilderFactory;
import com.foreach.across.modules.entity.web.EntityViewModel;
import com.foreach.across.testmodules.springdata.business.Client;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.*;

/**
 * @author Arne Vandamme
 */
public class TestHiddenFormElementBuilderFactory extends ViewElementBuilderFactoryTestSupport<HiddenFormElement>
{
	@Mock
	private ConversionService conversionService;

	@Override
	protected EntityViewElementBuilderFactory createBuilderFactory() {
		HiddenFormElementBuilderFactory builderFactory = new HiddenFormElementBuilderFactory();
		builderFactory.setEntityRegistry( entityRegistry );
		builderFactory.setConversionService( conversionService );
		return builderFactory;
	}

	@Override
	protected Class getTestClass() {
		return Instance.class;
	}

	@Test
	public void stringValue() {
		when( properties.get( "name" ).getValueFetcher() ).thenReturn( entity -> "fetchedValue" );
		when( conversionService.convert( eq( "fetchedValue" ), any( TypeDescriptor.class ),
		                                 any( TypeDescriptor.class ) ) )
				.thenReturn( "some value" );
		when( builderContext.getAttribute( EntityViewModel.ENTITY ) ).thenReturn( "entity" );

		HiddenFormElement hidden = assembleAndVerify( "name" );
		assertEquals( "some value", hidden.getValue() );
	}

	@Test
	public void longValue() {
		when( properties.get( "number" ).getValueFetcher() ).thenReturn( entity -> 123L );
		when( conversionService.convert( eq( 123L ), any( TypeDescriptor.class ),
		                                 any( TypeDescriptor.class ) ) )
				.thenReturn( "321" );
		when( builderContext.getAttribute( EntityViewModel.ENTITY ) ).thenReturn( "entity" );

		HiddenFormElement hidden = assembleAndVerify( "number" );
		assertEquals( "321", hidden.getValue() );
	}

	@Test
	public void unknownEntityValue() {
		Client client = new Client();
		when( properties.get( "client" ).getValueFetcher() ).thenReturn( entity -> client );
		when( conversionService.convert( eq( client ), any( TypeDescriptor.class ),
		                                 any( TypeDescriptor.class ) ) )
				.thenReturn( "some client" );
		when( builderContext.getAttribute( EntityViewModel.ENTITY ) ).thenReturn( "entity" );

		HiddenFormElement hidden = assembleAndVerify( "client" );
		assertEquals( "some client", hidden.getValue() );

		verify( entityRegistry ).getEntityConfiguration( client );
	}

	@Test
	public void entityValue() {
		Client client = new Client();
		when( properties.get( "client" ).getValueFetcher() ).thenReturn( entity -> client );

		EntityConfiguration clientConfig = mock( EntityConfiguration.class );
		when( entityRegistry.getEntityConfiguration( client ) ).thenReturn( clientConfig );
		when( clientConfig.getId( client ) ).thenReturn( 9999L );

		when( conversionService.convert( eq( 9999L ), eq( String.class ) ) ).thenReturn( "ENTITY ID" );

		when( builderContext.getAttribute( EntityViewModel.ENTITY ) ).thenReturn( "entity" );

		HiddenFormElement hidden = assembleAndVerify( "client" );
		assertEquals( "ENTITY ID", hidden.getValue() );
	}

	@SuppressWarnings("unchecked")
	private <V> V assembleAndVerify( String propertyName ) {
		HiddenFormElement control = assemble( propertyName, ViewElementMode.CONTROL );
		assertEquals( propertyName, control.getName() );
		assertEquals( propertyName, control.getControlName() );
		assertFalse( control.isDisabled() );

		return (V) control;
	}

	@SuppressWarnings("unused")
	private static class Instance
	{
		public long number;

		public String name;

		public Client client;
	}
}
