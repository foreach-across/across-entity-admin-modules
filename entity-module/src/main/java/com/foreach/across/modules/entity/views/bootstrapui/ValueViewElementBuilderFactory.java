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

import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.EntityRegistry;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.views.EntityViewElementBuilderFactorySupport;
import com.foreach.across.modules.entity.views.ViewElementLookupRegistry;
import com.foreach.across.modules.entity.views.ViewElementMode;
import com.foreach.across.modules.entity.views.util.EntityViewElementUtils;
import com.foreach.across.modules.web.ui.DefaultViewElementBuilderContext;
import com.foreach.across.modules.web.ui.ViewElementBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Special builder factory that will check if the descriptor matches a registered entity, and if so will
 * try fetching the control for the requested {@link ViewElementMode} from the entity configuration instead.
 * <p/>
 * This allows a user to configure a default {@link ViewElementMode#VALUE} or {@link ViewElementMode#LIST_VALUE} on an
 * {@link EntityConfiguration} so all properties of that entity configuration type will use that builder.
 *
 * @author Arne Vandamme
 * @since 2.1.0
 */
@Component
@RequiredArgsConstructor
class ValueViewElementBuilderFactory extends EntityViewElementBuilderFactorySupport<ViewElementBuilder>
{
	public static final String READONLY = "entityValue";

	private final TextViewElementBuilderFactory textViewElementBuilderFactory;
	private final EntityRegistry entityRegistry;

	@Override
	public boolean supports( String viewElementType ) {
		return READONLY.equals( viewElementType );
	}

	@SuppressWarnings("unchecked")
	@Override
	protected ViewElementBuilder createInitialBuilder( EntityPropertyDescriptor descriptor, ViewElementMode viewElementMode, String viewElementType ) {
		Class<?> propertyType = descriptor.getPropertyType();

		if ( propertyType != null ) {
			EntityConfiguration<?> entityConfiguration = entityRegistry.getEntityConfiguration( propertyType );

			ViewElementLookupRegistry lookupRegistry = entityConfiguration.getAttribute( ViewElementLookupRegistry.class );

			if ( lookupRegistry != null ) {
				ViewElementBuilder baseBuilder = lookupRegistry.getViewElementBuilder( viewElementMode );

				if ( baseBuilder != null ) {
					return ctx -> {
						// Set the property as the actual entity
						Object entity = EntityViewElementUtils.currentEntity( ctx );

						DefaultViewElementBuilderContext nested = new DefaultViewElementBuilderContext( ctx );
						nested.setAttribute( EntityPropertyDescriptor.class, descriptor );

						EntityViewElementUtils.setCurrentEntity( nested, descriptor.getValueFetcher().getValue( entity ) );

						return baseBuilder.build( nested );
					};
				}
			}
		}

		return textViewElementBuilderFactory.createBuilder( descriptor, viewElementMode, viewElementType );
	}
}
