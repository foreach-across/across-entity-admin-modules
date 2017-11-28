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
import com.foreach.across.modules.entity.views.support.ValueFetcher;
import com.foreach.across.modules.entity.web.EntityViewModel;
import com.foreach.across.modules.web.support.LocalizedTextResolver;
import com.foreach.across.modules.web.support.MessageCodeSupportingLocalizedTextResolver;
import com.foreach.across.modules.web.ui.DefaultViewElementBuilderContext;
import com.foreach.across.modules.web.ui.elements.ConfigurableTextViewElement;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Locale;

import static org.mockito.Mockito.*;

/**
 * @author Steven Gentens
 * @since 2.0.0
 */
@SuppressWarnings("unchecked")
@RunWith(MockitoJUnitRunner.class)
public class TestBooleanValueTextProcessor
{
	@Mock
	private EntityPropertyDescriptor descriptor;

	@Mock
	private DefaultViewElementBuilderContext builderContext;

	private MessageCodeSupportingLocalizedTextResolver localizedTextResolver = new MessageCodeSupportingLocalizedTextResolver();

	@Mock
	private ConfigurableTextViewElement text;

	private BooleanValueTextProcessor processor;

	@Before
	public void setUp() throws Exception {
		processor = new BooleanValueTextProcessor( descriptor );

		when( builderContext.getAttribute( LocalizedTextResolver.class ) ).thenReturn( localizedTextResolver );
		when( builderContext.resolveText( any( String.class ), any( String.class ) ) ).then( (Answer<String>) invocation -> {
			Object[] args = invocation.getArguments();
			return localizedTextResolver.resolveText( (String) args[0], (String) args[1] );
		} );

		when( builderContext.getAttribute( EntityViewModel.ENTITY ) ).thenReturn( "123" );
		ValueFetcher valueFetcher = mock( ValueFetcher.class );
		when( descriptor.getValueFetcher() ).thenReturn( valueFetcher );
		when( descriptor.getName() ).thenReturn( "myBoolean" );
		LocaleContextHolder.setLocale( Locale.CANADA );
	}

	@After
	public void tearDown() throws Exception {
		LocaleContextHolder.resetLocaleContext();
	}

	@Test
	public void defaultValues() {
		when( descriptor.getValueFetcher().getValue( "123" ) ).thenReturn( true );
		processor.postProcess( builderContext, text );
		verify( text ).setText( "Yes" );

		when( descriptor.getValueFetcher().getValue( "123" ) ).thenReturn( false );
		processor.postProcess( builderContext, text );
		verify( text ).setText( "No" );

		when( descriptor.getValueFetcher().getValue( "123" ) ).thenReturn( null );
		processor.postProcess( builderContext, text );
		verify( text ).setText( "" );
	}

	@Test
	public void resolvedMessageCode() {
		when( descriptor.getValueFetcher().getValue( "123" ) ).thenReturn( true );
		when( builderContext.resolveText( "#{properties.myBoolean.value[true]}", "Yes" ) ).thenReturn( "Active" );
		processor.postProcess( builderContext, text );
		verify( text ).setText( "Active" );

		when( descriptor.getValueFetcher().getValue( "123" ) ).thenReturn( false );
		when( builderContext.resolveText( "#{properties.myBoolean.value[false]}", "No" ) ).thenReturn( "Not active" );
		processor.postProcess( builderContext, text );
		verify( text ).setText( "Not active" );

		when( descriptor.getValueFetcher().getValue( "123" ) ).thenReturn( null );
		when( builderContext.resolveText( "#{properties.myBoolean.value[empty]}", "" ) ).thenReturn( "Not filled in" );
		processor.postProcess( builderContext, text );
		verify( text ).setText( "Not filled in" );
	}
}
