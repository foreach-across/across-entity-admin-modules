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

import com.foreach.across.modules.bootstrapui.styles.AcrossStyleRule;
import com.foreach.across.modules.bootstrapui.styles.BootstrapStyleRule;
import com.foreach.across.modules.bootstrapui.styles.BootstrapStyles;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

/**
 * https://getbootstrap.com/docs/4.3/utilities/text/#font-weight-and-italics
 *
 * @author Steven Gentens
 * @since 3.0.0
 */
public class AcrossFontStyleRule
{
	public final Weight weight = new Weight();
	public final BootstrapStyleRule italic = AcrossStyleRule.of( BootstrapStyles.css.font.italic );

	@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
	public static class Weight
	{
		public final BootstrapStyleRule bold = AcrossStyleRule.of( BootstrapStyles.css.font.weight.bold );
		public final BootstrapStyleRule bolder = AcrossStyleRule.of( BootstrapStyles.css.font.weight.bolder );
		public final BootstrapStyleRule normal = AcrossStyleRule.of( BootstrapStyles.css.font.weight.normal );
		public final BootstrapStyleRule light = AcrossStyleRule.of( BootstrapStyles.css.font.weight.light );
		public final BootstrapStyleRule lighter = AcrossStyleRule.of( BootstrapStyles.css.font.weight.lighter );
	}
}
