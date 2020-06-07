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
import lombok.RequiredArgsConstructor;

/**
 * https://getbootstrap.com/docs/4.3/utilities/flex/#align-items
 * https://getbootstrap.com/docs/4.3/utilities/flex/#align-self
 * https://getbootstrap.com/docs/4.3/utilities/flex/#align-content
 * https://getbootstrap.com/docs/4.3/utilities/vertical-align/
 *
 * @author Steven Gentens
 * @since 3.0.0
 */
public class AcrossAlignStyleRule
{
	public final Items items = new Items();
	public final Self self = new Self();
	public final Content content = new Content();
	public final BootstrapStyleRule baseline = AcrossStyleRule.of( BootstrapStyles.css.align.baseline );
	public final BootstrapStyleRule top = AcrossStyleRule.of( BootstrapStyles.css.align.top );
	public final BootstrapStyleRule middle = AcrossStyleRule.of( BootstrapStyles.css.align.middle );
	public final BootstrapStyleRule bottom = AcrossStyleRule.of( BootstrapStyles.css.align.bottom );
	public final Text text = new Text();

	@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
	public static class Items
	{
		public final BreakpointStyleRule start = new AcrossSimpleBreakpointStyleRule( "align-items", "start" );
		public final BreakpointStyleRule end = new AcrossSimpleBreakpointStyleRule( "align-items", "end" );
		public final BreakpointStyleRule center = new AcrossSimpleBreakpointStyleRule( "align-items", "center" );
		public final BreakpointStyleRule baseline = new AcrossSimpleBreakpointStyleRule( "align-items", "baseline" );
		public final BreakpointStyleRule stretch = new AcrossSimpleBreakpointStyleRule( "align-items", "stretch" );
	}

	@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
	public static class Self
	{
		public final BreakpointStyleRule start = new AcrossSimpleBreakpointStyleRule( "align-self", "start" );
		public final BreakpointStyleRule end = new AcrossSimpleBreakpointStyleRule( "align-self", "end" );
		public final BreakpointStyleRule center = new AcrossSimpleBreakpointStyleRule( "align-self", "center" );
		public final BreakpointStyleRule baseline = new AcrossSimpleBreakpointStyleRule( "align-self", "baseline" );
		public final BreakpointStyleRule stretch = new AcrossSimpleBreakpointStyleRule( "align-self", "stretch" );
	}

	@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
	public static class Content
	{
		public final BreakpointStyleRule start = new AcrossSimpleBreakpointStyleRule( "align-content", "start" );
		public final BreakpointStyleRule end = new AcrossSimpleBreakpointStyleRule( "align-content", "end" );
		public final BreakpointStyleRule center = new AcrossSimpleBreakpointStyleRule( "align-content", "center" );
		public final BreakpointStyleRule between = new AcrossSimpleBreakpointStyleRule( "align-content", "between" );
		public final BreakpointStyleRule around = new AcrossSimpleBreakpointStyleRule( "align-content", "around" );
		public final BreakpointStyleRule stretch = new AcrossSimpleBreakpointStyleRule( "align-content", "stretch" );
	}

	@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
	public static class Text
	{
		public final BootstrapStyleRule top = AcrossStyleRule.of( BootstrapStyles.css.align.text.top );
		public final BootstrapStyleRule bottom = AcrossStyleRule.of( BootstrapStyles.css.align.text.bottom );
	}
}
