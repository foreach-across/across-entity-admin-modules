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

import com.foreach.across.modules.bootstrapui.elements.LabelFormElement;
import com.foreach.across.modules.bootstrapui.elements.TextboxFormElement;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.web.ui.DefaultViewElementBuilderContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TestPropertyPlaceholderTextProcessor
{
	@Mock
	private EntityPropertyDescriptor descriptor;

	@Spy
	private DefaultViewElementBuilderContext builderContext = new DefaultViewElementBuilderContext();

	private PropertyPlaceholderTextPostProcessor postProcessor = new PropertyPlaceholderTextPostProcessor();
	private TextboxFormElement box = new TextboxFormElement();

	@BeforeEach
	public void before() {
		builderContext.setAttribute( EntityPropertyDescriptor.class, descriptor );
	}

	@Test
	public void nothingHappensIfDifferentViewElementType() {
		postProcessor.postProcess( builderContext, new LabelFormElement() );
		verify( builderContext, never() ).getMessage( any(), eq( "" ) );
		assertThat( box.getPlaceholder() ).isNull();
	}

	@Test
	public void noPlaceholderIfThereAlreadyIsOne() {
		box.setPlaceholder( "" );

		postProcessor.postProcess( builderContext, box );
		verify( builderContext, never() ).getMessage( any(), eq( "" ) );
		assertThat( box.getPlaceholder() ).isEmpty();
	}

	@Test
	public void resolvedPlaceholder() {
		when( descriptor.getName() ).thenReturn( "myprop" );
		when( builderContext.getMessage( "properties.myprop[placeholder]", "" ) ).thenReturn( "my placeholder" );
		postProcessor.postProcess( builderContext, box );
		assertThat( box.getPlaceholder() ).isEqualTo( "my placeholder" );
	}

}
