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

import com.foreach.across.modules.bootstrapui.elements.*;
import com.foreach.across.modules.bootstrapui.elements.builder.OptionFormElementBuilder;
import com.foreach.across.modules.entity.EntityAttributes;
import com.foreach.across.modules.entity.support.EntityMessageCodeResolver;
import com.foreach.across.modules.entity.views.ViewElementMode;
import com.foreach.across.modules.entity.views.bootstrapui.OptionsFormElementBuilderFactory;
import com.foreach.across.modules.entity.views.bootstrapui.options.FixedOptionIterableBuilder;
import com.foreach.across.modules.entity.views.bootstrapui.options.OptionIterableBuilder;
import com.foreach.across.modules.entity.views.request.EntityViewCommand;
import com.foreach.across.modules.web.ui.elements.AbstractNodeViewElement;
import com.foreach.across.modules.web.ui.elements.ContainerViewElement;
import com.foreach.common.test.MockedLoader;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.convert.ConversionService;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import java.util.EnumSet;
import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Arne Vandamme
 */
@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext
@ContextConfiguration(classes = TestOptionsFormElementBuilderFactory.Config.class, loader = MockedLoader.class)
public class TestOptionsFormElementBuilderFactory extends ViewElementBuilderFactoryTestSupport<AbstractNodeViewElement>
{
	@Override
	protected Class getTestClass() {
		return Validators.class;
	}

	@Test
	public void controlNamePrefixingSelect() {
		when( builderContext.hasAttribute( EntityViewCommand.class ) ).thenReturn( true );
		SelectFormElement select = assemble( "enumNoValidator", ViewElementMode.CONTROL );
		assertEquals( "entity.enumNoValidator", select.getControlName() );
		assertEquals( "entity.enumNoValidator", select.getHtmlId() );
	}

	@Test
	public void controlNamePrefixingRadio() {
		when( builderContext.hasAttribute( EntityViewCommand.class ) ).thenReturn( true );

		ContainerViewElement container = assemble( "enumNoValidator", ViewElementMode.CONTROL, BootstrapUiElements.RADIO );

		assertEquals(
				2,
				container.findAll( RadioFormElement.class )
				         .filter( e -> e.getControlName().startsWith( "entity." ) && e.getHtmlId().startsWith( "entity." ) )
				         .count()
		);
	}

	@Test
	public void controlNamePrefixingCheckbox() {
		when( builderContext.hasAttribute( EntityViewCommand.class ) ).thenReturn( true );
		ContainerViewElement container = assemble( "enumMultiple", ViewElementMode.CONTROL );

		assertEquals(
				2,
				container.findAll( CheckboxFormElement.class )
				         .filter( e -> CheckboxFormElement.class.equals( e.getClass() ) )
				         .filter( e -> e.getControlName().startsWith( "entity." ) && e.getHtmlId().startsWith( "entity." ) )
				         .count()
		);
	}

	@Test
	public void selectInsteadOfCheckboxDueToSelectFormElementConfiguration() {
		when( builderContext.hasAttribute( EntityViewCommand.class ) ).thenReturn( true );

		SelectFormElementConfiguration configuration = SelectFormElementConfiguration.liveSearch().setActionsBox( true );
		when( properties.get( "enumMultiple" ).getAttribute( SelectFormElementConfiguration.class ) )
				.thenReturn( configuration );

		SelectFormElement select = assemble( "enumMultiple", ViewElementMode.CONTROL );
		assertTrue( select.isMultiple() );
		assertNotNull( select.getConfiguration() );
		assertEquals( true, select.getConfiguration().get( "actionsBox" ) );
	}

	@Test
	public void selectSpecifiedAsType() {
		when( builderContext.hasAttribute( EntityViewCommand.class ) ).thenReturn( true );

		SelectFormElement select = assemble( "enumMultiple", ViewElementMode.CONTROL, BootstrapUiElements.SELECT );
		assertTrue( select.isMultiple() );
		assertNotNull( select.getConfiguration() );
	}

	@Test
	public void fixedTypeTakesPrecedenceOverSelectFormElementConfiguration() {
		when( builderContext.hasAttribute( EntityViewCommand.class ) ).thenReturn( true );

		SelectFormElementConfiguration configuration = SelectFormElementConfiguration.liveSearch().setActionsBox( true );
		when( properties.get( "enumMultiple" ).getAttribute( SelectFormElementConfiguration.class ) )
				.thenReturn( configuration );

		ContainerViewElement container = assemble( "enumMultiple", ViewElementMode.CONTROL, BootstrapUiElements.MULTI_CHECKBOX );
		assertFalse( container instanceof SelectFormElement );
	}

	@Test
	public void enumNoValidator() {
		SelectFormElement select = assembleAndVerify( "enumNoValidator" );

		assertFalse( select.isRequired() );
		assertFalse( select.isMultiple() );
		assertEquals( 1, select.getChildren().size() );

		ContainerViewElement options = (ContainerViewElement) select.getChildren().get( 0 );
		assertEquals( 3, options.getChildren().size() );
	}

	@Test
	public void enumNotNullValidator() {
		SelectFormElement select = assembleAndVerify( "enumNotNullValidator" );

		assertTrue( select.isRequired() );
		assertFalse( select.isMultiple() );
		assertEquals( 1, select.getChildren().size() );
	}

	@Test
	public void enumManyToOneOptional() {
		SelectFormElement select = assembleAndVerify( "enumManyToOneOptional" );

		assertFalse( select.isRequired() );
		assertFalse( select.isMultiple() );
		assertEquals( 1, select.getChildren().size() );
	}

	@Test
	public void enumManyToOneNonOptional() {
		SelectFormElement select = assembleAndVerify( "enumManyToOneNonOptional" );

		assertTrue( select.isRequired() );
		assertFalse( select.isMultiple() );
		assertEquals( 1, select.getChildren().size() );
	}

	@Test
	public void allowedValuesSpecified() {
		when( properties.get( "enumNoValidator" ).getAttribute( EntityAttributes.OPTIONS_ALLOWED_VALUES ) )
				.thenReturn( EnumSet.of( TestEnum.TWO ) );

		SelectFormElement select = assembleAndVerify( "enumNoValidator" );

		assertFalse( select.isRequired() );
		assertFalse( select.isMultiple() );
		assertEquals( 1, select.getChildren().size() );

		ContainerViewElement options = (ContainerViewElement) select.getChildren().get( 0 );
		assertEquals( 2, options.getChildren().size() );

		SelectFormElement.Option option = (SelectFormElement.Option) options.getChildren().get( 1 );
		assertEquals( "TWO", option.getValue() );
	}

	@Test
	public void fixedIterableOnPropertyIsUsed() {
		OptionIterableBuilder optionBuilder = new FixedOptionIterableBuilder( new OptionFormElementBuilder().label( "test" ).value( "fixed" ).selected() );
		when( properties.get( "enumNoValidator" ).getAttribute( OptionIterableBuilder.class ) )
				.thenReturn( optionBuilder );

		SelectFormElement select = assembleAndVerify( "enumNoValidator" );

		assertFalse( select.isRequired() );
		assertFalse( select.isMultiple() );
		assertEquals( 1, select.getChildren().size() );

		SelectFormElement.Option option = (SelectFormElement.Option) ( (ContainerViewElement) select.getChildren().get( 0 ) ).getChildren().get( 1 );
		assertTrue( option.isSelected() );
		assertEquals( "test", option.getLabel() );
		assertEquals( "fixed", option.getValue() );
	}

	@SuppressWarnings("unchecked")
	private <V> V assembleAndVerify( String propertyName ) {
		EntityMessageCodeResolver codeResolver = mock( EntityMessageCodeResolver.class );
		when( builderContext.getAttribute( EntityMessageCodeResolver.class ) ).thenReturn( codeResolver );

		AbstractNodeViewElement control = assemble( propertyName, ViewElementMode.CONTROL );

		return (V) control;
	}

	@SuppressWarnings("unused")
	private enum TestEnum
	{
		ONE,
		TWO
	}

	@SuppressWarnings("unused")
	private static class Validators
	{
		public TestEnum enumNoValidator;

		@NotNull
		public TestEnum enumNotNullValidator;

		@Column
		public TestEnum enumManyToOneOptional;

		@Column(nullable = false)
		public TestEnum enumManyToOneNonOptional;

		public Set<TestEnum> enumMultiple;
	}

	@Configuration
	protected static class Config
	{
		@Bean
		@Primary
		public OptionsFormElementBuilderFactory optionsFormElementBuilderFactory() {
			return new OptionsFormElementBuilderFactory();
		}

		@Bean
		public BootstrapUiFactory bootstrapUiFactory() {
			return new BootstrapUiFactoryImpl();
		}

		@Bean
		public ConversionService conversionService() {
			return mock( ConversionService.class );
		}
	}
}
