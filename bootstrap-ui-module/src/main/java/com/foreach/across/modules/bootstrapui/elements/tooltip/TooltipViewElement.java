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

package com.foreach.across.modules.bootstrapui.elements.tooltip;

import com.foreach.across.modules.bootstrapui.styles.BootstrapStyles;
import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.elements.ConfigurableTextViewElement;
import com.foreach.across.modules.web.ui.elements.ContainerViewElement;
import com.foreach.across.modules.web.ui.elements.NodeViewElement;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static com.foreach.across.modules.bootstrapui.attributes.BootstrapAttributes.attribute;
import static com.foreach.across.modules.web.ui.elements.HtmlViewElements.i;

/**
 * Represents a default tooltip view element, rendered as a link with a
 * question mark icon, where the tooltip text is shown when hovering.
 * <p/>
 * Uses Bootstrap tooltip support. This is a convenience class as a tooltip can be
 * pretty much any node with the right HTML attributes set.
 *
 * @author Arne Vandamme
 * @since 2.0.0
 */
@Accessors(chain = true)
public class TooltipViewElement extends NodeViewElement implements ConfigurableTextViewElement
{
	/**
	 * -- SETTER --
	 * Set the icon view element for this tooltip.
	 * This will always be the first child of the tooltip node.
	 */
	@Getter
	@Setter
	private ViewElement icon;

	public TooltipViewElement() {
		super( "a" );
		setEscapeHtml( false );
		set( attribute.data.toggle.tooltip );
		addCssClass( "tooltip-link", "text-muted" );
		// todo use icon set
		setIcon( i( BootstrapStyles.css.fa.solid( "question-circle" ) ).set( attribute.aria.hidden ) );
	}

	public boolean isEscapeHtml() {
		return !Boolean.TRUE.equals( get( attribute.data( "html" ).as( Boolean.class ) ) );
	}

	/**
	 * Should HTML text be escaped. By default HTML is supported.
	 *
	 * @param escapeHtml should HTML text be escaped
	 */
	public TooltipViewElement setEscapeHtml( boolean escapeHtml ) {
		return set( attribute.data( "html", !escapeHtml ) );
	}

	/**
	 * Set the tooltip text.
	 *
	 * @param text to set
	 */
	@Override
	public TooltipViewElement setText( String text ) {
		return setAttribute( "title", text );
	}

	@Override
	public String getText() {
		return getAttribute( "title", String.class );
	}

	@Override
	public List<ViewElement> getChildren() {
		List<ViewElement> manualChildren = super.getChildren();
		List<ViewElement> children = new ArrayList<>( manualChildren.size() + 1 );

		if ( icon != null ) {
			children.add( icon );
		}
		children.addAll( manualChildren );

		return children;
	}

	@Override
	public TooltipViewElement setTagName( String tagName ) {
		super.setTagName( tagName );
		return this;
	}

	@Override
	public TooltipViewElement setHtmlId( String htmlId ) {
		super.setHtmlId( htmlId );
		return this;
	}

	@Override
	public TooltipViewElement addCssClass( String... cssClass ) {
		super.addCssClass( cssClass );
		return this;
	}

	@Override
	public TooltipViewElement removeCssClass( String... cssClass ) {
		super.removeCssClass( cssClass );
		return this;
	}

	@Override
	public TooltipViewElement setAttributes( Map<String, Object> attributes ) {
		super.setAttributes( attributes );
		return this;
	}

	@Override
	public TooltipViewElement setAttribute( String attributeName, Object attributeValue ) {
		super.setAttribute( attributeName, attributeValue );
		return this;
	}

	@Override
	public TooltipViewElement addAttributes( Map<String, Object> attributes ) {
		super.addAttributes( attributes );
		return this;
	}

	@Override
	public TooltipViewElement removeAttribute( String attributeName ) {
		super.removeAttribute( attributeName );
		return this;
	}

	@Override
	public TooltipViewElement setName( String name ) {
		super.setName( name );
		return this;
	}

	@Override
	public TooltipViewElement setCustomTemplate( String customTemplate ) {
		super.setCustomTemplate( customTemplate );
		return this;
	}

	@Override
	protected TooltipViewElement setElementType( String elementType ) {
		super.setElementType( elementType );
		return this;
	}

	@Override
	public TooltipViewElement addChild( ViewElement element ) {
		super.addChild( element );
		return this;
	}

	@Override
	public TooltipViewElement addChildren( Collection<? extends ViewElement> elements ) {
		super.addChildren( elements );
		return this;
	}

	@Override
	public TooltipViewElement addFirstChild( ViewElement element ) {
		super.addFirstChild( element );
		return this;
	}

	@Override
	public TooltipViewElement clearChildren() {
		super.clearChildren();
		return this;
	}

	@Override
	public TooltipViewElement apply( Consumer<ContainerViewElement> consumer ) {
		super.apply( consumer );
		return this;
	}

	@Override
	public <U extends ViewElement> TooltipViewElement applyUnsafe( Consumer<U> consumer ) {
		super.applyUnsafe( consumer );
		return this;
	}

	@Override
	public TooltipViewElement set( WitherSetter... setters ) {
		super.set( setters );
		return this;
	}

	@Override
	public TooltipViewElement remove( WitherRemover... functions ) {
		super.remove( functions );
		return this;
	}
}
