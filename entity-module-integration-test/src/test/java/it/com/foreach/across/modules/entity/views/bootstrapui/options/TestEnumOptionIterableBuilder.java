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

import com.foreach.across.modules.bootstrapui.elements.builder.OptionFormElementBuilder;
import com.foreach.across.modules.entity.registry.EntityModel;
import com.foreach.across.modules.entity.support.EntityMessageCodeResolver;
import com.foreach.across.modules.entity.util.EntityUtils;
import com.foreach.across.modules.entity.views.bootstrapui.options.EnumOptionIterableBuilder;
import com.foreach.across.modules.web.ui.DefaultViewElementBuilderContext;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static it.com.foreach.across.modules.entity.views.bootstrapui.options.TestEnumOptionIterableBuilder.Counter.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.*;

/**
 * @author Arne Vandamme
 */
public class TestEnumOptionIterableBuilder
{
	private EnumOptionIterableBuilder iterableBuilder;
	private ViewElementBuilderContext elementBuilderContext;
	private Map<Counter, OptionFormElementBuilder> options = new HashMap<>();

	@BeforeEach
	@SuppressWarnings("unchecked")
	public void before() {
		iterableBuilder = new EnumOptionIterableBuilder();
		iterableBuilder.setEnumType( Counter.class );

		elementBuilderContext = new DefaultViewElementBuilderContext();

		EntityMessageCodeResolver codeResolver = mock( EntityMessageCodeResolver.class );
		when( codeResolver.getMessageWithFallback( anyString(), anyString() ) )
				.thenAnswer( invocationOnMock -> invocationOnMock.getArguments()[1] );

		elementBuilderContext.setAttribute( EntityMessageCodeResolver.class, codeResolver );

		options.clear();
	}

	@Test
	public void allEnumOptionsAreGenerated() {
		build();
		assertOptions( ONE, TWO, THREE );
	}

	@Test
	public void restrictAllowedValues() {
		iterableBuilder.setAllowedValues( EnumSet.of( ONE, THREE ) );

		options.clear();

		Iterable<OptionFormElementBuilder> iterable = iterableBuilder.buildOptions( elementBuilderContext );

		List<OptionFormElementBuilder> optionsInOrder = new ArrayList<>( 3 );

		for ( OptionFormElementBuilder option : iterable ) {
			optionsInOrder.add( option );
			options.put( Counter.valueOf( (String) option.getValue() ), option );
		}

		assertEquals( 2, optionsInOrder.size() );

		assertEquals( EntityUtils.generateDisplayName( ONE.name() ), optionsInOrder.get( 0 ).getLabel() );
		assertEquals( ONE.name(), optionsInOrder.get( 0 ).getValue() );
		assertEquals( ONE, optionsInOrder.get( 0 ).getRawValue() );

		assertEquals( EntityUtils.generateDisplayName( Counter.THREE.name() ), optionsInOrder.get( 1 ).getLabel() );
		assertEquals( Counter.THREE.name(), optionsInOrder.get( 1 ).getValue() );
		assertEquals( Counter.THREE, optionsInOrder.get( 1 ).getRawValue() );
	}

	@Test
	@SuppressWarnings("unchecked")
	public void entityModelIsUsedIfPresent() {
		EntityModel model = mock( EntityModel.class );
		when( model.getLabel( ONE ) ).thenReturn( "one" );
		when( model.getLabel( Counter.TWO ) ).thenReturn( "two" );
		when( model.getLabel( Counter.THREE ) ).thenReturn( "three" );
		when( model.getId( ONE ) ).thenReturn( 1 );
		when( model.getId( Counter.TWO ) ).thenReturn( 2 );
		when( model.getId( Counter.THREE ) ).thenReturn( 3 );

		iterableBuilder.setEntityModel( model );

		Iterable<OptionFormElementBuilder> iterable = iterableBuilder.buildOptions( elementBuilderContext );
		List<OptionFormElementBuilder> optionsInOrder = new ArrayList<>( 3 );

		for ( OptionFormElementBuilder option : iterable ) {
			optionsInOrder.add( option );
		}

		assertEquals( ONE, optionsInOrder.get( 0 ).getRawValue() );
		assertEquals( TWO, optionsInOrder.get( 1 ).getRawValue() );
		assertEquals( THREE, optionsInOrder.get( 2 ).getRawValue() );
		assertEquals( "one", optionsInOrder.get( 0 ).getLabel() );
		assertEquals( "two", optionsInOrder.get( 1 ).getLabel() );
		assertEquals( "three", optionsInOrder.get( 2 ).getLabel() );
		assertEquals( 1, optionsInOrder.get( 0 ).getValue() );
		assertEquals( 2, optionsInOrder.get( 1 ).getValue() );
		assertEquals( 3, optionsInOrder.get( 2 ).getValue() );

	}

	private void assertOptions( Counter... counters ) {
		for ( Counter counter : counters ) {
			assertFalse( options.get( counter ).isSelected() );
		}
	}

	private void build() {
		options.clear();

		Iterable<OptionFormElementBuilder> iterable = iterableBuilder.buildOptions( elementBuilderContext );

		List<OptionFormElementBuilder> optionsInOrder = new ArrayList<>( 3 );

		for ( OptionFormElementBuilder option : iterable ) {
			optionsInOrder.add( option );
			options.put( Counter.valueOf( (String) option.getValue() ), option );
		}

		assertEquals( 3, optionsInOrder.size() );

		assertEquals( EntityUtils.generateDisplayName( ONE.name() ), optionsInOrder.get( 0 ).getLabel() );
		assertEquals( ONE.name(), optionsInOrder.get( 0 ).getValue() );
		assertEquals( ONE, optionsInOrder.get( 0 ).getRawValue() );

		assertEquals( EntityUtils.generateDisplayName( Counter.TWO.name() ), optionsInOrder.get( 1 ).getLabel() );
		assertEquals( Counter.TWO.name(), optionsInOrder.get( 1 ).getValue() );
		assertEquals( Counter.TWO, optionsInOrder.get( 1 ).getRawValue() );

		assertEquals( EntityUtils.generateDisplayName( Counter.THREE.name() ), optionsInOrder.get( 2 ).getLabel() );
		assertEquals( Counter.THREE.name(), optionsInOrder.get( 2 ).getValue() );
		assertEquals( Counter.THREE, optionsInOrder.get( 2 ).getRawValue() );
	}

	enum Counter
	{
		ONE,
		TWO,
		THREE
	}
}

