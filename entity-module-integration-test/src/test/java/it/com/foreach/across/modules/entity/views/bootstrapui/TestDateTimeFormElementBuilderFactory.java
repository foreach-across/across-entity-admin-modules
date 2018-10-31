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

import com.foreach.across.modules.bootstrapui.elements.DateTimeFormElement;
import com.foreach.across.modules.bootstrapui.elements.DateTimeFormElementConfiguration;
import com.foreach.across.modules.bootstrapui.elements.DateTimeFormElementConfiguration.Format;
import com.foreach.across.modules.bootstrapui.elements.GlyphIcon;
import com.foreach.across.modules.bootstrapui.elements.builder.DateTimeFormElementBuilder;
import com.foreach.across.modules.entity.EntityAttributes;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyBindingContext;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.views.EntityViewElementBuilderFactory;
import com.foreach.across.modules.entity.views.EntityViewElementBuilderFactoryHelper;
import com.foreach.across.modules.entity.views.EntityViewElementBuilderService;
import com.foreach.across.modules.entity.views.ViewElementMode;
import com.foreach.across.modules.entity.views.bootstrapui.DateTimeFormElementBuilderFactory;
import com.foreach.across.modules.entity.web.EntityViewModel;
import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.elements.TextViewElement;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.format.Printer;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.text.MessageFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * @author Arne Vandamme
 */
public class TestDateTimeFormElementBuilderFactory extends ViewElementBuilderFactoryTestSupport<ViewElement>
{
	private static final Date PRINT_DATE;
	private static final LocalDateTime PRINT_DATE_LOCAL_DATE_TIME = LocalDateTime.parse( "2015-08-07 10:31:22",
	                                                                                     DateTimeFormatter.ofPattern( "yyyy-MM-dd HH:mm:ss" ) );

	static {
		try {
			PRINT_DATE = DateUtils.parseDate( "2015-08-07 10:31:22", "yyyy-MM-dd HH:mm:ss" );
		}
		catch ( ParseException pe ) {
			throw new RuntimeException( pe );
		}
	}

	@Mock
	private EntityViewElementBuilderService entityViewElementBuilderService;

	@InjectMocks
	private EntityViewElementBuilderFactoryHelper builderFactoryHelper;

	@Override
	protected EntityViewElementBuilderFactory createBuilderFactory() {
		DateTimeFormElementBuilderFactory builderFactory = new DateTimeFormElementBuilderFactory();
		builderFactory.setBuilderFactoryHelpers( builderFactoryHelper );
		builderFactory.setViewElementBuilderService( entityViewElementBuilderService );
		return builderFactory;
	}

	@Override
	protected Class getTestClass() {
		return DateProperties.class;
	}

	@Test
	public void controlNamePrefixing() {
		simulateEntityViewForm();
		DateTimeFormElement datetime = assemble( "withoutAnnotations", ViewElementMode.CONTROL );
		assertEquals( "entity.withoutAnnotations", datetime.getControlName() );
	}

	@Test
	public void withoutAnnotations() {
		LocaleContextHolder.setLocale( Locale.UK );

		try {
			DateTimeFormElement datetime = assembleAndVerify( "withoutAnnotations", false );
			assertEquals( Format.DATETIME, datetime.getConfiguration().getFormat() );
			assertEquals( "en-GB", datetime.getConfiguration().get( "locale" ) );
			assertEquals( true, datetime.getConfiguration().get( "showClear" ) );
		}
		finally {
			LocaleContextHolder.resetLocaleContext();
		}
	}

	@Test
	public void required() {
		DateTimeFormElement datetime = assembleAndVerify( "required", true );
		assertEquals( Format.DATETIME, datetime.getConfiguration().getFormat() );
		assertEquals( false, datetime.getConfiguration().get( "showClear" ) );
	}

	@Test
	public void date() {
		DateTimeFormElement datetime = assembleAndVerify( "date", false );
		assertEquals( Format.DATE, datetime.getConfiguration().getFormat() );
	}

	@Test
	public void timeWithPastAnnotation() {
		DateTimeFormElement datetime = assembleAndVerify( "timeWithPast", false );
		assertEquals( Format.TIME, datetime.getConfiguration().getFormat() );
		assertTrue( datetime.getAddonAfter( GlyphIcon.class ).getGlyph().equals( GlyphIcon.TIME ) );
		assertNull( datetime.getConfiguration().get( "minDate" ) );
		assertNotNull( datetime.getConfiguration().get( "maxDate" ) );
	}

	@Test
	public void datetimeWithFutureAnnotation() {
		DateTimeFormElement datetime = assembleAndVerify( "datetimeWithFuture", false );
		assertEquals( Format.DATETIME, datetime.getConfiguration().getFormat() );
		assertTrue( datetime.getAddonAfter( GlyphIcon.class ).getGlyph().equals( GlyphIcon.CALENDAR ) );
		assertNotNull( datetime.getConfiguration().get( "minDate" ) );
		assertNull( datetime.getConfiguration().get( "maxDate" ) );
	}

	@Test
	public void preferredFormatConfiguredStillHasAnnotationsProcessed() {
		when( properties.get( "timeWithPast" ).hasAttribute( Format.class ) ).thenReturn( true );
		when( properties.get( "timeWithPast" ).getAttribute( Format.class ) )
				.thenReturn( Format.DATE );

		DateTimeFormElement datetime = assembleAndVerify( "timeWithPast", false );
		assertEquals( Format.DATE, datetime.getConfiguration().getFormat() );
		assertTrue( datetime.getAddonAfter( GlyphIcon.class ).getGlyph().equals( GlyphIcon.CALENDAR ) );
		assertNull( datetime.getConfiguration().get( "minDate" ) );
		assertNotNull( datetime.getConfiguration().get( "maxDate" ) );
	}

	@Test
	public void preferredConfigurationConfigured() {
		DateTimeFormElementConfiguration configuration = new DateTimeFormElementConfiguration();
		configuration.setFormat( Format.TIME );
		configuration.setShowClearButton( true );

		when( properties.get( "datetimeWithFuture" ).getAttribute( DateTimeFormElementConfiguration.class ) )
				.thenReturn( configuration );

		DateTimeFormElement datetime = assembleAndVerify( "datetimeWithFuture", false );
		assertEquals( Format.TIME, datetime.getConfiguration().getFormat() );
		assertTrue( datetime.getAddonAfter( GlyphIcon.class ).getGlyph().equals( GlyphIcon.TIME ) );
		assertNull( datetime.getConfiguration().get( "minDate" ) );
		assertNull( datetime.getConfiguration().get( "maxDate" ) );
		assertEquals( true, datetime.getConfiguration().get( "showClear" ) );
	}

	@Test
	public void valueSetFromEntity() throws ParseException {
		Date date = DateUtils.parseDate( "2015-08-07 10:31", "yyyy-MM-dd HH:mm" );

		when( properties.get( "date" ).getValueFetcher() ).thenReturn( entity -> date );
		when( builderContext.getAttribute( EntityViewModel.ENTITY ) ).thenReturn( "entity" );

		DateTimeFormElement datetime = assembleAndVerify( "date", false );
		assertEquals( date, datetime.getValue() );
	}

	@Test
	public void localDateValueSetFromEntity() {
		LocalDate date = PRINT_DATE_LOCAL_DATE_TIME.toLocalDate();

		when( properties.get( "date" ).getValueFetcher() ).thenReturn( entity -> date );
		when( builderContext.getAttribute( EntityViewModel.ENTITY ) ).thenReturn( "entity" );

		DateTimeFormElement datetime = assembleAndVerify( "date", false );
		assertEquals( date, datetime.getLocalDate() );
	}

	@Test
	public void localTimeValueSetFromEntity() {
		LocalTime date = PRINT_DATE_LOCAL_DATE_TIME.toLocalTime();

		when( properties.get( "date" ).getValueFetcher() ).thenReturn( entity -> date );
		when( builderContext.getAttribute( EntityViewModel.ENTITY ) ).thenReturn( "entity" );

		DateTimeFormElement datetime = assembleAndVerify( "date", false );
		assertEquals( date, datetime.getLocalTime() );
	}

	@Test
	public void localDateTimeValueSetFromEntity() {
		LocalDateTime date = PRINT_DATE_LOCAL_DATE_TIME;

		when( properties.get( "date" ).getValueFetcher() ).thenReturn( entity -> date );
		when( builderContext.getAttribute( EntityViewModel.ENTITY ) ).thenReturn( "entity" );

		DateTimeFormElement datetime = assembleAndVerify( "date", false );
		assertEquals( date, datetime.getLocalDateTime() );
	}

	@Test
	public void customLocaleSpecified() {
		LocaleContextHolder.setLocale( Locale.FRANCE );

		try {
			DateTimeFormElement datetime = assembleAndVerify( "withoutAnnotations", false );
			assertEquals( Format.DATETIME, datetime.getConfiguration().getFormat() );
			assertEquals( "fr-FR", datetime.getConfiguration().get( "locale" ) );
		}
		finally {
			LocaleContextHolder.resetLocaleContext();
		}
	}

	@Test
	public void valueOrderIsPrinterFormatDateTimeConfigurationAndConversionService() {
		LocaleContextHolder.setLocale( Locale.UK );

		try {
			when( builderContext.getAttribute( EntityViewModel.ENTITY ) ).thenReturn( "entity" );
			when( properties.get( "required" ).getController().fetchValue( EntityPropertyBindingContext.forReading( "entity" ) ) ).thenReturn( PRINT_DATE );

			TextViewElement text = assembleValue( "required" );
			assertEquals( "07-Aug-2015 10:31", text.getText() );

			DateTimeFormElementConfiguration configuration = new DateTimeFormElementConfiguration( Format.DATE );
			when( properties.get( "required" ).hasAttribute( DateTimeFormElementConfiguration.class ) )
					.thenReturn( true );
			when( properties.get( "required" ).getAttribute( DateTimeFormElementConfiguration.class ) )
					.thenReturn( configuration );
			assertEquals( "07-Aug-2015", assembleValue( "required" ).getText() );

			DateTimeFormElementConfiguration timeConfiguration = new DateTimeFormElementConfiguration( Format.TIME );
			DateTimeFormElementBuilder builder = new DateTimeFormElementBuilder().configuration( timeConfiguration );
			when( entityViewElementBuilderService.getElementBuilder(
					properties.get( "required" ), ViewElementMode.CONTROL ) ).thenReturn( builder );
			assertEquals( "10:31", assembleValue( "required" ).getText() );

			java.text.Format format = new MessageFormat( "messageFormat" );
			when( properties.get( "required" ).hasAttribute( java.text.Format.class ) ).thenReturn( true );
			when( properties.get( "required" ).getAttribute( java.text.Format.class ) ).thenReturn( format );
			assertEquals( "messageFormat", assembleValue( "required" ).getText() );

			Printer printer = mock( Printer.class );
			when( printer.print( any(), any() ) ).thenReturn( "printer" );
			when( properties.get( "required" ).hasAttribute( Printer.class ) ).thenReturn( true );
			when( properties.get( "required" ).getAttribute( Printer.class ) ).thenReturn( printer );
			assertEquals( "printer", assembleValue( "required" ).getText() );
		}
		finally {
			LocaleContextHolder.resetLocaleContext();
		}
	}

	@Test
	public void supportedTemporalAccessorsAreCorrectlyPrinterFormattedNoFormatSet() {
		assertPrinterFormat( "localDateTime", PRINT_DATE_LOCAL_DATE_TIME, "07-Aug-2015 10:31" );
		assertPrinterFormat( "localDate", PRINT_DATE_LOCAL_DATE_TIME.toLocalDate(), "07-Aug-2015" );
		assertPrinterFormat( "localTime", PRINT_DATE_LOCAL_DATE_TIME.toLocalTime(), "10:31" );
	}

	@Test
	public void supportedTemporalAccessorsAreCorrectlyPrinterFormattedWithFormatSet() {
		assertPrinterFormat( "localDateTime", PRINT_DATE_LOCAL_DATE_TIME.toLocalDate(), Format.DATE, "07-Aug-2015" );
		assertPrinterFormat( "localDateTime", PRINT_DATE_LOCAL_DATE_TIME.toLocalTime(), Format.TIME, "10:31" );
	}

	private void assertPrinterFormat( String name, Object value, String expected ) {
		assertPrinterFormat( name, value, null, expected );
	}

	private void assertPrinterFormat( String name, Object value, DateTimeFormElementConfiguration.Format format, String expected ) {
		LocaleContextHolder.setLocale( Locale.UK );

		try {
			when( builderContext.getAttribute( EntityViewModel.ENTITY ) ).thenReturn( "entity" );
			when( properties.get( name ).getController().fetchValue( EntityPropertyBindingContext.forReading( "entity" ) ) ).thenReturn( value );
			if ( format != null ) {
				when( properties.get( name ).hasAttribute( Format.class ) ).thenReturn( true );
				when( properties.get( name ).getAttribute( Format.class ) ).thenReturn( format );
			}

			TextViewElement text = assembleValue( name );
			assertEquals( expected, text.getText() );
		}
		finally {
			LocaleContextHolder.resetLocaleContext();
		}
	}

	private TextViewElement assembleValue( String propertyName ) {
		return (TextViewElement) assemble( propertyName, ViewElementMode.VALUE );
	}

	@SuppressWarnings("unchecked")
	private <V> V assembleAndVerify( String propertyName, boolean required ) {
		if ( required ) {
			EntityPropertyDescriptor descriptor = properties.get( propertyName );
			when( descriptor.getAttribute( EntityAttributes.PROPERTY_REQUIRED, Boolean.class ) ).thenReturn( true );
		}
		DateTimeFormElement control = assemble( propertyName, ViewElementMode.CONTROL );
		assertEquals( propertyName, control.getName() );
		assertEquals( propertyName, control.getControlName() );
		assertFalse( control.isReadonly() );
		assertFalse( control.isDisabled() );
		assertEquals( required, control.isRequired() );

		return (V) control;
	}

	@SuppressWarnings("unused")
	private static class DateProperties
	{
		public Date withoutAnnotations;

		@NotNull
		public Date required;

		@NotNull
		public LocalDate localDate;

		@NotNull
		public LocalTime localTime;

		@NotNull
		public LocalDateTime localDateTime;

		@Future
		@Temporal(TemporalType.TIMESTAMP)
		public Date datetimeWithFuture;

		@Past
		@Temporal(TemporalType.TIME)
		public Date timeWithPast;

		@Temporal(TemporalType.DATE)
		public Date date;
	}
}
