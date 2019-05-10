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

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import static com.foreach.across.modules.bootstrapui.styles.BootstrapStyleRule.of;

/**
 * https://getbootstrap.com/docs/4.3/utilities/text/
 *
 * @author Arne Vandamme
 * @since 2.3.0
 */
@SuppressWarnings("WeakerAccess")
public class TextStyleRule extends ColorStyleRule
{
	public final BootstrapStyleRule body = of( "text-body" );
	public final BootstrapStyleRule muted = of( "text-muted" );
	public final BootstrapStyleRule justify = of( "text-justify" );
	public final BreakpointStyleRule right = new SimpleBreakpointStyleRule( "text", "right" );
	public final BreakpointStyleRule left = new SimpleBreakpointStyleRule( "text", "left" );
	public final BreakpointStyleRule center = new SimpleBreakpointStyleRule( "text", "center" );
	public final Wrap wrap = new Wrap();
	public final BootstrapStyleRule nowrap = wrap.none;
	public final BootstrapStyleRule truncate = of( "text-truncate" );
	public final BootstrapStyleRule breakWord = of( "text-break" );
	public final BootstrapStyleRule lowerCase = of( "text-lowercase" );
	public final BootstrapStyleRule upperCase = of( "text-uppercase" );
	public final BootstrapStyleRule capitalize = of( "text-capitalize" );
	public final BootstrapStyleRule monospace = of( "text-monospace" );
	public final BootstrapStyleRule reset = of( "text-reset" );
	public final BootstrapStyleRule noDecoration = of( "text-decoration-none" );

	public TextStyleRule() {
		super( "text" );
	}

	public BootstrapStyleRule body() {
		return of( "text-body" );
	}

	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class Wrap implements BootstrapStyleRule
	{
		public final BootstrapStyleRule none = BootstrapStyleRule.of( "text-nowrap" );

		@Override
		public String[] toCssClasses() {
			return new String[] { "text-wrap" };
		}
	}
}
