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

import com.foreach.across.modules.bootstrapui.elements.NumericFormElement;
import com.foreach.across.modules.bootstrapui.elements.NumericFormElementConfiguration;
import com.foreach.across.modules.bootstrapui.elements.builder.NumericFormElementBuilder;
import com.foreach.across.modules.entity.EntityAttributes;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyBindingContext;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.views.EntityViewElementBuilderFactory;
import com.foreach.across.modules.entity.views.EntityViewElementBuilderFactoryHelper;
import com.foreach.across.modules.entity.views.EntityViewElementBuilderService;
import com.foreach.across.modules.entity.views.ViewElementMode;
import com.foreach.across.modules.entity.views.bootstrapui.NumericFormElementBuilderFactory;
import com.foreach.across.modules.entity.web.EntityViewModel;
import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.elements.TextViewElement;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.format.Printer;
import org.springframework.format.annotation.NumberFormat;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.text.Format;
import java.text.MessageFormat;
import java.util.Currency;
import java.util.Locale;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * @author Arne Vandamme
 */
public class TestNumericFormElementBuilderFactory extends ViewElementBuilderFactoryTestSupport<ViewElement>
{
	@Mock
	private ConversionService mvcConversionService;

	@Mock
	private EntityViewElementBuilderService entityViewElementBuilderService;

	@InjectMocks
	private EntityViewElementBuilderFactoryHelper builderFactoryHelper;

	@Override
	protected EntityViewElementBuilderFactory createBuilderFactory() {
		NumericFormElementBuilderFactory builderFactory = new NumericFormElementBuilderFactory();
		builderFactory.setBuilderFactoryHelpers( builderFactoryHelper );
		builderFactory.setViewElementBuilderService( entityViewElementBuilderService );
		return builderFactory;
	}

	@Override
	protected Class getTestClass() {
		return NumericProperties.class;
	}

	@Test
	public void controlNamePrefixing() {
		simulateEntityViewForm();
		NumericFormElement numeric = assemble( "withoutAnnotations", ViewElementMode.CONTROL );
		assertEquals( "entity.withoutAnnotations", numeric.getControlName() );
	}

	@Test
	public void withoutAnnotations() {
		NumericFormElement numeric = assembleControl( "withoutAnnotations", false );
		assertNull( numeric.getConfiguration() );
	}

	@Test
	public void required() {
		NumericFormElement numeric = assembleControl( "required", true );
		assertNull( numeric.getConfiguration() );
	}

	@Test
	public void defaultDecimal() {
		NumericFormElement numeric = assembleControl( "decimal", false );
		assertNull( numeric.getConfiguration() );
	}

	@Test
	public void currencyInteger() {
		when( properties.get( "withoutAnnotations" ).hasAttribute( Currency.class ) ).thenReturn( true );
		when( properties.get( "withoutAnnotations" ).getAttribute( Currency.class ) )
				.thenReturn( Currency.getInstance( "EUR" ) );

		NumericFormElement numeric = assembleControl( "withoutAnnotations", false );

		NumericFormElementConfiguration configuration = numeric.getConfiguration();
		assertNotNull( configuration );
		assertEquals( 0, configuration.get( "mDec" ) );
		assertEquals( Currency.getInstance( "EUR" ), configuration.getCurrency() );
	}

	@Test
	public void percentageLong() {
		when( properties.get( "required" ).hasAttribute( NumericFormElementConfiguration.Format.class ) )
				.thenReturn( true );
		when( properties.get( "required" ).getAttribute( NumericFormElementConfiguration.Format.class ) )
				.thenReturn( NumericFormElementConfiguration.Format.PERCENT );

		NumericFormElement numeric = assembleControl( "required", true );

		NumericFormElementConfiguration configuration = numeric.getConfiguration();
		assertNotNull( configuration );
		assertEquals( 0, configuration.get( "mDec" ) );
		assertEquals( NumericFormElementConfiguration.Format.PERCENT, configuration.getFormat() );
		assertEquals( 1, configuration.getMultiplier() );
	}

	@Test
	public void manualConfiguration() {
		NumericFormElementConfiguration configuration = new NumericFormElementConfiguration();
		configuration.setDecimalPositions( 6 );
		configuration.setLocalizeDecimalSymbols( false );

		when( properties.get( "decimal" ).hasAttribute( NumericFormElementConfiguration.class ) )
				.thenReturn( true );
		when( properties.get( "decimal" ).getAttribute( NumericFormElementConfiguration.class ) )
				.thenReturn( configuration );

		NumericFormElement numeric = assembleControl( "decimal", false );
		assertEquals( configuration, numeric.getConfiguration() );
	}

	@Test
	public void currencyNumberFormat() {
		LocaleContextHolder.setLocale( Locale.forLanguageTag( "nl-BE" ) );

		NumericFormElement numeric = assembleControl( "currency", false );
		NumericFormElementConfiguration configuration = numeric.getConfiguration();
		assertNotNull( configuration );
		assertEquals( 2, configuration.get( "mDec" ) );
		assertEquals( Currency.getInstance( "EUR" ), configuration.getCurrency() );
	}

	@Test
	public void percentNumberFormat() {
		LocaleContextHolder.setLocale( Locale.forLanguageTag( "nl-BE" ) );

		NumericFormElement numeric = assembleControl( "percent", false );
		NumericFormElementConfiguration configuration = numeric.getConfiguration();
		assertNotNull( configuration );
		assertEquals( 2, configuration.get( "mDec" ) );
		assertEquals( NumericFormElementConfiguration.Format.PERCENT, configuration.getFormat() );
		assertEquals( 100, configuration.getMultiplier() );
	}

	@Test
	public void valueOrderIsPrinterFormatNumericConfigurationAndConversionService() {
		when( builderContext.getAttribute( EntityViewModel.ENTITY ) ).thenReturn( "entity" );
		when( properties.get( "decimal" ).getController().fetchValue( EntityPropertyBindingContext.forReading( "entity" ) ) ).thenReturn( 123L );
		when( mvcConversionService.canConvert( any( TypeDescriptor.class ), any() ) ).thenReturn( true );
		when( mvcConversionService.convert( eq( 123L ), any(), any() ) ).thenReturn( "fromConversionService" );

		TextViewElement text = assembleValue( "decimal" );
		assertEquals( "fromConversionService", text.getText() );

		NumericFormElementConfiguration configuration = new NumericFormElementConfiguration();
		configuration.setDecimalPositions( 0 );
		when( properties.get( "decimal" ).hasAttribute( NumericFormElementConfiguration.class ) ).thenReturn( true );
		when( properties.get( "decimal" ).getAttribute( NumericFormElementConfiguration.class ) )
				.thenReturn( configuration );
		assertEquals( "123", assembleValue( "decimal" ).getText() );

		NumericFormElementConfiguration decimalConfig = new NumericFormElementConfiguration();
		decimalConfig.setDecimalPositions( 2 );
		decimalConfig.setDecimalSeparator( '|' );
		decimalConfig.setLocalizeDecimalSymbols( false );
		NumericFormElementBuilder builder = new NumericFormElementBuilder().configuration( decimalConfig );
		when( entityViewElementBuilderService.getElementBuilder(
				properties.get( "decimal" ), ViewElementMode.CONTROL ) ).thenReturn( builder );
		assertEquals( "123|00", assembleValue( "decimal" ).getText() );

		Format format = new MessageFormat( "messageFormat" );
		when( properties.get( "decimal" ).hasAttribute( Format.class ) ).thenReturn( true );
		when( properties.get( "decimal" ).getAttribute( Format.class ) ).thenReturn( format );
		assertEquals( "messageFormat", assembleValue( "decimal" ).getText() );

		Printer printer = mock( Printer.class );
		when( printer.print( any(), any() ) ).thenReturn( "printer" );
		when( properties.get( "decimal" ).hasAttribute( Printer.class ) ).thenReturn( true );
		when( properties.get( "decimal" ).getAttribute( Printer.class ) ).thenReturn( printer );
		assertEquals( "printer", assembleValue( "decimal" ).getText() );
	}

	private TextViewElement assembleValue( String propertyName ) {
		return (TextViewElement) assemble( propertyName, ViewElementMode.VALUE );
	}

	@SuppressWarnings("unchecked")
	private <V> V assembleControl( String propertyName, boolean required ) {
		if ( required ) {
			EntityPropertyDescriptor descriptor = properties.get( propertyName );
			when( descriptor.getAttribute( EntityAttributes.PROPERTY_REQUIRED, Boolean.class ) ).thenReturn( true );
		}
		NumericFormElement control = assemble( propertyName, ViewElementMode.CONTROL );
		assertEquals( propertyName, control.getName() );
		assertEquals( propertyName, control.getControlName() );
		assertFalse( control.isReadonly() );
		assertFalse( control.isDisabled() );
		assertEquals( required, control.isRequired() );

		return (V) control;
	}

	@SuppressWarnings("unused")
	private static class NumericProperties
	{
		public int withoutAnnotations;

		@NotNull
		public Long required;

		public BigDecimal decimal;

		@NumberFormat(style = NumberFormat.Style.CURRENCY)
		public BigDecimal currency;

		@NumberFormat(style = NumberFormat.Style.PERCENT)
		public BigDecimal percent;
	}
}
