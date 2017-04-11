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

import com.foreach.across.modules.bootstrapui.elements.builder.OptionFormElementBuilder;
import com.foreach.across.modules.bootstrapui.elements.builder.OptionsFormElementBuilder;
import com.foreach.across.modules.entity.views.support.ValueFetcher;
import com.foreach.across.modules.entity.views.util.EntityViewElementUtils;
import com.foreach.across.modules.web.ui.ViewElementBuilder;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.elements.ContainerViewElement;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * <p>Wrapper that generates the children of an {@link OptionsFormElementBuilder}.
 * Requires an {@link OptionIterableBuilder} that is responsible for creating
 * the initial collection of {@link OptionFormElementBuilder}s.</p>
 * <p>When the {@link #setSorted(boolean)}</p> property is true, the options will be sorted by label and text
 * in the resulting set.</p>
 * <p>In case of a resulting {@link com.foreach.across.modules.bootstrapui.elements.SelectFormElement} an
 * empty option will be added if none is selected or if the element is not required.  The empty
 * option can be overridden (and set to null) using {@link #setEmptyOption(OptionFormElementBuilder)}.</p>
 *
 * @author Arne Vandamme
 * @see OptionsFormElementBuilder
 * @see OptionIterableBuilder
 */
public class OptionGenerator implements ViewElementBuilder<ContainerViewElement>
{
	private OptionIterableBuilder options;
	private boolean sorted = false;
	private OptionFormElementBuilder emptyOption;
	private ValueFetcher<Object> valueFetcher;
	private boolean selfOptionIncluded = false;

	public OptionGenerator() {
		emptyOption = new OptionFormElementBuilder().label( "" ).value( "" );
	}

	/**
	 * @param options iterable builder generating the list of options
	 */
	public void setOptions( OptionIterableBuilder options ) {
		this.options = options;
	}

	/**
	 * @param sorted true if the options should be sorted by name
	 */
	public void setSorted( boolean sorted ) {
		this.sorted = sorted;
	}

	/**
	 * @param emptyOption to include if none is selected or a value is not required
	 */
	public void setEmptyOption( OptionFormElementBuilder emptyOption ) {
		this.emptyOption = emptyOption;
	}

	/**
	 * @param valueFetcher to be used to auto-select values
	 */
	@SuppressWarnings("unchecked")
	public void setValueFetcher( ValueFetcher valueFetcher ) {
		this.valueFetcher = valueFetcher;
	}

	/**
	 * In case of options that are the same type as the entity being built, should the entity
	 * itself be provided as an option.  Default value is {@code false}.
	 *
	 * @param selfOptionIncluded True when entity itself should be included.
	 */
	public void setSelfOptionIncluded( boolean selfOptionIncluded ) {
		this.selfOptionIncluded = selfOptionIncluded;
	}

	@Override
	public ContainerViewElement build( ViewElementBuilderContext builderContext ) {
		ContainerViewElement container = new ContainerViewElement();
		Object entity = EntityViewElementUtils.currentEntity( builderContext );
		Collection selectedValues = valueFetcher != null ? retrieveSelected( entity ) : null;

		OptionsFormElementBuilder optionsBuilder = builderContext.getAttribute( OptionsFormElementBuilder.class );
		Assert.notNull( optionsBuilder );

		boolean hasSelected = false;
		List<OptionFormElementBuilder> actual = new ArrayList<>();

		if ( options != null ) {
			for ( OptionFormElementBuilder option : options.buildOptions( builderContext ) ) {
				selectOption( option, selectedValues );
				if ( isAllowed( option, entity ) ) {
					actual.add( option );
				}
				hasSelected |= option.isSelected();
			}

			if ( sorted ) {
				Collections.sort( actual );
			}
		}

		boolean shouldAddEmptyOption = emptyOption != null
				&& optionsBuilder.getType() == OptionsFormElementBuilder.Type.SELECT
				&& ( !hasSelected || !optionsBuilder.isRequired() );

		if ( shouldAddEmptyOption ) {
			container.addChild( emptyOption.build( builderContext ) );
		}

		for ( OptionFormElementBuilder option : actual ) {
			container.addChild( option.build( builderContext ) );
		}

		return container;
	}

	private boolean isAllowed( OptionFormElementBuilder option, Object self ) {
		return selfOptionIncluded || self == null || !Objects.equals( self, option.getRawValue() );
	}

	private void selectOption( OptionFormElementBuilder option, Collection selectedValues ) {
		if ( selectedValues != null ) {
			option.selected( selectedValues.contains( option.getRawValue() ) );
		}
	}

	private Collection retrieveSelected( Object entity ) {
		if ( entity != null && valueFetcher != null ) {
			Object selected = valueFetcher.getValue( entity );

			if ( selected != null ) {
				if ( selected instanceof Collection ) {
					return (Collection) selected;
				}
				else if ( selected.getClass().isArray() ) {
					return CollectionUtils.arrayToList( selected );
				}
				else {
					return Collections.singleton( selected );
				}
			}
		}

		return Collections.emptyList();
	}
}
