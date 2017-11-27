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

import com.foreach.across.modules.bootstrapui.elements.BootstrapUiBuilders;
import com.foreach.across.modules.bootstrapui.elements.BootstrapUiElements;
import com.foreach.across.modules.bootstrapui.elements.CheckboxFormElement;
import com.foreach.across.modules.bootstrapui.elements.SelectFormElementConfiguration;
import com.foreach.across.modules.bootstrapui.elements.builder.OptionFormElementBuilder;
import com.foreach.across.modules.bootstrapui.elements.builder.OptionsFormElementBuilder;
import com.foreach.across.modules.entity.EntityAttributes;
import com.foreach.across.modules.entity.query.EntityQuery;
import com.foreach.across.modules.entity.query.EntityQueryExecutor;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.EntityRegistry;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.util.EntityTypeDescriptor;
import com.foreach.across.modules.entity.util.EntityUtils;
import com.foreach.across.modules.entity.views.EntityViewElementBuilderFactorySupport;
import com.foreach.across.modules.entity.views.EntityViewElementBuilderProcessor;
import com.foreach.across.modules.entity.views.ViewElementMode;
import com.foreach.across.modules.entity.views.bootstrapui.options.*;
import com.foreach.across.modules.entity.views.bootstrapui.processors.builder.FormControlNameBuilderProcessor;
import com.foreach.across.modules.entity.views.bootstrapui.processors.builder.PersistenceAnnotationBuilderProcessor;
import com.foreach.across.modules.entity.views.bootstrapui.processors.builder.ValidationConstraintsBuilderProcessor;
import com.foreach.across.modules.entity.views.bootstrapui.processors.element.LocalizedTextPostProcessor;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mapping.PersistentProperty;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;

import javax.persistence.Column;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;
import javax.validation.metadata.ConstraintDescriptor;
import javax.validation.metadata.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.util.EnumSet;
import java.util.Map;
import java.util.function.Consumer;

import static com.foreach.across.modules.bootstrapui.elements.BootstrapUiBuilders.option;
import static com.foreach.across.modules.entity.EntityAttributes.OPTIONS_ENHANCER;

/**
 * Builds a {@link OptionsFormElementBuilder} for a given {@link EntityPropertyDescriptor}.
 * If the entity descriptor defines an {@link OptionIterableBuilder} or {@link OptionGenerator} attribute, that one will be
 * used for generating the options.
 *
 * @author Arne Vandamme
 */
@Component
public class OptionsFormElementBuilderFactory extends EntityViewElementBuilderFactorySupport<OptionsFormElementBuilder>
{
	// collection of options - defaults to multi checkbox
	public static final String OPTIONS = "entityModuleOptions";

	private EntityRegistry entityRegistry;

	public OptionsFormElementBuilderFactory() {
		addProcessor( new PersistenceAnnotationsBuilderProcessor() );
		addProcessor( new OptionsRequiredBuilderProcessor() );
		// ensure that checkboxes are prefixed
		addProcessor( new FormControlNameBuilderProcessor<>( CheckboxFormElement.class::isInstance ) );
		addProcessor( new NullOptionBuilderProcessor() );
	}

	@Override
	public boolean supports( String viewElementType ) {
		return StringUtils.equals( OPTIONS, viewElementType )
				|| StringUtils.equals( BootstrapUiElements.SELECT, viewElementType )
				|| StringUtils.equals( BootstrapUiElements.RADIO, viewElementType )
				|| StringUtils.equals( BootstrapUiElements.MULTI_CHECKBOX, viewElementType );
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

		OptionsFormElementBuilder options
				= BootstrapUiBuilders.options()
				                     .name( descriptor.getName() )
				                     .controlName( EntityAttributes.controlName( descriptor ) );

		EntityConfiguration optionConfiguration = entityRegistry.getEntityConfiguration( typeDescriptor.getSimpleTargetType() );
		OptionGenerator optionGenerator = determineOptionGenerator( descriptor, typeDescriptor.getSimpleTargetType(), optionConfiguration, viewElementMode );

		if ( BootstrapUiElements.SELECT.equals( actualType ) ) {
			if ( ViewElementMode.FILTER_CONTROL.equals( viewElementMode.forSingle() ) ) {
				selectFormElementConfiguration = createFilterSelectFormElementConfiguration();
			}
			if ( selectFormElementConfiguration == null ) {
				selectFormElementConfiguration = createDefaultSelectFormElementConfiguration( optionConfiguration );
			}
			options.select( selectFormElementConfiguration );
		}
		else if ( BootstrapUiElements.MULTI_CHECKBOX.equals( actualType ) ) {
			options.checkbox();
		}
		else {
			options.radio();
		}

		boolean isFilterControl = ViewElementMode.FILTER_CONTROL.equals( viewElementMode.forSingle() );
		boolean nullValuePossible = !typeDescriptor.getSimpleTargetType().isPrimitive();

		if ( isFilterControl ) {
			optionGenerator.setEmptyOption(
					new OptionFormElementBuilder().text( "#{properties." + descriptor.getName() + "[filterNotSelected]=}" )
					                              .value( "" )
					                              .postProcessor( LocalizedTextPostProcessor.INSTANCE )
			);

			if ( nullValuePossible && optionGenerator instanceof FilterOptionGenerator ) {
				( (FilterOptionGenerator) optionGenerator ).setValueNotSetOption(
						new OptionFormElementBuilder().text( "#{properties." + descriptor.getName() + ".value[notSet]=Unknown}" )
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

		options.multiple( typeDescriptor.isCollection() || viewElementMode.isForMultiple() )
		       .add( optionGenerator );

		return options;
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
				return BootstrapUiElements.SELECT;
			}

			if ( isCollection ) {
				return BootstrapUiElements.MULTI_CHECKBOX;
			}

			return BootstrapUiElements.SELECT;
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

		if ( !optionGenerator.hasValueFetcher() ) {
			optionGenerator.setValueFetcher( descriptor.getValueFetcher() );
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
		return eqBuilder;
	}

	@SuppressWarnings("unchecked")
	private OptionIterableBuilder createBooleanOptionIterableBuilder( EntityPropertyDescriptor descriptor ) {
		return FixedOptionIterableBuilder.sorted(
				option().rawValue( Boolean.TRUE )
				        .text( "#{properties." + descriptor.getName() + ".value[true]=Yes}" )
				        .value( Boolean.TRUE )
				        .postProcessor( LocalizedTextPostProcessor.INSTANCE ),
				option().rawValue( Boolean.FALSE )
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

	public static class OptionsRequiredBuilderProcessor
			extends ValidationConstraintsBuilderProcessor<OptionsFormElementBuilder>
	{
		@Override
		protected void handleConstraint( EntityPropertyDescriptor propertyDescriptor,
		                                 ViewElementMode viewElementMode,
		                                 String viewElementType,
		                                 OptionsFormElementBuilder options,
		                                 Annotation annotation,
		                                 Map<String, Object> annotationAttributes,
		                                 ConstraintDescriptor constraint ) {
			if ( isOfType( annotation, NotNull.class, NotEmpty.class ) ) {
				options.required();
			}
		}
	}

	public static class PersistenceAnnotationsBuilderProcessor
			extends PersistenceAnnotationBuilderProcessor<OptionsFormElementBuilder>
	{
		@Override
		protected void handleAnnotation( OptionsFormElementBuilder builder,
		                                 Annotation annotation,
		                                 Map<String, Object> annotationAttributes,
		                                 PersistentProperty property ) {
			if ( isOfType( annotation, ManyToOne.class, OneToOne.class ) ) {
				Boolean optional = (Boolean) annotationAttributes.get( "optional" );

				if ( optional != null && !optional ) {
					builder.required();
				}
			}
			else if ( isOfType( annotation, Column.class ) ) {
				Boolean nullable = (Boolean) annotationAttributes.get( "nullable" );

				if ( nullable != null && !nullable ) {
					builder.required();
				}
			}
		}
	}

	/**
	 * Adds a {@code null} option in the case of a {@link ViewElementMode#FILTER_CONTROL} if there is no {@link NotNull} or {@link NotEmpty} present on the property.
	 */
	private class NullOptionBuilderProcessor implements EntityViewElementBuilderProcessor<OptionsFormElementBuilder>
	{

		@Override
		public void process( EntityPropertyDescriptor propertyDescriptor,
		                     ViewElementMode viewElementMode,
		                     String viewElementType,
		                     OptionsFormElementBuilder options ) {
			if ( ViewElementMode.FILTER_CONTROL.equals( viewElementMode.forSingle() ) ) {

//				if ( options.isRequired() ) {
//					options.postProcessor( ( ctx, container ) -> container.removeAllFromTree( "null-option" ) );
//					options.required( false );
//				}

//
//				PropertyDescriptor validationDescriptor = propertyDescriptor.getAttribute( PropertyDescriptor.class );
//				boolean addEmptyOption = true;
//				if ( validationDescriptor != null && validationDescriptor.hasConstraints() ) {
//					addEmptyOption = validationDescriptor.getConstraintDescriptors().stream()
//					                                     .noneMatch( constraintDescriptor -> ArrayUtils
//							                                     .contains( new Object[] { NotNull.class, NotEmpty.class },
//							                                                constraintDescriptor.getAnnotation().annotationType() ) );
//				}
//				if ( addEmptyOption && !propertyDescriptor.getPropertyType().isPrimitive() ) {
//					options.add( option().value( "'NULL'" )
//					                     .rawValue( null )
//					                     .text( "NULL"/*builderContext
//						                                       .resolveText( "#{properties." + property.getName() + ".entityQueryFilter.nullOption}" ) */ ) );
//				}

			}
		}
	}
}
