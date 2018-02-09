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
import com.foreach.across.modules.entity.views.util.EntityViewElementUtils;
import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.ViewElementPostProcessor;
import lombok.val;

/**
 * Base class for an element post-processor that looks for the current {@link EntityPropertyDescriptor}
 * set on the {@link ViewElementBuilderContext}. If there is none, no post-processing will be done.
 * <p/>
 * This is a useful base class for creating general post-processors that determine their actions
 * based on property descriptor attributes when they are being used on a property bound element.
 *
 * @param <T> view element
 * @author Arne Vandamme
 * @since 3.0.0
 */
public abstract class AbstractPropertyDescriptorAwarePostProcessor<T extends ViewElement> implements ViewElementPostProcessor<T>
{
	@Override
	public final void postProcess( ViewElementBuilderContext builderContext, T element ) {
		val descriptor = EntityViewElementUtils.currentPropertyDescriptor( builderContext );

		if ( descriptor != null ) {
			postProcess( builderContext, element, descriptor );
		}
	}

	protected abstract void postProcess( ViewElementBuilderContext builderContext, T element, EntityPropertyDescriptor propertyDescriptor );
}
