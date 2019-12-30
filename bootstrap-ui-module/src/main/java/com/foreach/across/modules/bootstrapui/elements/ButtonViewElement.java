/*
 * Copyright 2019 the original author or authors
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

import com.foreach.across.modules.bootstrapui.styles.BootstrapStyleRule;
import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.elements.AbstractNodeViewElement;
import com.foreach.across.modules.web.ui.elements.ConfigurableTextViewElement;
import com.foreach.across.modules.web.ui.elements.ContainerViewElement;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Collection;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * Represents a Bootstrap button.
 *
 * @author Arne Vandamme
 */
@Accessors(chain = true)
@Getter
@Setter
public class ButtonViewElement extends AbstractNodeViewElement implements ConfigurableTextViewElement, FormInputElement
{
	public static final String ELEMENT_TYPE = BootstrapUiElements.BUTTON;
	private String text, title, url = "#";
	private Style style;
	private BootstrapStyleRule styleRule;

	@NonNull
	private Type type = Type.BUTTON;

	private State state;
	private Size size;

	/**
	 * Set the icon to be aligned on the left-hand side of the text (if there is any text).
	 */
	private ViewElement icon;

	private String controlName, value;

	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	private boolean htmlIdSpecified;

	public ButtonViewElement() {
		super( ELEMENT_TYPE );
		setTagName( "button" );
		setStyle( Style.Button.DEFAULT );
	}

	public ButtonViewElement setStyle( Style style ) {
		this.style = style;
		if ( styleRule != null ) {
			remove( styleRule );
		}
		styleRule = Style.Button.toBootstrapStyleRule( style );
		if ( styleRule != null ) {
			super.set( styleRule );
		}
		return this;
	}

	@Override
	public ButtonViewElement setName( String name ) {
		super.setName( name );
		if ( controlName == null ) {
			setControlName( name );
		}
		return this;
	}

	@Override
	public String getControlName() {
		return controlName;
	}

	@Override
	public ButtonViewElement setControlName( String controlName ) {
		this.controlName = controlName;
		if ( !htmlIdSpecified ) {
			super.setHtmlId( controlName );
		}
		return this;
	}

	@Override
	public ButtonViewElement setHtmlId( String htmlId ) {
		this.htmlIdSpecified = true;
		super.setHtmlId( htmlId );
		return this;
	}

	@Override
	public boolean isDisabled() {
		return state == State.DISABLED;
	}

	@Override
	public ButtonViewElement setDisabled( boolean disabled ) {
		return setState( disabled ? State.DISABLED : null );
	}

	@Override
	public ButtonViewElement addCssClass( String... cssClass ) {
		super.addCssClass( cssClass );
		return this;
	}

	@Override
	public ButtonViewElement removeCssClass( String... cssClass ) {
		super.removeCssClass( cssClass );
		return this;
	}

	@Override
	public ButtonViewElement setAttributes( Map<String, Object> attributes ) {
		super.setAttributes( attributes );
		return this;
	}

	@Override
	public ButtonViewElement setAttribute( String attributeName, Object attributeValue ) {
		super.setAttribute( attributeName, attributeValue );
		return this;
	}

	@Override
	public ButtonViewElement addAttributes( Map<String, Object> attributes ) {
		super.addAttributes( attributes );
		return this;
	}

	@Override
	public ButtonViewElement removeAttribute( String attributeName ) {
		super.removeAttribute( attributeName );
		return this;
	}

	@Override
	public ButtonViewElement setCustomTemplate( String customTemplate ) {
		super.setCustomTemplate( customTemplate );
		return this;
	}

	@Override
	protected ButtonViewElement setElementType( String elementType ) {
		super.setElementType( elementType );
		return this;
	}

	@Override
	public ButtonViewElement addChild( ViewElement element ) {
		super.addChild( element );
		return this;
	}

	@Override
	public ButtonViewElement addChildren( Collection<? extends ViewElement> elements ) {
		super.addChildren( elements );
		return this;
	}

	@Override
	public ButtonViewElement addFirstChild( ViewElement element ) {
		super.addFirstChild( element );
		return this;
	}

	@Override
	public ButtonViewElement clearChildren() {
		super.clearChildren();
		return this;
	}

	@Override
	public ButtonViewElement apply( Consumer<ContainerViewElement> consumer ) {
		super.apply( consumer );
		return this;
	}

	@Override
	public <U extends ViewElement> ButtonViewElement applyUnsafe( Consumer<U> consumer ) {
		super.applyUnsafe( consumer );
		return this;
	}

	@Override
	protected ButtonViewElement setTagName( String tagName ) {
		super.setTagName( tagName );
		return this;
	}

	@Override
	public ButtonViewElement set( WitherSetter... setters ) {
		Stream.of( setters )
		      .forEach( setter -> {
			      if ( setter instanceof BootstrapStyleRule ) {
				      BootstrapStyleRule sr = (BootstrapStyleRule) setter;
				      Style buttonStyle = Style.Button.fromBootstrapStyleRule( sr );
				      if ( buttonStyle != null ) {
					      if ( styleRule != null ) {
						      remove( styleRule );
					      }
					      style = buttonStyle;
					      styleRule = sr;
				      }
			      }
			      super.set( setter );
		      } );
		return this;
	}

	@Override
	public ButtonViewElement remove( WitherRemover... functions ) {
		super.remove( functions );
		return this;
	}

	public enum Type
	{
		BUTTON,
		BUTTON_SUBMIT,
		BUTTON_RESET,
		INPUT,
		INPUT_SUBMIT,
		INPUT_RESET,
		LINK
	}

	public enum State
	{
		ACTIVE,
		DISABLED
	}
}
