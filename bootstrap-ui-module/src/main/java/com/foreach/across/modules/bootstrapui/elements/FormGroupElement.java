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
import lombok.Getter;
import lombok.Setter;

import java.util.stream.Stream;

/**
 * A form group element usually represents a single form control, with associated
 * label and description texts. It is a helper for automatic styling of forms
 * based on a general form layout.
 *
 * @author Arne Vandamme
 */
@Getter
@Setter
public class FormGroupElement extends AbstractNodeViewElement
{
	public static final String ELEMENT_TYPE = BootstrapUiElements.FORM_GROUP;

	private ViewElement label;
	private ViewElement control;
	private ViewElement helpBlock;

	/**
	 * -- SETTER --
	 * Set a tooltip element. This will be inserted inside the label, after the label text.
	 */
	@Getter
	@Setter
	private ViewElement tooltip;

	/**
	 * -- SETTER --
	 * Set the description block that should be rendered above the control,
	 * usually between the label and the control.
	 */
	@Getter
	@Setter
	private ViewElement descriptionBlock;

	private FormLayout formLayout;
	private boolean required;

	@Deprecated
	private boolean renderHelpBlockBeforeControl;

	/**
	 * -- SETTER --
	 * When rendering, should field errors be detected from the bound object.
	 * If {@code true} (default) the controlName of the form control will be used as property name of the bound
	 * object, if no such property, an exception will occur when rendering.
	 */
	private boolean detectFieldErrors = true;

	public FormGroupElement() {
		super( "div" );
		setElementType( ELEMENT_TYPE );
	}

	public <V extends ViewElement> V getLabel( Class<V> elementType ) {
		return returnIfType( label, elementType );
	}

	public <V extends ViewElement> V getControl( Class<V> elementType ) {
		return returnIfType( control, elementType );
	}

	public <V extends ViewElement> V getHelpBlock( Class<V> elementType ) {
		return returnIfType( helpBlock, elementType );
	}

	/**
	 * @return true if helpBlock should be rendered before the control (default: false)
	 */
	@Deprecated
	public boolean isRenderHelpBlockBeforeControl() {
		return renderHelpBlockBeforeControl;
	}

	@Deprecated
	public void setRenderHelpBlockBeforeControl( boolean renderHelpBlockBeforeControl ) {
		this.renderHelpBlockBeforeControl = renderHelpBlockBeforeControl;
	}

	@Override
	public Stream<ViewElement> elementStream() {
		Stream.Builder<ViewElement> stream = Stream.builder();

		if ( label != null ) {
			stream.accept( label );
		}
		if ( renderHelpBlockBeforeControl && helpBlock != null ) {
			stream.accept( helpBlock );
		}
		if ( control != null ) {
			stream.accept( control );
		}
		if ( !renderHelpBlockBeforeControl && helpBlock != null ) {
			stream.accept( helpBlock );
		}
		return Stream.concat( stream.build(), super.elementStream() );
	}

	@Override
	public boolean removeChild( ViewElement element ) {
		boolean removed = false;

		if ( element != null ) {
			if ( element.equals( label ) ) {
				setLabel( null );
				removed = true;
			}
			if ( element.equals( control ) ) {
				setControl( null );
				removed = true;
			}
			if ( element.equals( helpBlock ) ) {
				setHelpBlock( null );
				removed = true;
			}
		}
		return removed || super.removeChild( element );
	}
}
