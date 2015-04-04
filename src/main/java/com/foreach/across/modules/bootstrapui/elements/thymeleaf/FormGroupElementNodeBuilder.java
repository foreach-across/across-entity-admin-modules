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

import com.foreach.across.modules.bootstrapui.elements.*;
import com.foreach.across.modules.web.thymeleaf.ViewElementNodeFactory;
import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.elements.thymeleaf.NestableNodeBuilderSupport;
import org.apache.commons.lang3.StringUtils;
import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Element;
import org.thymeleaf.dom.Node;

import java.util.List;

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

		if ( group.isRequired() ) {
			attributeAppend( node, "class", "required" );
		}

		FormLayout layout = group.getFormLayout();

		ViewElement label = group.getLabel();
		if ( label != null ) {
			List<Node> labelNodes = viewElementNodeFactory.buildNodes( label, arguments );
			Element labelElement = labelElement( labelNodes );

			if ( labelElement != null ) {
				if ( layout != null && layout.getType() == FormLayout.Type.INLINE && !layout.isShowLabels() ) {
					labelElement.setAttribute( "class", "sr-only" );
				}
			}

			for ( Node childNode : labelNodes ) {
				node.addChild( childNode );
			}
		}

		ViewElement control = group.getControl();
		if ( control != null ) {
			List<Node> controlNodes = viewElementNodeFactory.buildNodes( control, arguments );

			if ( control instanceof TextboxFormElement && layout != null
					&& layout.getType() == FormLayout.Type.INLINE && !layout.isShowLabels() ) {
				Element controlElement = controlElement( controlNodes );

				if ( controlElement != null && label instanceof LabelFormElement ) {
					String labelText = ((LabelFormElement) label).getText();

					if ( labelText != null && !controlElement.hasAttribute( "placeholder" ) ) {
						controlElement.setAttribute( "placeholder", labelText );
					}
				}
			}

			for ( Node childNode : controlNodes ) {
				node.addChild( childNode );
			}
		}

		return node;
	}

	private Element labelElement( List<Node> labelNodes ) {
		if ( !labelNodes.isEmpty() ) {
			Node node = labelNodes.get( 0 );
			if ( node instanceof Element ) {
				Element candidate = (Element) node;

				if ( StringUtils.equals( candidate.getNormalizedName(), "label" ) ) {
					return candidate;
				}
			}
		}
		return null;
	}

	private Element controlElement( List<Node> controlNodes ) {
		if ( !controlNodes.isEmpty() ) {
			Node node = controlNodes.get( 0 );
			if ( node instanceof Element ) {
				Element candidate = (Element) node;

				if ( StringUtils.equals( candidate.getNormalizedName(), "input" )
						|| StringUtils.equals( candidate.getNormalizedName(), "textarea" ) ) {
					return candidate;
				}
			}
		}
		return null;
	}

	private boolean isCheckboxGroup( FormGroupElement group ) {
		return group.getLabel() instanceof LabelFormElement
				&& group.getControl() instanceof CheckboxFormElement;
	}
}
