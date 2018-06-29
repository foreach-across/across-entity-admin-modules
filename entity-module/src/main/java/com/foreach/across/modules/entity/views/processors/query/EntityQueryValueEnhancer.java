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

import com.foreach.across.modules.bootstrapui.elements.builder.OptionFormElementBuilder;

import java.util.function.Consumer;

/**
 * Base class to register an {@link com.foreach.across.modules.entity.EntityAttributes#OPTIONS_ENHANCER}.
 * Registers the value to be used in option form elements for a basic entity query filter
 * (instead of the default value that is used, which is usually the id).
 *
 * @author Steven Gentens
 * @since 2.2.0
 */
public abstract class EntityQueryValueEnhancer<T> implements Consumer<OptionFormElementBuilder>
{
	/**
	 * Fetches the value to be used on a multi-value control in an eql statement.
	 *
	 * @param label  of the object
	 * @param object the object
	 * @return the value that represents the object
	 */
	public abstract Object retrieveValue( String label, T object );

	@Override
	@SuppressWarnings("unchecked")
	public void accept( OptionFormElementBuilder optionFormElementBuilder ) {
		optionFormElementBuilder.attribute( "data-entity-query-pretty-value",
		                                    retrieveValue( optionFormElementBuilder.getLabel(), (T) optionFormElementBuilder.getRawValue() ) );
	}
}
