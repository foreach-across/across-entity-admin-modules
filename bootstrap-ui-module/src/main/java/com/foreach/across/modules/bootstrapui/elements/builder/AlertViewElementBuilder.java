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

import com.foreach.across.modules.bootstrapui.elements.AlertViewElement;
import com.foreach.across.modules.bootstrapui.elements.Style;
import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.ViewElementBuilder;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.elements.builder.AbstractNodeViewElementBuilder;

/**
 * @author Arne Vandamme
 * @since 1.0.0
 */
public class AlertViewElementBuilder extends AbstractNodeViewElementBuilder<AlertViewElement, AlertViewElementBuilder>
{
	private Object text;
	private String closeLabel;
	private Style style;
	private boolean dismissible = false;

	public AlertViewElementBuilder text( String text ) {
		this.text = text;
		return this;
	}

	/**
	 * Configure a {@link ViewElement} that contains the text to render.
	 * This element will be added as the first child.
	 *
	 * @param textElement that represents the text
	 * @return current builder
	 */
	public AlertViewElementBuilder text( ViewElement textElement ) {
		this.text = textElement;
		return this;
	}

	/**
	 * Configure a builder that should supply the text to be rendered.
	 * The resulting element will be added as the first child if it is not {@code null}.
	 *
	 * @param textBuilder responsible for generating the text
	 * @return current builder
	 */
	public AlertViewElementBuilder text( ViewElementBuilder textBuilder ) {
		text = textBuilder;
		return this;
	}

	public AlertViewElementBuilder closeLabel( String closeLabel ) {
		this.closeLabel = closeLabel;
		return this;
	}

	public AlertViewElementBuilder warning() {
		return style( Style.WARNING );
	}

	public AlertViewElementBuilder danger() {
		return style( Style.DANGER );
	}

	public AlertViewElementBuilder info() {
		return style( Style.INFO );
	}

	public AlertViewElementBuilder success() {
		return style( Style.SUCCESS );
	}

	public AlertViewElementBuilder style( Style style ) {
		this.style = style;
		return this;
	}

	public AlertViewElementBuilder dismissible() {
		return dismissible( true );
	}

	public AlertViewElementBuilder dismissible( boolean dismissible ) {
		this.dismissible = dismissible;
		return this;
	}

	@Override
	protected AlertViewElement createElement( ViewElementBuilderContext viewElementBuilderContext ) {
		AlertViewElement alert = new AlertViewElement();
		alert.setDismissible( dismissible );

		if ( closeLabel != null ) {
			alert.setCloseLabel( closeLabel );
		}

		if ( style != null ) {
			alert.setStyle( style );
		}

		if ( text instanceof String ) {
			alert.setText( (String) text );
		}
		else if ( text instanceof ViewElement ) {
			alert.addFirstChild( (ViewElement) text );
		}
		else if ( text instanceof ViewElementBuilder ) {
			ViewElementBuilder textBuilder = (ViewElementBuilder) text;
			ViewElement textElement = textBuilder.build( viewElementBuilderContext );

			if ( textElement != null ) {
				alert.addFirstChild( textElement );
			}
		}

		return apply( alert, viewElementBuilderContext );
	}
}
