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
package com.foreach.across.modules.bootstrapui.elements.builder;

import com.foreach.across.modules.bootstrapui.elements.FormGroupElement;
import com.foreach.across.modules.bootstrapui.elements.FormLayout;
import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.ViewElementBuilder;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.ViewElementPostProcessor;
import com.foreach.across.modules.web.ui.elements.builder.NodeViewElementSupportBuilder;

import java.util.Map;

public class FormGroupElementBuilder extends NodeViewElementSupportBuilder<FormGroupElement, FormGroupElementBuilder>
{
	private ElementOrBuilder label, control;
	private FormLayout formLayout;
	private Boolean required;

	public Boolean isRequired() {
		return required;
	}

	public ElementOrBuilder getLabel() {
		return label;
	}

	/**
	 * Gets the label configured on this builder if it is of the target type specified.
	 * If no label is set or the label is not of the specified type, null will be returned.
	 *
	 * @param targetClass target type the label should match
	 * @param <V>         target type the label should match
	 * @return label instance or null
	 */
	public <V> V getLabel( Class<V> targetClass ) {
		if ( targetClass.isInstance( label.getSource() ) ) {
			return (V) label.getSource();
		}

		return null;
	}

	public ElementOrBuilder getControl() {
		return control;
	}

	/**
	 * Gets the control configured on this builder if it is of the target type specified.
	 * If no control is set or the control is not of the specified type, null will be returned.
	 *
	 * @param targetClass target type the control should match
	 * @param <V>         target type the control should match
	 * @return control instance or null
	 */
	@SuppressWarnings("unchecked")
	public <V> V getControl( Class<V> targetClass ) {
		if ( targetClass.isInstance( control.getSource() ) ) {
			return (V) control.getSource();
		}

		return null;
	}

	public FormLayout getFormLayout() {
		return formLayout;
	}

	public FormGroupElementBuilder label( ViewElement label ) {
		this.label = ElementOrBuilder.wrap( label );
		return this;
	}

	public FormGroupElementBuilder label( ViewElementBuilder labelBuilder ) {
		this.label = ElementOrBuilder.wrap( labelBuilder );
		return this;
	}

	public FormGroupElementBuilder control( ViewElement control ) {
		this.control = ElementOrBuilder.wrap( control );
		return this;
	}

	public FormGroupElementBuilder control( ViewElementBuilder controlBuilder ) {
		this.control = ElementOrBuilder.wrap( controlBuilder );
		return this;
	}

	public FormGroupElementBuilder formLayout( FormLayout formLayout ) {
		this.formLayout = formLayout;
		return this;
	}

	public FormGroupElementBuilder required() {
		return required( true );
	}

	public FormGroupElementBuilder required( boolean required ) {
		this.required = required;
		return this;
	}

	@Override
	public FormGroupElementBuilder htmlId( String htmlId ) {
		return super.htmlId( htmlId );
	}

	@Override
	public FormGroupElementBuilder attribute( String name, Object value ) {
		return super.attribute( name, value );
	}

	@Override
	public FormGroupElementBuilder attributes( Map<String, Object> attributes ) {
		return super.attributes( attributes );
	}

	@Override
	public FormGroupElementBuilder removeAttribute( String name ) {
		return super.removeAttribute( name );
	}

	@Override
	public FormGroupElementBuilder clearAttributes() {
		return super.clearAttributes();
	}

	@Override
	public FormGroupElementBuilder add( ViewElement... viewElements ) {
		return super.add( viewElements );
	}

	@Override
	public FormGroupElementBuilder add( ViewElementBuilder... viewElements ) {
		return super.add( viewElements );
	}

	@Override
	public FormGroupElementBuilder addAll( Iterable<?> viewElements ) {
		return super.addAll( viewElements );
	}

	@Override
	public FormGroupElementBuilder sort( String... elementNames ) {
		return super.sort( elementNames );
	}

	@Override
	public FormGroupElementBuilder name( String name ) {
		return super.name( name );
	}

	@Override
	public FormGroupElementBuilder customTemplate( String template ) {
		return super.customTemplate( template );
	}

	@Override
	public FormGroupElementBuilder postProcessor( ViewElementPostProcessor<FormGroupElement> postProcessor ) {
		return super.postProcessor( postProcessor );
	}

	@Override
	protected FormGroupElement createElement( ViewElementBuilderContext builderContext ) {
		FormGroupElement group = new FormGroupElement();

		if ( required != null ) {
			group.setRequired( required );
		}

		if ( label != null ) {
			group.setLabel( label.get( builderContext ) );
		}

		if ( control != null ) {
			group.setControl( control.get( builderContext ) );
		}

		if ( formLayout != null ) {
			group.setFormLayout( formLayout );
		}

		return apply( group, builderContext );
	}
}
