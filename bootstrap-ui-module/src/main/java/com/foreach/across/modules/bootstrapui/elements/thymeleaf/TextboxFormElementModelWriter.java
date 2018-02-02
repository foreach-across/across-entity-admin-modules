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

import com.foreach.across.modules.bootstrapui.elements.TextboxFormElement;
import com.foreach.across.modules.web.thymeleaf.ThymeleafModelBuilder;

/**
 * @author Arne Vandamme
 * @since 1.0.0
 */
public class TextboxFormElementModelWriter extends FormControlElementModelWriter<TextboxFormElement>
{
	/**
	 * Attribute that holds the error value that should be rendered instead of the default text property.
	 * This attribute will be removed right after the element has been rendered.
	 * Usually set by a parent container, e.g. the {@link FormGroupElementModelWriter}.
	 * <p/>
	 * Given a data-attribute name in case it gets sent to the output anyway.
	 */
	public static String TRANSIENT_ERROR_VALUE_ATTRIBUTE = "data-transient-attr-error-value";

	@Override
	protected void writeOpenElement( TextboxFormElement control, ThymeleafModelBuilder model ) {
		String transientErrorValue = control.getAttribute( TRANSIENT_ERROR_VALUE_ATTRIBUTE, String.class );

		if ( transientErrorValue != null ) {
			control.removeAttribute( TRANSIENT_ERROR_VALUE_ATTRIBUTE );
		}

		super.writeOpenElement( control, model );

		model.addAttribute( "type", control.getType().getName() );
		model.addAttributeValue( "class", "form-control" );
		model.addAttribute( "placeholder", control.getPlaceholder() );
		model.addAttribute( "value", transientErrorValue != null ? transientErrorValue : control.getText() );
		model.addAttribute( "maxlength", control.getMaxLength() );
	}
}
