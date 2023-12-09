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

package com.foreach.across.modules.entity.views.processors.query;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;
import java.util.function.Function;

/**
 * Convert the raw value of an {@link com.foreach.across.modules.bootstrapui.elements.builder.OptionFormElementBuilder}
 * to a {@code String} and returns it as a valid EQL string: surrounded with single quotes and pre-existing single quotes escaped.
 * <p/>
 * Optionally provide a {@code Function} to apply to the raw value first. In that case the result of the function will be converted
 * to the EQL string.
 *
 * @author Arne Vandamme
 * @since 3.1.0
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class EQLStringValueOptionEnhancer<T> extends EntityQueryValueEnhancer<T>
{
	private final Function<T, Object> function;

	@Override
	public Object retrieveValue( String label, T object ) {
		String value = Objects.toString( function.apply( object ), "" );
		return "'" + StringUtils.replace( value, "'", "\\'" ) + "'";
	}

	/**
	 * Create a typed instance that will convert the initial raw value.
	 *
	 * @param <U> type of the raw value
	 * @return instance
	 */
	public static <U> EntityQueryValueEnhancer<U> create() {
		return new EQLStringValueOptionEnhancer<>( x -> x );
	}

	/**
	 * Create a types instance that will first apply a function to the initial raw value,
	 * and then convert the resulting value.
	 *
	 * @param valueFetcher function to apply to the initial raw value
	 * @param <U>          type of the raw value
	 * @return instance
	 */
	public static <U> EntityQueryValueEnhancer<U> create( @NonNull Function<U, Object> valueFetcher ) {
		return new EQLStringValueOptionEnhancer<U>( valueFetcher );
	}
}
