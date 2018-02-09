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
import com.foreach.across.modules.web.ui.DefaultViewElementBuilderContext;
import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.elements.TextViewElement;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TestEntityPropertyDescriptorAwareElementPostProcessor
{
	@Spy
	private AbstractPropertyDescriptorAwarePostProcessor<ViewElement> postProcessor;

	private TextViewElement element = new TextViewElement();
	private ViewElementBuilderContext builderContext = new DefaultViewElementBuilderContext();

	@Test
	public void neverForwardedIfNoPropertyDescriptor() {
		postProcessor.postProcess( builderContext, element );
		verify( postProcessor, never() ).postProcess( eq( builderContext ), eq( element ), any() );
	}

	@Test
	public void forwardedIfThereIsAPropertyDescriptor() {
		EntityPropertyDescriptor descriptor = mock( EntityPropertyDescriptor.class );
		builderContext.setAttribute( EntityPropertyDescriptor.class, descriptor );

		postProcessor.postProcess( builderContext, element );
		verify( postProcessor ).postProcess( builderContext, element, descriptor );
	}
}
