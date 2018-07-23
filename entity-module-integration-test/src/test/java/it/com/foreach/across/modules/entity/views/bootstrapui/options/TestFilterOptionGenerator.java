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
import com.foreach.across.modules.entity.views.bootstrapui.options.FilterOptionGenerator;
import com.foreach.across.modules.entity.views.bootstrapui.options.FixedOptionIterableBuilder;
import com.foreach.across.modules.entity.views.bootstrapui.options.OptionIterableBuilder;
import com.foreach.across.modules.entity.views.support.ValueFetcher;
import com.foreach.across.modules.entity.web.EntityViewModel;
import com.foreach.across.modules.web.ui.DefaultViewElementBuilderContext;
import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.elements.AbstractNodeViewElement;
import com.foreach.across.modules.web.ui.elements.ContainerViewElement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

/**
 * @author Steven Gentens
 * @since 2.2.0
 */
@RunWith(MockitoJUnitRunner.class)
public class TestFilterOptionGenerator
{
	private FilterOptionGenerator generator;
	private OptionsFormElementBuilder options;
	private ViewElementBuilderContext builderContext;

	private final OptionIterableBuilder noneSelected = FixedOptionIterableBuilder.of(
			new OptionFormElementBuilder().label( "bbb" ).rawValue( 1L ),
			new OptionFormElementBuilder().label( "aaa" ).rawValue( "2" )
	);

	@Mock
	private ValueFetcher<String> valueFetcher;

	@Before
	public void before() {
		generator = new FilterOptionGenerator();
		options = new OptionsFormElementBuilder();

		generator.setOptions( noneSelected );
		generator.setValueFetcher( valueFetcher );

		builderContext = new DefaultViewElementBuilderContext();
		builderContext.setAttribute( OptionsFormElementBuilder.class, options );
	}

	@Test
	public void emptyOptionBeforeNotSetOption() {
		generator.setValueNotSetOption( new OptionFormElementBuilder().label( "ccc" ).rawValue( "123" ) );

		List<AbstractNodeViewElement> generated = build();
		assertEquals( 4, generated.size() );
		assertEquals( "", ( (SelectFormElement.Option) generated.get( 0 ) ).getLabel() );
		assertTrue( "", ( (SelectFormElement.Option) generated.get( 0 ) ).isSelected() );
		assertTrue( generated.get( 1 ) instanceof SelectFormElement.OptionGroup );
		assertEquals( 1, generated.get( 1 ).getChildren().size() );
		assertEquals( "ccc", ( (SelectFormElement.Option) generated.get( 1 ).getChildren().get( 0 ) ).getLabel() );
		assertEquals( "aaa", ( (SelectFormElement.Option) generated.get( 2 ) ).getLabel() );
		assertEquals( "bbb", ( (SelectFormElement.Option) generated.get( 3 ) ).getLabel() );
	}

	@Test
	public void notSetOptionSelectedIfSelectedValuesContainsNull() {
		builderContext.setAttribute( EntityViewModel.ENTITY, "entity" );
		generator.setValueNotSetOption( new OptionFormElementBuilder().label( "ccc" ).rawValue( "123" ) );
		when( valueFetcher.getValue( "entity" ) ).thenReturn( Collections.singletonList( null ) );

		List<AbstractNodeViewElement> generated = build();
		assertEquals( 4, generated.size() );
		assertEquals( "", ( (SelectFormElement.Option) generated.get( 0 ) ).getLabel() );
		assertTrue( generated.get( 1 ) instanceof SelectFormElement.OptionGroup );
		assertEquals( 1, generated.get( 1 ).getChildren().size() );
		assertEquals( "ccc", ( (SelectFormElement.Option) generated.get( 1 ).getChildren().get( 0 ) ).getLabel() );
		assertTrue( ( (SelectFormElement.Option) generated.get( 1 ).getChildren().get( 0 ) ).isSelected() );
		assertEquals( "aaa", ( (SelectFormElement.Option) generated.get( 2 ) ).getLabel() );
		assertEquals( "bbb", ( (SelectFormElement.Option) generated.get( 3 ) ).getLabel() );
	}

	@Test
	public void notSetOptionSelectedIfSelectedValueIsNull() {
		builderContext.setAttribute( EntityViewModel.ENTITY, "entity" );
		generator.setValueNotSetOption( new OptionFormElementBuilder().label( "ccc" ).rawValue( "123" ) );
		when( valueFetcher.getValue( "entity" ) ).thenReturn( null );

		List<AbstractNodeViewElement> generated = build();
		assertEquals( 4, generated.size() );
		assertEquals( "", ( (SelectFormElement.Option) generated.get( 0 ) ).getLabel() );
		assertTrue( generated.get( 1 ) instanceof SelectFormElement.OptionGroup );
		assertEquals( 1, generated.get( 1 ).getChildren().size() );
		assertEquals( "ccc", ( (SelectFormElement.Option) generated.get( 1 ).getChildren().get( 0 ) ).getLabel() );
		assertTrue( ( (SelectFormElement.Option) generated.get( 1 ).getChildren().get( 0 ) ).isSelected() );
		assertEquals( "aaa", ( (SelectFormElement.Option) generated.get( 2 ) ).getLabel() );
		assertEquals( "bbb", ( (SelectFormElement.Option) generated.get( 3 ) ).getLabel() );
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
