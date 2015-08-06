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
import com.foreach.across.modules.web.thymeleaf.ViewElementNodeFactory;
import com.foreach.across.modules.web.ui.elements.thymeleaf.HtmlViewElementThymeleafSupport;
import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Element;

/**
 * @author Arne Vandamme
 */
public class SelectFormElementThymeleafBuilder extends FormControlElementBuilderSupport<SelectFormElement>
{
	public static class OptionBuilder extends HtmlViewElementThymeleafSupport<SelectFormElement.Option>
	{
		@Override
		protected Element createNode( SelectFormElement.Option control,
		                              Arguments arguments,
		                              ViewElementNodeFactory viewElementNodeFactory ) {
			Element element = new Element( "option" );

			attribute( element, "disabled", control.isDisabled() );
			attribute( element, "selected", control.isSelected() );

			String text = control.getText();

			if ( text != null ) {
				attribute( element, "label", control.getLabel() );
			}
			else {
				text = control.getLabel();
			}

			attribute( element, "value", control.getValue(), viewElementNodeFactory );

			text( element, text );

			return element;
		}
	}

	public static class OptionGroupBuilder extends HtmlViewElementThymeleafSupport<SelectFormElement.OptionGroup>
	{
		@Override
		protected Element createNode( SelectFormElement.OptionGroup control,
		                              Arguments arguments,
		                              ViewElementNodeFactory viewElementNodeFactory ) {
			Element element = new Element( "optgroup" );

			attribute( element, "disabled", control.isDisabled() );
			attribute( element, "label", control.getLabel() );

			return element;
		}
	}

	@Override
	protected Element createNode( SelectFormElement control,
	                              Arguments arguments,
	                              ViewElementNodeFactory viewElementNodeFactory ) {
		Element element = new Element( "select" );
		element.setAttribute( "class", "form-control" );

		attribute( element, "multiple", control.isMultiple() );

		return element;
	}
}
