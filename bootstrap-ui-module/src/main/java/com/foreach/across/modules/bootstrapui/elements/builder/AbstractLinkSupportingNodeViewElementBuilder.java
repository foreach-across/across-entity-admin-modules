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

package com.foreach.across.modules.bootstrapui.elements.builder;

import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.elements.AbstractNodeViewElement;
import com.foreach.across.modules.web.ui.elements.builder.AbstractNodeViewElementBuilder;

import java.util.function.Function;

/**
 * Base class for elements that have url properties that allow using a custom link builder function.
 * Note that any link supporting builder also implements {@link AbstractHtmlSupportingNodeViewElementBuilder}
 * and supports configuration of HTML escaping behaviour.
 *
 * @author Arne Vandamme
 * @see AbstractHtmlSupportingNodeViewElementBuilder
 * @since 1.0.0
 */
public abstract class AbstractLinkSupportingNodeViewElementBuilder<T extends AbstractNodeViewElement, SELF extends AbstractNodeViewElementBuilder<T, SELF>>
		extends AbstractHtmlSupportingNodeViewElementBuilder<T, SELF>
{
	private Function<String, String> linkBuilder;

	/**
	 * Set a conversion function that should be applied to all url type properties
	 * when setting them as the attribute for the generated links.
	 * <p/>
	 * If not set, this builder will dispatch to {@link ViewElementBuilderContext#buildLink(String)}.
	 * You can suppress the default behaviour by setting this property to {@link Function#identity()}.
	 *
	 * @param linkBuilder to use for translating the urls
	 * @return current builder
	 */
	@SuppressWarnings("unchecked")
	public SELF linkBuilder( Function<String, String> linkBuilder ) {
		this.linkBuilder = linkBuilder;
		return (SELF) this;
	}

	protected String buildLink( String link, ViewElementBuilderContext builderContext ) {
		if ( linkBuilder != null ) {
			return linkBuilder.apply( link );
		}

		return builderContext.buildLink( link );
	}
}
