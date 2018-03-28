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

package com.foreach.across.modules.entity.views.helpers;

import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.web.ui.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.*;

/**
 * @author Arne Vandamme
 * @since 2.2.0
 */
@RunWith(MockitoJUnitRunner.class)
public class TestPropertyViewElementBuilderWrapper
{
	@Mock
	private EntityPropertyDescriptor propertyDescriptor;

	@Mock
	private ViewElementBuilder<ViewElement> targetBuilder;

	@Mock
	private ViewElement element;

	private ViewElementBuilderContext builderContext = new DefaultViewElementBuilderContext();

	@Test
	public void previousPropertyDescriptorIsResetAfterBuilding() {
		EntityPropertyDescriptor original = mock( EntityPropertyDescriptor.class );
		builderContext.setAttribute( EntityPropertyDescriptor.class, original );

		doAnswer( invocation -> {
			assertSame( propertyDescriptor, invocation.<ViewElementBuilderContext>getArgument( 0 ).getAttribute( EntityPropertyDescriptor.class ) );
			return element;
		} ).when( targetBuilder ).build( builderContext );

		PropertyViewElementBuilderWrapper wrapper = new PropertyViewElementBuilderWrapper<>( targetBuilder, propertyDescriptor, Collections.emptyList() );
		assertSame( element, wrapper.build( builderContext ) );

		assertSame( original, builderContext.getAttribute( EntityPropertyDescriptor.class ) );
	}

	@Test
	@SuppressWarnings("unchecked")
	public void postProcessorsAreAppliedAfterBuilder() {
		ViewElementPostProcessor<ViewElement> one = mock( ViewElementPostProcessor.class );
		ViewElementPostProcessor<ViewElement> two = mock( ViewElementPostProcessor.class );

		when( targetBuilder.build( builderContext ) ).thenReturn( element );

		PropertyViewElementBuilderWrapper wrapper = new PropertyViewElementBuilderWrapper<>( targetBuilder, propertyDescriptor, Arrays.asList( one, two ) );
		assertSame( element, wrapper.build( builderContext ) );

		InOrder inOrder = inOrder( one, two );
		inOrder.verify( one ).postProcess( builderContext, element );
		inOrder.verify( two ).postProcess( builderContext, element );
	}
}
