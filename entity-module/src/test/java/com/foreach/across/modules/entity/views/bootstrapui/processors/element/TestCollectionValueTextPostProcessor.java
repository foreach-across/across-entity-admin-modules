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

package com.foreach.across.modules.entity.views.bootstrapui.processors.element;

import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * @author Arne Vandamme
 * @since 3.2.0
 */
@RunWith(MockitoJUnitRunner.class)
public class TestCollectionValueTextPostProcessor
{
	@Mock
	private AbstractValueTextPostProcessor item;

	@Mock
	private ViewElementBuilderContext builderContext;

	@InjectMocks
	private CollectionValueTextPostProcessor text;

	@Test
	public void emptyStringIfNullOrEmpty() {
		assertThat( text.print( null, Locale.UK ) ).isEqualTo( "" );
		assertThat( text.print( new Object[0], Locale.UK ) ).isEqualTo( "" );
		assertThat( text.print( Collections.emptyList(), Locale.UK ) ).isEqualTo( "" );
		assertThat( text.print( new HashMap<>(), Locale.UK ) ).isEqualTo( "" );
	}

	@Test
	public void array() {
		when( item.print( 1, Locale.UK, builderContext ) ).thenReturn( "één" );
		when( item.print( 2, Locale.UK, builderContext ) ).thenReturn( "twee" );
		assertThat( text.print( new Object[] { 1, 2 }, Locale.UK, builderContext ) ).isEqualTo( "één, twee" );
	}

	@Test
	public void collection() {
		text.setSeparator( " | " );

		when( item.print( "one", Locale.FRENCH, builderContext ) ).thenReturn( "hello" );
		when( item.print( 5, Locale.FRENCH, builderContext ) ).thenReturn( "goodbye" );

		assertThat( text.print( Arrays.asList( "one", 5 ), Locale.FRENCH, builderContext ) ).isEqualTo( "hello | goodbye" );
	}
}
