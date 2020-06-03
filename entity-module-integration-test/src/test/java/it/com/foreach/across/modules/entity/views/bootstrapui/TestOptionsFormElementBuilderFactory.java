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
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.support.EntityMessageCodeResolver;
import com.foreach.across.modules.entity.views.EntityViewElementBuilderFactory;
import com.foreach.across.modules.entity.views.ViewElementMode;
import com.foreach.across.modules.entity.views.bootstrapui.OptionsFormElementBuilderFactory;
import com.foreach.across.modules.entity.views.bootstrapui.options.FixedOptionIterableBuilder;
import com.foreach.across.modules.entity.views.bootstrapui.options.OptionGenerator;
import com.foreach.across.modules.entity.views.bootstrapui.options.OptionIterableBuilder;
import com.foreach.across.modules.entity.views.request.EntityViewCommand;
import com.foreach.across.modules.web.ui.elements.AbstractNodeViewElement;
import com.foreach.across.modules.web.ui.elements.ContainerViewElement;
import org.junit.Test;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * @author Arne Vandamme
 */
public class TestOptionsFormElementBuilderFactory extends ViewElementBuilderFactoryTestSupport<AbstractNodeViewElement>
{
	@Override
	protected EntityViewElementBuilderFactory createBuilderFactory() {
		OptionsFormElementBuilderFactory builderFactory = new OptionsFormElementBuilderFactory();
		builderFactory.setEntityRegistry( entityRegistry );
		return builderFactory;
	}

	@Override
	protected Class getTestClass() {
		return Validators.class;
	}

	@Test
	public void controlNamePrefixingSelect() {
		simulateEntityViewForm();
		SelectFormElement select = assemble( "singleValue", ViewElementMode.CONTROL );
		assertEquals( "entity.singleValue", select.getControlName() );
		assertEquals( "entity.singleValue", select.getHtmlId() );
	}

	@Test
	public void controlNamePrefixingRadio() {
		simulateEntityViewForm();

		ContainerViewElement container = assemble( "singleValue", ViewElementMode.CONTROL, BootstrapUiElements.RADIO );

		assertEquals(
				3,
				container.findAll( RadioFormElement.class )
				         .filter( e -> e.getControlName().startsWith( "entity." ) && e.getHtmlId().startsWith( "entity." ) )
				         .count()
		);
	}

	@Test
	public void controlNamePrefixingCheckbox() {
		simulateEntityViewForm();
		ContainerViewElement container = assemble( "multiValue", ViewElementMode.CONTROL );

		assertEquals(
				2,
				container.findAll( CheckboxFormElement.class )
				         .filter( e -> CheckboxFormElement.class.equals( e.getClass() ) )
				         .filter( e -> e.getControlName().startsWith( "entity." ) && e.getHtmlId().startsWith( "entity." ) )
				         .count()
		);
	}

	@Test
	public void controlNamePrefixingToggle() {
		simulateEntityViewForm();

		ContainerViewElement container = assemble( "singleValue", ViewElementMode.CONTROL, BootstrapUiElements.MULTI_TOGGLE );

		assertEquals(
				3,
				container.findAll( ToggleFormElement.class )
				         .filter( e -> e.getControlName().startsWith( "entity." ) && e.getHtmlId().startsWith( "entity." ) )
				         .count()
		);
	}

	@Test
	public void selectInsteadOfCheckboxDueToSelectFormElementConfiguration() {
		when( builderContext.hasAttribute( EntityViewCommand.class ) ).thenReturn( true );

		SelectFormElementConfiguration configuration = SelectFormElementConfiguration.liveSearch().setActionsBox( true );
		when( properties.get( "multiValue" ).getAttribute( SelectFormElementConfiguration.class ) )
				.thenReturn( configuration );

		SelectFormElement select = assemble( "multiValue", ViewElementMode.CONTROL );
		assertTrue( select.isMultiple() );
		assertNotNull( select.getConfiguration() );
		assertEquals( true, select.getConfiguration().get( "actionsBox" ) );
	}

	@Test
	public void selectSpecifiedAsType() {
		when( builderContext.hasAttribute( EntityViewCommand.class ) ).thenReturn( true );

		SelectFormElement select = assemble( "multiValue", ViewElementMode.CONTROL, BootstrapUiElements.SELECT );
		assertTrue( select.isMultiple() );
		assertNotNull( select.getConfiguration() );
	}

	@Test
	public void filterControlsAreSingleByDefault() {
		when( builderContext.hasAttribute( EntityViewCommand.class ) ).thenReturn( true );

		SelectFormElement select = assemble( "multiValue", ViewElementMode.FILTER_CONTROL, BootstrapUiElements.SELECT );
		assertFalse( select.isMultiple() );
		assertNotNull( select.getConfiguration() );

		select = assemble( "multiValue", ViewElementMode.FILTER_CONTROL.forMultiple(), BootstrapUiElements.SELECT );
		assertTrue( select.isMultiple() );
		assertNotNull( select.getConfiguration() );
	}

	@Test
	public void fixedTypeTakesPrecedenceOverSelectFormElementConfiguration() {
		when( builderContext.hasAttribute( EntityViewCommand.class ) ).thenReturn( true );

		SelectFormElementConfiguration configuration = SelectFormElementConfiguration.liveSearch().setActionsBox( true );
		when( properties.get( "multiValue" ).getAttribute( SelectFormElementConfiguration.class ) )
				.thenReturn( configuration );

		ContainerViewElement container = assemble( "multiValue", ViewElementMode.CONTROL, BootstrapUiElements.MULTI_CHECKBOX );
		assertFalse( container instanceof SelectFormElement );
	}

	@Test
	public void singleValueNotRequiredAsRadio() {
		simulateEntityViewForm();

		ContainerViewElement container = assemble( "singleValue", ViewElementMode.CONTROL, BootstrapUiElements.RADIO );
		List<RadioFormElement> radioElements = container.findAll( RadioFormElement.class ).collect( Collectors.toList() );

		assertEquals( 3, radioElements.size() );
		assertFalse( radioElements.stream().anyMatch( FormControlElementSupport::isRequired ) );
		assertEquals( 1, radioElements.stream().filter( RadioFormElement::isChecked ).count() );
		assertEquals( "", radioElements.stream().filter( RadioFormElement::isChecked ).findFirst().get().getText() );
	}

	@Test
	public void singleValueNotRequiredAsToggle() {
		simulateEntityViewForm();

		ContainerViewElement container = assemble( "singleValue", ViewElementMode.CONTROL, BootstrapUiElements.MULTI_TOGGLE );
		List<ToggleFormElement> toggleFormElements = container.findAll( ToggleFormElement.class ).collect( Collectors.toList() );

		assertEquals( 3, toggleFormElements.size() );
		assertFalse( toggleFormElements.stream().anyMatch( FormControlElementSupport::isRequired ) );
		assertEquals( 1, toggleFormElements.stream().filter( ToggleFormElement::isChecked ).count() );
		assertEquals( "", toggleFormElements.stream().filter( ToggleFormElement::isChecked ).findFirst().get().getText() );
	}

	@Test
	public void singleValueNotRequiredAsSelect() {
		SelectFormElement select = assembleAndVerify( "singleValue" );

		assertFalse( select.isRequired() );
		assertFalse( select.isMultiple() );
		assertEquals( 1, select.getChildren().size() );

		ContainerViewElement options = (ContainerViewElement) select.getChildren().get( 0 );
		assertEquals( 3, options.getChildren().size() );
	}

	@Test
	public void singleValueRequiredAsRadio() {
		when( properties.get( "singleValue" ).getAttribute( EntityAttributes.PROPERTY_REQUIRED, Boolean.class ) ).thenReturn( true );
		simulateEntityViewForm();

		ContainerViewElement container = assemble( "singleValue", ViewElementMode.CONTROL, BootstrapUiElements.RADIO );
		List<RadioFormElement> radioElements = container.findAll( RadioFormElement.class ).collect( Collectors.toList() );

		assertEquals( 2, radioElements.size() );
		assertTrue( radioElements.stream().allMatch( FormControlElementSupport::isRequired ) );
		assertEquals( 0, radioElements.stream().filter( RadioFormElement::isChecked ).count() );
	}

	@Test
	public void singleValueRequiredAsToggle() {
		when( properties.get( "singleValue" ).getAttribute( EntityAttributes.PROPERTY_REQUIRED, Boolean.class ) ).thenReturn( true );
		simulateEntityViewForm();

		ContainerViewElement container = assemble( "singleValue", ViewElementMode.CONTROL, BootstrapUiElements.MULTI_TOGGLE );
		List<ToggleFormElement> toggleFormElements = container.findAll( ToggleFormElement.class ).collect( Collectors.toList() );

		assertEquals( 2, toggleFormElements.size() );
		assertTrue( toggleFormElements.stream().allMatch( FormControlElementSupport::isRequired ) );
		assertEquals( 0, toggleFormElements.stream().filter( ToggleFormElement::isChecked ).count() );
	}

	@Test
	public void singleValueRequiredAsSelect() {
		when( properties.get( "singleValue" ).getAttribute( EntityAttributes.PROPERTY_REQUIRED, Boolean.class ) ).thenReturn( true );
		SelectFormElement select = assembleAndVerify( "singleValue" );

		assertTrue( select.isRequired() );
		assertFalse( select.isMultiple() );
		assertEquals( 1, select.getChildren().size() );
	}

	@Test
	public void allowedValuesSpecified() {
		when( properties.get( "singleValue" ).getAttribute( EntityAttributes.OPTIONS_ALLOWED_VALUES ) )
				.thenReturn( EnumSet.of( TestEnum.TWO ) );

		SelectFormElement select = assembleAndVerify( "singleValue" );

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
		OptionIterableBuilder optionBuilder = FixedOptionIterableBuilder.of( new OptionFormElementBuilder().label( "test" ).value( "fixed" ).selected() );
		when( properties.get( "singleValue" ).getAttribute( OptionIterableBuilder.class ) )
				.thenReturn( optionBuilder );

		SelectFormElement select = assembleAndVerify( "singleValue" );

		assertFalse( select.isRequired() );
		assertFalse( select.isMultiple() );
		assertEquals( 1, select.getChildren().size() );

		SelectFormElement.Option option = (SelectFormElement.Option) ( (ContainerViewElement) select.getChildren().get( 0 ) ).getChildren().get( 1 );
		assertTrue( option.isSelected() );
		assertEquals( "test", option.getLabel() );
		assertEquals( "fixed", option.getValue() );
	}

	@Test
	public void descriptorEnhancerIsApplied() {
		Consumer<OptionFormElementBuilder> propertyEnhancer = option -> option.attribute( "data-test-property", "test property" );
		when( properties.get( "singleValue" ).getAttribute( EntityAttributes.OPTIONS_ENHANCER ) ).thenReturn( propertyEnhancer );

		OptionIterableBuilder optionBuilder = FixedOptionIterableBuilder.of( new OptionFormElementBuilder().label( "test" ).value( "fixed" ).selected() );
		when( properties.get( "singleValue" ).getAttribute( OptionIterableBuilder.class ) )
				.thenReturn( optionBuilder );

		SelectFormElement select = assembleAndVerify( "singleValue" );

		assertEquals( 1, select.getChildren().size() );
		SelectFormElement.Option option = (SelectFormElement.Option) ( (ContainerViewElement) select.getChildren().get( 0 ) ).getChildren().get( 1 );
		assertEquals( "test property", option.getAttribute( "data-test-property" ) );
	}

	@Test
	public void multipleEnhancersAreExecuted() {
		Consumer<OptionFormElementBuilder> propertyEnhancer = option -> option.attribute( "data-test-property", "test property" );
		when( properties.get( "singleValue" ).getAttribute( EntityAttributes.OPTIONS_ENHANCER ) ).thenReturn( propertyEnhancer );

		OptionIterableBuilder optionBuilder = FixedOptionIterableBuilder.of( new OptionFormElementBuilder().label( "test" ).value( "fixed" ).selected() );
		when( properties.get( "singleValue" ).getAttribute( OptionIterableBuilder.class ) )
				.thenReturn( optionBuilder );

		EntityConfiguration configuration = mock( EntityConfiguration.class );
		Consumer<OptionFormElementBuilder> configurationEnhancer = option -> option.attribute( "data-test-configuration", "configuration property" );
		when( configuration.getAttribute( EntityAttributes.OPTIONS_ENHANCER ) ).thenReturn( configurationEnhancer );
		when( configuration.getEntityType() ).thenReturn( getTestClass() );
		when( entityRegistry.getEntityConfiguration( any( Class.class ) ) ).thenReturn( configuration );

		SelectFormElement select = assembleAndVerify( "singleValue" );

		assertEquals( 1, select.getChildren().size() );
		SelectFormElement.Option option = (SelectFormElement.Option) ( (ContainerViewElement) select.getChildren().get( 0 ) ).getChildren().get( 1 );
		assertEquals( "test property", option.getAttribute( "data-test-property" ) );
		assertEquals( "configuration property", option.getAttribute( "data-test-configuration" ) );
	}

	@Test
	public void propertyDescriptorEnhancerIsExecutedAfterConfigurationEnhancer() {
		Consumer<OptionFormElementBuilder> configurationEnhancer = option -> option.attribute( "data-test", "configuration property" );
		Consumer<OptionFormElementBuilder> propertyEnhancer = option -> option.removeAttribute( "data-test" );
		when( properties.get( "singleValue" ).getAttribute( EntityAttributes.OPTIONS_ENHANCER ) ).thenReturn( propertyEnhancer );

		OptionIterableBuilder optionBuilder = FixedOptionIterableBuilder.of( new OptionFormElementBuilder().label( "test" ).value( "fixed" ).selected() );
		when( properties.get( "singleValue" ).getAttribute( OptionIterableBuilder.class ) )
				.thenReturn( optionBuilder );

		EntityConfiguration configuration = mock( EntityConfiguration.class );
		when( configuration.getAttribute( EntityAttributes.OPTIONS_ENHANCER ) ).thenReturn( configurationEnhancer );
		when( configuration.getEntityType() ).thenReturn( getTestClass() );
		when( entityRegistry.getEntityConfiguration( any( Class.class ) ) ).thenReturn( configuration );

		SelectFormElement select = assembleAndVerify( "singleValue" );
		assertEquals( 1, select.getChildren().size() );
		SelectFormElement.Option option = (SelectFormElement.Option) ( (ContainerViewElement) select.getChildren().get( 0 ) ).getChildren().get( 1 );
		assertNull( option.getAttribute( "data-test" ) );
	}

	@Test
	public void optionGeneratorEnhancerIsExecutedAfterPropertyDescriptorEnhancer() {
		Consumer<OptionFormElementBuilder> propertyDescriptorEnhancer = option -> option.attribute( "data-test", "configuration property" );
		Consumer<OptionFormElementBuilder> optionGeneratorEnhancer = option -> option.removeAttribute( "data-test" );
		when( properties.get( "singleValue" ).getAttribute( EntityAttributes.OPTIONS_ENHANCER ) ).thenReturn( propertyDescriptorEnhancer );

		OptionIterableBuilder optionBuilder = FixedOptionIterableBuilder.of( new OptionFormElementBuilder().label( "test" ).value( "fixed" ).selected() );
		when( properties.get( "singleValue" ).getAttribute( OptionIterableBuilder.class ) )
				.thenReturn( optionBuilder );

		EntityConfiguration configuration = mock( EntityConfiguration.class );
		OptionGenerator optionGenerator = new OptionGenerator();
		optionGenerator.setEnhancer( optionGeneratorEnhancer );

		when( configuration.getAttribute( OptionGenerator.class ) ).thenReturn( optionGenerator );
		when( configuration.getEntityType() ).thenReturn( getTestClass() );
		when( entityRegistry.getEntityConfiguration( any( Class.class ) ) ).thenReturn( configuration );

		SelectFormElement select = assembleAndVerify( "singleValue" );
		assertEquals( 1, select.getChildren().size() );
		SelectFormElement.Option option = (SelectFormElement.Option) ( (ContainerViewElement) select.getChildren().get( 0 ) ).getChildren().get( 1 );
		assertNull( option.getAttribute( "data-test" ) );
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
		public TestEnum singleValue;

		public Set<TestEnum> multiValue;
	}
}
