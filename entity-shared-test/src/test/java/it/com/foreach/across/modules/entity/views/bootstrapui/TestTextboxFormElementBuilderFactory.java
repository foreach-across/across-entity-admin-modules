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

import com.foreach.across.modules.bootstrapui.elements.BootstrapUiFactory;
import com.foreach.across.modules.bootstrapui.elements.BootstrapUiFactoryImpl;
import com.foreach.across.modules.bootstrapui.elements.TextareaFormElement;
import com.foreach.across.modules.bootstrapui.elements.TextboxFormElement;
import com.foreach.across.modules.entity.registry.EntityRegistry;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.views.EntityViewElementBuilderFactoryHelper;
import com.foreach.across.modules.entity.views.ViewElementMode;
import com.foreach.across.modules.entity.views.bootstrapui.TextboxFormElementBuilderFactory;
import com.foreach.across.modules.entity.views.request.EntityViewCommand;
import com.foreach.across.modules.entity.views.support.ValueFetcher;
import com.foreach.across.modules.entity.web.EntityViewModel;
import com.foreach.common.test.MockedLoader;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Arne Vandamme
 */
@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext
@ContextConfiguration(classes = TestTextboxFormElementBuilderFactory.Config.class, loader = MockedLoader.class)
public class TestTextboxFormElementBuilderFactory extends ViewElementBuilderFactoryTestSupport<TextboxFormElement>
{
	@Autowired
	private ConversionService conversionService;

	@Override
	protected Class getTestClass() {
		return Validators.class;
	}

	@Test
	public void controlNamePrefixing() {
		when( builderContext.hasAttribute( EntityViewCommand.class ) ).thenReturn( true );
		TextboxFormElement textbox = assemble( "noValidatorNumber", ViewElementMode.CONTROL );
		assertEquals( "entity.noValidatorNumber", textbox.getControlName() );
	}

	@Test
	public void noValidator() {
		TextareaFormElement textarea = assembleAndVerify( "noValidator", false );
		assertEquals( TextareaFormElement.Type.TEXTAREA, textarea.getType() );
		assertNull( textarea.getMaxLength() );
	}

	@Test
	public void noValidatorNumber() {
		TextboxFormElement textbox = assembleAndVerify( "noValidatorNumber", false );
		assertEquals( TextboxFormElement.Type.TEXT, textbox.getType() );
		assertNull( textbox.getMaxLength() );
	}

	@Test
	public void notNullValidator() {
		TextareaFormElement textarea = assembleAndVerify( "notNullValidator", true );
		assertNull( textarea.getMaxLength() );
	}

	@Test
	public void notBlankValidator() {
		TextareaFormElement textarea = assembleAndVerify( "notBlankValidator", true );
		assertNull( textarea.getMaxLength() );
	}

	@Test
	public void notEmptyValidator() {
		TextareaFormElement textarea = assembleAndVerify( "notEmptyValidator", true );
		assertNull( textarea.getMaxLength() );
	}

	@Test
	public void sizeValidator() {
		TextboxFormElement textbox = assembleAndVerify( "sizeValidator", false );
		assertEquals( Integer.valueOf( 200 ), textbox.getMaxLength() );
		assertFalse( textbox instanceof TextareaFormElement );
	}

	@Test
	public void sizeForMultiLineValidator() {
		TextareaFormElement textarea = assembleAndVerify( "sizeForMultiLineValidator", false );
		assertEquals( Integer.valueOf( 201 ), textarea.getMaxLength() );
	}

	@Test
	public void multilineForcedToTextbox() {
		when( properties.get( "sizeForMultiLineValidator" ).hasAttribute( TextboxFormElement.Type.class ) )
				.thenReturn( true );
		when( properties.get( "sizeForMultiLineValidator" ).getAttribute( TextboxFormElement.Type.class ) )
				.thenReturn( TextboxFormElement.Type.TEXT );

		TextboxFormElement textbox = assembleAndVerify( "sizeForMultiLineValidator", false );
		assertFalse( textbox instanceof TextareaFormElement );
	}

	@Test
	public void singlelineForcedToTextarea() {
		when( properties.get( "sizeValidator" ).hasAttribute( TextboxFormElement.Type.class ) )
				.thenReturn( true );
		when( properties.get( "sizeValidator" ).getAttribute( TextboxFormElement.Type.class ) )
				.thenReturn( TextareaFormElement.Type.TEXTAREA );

		TextareaFormElement textarea = assembleAndVerify( "sizeValidator", false );
		assertEquals( 1, textarea.getRows() );
		assertNotNull( textarea );
	}

	@Test
	public void singlelineForcedToBootstrapTextarea() {
		TextareaFormElement control = assemble( "sizeValidator", ViewElementMode.CONTROL, TextareaFormElement.ELEMENT_TYPE );
		assertEquals( "sizeValidator", control.getName() );
		assertEquals( "sizeValidator", control.getControlName() );
		assertFalse( control.isReadonly() );
		assertFalse( control.isDisabled() );
		assertFalse( control.isRequired() );

		assertNotNull( control );
		assertEquals( 1, control.getRows() );
	}

	@Test
	public void lengthValidator() {
		TextboxFormElement textbox = assembleAndVerify( "lengthValidator", false );
		assertEquals( Integer.valueOf( 10 ), textbox.getMaxLength() );
		assertFalse( textbox instanceof TextareaFormElement );
	}

	@Test
	public void combinedValidator() {
		TextboxFormElement textbox = assembleAndVerify( "combinedValidator", true );
		assertEquals( Integer.valueOf( 50 ), textbox.getMaxLength() );
		assertFalse( textbox instanceof TextareaFormElement );
	}

	@Test
	public void preferredTypeCanBeSet() {
		when( properties.get( "noValidator" ).hasAttribute( TextboxFormElement.Type.class ) ).thenReturn( true );
		when( properties.get( "noValidator" ).getAttribute( TextboxFormElement.Type.class ) )
				.thenReturn( TextboxFormElement.Type.DATETIME );

		TextboxFormElement textbox = assembleAndVerify( "noValidator", false );
		assertEquals( TextboxFormElement.Type.DATETIME, textbox.getType() );
	}

	@Test
	public void emailType() {
		TextboxFormElement textbox = assembleAndVerify( "email", false );
		assertEquals( TextboxFormElement.Type.EMAIL, textbox.getType() );
	}

	@Test
	public void passwordType() {
		EntityPropertyDescriptor descriptor = properties.get( "password" );
		when( descriptor.hasAttribute( TextboxFormElement.Type.class ) ).thenReturn( true );
		when( descriptor.getAttribute( TextboxFormElement.Type.class ) ).thenReturn( TextboxFormElement.Type.PASSWORD );

		TextboxFormElement textbox = assembleAndVerify( "password", false );
		assertEquals( TextboxFormElement.Type.PASSWORD, textbox.getType() );
	}

	@Test
	public void valueSetFromEntity() {
		when( properties.get( "noValidator" ).getValueFetcher() ).thenReturn( new ValueFetcher()
		{
			@Override
			public Object getValue( Object entity ) {
				return "fetchedValue";
			}
		} );

		when( conversionService.convert( eq( "fetchedValue" ), any( TypeDescriptor.class ),
		                                 any( TypeDescriptor.class ) ) )
				.thenReturn( "converted-value" );

		when( builderContext.getAttribute( EntityViewModel.ENTITY ) ).thenReturn( "entity" );

		TextareaFormElement textarea = assembleAndVerify( "noValidator", false );
		assertEquals( "converted-value", textarea.getText() );
	}

	@SuppressWarnings("unchecked")
	private <V> V assembleAndVerify( String propertyName, boolean required ) {
		TextboxFormElement control = assemble( propertyName, ViewElementMode.CONTROL );
		assertEquals( propertyName, control.getName() );
		assertEquals( propertyName, control.getControlName() );
		assertFalse( control.isReadonly() );
		assertFalse( control.isDisabled() );
		assertEquals( required, control.isRequired() );

		return (V) control;
	}

	private static class Validators
	{
		public String noValidator;

		public int noValidatorNumber;

		@NotNull
		public String notNullValidator;

		@NotBlank
		public String notBlankValidator;

		@NotEmpty
		public String notEmptyValidator;

		@Size(min = 5, max = 200)
		public String sizeValidator;

		@Size(max = 201)
		public String sizeForMultiLineValidator;

		@Length(min = 1, max = 10)
		public String lengthValidator;

		@NotBlank
		@Size(max = 50)
		public String combinedValidator;

		@Email
		public String email;

		public String password;
	}

	@Configuration
	protected static class Config
	{
		@Bean
		@Primary
		public TextboxFormElementBuilderFactory textboxFormElementBuilderFactory() {
			TextboxFormElementBuilderFactory factory = new TextboxFormElementBuilderFactory();
			factory.setMaximumSingleLineLength( 200 );

			return factory;
		}

		@Bean
		public BootstrapUiFactory bootstrapUiFactory() {
			return new BootstrapUiFactoryImpl();
		}

		@Bean
		public ConversionService conversionService() {
			return mock( ConversionService.class );
		}

		@Bean
		public EntityRegistry entityRegistry() {
			return mock( EntityRegistry.class );
		}

		@Bean
		public EntityViewElementBuilderFactoryHelper builderFactoryHelper() {
			return new EntityViewElementBuilderFactoryHelper();
		}
	}
}
