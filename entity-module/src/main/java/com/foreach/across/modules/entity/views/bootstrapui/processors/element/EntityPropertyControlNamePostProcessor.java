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

import com.foreach.across.modules.bootstrapui.elements.processor.ControlNamePrefixingPostProcessor;
import com.foreach.across.modules.entity.EntityAttributes;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.views.request.EntityViewCommand;
import com.foreach.across.modules.web.ui.*;
import com.foreach.across.modules.web.ui.elements.ContainerViewElement;
import org.springframework.util.Assert;

import java.util.function.Predicate;

/**
 * Post processor for {@link com.foreach.across.modules.bootstrapui.elements.FormInputElement} that will prefix the control name
 * with <strong>entity</strong> if the builder context contains an {@link com.foreach.across.modules.entity.views.request.EntityViewCommand}.
 * Optionally specify a child element predicate.  If one is specified and the element is a {@link com.foreach.across.modules.web.ui.elements.ContainerViewElement},
 * all children matching the predicate in the entire element tree of the container will also be prefixed.
 * <p/>
 * Use the static {@link #registerForProperty(EntityPropertyDescriptor, ViewElementBuilder, Predicate)} method to conditionally register the postprocessor
 * for entity properties.  Only registers the postprocessor if:
 * <ul>
 * <li>the builder supports postprocessors by extending {@link com.foreach.across.modules.web.ui.ViewElementBuilderSupport}</li>
 * <li>the descriptor should be a native property (presence of {@link com.foreach.across.modules.entity.EntityAttributes#NATIVE_PROPERTY_DESCRIPTOR} attribute)</li>
 * <li>the descriptor should not have a control name specified (absence of {@link com.foreach.across.modules.entity.EntityAttributes#CONTROL_NAME} attribute)</li>
 * </ul>
 *
 * @author Arne Vandamme
 * @see ControlNamePrefixingPostProcessor
 * @since 2.0.0
 */
public final class EntityPropertyControlNamePostProcessor<T extends ViewElement> implements ViewElementPostProcessor<T>
{
	private final static ControlNamePrefixingPostProcessor PREFIXING_POST_PROCESSOR = new ControlNamePrefixingPostProcessor<>( "entity" );

	private final Predicate<ViewElement> childElementPredicate;

	/**
	 * Create a new post processor that will always prefix the current element
	 * if a {@link com.foreach.across.modules.entity.views.request.EntityViewCommand} is present.
	 */
	public EntityPropertyControlNamePostProcessor() {
		childElementPredicate = null;
	}

	/**
	 * Create a new post processor that will also prefix all child elements if they match the given predicate.
	 *
	 * @param childElementPredicate predicate that child elements should match in order to be prefixed
	 */
	public EntityPropertyControlNamePostProcessor( Predicate<ViewElement> childElementPredicate ) {
		this.childElementPredicate = childElementPredicate;
	}

	@Override
	public void postProcess( ViewElementBuilderContext builderContext, T element ) {
		if ( builderContext.hasAttribute( EntityViewCommand.class ) ) {
			PREFIXING_POST_PROCESSOR.postProcess( builderContext, element );

			if ( childElementPredicate != null && element instanceof ContainerViewElement ) {
				( (ContainerViewElement) element ).findAll( childElementPredicate )
				                                  .forEach( input -> PREFIXING_POST_PROCESSOR.postProcess( builderContext, input ) );
			}
		}
	}

	/**
	 * Attempts to register the post processor if the property descriptor is a native property and does not have a control name specified.
	 * If a child element predicate is specified and the resulting parent element is a container, all input elements in the container tree
	 * that match the predicate will also be prefixed.
	 * <p/>
	 * If the builder is not of type {@link com.foreach.across.modules.web.ui.ViewElementBuilderSupport} this method will do nothing.
	 *
	 * @param propertyDescriptor    to check the attributes
	 * @param builder               to add the postprocessor to
	 * @param childElementPredicate optional predicate for all child elements that should also be prefixed
	 */
	@SuppressWarnings("unchecked")
	public static void registerForProperty( EntityPropertyDescriptor propertyDescriptor,
	                                        ViewElementBuilder<? extends ViewElement> builder,
	                                        Predicate<ViewElement> childElementPredicate ) {
		Assert.notNull( propertyDescriptor );
		Assert.notNull( builder );

		if ( builder instanceof ViewElementBuilderSupport
				&& propertyDescriptor.hasAttribute( EntityAttributes.NATIVE_PROPERTY_DESCRIPTOR )
				&& !propertyDescriptor.hasAttribute( EntityAttributes.CONTROL_NAME ) ) {
			( (ViewElementBuilderSupport) builder ).postProcessor( new EntityPropertyControlNamePostProcessor<>( childElementPredicate ) );
		}
	}
}
