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

import com.foreach.across.modules.bootstrapui.elements.SelectFormElement;
import com.foreach.across.modules.web.thymeleaf.ThymeleafModelBuilder;
import com.foreach.across.modules.web.ui.elements.thymeleaf.AbstractHtmlViewElementModelWriter;

/**
 * @author Arne Vandamme
 * @since 1.0.0
 */
public class SelectFormElementModelWriter extends FormControlElementModelWriter<SelectFormElement>
{
	public static class OptionBuilder extends AbstractHtmlViewElementModelWriter<SelectFormElement.Option>
	{
		@Override
		protected void writeOpenElement( SelectFormElement.Option option, ThymeleafModelBuilder writer ) {
			super.writeOpenElement( option, writer );

			writer.addBooleanAttribute( "disabled", option.isDisabled() );
			writer.addBooleanAttribute( "selected", option.isSelected() );
			writer.addAttribute( "value", option.getValue() );

			String text = option.getText();

			if ( text != null ) {
				writer.addAttribute( "label", option.getLabel() );
			}
			else {
				text = option.getLabel();
			}

			writer.addText( text );
		}
	}

	public static class OptionGroupBuilder extends AbstractHtmlViewElementModelWriter<SelectFormElement.OptionGroup>
	{
		@Override
		protected void writeOpenElement( SelectFormElement.OptionGroup optionGroup, ThymeleafModelBuilder writer ) {
			super.writeOpenElement( optionGroup, writer );

			writer.addBooleanAttribute( "disabled", optionGroup.isDisabled() );
			writer.addAttribute( "label", optionGroup.getLabel() );
		}
	}

	@Override
	protected void writeOpenElement( SelectFormElement control, ThymeleafModelBuilder model ) {
		super.writeOpenElement( control, model );

		model.addAttributeValue( "class", "form-control" );
		model.addBooleanAttribute( "multiple", control.isMultiple() );
	}

	@Override
	protected void writeCloseElement( SelectFormElement control, ThymeleafModelBuilder model ) {
		super.writeCloseElement( control, model );

		// write hidden value for form post
		if ( control.getControlName() != null && control.isMultiple() ) {
			model.addOpenElement( "input" );
			model.addAttribute( "type", "hidden" );
			model.addAttribute( "name", "_" + control.getControlName() );
			model.addAttribute( "value", "" );

			if ( control.isDisabled() || control.hasAttribute( "disabled" ) ) {
				model.addBooleanAttribute( "disabled", true );
			}

			model.addCloseElement();
		}
	}
}
