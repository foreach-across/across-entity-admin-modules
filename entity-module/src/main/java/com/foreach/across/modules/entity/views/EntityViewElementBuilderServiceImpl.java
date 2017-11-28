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
package com.foreach.across.modules.entity.views;

import com.foreach.across.core.annotations.RefreshableCollection;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.EntityRegistry;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.util.EntityTypeDescriptor;
import com.foreach.across.modules.entity.util.EntityUtils;
import com.foreach.across.modules.entity.views.helpers.PropertyViewElementBuilderWrapper;
import com.foreach.across.modules.web.ui.ViewElementBuilder;
import com.foreach.across.modules.web.ui.ViewElementPostProcessor;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * @author Arne Vandamme
 */
@Service
@RequiredArgsConstructor
public class EntityViewElementBuilderServiceImpl implements EntityViewElementBuilderService
{
	private Collection<ViewElementTypeLookupStrategy> elementTypeLookupStrategies;
	private Collection<EntityViewElementBuilderFactory> builderFactories;

	private final EntityRegistry entityRegistry;

	@SuppressWarnings("unchecked")
	@Override
	public ViewElementBuilder getElementBuilder( EntityPropertyDescriptor descriptor, ViewElementMode mode ) {
		// todo implement caching once a valid inheritance strategy for ViewElementLookupStrategy has been defined
		return createElementBuilder( descriptor, mode );
	}

	@Override
	@SuppressWarnings("unchecked")
	public ViewElementBuilder createElementBuilder( EntityPropertyDescriptor descriptor, ViewElementMode mode ) {
		LookupHelper lookupHelper = createLookupHelper( descriptor );

		String viewElementType = null;
		ViewElementBuilder<?> builder = null;

		if ( lookupHelper.hasPropertyLookupRegistry() ) {
			builder = lookupHelper.propertyLookupRegistry.getViewElementBuilder( mode );

			if ( builder == null ) {
				viewElementType = lookupHelper.propertyLookupRegistry.getViewElementType( mode );
			}
		}

		if ( ( builder == null && viewElementType == null ) && lookupHelper.hasEntityLookupRegistry() ) {
			ViewElementMode translatedMode = lookupHelper.isMultipleProperty() ? mode.forMultiple() : mode;
			builder = lookupHelper.entityLookupRegistry.getViewElementBuilder( translatedMode );

			if ( builder == null ) {
				viewElementType = lookupHelper.entityLookupRegistry.getViewElementType( translatedMode );
			}
		}

		if ( builder == null && viewElementType == null ) {
			viewElementType = lookupElementType( descriptor, mode );
		}

		if ( builder == null && viewElementType != null ) {
			builder = createElementBuilder( descriptor, mode, viewElementType );
		}

		return builder != null ? new PropertyViewElementBuilderWrapper( builder, descriptor, buildPostProcessors( mode, lookupHelper ) ) : null;
	}

	@SuppressWarnings("unchecked")
	private List<ViewElementPostProcessor> buildPostProcessors( ViewElementMode mode, LookupHelper lookupHelper ) {
		List<ViewElementPostProcessor> postProcessors = new ArrayList<>();

		if ( lookupHelper.hasEntityLookupRegistry() ) {
			postProcessors.addAll( lookupHelper.entityLookupRegistry.getViewElementPostProcessors( mode ) );
		}

		if ( lookupHelper.hasPropertyLookupRegistry() ) {
			postProcessors.addAll( lookupHelper.propertyLookupRegistry.getViewElementPostProcessors( mode ) );
		}

		return postProcessors;
	}

	private String lookupElementType( EntityPropertyDescriptor descriptor, ViewElementMode mode ) {
		return elementTypeLookupStrategies.stream()
		                                  .map( strategy -> strategy.findElementType( descriptor, mode ) )
		                                  .filter( Objects::nonNull )
		                                  .findFirst()
		                                  .orElse( null );
	}

	private LookupHelper createLookupHelper( EntityPropertyDescriptor descriptor ) {
		EntityTypeDescriptor typeDescriptor = EntityUtils.resolveEntityTypeDescriptor( descriptor.getPropertyTypeDescriptor(), entityRegistry );
		ViewElementLookupRegistry propertyLookupRegistry = descriptor.getAttribute( ViewElementLookupRegistry.class );

		if ( typeDescriptor.isTargetTypeResolved() ) {
			EntityConfiguration entityConfiguration = entityRegistry.getEntityConfiguration( typeDescriptor.getSimpleTargetType() );

			if ( entityConfiguration != null ) {
				return new LookupHelper( typeDescriptor, propertyLookupRegistry, entityConfiguration.getAttribute( ViewElementLookupRegistry.class ) );
			}
		}

		return new LookupHelper( typeDescriptor, propertyLookupRegistry, null );
	}

	@Value
	private static class LookupHelper
	{
		private final EntityTypeDescriptor typeDescriptor;
		private final ViewElementLookupRegistry propertyLookupRegistry;

		private final ViewElementLookupRegistry entityLookupRegistry;

		public boolean isResolved() {
			return typeDescriptor.isTargetTypeResolved();
		}

		boolean isMultipleProperty() {
			return typeDescriptor.isCollection();
		}

		boolean hasPropertyLookupRegistry() {
			return propertyLookupRegistry != null;
		}

		boolean hasEntityLookupRegistry() {
			return entityLookupRegistry != null;
		}

	}

	@Override
	public ViewElementBuilder createElementBuilder( EntityPropertyDescriptor descriptor, ViewElementMode mode, String elementType ) {
		for ( EntityViewElementBuilderFactory builderFactory : builderFactories ) {
			if ( builderFactory.supports( elementType ) ) {
				return builderFactory.createBuilder( descriptor, mode, elementType );
			}
		}

		return null;
	}

	@Autowired
	void setElementTypeLookupStrategies( @RefreshableCollection(incremental = true, includeModuleInternals = true) Collection<ViewElementTypeLookupStrategy> elementTypeLookupStrategies ) {
		this.elementTypeLookupStrategies = elementTypeLookupStrategies;
	}

	@Autowired
	void setBuilderFactories( @RefreshableCollection(incremental = true, includeModuleInternals = true) Collection<EntityViewElementBuilderFactory> builderFactories ) {
		this.builderFactories = builderFactories;
	}
}
