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
import com.foreach.across.modules.entity.EntityAttributes;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.EntityRegistry;
import com.foreach.across.modules.entity.registry.properties.DefaultEntityPropertyRegistryProvider;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistry;
import com.foreach.across.modules.entity.views.ViewElementMode;
import com.foreach.across.modules.entity.views.bootstrapui.*;
import com.foreach.across.testmodules.springdata.business.Client;
import com.foreach.across.testmodules.springdata.business.CompanyStatus;
import lombok.Getter;
import lombok.Setter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.TypeDescriptor;

import java.math.BigDecimal;
import java.time.*;
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

	private BootstrapUiElementTypeLookupStrategy strategy;

	private EntityConfiguration entityConfiguration = mock( EntityConfiguration.class );
	private EntityPropertyDescriptor descriptor = mock( EntityPropertyDescriptor.class );

	@Before
	public void resetMocks() {
		strategy = new BootstrapUiElementTypeLookupStrategy( entityRegistry, DefaultEntityPropertyRegistryProvider.INSTANCE );
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
	public void filterFormGroupIsReturnedForFilterForm() {
		assertEquals( FilterFormGroupElementBuilderFactory.VIEW_ELEMENT_TYPE, lookup( String.class, ViewElementMode.FILTER_FORM ) );
		assertEquals( FilterFormGroupElementBuilderFactory.VIEW_ELEMENT_TYPE, lookup( int.class, ViewElementMode.FILTER_FORM ) );
		assertEquals( FilterFormGroupElementBuilderFactory.VIEW_ELEMENT_TYPE, lookup( String.class, ViewElementMode.FILTER_FORM.forMultiple() ) );
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
		assertEquals( BootstrapUiElements.DATETIME, lookup( LocalDate.class, ViewElementMode.VALUE ) );
		assertEquals( BootstrapUiElements.DATETIME, lookup( LocalDate.class, ViewElementMode.LIST_VALUE ) );
		assertEquals( BootstrapUiElements.DATETIME, lookup( LocalTime.class, ViewElementMode.VALUE ) );
		assertEquals( BootstrapUiElements.DATETIME, lookup( LocalTime.class, ViewElementMode.LIST_VALUE ) );
		assertEquals( BootstrapUiElements.DATETIME, lookup( LocalDateTime.class, ViewElementMode.VALUE ) );
		assertEquals( BootstrapUiElements.DATETIME, lookup( LocalDateTime.class, ViewElementMode.LIST_VALUE ) );
		assertEquals( BootstrapUiElements.DATETIME, lookup( ZonedDateTime.class, ViewElementMode.VALUE ) );
		assertEquals( BootstrapUiElements.DATETIME, lookup( ZonedDateTime.class, ViewElementMode.LIST_VALUE ) );
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
	public void textTypeForDuration() {
		assertEquals( BootstrapUiElements.TEXT, lookup( Duration.class, ViewElementMode.VALUE ) );
		assertEquals( BootstrapUiElements.TEXT, lookup( Duration.class, ViewElementMode.LIST_VALUE ) );
		assertEquals( BootstrapUiElements.TEXTBOX, lookup( Duration.class, ViewElementMode.CONTROL ) );
		assertEquals( BootstrapUiElements.TEXTBOX, lookup( Duration.class, ViewElementMode.FILTER_CONTROL ) );
		assertEquals( BootstrapUiElements.TEXTBOX, lookup( Duration.class, ViewElementMode.LIST_CONTROL ) );
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
		assertEquals( BootstrapUiElements.LABEL, lookup( ZonedDateTime.class, ViewElementMode.LABEL ) );
		assertEquals( BootstrapUiElements.LABEL, lookup( String.class, ViewElementMode.LIST_LABEL ) );
		assertEquals( BootstrapUiElements.LABEL, lookup( Integer.class, ViewElementMode.LIST_LABEL ) );
		assertEquals( BootstrapUiElements.LABEL, lookup( int.class, ViewElementMode.LIST_LABEL ) );
		assertEquals( BootstrapUiElements.LABEL, lookup( AtomicInteger.class, ViewElementMode.LIST_LABEL ) );
		assertEquals( BootstrapUiElements.LABEL, lookup( Long.class, ViewElementMode.LIST_LABEL ) );
		assertEquals( BootstrapUiElements.LABEL, lookup( BigDecimal.class, ViewElementMode.LIST_LABEL ) );
		assertEquals( BootstrapUiElements.LABEL, lookup( Date.class, ViewElementMode.LIST_LABEL ) );
		assertEquals( BootstrapUiElements.LABEL, lookup( ZonedDateTime.class, ViewElementMode.LIST_LABEL ) );
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
		assertEquals( BootstrapUiElements.FORM_GROUP, lookup( AtomicInteger.class, ViewElementMode.FORM_READ ) );
		assertEquals( BootstrapUiElements.FORM_GROUP, lookup( AtomicInteger.class, ViewElementMode.FORM_WRITE ) );

		when( descriptor.getAttribute( EntityAttributes.IS_EMBEDDED_OBJECT ) ).thenReturn( true );

		assertEquals( BootstrapUiElements.FIELDSET, lookup( AtomicInteger.class, ViewElementMode.FORM_READ ) );
		assertEquals( BootstrapUiElements.FIELDSET, lookup( AtomicInteger.class, ViewElementMode.FORM_WRITE ) );
	}

	@Test
	@SuppressWarnings("unchecked")
	public void propertyIsEmbeddedIfTargetEntityConfigurationIsEmbedded() {
		EntityConfiguration clientConfig = mock( EntityConfiguration.class );
		when( clientConfig.hasEntityModel() ).thenReturn( false );
		when( clientConfig.getPropertyRegistry() ).thenReturn( DefaultEntityPropertyRegistryProvider.INSTANCE.get( Client.class ) );
		when( entityRegistry.getEntityConfiguration( Client.class ) ).thenReturn( clientConfig );

		assertEquals( BootstrapUiElements.FIELDSET, lookup( Client.class, ViewElementMode.FORM_READ ) );
		assertEquals( BootstrapUiElements.FIELDSET, lookup( Client.class, ViewElementMode.FORM_WRITE ) );

		when( clientConfig.hasEntityModel() ).thenReturn( true );
		assertEquals( BootstrapUiElements.FORM_GROUP, lookup( Client.class, ViewElementMode.FORM_READ ) );
		assertEquals( BootstrapUiElements.FORM_GROUP, lookup( Client.class, ViewElementMode.FORM_WRITE ) );

		when( clientConfig.getAttribute( EntityAttributes.IS_EMBEDDED_OBJECT ) ).thenReturn( true );
		assertEquals( BootstrapUiElements.FIELDSET, lookup( Client.class, ViewElementMode.FORM_READ ) );
		assertEquals( BootstrapUiElements.FIELDSET, lookup( Client.class, ViewElementMode.FORM_WRITE ) );

		when( clientConfig.hasEntityModel() ).thenReturn( false );
		when( clientConfig.getAttribute( EntityAttributes.IS_EMBEDDED_OBJECT ) ).thenReturn( false );
		assertEquals( BootstrapUiElements.FORM_GROUP, lookup( Client.class, ViewElementMode.FORM_READ ) );
		assertEquals( BootstrapUiElements.FORM_GROUP, lookup( Client.class, ViewElementMode.FORM_WRITE ) );
	}

	@Test
	public void labelForEmbeddedTypesWhenLabelRequested() {
		when( descriptor.getAttribute( EntityAttributes.IS_EMBEDDED_OBJECT ) ).thenReturn( true );

		assertEquals( BootstrapUiElements.LABEL, lookup( AtomicInteger.class, ViewElementMode.LABEL ) );
		assertEquals( BootstrapUiElements.LABEL, lookup( AtomicInteger.class, ViewElementMode.LIST_LABEL ) );
		assertEquals( BootstrapUiElements.LABEL, lookup( AtomicInteger.class, ViewElementMode.LABEL.forMultiple() ) );
		assertEquals( BootstrapUiElements.LABEL, lookup( AtomicInteger.class, ViewElementMode.LIST_LABEL.forMultiple() ) );
	}

	@Test
	public void dateTypeForDates() {
		assertEquals( BootstrapUiElements.DATETIME, lookup( Date.class, ViewElementMode.CONTROL ) );
		assertEquals( BootstrapUiElements.DATETIME, lookup( LocalDate.class, ViewElementMode.CONTROL ) );
		assertEquals( BootstrapUiElements.DATETIME, lookup( LocalTime.class, ViewElementMode.CONTROL ) );
		assertEquals( BootstrapUiElements.DATETIME, lookup( LocalDateTime.class, ViewElementMode.CONTROL ) );
		assertEquals( BootstrapUiElements.DATETIME, lookup( ZonedDateTime.class, ViewElementMode.CONTROL ) );
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
	public void enumValueShouldReturnValueForReadonly() {
		assertEquals( BootstrapUiElements.TEXT, lookup( CompanyStatus.class, ViewElementMode.VALUE ) );
		assertEquals( BootstrapUiElements.TEXT, lookup( CompanyStatus.class, ViewElementMode.LIST_VALUE ) );
	}

	@Test
	@SuppressWarnings("unchecked")
	public void singleEntityTypeShouldReturnSelectType() {
		EntityConfiguration clientConfig = mock( EntityConfiguration.class );
		when( clientConfig.hasEntityModel() ).thenReturn( true );

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
	@SuppressWarnings("unchecked")
	public void stringSetsAreSupportedAsMultiValue() {
		when( descriptor.getPropertyType() ).thenReturn( (Class) Set.class );
		TypeDescriptor collectionTypeDescriptor = TypeDescriptor.collection( Set.class, TypeDescriptor.valueOf( String.class ) );
		when( descriptor.getPropertyTypeDescriptor() ).thenReturn( collectionTypeDescriptor );

		assertEquals( MultiValueElementBuilderFactory.ELEMENT_TYPE, strategy.findElementType( descriptor, ViewElementMode.CONTROL ) );

		assertEquals( BootstrapUiElements.TEXT, strategy.findElementType( descriptor, ViewElementMode.VALUE ) );
		assertEquals( BootstrapUiElements.TEXT, strategy.findElementType( descriptor, ViewElementMode.LIST_VALUE ) );
	}

	@Test
	public void collectionEnumShouldReturnOptionsForRegularControl() {
		TypeDescriptor propertyType = TypeDescriptor.collection( Set.class, TypeDescriptor.valueOf( CompanyStatus.class ) );
		assertEquals( OptionsFormElementBuilderFactory.OPTIONS, lookup( propertyType, ViewElementMode.CONTROL ) );
	}

	@Test
	public void collectionEnumShouldReturnSelectForFilterControl() {
		TypeDescriptor propertyType = TypeDescriptor.collection( Set.class, TypeDescriptor.valueOf( CompanyStatus.class ) );
		assertEquals( BootstrapUiElements.SELECT, lookup( propertyType, ViewElementMode.FILTER_CONTROL ) );
	}

	@Test
	public void collectionEnumShouldReturnTextForReadonly() {
		TypeDescriptor propertyType = TypeDescriptor.collection( Set.class, TypeDescriptor.valueOf( CompanyStatus.class ) );
		assertEquals( BootstrapUiElements.TEXT, lookup( propertyType, ViewElementMode.VALUE ) );
		assertEquals( BootstrapUiElements.TEXT, lookup( propertyType, ViewElementMode.LIST_VALUE ) );
	}

	@Test
	public void unknownType() {
		assertNull( lookup( ViewElementMode.CONTROL ) );
		assertEquals( BootstrapUiElements.TEXT, lookup( ViewElementMode.VALUE ) );
	}

	@Test
	public void specificDomainTypeShouldReturnTextboxControlIfNoProperties() {
		assertEquals( BootstrapUiElements.FORM_GROUP, lookup( EmailType.class, ViewElementMode.FORM_READ ) );
		assertEquals( BootstrapUiElements.FORM_GROUP, lookup( EmailType.class, ViewElementMode.FORM_WRITE ) );
		assertEquals( BootstrapUiElements.TEXTBOX, lookup( EmailType.class, ViewElementMode.CONTROL ) );
	}

	@Test
	public void specificDomainTypeShouldReturnEmbeddedIfProperties() {
		assertEquals( BootstrapUiElements.FIELDSET, lookup( TypeWithProps.class, ViewElementMode.FORM_READ ) );
		assertEquals( BootstrapUiElements.FIELDSET, lookup( TypeWithProps.class, ViewElementMode.FORM_WRITE ) );
	}

	@Test
	public void specificDomainTypeShouldReturnEmbeddedIfMarkedAsEmbedded() {
		when( descriptor.getAttribute( EntityAttributes.IS_EMBEDDED_OBJECT ) ).thenReturn( true );
		assertEquals( BootstrapUiElements.FIELDSET, lookup( EmailType.class, ViewElementMode.FORM_READ ) );
		assertEquals( BootstrapUiElements.FIELDSET, lookup( EmailType.class, ViewElementMode.FORM_WRITE ) );
	}

	@Test
	@SuppressWarnings("unchecked")
	public void collectionIsEmbeddedIfPropertyIsExplicitlyEmbeddedEvenIfTargetIsRelatedEntity() {
		EntityPropertyDescriptor member = mock( EntityPropertyDescriptor.class );
		EntityPropertyRegistry rootRegistry = mock( EntityPropertyRegistry.class );

		when( descriptor.getName() ).thenReturn( "users" );
		when( descriptor.getPropertyType() ).thenReturn( (Class) List.class );
		when( descriptor.getPropertyTypeDescriptor() ).thenReturn( TypeDescriptor.valueOf( List.class ) );
		when( descriptor.getPropertyRegistry() ).thenReturn( rootRegistry );
		when( rootRegistry.getProperty( "users[]" ) ).thenReturn( member );
		when( member.getPropertyType() ).thenReturn( (Class) Client.class );

		assertEquals( EmbeddedCollectionOrMapElementBuilderFactory.ELEMENT_TYPE, strategy.findElementType( descriptor, ViewElementMode.CONTROL ) );
		assertEquals( EmbeddedCollectionOrMapElementBuilderFactory.ELEMENT_TYPE, strategy.findElementType( descriptor, ViewElementMode.VALUE ) );

		when( member.getAttribute( EntityAttributes.IS_EMBEDDED_OBJECT ) ).thenReturn( true );
		assertEquals( EmbeddedCollectionOrMapElementBuilderFactory.ELEMENT_TYPE, strategy.findElementType( descriptor, ViewElementMode.CONTROL ) );
		assertEquals( EmbeddedCollectionOrMapElementBuilderFactory.ELEMENT_TYPE, strategy.findElementType( descriptor, ViewElementMode.VALUE ) );

		when( member.getAttribute( EntityAttributes.IS_EMBEDDED_OBJECT ) ).thenReturn( false );
		assertEquals( OptionsFormElementBuilderFactory.OPTIONS, strategy.findElementType( descriptor, ViewElementMode.CONTROL ) );
		assertEquals( BootstrapUiElements.TEXT, strategy.findElementType( descriptor, ViewElementMode.VALUE ) );

		when( member.getAttribute( EntityAttributes.IS_EMBEDDED_OBJECT ) ).thenReturn( null );

		EntityConfiguration memberConfiguration = mock( EntityConfiguration.class );
		when( entityRegistry.getEntityConfiguration( Client.class ) ).thenReturn( memberConfiguration );
		when( memberConfiguration.hasEntityModel() ).thenReturn( true );
		assertEquals( OptionsFormElementBuilderFactory.OPTIONS, strategy.findElementType( descriptor, ViewElementMode.CONTROL ) );
		assertEquals( BootstrapUiElements.TEXT, strategy.findElementType( descriptor, ViewElementMode.VALUE ) );

		when( memberConfiguration.hasEntityModel() ).thenReturn( false );
		assertEquals( EmbeddedCollectionOrMapElementBuilderFactory.ELEMENT_TYPE, strategy.findElementType( descriptor, ViewElementMode.CONTROL ) );
		assertEquals( EmbeddedCollectionOrMapElementBuilderFactory.ELEMENT_TYPE, strategy.findElementType( descriptor, ViewElementMode.VALUE ) );

		when( memberConfiguration.hasEntityModel() ).thenReturn( true );
		when( memberConfiguration.getAttribute( EntityAttributes.IS_EMBEDDED_OBJECT ) ).thenReturn( true );
		assertEquals( EmbeddedCollectionOrMapElementBuilderFactory.ELEMENT_TYPE, strategy.findElementType( descriptor, ViewElementMode.CONTROL ) );
		assertEquals( EmbeddedCollectionOrMapElementBuilderFactory.ELEMENT_TYPE, strategy.findElementType( descriptor, ViewElementMode.VALUE ) );

		when( memberConfiguration.hasEntityModel() ).thenReturn( false );
		when( memberConfiguration.getAttribute( EntityAttributes.IS_EMBEDDED_OBJECT ) ).thenReturn( false );
		assertEquals( OptionsFormElementBuilderFactory.OPTIONS, strategy.findElementType( descriptor, ViewElementMode.CONTROL ) );
		assertEquals( BootstrapUiElements.TEXT, strategy.findElementType( descriptor, ViewElementMode.VALUE ) );
	}

	@SuppressWarnings("unchecked")
	private String lookup( Class propertyType, ViewElementMode mode ) {
		when( descriptor.getPropertyType() ).thenReturn( propertyType );
		when( descriptor.getPropertyTypeDescriptor() ).thenReturn( TypeDescriptor.valueOf( propertyType ) );
		return strategy.findElementType( descriptor, mode );
	}

	@SuppressWarnings("unchecked")
	private String lookup( TypeDescriptor propertyType, ViewElementMode mode ) {
		when( descriptor.getPropertyType() ).thenReturn( (Class) propertyType.getObjectType() );
		when( descriptor.getPropertyTypeDescriptor() ).thenReturn( propertyType );
		return strategy.findElementType( descriptor, mode );
	}

	private String lookup( ViewElementMode mode ) {
		return strategy.findElementType( descriptor, mode );
	}

	private static class EmailType
	{
	}

	private static class TypeWithProps
	{
		@Getter
		@Setter
		private String value;
	}

	@Configuration
	protected static class Config
	{

	}
}
