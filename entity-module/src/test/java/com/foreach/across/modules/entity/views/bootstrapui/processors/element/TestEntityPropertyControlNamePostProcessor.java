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

import com.foreach.across.modules.bootstrapui.elements.FormInputElement;
import com.foreach.across.modules.bootstrapui.elements.TextboxFormElement;
import com.foreach.across.modules.entity.EntityAttributes;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.views.request.EntityViewCommand;
import com.foreach.across.modules.web.ui.ViewElementBuilder;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.ViewElementBuilderSupport;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

/**
 * @author Arne Vandamme
 * @since 2.0.0
 */
@RunWith(MockitoJUnitRunner.class)
public class TestEntityPropertyControlNamePostProcessor
{
	@Mock
	private ViewElementBuilderContext builderContext;

	@Mock
	private EntityPropertyDescriptor descriptor;

	@Mock
	private ViewElementBuilderSupport builder;

	private TextboxFormElement input, child;
	private EntityPropertyControlNamePostProcessor<FormInputElement> postProcessor;

	@Before
	public void setUp() throws Exception {
		when( builderContext.hasAttribute( EntityViewCommand.class ) ).thenReturn( true );
		when( builderContext.getAttribute( EntityPropertyControlNamePostProcessor.PREFIX_CONTROL_NAMES, Boolean.class ) ).thenReturn( true );
		when( builderContext.getAttribute( EntityPropertyDescriptor.class ) ).thenReturn( descriptor );

		when( descriptor.getName() ).thenReturn( "myprop" );

		postProcessor = new EntityPropertyControlNamePostProcessor<>();
		input = new TextboxFormElement();
		input.setName( "myprop" );

		child = new TextboxFormElement();
		child.setName( "myprop.child" );

		input.addChild( child );
	}

	@Test
	public void notPrefixedIfNoCommand() {
		when( builderContext.hasAttribute( EntityViewCommand.class ) ).thenReturn( false );
		postProcessor.postProcess( builderContext, input );
		assertEquals( "myprop", input.getControlName() );
		assertEquals( "myprop.child", child.getControlName() );
	}

	@Test
	public void notPrefixedIfNoEntityPropertyDescriptor() {
		when( builderContext.hasAttribute( EntityViewCommand.class ) ).thenReturn( true );
		when( builderContext.getAttribute( EntityPropertyDescriptor.class ) ).thenReturn( null );
		postProcessor.postProcess( builderContext, input );
		assertEquals( "myprop", input.getControlName() );
		assertEquals( "myprop.child", child.getControlName() );
	}

	@Test
	public void notPrefixedIfNoAttributeNotSet() {
		when( builderContext.getAttribute( EntityPropertyControlNamePostProcessor.PREFIX_CONTROL_NAMES, Boolean.class ) ).thenReturn( false );
		postProcessor.postProcess( builderContext, input );
		assertEquals( "myprop", input.getControlName() );
		assertEquals( "myprop.child", child.getControlName() );

		when( builderContext.getAttribute( EntityPropertyControlNamePostProcessor.PREFIX_CONTROL_NAMES, Boolean.class ) ).thenReturn( null );
		postProcessor.postProcess( builderContext, input );
		assertEquals( "myprop", input.getControlName() );
		assertEquals( "myprop.child", child.getControlName() );
	}

	@Test
	public void prefixChildren() {
		postProcessor = new EntityPropertyControlNamePostProcessor<>();
		postProcessor.postProcess( builderContext, input );
		assertEquals( "entity.myprop", input.getControlName() );
		assertEquals( "entity.myprop.child", child.getControlName() );
	}

	@Test
	public void prefixOnlyMatchingChildren() {
		input.setControlName( "input" );

		postProcessor = new EntityPropertyControlNamePostProcessor<>();
		postProcessor.postProcess( builderContext, input );

		assertEquals( "input", input.getControlName() );
		assertEquals( "entity.myprop.child", child.getControlName() );
	}

	@Test
	public void dontRegisterIfNotANativeProperty() {
		EntityPropertyControlNamePostProcessor.registerForProperty( descriptor, builder );
		verifyNoMoreInteractions( builder );
	}

	@SuppressWarnings("unchecked")
	@Test
	public void registerIfNativeProperty() {
		when( descriptor.hasAttribute( EntityAttributes.NATIVE_PROPERTY_DESCRIPTOR ) ).thenReturn( true );
		EntityPropertyControlNamePostProcessor.registerForProperty( descriptor, builder );
		verify( builder ).postProcessor( any( EntityPropertyControlNamePostProcessor.class ) );
	}

	@Test
	public void dontRegisterIfControlName() {
		when( descriptor.hasAttribute( EntityAttributes.NATIVE_PROPERTY_DESCRIPTOR ) ).thenReturn( true );
		when( descriptor.hasAttribute( EntityAttributes.CONTROL_NAME ) ).thenReturn( true );
		EntityPropertyControlNamePostProcessor.registerForProperty( descriptor, builder );
		verifyNoMoreInteractions( builder );
	}

	@SuppressWarnings("unchecked")
	@Test
	public void dontFailIfPostProcessorsNotSupported() {
		EntityPropertyControlNamePostProcessor.registerForProperty( descriptor, mock( ViewElementBuilder.class ) );
	}
}
