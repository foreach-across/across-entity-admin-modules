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
import com.foreach.across.modules.web.ui.elements.ContainerViewElement;

import java.util.Collection;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author Arne Vandamme
 * @since 1.0.0
 */
public class FileUploadFormElement extends FormControlElementSupport
{
	public static final String ELEMENT_TYPE = BootstrapUiElements.FILE_UPLOAD;

	public FileUploadFormElement() {
		super( ELEMENT_TYPE );

		setAttribute( "type", "file" );
	}

	/**
	 * @param accept format that is accepted
	 */
	public FileUploadFormElement setAccept( String accept ) {
		return setAttribute( "accept", accept );
	}

	/**
	 * @param multiple true if multiple files are allowed
	 */
	public FileUploadFormElement setMultiple( boolean multiple ) {
		return setAttribute( "multiple", multiple ? true : null );
	}

	/**
	 * @param value selected in the file upload (usually not supported)
	 */
	public FileUploadFormElement setValue( String value ) {
		return setAttribute( "value", value );
	}

	@Override
	public FileUploadFormElement setDisabled( boolean disabled ) {
		super.setDisabled( disabled );
		return this;
	}

	@Override
	public FileUploadFormElement setReadonly( boolean readonly ) {
		super.setReadonly( readonly );
		return this;
	}

	@Override
	public FileUploadFormElement setRequired( boolean required ) {
		super.setRequired( required );
		return this;
	}

	@Override
	public FileUploadFormElement setName( String name ) {
		super.setName( name );
		return this;
	}

	@Override
	public FileUploadFormElement setControlName( String controlName ) {
		super.setControlName( controlName );
		return this;
	}

	@Override
	public FileUploadFormElement setHtmlId( String htmlId ) {
		super.setHtmlId( htmlId );
		return this;
	}

	@Override
	public FileUploadFormElement addCssClass( String... cssClass ) {
		super.addCssClass( cssClass );
		return this;
	}

	@Override
	public FileUploadFormElement removeCssClass( String... cssClass ) {
		super.removeCssClass( cssClass );
		return this;
	}

	@Override
	public FileUploadFormElement setAttributes( Map<String, Object> attributes ) {
		super.setAttributes( attributes );
		return this;
	}

	@Override
	public FileUploadFormElement setAttribute( String attributeName, Object attributeValue ) {
		super.setAttribute( attributeName, attributeValue );
		return this;
	}

	@Override
	public FileUploadFormElement addAttributes( Map<String, Object> attributes ) {
		super.addAttributes( attributes );
		return this;
	}

	@Override
	public FileUploadFormElement removeAttribute( String attributeName ) {
		super.removeAttribute( attributeName );
		return this;
	}

	@Override
	public FileUploadFormElement setCustomTemplate( String customTemplate ) {
		super.setCustomTemplate( customTemplate );
		return this;
	}

	@Override
	protected FileUploadFormElement setElementType( String elementType ) {
		super.setElementType( elementType );
		return this;
	}

	@Override
	public FileUploadFormElement addChild( ViewElement element ) {
		super.addChild( element );
		return this;
	}

	@Override
	public FileUploadFormElement addChildren( Collection<? extends ViewElement> elements ) {
		super.addChildren( elements );
		return this;
	}

	@Override
	public FileUploadFormElement addFirstChild( ViewElement element ) {
		super.addFirstChild( element );
		return this;
	}

	@Override
	public FileUploadFormElement clearChildren() {
		super.clearChildren();
		return this;
	}

	@Override
	public FileUploadFormElement apply( Consumer<ContainerViewElement> consumer ) {
		super.apply( consumer );
		return this;
	}

	@Override
	public <U extends ViewElement> FileUploadFormElement applyUnsafe( Consumer<U> consumer ) {
		super.applyUnsafe( consumer );
		return this;
	}

	@Override
	protected FileUploadFormElement setTagName( String tagName ) {
		super.setTagName( tagName );
		return this;
	}

	@Override
	public FileUploadFormElement set( WitherSetter... setters ) {
		super.set( setters );
		return this;
	}

	@Override
	public FileUploadFormElement remove( WitherRemover... functions ) {
		super.remove( functions );
		return this;
	}
}
