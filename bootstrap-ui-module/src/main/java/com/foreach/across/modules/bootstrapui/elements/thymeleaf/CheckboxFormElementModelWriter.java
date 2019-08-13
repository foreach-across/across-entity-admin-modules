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

import com.foreach.across.modules.bootstrapui.elements.CheckboxFormElement;
import com.foreach.across.modules.web.thymeleaf.ThymeleafModelBuilder;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Arne Vandamme
 * @since 1.0.0
 */
public class CheckboxFormElementModelWriter extends FormControlElementModelWriter<CheckboxFormElement>
{
	@Override
	protected void writeOpenElement( CheckboxFormElement control, ThymeleafModelBuilder model ) {
		writeOpenElement( "checkbox", control, model );
	}

	protected void writeOpenElement( String type, CheckboxFormElement control, ThymeleafModelBuilder model ) {
		String text = control.getText();
		boolean showLabel = StringUtils.isNotEmpty( text ) || control.hasChildren();

		// add wrapper div
		if ( control.isWrapped() ) {
			model.addOpenElement( "div" );
			control.getAttributes().forEach( model::addAttribute );
			model.addAttributeValue( "class", "form-check" );
		}

		writeCheckboxInputWithoutAttributes( control, model );

		if ( !control.isWrapped() && !showLabel ) {
			control.getAttributes().forEach( model::addAttribute );
		}

		model.addAttribute( "type", type );
		model.addAttribute( "value", control.getValue() );
		model.addBooleanAttribute( "checked", control.isChecked() );

		if ( control.isWrapped() ) {
			model.addAttributeValue( "class", "form-check-input" );
			if ( !showLabel ) {
				model.addAttributeValue( "class", "position-static" );
			}
		}
		else {
			control.getAttributes().forEach( model::addAttribute );
		}

		// add label div
		if ( showLabel ) {
			model.addCloseElement();
			model.addOpenElement( "label" );
			if ( control.isWrapped() ) {
				model.addAttribute( "class", "form-check-label" );
			}

			model.addAttribute( "for", model.retrieveHtmlId( control ) );

			// add label text
			model.addHtml( control.getText() );
		}
	}

	private void writeCheckboxInputWithoutAttributes( CheckboxFormElement control, ThymeleafModelBuilder model ) {
		model.addOpenElement( control.getTagName() );
		model.addAttribute( "id", model.retrieveHtmlId( control ) );
		model.addAttribute( "name", control.getControlName() );
		model.addBooleanAttribute( "disabled", control.isDisabled() );
		model.addBooleanAttribute( "readonly", control.isReadonly() );
		model.addBooleanAttribute( "required", control.isRequired() );
	}

	@Override
	protected void writeChildren( CheckboxFormElement control, ThymeleafModelBuilder model ) {
		// never add children inside the checkbox input
		model.addCloseElement();

		// add children after checkbox tag and text
		super.writeChildren( control, model );
	}

	@Override
	protected void writeCloseElement( CheckboxFormElement control, ThymeleafModelBuilder model ) {
		// close label if there is one
//		if ( control.getText() != null || control.hasChildren() ) {
//			model.addCloseElement();
//		}

		// write hidden value for form post
		if ( control.getControlName() != null ) {
			model.addOpenElement( "input" );
			model.addAttribute( "type", "hidden" );
			model.addAttribute( "name", "_" + control.getControlName() );
			model.addAttribute( "value", "on" );

			if ( control.isDisabled() || control.hasAttribute( "disabled" ) ) {
				model.addBooleanAttribute( "disabled", true );
			}

			model.addCloseElement();
		}

		// close wrapper
		if ( control.isWrapped() ) {
			model.addCloseElement();
		}
	}
}
