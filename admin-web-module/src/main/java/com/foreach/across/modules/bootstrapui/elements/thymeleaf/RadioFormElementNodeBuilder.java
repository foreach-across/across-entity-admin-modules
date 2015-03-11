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

import com.foreach.across.modules.bootstrapui.elements.RadioFormElement;
import com.foreach.across.modules.web.thymeleaf.ViewElementNodeFactory;
import com.foreach.across.modules.web.ui.ViewElement;
import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Element;
import org.thymeleaf.dom.Node;

import java.util.Collections;
import java.util.List;

/**
 * @author Arne Vandamme
 */
public class RadioFormElementNodeBuilder extends FormControlElementBuilderSupport<RadioFormElement>
{
	@Override
	public List<Node> buildNodes( RadioFormElement control,
	                              Arguments arguments,
	                              ViewElementNodeFactory viewElementNodeFactory ) {
		Element wrapper = new Element( "div" );
		wrapper.setAttribute( "class", "radio" );

		Element label = new Element( "label" );
		Element checkbox = new Element( "input" );
		checkbox.setAttribute( "type", "radio" );

		attribute( checkbox, "id", retrieveHtmlId( arguments, control ) );
		attribute( checkbox, "value", control.getValue(), viewElementNodeFactory );
		attribute( checkbox, "checked", control.isChecked() );
		applyProperties( control, arguments, checkbox );

		if ( control.getLabel() != null ) {
			text( label, " " + control.getLabel() );
		}

		for ( ViewElement child : control ) {
			for ( Node childNode : viewElementNodeFactory.buildNodes( child, arguments ) ) {
				label.addChild( childNode );
			}
		}

		label.addChild( checkbox );
		wrapper.addChild( label );

		return Collections.singletonList( (Node) wrapper );
	}

	@Override
	protected Element createNode( RadioFormElement control,
	                              Arguments arguments,
	                              ViewElementNodeFactory viewElementNodeFactory ) {
		return null;
	}

}
