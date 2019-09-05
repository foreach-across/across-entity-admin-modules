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

package com.foreach.across.modules.bootstrapui.styles.utilities;

import com.foreach.across.modules.bootstrapui.styles.BootstrapStyleRule;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

/**
 * @author Arne Vandamme
 * @since 2.3.0
 */
public class BorderStyleRule extends ColorStyleRule implements BootstrapStyleRule
{
	public final BootstrapStyleRule none = BootstrapStyleRule.of( "border-0" );
	public final NoBorderStyleRule top = new NoBorderStyleRule( "border-top" );
	public final NoBorderStyleRule right = new NoBorderStyleRule( "border-right" );
	public final NoBorderStyleRule bottom = new NoBorderStyleRule( "border-bottom" );
	public final NoBorderStyleRule left = new NoBorderStyleRule( "border-left" );

	public BorderStyleRule() {
		super( "border" );
	}

	@Override
	public String[] toCssClasses() {
		return new String[] { "border" };
	}

	@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
	public static class NoBorderStyleRule implements BootstrapStyleRule
	{
		public final BootstrapStyleRule none;

		private final String prefix;

		private NoBorderStyleRule( String prefix ) {
			this.prefix = prefix;
			none = BootstrapStyleRule.of( prefix + "-0" );
		}

		@Override
		public String[] toCssClasses() {
			return new String[] { prefix };
		}
	}
}
