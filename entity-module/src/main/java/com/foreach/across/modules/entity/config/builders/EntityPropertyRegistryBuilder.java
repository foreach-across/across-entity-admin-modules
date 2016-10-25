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

import com.foreach.across.modules.entity.registry.builders.LabelPropertiesRegistrar;
import com.foreach.across.modules.entity.registry.properties.*;
import com.foreach.across.modules.entity.views.ViewElementMode;
import com.foreach.across.modules.entity.views.support.ValueFetcher;
import com.foreach.across.modules.web.ui.ViewElementBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * Builder to customize a {@link com.foreach.across.modules.entity.registry.properties.MutableEntityPropertyRegistry}.
 *
 * @author Arne Vandamme
 * @since 2.0.0
 */
public class EntityPropertyRegistryBuilder
{
	private final static Logger LOG = LoggerFactory.getLogger( EntityPropertyRegistryBuilder.class );

	private final Map<String, PropertyDescriptorBuilder> builders = new HashMap<>();
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

	public synchronized PropertyDescriptorBuilder property( String name ) {
		Assert.notNull( name );

		PropertyDescriptorBuilder builder = builders.get( name );

		if ( builder == null ) {
			builder = new PropertyDescriptorBuilder( this, name );
			builders.put( name, builder );
		}

		return builder;
	}

	/**
	 * Apply the configured builder to the registry.
	 *
	 * @param propertyRegistry to modify
	 */
	public void apply( MutableEntityPropertyRegistry propertyRegistry ) {
		builders.entrySet().forEach( builder -> {
			if ( !EntityPropertyRegistry.LABEL.equals( builder.getKey() ) ) {
				applyBuilder( propertyRegistry, builder.getKey(), builder.getValue() );
			}
		} );

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
		Comparator<EntityPropertyDescriptor> defaultOrder = propertyRegistry.getDefaultOrder();

		if ( defaultOrder == null ) {
			defaultOrder = EntityPropertyComparators.ordered();
			propertyRegistry.setDefaultOrder( defaultOrder );
		}

		if ( defaultOrder instanceof EntityPropertyComparators.Ordered ) {
			EntityPropertyComparators.Ordered propertyOrder
					= ( (EntityPropertyComparators.Ordered) defaultOrder );
			propertyOrder.put( propertyName, order );
		}
		else {
			LOG.warn( "Unable to register a property order as the used comparator is of type {} instead of {}",
			          ClassUtils.getUserClass( defaultOrder ), EntityPropertyComparators.Ordered.class );
		}
	}

	/**
	 * Extended class that adds order information and a method back to the parent.
	 */
	public static class PropertyDescriptorBuilder extends EntityPropertyDescriptorBuilder
	{
		private final EntityPropertyRegistryBuilder parent;

		private Integer order;

		PropertyDescriptorBuilder( EntityPropertyRegistryBuilder parent, String propertyName ) {
			super( propertyName );
			this.parent = parent;
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
		public PropertyDescriptorBuilder displayName( String displayName ) {
			return (PropertyDescriptorBuilder) super.displayName( displayName );
		}

		@Override
		public PropertyDescriptorBuilder spelValueFetcher( String expression ) {
			return (PropertyDescriptorBuilder) super.spelValueFetcher( expression );
		}

		@Override
		public PropertyDescriptorBuilder valueFetcher( ValueFetcher valueFetcher ) {
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

		/**
		 * @return parent registry builder
		 */
		public EntityPropertyRegistryBuilder and() {
			return parent;
		}
	}
}
