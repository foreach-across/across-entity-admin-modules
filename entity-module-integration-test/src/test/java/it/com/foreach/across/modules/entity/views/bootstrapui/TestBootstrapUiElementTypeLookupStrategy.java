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

import com.foreach.across.modules.bootstrapui.elements.BootstrapUiElements;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.EntityRegistry;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.registry.properties.meta.PropertyPersistenceMetadata;
import com.foreach.across.testmodules.springdata.business.Client;
import com.foreach.across.testmodules.springdata.business.CompanyStatus;
import com.foreach.across.modules.entity.views.ViewElementMode;
import com.foreach.across.modules.entity.views.bootstrapui.BootstrapUiElementTypeLookupStrategy;
import com.foreach.across.modules.entity.views.bootstrapui.MultiValueElementBuilderFactory;
import com.foreach.across.modules.entity.views.bootstrapui.OptionsFormElementBuilderFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.TypeDescriptor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.*;

/**
 * @author Arne Vandamme
 */
@RunWith(MockitoJUnitRunner.class)
public class TestBootstrapUiElementTypeLookupStrategy
{
	@Mock
	private EntityRegistry entityRegistry;

	@InjectMocks
	private BootstrapUiElementTypeLookupStrategy strategy;

	private EntityConfiguration entityConfiguration = mock( EntityConfiguration.class );
	private EntityPropertyDescriptor descriptor = mock( EntityPropertyDescriptor.class );

	@Before
	public void resetMocks() {
		reset( entityConfiguration, descriptor );

		when( descriptor.isWritable() ).thenReturn( true );
		when( descriptor.isReadable() ).thenReturn( true );
	}

	@Test
	public void formGroupIsReturnedForFormModes() {
		assertEquals( BootstrapUiElements.FORM_GROUP, lookup( String.class, ViewElementMode.FORM_WRITE ) );
		assertEquals( BootstrapUiElements.FORM_GROUP, lookup( Integer.class, ViewElementMode.FORM_WRITE ) );
		assertEquals( BootstrapUiElements.FORM_GROUP, lookup( int.class, ViewElementMode.FORM_WRITE ) );
		assertEquals( BootstrapUiElements.FORM_GROUP, lookup( String.class, ViewElementMode.FORM_READ ) );
		assertEquals( BootstrapUiElements.FORM_GROUP, lookup( Integer.class, ViewElementMode.FORM_READ ) );
		assertEquals( BootstrapUiElements.FORM_GROUP, lookup( int.class, ViewElementMode.FORM_READ ) );
		assertEquals( BootstrapUiElements.FORM_GROUP, lookup( String.class, ViewElementMode.FORM_WRITE.forMultiple() ) );
		assertEquals( BootstrapUiElements.FORM_GROUP, lookup( String.class, ViewElementMode.FORM_READ.forMultiple() ) );
	}

	@Test
	public void nullIsReturnedForFormModeReadWhenNotReadable() {
		when( descriptor.isReadable() ).thenReturn( false );

		assertEquals( BootstrapUiElements.FORM_GROUP, lookup( String.class, ViewElementMode.FORM_WRITE ) );
		assertEquals( BootstrapUiElements.FORM_GROUP, lookup( Integer.class, ViewElementMode.FORM_WRITE ) );
		assertEquals( BootstrapUiElements.FORM_GROUP, lookup( int.class, ViewElementMode.FORM_WRITE ) );
		assertNull( lookup( String.class, ViewElementMode.FORM_READ ) );
		assertNull( lookup( Integer.class, ViewElementMode.FORM_READ ) );
		assertNull( lookup( int.class, ViewElementMode.FORM_READ ) );
	}

	@Test
	public void nullIsReturnedForFormModeWriteWhenNotWritable() {
		when( descriptor.isWritable() ).thenReturn( false );

		assertEquals( BootstrapUiElements.FORM_GROUP, lookup( String.class, ViewElementMode.FORM_WRITE ) );
		assertEquals( BootstrapUiElements.FORM_GROUP, lookup( Integer.class, ViewElementMode.FORM_WRITE ) );
		assertEquals( BootstrapUiElements.NUMERIC, lookup( int.class, ViewElementMode.CONTROL ) );
		assertEquals( BootstrapUiElements.FORM_GROUP, lookup( String.class, ViewElementMode.FORM_READ ) );
		assertEquals( BootstrapUiElements.FORM_GROUP, lookup( Integer.class, ViewElementMode.FORM_READ ) );
		assertEquals( BootstrapUiElements.FORM_GROUP, lookup( int.class, ViewElementMode.FORM_READ ) );
	}

	@Test
	public void textTypeForReadonlyValues() {
		assertEquals( BootstrapUiElements.TEXT, lookup( String.class, ViewElementMode.VALUE ) );
		assertEquals( BootstrapUiElements.TEXT, lookup( String.class, ViewElementMode.LIST_VALUE ) );
	}

	@Test
	public void datetimeForReadonlyDateTimeValues() {
		assertEquals( BootstrapUiElements.DATETIME, lookup( Date.class, ViewElementMode.VALUE ) );
		assertEquals( BootstrapUiElements.DATETIME, lookup( Date.class, ViewElementMode.LIST_VALUE ) );
	}

	@Test
	public void numericForReadonlyNumericValues() {
		assertEquals( BootstrapUiElements.NUMERIC, lookup( Integer.class, ViewElementMode.VALUE ) );
		assertEquals( BootstrapUiElements.NUMERIC, lookup( int.class, ViewElementMode.VALUE ) );
		assertEquals( BootstrapUiElements.NUMERIC, lookup( AtomicInteger.class, ViewElementMode.VALUE ) );
		assertEquals( BootstrapUiElements.NUMERIC, lookup( Long.class, ViewElementMode.VALUE ) );
		assertEquals( BootstrapUiElements.NUMERIC, lookup( BigDecimal.class, ViewElementMode.VALUE ) );
		assertEquals( BootstrapUiElements.NUMERIC, lookup( Integer.class, ViewElementMode.LIST_VALUE ) );
		assertEquals( BootstrapUiElements.NUMERIC, lookup( int.class, ViewElementMode.LIST_VALUE ) );
		assertEquals( BootstrapUiElements.NUMERIC, lookup( AtomicInteger.class, ViewElementMode.LIST_VALUE ) );
		assertEquals( BootstrapUiElements.NUMERIC, lookup( Long.class, ViewElementMode.LIST_VALUE ) );
		assertEquals( BootstrapUiElements.NUMERIC, lookup( BigDecimal.class, ViewElementMode.LIST_VALUE ) );
	}

	@Test
	public void labelTypeForLabelModes() {
		assertEquals( BootstrapUiElements.LABEL, lookup( String.class, ViewElementMode.LABEL ) );
		assertEquals( BootstrapUiElements.LABEL, lookup( Integer.class, ViewElementMode.LABEL ) );
		assertEquals( BootstrapUiElements.LABEL, lookup( int.class, ViewElementMode.LABEL ) );
		assertEquals( BootstrapUiElements.LABEL, lookup( AtomicInteger.class, ViewElementMode.LABEL ) );
		assertEquals( BootstrapUiElements.LABEL, lookup( Long.class, ViewElementMode.LABEL ) );
		assertEquals( BootstrapUiElements.LABEL, lookup( BigDecimal.class, ViewElementMode.LABEL ) );
		assertEquals( BootstrapUiElements.LABEL, lookup( Date.class, ViewElementMode.LABEL ) );
		assertEquals( BootstrapUiElements.LABEL, lookup( String.class, ViewElementMode.LIST_LABEL ) );
		assertEquals( BootstrapUiElements.LABEL, lookup( Integer.class, ViewElementMode.LIST_LABEL ) );
		assertEquals( BootstrapUiElements.LABEL, lookup( int.class, ViewElementMode.LIST_LABEL ) );
		assertEquals( BootstrapUiElements.LABEL, lookup( AtomicInteger.class, ViewElementMode.LIST_LABEL ) );
		assertEquals( BootstrapUiElements.LABEL, lookup( Long.class, ViewElementMode.LIST_LABEL ) );
		assertEquals( BootstrapUiElements.LABEL, lookup( BigDecimal.class, ViewElementMode.LIST_LABEL ) );
		assertEquals( BootstrapUiElements.LABEL, lookup( Date.class, ViewElementMode.LIST_LABEL ) );
		assertEquals( BootstrapUiElements.LABEL, lookup( String.class, ViewElementMode.LABEL.forMultiple() ) );
		assertEquals( BootstrapUiElements.LABEL, lookup( String.class, ViewElementMode.LIST_LABEL.forMultiple() ) );
	}

	@Test
	public void textboxTypeForPrimitives() {
		assertEquals( BootstrapUiElements.TEXTBOX, lookup( String.class, ViewElementMode.CONTROL ) );
		assertEquals( BootstrapUiElements.TEXTBOX, lookup( String.class, ViewElementMode.LIST_CONTROL ) );
		assertEquals( BootstrapUiElements.TEXTBOX, lookup( String.class, ViewElementMode.FILTER_CONTROL ) );
	}

	@Test
	public void numericTypeForNumerics() {
		assertEquals( BootstrapUiElements.NUMERIC, lookup( Integer.class, ViewElementMode.CONTROL ) );
		assertEquals( BootstrapUiElements.NUMERIC, lookup( int.class, ViewElementMode.CONTROL ) );
		assertEquals( BootstrapUiElements.NUMERIC, lookup( AtomicInteger.class, ViewElementMode.CONTROL ) );
		assertEquals( BootstrapUiElements.NUMERIC, lookup( Long.class, ViewElementMode.CONTROL ) );
		assertEquals( BootstrapUiElements.NUMERIC, lookup( BigDecimal.class, ViewElementMode.CONTROL ) );
		assertEquals( BootstrapUiElements.NUMERIC, lookup( Integer.class, ViewElementMode.LIST_CONTROL ) );
		assertEquals( BootstrapUiElements.NUMERIC, lookup( int.class, ViewElementMode.LIST_CONTROL ) );
		assertEquals( BootstrapUiElements.NUMERIC, lookup( AtomicInteger.class, ViewElementMode.LIST_CONTROL ) );
		assertEquals( BootstrapUiElements.NUMERIC, lookup( Long.class, ViewElementMode.LIST_CONTROL ) );
		assertEquals( BootstrapUiElements.NUMERIC, lookup( BigDecimal.class, ViewElementMode.LIST_CONTROL ) );
	}

	@Test
	public void fieldsetForEmbeddedTypesWhenFormElementsRequested() {
		PropertyPersistenceMetadata metadata = new PropertyPersistenceMetadata();
		when( descriptor.getAttribute( PropertyPersistenceMetadata.class ) ).thenReturn( metadata );

		assertEquals( BootstrapUiElements.FORM_GROUP, lookup( AtomicInteger.class, ViewElementMode.FORM_READ ) );
		assertEquals( BootstrapUiElements.FORM_GROUP, lookup( AtomicInteger.class, ViewElementMode.FORM_WRITE ) );

		metadata.setEmbedded( true );

		assertEquals( BootstrapUiElements.FIELDSET, lookup( AtomicInteger.class, ViewElementMode.FORM_READ ) );
		assertEquals( BootstrapUiElements.FIELDSET, lookup( AtomicInteger.class, ViewElementMode.FORM_WRITE ) );
	}

	@Test
	public void labelForEmbeddedTypesWhenLabelRequested() {
		PropertyPersistenceMetadata metadata = new PropertyPersistenceMetadata();
		when( descriptor.getAttribute( PropertyPersistenceMetadata.class ) ).thenReturn( metadata );
		metadata.setEmbedded( true );

		assertEquals( BootstrapUiElements.LABEL, lookup( AtomicInteger.class, ViewElementMode.LABEL ) );
		assertEquals( BootstrapUiElements.LABEL, lookup( AtomicInteger.class, ViewElementMode.LIST_LABEL ) );
		assertEquals( BootstrapUiElements.LABEL, lookup( AtomicInteger.class, ViewElementMode.LABEL.forMultiple() ) );
		assertEquals( BootstrapUiElements.LABEL, lookup( AtomicInteger.class, ViewElementMode.LIST_LABEL.forMultiple() ) );
	}

	@Test
	public void nullForEmbeddedTypesIfNotAFormElement() {
		PropertyPersistenceMetadata metadata = new PropertyPersistenceMetadata();
		when( descriptor.getAttribute( PropertyPersistenceMetadata.class ) ).thenReturn( metadata );

		assertEquals( BootstrapUiElements.NUMERIC, lookup( AtomicInteger.class, ViewElementMode.CONTROL ) );

		metadata.setEmbedded( true );

		assertNull( lookup( AtomicInteger.class, ViewElementMode.CONTROL ) );
		assertNull( lookup( AtomicInteger.class, ViewElementMode.LIST_VALUE ) );
	}

	@Test
	public void dateTypeForDates() throws Exception {
		assertEquals( BootstrapUiElements.DATETIME, lookup( Date.class, ViewElementMode.CONTROL ) );
		assertEquals( BootstrapUiElements.DATETIME, lookup( LocalDate.class, ViewElementMode.CONTROL ) );
		assertEquals( BootstrapUiElements.DATETIME, lookup( LocalTime.class, ViewElementMode.CONTROL ) );
		assertEquals( BootstrapUiElements.DATETIME, lookup( LocalDateTime.class, ViewElementMode.CONTROL ) );
	}

	@Test
	public void checkboxTypeForBooleans() {
		assertEquals( BootstrapUiElements.CHECKBOX, lookup( Boolean.class, ViewElementMode.CONTROL ) );
		assertEquals( BootstrapUiElements.CHECKBOX, lookup( boolean.class, ViewElementMode.CONTROL ) );
		assertEquals( BootstrapUiElements.CHECKBOX, lookup( AtomicBoolean.class, ViewElementMode.CONTROL ) );
		assertEquals( BootstrapUiElements.CHECKBOX, lookup( Boolean.class, ViewElementMode.LIST_CONTROL ) );
		assertEquals( BootstrapUiElements.CHECKBOX, lookup( boolean.class, ViewElementMode.LIST_CONTROL ) );
		assertEquals( BootstrapUiElements.CHECKBOX, lookup( AtomicBoolean.class, ViewElementMode.LIST_CONTROL ) );
	}

	@Test
	public void enumValueShouldReturnSelectType() {
		assertEquals( BootstrapUiElements.SELECT, lookup( CompanyStatus.class, ViewElementMode.CONTROL ) );
		assertEquals( BootstrapUiElements.SELECT, lookup( CompanyStatus.class, ViewElementMode.FILTER_CONTROL ) );
	}

	@Test
	@SuppressWarnings("unchecked")
	public void singleEntityTypeShouldReturnSelectType() {
		EntityConfiguration clientConfig = mock( EntityConfiguration.class );

		when( entityRegistry.getEntityConfiguration( Client.class ) ).thenReturn( clientConfig );
		when( entityRegistry.contains( Client.class ) ).thenReturn( true );

		assertEquals( BootstrapUiElements.SELECT, lookup( Client.class, ViewElementMode.CONTROL ) );
		assertEquals( BootstrapUiElements.SELECT, lookup( Client.class, ViewElementMode.FILTER_CONTROL ) );
	}

	@Test
	@SuppressWarnings("unchecked")
	public void collectionEntityTypeShouldReturnOptionsOrSelectTypeForFilterControl() {
		when( entityRegistry.contains( Client.class ) ).thenReturn( true );
		when( descriptor.getPropertyType() ).thenReturn( (Class) List.class );
		TypeDescriptor collectionTypeDescriptor = TypeDescriptor.collection( List.class, TypeDescriptor.valueOf( Client.class ) );
		when( descriptor.getPropertyTypeDescriptor() ).thenReturn( collectionTypeDescriptor );

		assertEquals( OptionsFormElementBuilderFactory.OPTIONS, strategy.findElementType( descriptor, ViewElementMode.CONTROL ) );
		assertEquals( BootstrapUiElements.SELECT, strategy.findElementType( descriptor, ViewElementMode.FILTER_CONTROL ) );
	}

	@Test
	public void stringSetsAreSupportedAsMultiValue() {
		when( descriptor.getPropertyType() ).thenReturn( (Class) Set.class );
		TypeDescriptor collectionTypeDescriptor = TypeDescriptor.collection(
				Set.class, TypeDescriptor.valueOf( String.class )
		);
		when( descriptor.getPropertyTypeDescriptor() ).thenReturn( collectionTypeDescriptor );

		assertEquals( MultiValueElementBuilderFactory.ELEMENT_TYPE,
		              strategy.findElementType( descriptor, ViewElementMode.CONTROL ) );
	}

	@Test
	@SuppressWarnings("unchecked")
	public void collectionEnumShouldReturnOptions() {
		when( descriptor.getPropertyType() ).thenReturn( (Class) Set.class );
		TypeDescriptor collectionTypeDescriptor = TypeDescriptor.collection(
				Set.class, TypeDescriptor.valueOf( CompanyStatus.class )
		);
		when( descriptor.getPropertyTypeDescriptor() ).thenReturn( collectionTypeDescriptor );

		assertEquals( OptionsFormElementBuilderFactory.OPTIONS, strategy.findElementType( descriptor, ViewElementMode.CONTROL ) );
	}

	@Test
	public void unknownType() {
		assertNull( lookup( ViewElementMode.CONTROL ) );
		assertEquals( BootstrapUiElements.TEXT, lookup( ViewElementMode.VALUE ) );
	}

	@SuppressWarnings("unchecked")
	private String lookup( Class propertyType, ViewElementMode mode ) {
		when( descriptor.getPropertyType() ).thenReturn( propertyType );
		when( descriptor.getPropertyTypeDescriptor() ).thenReturn( TypeDescriptor.valueOf( propertyType ) );
		return strategy.findElementType( descriptor, mode );
	}

	private String lookup( ViewElementMode mode ) {
		return strategy.findElementType( descriptor, mode );
	}

	@Configuration
	protected static class Config
	{

	}
}
