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

package com.foreach.across.modules.entity.views.bootstrapui;

import com.foreach.across.modules.bootstrapui.elements.SelectFormElementConfiguration;
import com.foreach.across.modules.bootstrapui.elements.builder.OptionFormElementBuilder;
import com.foreach.across.modules.bootstrapui.elements.builder.OptionsFormElementBuilder;
import com.foreach.across.modules.entity.EntityAttributes;
import com.foreach.across.modules.entity.conditionals.ConditionalOnBootstrapUI;
import com.foreach.across.modules.entity.query.EQGroup;
import com.foreach.across.modules.entity.query.EntityQuery;
import com.foreach.across.modules.entity.query.EntityQueryExecutor;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.EntityRegistry;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.util.EntityTypeDescriptor;
import com.foreach.across.modules.entity.util.EntityUtils;
import com.foreach.across.modules.entity.views.EntityViewElementBuilderFactorySupport;
import com.foreach.across.modules.entity.views.ViewElementMode;
import com.foreach.across.modules.entity.views.bootstrapui.options.*;
import com.foreach.across.modules.entity.views.bootstrapui.processors.element.LocalizedTextPostProcessor;
import com.foreach.across.modules.entity.views.processors.EntityQueryFilterProcessor;
import com.foreach.across.modules.entity.views.util.EntityViewElementUtils;
import com.foreach.across.modules.web.ui.ViewElementBuilderSupport;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;

import java.util.EnumSet;
import java.util.function.Consumer;

import static com.foreach.across.modules.bootstrapui.elements.BootstrapUiElements.*;
import static com.foreach.across.modules.bootstrapui.ui.factories.BootstrapViewElements.bootstrap;
import static com.foreach.across.modules.entity.EntityAttributes.OPTIONS_ENHANCER;
import static com.foreach.across.modules.entity.views.processors.query.EntityQueryFilterControlUtils.*;
import static com.foreach.across.modules.web.ui.ViewElementBuilderSupport.ElementOrBuilder.wrap;

/**
 * Builds a {@link OptionsFormElementBuilder} for a given {@link EntityPropertyDescriptor}.
 * If the entity descriptor defines an {@link OptionIterableBuilder} or {@link OptionGenerator} attribute, that one will be
 * used for generating the options.
 *
 * @author Arne Vandamme
 */
@ConditionalOnBootstrapUI
@Component
public class OptionsFormElementBuilderFactory extends EntityViewElementBuilderFactorySupport<OptionsFormElementBuilder>
{
	// collection of options - defaults to multi checkbox
	public static final String OPTIONS = "entityModuleOptions";

	private EntityRegistry entityRegistry;
	private ConversionService conversionService;

	@Override
	public boolean supports( String viewElementType ) {
		return StringUtils.equals( OPTIONS, viewElementType )
				|| StringUtils.equals( SELECT, viewElementType )
				|| StringUtils.equals( RADIO, viewElementType )
				|| StringUtils.equals( MULTI_TOGGLE, viewElementType )
				|| StringUtils.equals( MULTI_CHECKBOX, viewElementType );
	}

	@Override
	@SuppressWarnings("unchecked")
	protected OptionsFormElementBuilder createInitialBuilder( EntityPropertyDescriptor descriptor,
	                                                          ViewElementMode viewElementMode,
	                                                          String viewElementType ) {
		EntityTypeDescriptor typeDescriptor = EntityUtils.resolveEntityTypeDescriptor( descriptor.getPropertyTypeDescriptor(), entityRegistry );

		if ( !typeDescriptor.isTargetTypeResolved() ) {
			throw new RuntimeException( "Unable to determine property type specific enough for form element assembly " + descriptor.getName() );
		}

		SelectFormElementConfiguration selectFormElementConfiguration = descriptor.getAttribute( SelectFormElementConfiguration.class );
		String actualType = determineActualType( viewElementType, selectFormElementConfiguration, typeDescriptor.isCollection() );

		OptionsFormElementBuilder options =
				bootstrap.builders
						.options()
						.name( descriptor.getName() )
						.controlName( descriptor.getName() )
						.postProcessor( EntityViewElementUtils.controlNamePostProcessor( descriptor ) )
						.postProcessor(
								( ( builderContext, element ) -> {
									if ( ViewElementMode.FILTER_CONTROL.equals( viewElementMode.forSingle() ) ) {
										ViewElementBuilderSupport.ElementOrBuilder wrappedElement = wrap( element );
										configureControlSettings( wrappedElement, descriptor );
										if ( viewElementMode.isForMultiple() ) {
											setAttribute( wrappedElement, FilterControlAttributes.TYPE, EQGroup.class.getSimpleName() );
										}
										element.addCssClass( EntityQueryFilterProcessor.ENTITY_QUERY_CONTROL_MARKER );
									}
								} )
						);

		EntityConfiguration optionConfiguration = entityRegistry.getEntityConfiguration( typeDescriptor.getSimpleTargetType() );
		OptionGenerator optionGenerator = determineOptionGenerator( descriptor, typeDescriptor.getSimpleTargetType(), optionConfiguration, viewElementMode );

		if ( SELECT.equals( actualType ) ) {
			if ( ViewElementMode.FILTER_CONTROL.equals( viewElementMode.forSingle() ) ) {
				selectFormElementConfiguration = createFilterSelectFormElementConfiguration();
			}
			if ( selectFormElementConfiguration == null ) {
				selectFormElementConfiguration = createDefaultSelectFormElementConfiguration( optionConfiguration );
			}
			options.select( selectFormElementConfiguration );
		}
		else if ( MULTI_CHECKBOX.equals( actualType ) ) {
			options.checkbox();
		}
		else if ( MULTI_TOGGLE.equals( actualType ) ) {
			options.toggle();
		}
		else {
			options.radio();
		}

		if ( EntityAttributes.isRequired( descriptor ) ) {
			options.required();
		}

		boolean isFilterControl = ViewElementMode.FILTER_CONTROL.equals( viewElementMode.forSingle() );
		boolean nullValuePossible = !typeDescriptor.getSimpleTargetType().isPrimitive()
				&& !( isRadioOrToggleElement( options ) && EntityAttributes.isRequired( descriptor ) );

		if ( isFilterControl ) {
			optionGenerator.setEmptyOption(
					new OptionFormElementBuilder().text( "#{properties." + descriptor.getName() + "[filterNotSelected]=}" )
					                              .value( "" )
					                              .postProcessor( LocalizedTextPostProcessor.INSTANCE )
			);

			if ( nullValuePossible && optionGenerator instanceof FilterOptionGenerator ) {
				( (FilterOptionGenerator) optionGenerator ).setValueNotSetOption(
						new OptionFormElementBuilder().text( "#{properties." + descriptor.getName() + ".value[notSet]=No value set}" )
						                              .value( "NULL" )
						                              .postProcessor( LocalizedTextPostProcessor.INSTANCE )
				);
			}
		}
		else {
			if ( nullValuePossible ) {
				optionGenerator.setEmptyOption(
						new OptionFormElementBuilder().text( "#{properties." + descriptor.getName() + ".value[empty]=}" )
						                              .value( "" )
						                              .postProcessor( LocalizedTextPostProcessor.INSTANCE )
				);
			}
			else {
				optionGenerator.setEmptyOption( null );
			}
		}

		options.multiple( ( !isFilterControl && typeDescriptor.isCollection() ) || viewElementMode.isForMultiple() )
		       .add( optionGenerator );

		return options;
	}

	private boolean isRadioOrToggleElement( OptionsFormElementBuilder options ) {
		return OptionsFormElementBuilder.Type.RADIO.equals( options.getType() )
				|| OptionsFormElementBuilder.Type.TOGGLE.equals( options.getType() );
	}

	private SelectFormElementConfiguration createFilterSelectFormElementConfiguration() {
		return SelectFormElementConfiguration.liveSearch().setSelectedTextFormat( "count > 1" );
	}

	private SelectFormElementConfiguration createDefaultSelectFormElementConfiguration( EntityConfiguration optionConfiguration ) {
		if ( optionConfiguration != null && !optionConfiguration.getEntityType().isEnum() ) {
			return SelectFormElementConfiguration.liveSearch();
		}

		return SelectFormElementConfiguration.simple();
	}

	private String determineActualType( String requestedType, SelectFormElementConfiguration selectFormElementConfiguration, boolean isCollection ) {
		if ( OPTIONS.equals( requestedType ) || requestedType == null ) {
			if ( selectFormElementConfiguration != null ) {
				return SELECT;
			}

			if ( isCollection ) {
				return MULTI_CHECKBOX;
			}

			return SELECT;
		}

		return requestedType;
	}

	private OptionGenerator determineOptionGenerator( EntityPropertyDescriptor descriptor,
	                                                  Class<?> memberType,
	                                                  EntityConfiguration optionConfiguration,
	                                                  ViewElementMode viewElementMode ) {
		OptionGenerator optionGenerator = descriptor.getAttribute( OptionGenerator.class );

		if ( optionGenerator == null && optionConfiguration != null ) {
			optionGenerator = optionConfiguration.getAttribute( OptionGenerator.class );
		}

		if ( optionGenerator == null ) {
			optionGenerator = ViewElementMode.FILTER_CONTROL.equals( viewElementMode.forSingle() ) ? new FilterOptionGenerator() : new OptionGenerator();
		}
		else {
			optionGenerator = optionGenerator.toBuilder().build();
		}

		Consumer<OptionFormElementBuilder> consumer = buildOptionEnhancer( optionConfiguration, descriptor, optionGenerator );

		if ( consumer != null ) {
			optionGenerator.setEnhancer( consumer );
		}

		if ( !optionGenerator.hasOptions() ) {
			optionGenerator.setOptions( determineOptionBuilder( descriptor, memberType, optionConfiguration ) );
		}

		return optionGenerator;
	}

	@SuppressWarnings("unchecked")
	private Consumer<OptionFormElementBuilder> buildOptionEnhancer( EntityConfiguration configuration,
	                                                                EntityPropertyDescriptor descriptor,
	                                                                OptionGenerator optionGenerator ) {
		Consumer<OptionFormElementBuilder> consumer = configuration != null
				? getOptionEnhancer( null, (Consumer<OptionFormElementBuilder>) configuration.getAttribute( OPTIONS_ENHANCER ) )
				: null;
		consumer = getOptionEnhancer( consumer, (Consumer<OptionFormElementBuilder>) descriptor.getAttribute( OPTIONS_ENHANCER ) );
		return getOptionEnhancer( consumer, optionGenerator.getEnhancer() );
	}

	private Consumer<OptionFormElementBuilder> getOptionEnhancer( Consumer<OptionFormElementBuilder> current, Consumer<OptionFormElementBuilder> next ) {
		if ( current == null ) {
			return next;
		}
		else if ( next != null ) {
			return current.andThen( next );
		}
		return current;
	}

	@SuppressWarnings("unchecked")
	private OptionIterableBuilder determineOptionBuilder( EntityPropertyDescriptor descriptor, Class<?> memberType, EntityConfiguration optionConfiguration ) {
		OptionIterableBuilder builderToUse = descriptor.getAttribute( OptionIterableBuilder.class );

		if ( builderToUse == null && optionConfiguration != null ) {
			builderToUse = optionConfiguration.getAttribute( OptionIterableBuilder.class );
		}

		if ( builderToUse == null && ClassUtils.isAssignable( boolean.class, descriptor.getPropertyType() ) ) {
			return createBooleanOptionIterableBuilder( descriptor );
		}

		if ( builderToUse == null ) {
			if ( memberType.isEnum() ) {
				builderToUse = createEnumOptionIterableBuilder( descriptor, (Class<? extends Enum>) memberType, optionConfiguration );
			}
			else if ( optionConfiguration != null && optionConfiguration.hasAttribute( EntityQueryExecutor.class ) ) {
				builderToUse = createEntityQueryOptionIterableBuilder( descriptor, optionConfiguration );
			}
		}

		return builderToUse;
	}

	private EntityQueryOptionIterableBuilder createEntityQueryOptionIterableBuilder( EntityPropertyDescriptor descriptor,
	                                                                                 EntityConfiguration optionConfiguration ) {
		EntityQueryOptionIterableBuilder eqBuilder = EntityQueryOptionIterableBuilder.forEntityConfiguration( optionConfiguration );
		Object entityQueryToUse = determineEntityQuery( descriptor, optionConfiguration );

		if ( entityQueryToUse instanceof EntityQuery ) {
			eqBuilder.setEntityQuery( (EntityQuery) entityQueryToUse );
		}
		else if ( entityQueryToUse instanceof String ) {
			eqBuilder.setEntityQuery( (String) entityQueryToUse );
		}
		else if ( entityQueryToUse != null ) {
			throw new IllegalStateException(
					"Illegal " + EntityAttributes.OPTIONS_ENTITY_QUERY + " attribute - expected to be String or EntityQuery" );
		}

		if ( conversionService != null ) {
			eqBuilder.setConversionService( conversionService );
		}

		return eqBuilder;
	}

	@SuppressWarnings("unchecked")
	private OptionIterableBuilder createBooleanOptionIterableBuilder( EntityPropertyDescriptor descriptor ) {
		return FixedOptionIterableBuilder.sorted(
				bootstrap.builders
						.option().rawValue( Boolean.TRUE )
						.text( "#{properties." + descriptor.getName() + ".value[true]=Yes}" )
						.value( Boolean.TRUE )
						.postProcessor( LocalizedTextPostProcessor.INSTANCE ),
				bootstrap.builders
						.option().rawValue( Boolean.FALSE )
						.text( "#{properties." + descriptor.getName() + ".value[false]=No}" )
						.value( Boolean.FALSE )
						.postProcessor( LocalizedTextPostProcessor.INSTANCE )
		);
	}

	private EnumOptionIterableBuilder createEnumOptionIterableBuilder( EntityPropertyDescriptor descriptor,
	                                                                   Class<? extends Enum> memberType,
	                                                                   EntityConfiguration optionConfiguration ) {
		EnumOptionIterableBuilder iterableBuilder = new EnumOptionIterableBuilder();
		iterableBuilder.setEnumType( memberType );
		if ( optionConfiguration != null && optionConfiguration.hasEntityModel() ) {
			iterableBuilder.setEntityModel( optionConfiguration.getEntityModel() );
		}
		Object allowedValues = descriptor.getAttribute( EntityAttributes.OPTIONS_ALLOWED_VALUES );
		if ( allowedValues != null ) {
			if ( allowedValues instanceof EnumSet ) {
				iterableBuilder.setAllowedValues( (EnumSet) allowedValues );
			}
			else {
				throw new IllegalStateException(
						"Illegal " + EntityAttributes.OPTIONS_ALLOWED_VALUES + " attribute - expected EnumSet for enum property" );
			}
		}
		return iterableBuilder;
	}

	private Object determineEntityQuery( EntityPropertyDescriptor descriptor, EntityConfiguration optionConfiguration ) {
		Object entityQuery = descriptor.getAttribute( EntityAttributes.OPTIONS_ENTITY_QUERY );

		if ( entityQuery == null && optionConfiguration != null ) {
			entityQuery = optionConfiguration.getAttribute( EntityAttributes.OPTIONS_ENTITY_QUERY );
		}

		return entityQuery;
	}

	@Autowired
	public void setEntityRegistry( EntityRegistry entityRegistry ) {
		this.entityRegistry = entityRegistry;
	}

	@Autowired
	@Qualifier("mvcConversionService")
	public void setConversionService( ConversionService conversionService ) {
		this.conversionService = conversionService;
	}
}
