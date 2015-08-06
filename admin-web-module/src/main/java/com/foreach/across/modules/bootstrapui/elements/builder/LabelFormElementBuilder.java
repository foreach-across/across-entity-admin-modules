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

import com.foreach.across.modules.bootstrapui.elements.LabelFormElement;
import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.ViewElementBuilder;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.elements.TextViewElement;
import com.foreach.across.modules.web.ui.elements.builder.AbstractNodeViewElementBuilder;

public class LabelFormElementBuilder extends AbstractNodeViewElementBuilder<LabelFormElement, LabelFormElementBuilder>
{
	private Object text;
	private Object target;

	public LabelFormElementBuilder text( String text ) {
		this.text = text;
		return this;
	}

	/**
	 * Configure a builder that should supply the text to be rendered.  If the builder produces a single
	 * {@link TextViewElement} with plain text, it will not be added as a child of the label, but used as
	 * the text property.  In all other cases the element will be the first child of the label.
	 *
	 * @param textBuilder responsible for generating the text
	 * @return current builder
	 */
	public LabelFormElementBuilder text( ViewElementBuilder textBuilder ) {
		text = textBuilder;
		return this;
	}

	public LabelFormElementBuilder target( String htmlId ) {
		this.target = htmlId;
		return this;
	}

	public LabelFormElementBuilder target( ViewElement element ) {
		this.target = element;
		return this;
	}

	public LabelFormElementBuilder target( ViewElementBuilder elementBuilder ) {
		this.target = elementBuilder;
		return this;
	}

	@Override
	protected LabelFormElement createElement( ViewElementBuilderContext viewElementBuilderContext ) {
		LabelFormElement label = new LabelFormElement();

		if ( text instanceof String ) {
			label.setText( (String) text );
		}
		else if ( text instanceof ViewElementBuilder ) {
			ViewElementBuilder textBuilder = (ViewElementBuilder) text;
			ViewElement textElement = textBuilder.build( viewElementBuilderContext );

			if ( textElement != null ) {
				boolean addAsChild = true;

				if ( textElement instanceof TextViewElement ) {
					TextViewElement textViewElement = (TextViewElement) textElement;

					if ( textViewElement.isEscapeXml() && textViewElement.getCustomTemplate() == null ) {
						label.setText( textViewElement.getText() );
						addAsChild = false;
					}
				}

				if ( addAsChild ) {
					label.addFirst( textElement );
				}
			}
		}

		if ( target != null ) {
			if ( target instanceof String ) {
				label.setTarget( (String) target );
			}
			else if ( target instanceof ViewElement ) {
				label.setTarget( (ViewElement) target );
			}
			else {
				label.setTarget( ( (ViewElementBuilder) target ).build( viewElementBuilderContext ) );
			}
		}

		return apply( label, viewElementBuilderContext );
	}
}
