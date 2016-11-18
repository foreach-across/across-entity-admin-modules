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

package com.foreach.across.modules.entity.query;

import org.springframework.core.convert.TypeDescriptor;

import java.util.Collection;
import java.util.function.BiFunction;

/**
 * Handler API for defining and executing an function in an {@link EntityQuery}.
 *
 * @author Arne Vandamme
 * @see EQFunction
 * @since 2.0.0
 */
public interface EntityQueryFunctionHandler extends BiFunction<String, Collection<Object>, Object>
{
	/**
	 * Specify the type this function will return.
	 *
	 * @return type descriptor for to match
	 */
	TypeDescriptor returnType( String functionName );

	/**
	 * Apply the function and return the value of type specified by {@link #returnType(String)}.
	 *
	 * @param functionName name of the function being executed.
	 * @param arguments    for the function
	 * @return evaluation result
	 */
	@Override
	Object apply( String functionName, Collection<Object> arguments );
}
