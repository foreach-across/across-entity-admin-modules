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
package com.foreach.across.modules.entity.newviews.bootstrapui.options;

import com.foreach.across.modules.bootstrapui.elements.builder.OptionsFormElementBuilder.Option;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;

import java.util.Arrays;

/**
 * Simple implementation that always returns the same (constant) collection of {@link Option}s.
 *
 * @author Arne Vandamme
 */
public class FixedOptionIterableBuilder implements OptionIterableBuilder
{
	private final Iterable<Option> options;

	public FixedOptionIterableBuilder( Option... options ) {
		this( Arrays.asList( options ) );
	}

	public FixedOptionIterableBuilder( Iterable<Option> options ) {
		this.options = options;
	}

	@Override
	public Iterable<Option> buildOptions( ViewElementBuilderContext builderContext ) {
		return options;
	}
}
