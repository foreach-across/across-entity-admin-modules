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

package com.foreach.across.modules.bootstrapui.elements.tooltip;

import com.foreach.across.modules.bootstrapui.elements.builder.AbstractHtmlSupportingNodeViewElementBuilder;
import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;

import static com.foreach.across.modules.bootstrapui.attributes.BootstrapAttributes.attribute;
import static com.foreach.across.modules.bootstrapui.styles.BootstrapStyles.css;
import static com.foreach.across.modules.web.ui.elements.HtmlViewElements.i;

/**
 * Responsible for creating a {@link TooltipViewElement} instance.
 * A default tooltip instance supports HTML markup in its text.
 *
 * @author Arne Vandamme
 * @since 2.0.0
 */
@Accessors(fluent = true)
public class TooltipViewElementBuilder extends AbstractHtmlSupportingNodeViewElementBuilder<TooltipViewElement, TooltipViewElementBuilder>
{
	/**
	 * -- SETTER --
	 * Set the tooltip text.
	 */
	@Setter
	private String text;

	/**
	 * -- SETTER --
	 * Set the icon that should be used.
	 * Defaults to a question mark.
	 */
	@Setter
	// todo use icon set
	private ViewElement icon = i( css.fa.solid( "question-circle" ), attribute.aria.hidden );

	/**
	 * -- SETTER --
	 * Set a custom tag for the tooltip element.
	 */
	@NonNull
	@Setter
	private String tagName;

	public TooltipViewElementBuilder() {
		escapeHtml( false );
	}

	@Override
	protected TooltipViewElement createElement( ViewElementBuilderContext builderContext ) {
		TooltipViewElement tooltip = new TooltipViewElement();
		tooltip.setEscapeHtml( isEscapeHtml() );
		tooltip.setIcon( icon );

		if ( tagName != null ) {
			tooltip.setTagName( tagName );
		}

		if ( text != null ) {
			tooltip.setText( builderContext.resolveText( text ) );
		}

		return apply( tooltip, builderContext );
	}
}
