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
import com.foreach.across.modules.web.ui.elements.AbstractTextNodeViewElement;
import com.foreach.across.modules.web.ui.elements.NodeViewElement;
import com.foreach.across.modules.web.ui.elements.TextViewElement;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a Bootstrap alert.
 *
 * @author Arne Vandamme
 * @since 1.0.0
 */
public class AlertViewElement extends AbstractTextNodeViewElement
{
	/**
	 * CSS class to add to a link inside an alert box.
	 */
	public static final String CSS_LINK = "alert-link";

	private static final String CSS_DISMISSIBLE = "alert-dismissible";
	private static final String CSS_PREFIX = "alert";

	private String closeLabel = "Close";
	private Style style;

	public AlertViewElement() {
		super( "div" );
		setAttribute( "role", CSS_PREFIX );
		addCssClass( CSS_PREFIX );
	}

	public void setStyle( Style style ) {
		if ( this.style != null ) {
			removeCssClass( this.style.forPrefix( CSS_PREFIX ) );
		}
		this.style = style;
		if ( style != null ) {
			addCssClass( style.forPrefix( CSS_PREFIX ) );
		}
	}

	public Style getStyle() {
		return style;
	}

	/**
	 * Should the alert be dismissible or not?  If so a button will be added.
	 *
	 * @param dismissible should the button be added
	 */
	public void setDismissible( boolean dismissible ) {
		if ( dismissible ) {
			addCssClass( CSS_DISMISSIBLE );
		}
		else {
			removeCssClass( CSS_DISMISSIBLE );
		}
	}

	public boolean isDismissible() {
		return hasCssClass( CSS_DISMISSIBLE );
	}

	/**
	 * Set the label for the close button if the alert is dismissible.
	 *
	 * @param closeLabel text
	 */
	public void setCloseLabel( String closeLabel ) {
		this.closeLabel = closeLabel;
	}

	public String getCloseLabel() {
		return closeLabel;
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
}
