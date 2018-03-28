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

import com.foreach.across.modules.bootstrapui.elements.FaIcon;
import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.elements.ConfigurableTextViewElement;
import com.foreach.across.modules.web.ui.elements.NodeViewElement;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

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
public class TooltipViewElement extends NodeViewElement implements ConfigurableTextViewElement
{
	/**
	 * -- SETTER --
	 * Set the icon view element for this tooltip.
	 * This will always be the first child of the tooltip node.
	 * Defaults to a question mark {@link com.foreach.across.modules.bootstrapui.elements.FaIcon}.
	 */
	@Getter
	@Setter
	private ViewElement icon;

	public TooltipViewElement() {
		super( "a" );
		setEscapeHtml( false );
		setAttribute( "data-toggle", "tooltip" );
		addCssClass( "tooltip-link", "text-muted" );
		setIcon( new FaIcon( FaIcon.WebApp.QUESTION_CIRCLE ) );
	}

	public boolean isEscapeHtml() {
		return !Boolean.TRUE.equals( getAttribute( "data-html", Boolean.class ) );
	}

	/**
	 * Should HTML text be escaped. By default HTML is supported.
	 *
	 * @param escapeHtml should HTML text be escaped
	 */
	public void setEscapeHtml( boolean escapeHtml ) {
		setAttribute( "data-html", !escapeHtml );
	}

	/**
	 * Set the tooltip text.
	 *
	 * @param text to set
	 */
	@Override
	public void setText( String text ) {
		setAttribute( "title", text );
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
}
