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

import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.support.EntityMessageCodeResolver;
import com.foreach.across.modules.entity.views.support.ValueFetcher;
import com.foreach.across.modules.entity.web.EntityViewModel;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.elements.ConfigurableTextViewElement;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Locale;

import static org.mockito.Mockito.*;

/**
 * @author Arne Vandamme
 * @since 2.0.0
 */
@SuppressWarnings("unchecked")
@RunWith(MockitoJUnitRunner.class)
public class TestEnumValueTextPostProcessor
{
	@Mock
	private EntityPropertyDescriptor descriptor;

	@Mock
	private ViewElementBuilderContext builderContext;

	@Mock
	private EntityMessageCodeResolver codeResolver;

	@Mock
	private ConfigurableTextViewElement text;

	private EnumValueTextPostProcessor processor;

	@Before
	public void setUp() throws Exception {
		processor = new EnumValueTextPostProcessor( descriptor, Counter.class );

		when( builderContext.getAttribute( EntityViewModel.ENTITY ) ).thenReturn( "123" );
		ValueFetcher valueFetcher = mock( ValueFetcher.class );
		when( descriptor.getValueFetcher() ).thenReturn( valueFetcher );
		when( valueFetcher.getValue( "123" ) ).thenReturn( Counter.ONE );

		LocaleContextHolder.setLocale( Locale.CANADA );
	}

	@After
	public void tearDown() throws Exception {
		LocaleContextHolder.resetLocaleContext();
	}

	@Test
	public void nullValueNotSet() {
		when( descriptor.getValueFetcher().getValue( "123" ) ).thenReturn( null );
		processor.postProcess( builderContext, text );
		verifyNoMoreInteractions( text );
	}

	@Test
	public void valueNameIfNoCodeResolver() {
		processor.postProcess( builderContext, text );
		verify( text ).setText( "ONE" );
	}

	@Test
	public void codeResolvedIfResolver() {
		when( builderContext.getAttribute( EntityMessageCodeResolver.class ) ).thenReturn( codeResolver );
		when( codeResolver.getMessageWithFallback( "enums.Counter.ONE", "One", Locale.CANADA ) ).thenReturn( "één" );
		processor.postProcess( builderContext, text );
		verify( text ).setText( "één" );
	}

	enum Counter
	{
		ONE
	}
}
