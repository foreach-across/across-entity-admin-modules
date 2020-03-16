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
import com.foreach.across.modules.bootstrapui.styles.utilities.BorderStyleRule;

/**
 * @author Steven Gentens
 * @since 3.0.0
 */
public class AcrossBorderStyleRule extends AcrossColorStyleRule implements BootstrapStyleRule
{
	public final BootstrapStyleRule none = AcrossBootstrapStyleRule.of( BootstrapStyles.css.border.none );
	public final AcrossNoBorderStyleRule top = new AcrossNoBorderStyleRule( BootstrapStyles.css.border.top );
	public final AcrossNoBorderStyleRule right = new AcrossNoBorderStyleRule( BootstrapStyles.css.border.right );
	public final AcrossNoBorderStyleRule bottom = new AcrossNoBorderStyleRule( BootstrapStyles.css.border.bottom );
	public final AcrossNoBorderStyleRule left = new AcrossNoBorderStyleRule( BootstrapStyles.css.border.left );

	public AcrossBorderStyleRule() {
		super( "border" );
	}

	@Override
	public String[] toCssClasses() {
		return AcrossBootstrapStyleRule.of( BootstrapStyles.css.border ).toCssClasses();
	}

	public static class AcrossNoBorderStyleRule implements BootstrapStyleRule
	{
		public final BootstrapStyleRule none;

		private final BorderStyleRule.NoBorderStyleRule original;

		private AcrossNoBorderStyleRule( BorderStyleRule.NoBorderStyleRule original ) {
			this.original = original;
			none = AcrossBootstrapStyleRule.of( original.none );
		}

		@Override
		public String[] toCssClasses() {
			return AcrossBootstrapStyleRule.of( original ).toCssClasses();
		}
	}
}
