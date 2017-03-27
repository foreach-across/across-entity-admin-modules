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

import com.foreach.across.modules.bootstrapui.elements.ButtonViewElement;
import com.foreach.across.modules.web.thymeleaf.ThymeleafModelBuilder;
import com.foreach.across.modules.web.ui.elements.thymeleaf.AbstractHtmlViewElementModelWriter;
import org.apache.commons.lang3.StringUtils;

import java.util.EnumSet;

import static com.foreach.across.modules.bootstrapui.elements.ButtonViewElement.Type.*;
import static com.foreach.across.modules.bootstrapui.elements.thymeleaf.BootstrapModelWriterUtils.addSizeForPrefix;
import static com.foreach.across.modules.bootstrapui.elements.thymeleaf.BootstrapModelWriterUtils.addStyleForPrefix;

/**
 * @author Arne Vandamme
 * @since 1.0.0
 */
public class ButtonViewElementModelWriter extends AbstractHtmlViewElementModelWriter<ButtonViewElement>
{
	@Override
	protected void writeOpenElement( ButtonViewElement button, ThymeleafModelBuilder writer ) {
		super.writeOpenElement( button, writer );

		changeButtonTypeElement( button, writer );
		addButtonTypeAttributes( button, writer );

		writer.addAttribute( "title", button.getTitle() );

		addButtonStyleAttributes( button, writer );
		addButtonStateAttributes( button, writer );
		addButtonUrl( button, writer );

		// write text attribute if necessary
		String text = button.getText();

		if ( button.getType() != ButtonViewElement.Type.LINK ) {
			writer.addAttribute( "name", button.getControlName() );
		}

		addButtonValue( button, writer );

		// add left-hand side icon
		if ( button.getIcon() != null ) {
			writer.addViewElement( button.getIcon() );
		}

		// add text as child
		if ( !EnumSet.of( INPUT, INPUT_RESET, INPUT_SUBMIT ).contains( button.getType() ) ) {
			writer.addHtml( text );
		}
	}

	private void addButtonValue( ButtonViewElement button, ThymeleafModelBuilder writer ) {
		switch ( button.getType() ) {
			case INPUT:
			case INPUT_RESET:
			case INPUT_SUBMIT:
				writer.addAttribute( "value", StringUtils.defaultString( button.getValue(), button.getText() ) );
				break;
			case LINK:
				writer.addAttribute( "data-value", button.getValue() );
				break;
			default:
				writer.addAttribute( "value", button.getValue() );
		}
	}

	private void addButtonUrl( ButtonViewElement button, ThymeleafModelBuilder writer ) {
		if ( button.getType() == ButtonViewElement.Type.LINK ) {
			writer.addAttribute( "href", button.getUrl() );
		}
		else if ( !StringUtils.equals( "#", button.getUrl() ) ) {
			writer.addAttribute( "data-url", button.getUrl() );
		}
	}

	private void addButtonStateAttributes( ButtonViewElement button, ThymeleafModelBuilder writer ) {
		if ( button.getState() == ButtonViewElement.State.ACTIVE ) {
			writer.addAttributeValue( "class", "active" );
		}
		else if ( button.getState() == ButtonViewElement.State.DISABLED ) {
			if ( button.getType() == ButtonViewElement.Type.LINK ) {
				writer.addAttributeValue( "class", "disabled" );
			}
			else {
				writer.addBooleanAttribute( "disabled", true );
			}
		}
	}

	private void addButtonStyleAttributes( ButtonViewElement button, ThymeleafModelBuilder writer ) {
		writer.addAttributeValue( "class", "btn" );
		addStyleForPrefix( writer, button.getStyle(), "btn" );
		addSizeForPrefix( writer, button.getSize(), "btn" );
	}

	private void changeButtonTypeElement( ButtonViewElement button, ThymeleafModelBuilder writer ) {
		switch ( button.getType() ) {
			case INPUT:
			case INPUT_RESET:
			case INPUT_SUBMIT:
				writer.changeOpenElement( "input" );
				break;
			case LINK:
				writer.changeOpenElement( "a" );
				break;
		}
	}

	private void addButtonTypeAttributes( ButtonViewElement button, ThymeleafModelBuilder writer ) {
		switch ( button.getType() ) {
			case BUTTON_RESET:
			case INPUT_RESET:
				writer.addAttribute( "type", "reset" );
				break;
			case BUTTON_SUBMIT:
			case INPUT_SUBMIT:
				writer.addAttribute( "type", "submit" );
				break;
			case BUTTON:
			case INPUT:
				writer.addAttribute( "type", "button" );
				break;
			case LINK:
				writer.addAttribute( "role", "button" );
				break;
		}
	}
}
