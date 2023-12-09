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

import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.elements.ConfigurableTextViewElement;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Simple {@link AbstractValueTextPostProcessor} for collections or arrays.
 * That takes another {@link AbstractValueTextPostProcessor} for a single item of the collection.
 *
 * @author Arne Vandamme
 * @since 3.2.0
 */
@Getter
@Setter
@Accessors(chain = true)
public final class CollectionValueTextPostProcessor<T extends ConfigurableTextViewElement> extends AbstractValueTextPostProcessor<T>
{
	@NonNull
	private String separator = ", ";

	@NonNull
	private AbstractValueTextPostProcessor<?> itemTextProcessor;

	public CollectionValueTextPostProcessor( EntityPropertyDescriptor propertyDescriptor, AbstractValueTextPostProcessor<?> itemTextProcessor ) {
		super( propertyDescriptor );
		this.itemTextProcessor = itemTextProcessor;
	}

	@Override
	protected String print( Object value, Locale locale, ViewElementBuilderContext builderContext ) {
		if ( value != null ) {
			return fetchStream( value )
					.map( item -> itemTextProcessor.print( item, locale, builderContext ) )
					.collect( Collectors.joining( separator ) );
		}

		return "";
	}

	@Override
	protected String print( Object value, Locale locale ) {
		return print( value, locale, ViewElementBuilderContext.retrieveGlobalBuilderContext().orElse( null ) );
	}

	private Stream<?> fetchStream( Object value ) {
		if ( value.getClass().isArray() ) {
			return Arrays.stream( (Object[]) value );
		}
		if ( value instanceof Collection ) {
			return ( (Collection) value ).stream();
		}
		return Stream.empty();
	}
}
