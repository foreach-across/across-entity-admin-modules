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

import com.foreach.across.modules.bootstrapui.elements.autosuggest.AutoSuggestFormElement;
import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.elements.AbstractTextNodeViewElement;
import com.foreach.across.modules.web.ui.elements.ContainerViewElement;
import com.foreach.across.modules.web.ui.elements.NodeViewElement;
import com.foreach.across.modules.web.ui.elements.TextViewElement;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Represents a Bootstrap alert.
 *
 * @author Arne Vandamme
 * @since 1.0.0
 */
@Accessors(chain = true)
public class AlertViewElement extends AbstractTextNodeViewElement
{
	/**
	 * CSS class to add to a link inside an alert box.
	 */
	public static final String CSS_LINK = "alert-link";

	private static final String CSS_DISMISSIBLE = "alert-dismissible";
	private static final String CSS_PREFIX = "alert";

	/**
	 * Set the label for the close button if the alert is dismissible.
	 */
	@Getter
	@Setter
	private String closeLabel = "Close";

	@Getter
	private Style style;

	public AlertViewElement() {
		super( "div" );
		setAttribute( "role", CSS_PREFIX );
		addCssClass( CSS_PREFIX );
	}

	public AlertViewElement setStyle( Style style ) {
		if ( this.style != null ) {
			removeCssClass( this.style.forPrefix( CSS_PREFIX ) );
		}
		this.style = style;
		if ( style != null ) {
			addCssClass( style.forPrefix( CSS_PREFIX ) );
		}
		return this;
	}

	/**
	 * Should the alert be dismissible or not?  If so a button will be added.
	 *
	 * @param dismissible should the button be added
	 */
	public AlertViewElement setDismissible( boolean dismissible ) {
		if ( dismissible ) {
			return addCssClass( CSS_DISMISSIBLE );
		}
		else {
			return removeCssClass( CSS_DISMISSIBLE );
		}
	}

	public boolean isDismissible() {
		return hasCssClass( CSS_DISMISSIBLE );
	}

	@Override
	public boolean hasChildren() {
		return isDismissible() || super.hasChildren();
	}

	@Override
	public List<ViewElement> getChildren() {
		if ( isDismissible() ) {
			List<ViewElement> children = new ArrayList<>();
			children.add( createButton() );
			children.addAll( super.getChildren() );
			return children;
		}
		return super.getChildren();
	}

	private ViewElement createButton() {
		NodeViewElement button = new NodeViewElement( "button" );
		button.setAttribute( "type", "button" );
		button.setAttribute( "class", "close" );
		button.setAttribute( "data-dismiss", CSS_PREFIX );
		button.setAttribute( "aria-label", getCloseLabel() );

		NodeViewElement span = new NodeViewElement( "span" );
		span.setAttribute( "aria-hidden", "true" );
		span.addChild( TextViewElement.xml( "&times;" ) );
		button.addChild( span );

		return button;
	}

	@Override
	protected AlertViewElement setTagName( String tagName ) {
		super.setTagName( tagName );
		return this;
	}

	@Override
	public AlertViewElement setHtmlId( String htmlId ) {
		super.setHtmlId( htmlId );
		return this;
	}

	@Override
	public AlertViewElement addCssClass( String... cssClass ) {
		super.addCssClass( cssClass );
		return this;
	}

	@Override
	public AlertViewElement removeCssClass( String... cssClass ) {
		super.removeCssClass( cssClass );
		return this;
	}

	@Override
	public AlertViewElement setAttributes( Map<String, Object> attributes ) {
		super.setAttributes( attributes );
		return this;
	}

	@Override
	public AlertViewElement setAttribute( String attributeName, Object attributeValue ) {
		super.setAttribute( attributeName, attributeValue );
		return this;
	}

	@Override
	public AlertViewElement addAttributes( Map<String, Object> attributes ) {
		super.addAttributes( attributes );
		return this;
	}

	@Override
	public AlertViewElement removeAttribute( String attributeName ) {
		super.removeAttribute( attributeName );
		return this;
	}

	@Override
	public AlertViewElement setName( String name ) {
		super.setName( name );
		return this;
	}

	@Override
	public AlertViewElement setCustomTemplate( String customTemplate ) {
		super.setCustomTemplate( customTemplate );
		return this;
	}

	@Override
	protected AlertViewElement setElementType( String elementType ) {
		super.setElementType( elementType );
		return this;
	}

	@Override
	public AlertViewElement addChild( ViewElement element ) {
		super.addChild( element );
		return this;
	}

	@Override
	public AlertViewElement addChildren( Collection<? extends ViewElement> elements ) {
		super.addChildren( elements );
		return this;
	}

	@Override
	public AlertViewElement addFirstChild( ViewElement element ) {
		super.addFirstChild( element );
		return this;
	}

	@Override
	public AlertViewElement clearChildren() {
		super.clearChildren();
		return this;
	}

	@Override
	public AlertViewElement apply( Consumer<ContainerViewElement> consumer ) {
		super.apply( consumer );
		return this;
	}

	@Override
	public <U extends ViewElement> AlertViewElement applyUnsafe( Consumer<U> consumer ) {
		super.applyUnsafe( consumer );
		return this;
	}

	@Override
	public AlertViewElement setText( String text ) {
		super.setText( text );
		return this;
	}

	@Override
	public AlertViewElement set( WitherSetter... setters ) {
		super.set( setters );
		return this;
	}

	@Override
	public AlertViewElement remove( WitherRemover... functions ) {
		super.remove( functions );
		return this;
	}
}
