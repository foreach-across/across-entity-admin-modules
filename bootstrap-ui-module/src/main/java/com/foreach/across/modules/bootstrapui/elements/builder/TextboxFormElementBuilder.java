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

import com.foreach.across.modules.bootstrapui.elements.TextareaFormElement;
import com.foreach.across.modules.bootstrapui.elements.TextboxFormElement;
import com.foreach.across.modules.bootstrapui.resource.BootstrapUiFormElementsWebResources;
import com.foreach.across.modules.web.resource.WebResourceRegistry;
import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.ViewElementBuilder;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.ViewElementPostProcessor;

import java.util.Map;

/**
 * Reponsible for building both {@link com.foreach.across.modules.bootstrapui.elements.TextboxFormElement}
 * and {@link com.foreach.across.modules.bootstrapui.elements.TextareaFormElement}.
 */
public class TextboxFormElementBuilder extends FormControlElementBuilderSupport<TextboxFormElement, TextboxFormElementBuilder>
{
	private Boolean autoSize;

	private boolean multiLine = false;

	private TextboxFormElement.Type type;
	private String placeholder, text;
	private Integer rows, maxLength;

	/**
	 * Will create a textarea element.
	 *
	 * @return current builder
	 */
	public TextboxFormElementBuilder multiLine() {
		return multiLine( true );
	}

	/**
	 * Should a textarea element be created.
	 *
	 * @param multiLine true if a textarea should be created
	 * @return current builder
	 */
	public TextboxFormElementBuilder multiLine( boolean multiLine ) {
		this.multiLine = multiLine;
		return this;
	}

	/**
	 * Will create a textarea element with the set number of rows.
	 *
	 * @return current builder
	 */
	public TextboxFormElementBuilder multiLine( int rows ) {
		return rows( rows );
	}

	/**
	 * Set the textbox to resize automatically.  Only supported by multi line textboxes.
	 *
	 * @return current builder
	 */
	public TextboxFormElementBuilder autoSize() {
		return autoSize( true );
	}

	/**
	 * Set the textbox to resize automatically or not.
	 *
	 * @param autoSize true if box should resize automatically
	 * @return current builder
	 */
	public TextboxFormElementBuilder autoSize( boolean autoSize ) {
		this.autoSize = autoSize;
		return this;
	}

	/**
	 * Set the maxlength attribute. Usually only applies to non-textarea elements.
	 *
	 * @param maxLength maximum allowed length of the value
	 * @return current builder
	 */
	public TextboxFormElementBuilder maxLength( Integer maxLength ) {
		this.maxLength = maxLength;
		return this;
	}

	/**
	 * Will create a password type element.
	 *
	 * @return current builder
	 */
	public TextboxFormElementBuilder password() {
		return type( TextboxFormElement.Type.PASSWORD );
	}

	public TextboxFormElementBuilder type( TextboxFormElement.Type type ) {
		this.type = type;
		return this;
	}

	public TextboxFormElementBuilder placeholder( String placeholder ) {
		this.placeholder = placeholder;
		return this;
	}

	public TextboxFormElementBuilder text( String text ) {
		this.text = text;
		return this;
	}

	/**
	 * Will switch to creating a textarea element with the set number of rows.
	 *
	 * @param rows Number of rows to display.
	 * @return current builder
	 */
	public TextboxFormElementBuilder rows( int rows ) {
		multiLine = true;
		this.rows = rows;
		return this;
	}

	@Override
	public TextboxFormElementBuilder controlName( String controlName ) {
		return super.controlName( controlName );
	}

	@Override
	public TextboxFormElementBuilder disabled() {
		return super.disabled();
	}

	@Override
	public TextboxFormElementBuilder disabled( boolean disabled ) {
		return super.disabled( disabled );
	}

	@Override
	public TextboxFormElementBuilder readonly() {
		return super.readonly();
	}

	@Override
	public TextboxFormElementBuilder readonly( boolean readonly ) {
		return super.readonly( readonly );
	}

	@Override
	public TextboxFormElementBuilder required() {
		return super.required();
	}

	@Override
	public TextboxFormElementBuilder required( boolean required ) {
		return super.required( required );
	}

	@Override
	public TextboxFormElementBuilder htmlId( String htmlId ) {
		return super.htmlId( htmlId );
	}

	@Override
	public TextboxFormElementBuilder attribute( String name, Object value ) {
		return super.attribute( name, value );
	}

	@Override
	public TextboxFormElementBuilder attributes( Map<String, Object> attributes ) {
		return super.attributes( attributes );
	}

	@Override
	public TextboxFormElementBuilder removeAttribute( String name ) {
		return super.removeAttribute( name );
	}

	@Override
	public TextboxFormElementBuilder clearAttributes() {
		return super.clearAttributes();
	}

	@Override
	public TextboxFormElementBuilder add( ViewElement... viewElements ) {
		return super.add( viewElements );
	}

	@Override
	public TextboxFormElementBuilder add( ViewElementBuilder... viewElements ) {
		return super.add( viewElements );
	}

	@Override
	public TextboxFormElementBuilder addAll( Iterable<?> viewElements ) {
		return super.addAll( viewElements );
	}

	@Override
	public TextboxFormElementBuilder sort( String... elementNames ) {
		return super.sort( elementNames );
	}

	@Override
	public TextboxFormElementBuilder name( String name ) {
		return super.name( name );
	}

	@Override
	public TextboxFormElementBuilder customTemplate( String template ) {
		return super.customTemplate( template );
	}

	@Override
	public TextboxFormElementBuilder postProcessor( ViewElementPostProcessor<TextboxFormElement> postProcessor ) {
		return super.postProcessor( postProcessor );
	}

	@Override
	protected TextboxFormElement createElement( ViewElementBuilderContext builderContext ) {
		TextboxFormElement textbox;

		if ( multiLine ) {
			TextareaFormElement textarea = new TextareaFormElement();
			if ( rows != null ) {
				textarea.setRows( rows );
			}

			if ( autoSize != null ) {
				textarea.setAutoSize( autoSize );
			}

			textbox = textarea;
		}
		else {
			textbox = new TextboxFormElement();
		}

		if ( text != null ) {
			textbox.setText( text );
		}
		if ( type != null ) {
			textbox.setType( type );
		}
		if ( placeholder != null ) {
			textbox.setPlaceholder( placeholder );
		}
		if ( maxLength != null ) {
			textbox.setMaxLength( maxLength );
		}

		return apply( textbox, builderContext );
	}

	@Override
	protected void registerWebResources( WebResourceRegistry webResourceRegistry ) {
		webResourceRegistry.addPackage( BootstrapUiFormElementsWebResources.NAME );
	}
}
