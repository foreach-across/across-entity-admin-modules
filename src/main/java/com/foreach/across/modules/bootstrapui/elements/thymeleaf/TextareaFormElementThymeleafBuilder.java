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

import com.foreach.across.modules.bootstrapui.elements.TextareaFormElement;
import com.foreach.across.modules.web.thymeleaf.ViewElementNodeFactory;
import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Element;

/**
 * @author Arne Vandamme
 */
public class TextareaFormElementThymeleafBuilder extends FormControlElementBuilderSupport<TextareaFormElement>
{
	@Override
	protected Element createNode( TextareaFormElement control,
	                              Arguments arguments,
	                              ViewElementNodeFactory viewElementNodeFactory ) {
		Element node = new Element( "textarea" );
		node.setAttribute( "class", "form-control" );
		node.setAttribute( "rows", String.valueOf( control.getRows() ) );

		attribute( node, "placeholder", control.getPlaceholder() );
		text( node, control.getText() );

		return node;
	}
}
