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

import com.foreach.across.modules.bootstrapui.elements.builder.FormGroupElementBuilder;
import com.foreach.across.modules.bootstrapui.elements.builder.LabelFormElementBuilder;
import com.foreach.across.modules.bootstrapui.elements.builder.TextboxFormElementBuilder;
import com.foreach.across.modules.entity.newviews.EntityViewElementBuilderFactory;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistry;
import com.foreach.across.modules.web.ui.ViewElementBuilder;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.Size;
import javax.validation.metadata.ConstraintDescriptor;
import javax.validation.metadata.PropertyDescriptor;
import java.lang.annotation.Annotation;

/**
 * @author Arne Vandamme
 */
public class TextboxFormElementBuilderFactory implements EntityViewElementBuilderFactory
{
	public interface FormGroupElementBuilderFactoryProcessor<T extends ViewElementBuilder>
	{
		void enhance( EntityPropertyDescriptor propertyDescriptor,
		              EntityPropertyRegistry entityPropertyRegistry,
		              EntityConfiguration entityConfiguration,
		              FormGroupElementBuilder group,
		              LabelFormElementBuilder label,
		              T control );
	}

	private int maximumSingleLineLength = 300;

	public void setMaximumSingleLineLength( int maximumSingleLineLength ) {
		this.maximumSingleLineLength = maximumSingleLineLength;
	}

	@Override
	public boolean supports( String viewElementType ) {
		return false;
	}

	@Override
	public ViewElementBuilder createBuilder( EntityPropertyDescriptor propertyDescriptor,
	                                         EntityPropertyRegistry entityPropertyRegistry,
	                                         EntityConfiguration entityConfiguration ) {
		FormGroupElementBuilder group = new FormGroupElementBuilder();

		LabelFormElementBuilder label = new LabelFormElementBuilder()
				.text( propertyDescriptor.getDisplayName() );

		TextboxFormElementBuilder textbox = new TextboxFormElementBuilder()
				.name( propertyDescriptor.getName() )
				.controlName( propertyDescriptor.getName() )
				.multiLine( true );

		new TextboxConstraintsHandler()
				.enhance( propertyDescriptor, entityPropertyRegistry, entityConfiguration, group, label, textbox );

		// required yes/no
		// fetch label from the message resolver, fetch placeholder from the message resolver
		// render dependson qualifiers

		return group.label( label ).control( textbox );

	}

	private static class ConstraintsHandlerAdapter<T extends ViewElementBuilder> implements FormGroupElementBuilderFactoryProcessor<T>
	{
		@Override
		public void enhance( EntityPropertyDescriptor propertyDescriptor,
		                     EntityPropertyRegistry entityPropertyRegistry,
		                     EntityConfiguration entityConfiguration,
		                     FormGroupElementBuilder group,
		                     LabelFormElementBuilder label,
		                     T control ) {
			PropertyDescriptor validationDescriptor = propertyDescriptor.getAttribute( PropertyDescriptor.class );

			if ( validationDescriptor != null && validationDescriptor.hasConstraints() ) {
				for ( ConstraintDescriptor constraint : validationDescriptor.getConstraintDescriptors() ) {
					Annotation annotation = constraint.getAnnotation();
					Class<? extends Annotation> type = annotation.annotationType();

					handleLabelConstraint( label, type, constraint );
					handleControlConstraint( control, type, constraint );
					handleFormGroupConstraint( group, type, constraint );

//					if ( NotBlank.class.equals( type ) ) {
//						NotBlank notBlank = (NotBlank) annotation;
//						Class<?>[] groups = notBlank.groups();
//						//checkValidationGroups( template, groups );
//					}
//					else if ( NotNull.class.equals( type ) ) {
//						NotNull notBlank = (NotNull) annotation;
//						Class<?>[] groups = notBlank.groups();
//						//checkValidationGroups( template, groups );
//					}
//					else if ( NotEmpty.class.equals( type ) ) {
//						NotEmpty notBlank = (NotEmpty) annotation;
//						Class<?>[] groups = notBlank.groups();
//						//checkValidationGroups( template, groups );
//					}
//					else {
//						//handleConstraint( template, type, constraint );
//					}
				}
			}
		}

		protected void handleFormGroupConstraint( FormGroupElementBuilder group,
		                                          Class<? extends Annotation> type,
		                                          ConstraintDescriptor constraint ) {

		}

		protected void handleLabelConstraint( LabelFormElementBuilder label,
		                                      Class<? extends Annotation> type,
		                                      ConstraintDescriptor constraint ) {
		}

		protected void handleControlConstraint( T control,
		                                        Class<? extends Annotation> type,
		                                        ConstraintDescriptor constraint ) {
		}
	}

	private class TextboxConstraintsHandler extends ConstraintsHandlerAdapter<TextboxFormElementBuilder>
	{
		@Override
		protected void handleControlConstraint( TextboxFormElementBuilder control,
		                                        Class<? extends Annotation> type,
		                                        ConstraintDescriptor constraint ) {
			if ( Size.class.equals( type ) || Length.class.equals( type ) ) {
				Integer max = (Integer) constraint.getAttributes().get( "max" );

				if ( max != Integer.MAX_VALUE ) {
					control.maxLength( max );
					control.multiLine( max > maximumSingleLineLength );
				}
			}

			if ( URL.class.equals( type ) ) {
				//template.setUrl( true );
			}
		}
	}
}
