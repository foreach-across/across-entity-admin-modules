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

import com.foreach.across.modules.bootstrapui.utils.BootstrapElementUtils;
import com.foreach.across.modules.entity.EntityAttributes;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.views.request.EntityViewCommand;
import com.foreach.across.modules.entity.views.util.EntityViewElementUtils;
import com.foreach.across.modules.web.ui.*;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;

/**
 * Post processor for {@link com.foreach.across.modules.bootstrapui.elements.FormInputElement} that will prefix the control name
 * with <strong>entity</strong> if the builder context contains an {@link com.foreach.across.modules.entity.views.request.EntityViewCommand}
 * and the value of attribute {@link EntityPropertyControlNamePostProcessor#PREFIX_CONTROL_NAMES} on the builder context is set to {@code true}.
 * Usually the attribute value is set by the {@link com.foreach.across.modules.entity.views.processors.PropertyRenderingViewProcessor} if the right
 * {@link com.foreach.across.modules.entity.views.ViewElementMode} is being requested.
 * <p/>
 * Optionally specify a child element predicate.  If one is specified and the element is a {@link com.foreach.across.modules.web.ui.elements.ContainerViewElement},
 * all children matching the predicate in the entire element tree of the container will also be prefixed.
 * <p/>
 * Use the static {@link #registerForProperty(EntityPropertyDescriptor, ViewElementBuilde)} method to conditionally register the postprocessor
 * for entity properties.  Only registers the postprocessor if:
 * <ul>
 * <li>the builder supports postprocessors by extending {@link com.foreach.across.modules.web.ui.ViewElementBuilderSupport}</li>
 * <li>the descriptor should be a native property (presence of {@link com.foreach.across.modules.entity.EntityAttributes#NATIVE_PROPERTY_DESCRIPTOR} attribute)</li>
 * <li>the descriptor should not have a control name specified (absence of {@link com.foreach.across.modules.entity.EntityAttributes#CONTROL_NAME} attribute)</li>
 * </ul>
 *
 * @author Arne Vandamme
 * @since 2.0.0
 */
@Deprecated
public final class EntityPropertyControlNamePostProcessor<T extends ViewElement> implements ViewElementPostProcessor<T>
{
	/**
	 * Name of the {@link ViewElementBuilderContext} attribute that should contain {@code true} if actual prefixing should be done.
	 */
	public final static String PREFIX_CONTROL_NAMES = EntityPropertyControlNamePostProcessor.class.getName() + ".enabled";

	@Override
	public void postProcess( ViewElementBuilderContext builderContext, T element ) {
		if ( builderContext.hasAttribute( EntityViewCommand.class )
				&& Boolean.TRUE.equals( builderContext.getAttribute( PREFIX_CONTROL_NAMES, Boolean.class ) ) ) {
			EntityPropertyDescriptor descriptor = EntityViewElementUtils.currentPropertyDescriptor( builderContext );

			if ( descriptor != null ) {
				BootstrapElementUtils.prefixControlNames( "entity" )
				                     .controlNamePredicate(
						                     controlName -> StringUtils.startsWithAny( controlName, descriptor.getName(), "_" + descriptor.getName() )
				                     )
				                     .accept( element );
			}
		}
	}

	/**
	 * Attempts to register the post processor if the property descriptor is a native property and does not have a control name specified.
	 * All controls part of that
	 * <p/>
	 * If the builder is not of type {@link com.foreach.across.modules.web.ui.ViewElementBuilderSupport} this method will do nothing.
	 *
	 * @param propertyDescriptor to check the attributes
	 * @param builder            to add the postprocessor to
	 */
	@SuppressWarnings("unchecked")
	public static void registerForProperty( @NonNull EntityPropertyDescriptor propertyDescriptor,
	                                        @NonNull ViewElementBuilder<? extends ViewElement> builder ) {
		if ( builder instanceof ViewElementBuilderSupport
				&& propertyDescriptor.hasAttribute( EntityAttributes.NATIVE_PROPERTY_DESCRIPTOR )
				&& !propertyDescriptor.hasAttribute( EntityAttributes.CONTROL_NAME ) ) {
			( (ViewElementBuilderSupport) builder ).postProcessor( new EntityPropertyControlNamePostProcessor<>() );
		}
	}
}
