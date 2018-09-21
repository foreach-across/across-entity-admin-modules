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
import com.foreach.across.modules.bootstrapui.elements.DateTimeFormElementConfiguration;
import com.foreach.across.modules.bootstrapui.elements.DateTimeFormElementConfiguration.Format;
import com.foreach.across.modules.bootstrapui.elements.builder.DateTimeFormElementBuilder;
import com.foreach.across.modules.entity.EntityAttributes;
import com.foreach.across.modules.entity.conditionals.ConditionalOnBootstrapUI;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.views.EntityViewElementBuilderFactoryHelper;
import com.foreach.across.modules.entity.views.EntityViewElementBuilderFactorySupport;
import com.foreach.across.modules.entity.views.EntityViewElementBuilderService;
import com.foreach.across.modules.entity.views.ViewElementMode;
import com.foreach.across.modules.entity.views.bootstrapui.processors.builder.FormControlNameBuilderProcessor;
import com.foreach.across.modules.entity.views.bootstrapui.processors.builder.PersistenceAnnotationBuilderProcessor;
import com.foreach.across.modules.entity.views.bootstrapui.processors.builder.ValidationConstraintsBuilderProcessor;
import com.foreach.across.modules.entity.views.bootstrapui.processors.element.ConversionServiceValueTextPostProcessor;
import com.foreach.across.modules.entity.views.bootstrapui.processors.element.DateTimeValueTextPostProcessor;
import com.foreach.across.modules.entity.views.bootstrapui.processors.element.PropertyPlaceholderTextPostProcessor;
import com.foreach.across.modules.entity.views.support.ValueFetcher;
import com.foreach.across.modules.entity.views.util.EntityViewElementUtils;
import com.foreach.across.modules.web.ui.ViewElementBuilder;
import com.foreach.across.modules.web.ui.ViewElementPostProcessor;
import com.foreach.across.modules.web.ui.elements.builder.TextViewElementBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mapping.PersistentProperty;
import org.springframework.stereotype.Component;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Future;
import javax.validation.constraints.Past;
import javax.validation.metadata.ConstraintDescriptor;
import java.lang.annotation.Annotation;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.Map;

/**
 * @author Arne Vandamme
 */
@ConditionalOnBootstrapUI
@Component
public class DateTimeFormElementBuilderFactory extends EntityViewElementBuilderFactorySupport<ViewElementBuilder>
{
	private final ControlBuilderFactory controlBuilderFactory = new ControlBuilderFactory();
	private final ValueBuilderFactory valueBuilderFactory = new ValueBuilderFactory();

	private EntityViewElementBuilderService viewElementBuilderService;
	private EntityViewElementBuilderFactoryHelper builderFactoryHelpers;

	@Override
	public boolean supports( String viewElementType ) {
		return BootstrapUiElements.DATETIME.equals( viewElementType );
	}

	@Override
	protected ViewElementBuilder createInitialBuilder( EntityPropertyDescriptor propertyDescriptor,
	                                                   ViewElementMode viewElementMode,
	                                                   String viewElementType ) {
		if ( ViewElementMode.isControl( viewElementMode ) && propertyDescriptor.isWritable() ) {
			return controlBuilderFactory.createBuilder( propertyDescriptor, viewElementMode, viewElementType );
		}

		return valueBuilderFactory.createBuilder( propertyDescriptor, viewElementMode, viewElementType );
	}

	@Autowired
	public void setViewElementBuilderService( EntityViewElementBuilderService viewElementBuilderService ) {
		this.viewElementBuilderService = viewElementBuilderService;
	}

	@Autowired
	public void setBuilderFactoryHelpers( EntityViewElementBuilderFactoryHelper builderFactoryHelpers ) {
		this.builderFactoryHelpers = builderFactoryHelpers;
	}

	/**
	 * Responsible for creating the value element that also supports the {@link DateTimeFormElementConfiguration}
	 * that was specified on the control.
	 */
	private class ValueBuilderFactory extends EntityViewElementBuilderFactorySupport<TextViewElementBuilder>
	{
		@Override
		public boolean supports( String viewElementType ) {
			return true;
		}

		@Override
		protected TextViewElementBuilder createInitialBuilder( EntityPropertyDescriptor propertyDescriptor,
		                                                       ViewElementMode viewElementMode,
		                                                       String viewElementType ) {
			ViewElementPostProcessor valueTextPostProcessor
					= builderFactoryHelpers.createDefaultValueTextPostProcessor( propertyDescriptor );

			if ( valueTextPostProcessor instanceof ConversionServiceValueTextPostProcessor ) {
				DateTimeFormElementConfiguration config = null;

				if ( propertyDescriptor.isWritable() ) {
					ViewElementBuilder control = viewElementBuilderService.getElementBuilder(
							propertyDescriptor, ViewElementMode.CONTROL
					);

					if ( control instanceof DateTimeFormElementBuilder ) {
						config = ( (DateTimeFormElementBuilder) control ).getConfiguration();
					}
				}

				if ( config == null ) {
					DateTimeFormElementBuilder created = controlBuilderFactory.createBuilder(
							propertyDescriptor, ViewElementMode.CONTROL, viewElementType
					);

					config = created.getConfiguration();
				}

				if ( config != null ) {
					valueTextPostProcessor = new DateTimeValueTextPostProcessor<>( propertyDescriptor, config );
				}
			}

			return BootstrapUiBuilders.text().postProcessor( valueTextPostProcessor );
		}
	}

	/**
	 * Responsible for creating the actual control.
	 */
	private class ControlBuilderFactory extends EntityViewElementBuilderFactorySupport<DateTimeFormElementBuilder>
	{
		public ControlBuilderFactory() {
			addProcessor( new TemporalAnnotationProcessor() );
			addProcessor( new PastAndFutureValidationProcessor() );
			addProcessor( new FormControlNameBuilderProcessor<>() );
		}

		@Override
		public boolean supports( String viewElementType ) {
			return true;
		}

		@Override
		public DateTimeFormElementBuilder createBuilder( EntityPropertyDescriptor propertyDescriptor,
		                                                 ViewElementMode viewElementMode, String viewElementType ) {
			DateTimeFormElementBuilder builder
					= super.createBuilder( propertyDescriptor, viewElementMode, viewElementType );

			// Apply custom configuration
			DateTimeFormElementConfiguration configuration = propertyDescriptor.getAttribute(
					DateTimeFormElementConfiguration.class );

			if ( configuration != null ) {
				builder.format( configuration.getFormat() ).configuration( configuration );
			}
			else {
				configuration = builder.getConfiguration();
				configuration.setShowClearButton( !Boolean.TRUE.equals( builder.getRequired() ) );

				if ( propertyDescriptor.hasAttribute( Format.class ) ) {
					builder.format( propertyDescriptor.getAttribute( Format.class ) );
				}
				else {
					Class<?> propertyType = propertyDescriptor.getPropertyType();
					if ( LocalDate.class.isAssignableFrom( propertyType ) ) {
						builder.format( Format.DATE );
					}
					else if ( LocalTime.class.isAssignableFrom( propertyType ) ) {
						builder.format( Format.TIME );
					}
				}
			}

			return builder;
		}

		@Override
		protected DateTimeFormElementBuilder createInitialBuilder( EntityPropertyDescriptor propertyDescriptor,
		                                                           ViewElementMode viewElementMode,
		                                                           String viewElementType ) {

			return BootstrapUiBuilders
					.datetime()
					.name( propertyDescriptor.getName() )
					.required( EntityAttributes.isRequired( propertyDescriptor ) )
					.postProcessor( EntityViewElementUtils.controlNamePostProcessor( propertyDescriptor ) )
					.postProcessor( new PropertyPlaceholderTextPostProcessor<>() )
					.postProcessor(
							( builderContext, datetime ) ->
							{
								Object entity = EntityViewElementUtils.currentEntity( builderContext );
								ValueFetcher valueFetcher = propertyDescriptor.getValueFetcher();

								if ( entity != null && valueFetcher != null ) {
									Object value = valueFetcher.getValue( entity );
									if ( value instanceof LocalDate ) {
										LocalDate propertyValue = (LocalDate) value;
										datetime.setLocalDate( propertyValue );
									}
									else if ( value instanceof LocalTime ) {
										LocalTime propertyValue = (LocalTime) value;
										datetime.setLocalTime( propertyValue );
									}
									else if ( value instanceof LocalDateTime ) {
										LocalDateTime propertyValue = (LocalDateTime) value;
										datetime.setLocalDateTime( propertyValue );
									}
									else {
										Date propertyValue = (Date) value;

										if ( propertyValue != null ) {
											datetime.setValue( propertyValue );
										}
									}

								}
							}
					);
		}
	}

	/**
	 * Change the basic format of the datetime picker if a {@link Temporal} annotation is present.
	 */
	public static class TemporalAnnotationProcessor extends PersistenceAnnotationBuilderProcessor<DateTimeFormElementBuilder>
	{
		@Override
		protected void handleAnnotation( DateTimeFormElementBuilder builder,
		                                 Annotation annotation,
		                                 Map<String, Object> annotationAttributes,
		                                 PersistentProperty property ) {
			if ( isOfType( annotation, Temporal.class ) ) {
				TemporalType temporalType = (TemporalType) annotationAttributes.get( "value" );

				if ( temporalType == TemporalType.DATE ) {
					builder.date();
				}
				else if ( temporalType == TemporalType.TIME ) {
					builder.time();
				}
			}
		}
	}

	/**
	 * Apply {@link Past} and {@link Future} validation constraints that determine min and max date of the datetime picker.
	 */
	public static class PastAndFutureValidationProcessor extends ValidationConstraintsBuilderProcessor<DateTimeFormElementBuilder>
	{
		@Override
		protected void handleConstraint( EntityPropertyDescriptor propertyDescriptor,
		                                 ViewElementMode viewElementMode, String viewElementType, DateTimeFormElementBuilder builder,
		                                 Annotation annotation,
		                                 Map<String, Object> annotationAttributes,
		                                 ConstraintDescriptor constraint ) {
			DateTimeFormElementConfiguration configuration = builder.getConfiguration();

			if ( configuration != null ) {
				if ( isOfType( annotation, Past.class ) ) {
					configuration.setMaxDate( new Date() );
				}
				else if ( isOfType( annotation, Future.class ) ) {
					configuration.setMinDate( new Date() );
				}
			}
		}
	}
}
