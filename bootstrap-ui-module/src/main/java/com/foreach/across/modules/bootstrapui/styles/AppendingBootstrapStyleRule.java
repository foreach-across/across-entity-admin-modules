/*
 * Copyright 2019 the original author or authors
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

package com.foreach.across.modules.bootstrapui.styles;

import com.foreach.across.modules.web.ui.elements.HtmlViewElement;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ArrayUtils;

/**
 * Specialization of {@link BootstrapStyleRule} which extends an existing rule but has different behaviour
 * when it comes to removing. When adding all css classes will be added, but when removing only the additional
 * css classes will be removed. This allows for adding dependent classes for convenience, but leaving them
 * untouched when removing (in case they had already been added).
 *
 * @author Arne Vandamme
 * @since 3.0.0
 */
@RequiredArgsConstructor
class AppendingBootstrapStyleRule implements BootstrapStyleRule
{
	private final BootstrapStyleRule originalRule;
	private final String[] additionalCssClasses;

	@Override
	public String[] toCssClasses() {
		return ArrayUtils.addAll( originalRule.toCssClasses(), additionalCssClasses );
	}

	@Override
	public void removeFrom( HtmlViewElement target ) {
		target.removeCssClass( additionalCssClasses );
	}
}
