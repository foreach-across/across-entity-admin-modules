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

import com.foreach.across.modules.bootstrapui.elements.FieldsetFormElement;
import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.ViewElementBuilder;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.ViewElementPostProcessor;
import com.foreach.across.modules.web.ui.elements.builder.NodeViewElementSupportBuilder;

import java.util.Map;

/**
 * @author Arne Vandamme
 */
public class FieldsetFormElementBuilder extends NodeViewElementSupportBuilder<FieldsetFormElement, FieldsetFormElementBuilder>
{
	private String formId, fieldsetName;
	private boolean disabled;
	private Legend legendBuilder;

	public FieldsetFormElementBuilder formId( String formId ) {
		this.formId = formId;
		return this;
	}

	public FieldsetFormElementBuilder fieldsetName( String name ) {
		fieldsetName = name;
		return this;
	}

	public FieldsetFormElementBuilder disabled() {
		return disabled( true );
	}

	public FieldsetFormElementBuilder disabled( boolean disabled ) {
		this.disabled = disabled;
		return this;
	}

	public FieldsetFormElementBuilder legend( String legendText ) {
		return legend().text( legendText ).and();
	}

	@Override
	public FieldsetFormElementBuilder htmlId( String htmlId ) {
		return super.htmlId( htmlId );
	}

	@Override
	public FieldsetFormElementBuilder attribute( String name, Object value ) {
		return super.attribute( name, value );
	}

	@Override
	public FieldsetFormElementBuilder attributes( Map<String, Object> attributes ) {
		return super.attributes( attributes );
	}

	@Override
	public FieldsetFormElementBuilder removeAttribute( String name ) {
		return super.removeAttribute( name );
	}

	@Override
	public FieldsetFormElementBuilder clearAttributes() {
		return super.clearAttributes();
	}

	@Override
	public FieldsetFormElementBuilder add( ViewElement... viewElements ) {
		return super.add( viewElements );
	}

	@Override
	public FieldsetFormElementBuilder add( ViewElementBuilder... viewElements ) {
		return super.add( viewElements );
	}

	@Override
	public FieldsetFormElementBuilder addAll( Iterable<?> viewElements ) {
		return super.addAll( viewElements );
	}

	@Override
	public FieldsetFormElementBuilder sort( String... elementNames ) {
		return super.sort( elementNames );
	}

	@Override
	public FieldsetFormElementBuilder name( String name ) {
		return super.name( name );
	}

	@Override
	public FieldsetFormElementBuilder customTemplate( String template ) {
		return super.customTemplate( template );
	}

	@Override
	public FieldsetFormElementBuilder postProcessor( ViewElementPostProcessor<FieldsetFormElement> postProcessor ) {
		return super.postProcessor( postProcessor );
	}

	public Legend legend() {
		if ( legendBuilder == null ) {
			legendBuilder = new Legend( this );
		}
		return legendBuilder;
	}

	@Override
	protected FieldsetFormElement createElement( ViewElementBuilderContext builderContext ) {
		FieldsetFormElement fieldset = apply( new FieldsetFormElement(), builderContext );
		fieldset.setDisabled( disabled );

		if ( fieldsetName != null ) {
			fieldset.setFieldsetName( fieldsetName );
		}
		if ( formId != null ) {
			fieldset.setFormId( formId );
		}

		if ( legendBuilder != null ) {
			fieldset.setLegend( legendBuilder.build( builderContext ) );
		}

		return fieldset;
	}

	public static class Legend extends NodeViewElementSupportBuilder<FieldsetFormElement.Legend, Legend>
	{
		private final FieldsetFormElementBuilder fieldset;

		private String text;

		public Legend( FieldsetFormElementBuilder fieldset ) {
			this.fieldset = fieldset;
		}

		public Legend text( String text ) {
			this.text = text;
			return this;
		}

		@Override
		protected FieldsetFormElement.Legend createElement( ViewElementBuilderContext builderContext ) {
			FieldsetFormElement.Legend legend = new FieldsetFormElement.Legend();

			if ( text != null ) {
				legend.setText( text );
			}

			return apply( legend, builderContext );
		}

		public FieldsetFormElementBuilder and() {
			return fieldset;
		}
	}
}
