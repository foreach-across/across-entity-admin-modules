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

package com.foreach.across.modules.entity.views.bootstrapui.options;

import com.foreach.across.modules.bootstrapui.elements.SelectFormElement;
import com.foreach.across.modules.bootstrapui.elements.builder.OptionFormElementBuilder;
import com.foreach.across.modules.bootstrapui.elements.builder.OptionsFormElementBuilder;
import com.foreach.across.modules.entity.views.support.ValueFetcher;
import com.foreach.across.modules.web.ui.MutableViewElement;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.elements.ContainerViewElement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collection;
import java.util.function.Consumer;

/**
 * Extension to the regular option generated that allows for a special "unfiltered" option.
 * The meaning of the empty option in this generator is redefined versus the regular generator.
 * In a regular generator the empty option usually means the same as "no-value-set", or a property
 * having for example a {@code null} value. This generator has an explicit option to add no-value selected option.
 * <p/>
 * The empty option only means that the filter has nothing selected.
 *
 * @author Arne Vandamme
 * @since 2.2.0
 */
@NoArgsConstructor
@AllArgsConstructor
public class FilterOptionGenerator extends OptionGenerator
{
	/**
	 * Special option to indicate no value has been set on the property.
	 */
	@Setter
	private OptionFormElementBuilder valueNotSetOption;

	@Builder(toBuilder = true)
	private FilterOptionGenerator( @Builder.ObtainVia(method = "getEmptyOption") OptionFormElementBuilder emptyOption,
	                               @Builder.ObtainVia(method = "getOptions") OptionIterableBuilder options,
	                               @Builder.ObtainVia(method = "getSorted") Boolean sorted,
	                               @Builder.ObtainVia(method = "getValueFetcher") ValueFetcher<Object> valueFetcher,
	                               @Builder.ObtainVia(method = "isSelfOptionIncluded") boolean selfOptionIncluded,
	                               @Builder.ObtainVia(method = "getEnhancer") Consumer<OptionFormElementBuilder> enhancer ) {
		super( emptyOption, options, sorted, valueFetcher, selfOptionIncluded, enhancer );
	}

	@Override
	protected void createInitialFixedOptions( ViewElementBuilderContext builderContext,
	                                          ContainerViewElement container,
	                                          OptionsFormElementBuilder optionsBuilder,
	                                          Collection selectedValues,
	                                          boolean hasSelected ) {
		super.createInitialFixedOptions( builderContext, container, optionsBuilder, selectedValues, hasSelected );

		if ( valueNotSetOption != null ) {
			MutableViewElement option = valueNotSetOption.build( builderContext );
			select( option, selectedValues.contains( null ) );

			if ( option instanceof SelectFormElement.Option ) {
				SelectFormElement.OptionGroup group = new SelectFormElement.OptionGroup();
				group.addChild( option );
				container.addChild( group );
			}
			else {
				container.addChild( option );
			}
		}
	}

	public static class FilterOptionGeneratorBuilder extends OptionGeneratorBuilder
	{
		private OptionFormElementBuilder emptyOption = new OptionFormElementBuilder().label( "" ).value( "" );
	}
}
