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
import com.foreach.across.modules.entity.support.EntityMessageCodeResolver;
import com.foreach.across.modules.entity.util.EntityUtils;
import com.foreach.across.modules.entity.views.bootstrapui.options.EnumOptionIterableBuilder;
import com.foreach.across.modules.web.ui.DefaultViewElementBuilderContext;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Arne Vandamme
 */
public class TestEnumOptionIterableBuilder
{
	private EnumOptionIterableBuilder iterableBuilder;
	private ViewElementBuilderContext elementBuilderContext;
	private Map<Counter, OptionFormElementBuilder> options = new HashMap<>();

	@Before
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
		assertOptions( Counter.ONE, Counter.TWO, Counter.THREE );
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

		assertEquals( EntityUtils.generateDisplayName( Counter.ONE.name() ), optionsInOrder.get( 0 ).getLabel() );
		assertEquals( Counter.ONE.name(), optionsInOrder.get( 0 ).getValue() );
		assertEquals( Counter.ONE, optionsInOrder.get( 0 ).getRawValue() );

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
