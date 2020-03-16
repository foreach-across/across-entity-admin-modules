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

/**
 * @author Steven Gentens
 * @since 3.0.0
 */
public class AcrossRoundedStyleRule implements BootstrapStyleRule
{
	public final BootstrapStyleRule top = AcrossBootstrapStyleRule.of( BootstrapStyles.css.rounded.top );
	public final BootstrapStyleRule right = AcrossBootstrapStyleRule.of( BootstrapStyles.css.rounded.right );
	public final BootstrapStyleRule bottom = AcrossBootstrapStyleRule.of( BootstrapStyles.css.rounded.bottom );
	public final BootstrapStyleRule left = AcrossBootstrapStyleRule.of( BootstrapStyles.css.rounded.left );
	public final BootstrapStyleRule circle = AcrossBootstrapStyleRule.of( BootstrapStyles.css.rounded.circle );
	public final BootstrapStyleRule pill = AcrossBootstrapStyleRule.of( BootstrapStyles.css.rounded.pill );
	public final BootstrapStyleRule none = AcrossBootstrapStyleRule.of( BootstrapStyles.css.rounded.none );
	public final BootstrapStyleRule small = AcrossBootstrapStyleRule.of( BootstrapStyles.css.rounded.small );
	public final BootstrapStyleRule large = AcrossBootstrapStyleRule.of( BootstrapStyles.css.rounded.large );

	@Override
	public String[] toCssClasses() {
		return AcrossBootstrapStyleRule.of( BootstrapStyles.css.rounded ).toCssClasses();
	}
}
