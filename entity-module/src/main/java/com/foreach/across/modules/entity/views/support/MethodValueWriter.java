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

package com.foreach.across.modules.entity.views.support;

import lombok.NonNull;
import lombok.SneakyThrows;

import java.lang.reflect.Method;
import java.util.function.BiFunction;

/**
 * @author Arne Vandamme
 * @since 3.1.0
 */
public class MethodValueWriter<T, U> implements BiFunction<T, U, Boolean>
{
	private final Method method;

	public MethodValueWriter( @NonNull Method method ) {
		method.setAccessible( true );
		this.method = method;
	}

	@SneakyThrows
	@Override
	public Boolean apply( T entity, U value ) {
		if ( entity == null ) {
			return false;
		}
		method.invoke( entity, value );
		return true;
	}

}
