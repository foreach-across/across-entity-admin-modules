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

import java.util.Collection;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author Arne Vandamme
 */
public class LabelFormElement extends AbstractNodeViewElement implements ConfigurableTextViewElement
{
	public static final String ELEMENT_TYPE = BootstrapUiElements.LABEL;

	private String text;
	private Object forTarget;

	public LabelFormElement() {
		super( "label" );
		setElementType( ELEMENT_TYPE );
	}

	@Override
	public String getText() {
		return text;
	}

	@Override
	public LabelFormElement setText( String text ) {
		this.text = text;
		return this;
	}

	public LabelFormElement setTarget( String fixedId ) {
		forTarget = fixedId;
		return this;
	}

	public LabelFormElement setTarget( ViewElement viewElement ) {
		forTarget = viewElement;
		return this;
	}

	/**
	 * @return True if the target of this label is specified manually with an id.
	 * False if a {@link com.foreach.across.modules.web.ui.ViewElement} was provided or target is not set.
	 */
	public boolean isTargetId() {
		return forTarget instanceof String;
	}

	public boolean hasTarget() {
		return forTarget != null;
	}

	public String getTargetAsId() {
		return (String) forTarget;
	}

	public ViewElement getTargetAsElement() {
		return (ViewElement) forTarget;
	}

	@Override
	public LabelFormElement addCssClass( String... cssClass ) {
		super.addCssClass( cssClass );
		return this;
	}

	@Override
	public LabelFormElement removeCssClass( String... cssClass ) {
		super.removeCssClass( cssClass );
		return this;
	}

	@Override
	public LabelFormElement setAttributes( Map<String, Object> attributes ) {
		super.setAttributes( attributes );
		return this;
	}

	@Override
	public LabelFormElement setAttribute( String attributeName, Object attributeValue ) {
		super.setAttribute( attributeName, attributeValue );
		return this;
	}

	@Override
	public LabelFormElement addAttributes( Map<String, Object> attributes ) {
		super.addAttributes( attributes );
		return this;
	}

	@Override
	public LabelFormElement removeAttribute( String attributeName ) {
		super.removeAttribute( attributeName );
		return this;
	}

	@Override
	public LabelFormElement setName( String name ) {
		super.setName( name );
		return this;
	}

	@Override
	public LabelFormElement setCustomTemplate( String customTemplate ) {
		super.setCustomTemplate( customTemplate );
		return this;
	}

	@Override
	protected LabelFormElement setElementType( String elementType ) {
		super.setElementType( elementType );
		return this;
	}

	@Override
	public LabelFormElement addChild( ViewElement element ) {
		super.addChild( element );
		return this;
	}

	@Override
	public LabelFormElement addChildren( Collection<? extends ViewElement> elements ) {
		super.addChildren( elements );
		return this;
	}

	@Override
	public LabelFormElement addFirstChild( ViewElement element ) {
		super.addFirstChild( element );
		return this;
	}

	@Override
	public LabelFormElement clearChildren() {
		super.clearChildren();
		return this;
	}

	@Override
	public LabelFormElement apply( Consumer<ContainerViewElement> consumer ) {
		super.apply( consumer );
		return this;
	}

	@Override
	public <U extends ViewElement> LabelFormElement applyUnsafe( Consumer<U> consumer ) {
		super.applyUnsafe( consumer );
		return this;
	}

	@Override
	protected LabelFormElement setTagName( String tagName ) {
		super.setTagName( tagName );
		return this;
	}

	@Override
	public LabelFormElement setHtmlId( String htmlId ) {
		super.setHtmlId( htmlId );
		return this;
	}

	@Override
	public LabelFormElement set( WitherSetter... setters ) {
		super.set( setters );
		return this;
	}

	@Override
	public LabelFormElement remove( WitherRemover... functions ) {
		super.remove( functions );
		return this;
	}
}
