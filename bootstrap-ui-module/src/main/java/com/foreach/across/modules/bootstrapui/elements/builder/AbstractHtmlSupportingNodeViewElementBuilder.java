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
import com.foreach.across.modules.web.ui.elements.TextViewElement;
import com.foreach.across.modules.web.ui.elements.builder.AbstractNodeViewElementBuilder;

/**
 * Base class for elements that have one or more text properties that could - optionally - be html escaped.
 *
 * @author Arne Vandamme
 * @since 2.0.0
 */
public abstract class AbstractHtmlSupportingNodeViewElementBuilder<T extends AbstractNodeViewElement, SELF extends AbstractNodeViewElementBuilder<T, SELF>>
		extends AbstractNodeViewElementBuilder<T, SELF>
{
	private boolean escapeHtml = true;

	/**
	 * Should text properties that this builder supports have HTML escaped?
	 * By default HTML is escaped.
	 *
	 * @param escapeHtml true if should be escaped
	 * @return current builder
	 */
	@SuppressWarnings("unchecked")
	public SELF escapeHtml( boolean escapeHtml ) {
		this.escapeHtml = escapeHtml;
		return (SELF) this;
	}

	protected boolean isEscapeHtml() {
		return escapeHtml;
	}

	/**
	 * Create a {@link TextViewElement} for a text string.
	 * Will first resolve the text using the {@link com.foreach.across.modules.web.support.LocalizedTextResolver}
	 * and will escape HTML depending on the value set using {@link #escapeHtml(boolean)}.
	 *
	 * @param text           to create an element for
	 * @param builderContext being used
	 * @return new text element
	 */
	protected TextViewElement resolveTextElement( String text, ViewElementBuilderContext builderContext ) {
		return toTextElement( builderContext.resolveText( text ) );
	}

	/**
	 * Create a {@link TextViewElement} for a text string.
	 * Will escape HTML depending on the value set using {@link #escapeHtml(boolean)}.
	 *
	 * @param text to create an element for
	 * @return new text element
	 */
	protected TextViewElement toTextElement( String text ) {
		return new TextViewElement( text, isEscapeHtml() );
	}
}
