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

import com.foreach.across.modules.entity.registry.builders.EntityPropertyRegistryLabelPropertyBuilder;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistry;
import com.foreach.across.modules.entity.registry.properties.MutableEntityPropertyDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Arne Vandamme
 */
public abstract class AbstractEntityPropertyRegistryBuilder<SELF extends AbstractEntityPropertyRegistryBuilder>
{
	private final Logger LOG = LoggerFactory.getLogger( getClass() );

	private final Map<String, AbstractEntityPropertyDescriptorBuilder> builders = new HashMap<>();
	private String labelBaseProperty;

	/**
	 * Get the builder for the label property after setting it based on the existing property.
	 *
	 * @param property that should be the basis for the label property
	 * @return builder for the label property
	 */
	public AbstractEntityPropertyDescriptorBuilder label( String property ) {
		labelBaseProperty = property;
		return label();
	}

	/**
	 * @return builder for the label property
	 */
	public AbstractEntityPropertyDescriptorBuilder label() {
		return property( EntityPropertyRegistry.LABEL );
	}

	@SuppressWarnings("unchecked")
	public synchronized AbstractEntityPropertyDescriptorBuilder property( String name ) {
		Assert.notNull( name );

		AbstractEntityPropertyDescriptorBuilder builder = builders.get( name );

		if ( builder == null ) {
			builder = createDescriptorBuilder( name );
			builder.setName( name );
			builders.put( name, builder );
		}

		return builder;
	}

	protected abstract AbstractEntityPropertyDescriptorBuilder createDescriptorBuilder( String name );

	/**
	 * @return parent builder
	 */
	public abstract Object and();

	protected void apply( EntityPropertyRegistry entityPropertyRegistry ) {
		AbstractEntityPropertyDescriptorBuilder labelBuilder = null;

		for ( Map.Entry<String, AbstractEntityPropertyDescriptorBuilder> builder : builders.entrySet() ) {
			if ( EntityPropertyRegistry.LABEL.equals( builder.getKey() ) ) {
				labelBuilder = builder.getValue();
			}
			else {
				builder.getValue().apply( entityPropertyRegistry );
			}
		}

		// Set the base property for the label
		if ( labelBaseProperty != null ) {
			EntityPropertyDescriptor label = entityPropertyRegistry.getProperty( EntityPropertyRegistry.LABEL );
			EntityPropertyDescriptor base = entityPropertyRegistry.getProperty( labelBaseProperty );

			if ( base != null ) {
				if ( label instanceof MutableEntityPropertyDescriptor ) {

					MutableEntityPropertyDescriptor mutableLabel = (MutableEntityPropertyDescriptor) label;
					EntityPropertyRegistryLabelPropertyBuilder.copyPropertyToLabel( base, mutableLabel );
				}
				else {
					LOG.warn( "Unable to modify the {} property: not a MutableEntityPropertyDescriptor",
					          EntityPropertyRegistry.LABEL );
				}
			}
		}

		// Finally apply the label builder if there is one
		if ( labelBuilder != null ) {
			labelBuilder.apply( entityPropertyRegistry );
		}
	}
}
