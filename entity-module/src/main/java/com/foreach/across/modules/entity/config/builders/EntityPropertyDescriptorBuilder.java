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

package com.foreach.across.modules.entity.config.builders;

import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.registry.properties.MutableEntityPropertyDescriptor;
import com.foreach.across.modules.entity.registry.properties.SimpleEntityPropertyDescriptor;
import com.foreach.across.modules.entity.views.ViewElementLookupRegistry;
import com.foreach.across.modules.entity.views.ViewElementLookupRegistryImpl;
import com.foreach.across.modules.entity.views.ViewElementMode;
import com.foreach.across.modules.entity.views.support.SpelValueFetcher;
import com.foreach.across.modules.entity.views.support.ValueFetcher;
import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.ViewElementBuilder;
import com.foreach.across.modules.web.ui.ViewElementPostProcessor;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.TypeDescriptor;

/**
 * Builder for a configuring a single {@link SimpleEntityPropertyDescriptor}.  The builder can also be
 * applied to any existing {@link MutableEntityPropertyDescriptor}.
 *
 * @author Arne Vandamme
 * @since 2.0.0
 */
public class EntityPropertyDescriptorBuilder extends AbstractWritableAttributesBuilder
{
	protected final Logger LOG = LoggerFactory.getLogger( getClass() );

	private final ViewElementLookupRegistryImpl viewElementLookupRegistry = new ViewElementLookupRegistryImpl();
	private final String name;
	private String displayName;
	private ValueFetcher valueFetcher;
	private Boolean hidden, writable, readable;
	private EntityPropertyDescriptor parent;
	private Class<?> propertyType;
	private TypeDescriptor propertyTypeDescriptor;

	/**
	 * Create a new builder for the entity property with the given name.
	 * The name of the property should be unique within the registry, and cannot be changed afterwards.
	 *
	 * @param name of the property
	 */
	public EntityPropertyDescriptorBuilder( @NonNull String name ) {
		this.name = name;
	}

	/**
	 * Set the parent descriptor.  If set, all properties from the parent will be inherited
	 * unless explicitly configured.
	 *
	 * @param parent descriptor
	 * @return current builder
	 */
	public EntityPropertyDescriptorBuilder parent( EntityPropertyDescriptor parent ) {
		this.parent = parent;
		return this;
	}

	/**
	 * Set the simple property type of the descriptor.  Favour {@link #propertyType(TypeDescriptor)} instead.
	 *
	 * @param propertyType class of the property value
	 * @return current builder
	 */
	public EntityPropertyDescriptorBuilder propertyType( Class<?> propertyType ) {
		this.propertyType = propertyType;
		return this;
	}

	/**
	 * Sets the full type descriptor for this property.
	 *
	 * @param typeDescriptor instance for the property value
	 * @return current builder
	 */
	public EntityPropertyDescriptorBuilder propertyType( TypeDescriptor typeDescriptor ) {
		this.propertyTypeDescriptor = typeDescriptor;
		return this;
	}

	@Override
	public EntityPropertyDescriptorBuilder attribute( String name, Object value ) {
		return (EntityPropertyDescriptorBuilder) super.attribute( name, value );
	}

	@Override
	public <S> EntityPropertyDescriptorBuilder attribute( Class<S> type, S value ) {
		return (EntityPropertyDescriptorBuilder) super.attribute( type, value );
	}

	/**
	 * @param displayName Display name to be configured on the property.
	 * @return current builder
	 */
	public EntityPropertyDescriptorBuilder displayName( String displayName ) {
		this.displayName = displayName;
		return this;
	}

	/**
	 * @param expression SpEL expression that should be used as value.
	 * @return current builder
	 */
	public EntityPropertyDescriptorBuilder spelValueFetcher( @NonNull String expression ) {
		return valueFetcher( new SpelValueFetcher<>( expression ) );
	}

	/**
	 * @param valueFetcher fetcher to configure on the property
	 * @return current builder
	 */
	public <U> EntityPropertyDescriptorBuilder valueFetcher( ValueFetcher<U> valueFetcher ) {
		this.valueFetcher = valueFetcher;
		return this;
	}

	/**
	 * @param writable true if the property should be writable in a UI
	 * @return current builder
	 */
	public EntityPropertyDescriptorBuilder writable( boolean writable ) {
		this.writable = writable;
		return this;
	}

	/**
	 * @param readable true if the property should be viewable/readable in a UI
	 * @return current builder
	 */
	public EntityPropertyDescriptorBuilder readable( boolean readable ) {
		this.readable = readable;
		return this;
	}

	/**
	 * @param hidden true if the property should be hidden from a UI
	 * @return current builder
	 */
	public EntityPropertyDescriptorBuilder hidden( boolean hidden ) {
		this.hidden = hidden;
		return this;
	}

	/**
	 * Set the caching mode for a particular {@link ViewElementMode}.  By default caching is enabled.
	 *
	 * @param mode      to set the caching option for
	 * @param cacheable true if {@link ViewElementBuilder}s should be cached
	 * @return current builder
	 */
	public EntityPropertyDescriptorBuilder viewElementModeCaching( ViewElementMode mode, boolean cacheable ) {
		viewElementLookupRegistry.setCacheable( mode, cacheable );
		return this;
	}

	/**
	 * Set the {@link com.foreach.across.modules.web.ui.ViewElement} type of a particular {@link ViewElementMode}.
	 *
	 * @param mode            to set the type for
	 * @param viewElementType to use
	 * @return current builder
	 */
	public EntityPropertyDescriptorBuilder viewElementType( ViewElementMode mode, String viewElementType ) {
		viewElementLookupRegistry.setViewElementType( mode, viewElementType );
		return this;
	}

	/**
	 * Set the {@link ViewElementBuilder} to use for a particular {@link ViewElementMode}.
	 *
	 * @param mode               to set the builder for
	 * @param viewElementBuilder to use
	 * @return current builder
	 */
	public EntityPropertyDescriptorBuilder viewElementBuilder( ViewElementMode mode,
	                                                           ViewElementBuilder viewElementBuilder ) {
		viewElementLookupRegistry.setViewElementBuilder( mode, viewElementBuilder );
		return this;
	}

	/**
	 * Add a {@link ViewElementPostProcessor} to apply to the default {@link ViewElementBuilder}.
	 * Note that postprocessor will be ignored if a custom {@link ViewElementBuilder}
	 * was set using {@link #viewElementBuilder(ViewElementMode, ViewElementBuilder)}.
	 *
	 * @param mode                     to add the postprocessor for
	 * @param viewElementPostProcessor to add
	 * @return current builder
	 */
	public <U extends ViewElement> EntityPropertyDescriptorBuilder viewElementPostProcessor(
			ViewElementMode mode,
			ViewElementPostProcessor<U> viewElementPostProcessor ) {
		viewElementLookupRegistry.addViewElementPostProcessor( mode, viewElementPostProcessor );
		return this;
	}

	/**
	 * Build a new descriptor with settings configured.
	 *
	 * @return descriptor
	 */
	public MutableEntityPropertyDescriptor build() {
		SimpleEntityPropertyDescriptor descriptor = new SimpleEntityPropertyDescriptor( name, parent );

		if ( parent == null ) {
			descriptor.setDisplayName( name );
			descriptor.setValueFetcher( new SpelValueFetcher( name ) );
			descriptor.setReadable( true );
		}

		apply( descriptor );

		return descriptor;
	}

	/**
	 * Applies this builder to the existing descriptor.
	 *
	 * @param descriptor whose settings to update
	 */
	public void apply( @NonNull MutableEntityPropertyDescriptor descriptor ) {
		if ( name != null && !name.equals( descriptor.getName() ) ) {
			LOG.error( "Unable to change the name of an existing EntityPropertyDescriptor: {} to {}",
			           descriptor.getName(), name );
		}

		// Update configured properties
		if ( displayName != null ) {
			descriptor.setDisplayName( displayName );
		}

		if ( valueFetcher != null ) {
			descriptor.setValueFetcher( valueFetcher );
			descriptor.setReadable( true );
		}

		if ( writable != null ) {
			descriptor.setWritable( writable );
		}

		if ( readable != null ) {
			descriptor.setReadable( readable );
		}

		if ( hidden != null ) {
			descriptor.setHidden( hidden );
		}

		if ( propertyTypeDescriptor != null ) {
			descriptor.setPropertyTypeDescriptor( propertyTypeDescriptor );
		}
		else if ( propertyType != null ) {
			descriptor.setPropertyType( propertyType );
		}

		applyAttributes( descriptor );

		ViewElementLookupRegistry existingLookupRegistry = descriptor.getAttribute( ViewElementLookupRegistry.class );

		if ( existingLookupRegistry != null ) {
			viewElementLookupRegistry.mergeInto( existingLookupRegistry );
		}
		else {
			descriptor.setAttribute( ViewElementLookupRegistry.class, viewElementLookupRegistry.clone() );
		}
	}
}
