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

import com.foreach.across.core.support.ReadableAttributes;
import com.foreach.across.core.support.WritableAttributes;
import com.foreach.across.modules.entity.config.AttributeRegistrar;
import lombok.NonNull;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Support class for builders building types that implement {@link com.foreach.across.core.support.WritableAttributes}.
 * Allows setting of attribute values.
 *
 * @author Arne Vandamme
 * @since 2.0.0
 */
public abstract class AbstractWritableAttributesBuilder<T extends ReadableAttributes>
{
	private final Map<String, Object> attributes = new HashMap<>();
	private final Collection<AttributeRegistrar<T>> registrars = new ArrayDeque<>();

	/**
	 * Add a custom attribute this builder should add to the entity.
	 *
	 * @param name  Name of the attribute.
	 * @param value Value of the attribute.
	 * @return current builder
	 */
	public AbstractWritableAttributesBuilder attribute( @NonNull String name, Object value ) {
		attributes.put( name, value );
		return this;
	}

	/**
	 * Add a custom attribute this builder should add to the entity.
	 *
	 * @param type  Type of the attribute.
	 * @param value Value of the attribute.
	 * @param <S>   Class that is both key and value type of the attribute
	 * @return current builder
	 */
	public <S> AbstractWritableAttributesBuilder attribute( @NonNull Class<S> type, S value ) {
		attributes.put( type.getName(), value );
		return this;
	}

	/**
	 * Add an attribute registrar for one or more attributes that should be added.
	 * Provides a mechanism to get the owner (eg. {@code EntityConfiguration}...) to be made accessible to the attribute values.
	 *
	 * @param attributeRegistrar registrar
	 * @return current builder
	 */
	public AbstractWritableAttributesBuilder attribute( @NonNull AttributeRegistrar<T> attributeRegistrar ) {
		registrars.add( attributeRegistrar );
		return this;
	}

	protected void applyAttributes( T owner, WritableAttributes attributes ) {
		attributes.setAttributes( this.attributes );
		registrars.forEach( r -> r.accept( owner, attributes ) );
	}
}
