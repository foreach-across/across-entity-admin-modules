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
package com.foreach.across.modules.entity.newviews.bootstrapui;

import com.foreach.across.modules.bootstrapui.elements.BootstrapUiElements;
import com.foreach.across.modules.bootstrapui.elements.TextboxFormElement;
import com.foreach.across.modules.bootstrapui.elements.builder.FormGroupElementBuilder;
import com.foreach.across.modules.bootstrapui.elements.builder.TextboxFormElementBuilder;
import com.foreach.across.modules.entity.newviews.ValidationConstraintsBuilderProcessor;
import com.foreach.across.modules.entity.newviews.ViewElementMode;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistry;
import com.foreach.across.modules.entity.views.EntityView;
import com.foreach.across.modules.entity.views.support.ValueFetcher;
import com.foreach.across.modules.web.ui.ViewElementBuilder;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.ViewElementPostProcessor;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;

import javax.validation.constraints.Size;
import javax.validation.metadata.ConstraintDescriptor;
import java.lang.annotation.Annotation;
import java.util.Map;

/**
 * Builds a {@link TextboxFormElement} for a given {@link EntityPropertyDescriptor}.
 *
 * @author Arne Vandamme
 */
public class TextboxFormElementBuilderFactory extends FormGroupElementBuilderFactorySupport
{
	@Autowired
	private ConversionService conversionService;

	private int maximumSingleLineLength = 300;

	public TextboxFormElementBuilderFactory() {
		addProcessor( new TextboxConstraintsProcessor() );
	}

	public void setMaximumSingleLineLength( int maximumSingleLineLength ) {
		this.maximumSingleLineLength = maximumSingleLineLength;
	}

	@Override
	public boolean supports( String viewElementType ) {
		return BootstrapUiElements.TEXTAREA.equals( viewElementType )
				|| BootstrapUiElements.TEXTBOX.equals( viewElementType );
	}

	@Override
	protected ViewElementBuilder createControlBuilder( EntityPropertyDescriptor propertyDescriptor,
	                                                   EntityPropertyRegistry entityPropertyRegistry,
	                                                   EntityConfiguration entityConfiguration,
	                                                   ViewElementMode viewElementMode ) {
		return new TextboxFormElementBuilder()
				.name( propertyDescriptor.getName() )
				.controlName( propertyDescriptor.getName() )
				.multiLine( String.class.equals( propertyDescriptor.getPropertyType() ) )
				.postProcessor( new EntityValueProcessor( conversionService, propertyDescriptor ) );
	}

	/**
	 * Responsible for calculating max length and multi/single line type based on validation constraints.
	 */
	private class TextboxConstraintsProcessor extends ValidationConstraintsBuilderProcessor<FormGroupElementBuilder>
	{
		@Override
		protected void handleConstraint( FormGroupElementBuilder builder,
		                                 Annotation annotation,
		                                 Map<String, Object> attributes,
		                                 ConstraintDescriptor constraint ) {
			TextboxFormElementBuilder textbox = builder.getControl( TextboxFormElementBuilder.class );

			if ( textbox != null ) {
				if ( isOfType( annotation, Size.class, Length.class ) ) {
					Integer max = (Integer) attributes.get( "max" );

					if ( max != Integer.MAX_VALUE ) {
						textbox.maxLength( max );
						textbox.multiLine( max > maximumSingleLineLength );
					}
				}
			}
		}
	}

	/**
	 * Responsible for fetching the property value and setting it on the textbox.
	 */
	public static class EntityValueProcessor implements ViewElementPostProcessor<TextboxFormElement>
	{
		private static final TypeDescriptor STRING_TYPE = TypeDescriptor.valueOf( String.class );

		private final ConversionService conversionService;
		private final EntityPropertyDescriptor propertyDescriptor;

		public EntityValueProcessor( ConversionService conversionService,
		                             EntityPropertyDescriptor propertyDescriptor ) {
			this.conversionService = conversionService;
			this.propertyDescriptor = propertyDescriptor;
		}

		@Override
		@SuppressWarnings("unchecked")
		public void postProcess( ViewElementBuilderContext builderContext, TextboxFormElement textbox ) {
			Object entity = builderContext.getAttribute( EntityView.ATTRIBUTE_ENTITY );
			ValueFetcher valueFetcher = propertyDescriptor.getValueFetcher();

			if ( entity != null && valueFetcher != null ) {
				String text = (String) conversionService.convert( valueFetcher.getValue( entity ),
				                                                  propertyDescriptor.getPropertyTypeDescriptor(),
				                                                  STRING_TYPE );

				textbox.setText( text );
			}
		}
	}
}
