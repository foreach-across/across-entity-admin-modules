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

package com.foreach.across.modules.bootstrapui.styles.utilities.across;

import com.foreach.across.modules.bootstrapui.styles.AcrossBootstrapStyleRule;
import com.foreach.across.modules.bootstrapui.styles.BootstrapStyleRule;
import com.foreach.across.modules.bootstrapui.styles.BootstrapStyles;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

/**
 * https://getbootstrap.com/docs/4.3/utilities/flex/#align-items
 * https://getbootstrap.com/docs/4.3/utilities/flex/#align-self
 * https://getbootstrap.com/docs/4.3/utilities/flex/#align-content
 * https://getbootstrap.com/docs/4.3/utilities/vertical-align/
 *
 * @author Arne Vandamme
 * @since 3.0.0
 */
public class AcrossAlignStyleRule
{
	public final BootstrapStyleRule baseline = AcrossBootstrapStyleRule.of( BootstrapStyles.css.align.baseline );
	public final BootstrapStyleRule top = AcrossBootstrapStyleRule.of( BootstrapStyles.css.align.top );
	public final BootstrapStyleRule middle = AcrossBootstrapStyleRule.of( BootstrapStyles.css.align.middle );
	public final BootstrapStyleRule bottom = AcrossBootstrapStyleRule.of( BootstrapStyles.css.align.bottom );
	public final Text text = new Text();

	@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
	public static class Text
	{
		public final BootstrapStyleRule top = AcrossBootstrapStyleRule.of( BootstrapStyles.css.align.text.top );
		public final BootstrapStyleRule bottom = AcrossBootstrapStyleRule.of( BootstrapStyles.css.align.text.bottom );
	}
}
