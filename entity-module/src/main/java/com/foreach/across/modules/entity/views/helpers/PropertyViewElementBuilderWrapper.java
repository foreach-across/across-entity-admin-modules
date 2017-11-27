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

import com.foreach.across.modules.bootstrapui.elements.builder.OptionFormElementBuilder;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.ViewElementBuilder;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.ViewElementPostProcessor;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.Collection;

/**
 * Wrapper for a {@link com.foreach.across.modules.web.ui.ViewElementBuilder} that sets the {@link EntityPropertyDescriptor}
 * attribute on the builder context and applies additional post processors after the initial builder.
 *
 * @author Arne Vandamme
 * @see com.foreach.across.modules.entity.views.EntityViewElementBuilderServiceImpl
 * @since 2.2.0
 */
@RequiredArgsConstructor
public final class PropertyViewElementBuilderWrapper<T extends ViewElement> implements ViewElementBuilder<T>
{
	@Getter
	@NonNull
	private final ViewElementBuilder<T> targetBuilder;

	@Getter
	private final EntityPropertyDescriptor propertyDescriptor;

	private final Collection<ViewElementPostProcessor<T>> postProcessors;

	@Override
	public T build( ViewElementBuilderContext builderContext ) {
		EntityPropertyDescriptor previous = builderContext.getAttribute( EntityPropertyDescriptor.class );

		try {
			builderContext.setAttribute( EntityPropertyDescriptor.class, propertyDescriptor );

			T original = targetBuilder.build( builderContext );
			postProcessors.forEach( pp -> pp.postProcess( builderContext, original ) );
			return original;
		}
		finally {
			builderContext.setAttribute( EntityPropertyDescriptor.class, previous );
		}
	}

	/**
	 * Fetches the target builder for a {@link ViewElementBuilder}.  If the builder is a {@link PropertyViewElementBuilderWrapper},
	 * the actual target builder will be returned.  Else the original builder is considered the target builder.
	 *
	 * @param builder to retrieve the target from
	 * @return current builder or unwrapped target
	 */
	public static ViewElementBuilder retrieveTargetBuilder( ViewElementBuilder builder ) {
		return builder instanceof PropertyViewElementBuilderWrapper ? ( (PropertyViewElementBuilderWrapper) builder ).getTargetBuilder() : builder;
	}
}
