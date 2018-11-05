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

import com.foreach.across.core.support.ReadableAttributes;
import com.foreach.across.modules.entity.bind.EntityPropertiesBinder;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistry;
import com.foreach.across.modules.entity.registry.properties.EntityPropertySelector;
import com.foreach.across.modules.entity.views.EntityViewElementBuilderService;
import com.foreach.across.modules.entity.views.ViewElementMode;
import com.foreach.across.modules.entity.web.EntityViewModel;
import com.foreach.across.modules.web.ui.DefaultViewElementBuilderContext;
import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.ViewElementBuilder;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import lombok.Getter;
import lombok.NonNull;

import javax.annotation.concurrent.NotThreadSafe;
import java.util.*;

/**
 * Helper to quickly generate {@link com.foreach.across.modules.web.ui.ViewElement} instances
 * for a number of properties of a certain {@link com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistry}.
 * <p>
 * The properties are determined by the selector assigned through {@link #setPropertySelector(EntityPropertySelector)}.
 * Behind the scenes a {@link #getPropertiesBinder()} will be used for fetching the values.
 * <p>
 * Note: this class is not thread safe.
 *
 * @author Arne Vandamme
 */
@NotThreadSafe
public class EntityViewElementBatch<T> extends DefaultViewElementBuilderContext
{
	private final EntityViewElementBuilderService viewElementBuilderService;

	private EntityPropertyRegistry propertyRegistry;
	private EntityPropertySelector propertySelector;

	private ViewElementMode viewElementMode;

	private Map<String, Object> builderHints = Collections.emptyMap();

	@Getter
	private EntityPropertiesBinder propertiesBinder;

	public EntityViewElementBatch( EntityViewElementBuilderService viewElementBuilderService ) {
		super( false );
		this.viewElementBuilderService = viewElementBuilderService;
	}

	public void setPropertyRegistry( EntityPropertyRegistry propertyRegistry ) {
		this.propertyRegistry = propertyRegistry;
		if ( propertyRegistry != null ) {
			propertiesBinder = new EntityPropertiesBinder( propertyRegistry );
			setAttribute( EntityPropertiesBinder.class, propertiesBinder );
		}
		else {
			removeAttribute( EntityPropertiesBinder.class );
		}
	}

	public void setParentViewElementBuilderContext( ViewElementBuilderContext viewElementBuilderContext ) {
		setParent( viewElementBuilderContext );
	}

	@Override
	protected ViewElementBuilderContext getParent() {
		return (ViewElementBuilderContext) super.getParent();
	}

	public void setPropertySelector( EntityPropertySelector propertySelector ) {
		this.propertySelector = propertySelector;
	}

	public void setViewElementMode( ViewElementMode viewElementMode ) {
		this.viewElementMode = viewElementMode;
	}

	/**
	 * Set the entity that should be used for the next build.
	 *
	 * @param entity instance
	 */
	public void setEntity( T entity ) {
		setAttribute( EntityViewModel.ENTITY, entity );
		propertiesBinder.setEntity( entity );
	}

	/**
	 * Set a map of property builder hints.  The keys are the property names, the value should be one of the
	 * following:
	 * <ul>
	 * <li>{@link ViewElementMode}: for which specific mode the builder should be retrieved</li>
	 * <li>{@link String}: which element type should be generated for the property</li>
	 * <li>{@link ViewElementBuilder}: element builder that should be used for building the element</li>
	 * <li>{@link ViewElement}: fixed element that should be returned</li>
	 * </ul>
	 *
	 * @param builderHints map instance, should not be null
	 */
	public void setBuilderHints( @NonNull Map<String, Object> builderHints ) {
		this.builderHints = builderHints;
	}

	/**
	 * Generates the final elements.
	 *
	 * @return map of property name/ element
	 */
	public Map<String, ViewElement> build() {
		return build(
				Optional.ofNullable( getParent() )
				        .orElseGet(
						        () -> ViewElementBuilderContext.retrieveGlobalBuilderContext().orElseGet( DefaultViewElementBuilderContext::new )
				        )
		);
	}

	/**
	 * Generates the final elements using the specified builder context as parent.
	 * This will override the default parent context set with {@link #setParentViewElementBuilderContext(ViewElementBuilderContext)}.
	 *
	 * @param parentBuilderContext to use
	 * @return map of property name/element
	 */
	public Map<String, ViewElement> build( ViewElementBuilderContext parentBuilderContext ) {
		List<EntityPropertyDescriptor> descriptors = propertyRegistry.select( propertySelector );

		ReadableAttributes currentParent = getParent();
		setParent( parentBuilderContext );

		Map<String, ViewElement> elements = new LinkedHashMap<>();

		for ( EntityPropertyDescriptor descriptor : descriptors ) {
			String propertyName = descriptor.getName();
			Object builderHint = builderHints.get( propertyName );

			elements.put( propertyName, getViewElement( descriptor, builderHint ) );
		}

		setParent( currentParent );

		return elements;
	}

	private ViewElement getViewElement( EntityPropertyDescriptor descriptor, Object builderHint ) {
		if ( builderHint instanceof ViewElement ) {
			return (ViewElement) builderHint;
		}

		ViewElementBuilder builder = getViewElementBuilder( descriptor, builderHint );
		return builder != null ? builder.build( this ) : null;
	}

	@SuppressWarnings("unchecked")
	private ViewElementBuilder getViewElementBuilder( EntityPropertyDescriptor descriptor, Object builderHint ) {
		if ( builderHint instanceof ViewElementBuilder ) {
			return new PropertyViewElementBuilderWrapper<>( (ViewElementBuilder<ViewElement>) builderHint, descriptor, Collections.emptyList() );
		}

		if ( builderHint instanceof String ) {
			return viewElementBuilderService.createElementBuilder( descriptor, viewElementMode, (String) builderHint );
		}

		ViewElementMode elementMode = builderHint instanceof ViewElementMode ? (ViewElementMode) builderHint : viewElementMode;

		return viewElementBuilderService.getElementBuilder( descriptor, elementMode );
	}
}
