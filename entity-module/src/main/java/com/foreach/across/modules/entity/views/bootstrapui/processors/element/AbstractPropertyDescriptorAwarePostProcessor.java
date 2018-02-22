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
import org.springframework.core.ResolvableType;

/**
 * Base class for an element post-processor that looks for the current {@link EntityPropertyDescriptor}
 * set on the {@link ViewElementBuilderContext}. If there is none, no post-processing will be done.
 * <p/>
 * This is a useful base class for creating general post-processors that determine their actions
 * based on property descriptor attributes when they are being used on a property bound element.
 * <p/>
 * This class has two generic types. The first defines the base type to which this post-processor can be added,
 * and determines for which elements you can actually add the post processor without it throwing class cast exceptions.
 * The second generic type defines the actual type that the element should match before the post processing should happen,
 * an actual instance check will be performed on this type. This second type does not need to be an actual {@code ViewElement} type.
 *
 * @param <T> the supported input view element type to which this processor can be configured (type matching)
 * @param <Y> the actual type that the element must match before the actual {@link #postProcess(ViewElementBuilderContext, Object, EntityPropertyDescriptor)} will be called
 * @author Arne Vandamme
 * @since 3.0.0
 */
public abstract class AbstractPropertyDescriptorAwarePostProcessor<T extends ViewElement, Y> implements ViewElementPostProcessor<T>
{
	private final Class<Y> typedClass;

	@SuppressWarnings("unchecked")
	AbstractPropertyDescriptorAwarePostProcessor() {
		typedClass = (Class<Y>) ResolvableType.forClass( getClass() ).as( AbstractPropertyDescriptorAwarePostProcessor.class ).getGeneric( 1 ).resolve();
	}

	protected AbstractPropertyDescriptorAwarePostProcessor( Class<Y> typedClass ) {
		this.typedClass = typedClass;
	}

	@Override
	public final void postProcess( ViewElementBuilderContext builderContext, T element ) {
		val descriptor = EntityViewElementUtils.currentPropertyDescriptor( builderContext );

		if ( descriptor != null && typedClass.isInstance( element ) ) {
			postProcess( builderContext, typedClass.cast( element ), descriptor );
		}
	}

	protected abstract void postProcess( ViewElementBuilderContext builderContext, Y element, EntityPropertyDescriptor propertyDescriptor );
}
