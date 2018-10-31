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

package it.com.foreach.across.modules.entity.views.bootstrapui.options;

import com.foreach.across.modules.bootstrapui.elements.SelectFormElement;
import com.foreach.across.modules.bootstrapui.elements.builder.OptionFormElementBuilder;
import com.foreach.across.modules.bootstrapui.elements.builder.OptionsFormElementBuilder;
import com.foreach.across.modules.entity.views.bootstrapui.options.FixedOptionIterableBuilder;
import com.foreach.across.modules.entity.views.bootstrapui.options.OptionGenerator;
import com.foreach.across.modules.entity.views.bootstrapui.options.OptionIterableBuilder;
import com.foreach.across.modules.entity.web.EntityViewModel;
import com.foreach.across.modules.web.ui.DefaultViewElementBuilderContext;
import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.elements.ContainerViewElement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import static com.foreach.across.modules.entity.views.util.EntityViewElementUtils.setCurrentPropertyValue;
import static org.junit.Assert.*;

/**
 * @author Arne Vandamme
 */
@RunWith(MockitoJUnitRunner.class)
public class TestOptionGenerator
{
	private final OptionIterableBuilder singleOption = FixedOptionIterableBuilder.of( new OptionFormElementBuilder().label( "bbb" ).rawValue( 1L ) );

	private final OptionIterableBuilder noneSelected = FixedOptionIterableBuilder.of(
			new OptionFormElementBuilder().label( "bbb" ).rawValue( 1L ),
			new OptionFormElementBuilder().label( "aaa" ).rawValue( "2" )
	);

	private final OptionIterableBuilder withSelectedAndSorted = FixedOptionIterableBuilder.sorted(
			new OptionFormElementBuilder().label( "bbb" ).rawValue( 1L ),
			new OptionFormElementBuilder().label( "aaa" ).rawValue( "2" ).selected()
	);

	private OptionGenerator generator;
	private OptionsFormElementBuilder options;
	private ViewElementBuilderContext builderContext;

	@Before
	public void before() {
		generator = new OptionGenerator();
		options = new OptionsFormElementBuilder();

		builderContext = new DefaultViewElementBuilderContext();
		builderContext.setAttribute( OptionsFormElementBuilder.class, options );
	}

	@Test
	public void sortedByDefaultIfIterableNotSorted() {
		options.select();
		generator.setOptions( noneSelected );
		generator.setEmptyOption( null );

		List<SelectFormElement.Option> generated = build();
		assertEquals( 2, generated.size() );
		assertEquals( "aaa", generated.get( 0 ).getLabel() );
		assertEquals( "bbb", generated.get( 1 ).getLabel() );
	}

	@Test
	public void notSortedIfDisabledAndIterableNotSorted() {
		options.select();
		generator.setOptions( noneSelected );
		generator.setEmptyOption( null );
		generator.setSorted( false );

		List<SelectFormElement.Option> generated = build();
		assertEquals( 2, generated.size() );
		assertEquals( "bbb", generated.get( 0 ).getLabel() );
		assertEquals( "aaa", generated.get( 1 ).getLabel() );
	}

	@Test
	public void notSortedByDefaultIfIterableSorted() {
		options.select();
		generator.setOptions( withSelectedAndSorted );
		generator.setEmptyOption( null );

		List<SelectFormElement.Option> generated = build();
		assertEquals( 2, generated.size() );
		assertEquals( "bbb", generated.get( 0 ).getLabel() );
		assertEquals( "aaa", generated.get( 1 ).getLabel() );
	}

	@Test
	public void alwaysSortedIfEnabled() {
		options.select();
		generator.setOptions( withSelectedAndSorted );
		generator.setEmptyOption( null );
		generator.setSorted( true );

		List<SelectFormElement.Option> generated = build();
		assertEquals( 2, generated.size() );
		assertEquals( "aaa", generated.get( 0 ).getLabel() );
		assertEquals( "bbb", generated.get( 1 ).getLabel() );
	}

	@Test
	public void emptyOptionAddedIfRequiredAndNoneSelected() {
		options.select().required();

		generator.setOptions( noneSelected );
		generator.setSorted( false );

		List<SelectFormElement.Option> generated = build();
		assertEquals( 3, generated.size() );
		assertEquals( "", generated.get( 0 ).getLabel() );
		assertEquals( "bbb", generated.get( 1 ).getLabel() );
		assertEquals( "aaa", generated.get( 2 ).getLabel() );
	}

	@Test
	public void emptyOptionNotAddedAndOptionSelectedIfRequiredAndOnlyOneOption() {
		options.select().required();

		generator.setOptions( singleOption );

		List<SelectFormElement.Option> generated = build();
		assertEquals( 1, generated.size() );
		assertEquals( "bbb", generated.get( 0 ).getLabel() );
		assertTrue( generated.get( 0 ).isSelected() );
	}

	@Test
	public void emptyOptionAddedAndNoneSelectedIfOnlyOneOptionButNotRequired() {
		options.select().required( false );

		generator.setOptions( singleOption );

		List<SelectFormElement.Option> generated = build();
		assertEquals( 2, generated.size() );
		assertEquals( "", generated.get( 0 ).getLabel() );
		assertEquals( "bbb", generated.get( 1 ).getLabel() );
		assertFalse( generated.get( 1 ).isSelected() );
	}

	@Test
	public void emptyOptionNotAddedIfRequiredAndOneSelected() {
		options.select().required();
		generator.setOptions( withSelectedAndSorted );

		List<SelectFormElement.Option> generated = build();
		assertEquals( 2, generated.size() );
		assertEquals( "bbb", generated.get( 0 ).getLabel() );
		assertEquals( "aaa", generated.get( 1 ).getLabel() );
	}

	@Test
	public void emptyOptionAlwaysAddedIfNotRequired() {
		options.select();

		generator.setOptions( noneSelected );
		generator.setSorted( false );

		List<SelectFormElement.Option> generated = build();
		assertEquals( 3, generated.size() );
		assertEquals( "", generated.get( 0 ).getLabel() );
		assertEquals( "bbb", generated.get( 1 ).getLabel() );
		assertEquals( "aaa", generated.get( 2 ).getLabel() );

		generator.setOptions( withSelectedAndSorted );
		generated = build();

		assertEquals( 3, generated.size() );
		assertEquals( "", generated.get( 0 ).getLabel() );
		assertEquals( "bbb", generated.get( 1 ).getLabel() );
		assertEquals( "aaa", generated.get( 2 ).getLabel() );

		generator.setOptions( singleOption );
		generated = build();
		assertEquals( 2, generated.size() );
		assertEquals( "", generated.get( 0 ).getLabel() );
		assertEquals( "bbb", generated.get( 1 ).getLabel() );
	}

	@Test
	public void customEmptyOptionIsUsed() {
		options.select();

		generator.setEmptyOption( new OptionFormElementBuilder().label( "myemptyoption" ) );
		generator.setOptions( noneSelected );
		generator.setSorted( false );

		List<SelectFormElement.Option> generated = build();
		assertEquals( 3, generated.size() );
		assertEquals( "myemptyoption", generated.get( 0 ).getLabel() );
		assertEquals( "bbb", generated.get( 1 ).getLabel() );
		assertEquals( "aaa", generated.get( 2 ).getLabel() );
	}

	@Test
	public void nullEmptyOptionIsNeverAdded() {
		options.select();

		generator.setEmptyOption( null );
		generator.setOptions( noneSelected );
		generator.setSorted( false );

		List<SelectFormElement.Option> generated = build();
		assertEquals( 2, generated.size() );
		assertEquals( "bbb", generated.get( 0 ).getLabel() );
		assertEquals( "aaa", generated.get( 1 ).getLabel() );

		generator.setOptions( withSelectedAndSorted );
		generated = build();

		assertEquals( 2, generated.size() );
		assertEquals( "bbb", generated.get( 0 ).getLabel() );
		assertEquals( "aaa", generated.get( 1 ).getLabel() );
	}

	@Test
	public void emptyOptionIsAddedInCaseOfCheckbox() {
		options.checkbox();
		generator.setOptions( noneSelected );

		List<SelectFormElement.Option> generated = build();
		assertEquals( 3, generated.size() );

		generator.setOptions( withSelectedAndSorted );
		generated = build();

		assertEquals( 3, generated.size() );
	}

	@Test
	public void emptyOptionsIsAddedInCaseOfRadio() {
		options.radio();
		generator.setOptions( noneSelected );

		List<SelectFormElement.Option> generated = build();
		assertEquals( 3, generated.size() );

		generator.setOptions( withSelectedAndSorted );
		generated = build();

		assertEquals( 3, generated.size() );
	}

	@Test
	public void emptyOptionsIsNeverAddedInCaseOfMultiple() {
		options.multiple();
		generator.setOptions( noneSelected );

		List<SelectFormElement.Option> generated = build();
		assertEquals( 2, generated.size() );

		generator.setOptions( withSelectedAndSorted );
		generated = build();

		assertEquals( 2, generated.size() );
	}

	@Test
	public void membersAreSortedByLabelAndTextIfEnabled() {
		options.select().required();

		generator.setOptions( noneSelected );
		generator.setSorted( true );

		List<SelectFormElement.Option> generated = build();
		assertEquals( 3, generated.size() );
		assertEquals( "", generated.get( 0 ).getLabel() );
		assertEquals( "aaa", generated.get( 1 ).getLabel() );
		assertEquals( "bbb", generated.get( 2 ).getLabel() );

		generator.setEmptyOption( null );
		generated = build();
		assertEquals( 2, generated.size() );
		assertEquals( "aaa", generated.get( 0 ).getLabel() );
		assertEquals( "bbb", generated.get( 1 ).getLabel() );
	}

	@Test
	public void noOptionSelected() {
		setCurrentPropertyValue( builderContext, Collections.emptyList() );

		generator.setSorted( true );
		generator.setEmptyOption( null );
		generator.setOptions( withSelectedAndSorted );
		List<SelectFormElement.Option> generated = build();

		assertEquals( 2, generated.size() );
		assertFalse( generated.get( 0 ).isSelected() );
		assertFalse( generated.get( 1 ).isSelected() );
	}

	@Test
	public void singleOptionsAutomaticallySelected() {
		setCurrentPropertyValue( builderContext, 1L );

		generator.setSorted( true );
		generator.setEmptyOption( null );
		generator.setOptions( withSelectedAndSorted );
		List<SelectFormElement.Option> generated = build();

		assertEquals( 2, generated.size() );
		assertFalse( generated.get( 0 ).isSelected() );
		assertTrue( generated.get( 1 ).isSelected() );
	}

	@Test
	public void multipleOptionsSelectedAsCollection() {
		setCurrentPropertyValue( builderContext, Arrays.asList( "2", 1L ) );

		generator.setSorted( true );
		generator.setEmptyOption( null );
		generator.setOptions( noneSelected );
		List<SelectFormElement.Option> generated = build();

		assertEquals( 2, generated.size() );
		assertTrue( generated.get( 0 ).isSelected() );
		assertTrue( generated.get( 1 ).isSelected() );
	}

	@Test
	public void multipleOptionsSelectedAsArray() {
		builderContext.setAttribute( EntityViewModel.ENTITY, "entity" );
		setCurrentPropertyValue( builderContext, new Object[] { "2", 1L } );

		generator.setSorted( true );
		generator.setEmptyOption( null );
		generator.setOptions( noneSelected );
		List<SelectFormElement.Option> generated = build();

		assertEquals( 2, generated.size() );
		assertTrue( generated.get( 0 ).isSelected() );
		assertTrue( generated.get( 1 ).isSelected() );
	}

	@Test
	public void selfOptionNotIncludedIsDefault() {
		builderContext.setAttribute( EntityViewModel.ENTITY, "2" );

		generator.setSorted( true );
		generator.setEmptyOption( null );
		generator.setOptions( noneSelected );
		List<SelectFormElement.Option> generated = build();

		assertEquals( 1, generated.size() );
		assertEquals( "bbb", generated.get( 0 ).getLabel() );
	}

	@Test
	public void selfOptionIncluded() {
		builderContext.setAttribute( EntityViewModel.ENTITY, "2" );

		generator.setSorted( true );
		generator.setEmptyOption( null );
		generator.setOptions( noneSelected );
		generator.setSelfOptionIncluded( true );
		List<SelectFormElement.Option> generated = build();

		assertEquals( 2, generated.size() );
		assertEquals( "aaa", generated.get( 0 ).getLabel() );
		assertEquals( "bbb", generated.get( 1 ).getLabel() );
	}

	@Test
	public void hasEnhancer() {
		generator.setEnhancer( option -> option.attribute( "data-test", "test" ) );

		generator.setSorted( true );
		generator.setEmptyOption( null );
		generator.setOptions( noneSelected );
		generator.setSelfOptionIncluded( true );
		List<SelectFormElement.Option> generated = build();

		assertEquals( 2, generated.size() );
		assertEquals( "aaa", generated.get( 0 ).getLabel() );
		assertEquals( "test", generated.get( 0 ).getAttribute( "data-test" ) );
		assertEquals( "bbb", generated.get( 1 ).getLabel() );
		assertEquals( "test", generated.get( 1 ).getAttribute( "data-test" ) );
	}

	@Test
	public void chainedEnhancerIsApplied() {
		Consumer<OptionFormElementBuilder> firstEnhancer = option -> option.attribute( "data-test", "test" );
		Consumer<OptionFormElementBuilder> secondEnhancer = option -> option.attribute( "data-test-label", option.getLabel() );
		generator.setEnhancer( firstEnhancer.andThen( secondEnhancer ) );

		generator.setSorted( true );
		generator.setEmptyOption( null );
		generator.setOptions( noneSelected );
		generator.setSelfOptionIncluded( true );
		List<SelectFormElement.Option> generated = build();

		assertEquals( 2, generated.size() );
		assertEquals( "test", generated.get( 0 ).getAttribute( "data-test" ) );
		assertEquals( "aaa", generated.get( 0 ).getAttribute( "data-test-label" ) );
		assertEquals( "test", generated.get( 1 ).getAttribute( "data-test" ) );
		assertEquals( "bbb", generated.get( 1 ).getAttribute( "data-test-label" ) );
	}

	@Test
	public void chainedEnhancerNegatesFirst() {
		Consumer<OptionFormElementBuilder> firstEnhancer = option -> option.attribute( "data-test", option.getLabel() );
		Consumer<OptionFormElementBuilder> secondEnhancer = option -> option.removeAttribute( "data-test" );
		generator.setEnhancer( firstEnhancer.andThen( secondEnhancer ) );

		generator.setSorted( true );
		generator.setEmptyOption( null );
		generator.setOptions( noneSelected );
		generator.setSelfOptionIncluded( true );
		List<SelectFormElement.Option> generated = build();

		assertEquals( 2, generated.size() );
		assertNull( generated.get( 0 ).getAttribute( "data-test" ) );
		assertNull( "test", generated.get( 1 ).getAttribute( "data-test" ) );
	}

	@SuppressWarnings("unchecked")
	private <U> List<U> build() {
		ContainerViewElement container = generator.build( builderContext );
		List<U> members = new ArrayList<>( container.getChildren().size() );

		for ( ViewElement element : container.getChildren() ) {
			members.add( (U) element );
		}

		return members;
	}
}
