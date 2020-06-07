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
package com.foreach.across.modules.bootstrapui.elements;

import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.elements.AbstractNodeViewElement;
import com.foreach.across.modules.web.ui.elements.ContainerViewElement;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpMethod;
import org.springframework.util.Assert;
import org.springframework.validation.Errors;

import java.util.Collection;
import java.util.Map;
import java.util.function.Consumer;

import static com.foreach.across.modules.bootstrapui.elements.FormLayout.Type.HORIZONTAL;
import static com.foreach.across.modules.bootstrapui.elements.FormLayout.Type.INLINE;

/**
 * Represents a HTML form element, supporting a Bootstrap {@link FormLayout} that will automatically
 * be applied to all controls of the form that do not have a separate layout specified.
 *
 * @author Arne Vandamme
 */
@Accessors(chain = true)
public class FormViewElement extends AbstractNodeViewElement
{
	public static final String ELEMENT_TYPE = BootstrapUiElements.FORM;

	public static final String ENCTYPE_PLAIN = "text/plain";
	public static final String ENCTYPE_MULTIPART = "multipart/form-data";
	public static final String ENCTYPE_URLENCODED = "application/x-www-form-urlencoded";

	private FormLayout formLayout;
	private String commandAttribute;

	/**
	 * Set the command object that this form is bound to. Can be used as an alternative to {@link #setCommandAttribute(String)}
	 * if you do not know the model attribute name. In this case the same instance of the object is expected to be present
	 * as a request attribute, where its attribute name will be looked for.
	 * <p/>
	 * Note that a value for {@link #commandAttribute} will always take precedence over the actual object.
	 */
	@Getter
	@Setter
	private Object commandObject;

	/**
	 * Set the collection of {@link Errors} for this form. Usually contains the {@link org.springframework.validation.BindingResult}
	 * for the object bound to this form. Though setting the object is not strictly required.
	 * <p/>
	 * When set, any {@link #commandAttribute} or {@link #commandObject} will be ignored.
	 */
	@Getter
	@Setter
	private Errors errors;

	public FormViewElement() {
		super( "form" );
		setElementType( ELEMENT_TYPE );

		setAttribute( "role", "form" );

		setMethod( HttpMethod.POST );
	}

	public FormLayout getFormLayout() {
		return formLayout;
	}

	/**
	 * Set the {@link FormLayout} that should be applied to all {@link FormGroupElement} members
	 * of this form.  Also set the corresponding class.
	 *
	 * @param formLayout instance
	 */
	public FormViewElement setFormLayout( FormLayout formLayout ) {
		this.formLayout = formLayout;

		removeCssClass( "form-horizontal", "form-inline" );
		if ( formLayout != null ) {
			if ( HORIZONTAL.equals( formLayout.getType() ) ) {
				addCssClass( "form-horizontal" );
			}
			else if ( INLINE.equals( formLayout.getType() ) ) {
				addCssClass( "form-inline" );
			}
		}
		return this;
	}

	public String getCommandAttribute() {
		return commandAttribute;
	}

	/**
	 * Set the attribute name of the command object that this form is for.  All form groups
	 * within this form will have the control names prefixed and field errors bound.
	 * This is basically the equivalent of putting a {@code th:object} attribute on an element in Thymeleaf.
	 *
	 * @param commandAttribute instance
	 */
	public FormViewElement setCommandAttribute( String commandAttribute ) {
		this.commandAttribute = commandAttribute;
		return this;
	}

	public HttpMethod getMethod() {
		return HttpMethod.valueOf( StringUtils.upperCase( (String) getAttribute( "method" ) ) );
	}

	public FormViewElement setMethod( HttpMethod httpMethod ) {
		Assert.isTrue( httpMethod == HttpMethod.GET || httpMethod == HttpMethod.POST,
		               "Method POST or GET is required." );
		return setAttribute( "method", StringUtils.lowerCase( httpMethod.toString() ) );
	}

	public String getAction() {
		return getAttribute( "action", String.class );
	}

	public FormViewElement setAction( String url ) {
		return setAttribute( "action", url );
	}

	@Override
	public FormViewElement setName( String name ) {
		super.setName( name );

		if ( getFormName() == null ) {
			setFormName( name );
		}
		return this;
	}

	/**
	 * @return name attribute of the form
	 */
	public String getFormName() {
		return getAttribute( "name", String.class );
	}

	/**
	 * Set the HTML name attribute of this form.  Defaults to {@link #getName()}.
	 *
	 * @param name attribute to use
	 */
	public FormViewElement setFormName( String name ) {
		return setAttribute( "name", name );
	}

	public String getEncType() {
		return getAttribute( "enctype", String.class );
	}

	/**
	 * Set encoding type (enctype attribute) to use for this form.
	 *
	 * @param encType value
	 */
	public FormViewElement setEncType( String encType ) {
		return setAttribute( "enctype", encType );
	}

	public String getAcceptCharSet() {
		return getAttribute( "accept-charset", String.class );
	}

	public FormViewElement setAcceptCharSet( String charSet ) {
		return setAttribute( "accept-charset", charSet );
	}

	public boolean isAutoComplete() {
		return !hasAttribute( "autocomplete" ) || StringUtils.equals( (String) getAttribute( "autocomplete" ), "on" );
	}

	public FormViewElement setAutoComplete( boolean autoComplete ) {
		return setAttribute( "autocomplete", autoComplete ? "on" : "off" );
	}

	public boolean isNoValidate() {
		return hasAttribute( "novalidate" );
	}

	public FormViewElement setNoValidate( boolean noValidate ) {
		if ( noValidate ) {
			setAttribute( "novalidate", "novalidate" );
		}
		else {
			removeAttribute( "novalidate" );
		}
		return this;
	}

	@Override
	public FormViewElement addCssClass( String... cssClass ) {
		super.addCssClass( cssClass );
		return this;
	}

	@Override
	public FormViewElement removeCssClass( String... cssClass ) {
		super.removeCssClass( cssClass );
		return this;
	}

	@Override
	public FormViewElement setAttributes( Map<String, Object> attributes ) {
		super.setAttributes( attributes );
		return this;
	}

	@Override
	public FormViewElement setAttribute( String attributeName, Object attributeValue ) {
		super.setAttribute( attributeName, attributeValue );
		return this;
	}

	@Override
	public FormViewElement addAttributes( Map<String, Object> attributes ) {
		super.addAttributes( attributes );
		return this;
	}

	@Override
	public FormViewElement removeAttribute( String attributeName ) {
		super.removeAttribute( attributeName );
		return this;
	}

	@Override
	public FormViewElement setCustomTemplate( String customTemplate ) {
		super.setCustomTemplate( customTemplate );
		return this;
	}

	@Override
	protected FormViewElement setElementType( String elementType ) {
		super.setElementType( elementType );
		return this;
	}

	@Override
	public FormViewElement addChild( ViewElement element ) {
		super.addChild( element );
		return this;
	}

	@Override
	public FormViewElement addChildren( Collection<? extends ViewElement> elements ) {
		super.addChildren( elements );
		return this;
	}

	@Override
	public FormViewElement addFirstChild( ViewElement element ) {
		super.addFirstChild( element );
		return this;
	}

	@Override
	public FormViewElement clearChildren() {
		super.clearChildren();
		return this;
	}

	@Override
	public FormViewElement apply( Consumer<ContainerViewElement> consumer ) {
		super.apply( consumer );
		return this;
	}

	@Override
	public <U extends ViewElement> FormViewElement applyUnsafe( Consumer<U> consumer ) {
		super.applyUnsafe( consumer );
		return this;
	}

	@Override
	protected FormViewElement setTagName( String tagName ) {
		super.setTagName( tagName );
		return this;
	}

	@Override
	public FormViewElement setHtmlId( String htmlId ) {
		super.setHtmlId( htmlId );
		return this;
	}

	@Override
	public FormViewElement set( WitherSetter... setters ) {
		super.set( setters );
		return this;
	}

	@Override
	public FormViewElement remove( WitherRemover... functions ) {
		super.remove( functions );
		return this;
	}
}
