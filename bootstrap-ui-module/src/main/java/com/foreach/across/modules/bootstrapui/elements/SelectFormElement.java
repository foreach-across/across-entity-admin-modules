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
import com.foreach.across.modules.web.ui.elements.ConfigurableTextViewElement;
import com.foreach.across.modules.web.ui.elements.ContainerViewElement;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Represents a HTML select element.  Supports both a default HTML select, and the more advanced bootstrap-select.
 * The latter is activated by setting a {@link SelectFormElementConfiguration} using {@link #setConfiguration(SelectFormElementConfiguration)}.
 *
 * @author Arne Vandamme
 * @see SelectFormElementConfiguration
 */
@Accessors(chain = true)
public class SelectFormElement extends FormControlElementSupport
{
	public static final String ELEMENT_TYPE = BootstrapUiElements.SELECT;
	public static final String ATTRIBUTE_DATA_SELECT = "data-bootstrapui-select";

	@Getter
	@Setter
	private boolean multiple;

	public SelectFormElement() {
		super( ELEMENT_TYPE );
		setTagName( "select" );
		addAttributes( Collections.singletonMap( BootstrapUiViewElementAttributes.CONTROL_ADAPTER_TYPE, "select" ) );
	}

	/**
	 * Get the attached bootstrap-select configuration if there is any.
	 *
	 * @return configuration or {@code null} in case of a simple HTML select
	 */
	public SelectFormElementConfiguration getConfiguration() {
		return getAttribute( ATTRIBUTE_DATA_SELECT, SelectFormElementConfiguration.class );
	}

	/**
	 * Set a bootstrap-select configuration.  If a non-null value is provided, the select
	 * will be converted into a bootstrap-select if the client-side resources have been registered.
	 *
	 * @param configuration to use
	 */
	public SelectFormElement setConfiguration( SelectFormElementConfiguration configuration ) {
		setAttribute( ATTRIBUTE_DATA_SELECT, configuration );
		if ( configuration != null ) {
			setAttribute( BootstrapUiViewElementAttributes.CONTROL_ADAPTER_TYPE, "bootstrap-select" );
		}
		return this;
	}

	@Override
	public SelectFormElement setDisabled( boolean disabled ) {
		super.setDisabled( disabled );
		return this;
	}

	@Override
	public SelectFormElement setReadonly( boolean readonly ) {
		super.setReadonly( readonly );
		return this;
	}

	@Override
	public SelectFormElement setRequired( boolean required ) {
		super.setRequired( required );
		return this;
	}

	@Override
	public SelectFormElement setName( String name ) {
		super.setName( name );
		return this;
	}

	@Override
	public SelectFormElement setControlName( String controlName ) {
		super.setControlName( controlName );
		return this;
	}

	@Override
	public SelectFormElement setHtmlId( String htmlId ) {
		super.setHtmlId( htmlId );
		return this;
	}

	@Override
	public SelectFormElement addCssClass( String... cssClass ) {
		super.addCssClass( cssClass );
		return this;
	}

	@Override
	public SelectFormElement removeCssClass( String... cssClass ) {
		super.removeCssClass( cssClass );
		return this;
	}

	@Override
	public SelectFormElement setAttributes( Map<String, Object> attributes ) {
		super.setAttributes( attributes );
		return this;
	}

	@Override
	public SelectFormElement setAttribute( String attributeName, Object attributeValue ) {
		super.setAttribute( attributeName, attributeValue );
		return this;
	}

	@Override
	public SelectFormElement addAttributes( Map<String, Object> attributes ) {
		super.addAttributes( attributes );
		return this;
	}

	@Override
	public SelectFormElement removeAttribute( String attributeName ) {
		super.removeAttribute( attributeName );
		return this;
	}

	@Override
	public SelectFormElement setCustomTemplate( String customTemplate ) {
		super.setCustomTemplate( customTemplate );
		return this;
	}

	@Override
	protected SelectFormElement setElementType( String elementType ) {
		super.setElementType( elementType );
		return this;
	}

	@Override
	public SelectFormElement addChild( ViewElement element ) {
		super.addChild( element );
		return this;
	}

	@Override
	public SelectFormElement addChildren( Collection<? extends ViewElement> elements ) {
		super.addChildren( elements );
		return this;
	}

	@Override
	public SelectFormElement addFirstChild( ViewElement element ) {
		super.addFirstChild( element );
		return this;
	}

	@Override
	public SelectFormElement clearChildren() {
		super.clearChildren();
		return this;
	}

	@Override
	public SelectFormElement apply( Consumer<ContainerViewElement> consumer ) {
		super.apply( consumer );
		return this;
	}

	@Override
	public <U extends ViewElement> SelectFormElement applyUnsafe( Consumer<U> consumer ) {
		super.applyUnsafe( consumer );
		return this;
	}

	@Override
	protected SelectFormElement setTagName( String tagName ) {
		super.setTagName( tagName );
		return this;
	}

	@Override
	public SelectFormElement set( WitherSetter... setters ) {
		super.set( setters );
		return this;
	}

	@Override
	public SelectFormElement remove( WitherRemover... functions ) {
		super.remove( functions );
		return this;
	}

	/**
	 * Single option.
	 */
	@Accessors(chain = true)
	@Getter
	@Setter
	public static class Option extends FormControlElementSupport implements ConfigurableTextViewElement
	{
		private boolean selected;
		private String text, label;
		private Object value;

		public Option() {
			super( SelectFormElement.ELEMENT_TYPE + ".option" );
			setTagName( "option" );
		}

		@Override
		public Option setDisabled( boolean disabled ) {
			super.setDisabled( disabled );
			return this;
		}

		@Override
		public Option setReadonly( boolean readonly ) {
			super.setReadonly( readonly );
			return this;
		}

		@Override
		public Option setRequired( boolean required ) {
			super.setRequired( required );
			return this;
		}

		@Override
		public Option setName( String name ) {
			super.setName( name );
			return this;
		}

		@Override
		public Option setControlName( String controlName ) {
			super.setControlName( controlName );
			return this;
		}

		@Override
		public Option setHtmlId( String htmlId ) {
			super.setHtmlId( htmlId );
			return this;
		}

		@Override
		public Option addCssClass( String... cssClass ) {
			super.addCssClass( cssClass );
			return this;
		}

		@Override
		public Option removeCssClass( String... cssClass ) {
			super.removeCssClass( cssClass );
			return this;
		}

		@Override
		public Option setAttributes( Map<String, Object> attributes ) {
			super.setAttributes( attributes );
			return this;
		}

		@Override
		public Option setAttribute( String attributeName, Object attributeValue ) {
			super.setAttribute( attributeName, attributeValue );
			return this;
		}

		@Override
		public Option addAttributes( Map<String, Object> attributes ) {
			super.addAttributes( attributes );
			return this;
		}

		@Override
		public Option removeAttribute( String attributeName ) {
			super.removeAttribute( attributeName );
			return this;
		}

		@Override
		public Option setCustomTemplate( String customTemplate ) {
			super.setCustomTemplate( customTemplate );
			return this;
		}

		@Override
		protected Option setElementType( String elementType ) {
			super.setElementType( elementType );
			return this;
		}

		@Override
		public Option addChild( ViewElement element ) {
			super.addChild( element );
			return this;
		}

		@Override
		public Option addChildren( Collection<? extends ViewElement> elements ) {
			super.addChildren( elements );
			return this;
		}

		@Override
		public Option addFirstChild( ViewElement element ) {
			super.addFirstChild( element );
			return this;
		}

		@Override
		public Option clearChildren() {
			super.clearChildren();
			return this;
		}

		@Override
		public Option apply( Consumer<ContainerViewElement> consumer ) {
			super.apply( consumer );
			return this;
		}

		@Override
		public <U extends ViewElement> Option applyUnsafe( Consumer<U> consumer ) {
			super.applyUnsafe( consumer );
			return this;
		}

		@Override
		protected Option setTagName( String tagName ) {
			super.setTagName( tagName );
			return this;
		}

		@Override
		public Option set( WitherSetter... setters ) {
			super.set( setters );
			return this;
		}

		@Override
		public Option remove( WitherRemover... functions ) {
			super.remove( functions );
			return this;
		}
	}

	/**
	 * Optgroup.
	 */
	@Accessors(chain = true)
	@Getter
	@Setter
	public static class OptionGroup extends AbstractNodeViewElement
	{
		private boolean disabled;
		private String label;

		public OptionGroup() {
			super( "optgroup" );
			setElementType( SelectFormElement.ELEMENT_TYPE + ".optgroup" );
		}

		@Override
		public OptionGroup addCssClass( String... cssClass ) {
			super.addCssClass( cssClass );
			return this;
		}

		@Override
		public OptionGroup removeCssClass( String... cssClass ) {
			super.removeCssClass( cssClass );
			return this;
		}

		@Override
		public OptionGroup setAttributes( Map<String, Object> attributes ) {
			super.setAttributes( attributes );
			return this;
		}

		@Override
		public OptionGroup setAttribute( String attributeName, Object attributeValue ) {
			super.setAttribute( attributeName, attributeValue );
			return this;
		}

		@Override
		public OptionGroup addAttributes( Map<String, Object> attributes ) {
			super.addAttributes( attributes );
			return this;
		}

		@Override
		public OptionGroup removeAttribute( String attributeName ) {
			super.removeAttribute( attributeName );
			return this;
		}

		@Override
		public OptionGroup setName( String name ) {
			super.setName( name );
			return this;
		}

		@Override
		public OptionGroup setCustomTemplate( String customTemplate ) {
			super.setCustomTemplate( customTemplate );
			return this;
		}

		@Override
		protected OptionGroup setElementType( String elementType ) {
			super.setElementType( elementType );
			return this;
		}

		@Override
		public OptionGroup addChild( ViewElement element ) {
			super.addChild( element );
			return this;
		}

		@Override
		public OptionGroup addChildren( Collection<? extends ViewElement> elements ) {
			super.addChildren( elements );
			return this;
		}

		@Override
		public OptionGroup addFirstChild( ViewElement element ) {
			super.addFirstChild( element );
			return this;
		}

		@Override
		public OptionGroup clearChildren() {
			super.clearChildren();
			return this;
		}

		@Override
		public OptionGroup apply( Consumer<ContainerViewElement> consumer ) {
			super.apply( consumer );
			return this;
		}

		@Override
		public <U extends ViewElement> OptionGroup applyUnsafe( Consumer<U> consumer ) {
			super.applyUnsafe( consumer );
			return this;
		}

		@Override
		protected OptionGroup setTagName( String tagName ) {
			super.setTagName( tagName );
			return this;
		}

		@Override
		public OptionGroup setHtmlId( String htmlId ) {
			super.setHtmlId( htmlId );
			return this;
		}

		@Override
		public OptionGroup set( WitherSetter... setters ) {
			super.set( setters );
			return this;
		}

		@Override
		public OptionGroup remove( WitherRemover... functions ) {
			super.remove( functions );
			return this;
		}
	}
}
