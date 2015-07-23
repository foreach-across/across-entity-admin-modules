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
package com.foreach.across.modules.bootstrapui.elements.builder;

import com.foreach.across.modules.bootstrapui.elements.builder.OptionsFormElementBuilder.Option;
import com.foreach.across.modules.web.ui.ViewElementBuilderFactory;
import com.foreach.across.modules.web.ui.elements.NodeViewElementSupport;
import com.foreach.across.test.support.AbstractViewElementBuilderTest;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

/**
 * @author Arne Vandamme
 */
public class TestOptionsFormElementBuilder extends AbstractViewElementBuilderTest<OptionsFormElementBuilder, NodeViewElementSupport>
{
	@Override
	protected OptionsFormElementBuilder createBuilder( ViewElementBuilderFactory builderFactory ) {
		return new OptionsFormElementBuilder();
	}

	@Override
	protected Collection<String> nonBuilderReturningMethods() {
		return Collections.singleton( "createOption" );
	}

	@Test(expected = IllegalStateException.class)
	public void noNestingOfOptionsFormElementBuilders() {
		when( builderContext.hasAttribute( OptionsFormElementBuilder.class ) ).thenReturn( true );

		build();
	}

	@Test(expected = IllegalStateException.class)
	public void optionCanOnlyBeUsedWithinOptionsBuilder() {
		builder.createOption().build( builderContext );
	}

	@Test
	public void sortOptionsOnTextOnly() {
		List<Option> options = Arrays.asList( new Option().text( "bbb" ), new Option().text( "aaa" ) );
		Collections.sort( options );

		assertEquals( "aaa", options.get( 0 ).getText() );
		assertEquals( "bbb", options.get( 1 ).getText() );
	}

	@Test
	public void sortOptionsOnLabelOnly() {
		List<Option> options = Arrays.asList( new Option().label( "bbb" ), new Option().label( "aaa" ) );
		Collections.sort( options );

		assertEquals( "aaa", options.get( 0 ).getLabel() );
		assertEquals( "bbb", options.get( 1 ).getLabel() );
	}

	@Test
	public void sortOptionsOnLabelBeforeText() {
		List<Option> options = Arrays.asList(
				new Option().label( "aaa" ).text( "bbb" ),
				new Option().label( "bbb" ).text( "aaa" )
		);
		Collections.sort( options );

		assertEquals( "aaa", options.get( 0 ).getLabel() );
		assertEquals( "bbb", options.get( 1 ).getLabel() );
	}
}
