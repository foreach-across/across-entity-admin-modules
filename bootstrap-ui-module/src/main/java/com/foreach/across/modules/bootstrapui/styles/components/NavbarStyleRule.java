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

package com.foreach.across.modules.bootstrapui.styles.components;

import com.foreach.across.modules.bootstrapui.styles.BootstrapStyleRule;
import com.foreach.across.modules.bootstrapui.styles.utilities.BreakpointStyleRule;
import com.foreach.across.modules.bootstrapui.styles.utilities.SimpleBreakpointStyleRule;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import static com.foreach.across.modules.bootstrapui.styles.BootstrapStyleRule.of;

/**
 * https://getbootstrap.com/docs/4.3/components/navbar/
 *
 * @author Arne Vandamme
 * @since 2.3.0
 */
public class NavbarStyleRule implements BootstrapStyleRule
{
	public final BootstrapStyleRule brand = of( "navbar-brand" );
	public final BootstrapStyleRule nav = of( "navbar-nav" );
	public final BootstrapStyleRule text = of( "navbar-text" );
	public final BootstrapStyleRule collapse = of( "collapse", "navbar-collapse" );
	public final BootstrapStyleRule light = of( "navbar", "navbar-light" );
	public final BootstrapStyleRule dark = of( "navbar", "navbar-dark" );
	public final Toggler toggler = new Toggler();
	public final BreakpointStyleRule expand = new SimpleBreakpointStyleRule( "navbar-expand", null );

	@Override
	public String[] toCssClasses() {
		return new String[] { "navbar" };
	}

	@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
	public static class Toggler implements BootstrapStyleRule
	{
		public final BootstrapStyleRule icon = of( "navbar-toggler-icon" );

		@Override
		public String[] toCssClasses() {
			return new String[] { "navbar-toggler" };
		}
	}
}
