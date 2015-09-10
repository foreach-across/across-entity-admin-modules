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
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.elements.builder.AbstractNodeViewElementBuilder;

/**
 * @author Arne Vandamme
 */
public class FieldsetFormElementBuilder extends AbstractNodeViewElementBuilder<FieldsetFormElement, FieldsetFormElementBuilder>
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

	public static class Legend extends AbstractNodeViewElementBuilder<FieldsetFormElement.Legend, Legend>
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
