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

/**
 * @author Arne Vandamme
 * @since 1.0.0
 */
public class RadioFormElementModelWriter extends CheckboxFormElementModelWriter
{
	@Override
	protected void writeOpenElement( CheckboxFormElement control, ThymeleafModelBuilder model ) {
		writeOpenElement( "radio", control, model );
	}

	@Override
	protected void writeCloseElement( CheckboxFormElement control, ThymeleafModelBuilder model ) {
		// radio button does not have hidden value holder
		if ( control.isWrapped() ) {
			model.addCloseElement();
		}
	}

	@Override
	protected String customControlCss() {
		return "custom-radio";
	}
}
