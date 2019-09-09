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
import com.foreach.across.modules.web.ui.elements.AbstractTextNodeViewElement;
import com.foreach.across.modules.web.ui.elements.ConfigurableTextViewElement;
import com.foreach.across.modules.web.ui.elements.ContainerViewElement;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author Arne Vandamme
 */
@Accessors(chain = true)
public class FieldsetFormElement extends AbstractNodeViewElement
{
	/**
	 * Legend for adding custom children or setting legend text
	 */
	@Getter
	@Setter
	private Legend legend = new Legend();

	private String fieldsetName;

	public FieldsetFormElement() {
		super( "fieldset" );
	}

	@Override
	public FieldsetFormElement setName( String name ) {
		super.setName( name );
		if ( fieldsetName == null ) {
			setFieldsetName( name );
			fieldsetName = null;
		}
		return this;
	}

	public String getFieldsetName() {
		return getAttribute( "name", String.class );
	}

	public FieldsetFormElement setFieldsetName( String name ) {
		this.fieldsetName = name;
		if ( name != null ) {
			setAttribute( "name", name );
		}
		else {
			removeAttribute( "name" );
		}
		return this;
	}

	public boolean isDisabled() {
		return hasAttribute( "disabled" );
	}

	public FieldsetFormElement setDisabled( boolean disabled ) {
		if ( disabled ) {
			setAttribute( "disabled", "disabled" );
		}
		else {
			removeAttribute( "disabled" );
		}
		return this;
	}

	public String getFormId() {
		return getAttribute( "form", String.class );
	}

	public FieldsetFormElement setFormId( String formId ) {
		if ( formId != null ) {
			setAttribute( "form", formId );
		}
		else {
			removeAttribute( "form" );
		}
		return this;
	}

	@Override
	public List<ViewElement> getChildren() {
		List<ViewElement> children = super.getChildren();
		if ( legend != null && !legend.hasChildren() ) {
			return children;
		}

		List<ViewElement> elements = new ArrayList<>( children.size() + 1 );
		elements.add( legend );
		elements.addAll( children );

		return elements;
	}

	@Override
	public FieldsetFormElement addCssClass( String... cssClass ) {
		super.addCssClass( cssClass );
		return this;
	}

	@Override
	public FieldsetFormElement removeCssClass( String... cssClass ) {
		super.removeCssClass( cssClass );
		return this;
	}

	@Override
	public FieldsetFormElement setAttributes( Map<String, Object> attributes ) {
		super.setAttributes( attributes );
		return this;
	}

	@Override
	public FieldsetFormElement setAttribute( String attributeName, Object attributeValue ) {
		super.setAttribute( attributeName, attributeValue );
		return this;
	}

	@Override
	public FieldsetFormElement addAttributes( Map<String, Object> attributes ) {
		super.addAttributes( attributes );
		return this;
	}

	@Override
	public FieldsetFormElement removeAttribute( String attributeName ) {
		super.removeAttribute( attributeName );
		return this;
	}

	@Override
	public FieldsetFormElement setCustomTemplate( String customTemplate ) {
		super.setCustomTemplate( customTemplate );
		return this;
	}

	@Override
	protected FieldsetFormElement setElementType( String elementType ) {
		super.setElementType( elementType );
		return this;
	}

	@Override
	public FieldsetFormElement addChild( ViewElement element ) {
		super.addChild( element );
		return this;
	}

	@Override
	public FieldsetFormElement addChildren( Collection<? extends ViewElement> elements ) {
		super.addChildren( elements );
		return this;
	}

	@Override
	public FieldsetFormElement addFirstChild( ViewElement element ) {
		super.addFirstChild( element );
		return this;
	}

	@Override
	public FieldsetFormElement clearChildren() {
		super.clearChildren();
		return this;
	}

	@Override
	public FieldsetFormElement apply( Consumer<ContainerViewElement> consumer ) {
		super.apply( consumer );
		return this;
	}

	@Override
	public <U extends ViewElement> FieldsetFormElement applyUnsafe( Consumer<U> consumer ) {
		super.applyUnsafe( consumer );
		return this;
	}

	@Override
	protected FieldsetFormElement setTagName( String tagName ) {
		super.setTagName( tagName );
		return this;
	}

	@Override
	public FieldsetFormElement setHtmlId( String htmlId ) {
		super.setHtmlId( htmlId );
		return this;
	}

	@Override
	public FieldsetFormElement set( WitherSetter... setters ) {
		super.set( setters );
		return this;
	}

	@Override
	public FieldsetFormElement remove( WitherRemover... functions ) {
		super.remove( functions );
		return this;
	}

	public static class Legend extends AbstractTextNodeViewElement implements ConfigurableTextViewElement
	{
		public Legend() {
			super( "legend" );
		}

		@Override
		public Legend setHtmlId( String htmlId ) {
			super.setHtmlId( htmlId );
			return this;
		}

		@Override
		public Legend set( WitherSetter... setters ) {
			super.set( setters );
			return this;
		}

		@Override
		public Legend remove( WitherRemover... functions ) {
			super.remove( functions );
			return this;
		}
	}
}
