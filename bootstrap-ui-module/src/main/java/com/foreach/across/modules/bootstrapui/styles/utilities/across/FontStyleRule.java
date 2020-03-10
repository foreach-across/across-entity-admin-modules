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

import com.foreach.across.modules.bootstrapui.styles.BootstrapStyleRule;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import static com.foreach.across.modules.bootstrapui.styles.BootstrapStyleRule.of;

/**
 * https://getbootstrap.com/docs/4.3/utilities/text/#font-weight-and-italics
 *
 * @author Arne Vandamme
 * @since 3.0.0
 */
public class FontStyleRule
{
	public final Weight weight = new Weight();
	public final BootstrapStyleRule italic = of( "font-italic" );

	@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
	public static class Weight
	{
		public final BootstrapStyleRule bold = of( "font-weight-bold" );
		public final BootstrapStyleRule bolder = of( "font-weight-bolder" );
		public final BootstrapStyleRule normal = of( "font-weight-normal" );
		public final BootstrapStyleRule light = of( "font-weight-light" );
		public final BootstrapStyleRule lighter = of( "font-weight-lighter" );
	}
}
