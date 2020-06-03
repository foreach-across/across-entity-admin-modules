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
import com.foreach.across.modules.bootstrapui.styles.utilities.BreakpointStyleRule;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * https://getbootstrap.com/docs/4.3/utilities/text/
 *
 * @author Steven Gentens
 * @since 3.0.0
 */
public class AcrossTextStyleRule extends AcrossColorStyleRule
{
	public final BootstrapStyleRule body = AcrossStyleRule.of( BootstrapStyles.css.text.body );
	public final BootstrapStyleRule muted = AcrossStyleRule.of( BootstrapStyles.css.text.muted );
	public final BootstrapStyleRule justify = AcrossStyleRule.of( BootstrapStyles.css.text.justify );
	public final BreakpointStyleRule right = new AcrossSimpleBreakpointStyleRule( "text", "right" );
	public final BreakpointStyleRule left = new AcrossSimpleBreakpointStyleRule( "text", "left" );
	public final BreakpointStyleRule center = new AcrossSimpleBreakpointStyleRule( "text", "center" );
	public final Wrap wrap = new Wrap();
	public final BootstrapStyleRule nowrap = wrap.none;
	public final BootstrapStyleRule truncate = AcrossStyleRule.of( BootstrapStyles.css.text.truncate );
	public final BootstrapStyleRule breakWord = AcrossStyleRule.of( BootstrapStyles.css.text.breakWord );
	public final BootstrapStyleRule lowerCase = AcrossStyleRule.of( BootstrapStyles.css.text.lowerCase );
	public final BootstrapStyleRule upperCase = AcrossStyleRule.of( BootstrapStyles.css.text.upperCase );
	public final BootstrapStyleRule capitalize = AcrossStyleRule.of( BootstrapStyles.css.text.capitalize );
	public final BootstrapStyleRule monospace = AcrossStyleRule.of( BootstrapStyles.css.text.monospace );
	public final BootstrapStyleRule reset = AcrossStyleRule.of( BootstrapStyles.css.text.reset );
	public final BootstrapStyleRule noDecoration = AcrossStyleRule.of( BootstrapStyles.css.text.noDecoration );

	public AcrossTextStyleRule() {
		super( "text" );
	}

	public BootstrapStyleRule body() {
		return AcrossStyleRule.of( BootstrapStyles.css.text.body );
	}

	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class Wrap implements BootstrapStyleRule
	{
		public final BootstrapStyleRule none = AcrossStyleRule.of( BootstrapStyles.css.text.wrap.none );

		@Override
		public String[] toCssClasses() {
			return AcrossStyleRule.of( BootstrapStyles.css.text.wrap ).toCssClasses();
		}
	}
}
