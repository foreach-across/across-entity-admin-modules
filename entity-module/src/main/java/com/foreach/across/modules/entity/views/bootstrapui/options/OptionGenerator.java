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

import com.foreach.across.modules.bootstrapui.elements.CheckboxFormElement;
import com.foreach.across.modules.bootstrapui.elements.SelectFormElement;
import com.foreach.across.modules.bootstrapui.elements.builder.OptionFormElementBuilder;
import com.foreach.across.modules.bootstrapui.elements.builder.OptionsFormElementBuilder;
import com.foreach.across.modules.entity.views.support.ValueFetcher;
import com.foreach.across.modules.entity.views.util.EntityViewElementUtils;
import com.foreach.across.modules.web.ui.MutableViewElement;
import com.foreach.across.modules.web.ui.ViewElementBuilder;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.elements.ContainerViewElement;
import lombok.*;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.function.Consumer;

/**
 * <p>Wrapper that generates the children of an {@link OptionsFormElementBuilder}.
 * Requires an {@link OptionIterableBuilder} that is responsible for creating
 * the initial collection of {@link OptionFormElementBuilder}s.</p>
 * <p>When the {@link #setSorted(Boolean)}</p> property is true, the options will always be sorted by label and text
 * in the resulting set. If unset, sorting will only happen if {@link OptionIterableBuilder#isSorted()} returns {@code false}.
 * <p>In case of single option selector, an empty option will be added if none is selected
 * or if the element is not required.  The empty option can be overridden (and set to null)
 * using {@link #setEmptyOption(OptionFormElementBuilder)}.</p>
 * <p>A single option will be automatically selected in case a selection is required.</p>
 *
 * @author Arne Vandamme
 * @see OptionsFormElementBuilder
 * @see OptionIterableBuilder
 * @see com.foreach.across.modules.entity.views.bootstrapui.OptionsFormElementBuilderFactory
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class OptionGenerator implements ViewElementBuilder<ContainerViewElement>
{
	@Getter(AccessLevel.PROTECTED)
	private OptionFormElementBuilder emptyOption = new OptionFormElementBuilder().label( "" ).value( "" );

	@Getter(AccessLevel.PROTECTED)
	private OptionIterableBuilder options;

	@Getter(AccessLevel.PROTECTED)
	private Boolean sorted;

	@Getter(AccessLevel.PROTECTED)
	private ValueFetcher<Object> valueFetcher;

	@Getter(AccessLevel.PROTECTED)
	private boolean selfOptionIncluded;

	@Getter
	@Setter
	private Consumer<OptionFormElementBuilder> enhancer;

	/**
	 * @param options iterable builder generating the list of options
	 */
	public void setOptions( OptionIterableBuilder options ) {
		this.options = options;
	}

	/**
	 * @param sorted true if the options should always be sorted by name - false if options should never be re-sorted
	 */
	public void setSorted( Boolean sorted ) {
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

	/**
	 * @return true if an {@link OptionIterableBuilder} has been set
	 */
	public boolean hasOptions() {
		return options != null;
	}

	/**
	 * @return true if a {@link ValueFetcher} has been set
	 */
	public boolean hasValueFetcher() {
		return valueFetcher != null;
	}

	/**
	 * @return true if an OptionsEnhancer has been set
	 */
	public boolean hasEnhancer() {
		return enhancer != null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public ContainerViewElement build( ViewElementBuilderContext builderContext ) {
		ContainerViewElement container = new ContainerViewElement();
		Object entity = EntityViewElementUtils.currentEntity( builderContext );
		Collection selectedValues = valueFetcher != null ? retrieveSelected( entity ) : null;

		OptionsFormElementBuilder optionsBuilder = builderContext.getAttribute( OptionsFormElementBuilder.class );
		Assert.notNull( optionsBuilder, "no optionsBuilder was found" );

		boolean hasSelected = false;
		List<OptionFormElementBuilder> actual = new ArrayList<>();

		if ( options != null ) {
			int optionCount = 0;
			OptionFormElementBuilder firstOption = null;
			for ( OptionFormElementBuilder option : options.buildOptions( builderContext ) ) {
				if ( firstOption == null ) {
					firstOption = option;
				}
				selectOption( option, selectedValues );
				if ( isAllowed( option, entity ) ) {
					actual.add( option );
				}
				hasSelected |= option.isSelected();
				optionCount++;
			}

			// auto-select if only single option and required
			if ( optionCount == 1 && optionsBuilder.isRequired() ) {
				firstOption.selected();
				hasSelected = true;
			}

			if ( shouldSort( options ) ) {
				Collections.sort( actual );
			}
		}

		if ( hasEnhancer() ) {
			actual.forEach( enhancer );
		}

		createInitialFixedOptions( builderContext, container, optionsBuilder, selectedValues, hasSelected );

		for ( OptionFormElementBuilder option : actual ) {
			container.addChild( option.build( builderContext ) );
		}

		return container;
	}

	protected void createInitialFixedOptions( ViewElementBuilderContext builderContext,
	                                          ContainerViewElement container,
	                                          OptionsFormElementBuilder optionsBuilder,
	                                          Collection selectedValues,
	                                          boolean hasSelected ) {
		boolean shouldAddEmptyOption = emptyOption != null
				&& !optionsBuilder.isMultiple()
				&& ( !hasSelected || !optionsBuilder.isRequired() );

		if ( shouldAddEmptyOption ) {
			MutableViewElement generatedOption = emptyOption.build( builderContext );
			if ( !hasSelected ) {
				select( generatedOption, true );
			}
			container.addChild( generatedOption );
		}
	}

	protected void select( MutableViewElement option, boolean selected ) {
		if ( option instanceof CheckboxFormElement ) {
			( (CheckboxFormElement) option ).setChecked( selected );
		}
		else if ( option instanceof SelectFormElement.Option ) {
			( (SelectFormElement.Option) option ).setSelected( selected );
		}
	}

	private boolean shouldSort( OptionIterableBuilder optionsBuilder ) {
		return Boolean.TRUE.equals( sorted ) || ( !Boolean.FALSE.equals( sorted ) && !optionsBuilder.isSorted() );
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

	@SuppressWarnings( "unused" )
	public static class OptionGeneratorBuilder
	{
		private OptionFormElementBuilder emptyOption = new OptionFormElementBuilder().label( "" ).value( "" );
	}
}
