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

import static com.foreach.across.modules.bootstrapui.styles.BootstrapStyleRule.of;

/**
 * https://getbootstrap.com/docs/4.3/utilities/flex/#align-items
 * https://getbootstrap.com/docs/4.3/utilities/flex/#align-self
 * https://getbootstrap.com/docs/4.3/utilities/flex/#align-content
 * https://getbootstrap.com/docs/4.3/utilities/vertical-align/
 *
 * @author Arne Vandamme
 * @since 2.3.0
 */
public class AlignStyleRule
{
	public final Items items = new Items();
	public final Self self = new Self();
	public final Content content = new Content();
	public final BootstrapStyleRule baseline = of( "align-baseline" );
	public final BootstrapStyleRule top = of( "align-top" );
	public final BootstrapStyleRule middle = of( "align-middle" );
	public final BootstrapStyleRule bottom = of( "align-bottom" );
	public final Text text = new Text();

	@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
	public static class Items
	{
		public final BreakpointStyleRule start = new SimpleBreakpointStyleRule( "align-items", "start" );
		public final BreakpointStyleRule end = new SimpleBreakpointStyleRule( "align-items", "end" );
		public final BreakpointStyleRule center = new SimpleBreakpointStyleRule( "align-items", "center" );
		public final BreakpointStyleRule baseline = new SimpleBreakpointStyleRule( "align-items", "baseline" );
		public final BreakpointStyleRule stretch = new SimpleBreakpointStyleRule( "align-items", "stretch" );
	}

	@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
	public static class Self
	{
		public final BreakpointStyleRule start = new SimpleBreakpointStyleRule( "align-self", "start" );
		public final BreakpointStyleRule end = new SimpleBreakpointStyleRule( "align-self", "end" );
		public final BreakpointStyleRule center = new SimpleBreakpointStyleRule( "align-self", "center" );
		public final BreakpointStyleRule baseline = new SimpleBreakpointStyleRule( "align-self", "baseline" );
		public final BreakpointStyleRule stretch = new SimpleBreakpointStyleRule( "align-self", "stretch" );
	}

	@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
	public static class Content
	{
		public final BreakpointStyleRule start = new SimpleBreakpointStyleRule( "align-content", "start" );
		public final BreakpointStyleRule end = new SimpleBreakpointStyleRule( "align-content", "end" );
		public final BreakpointStyleRule center = new SimpleBreakpointStyleRule( "align-content", "center" );
		public final BreakpointStyleRule between = new SimpleBreakpointStyleRule( "align-content", "between" );
		public final BreakpointStyleRule around = new SimpleBreakpointStyleRule( "align-content", "around" );
		public final BreakpointStyleRule stretch = new SimpleBreakpointStyleRule( "align-content", "stretch" );
	}

	@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
	public static class Text
	{
		public final BootstrapStyleRule top = of( "align-text-top" );
		public final BootstrapStyleRule bottom = of( "align-text-bottom" );
	}
}
