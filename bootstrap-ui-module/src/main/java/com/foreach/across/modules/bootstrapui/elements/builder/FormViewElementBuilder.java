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

import com.foreach.across.modules.bootstrapui.elements.FormLayout;
import com.foreach.across.modules.bootstrapui.elements.FormViewElement;
import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.ViewElementBuilder;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.ViewElementPostProcessor;
import org.springframework.http.HttpMethod;
import org.springframework.validation.Errors;

import java.util.Map;

public class FormViewElementBuilder extends AbstractLinkSupportingNodeViewElementBuilder<FormViewElement, FormViewElementBuilder>
{
	private String action, encType, acceptCharSet, formName;
	private boolean noValidate;
	private Boolean autoComplete;
	private HttpMethod method = HttpMethod.POST;
	private FormLayout formLayout;
	private String commandAttribute;
	private Object commandObject;
	private Errors errors;

	/**
	 * Set the attribute name of the command object that should be bound to this form.
	 * It is expected a {@link org.springframework.validation.BindingResult} is present on the model
	 * for this attribute. That {@link org.springframework.validation.BindingResult} will be used
	 * for field error binding by the {@link com.foreach.across.modules.bootstrapui.elements.FormGroupElement}
	 * members of this form.
	 *
	 * @param attributeName name of the command object
	 * @return current builder
	 */
	public FormViewElementBuilder commandAttribute( String attributeName ) {
		this.commandAttribute = attributeName;
		return this;
	}

	/**
	 * Set the command object that should be bound to this form.
	 * It is expected a {@link org.springframework.validation.BindingResult} is present on the model
	 * for this object. That {@link org.springframework.validation.BindingResult} will be used
	 * for field error binding by the {@link com.foreach.across.modules.bootstrapui.elements.FormGroupElement}
	 * members of this form.
	 *
	 * @param commandObject command bject
	 * @return current builder
	 */
	public FormViewElementBuilder commandObject( Object commandObject ) {
		this.commandObject = commandObject;
		return this;
	}

	/**
	 * Set the {@link Errors} that should be used for field error binding by t
	 * he {@link com.foreach.across.modules.bootstrapui.elements.FormGroupElement} members of this form.
	 *
	 * @param errors to use for field error lookup
	 * @return current builder
	 */
	public FormViewElementBuilder errors( Errors errors ) {
		this.errors = errors;
		return this;
	}

	/**
	 * @param url to use for the action attribute
	 * @return current builder
	 */
	public FormViewElementBuilder action( String url ) {
		this.action = url;
		return this;
	}

	/**
	 * Create a multipart form.
	 *
	 * @return current builder
	 */
	public FormViewElementBuilder multipart() {
		encType = FormViewElement.ENCTYPE_MULTIPART;
		return this;
	}

	/**
	 * Change the method attribute to GET.
	 *
	 * @return current builder
	 */
	public FormViewElementBuilder get() {
		method = HttpMethod.GET;
		return this;
	}

	/**
	 * Change the method attribute to POST.
	 *
	 * @return current builder
	 */
	public FormViewElementBuilder post() {
		method = HttpMethod.POST;
		return this;
	}

	public FormViewElementBuilder formName( String name ) {
		formName = name;
		return this;
	}

	public FormViewElementBuilder acceptCharSet( String charSet ) {
		acceptCharSet = charSet;
		return this;
	}

	public FormViewElementBuilder noValidate() {
		noValidate = true;
		return this;
	}

	public FormViewElementBuilder autoComplete( boolean on ) {
		autoComplete = on;
		return this;
	}

	public FormViewElementBuilder formLayout( FormLayout formLayout ) {
		this.formLayout = formLayout;
		return this;
	}

	@Override
	public FormViewElementBuilder htmlId( String htmlId ) {
		return super.htmlId( htmlId );
	}

	@Override
	public FormViewElementBuilder attribute( String name, Object value ) {
		return super.attribute( name, value );
	}

	@Override
	public FormViewElementBuilder attributes( Map<String, Object> attributes ) {
		return super.attributes( attributes );
	}

	@Override
	public FormViewElementBuilder removeAttribute( String name ) {
		return super.removeAttribute( name );
	}

	@Override
	public FormViewElementBuilder clearAttributes() {
		return super.clearAttributes();
	}

	@Override
	public FormViewElementBuilder add( ViewElement... viewElements ) {
		return super.add( viewElements );
	}

	@Override
	public FormViewElementBuilder add( ViewElementBuilder... viewElements ) {
		return super.add( viewElements );
	}

	@Override
	public FormViewElementBuilder addAll( Iterable<?> viewElements ) {
		return super.addAll( viewElements );
	}

	@Override
	public FormViewElementBuilder sort( String... elementNames ) {
		return super.sort( elementNames );
	}

	@Override
	public FormViewElementBuilder name( String name ) {
		return super.name( name );
	}

	@Override
	public FormViewElementBuilder customTemplate( String template ) {
		return super.customTemplate( template );
	}

	@Override
	public FormViewElementBuilder postProcessor( ViewElementPostProcessor<FormViewElement> postProcessor ) {
		return super.postProcessor( postProcessor );
	}

	@Override
	protected FormViewElement createElement( ViewElementBuilderContext viewElementBuilderContext ) {
		FormViewElement form = new FormViewElement();

		if ( commandAttribute != null ) {
			form.setCommandAttribute( commandAttribute );
		}

		if ( commandObject != null ) {
			form.setCommandObject( commandObject );
		}

		if ( errors != null ) {
			form.setErrors( errors );
		}

		if ( action != null ) {
			form.setAction( buildLink( action, viewElementBuilderContext ) );
		}

		if ( encType != null ) {
			form.setEncType( encType );
		}

		if ( acceptCharSet != null ) {
			form.setAcceptCharSet( acceptCharSet );
		}

		if ( formName != null ) {
			form.setFormName( formName );
		}

		if ( noValidate ) {
			form.setNoValidate( true );
		}

		if ( autoComplete != null ) {
			form.setAutoComplete( autoComplete );
		}

		if ( formLayout != null ) {
			form.setFormLayout( formLayout );
		}

		form.setMethod( method );

		return apply( form, viewElementBuilderContext );
	}
}
