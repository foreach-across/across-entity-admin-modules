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

import com.foreach.across.modules.bootstrapui.elements.CheckboxFormElement;
import com.foreach.across.modules.bootstrapui.elements.FormGroupElement;
import com.foreach.across.modules.bootstrapui.elements.LabelFormElement;
import com.foreach.across.modules.web.thymeleaf.ViewElementNodeFactory;
import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.elements.thymeleaf.NestableNodeBuilderSupport;
import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Element;
import org.thymeleaf.dom.Node;

/**
 * @author Arne Vandamme
 */
public class FormGroupElementNodeBuilder extends NestableNodeBuilderSupport<FormGroupElement>
{
	@Override
	protected Element createNode( FormGroupElement group,
	                              Arguments arguments,
	                              ViewElementNodeFactory viewElementNodeFactory ) {
		Element node = new Element( "div" );
		node.setAttribute( "class", "form-group" );

//		if ( isCheckboxGroup( group ) ) {
//			LabelFormElement configured = group.getLabel();
//
//			LabelFormElement label = new LabelFormElement();
//			label.setCustomTemplate( configured.getCustomTemplate() );
//			label.getAttributes().putAll( configured.getAttributes() );
//			label.add( group.getControl() );
//			label.add( new TextViewElement( " " + configured.getText() ) );
//
//			Element div = new Element( "div" );
//			div.setAttribute( "class", "checkbox" );
//
//			for ( Node childNode : viewElementNodeFactory.buildNodes( label, arguments ) ) {
//				if ( childNode instanceof Element ) {
//					Element e = (Element) childNode;
//					if ( StringUtils.equals( "label", e.getNormalizedName() ) ) {
//						String cleanedClass = StringUtils.replace( e.getAttributeValue( "class" ), "control-label",
//						                                           "" );
//
//						if ( StringUtils.isEmpty( cleanedClass ) ) {
//							e.removeAttribute( "class" );
//						}
//						else {
//							e.setAttribute( "class", cleanedClass );
//						}
//					}
//				}
//				div.addChild( childNode );
//			}
//
//			return div;
//			// Render label
//			// Render checkbox
//		}

		ViewElement label = group.getLabel();
		if ( label != null ) {
			for ( Node childNode : viewElementNodeFactory.buildNodes( label, arguments ) ) {
				node.addChild( childNode );
			}
		}

		ViewElement control = group.getControl();
		if ( control != null ) {
			for ( Node childNode : viewElementNodeFactory.buildNodes( control, arguments ) ) {
				node.addChild( childNode );
			}
		}

		return node;
	}

	private boolean isCheckboxGroup( FormGroupElement group ) {
		return group.getLabel() instanceof LabelFormElement
				&& group.getControl() instanceof CheckboxFormElement;
	}
}
