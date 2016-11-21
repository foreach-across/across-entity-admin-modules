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

/**
 * Handler API for defining and executing an function in an {@link EntityQuery}.
 *
 * @author Arne Vandamme
 * @see EQFunction
 * @since 2.0.0
 */
public interface EntityQueryFunctionHandler
{
	/**
	 * Does this handler accept a function with the given name and support the desired result type.
	 *
	 * @param functionName name of the function call
	 * @param desiredType  of the result
	 * @return true if handler can execute the requested function
	 */
	boolean accepts( String functionName, TypeDescriptor desiredType );

	/**
	 * Apply the function and return the value in the {@param desiredType}.
	 *
	 * @param functionName      name of the function being executed.
	 * @param arguments         for the function
	 * @param desiredType       for the output
	 * @param argumentConverter to be used for argument conversion
	 * @return evaluation result
	 */
	Object apply( String functionName,
	              Object[] arguments,
	              TypeDescriptor desiredType,
	              EQTypeConverter argumentConverter );
}
