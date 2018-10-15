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

package com.foreach.across.modules.entity.views.bootstrapui;

import com.foreach.across.modules.bootstrapui.elements.TextboxFormElement;
import com.foreach.across.modules.entity.conditionals.ConditionalOnBootstrapUI;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.views.EntityViewElementBuilderFactorySupport;
import com.foreach.across.modules.entity.views.ViewElementMode;
import com.foreach.across.modules.entity.views.bootstrapui.elements.builder.MultiValueControlViewElementBuilder;
import com.foreach.across.modules.entity.views.bootstrapui.processors.element.PropertyPlaceholderTextPostProcessor;
import com.foreach.across.modules.entity.views.util.EntityViewElementUtils;
import com.foreach.across.modules.web.ui.ViewElementBuilder;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.ViewElementPostProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Simple implementation that supports an unordered (set) of Strings as property value.
 *
 * @author Arne Vandamme
 * @since 2.0.0
 */
@ConditionalOnBootstrapUI
@Component
public class MultiValueElementBuilderFactory extends EntityViewElementBuilderFactorySupport<ViewElementBuilder>
{
	public static final String ELEMENT_TYPE = "entityMultiValue";

	@Override
	public boolean supports( String viewElementType ) {
		return ELEMENT_TYPE.equals( viewElementType );
	}

	@Override
	protected ViewElementBuilder createInitialBuilder( EntityPropertyDescriptor propertyDescriptor, ViewElementMode viewElementMode, String viewElementType ) {
		return new MultiValueControlViewElementBuilder()
				.name( propertyDescriptor.getName() )
				.controlName( propertyDescriptor.getName() )
				.postProcessor( EntityViewElementUtils.controlNamePostProcessor( propertyDescriptor ) )
				.postProcessor( new PropertyValueFetcher( propertyDescriptor ) )
				.postProcessor( new PropertyPlaceholderTextPostProcessor<>() );
	}

	@SuppressWarnings("unchecked")
	@RequiredArgsConstructor
	private static class PropertyValueFetcher implements ViewElementPostProcessor<TextboxFormElement>
	{
		private final EntityPropertyDescriptor propertyDescriptor;

		@Override
		public void postProcess( ViewElementBuilderContext builderContext, TextboxFormElement textbox ) {
			Object entity = EntityViewElementUtils.currentEntity( builderContext );
			textbox.setAttribute( "values", propertyDescriptor.getValueFetcher().getValue( entity ) );
		}
	}
}
