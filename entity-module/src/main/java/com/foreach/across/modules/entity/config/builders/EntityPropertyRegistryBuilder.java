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

import com.foreach.across.modules.entity.config.AttributeRegistrar;
import com.foreach.across.modules.entity.registry.properties.*;
import com.foreach.across.modules.entity.registry.properties.registrars.LabelPropertiesRegistrar;
import com.foreach.across.modules.entity.views.ViewElementMode;
import com.foreach.across.modules.entity.views.support.ValueFetcher;
import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.ViewElementBuilder;
import com.foreach.across.modules.web.ui.ViewElementPostProcessor;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.TypeDescriptor;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Builder to customize a {@link com.foreach.across.modules.entity.registry.properties.MutableEntityPropertyRegistry}.
 *
 * @author Arne Vandamme
 * @since 2.0.0
 */
public class EntityPropertyRegistryBuilder
{
	private final static Logger LOG = LoggerFactory.getLogger( EntityPropertyRegistryBuilder.class );

	private final Map<String, PropertyDescriptorBuilder> builders = new LinkedHashMap<>();
	private final List<Consumer<MutableEntityPropertyRegistry>> orderedConsumers = new ArrayList<>();

	private String labelBaseProperty;

	/**
	 * Get the builder for the label property after setting it based on the existing property.
	 *
	 * @param property that should be the basis for the label property
	 * @return builder for the label property
	 */
	public PropertyDescriptorBuilder label( String property ) {
		labelBaseProperty = property;
		return label();
	}

	/**
	 * @return builder for the label property
	 */
	public PropertyDescriptorBuilder label() {
		return property( EntityPropertyRegistry.LABEL );
	}

	public synchronized PropertyDescriptorBuilder property( @NonNull String name ) {
		PropertyDescriptorBuilder builder = builders.get( name );

		if ( builder == null ) {
			builder = new PropertyDescriptorBuilder( this, name );
			builders.put( name, builder );

			final PropertyDescriptorBuilder builderRef = builder;

			if ( !EntityPropertyRegistry.LABEL.equals( name ) ) {
				orderedConsumers.add( registry -> this.applyBuilder( registry, name, builderRef ) );
			}
		}

		return builder;
	}

	/**
	 * Apply a factory function which adapts the current registry (much like {@link #and(Consumer)}) but returns a single
	 * {@link PropertyDescriptorBuilder} which can then be customized further. Added for fluent API extensions with more
	 * complex property registration.
	 * <p/>
	 * Comparison: {@code and(Consumer)} would be the equivalent of {@code property(Function).and()}.
	 *
	 * @param factory function to apply
	 * @return descriptor builder
	 */
	public synchronized PropertyDescriptorBuilder property( @NonNull Function<EntityPropertyRegistryBuilder, PropertyDescriptorBuilder> factory ) {
		return factory.apply( this );
	}

	/**
	 * Apply an additional consumer to the registry builder. If your consumer registers a single property you can consider
	 * using {@link #property(Function)} instead, which allows you to return a descriptor builder for the property which
	 * can then allow further customization.
	 *
	 * @return registry builder
	 */
	public EntityPropertyRegistryBuilder and( @NonNull Consumer<EntityPropertyRegistryBuilder> consumer ) {
		consumer.accept( this );
		return this;
	}

	/**
	 * Apply an intermediate {@link MutableEntityPropertyRegistry} processor. This consumer will receive
	 * an instance to the property registry currently being created, with only the previous property
	 * descriptor builders and registry consumers applied.
	 *
	 * @param consumer to process the registry
	 * @return registry builder
	 */
	public EntityPropertyRegistryBuilder processRegistry( @NonNull Consumer<MutableEntityPropertyRegistry> consumer ) {
		orderedConsumers.add( consumer );
		return this;
	}

	/**
	 * Apply the configured builder to the registry.
	 *
	 * @param propertyRegistry to modify
	 */
	public void apply( MutableEntityPropertyRegistry propertyRegistry ) {
		orderedConsumers.forEach( c -> c.accept( propertyRegistry ) );

		// Set the base property for the label
		if ( labelBaseProperty != null ) {
			MutableEntityPropertyDescriptor label = propertyRegistry.getProperty( EntityPropertyRegistry.LABEL );
			EntityPropertyDescriptor base = propertyRegistry.getProperty( labelBaseProperty );

			if ( base != null ) {
				LabelPropertiesRegistrar.copyPropertyToLabel( base, label );
			}
		}

		// Finally apply the label builder if there is one
		PropertyDescriptorBuilder labelBuilder = builders.get( EntityPropertyRegistry.LABEL );
		if ( labelBuilder != null ) {
			applyBuilder( propertyRegistry, EntityPropertyRegistry.LABEL, labelBuilder );
		}
	}

	private void applyBuilder( MutableEntityPropertyRegistry propertyRegistry,
	                           String propertyName,
	                           PropertyDescriptorBuilder builder ) {
		MutableEntityPropertyDescriptor existing = propertyRegistry.getProperty( propertyName );

		if ( builder.parent != null ) {
			MutableEntityPropertyDescriptor parentPropertyDescriptor = propertyRegistry.getProperty( builder.parent );
			if ( parentPropertyDescriptor == null ) {
				throw new IllegalArgumentException( "Cannot find parent property [" + builder.parent + "] on property: " + propertyName );
			}
			builder.parent( parentPropertyDescriptor );
		}

		if ( existing != null ) {
			builder.apply( existing );
		}
		else {
			MutableEntityPropertyDescriptor descriptor = builder.build();
			propertyRegistry.register( descriptor );
		}

		if ( builder.order != null ) {
			registerPropertyOrder( propertyRegistry, propertyName, builder.order );
		}
	}

	private void registerPropertyOrder( MutableEntityPropertyRegistry propertyRegistry,
	                                    String propertyName,
	                                    Integer order ) {
		if ( propertyRegistry instanceof EntityPropertyRegistrySupport ) {
			( (EntityPropertyRegistrySupport) propertyRegistry ).setPropertyOrder( propertyName, order );
		}
		else {
			LOG.warn(
					"Unable to register a property order as the propertyRegistry is not of type EntityPropertyRegistrySupport" );
		}
	}

	/**
	 * Extended class that adds order information and a method back to the parent.
	 */
	public static class PropertyDescriptorBuilder extends EntityPropertyDescriptorBuilder
	{
		private final EntityPropertyRegistryBuilder registryBuilder;

		private Integer order;
		private String parent;

		PropertyDescriptorBuilder( EntityPropertyRegistryBuilder registryBuilder, String propertyName ) {
			super( propertyName );
			this.registryBuilder = registryBuilder;
		}

		/**
		 * Set the order of the property within the registry.
		 *
		 * @param order of the property
		 * @return current builder
		 */
		public PropertyDescriptorBuilder order( int order ) {
			this.order = order;
			return this;
		}

		@Override
		public PropertyDescriptorBuilder original( EntityPropertyDescriptor original ) {
			return (PropertyDescriptorBuilder) super.original( original );
		}

		public PropertyDescriptorBuilder parent( String parent ) {
			this.parent = parent;
			return this;
		}

		@Override
		public PropertyDescriptorBuilder parent( EntityPropertyDescriptor parent ) {
			return (PropertyDescriptorBuilder) super.parent( parent );
		}

		@Override
		public PropertyDescriptorBuilder propertyType( Class<?> propertyType ) {
			return (PropertyDescriptorBuilder) super.propertyType( propertyType );
		}

		@Override
		public PropertyDescriptorBuilder propertyType( TypeDescriptor typeDescriptor ) {
			return (PropertyDescriptorBuilder) super.propertyType( typeDescriptor );
		}

		@Override
		public PropertyDescriptorBuilder attribute( String name, Object value ) {
			return (PropertyDescriptorBuilder) super.attribute( name, value );
		}

		@Override
		public <S> PropertyDescriptorBuilder attribute( Class<S> type, S value ) {
			return (PropertyDescriptorBuilder) super.attribute( type, value );
		}

		@Override
		public PropertyDescriptorBuilder attribute( AttributeRegistrar<EntityPropertyDescriptor> attributeRegistrar ) {
			return (PropertyDescriptorBuilder) super.attribute( attributeRegistrar );
		}

		@Override
		public PropertyDescriptorBuilder displayName( String displayName ) {
			return (PropertyDescriptorBuilder) super.displayName( displayName );
		}

		@Override
		public PropertyDescriptorBuilder spelValueFetcher( String expression ) {
			return (PropertyDescriptorBuilder) super.spelValueFetcher( expression );
		}

		@Override
		public <U> PropertyDescriptorBuilder valueFetcher( ValueFetcher<U> valueFetcher ) {
			return (PropertyDescriptorBuilder) super.valueFetcher( valueFetcher );
		}

		@Override
		public PropertyDescriptorBuilder writable( boolean writable ) {
			return (PropertyDescriptorBuilder) super.writable( writable );
		}

		@Override
		public PropertyDescriptorBuilder readable( boolean readable ) {
			return (PropertyDescriptorBuilder) super.readable( readable );
		}

		@Override
		public PropertyDescriptorBuilder hidden( boolean hidden ) {
			return (PropertyDescriptorBuilder) super.hidden( hidden );
		}

		@Override
		public <U, V> PropertyDescriptorBuilder controller( Consumer<ConfigurableEntityPropertyController<U, V>> consumer ) {
			return (PropertyDescriptorBuilder) super.controller( consumer );
		}

		@Override
		public PropertyDescriptorBuilder controller( EntityPropertyController controller ) {
			return (PropertyDescriptorBuilder) super.controller( controller );
		}

		@Override
		public PropertyDescriptorBuilder viewElementModeCaching( ViewElementMode mode, boolean cacheable ) {
			return (PropertyDescriptorBuilder) super.viewElementModeCaching( mode, cacheable );
		}

		@Override
		public PropertyDescriptorBuilder viewElementType( ViewElementMode mode, String viewElementType ) {
			return (PropertyDescriptorBuilder) super.viewElementType( mode, viewElementType );
		}

		@Override
		public PropertyDescriptorBuilder viewElementBuilder( ViewElementMode mode,
		                                                     ViewElementBuilder viewElementBuilder ) {
			return (PropertyDescriptorBuilder) super.viewElementBuilder( mode, viewElementBuilder );
		}

		@Override
		public <U extends ViewElement> PropertyDescriptorBuilder viewElementPostProcessor(
				ViewElementMode mode,
				ViewElementPostProcessor<U> viewElementPostProcessor ) {
			return (PropertyDescriptorBuilder) super.viewElementPostProcessor( mode, viewElementPostProcessor );
		}

		/**
		 * @return parent registry builder
		 */
		public EntityPropertyRegistryBuilder and() {
			return registryBuilder;
		}

		/**
		 * Apply an additional consumer to the registry builder, and return it.
		 *
		 * @return parent registry builder
		 */
		public EntityPropertyRegistryBuilder and( Consumer<EntityPropertyRegistryBuilder> consumer ) {
			return registryBuilder.and( consumer );
		}
	}
}
