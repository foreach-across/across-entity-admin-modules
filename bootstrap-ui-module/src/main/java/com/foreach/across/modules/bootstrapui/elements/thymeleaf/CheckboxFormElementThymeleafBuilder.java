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
import com.foreach.across.modules.web.thymeleaf.ViewElementNodeFactory;
import com.foreach.across.modules.web.ui.ViewElement;
import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Element;
import org.thymeleaf.dom.Node;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Arne Vandamme
 */
public class CheckboxFormElementThymeleafBuilder extends FormControlElementThymleafSupport<CheckboxFormElement>
{
	@Override
	public List<Node> buildNodes( CheckboxFormElement control,
	                              Arguments arguments,
	                              ViewElementNodeFactory viewElementNodeFactory ) {
		boolean showLabel = control.getText() != null || !control.isEmpty();

		Element label = createElement( "label" );
		Element checkbox = createElement( "input" );
		checkbox.setAttribute( "type", "checkbox" );

		String checkboxId = retrieveHtmlId( arguments, control );
		attribute( checkbox, "id", checkboxId );
		attribute( label, "for", checkboxId );
		attribute( checkbox, "value", control.getValue(), viewElementNodeFactory );
		attribute( checkbox, "checked", control.isChecked() );
		applyProperties( control, arguments, checkbox );

		if ( showLabel ) {
			label.addChild( checkbox );

			if ( control.getText() != null ) {
				text( label, control.getText() );
			}

			for ( ViewElement child : control ) {
				for ( Node childNode : viewElementNodeFactory.buildNodes( child, arguments ) ) {
					label.addChild( childNode );
				}
			}
		}

		Element hidden = null;

		if ( control.getControlName() != null ) {
			hidden = createElement( "input" );
			hidden.setAttribute( "type", "hidden" );
			hidden.setAttribute( "name", "_" + control.getControlName() );
			hidden.setAttribute( "value", "on" );
		}

		if ( control.isWrapped() ) {
			Element wrapper = createElement( "div" );
			wrapper.setAttribute( "class", "checkbox" );

			if ( control.isDisabled() ) {
				wrapper.setAttribute( "class", wrapper.getAttributeValue( "class" ) + " disabled" );
			}

			if ( showLabel ) {
				wrapper.addChild( label );
			}
			else {
				wrapper.addChild( checkbox );
			}

			if ( hidden != null ) {
				wrapper.addChild( hidden );
			}

			return Collections.singletonList( wrapper    );
		}

		List<Node> nodes = new ArrayList<>( 2 );
		nodes.add( showLabel ? label : checkbox );
		if ( hidden != null ) {
			nodes.add( hidden );
		}

		return nodes;
	}

	@Override
	protected Element createNode( CheckboxFormElement control,
	                              Arguments arguments,
	                              ViewElementNodeFactory viewElementNodeFactory ) {
		return null;
	}
}
