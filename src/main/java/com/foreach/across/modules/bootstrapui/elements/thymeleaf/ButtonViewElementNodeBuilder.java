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
package com.foreach.across.modules.bootstrapui.elements.thymeleaf;

import com.foreach.across.modules.bootstrapui.elements.ButtonViewElement;
import com.foreach.across.modules.bootstrapui.elements.Size;
import com.foreach.across.modules.web.thymeleaf.ViewElementNodeFactory;
import com.foreach.across.modules.web.ui.elements.thymeleaf.NestableNodeBuilderSupport;
import org.apache.commons.lang3.StringUtils;
import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Element;
import org.thymeleaf.dom.Text;

/**
 * @author Arne Vandamme
 */
public class ButtonViewElementNodeBuilder extends NestableNodeBuilderSupport<ButtonViewElement>
{
	@Override
	protected Element createNode( ButtonViewElement button,
	                              Arguments arguments,
	                              ViewElementNodeFactory viewElementNodeFactory ) {
		Element node = basicNode( button );
		style( button, node );
		text( button, node );
		size( button, node );
		state( button, node );

		if ( button.getType() == ButtonViewElement.Type.LINK ) {
			attribute( node, "href", button.getUrl() );
		}
		else if ( !StringUtils.equals( "#", button.getUrl() ) ) {
			attribute( node, "data-url", button.getUrl() );
		}

		return node;
	}

	private void style( ButtonViewElement button, Element node ) {
		node.setAttribute( "class", "btn " + button.getStyle().forPrefix( "btn" ) );
	}

	private void size( ButtonViewElement button, Element node ) {
		Size size = button.getSize();
		if ( size != null && !Size.DEFAULT.equals( size ) ) {
			node.setAttribute( "class", node.getAttributeValue( "class" ) + " " + button.getSize().forPrefix( "btn" ) );
		}
	}

	private void state( ButtonViewElement button, Element node ) {
		if ( button.getState() == ButtonViewElement.State.ACTIVE ) {
			node.setAttribute( "class", node.getAttributeValue( "class" ) + " active" );
		}
		else if ( button.getState() == ButtonViewElement.State.DISABLED ) {
			if ( button.getType() == ButtonViewElement.Type.LINK ) {
				node.setAttribute( "class", node.getAttributeValue( "class" ) + " disabled" );
			}
			else {
				node.setAttribute( "disabled", "disabled" );
			}
		}
	}

	private void text( ButtonViewElement button, Element node ) {
		String text = button.getText();
		if ( text != null ) {
			switch ( button.getType() ) {
				case BUTTON:
				case BUTTON_RESET:
				case BUTTON_SUBMIT:
				case LINK:
					node.addChild( new Text( text ) );
					break;
				default:
					node.setAttribute( "value", text );
					break;
			}
		}
	}

	private Element basicNode( ButtonViewElement button ) {
		Element node = new Element( tagName( button ) );

		switch ( button.getType() ) {
			case BUTTON_RESET:
			case INPUT_RESET:
				node.setAttribute( "type", "reset" );
				break;
			case BUTTON_SUBMIT:
			case INPUT_SUBMIT:
				node.setAttribute( "type", "submit" );
				break;
			case BUTTON:
			case INPUT:
				node.setAttribute( "type", "button" );
				break;
			case LINK:
				node.setAttribute( "role", "button" );
				break;
		}

		return node;
	}

	private String tagName( ButtonViewElement button ) {
		switch ( button.getType() ) {
			case INPUT:
			case INPUT_RESET:
			case INPUT_SUBMIT:
				return "input";
			case LINK:
				return "a";
		}

		return "button";
	}
}
