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
import com.foreach.across.modules.bootstrapui.elements.TextareaFormElement;
import com.foreach.across.modules.bootstrapui.elements.TextboxFormElement;
import com.foreach.across.modules.bootstrapui.elements.builder.TextboxFormElementBuilder;
import com.foreach.across.modules.entity.EntityAttributes;
import com.foreach.across.modules.entity.conditionals.ConditionalOnBootstrapUI;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.views.EntityViewElementBuilderFactoryHelper;
import com.foreach.across.modules.entity.views.EntityViewElementBuilderFactorySupport;
import com.foreach.across.modules.entity.views.EntityViewElementBuilderProcessor;
import com.foreach.across.modules.entity.views.ViewElementMode;
import com.foreach.across.modules.entity.views.bootstrapui.processors.builder.ValidationConstraintsBuilderProcessor;
import com.foreach.across.modules.entity.views.bootstrapui.processors.element.PropertyPlaceholderTextPostProcessor;
import com.foreach.across.modules.entity.views.processors.EntityQueryFilterProcessor;
import com.foreach.across.modules.entity.views.processors.query.EntityQueryFilterControlUtils;
import com.foreach.across.modules.entity.views.util.EntityViewElementUtils;
import com.foreach.across.modules.web.ui.ViewElementBuilderSupport.ElementOrBuilder;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.constraints.Size;
import javax.validation.metadata.ConstraintDescriptor;
import java.lang.annotation.Annotation;
import java.util.Map;

/**
 * Builds a {@link TextboxFormElement} for a given {@link EntityPropertyDescriptor}.
 *
 * @author Arne Vandamme
 */
@ConditionalOnBootstrapUI
@Component
public class TextboxFormElementBuilderFactory extends EntityViewElementBuilderFactorySupport<TextboxFormElementBuilder>
{
	private EntityViewElementBuilderFactoryHelper builderFactoryHelpers;

	private int maximumSingleLineLength = 300;

	public TextboxFormElementBuilderFactory() {
		addProcessor( new TextboxConstraintsProcessor() );
		addProcessor( new EmailTypeDetectionProcessor() );
		addProcessor( new TextboxTypeDetectionProcessor() );
		addProcessor( new PasswordTypeDetectionProcessor() );
		addProcessor( new FilterControlProcessor() );
	}

	public void setMaximumSingleLineLength( int maximumSingleLineLength ) {
		this.maximumSingleLineLength = maximumSingleLineLength;
	}

	@Override
	public boolean supports( String viewElementType ) {
		return BootstrapUiElements.TEXTAREA.equals( viewElementType ) || BootstrapUiElements.TEXTBOX.equals( viewElementType );
	}

	@Override
	protected TextboxFormElementBuilder createInitialBuilder( EntityPropertyDescriptor propertyDescriptor,
	                                                          ViewElementMode viewElementMode,
	                                                          String viewElementType ) {
		return BootstrapUiBuilders
				.textbox()
				.name( propertyDescriptor.getName() )
				.controlName( propertyDescriptor.getName() )
				.required( EntityAttributes.isRequired( propertyDescriptor ) )
				.multiLine(
						String.class.equals( propertyDescriptor.getPropertyType() ) || BootstrapUiElements.TEXTAREA.equals( viewElementType )
				)
				.postProcessor( EntityViewElementUtils.controlNamePostProcessor( propertyDescriptor ) )
				.postProcessor( builderFactoryHelpers.createDefaultValueTextPostProcessor( propertyDescriptor ) )
				.postProcessor( new PropertyPlaceholderTextPostProcessor<>() )
				.postProcessor(
						( ( builderContext, element ) -> {
							if ( ViewElementMode.FILTER_CONTROL.equals( viewElementMode.forSingle() ) ) {
								element.addCssClass( EntityQueryFilterProcessor.ENTITY_QUERY_CONTROL_MARKER );
								EntityQueryFilterControlUtils.configureControlSettings( ElementOrBuilder.wrap( element ), propertyDescriptor );
							}
						} )
				);
	}

	@Autowired
	public void setBuilderFactoryHelpers( EntityViewElementBuilderFactoryHelper builderFactoryHelpers ) {
		this.builderFactoryHelpers = builderFactoryHelpers;
	}

	/**
	 * Configures the fixed type for a textbox.
	 */
	private static class TextboxTypeDetectionProcessor implements EntityViewElementBuilderProcessor<TextboxFormElementBuilder>
	{
		@Override
		public void process( EntityPropertyDescriptor propertyDescriptor,
		                     ViewElementMode viewElementMode,
		                     String viewElementType,
		                     TextboxFormElementBuilder builder ) {
			if ( propertyDescriptor.hasAttribute( TextboxFormElement.Type.class ) ) {
				TextboxFormElement.Type type = propertyDescriptor.getAttribute( TextboxFormElement.Type.class );
				builder.type( type ).multiLine( TextareaFormElement.Type.TEXTAREA.equals( type ) );
			}
		}
	}

	/**
	 * Detects email types based on the {@link Email} annotation presence.  Only if there was no specific type
	 * set as attribute.
	 */
	public static class EmailTypeDetectionProcessor extends ValidationConstraintsBuilderProcessor<TextboxFormElementBuilder>
	{
		@Override
		public void process( EntityPropertyDescriptor propertyDescriptor,
		                     ViewElementMode viewElementMode,
		                     String viewElementType,
		                     TextboxFormElementBuilder builder ) {
			if ( !propertyDescriptor.hasAttribute( TextboxFormElement.Type.class ) ) {
				super.process( propertyDescriptor, viewElementMode, viewElementType, builder );
			}
		}

		@Override
		protected void handleConstraint( EntityPropertyDescriptor propertyDescriptor,
		                                 ViewElementMode viewElementMode,
		                                 String viewElementType,
		                                 TextboxFormElementBuilder builder,
		                                 Annotation annotation,
		                                 Map<String, Object> annotationAttributes,
		                                 ConstraintDescriptor constraint ) {
			if ( isOfType( annotation, Email.class ) ) {
				builder.type( TextboxFormElement.Type.EMAIL );
			}
		}
	}

	/**
	 * Unreadable properties rendered as password type will have their text cleared.
	 */
	public static class PasswordTypeDetectionProcessor implements EntityViewElementBuilderProcessor<TextboxFormElementBuilder>
	{
		@Override
		public void process( EntityPropertyDescriptor propertyDescriptor,
		                     ViewElementMode viewElementMode,
		                     String viewElementType, TextboxFormElementBuilder builder ) {
			if ( TextboxFormElement.Type.PASSWORD.equals(
					propertyDescriptor.getAttribute( TextboxFormElement.Type.class ) )
					&& !propertyDescriptor.isReadable() ) {
				builder.postProcessor( ( builderContext, element ) -> element.setText( null ) );
			}
		}
	}

	/**
	 * Responsible for calculating max length and multi/single line type based on validation constraints.
	 */
	private class TextboxConstraintsProcessor extends ValidationConstraintsBuilderProcessor<TextboxFormElementBuilder>
	{
		@Override
		protected void handleConstraint( EntityPropertyDescriptor propertyDescriptor,
		                                 ViewElementMode viewElementMode,
		                                 String viewElementType,
		                                 TextboxFormElementBuilder textbox,
		                                 Annotation annotation,
		                                 Map<String, Object> attributes,
		                                 ConstraintDescriptor constraint ) {
			if ( isOfType( annotation, Size.class, Length.class ) ) {
				Integer max = (Integer) attributes.get( "max" );

				if ( max != Integer.MAX_VALUE ) {
					textbox.maxLength( max );
					if ( max <= maximumSingleLineLength ) {
						textbox.rows( 1 );
					}
					if ( !BootstrapUiElements.TEXTAREA.equals( viewElementType ) && !propertyDescriptor.hasAttribute( TextboxFormElement.Type.class ) ) {
						textbox.multiLine( max > maximumSingleLineLength );
					}
				}
			}
		}
	}

	/**
	 * In case of a {@link ViewElementMode#FILTER_CONTROL}, the {@link TextboxFormElementBuilder} must always be rendered as a single line {@link TextboxFormElement}.
	 */
	private static class FilterControlProcessor implements EntityViewElementBuilderProcessor<TextboxFormElementBuilder>
	{
		@Override
		public void process( EntityPropertyDescriptor propertyDescriptor,
		                     ViewElementMode viewElementMode,
		                     String viewElementType,
		                     TextboxFormElementBuilder textbox ) {
			if ( ViewElementMode.FILTER_CONTROL.equals( viewElementMode.forSingle() ) ) {
				textbox.multiLine( false );
			}
		}
	}

}
