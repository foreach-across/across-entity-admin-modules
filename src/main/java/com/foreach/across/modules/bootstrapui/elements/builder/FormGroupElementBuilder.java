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
import com.foreach.across.modules.web.ui.elements.builder.NodeViewElementSupportBuilder;

import java.util.Map;

public class FormGroupElementBuilder extends NodeViewElementSupportBuilder<FormGroupElement, FormGroupElementBuilder>
{
	private ElementOrBuilder label, control;
	private FormLayout formLayout;

	public ElementOrBuilder getLabel() {
		return label;
	}

	public ElementOrBuilder getControl() {
		return control;
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
	public FormGroupElement build( ViewElementBuilderContext builderContext ) {
		FormGroupElement group = new FormGroupElement();

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
