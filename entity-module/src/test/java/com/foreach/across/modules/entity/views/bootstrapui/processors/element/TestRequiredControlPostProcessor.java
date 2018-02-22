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
import com.foreach.across.modules.entity.EntityAttributes;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.web.ui.DefaultViewElementBuilderContext;
import com.foreach.across.modules.web.ui.ViewElement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * @author Arne Vandamme
 * @since 3.0.0
 */
@RunWith(MockitoJUnitRunner.class)
public class TestRequiredControlPostProcessor
{
	@Mock
	private EntityPropertyDescriptor descriptor;

	@Spy
	private DefaultViewElementBuilderContext builderContext = new DefaultViewElementBuilderContext();

	@Spy
	private TextboxFormElement box = new TextboxFormElement();

	private RequiredControlPostProcessor<ViewElement> postProcessor = new RequiredControlPostProcessor<>();

	@Before
	public void before() {
		builderContext.setAttribute( EntityPropertyDescriptor.class, descriptor );
	}

	@Test
	public void nothingHappensIfDifferentViewElementType() {
		postProcessor.postProcess( builderContext, new LabelFormElement() );
		verify( builderContext, never() ).getAttribute( EntityAttributes.PROPERTY_REQUIRED, Boolean.class );
	}

	@Test
	public void requiredSetIfAttributeIsTrue() {
		when( descriptor.getAttribute( EntityAttributes.PROPERTY_REQUIRED, Boolean.class ) ).thenReturn( true );
		postProcessor.postProcess( builderContext, box );
		assertThat( box.isRequired() ).isTrue();
	}

	@Test
	public void requiredNotSetIfAttributeIsFalse() {
		box.setRequired( true );
		when( descriptor.getAttribute( EntityAttributes.PROPERTY_REQUIRED, Boolean.class ) ).thenReturn( false );
		postProcessor.postProcess( builderContext, box );
		assertThat( box.isRequired() ).isFalse();
	}
}
