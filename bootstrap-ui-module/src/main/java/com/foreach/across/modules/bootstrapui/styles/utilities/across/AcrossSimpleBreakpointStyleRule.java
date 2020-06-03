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
import com.foreach.across.modules.bootstrapui.styles.utilities.BreakpointStyleRule;
import com.foreach.across.modules.bootstrapui.styles.utilities.SimpleBreakpointStyleRule;
import com.foreach.across.modules.web.ui.elements.HtmlViewElement;

/**
 * @author Steven Gentens
 * @since 3.0.0
 */
public class AcrossSimpleBreakpointStyleRule implements BreakpointStyleRule
{
	private final BreakpointStyleRule breakpointStyleRule;

	public AcrossSimpleBreakpointStyleRule( String prefix, String suffix, String... additionalCss ) {
		breakpointStyleRule = new SimpleBreakpointStyleRule( prefix, suffix, additionalCss );
	}

	@Override
	public BootstrapStyleRule on( String breakpoint ) {
		return AcrossStyleRule.of( breakpointStyleRule.on( breakpoint ) );
	}

	@Override
	public String[] toCssClasses() {
		return AcrossStyleRule.of( breakpointStyleRule ).toCssClasses();
	}

	@Override
	public void removeFrom( HtmlViewElement target ) {
		target.removeCssClass( toCssClasses() );
	}
}
