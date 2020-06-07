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

/**
 * https://getbootstrap.com/docs/4.3/utilities/spacing/
 *
 * @author Arne Vandamme
 * @since 3.0.0
 */
public class SpacingStyleRule
{
	public final BreakpointStyleRule none;
	public final BreakpointStyleRule s1;
	public final BreakpointStyleRule s2;
	public final BreakpointStyleRule s3;
	public final BreakpointStyleRule s4;
	public final BreakpointStyleRule s5;
	public final BreakpointStyleRule extraSmall;
	public final BreakpointStyleRule small;
	public final BreakpointStyleRule medium;
	public final BreakpointStyleRule large;
	public final BreakpointStyleRule extraLarge;

	private final String prefix;

	SpacingStyleRule( String prefix ) {
		this( prefix, "" );
	}

	private SpacingStyleRule( String prefix, String direction ) {
		this.prefix = prefix;

		none = new SimpleBreakpointStyleRule( prefix, direction + "0" );
		extraSmall = s1 = new SimpleBreakpointStyleRule( prefix, direction + "1" );
		small = s2 = new SimpleBreakpointStyleRule( prefix, direction + "2" );
		medium = s3 = new SimpleBreakpointStyleRule( prefix, direction + "3" );
		large = s4 = new SimpleBreakpointStyleRule( prefix, direction + "4" );
		extraLarge = s5 = new SimpleBreakpointStyleRule( prefix, direction + "5" );
	}

	public BreakpointStyleRule size( int size ) {
		return new SimpleBreakpointStyleRule( prefix, "" + size );
	}

	public static class WithNegative extends SpacingStyleRule
	{
		public final BreakpointStyleRule auto;
		public final SpacingStyleRule negative;

		WithNegative( String prefix ) {
			super( prefix );
			this.negative = new SpacingStyleRule( prefix, "n" );
			this.auto = new SimpleBreakpointStyleRule( prefix, "auto" );
		}
	}
}
