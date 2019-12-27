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
import com.foreach.across.modules.bootstrapui.styles.utilities.ColorStyleRule;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import static com.foreach.across.modules.bootstrapui.styles.BootstrapStyleRule.of;

/**
 * https://getbootstrap.com/docs/4.3/components/buttons/
 *
 * @author Arne Vandamme
 * @since 3.0.0
 */
public class ButtonStyleRule extends ColorStyleRule implements BootstrapStyleRule
{
	public final BootstrapStyleRule link = of( "btn", "btn-link" );
	public final ColorStyleRule outline = new ColorStyleRule( "btn-outline", "btn" );
	public final BootstrapStyleRule small = of( "btn-sm" );
	public final BootstrapStyleRule large = of( "btn-lg" );
	public final BootstrapStyleRule block = of( "btn-block" );
	public final Group group = new Group();
	public final BootstrapStyleRule toolbar = of( "btn-toolbar" );

	public ButtonStyleRule() {
		super( "btn", "btn" );
	}

	@Override
	public String[] toCssClasses() {
		return new String[] { "btn" };
	}

	/**
	 * https://getbootstrap.com/docs/4.3/components/button-group/
	 */
	@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
	public static class Group implements BootstrapStyleRule
	{
		public final BootstrapStyleRule toggle = of( "btn-group-toggle" );
		public final BootstrapStyleRule small = of( "btn-group-sm" );
		public final BootstrapStyleRule large = of( "btn-group-lg" );
		public final BootstrapStyleRule vertical = of( "btn-group-vertical" );

		@Override
		public String[] toCssClasses() {
			return new String[] { "btn-group" };
		}
	}
}
