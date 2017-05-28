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

import com.foreach.across.modules.bootstrapui.elements.BootstrapUiElements;
import com.foreach.across.modules.bootstrapui.elements.BootstrapUiFactory;
import com.foreach.across.modules.bootstrapui.elements.CheckboxFormElement;
import com.foreach.across.modules.bootstrapui.elements.SelectFormElementConfiguration;
import com.foreach.across.modules.bootstrapui.elements.builder.OptionsFormElementBuilder;
import com.foreach.across.modules.entity.EntityAttributes;
import com.foreach.across.modules.entity.query.EntityQuery;
import com.foreach.across.modules.entity.query.EntityQueryExecutor;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.EntityRegistry;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.views.EntityViewElementBuilderFactorySupport;
import com.foreach.across.modules.entity.views.ViewElementLookupRegistry;
import com.foreach.across.modules.entity.views.ViewElementMode;
import com.foreach.across.modules.entity.views.bootstrapui.options.EntityQueryOptionIterableBuilder;
import com.foreach.across.modules.entity.views.bootstrapui.options.EnumOptionIterableBuilder;
import com.foreach.across.modules.entity.views.bootstrapui.options.OptionGenerator;
import com.foreach.across.modules.entity.views.bootstrapui.options.OptionIterableBuilder;
import com.foreach.across.modules.entity.views.bootstrapui.processors.builder.FormControlNameBuilderProcessor;
import com.foreach.across.modules.entity.views.bootstrapui.processors.builder.PersistenceAnnotationBuilderProcessor;
import com.foreach.across.modules.entity.views.bootstrapui.processors.builder.ValidationConstraintsBuilderProcessor;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ResolvableType;
import org.springframework.data.mapping.PersistentProperty;
import org.springframework.stereotype.Component;

import javax.persistence.Column;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;
import javax.validation.metadata.ConstraintDescriptor;
import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Map;

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
	private BootstrapUiFactory bootstrapUi;
	private EntityRegistry entityRegistry;

	public OptionsFormElementBuilderFactory() {
		addProcessor( new PersistenceAnnotationsBuilderProcessor() );
		addProcessor( new OptionsRequiredBuilderProcessor() );
		// ensure that checkboxes are prefixed
		addProcessor( new FormControlNameBuilderProcessor<>( CheckboxFormElement.class::isInstance ) );
	}

	@Override
	public boolean supports( String viewElementType ) {
		return StringUtils.equals( BootstrapUiElements.SELECT, viewElementType )
				|| StringUtils.equals( BootstrapUiElements.RADIO, viewElementType )
				|| StringUtils.equals( BootstrapUiElements.MULTI_CHECKBOX, viewElementType );
	}

	@Override
	@SuppressWarnings("unchecked")
	protected OptionsFormElementBuilder createInitialBuilder( EntityPropertyDescriptor descriptor,
	                                                          ViewElementMode viewElementMode, String viewElementType ) {

		boolean isCollection = isCollection( descriptor );
		Class<?> memberType = isCollection ? determineCollectionMemberType( descriptor ) : descriptor.getPropertyType();

		if ( memberType == null ) {
			throw new RuntimeException( "Unable to determine property type specific enough for form element assembly "
					                            + descriptor.getName() );
		}

		OptionsFormElementBuilder options
				= bootstrapUi.options()
				             .name( descriptor.getName() )
				             .controlName( EntityAttributes.controlName( descriptor ) );

		EntityConfiguration optionConfiguration = entityRegistry.getEntityConfiguration( memberType );
		OptionGenerator optionGenerator = determineOptionGenerator( descriptor, memberType, optionConfiguration );

		if ( optionConfiguration != null && !optionConfiguration.getEntityType().isEnum() ) {
			options.select( SelectFormElementConfiguration.liveSearch() );
		}
		else {
			options.select( SelectFormElementConfiguration.simple() );
		}

		options.add( optionGenerator );

		if ( isCollection ) {
			options.checkbox().multiple( true );
		}

		// todo, don't fetch again but pass in the type
		ViewElementLookupRegistry lookupRegistry = descriptor.getAttribute( ViewElementLookupRegistry.class );

		if ( lookupRegistry != null
				&& BootstrapUiElements.RADIO.equals( lookupRegistry.getViewElementType( viewElementMode ) ) ) {
			options.radio();
		}

		return options;
	}

	private OptionGenerator determineOptionGenerator( EntityPropertyDescriptor descriptor, Class<?> memberType, EntityConfiguration optionConfiguration ) {
		OptionGenerator optionGenerator = descriptor.getAttribute( OptionGenerator.class );

		if ( optionGenerator == null && optionConfiguration != null ) {
			optionGenerator = optionConfiguration.getAttribute( OptionGenerator.class );
		}

		if ( optionGenerator == null ) {
			optionGenerator = new OptionGenerator();
			optionGenerator.setSorted( true );
			optionGenerator.setValueFetcher( descriptor.getValueFetcher() );
			optionGenerator.setOptions( determineOptionBuilder( descriptor, memberType, optionConfiguration ) );
		}

		return optionGenerator;
	}

	@SuppressWarnings("unchecked")
	private OptionIterableBuilder determineOptionBuilder( EntityPropertyDescriptor descriptor, Class<?> memberType, EntityConfiguration optionConfiguration ) {
		OptionIterableBuilder builderToUse = descriptor.getAttribute( OptionIterableBuilder.class );

		if ( builderToUse == null && optionConfiguration != null ) {
			builderToUse = optionConfiguration.getAttribute( OptionIterableBuilder.class );
		}

		if ( builderToUse == null ) {
			if ( memberType.isEnum() ) {
				EnumOptionIterableBuilder iterableBuilder = new EnumOptionIterableBuilder();
				iterableBuilder.setEnumType( (Class<? extends Enum>) memberType );
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
								"Illegal " + EntityAttributes.OPTIONS_ALLOWED_VALUES + " attribute - expected EnumSet for enum propery" );
					}
				}
				builderToUse = iterableBuilder;
			}
			else if ( optionConfiguration != null && optionConfiguration.hasAttribute( EntityQueryExecutor.class ) ) {
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

				builderToUse = eqBuilder;
			}
		}

		return builderToUse;
	}

	private Object determineEntityQuery( EntityPropertyDescriptor descriptor, EntityConfiguration optionConfiguration ) {
		Object entityQuery = descriptor.getAttribute( EntityAttributes.OPTIONS_ENTITY_QUERY );

		if ( entityQuery == null && optionConfiguration != null ) {
			entityQuery = optionConfiguration.getAttribute( EntityAttributes.OPTIONS_ENTITY_QUERY );
		}

		return entityQuery;
	}

	private boolean isCollection( EntityPropertyDescriptor descriptor ) {
		return descriptor.getPropertyType().isArray()
				|| Collection.class.isAssignableFrom( descriptor.getPropertyType() );
	}

	private Class determineCollectionMemberType( EntityPropertyDescriptor descriptor ) {
		if ( descriptor.getPropertyType().isArray() ) {
			return descriptor.getPropertyType().getComponentType();
		}

		ResolvableType resolvableType = descriptor.getPropertyTypeDescriptor().getResolvableType();

		if ( resolvableType != null && resolvableType.hasGenerics() ) {
			return resolvableType.resolveGeneric( 0 );
		}

		return null;
	}

	@Autowired
	public void setBootstrapUi( BootstrapUiFactory bootstrapUi ) {
		this.bootstrapUi = bootstrapUi;
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
		                                 OptionsFormElementBuilder builder,
		                                 Annotation annotation,
		                                 Map<String, Object> annotationAttributes,
		                                 ConstraintDescriptor constraint ) {
			if ( isOfType( annotation, NotNull.class, NotEmpty.class ) ) {
				builder.required();
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
}
