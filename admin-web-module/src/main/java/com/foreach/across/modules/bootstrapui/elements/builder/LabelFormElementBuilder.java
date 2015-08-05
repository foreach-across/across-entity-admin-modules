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
import com.foreach.across.modules.web.ui.ViewElementPostProcessor;
import com.foreach.across.modules.web.ui.elements.TextViewElement;
import com.foreach.across.modules.web.ui.elements.builder.NodeViewElementSupportBuilder;

import java.util.Map;

public class LabelFormElementBuilder extends NodeViewElementSupportBuilder<LabelFormElement, LabelFormElementBuilder>
{
	private Object text;
	private Object target;

	@Override
	public LabelFormElementBuilder htmlId( String htmlId ) {
		return super.htmlId( htmlId );
	}

	@Override
	public LabelFormElementBuilder attribute( String name, Object value ) {
		return super.attribute( name, value );
	}

	@Override
	public LabelFormElementBuilder attributes( Map<String, Object> attributes ) {
		return super.attributes( attributes );
	}

	@Override
	public LabelFormElementBuilder removeAttribute( String name ) {
		return super.removeAttribute( name );
	}

	@Override
	public LabelFormElementBuilder clearAttributes() {
		return super.clearAttributes();
	}

	@Override
	public LabelFormElementBuilder add( ViewElement... viewElements ) {
		return super.add( viewElements );
	}

	@Override
	public LabelFormElementBuilder add( ViewElementBuilder... viewElements ) {
		return super.add( viewElements );
	}

	@Override
	public LabelFormElementBuilder addAll( Iterable<?> viewElements ) {
		return super.addAll( viewElements );
	}

	@Override
	public LabelFormElementBuilder sort( String... elementNames ) {
		return super.sort( elementNames );
	}

	@Override
	public LabelFormElementBuilder name( String name ) {
		return super.name( name );
	}

	@Override
	public LabelFormElementBuilder customTemplate( String template ) {
		return super.customTemplate( template );
	}

	@Override
	public LabelFormElementBuilder postProcessor( ViewElementPostProcessor<LabelFormElement> postProcessor ) {
		return super.postProcessor( postProcessor );
	}

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

					if ( textViewElement.isEscapeXml() && textViewElement.getCustomTemplate() ==null ) {
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
