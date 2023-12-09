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

package com.foreach.across.modules.entity.views.bootstrapui.processors.element;

import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.ViewElementPostProcessor;
import com.foreach.across.modules.web.ui.elements.ConfigurableTextViewElement;
import org.apache.commons.lang3.StringUtils;

/**
 * Takes a {@link ConfigurableTextViewElement} and updates its {@link ConfigurableTextViewElement#getText()}
 * value after passing the current value through the {@link com.foreach.across.modules.web.support.LocalizedTextResolver}.
 *
 * @author Arne Vandamme
 * @since 2.2.0
 */
public class LocalizedTextPostProcessor implements ViewElementPostProcessor<ConfigurableTextViewElement>
{
	public static final LocalizedTextPostProcessor INSTANCE = new LocalizedTextPostProcessor();

	@Override
	public void postProcess( ViewElementBuilderContext builderContext, ConfigurableTextViewElement t ) {
		String text = t.getText();

		if ( !StringUtils.isEmpty( text ) ) {
			t.setText( builderContext.resolveText( text ) );
		}
	}
}
