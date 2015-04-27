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

import com.foreach.across.modules.bootstrapui.elements.IconViewElement;
import com.foreach.across.modules.web.thymeleaf.ViewElementNodeFactory;
import com.foreach.across.modules.web.ui.thymeleaf.ViewElementNodeBuilder;
import org.apache.commons.lang3.StringUtils;
import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Element;
import org.thymeleaf.dom.Node;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author Arne Vandamme
 */
public class IconViewElementNodeBuilder implements ViewElementNodeBuilder<IconViewElement>
{
	@Override
	public List<Node> buildNodes( IconViewElement viewElement,
	                              Arguments arguments,
	                              ViewElementNodeFactory viewElementNodeFactory ) {
		Element node = new Element( viewElement.getTagName() );

		viewElementNodeFactory.setAttributes( node, viewElement.getAttributes() );

		if ( viewElement.getIconCss() != null ) {
			node.setAttribute( "class",
			                   StringUtils.trim(
					                   StringUtils.join( new String[] {
							                   viewElement.getIconCss(),
							                   Objects.toString( viewElement.getAttribute( "class" ), "" )
					                   }, " " )
			                   ) );
		}

		return Collections.singletonList( (Node) node );
	}
}
