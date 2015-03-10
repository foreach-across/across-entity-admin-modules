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

import com.foreach.across.modules.bootstrapui.elements.StaticFormElement;
import com.foreach.across.modules.web.thymeleaf.ViewElementNodeFactory;
import org.thymeleaf.dom.Element;

/**
 * @author Arne Vandamme
 */
public class StaticFormElementNodeBuilder extends NestableNodeBuilderSupport<StaticFormElement>
{
	@Override
	protected Element createNode( StaticFormElement control, ViewElementNodeFactory viewElementNodeFactory ) {
		Element element = new Element( "p" );
		element.setAttribute( "class", "form-control-static" );

		text( element, control.getText() );

		return element;
	}
}
