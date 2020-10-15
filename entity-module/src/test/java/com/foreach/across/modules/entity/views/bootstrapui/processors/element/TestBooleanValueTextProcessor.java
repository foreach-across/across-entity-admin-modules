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
import com.foreach.across.modules.web.support.MessageCodeSupportingLocalizedTextResolver;
import com.foreach.across.modules.web.ui.DefaultViewElementBuilderContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

/**
 * @author Steven Gentens
 * @since 2.0.0
 */
@SuppressWarnings("unchecked")
@ExtendWith(MockitoExtension.class)
public class TestBooleanValueTextProcessor
{
	@Mock
	private EntityPropertyDescriptor descriptor;

	@Mock
	private DefaultViewElementBuilderContext builderContext;

	private MessageCodeSupportingLocalizedTextResolver localizedTextResolver = new MessageCodeSupportingLocalizedTextResolver();

	private BooleanValueTextProcessor processor;

	@BeforeEach
	public void setUp() throws Exception {
		processor = new BooleanValueTextProcessor( descriptor );

		when( builderContext.resolveText( any( String.class ), any( String.class ) ) ).then( (Answer<String>) invocation -> {
			Object[] args = invocation.getArguments();
			return localizedTextResolver.resolveText( (String) args[0], (String) args[1] );
		} );

		when( descriptor.getName() ).thenReturn( "myBoolean" );
	}

	@Test
	public void defaultValues() {
		assertThat( processor.print( true, Locale.CANADA, builderContext ) ).isEqualTo( "Yes" );
		assertThat( processor.print( false, Locale.CANADA, builderContext ) ).isEqualTo( "No" );
		assertThat( processor.print( null, Locale.CANADA, builderContext ) ).isEqualTo( "" );
	}

	@Test
	public void resolvedMessageCode() {
		when( builderContext.resolveText( "#{properties.myBoolean.value[true]}", "Yes" ) ).thenReturn( "Active" );
		when( builderContext.resolveText( "#{properties.myBoolean.value[false]}", "No" ) ).thenReturn( "Not active" );
		when( builderContext.resolveText( "#{properties.myBoolean.value[empty]}", "" ) ).thenReturn( "Not filled in" );

		assertThat( processor.print( true, Locale.CANADA, builderContext ) ).isEqualTo( "Active" );
		assertThat( processor.print( false, Locale.CANADA, builderContext ) ).isEqualTo( "Not active" );
		assertThat( processor.print( null, Locale.CANADA, builderContext ) ).isEqualTo( "Not filled in" );
	}
}
