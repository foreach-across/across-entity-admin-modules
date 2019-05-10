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

/**
 * https://getbootstrap.com/docs/4.3/utilities/flex/
 *
 * @author Arne Vandamme
 * @since 2.3.0
 */
@SuppressWarnings("WeakerAccess")
public class FlexStyleRule
{
	public final Direction row = new Direction( "row" );
	public final Direction column = new Direction( "column" );
	public final BreakpointStyleRule fill = new SimpleBreakpointStyleRule( "flex", "fill" );
	public final Toggle grow = new Toggle( "grow" );
	public final Toggle shrink = new Toggle( "shrink" );
	public final Wrap wrap = new Wrap();
	public final BreakpointStyleRule nowrap = wrap.none;

	public static class Direction extends SimpleBreakpointStyleRule
	{
		public final BreakpointStyleRule reverse;

		private Direction( String direction ) {
			super( "flex", direction );
			reverse = direction.endsWith( "-reverse" ) ? null : new Direction( direction + "-reverse" );
		}
	}

	public static class Wrap extends Direction
	{
		public final BreakpointStyleRule none = new SimpleBreakpointStyleRule( "flex", "nowrap" );

		private Wrap() {
			super( "wrap" );
		}
	}

	public static class Toggle
	{
		public final BreakpointStyleRule enabled;
		public final BreakpointStyleRule disabled;

		private Toggle( String css ) {
			enabled = new SimpleBreakpointStyleRule( "flex", css + "-1" );
			disabled = new SimpleBreakpointStyleRule( "flex", css + "-0" );
		}
	}
}
