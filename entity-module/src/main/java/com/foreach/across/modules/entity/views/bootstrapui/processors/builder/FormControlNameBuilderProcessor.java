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

package com.foreach.across.modules.entity.views.bootstrapui.processors.builder;

import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.views.EntityViewElementBuilderProcessor;
import com.foreach.across.modules.entity.views.ViewElementMode;
import com.foreach.across.modules.entity.views.bootstrapui.processors.element.EntityPropertyControlNamePostProcessor;
import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.ViewElementBuilder;

import java.util.function.Predicate;

/**
 * Registers the {@link EntityPropertyControlNamePostProcessor} on the builder if we are in a control {@link ViewElementMode}.
 *
 * @author Arne Vandamme
 * @see EntityPropertyControlNamePostProcessor
 * @since 2.0.0
 */
@SuppressWarnings("unchecked")
public class FormControlNameBuilderProcessor<T extends ViewElementBuilder> implements EntityViewElementBuilderProcessor<T>
{
	private final Predicate<ViewElement> childElementPredicate;

	public FormControlNameBuilderProcessor() {
		this.childElementPredicate = null;
	}

	public FormControlNameBuilderProcessor( Predicate<ViewElement> childElementPredicate ) {
		this.childElementPredicate = childElementPredicate;
	}

	@Override
	public void process( EntityPropertyDescriptor propertyDescriptor, ViewElementMode viewElementMode, String viewElementType, T builder ) {
		if ( ViewElementMode.isControl( viewElementMode ) ) {
			EntityPropertyControlNamePostProcessor.registerForProperty( propertyDescriptor, builder, childElementPredicate );
		}
	}
}
