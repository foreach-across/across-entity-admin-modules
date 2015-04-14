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

import com.foreach.across.modules.bootstrapui.elements.Style;
import com.foreach.across.modules.web.ui.elements.NodeViewElementSupport;
import com.foreach.across.modules.web.ui.elements.thymeleaf.NestableNodeBuilderSupport;
import org.thymeleaf.dom.Element;

import java.util.Collection;

/**
 * @author Arne Vandamme
 */
public abstract class BootstrapNodeBuilderSupport<T extends NodeViewElementSupport> extends NestableNodeBuilderSupport<T>
{
	protected void style( Element node, Collection<Style> styles ) {
		style( node, "", styles );
	}

	protected void style( Element node, String prefix, Collection<Style> styles ) {
		for ( Style style : styles ) {
			style( node, prefix, style );
		}
	}

	protected void style( Element node, Style style ) {
		style( node, "", style );
	}

	protected void style( Element node, String prefix, Style style ) {
		if ( style != null ) {
			attributeAppend( node, "class", style.forPrefix( prefix ) );
		}
	}
}
