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

package com.foreach.across.modules.bootstrapui.styles.layout;

import com.foreach.across.modules.bootstrapui.styles.BootstrapStyleRule;
import com.foreach.across.modules.bootstrapui.styles.utilities.BreakpointStyleRule;
import com.foreach.across.modules.bootstrapui.styles.utilities.SimpleBreakpointStyleRule;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import static com.foreach.across.modules.bootstrapui.styles.BootstrapStyleRule.of;

/**
 * https://getbootstrap.com/docs/4.3/layout/grid/
 *
 * @author Arne Vandamme
 * @since 3.0.0
 */
public class GridStyleRule
{
	public final Container container = new Container();
	public final Row row = new Row();
	public final Column column = new Column();
	public final BreakpointStyleRule column1 = column.w1;
	public final BreakpointStyleRule column2 = column.w2;
	public final BreakpointStyleRule column3 = column.w3;
	public final BreakpointStyleRule column4 = column.w4;
	public final BreakpointStyleRule column5 = column.w5;
	public final BreakpointStyleRule column6 = column.w6;
	public final BreakpointStyleRule column7 = column.w7;
	public final BreakpointStyleRule column8 = column.w8;
	public final BreakpointStyleRule column9 = column.w9;
	public final BreakpointStyleRule column10 = column.w10;
	public final BreakpointStyleRule column11 = column.w11;
	public final BreakpointStyleRule column12 = column.w12;

	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class Container implements BootstrapStyleRule
	{
		public final BootstrapStyleRule fluid = of( "container-fluid" );

		@Override
		public String[] toCssClasses() {
			return new String[] { "container" };
		}
	}

	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class Row implements BootstrapStyleRule
	{
		public final BootstrapStyleRule noGutters = of( "row", "no-gutters" );

		@Override
		public String[] toCssClasses() {
			return new String[] { "row" };
		}
	}

	public static class Column extends SimpleBreakpointStyleRule implements BootstrapStyleRule
	{
		public final BreakpointStyleRule w1 = width( 1 );
		public final BreakpointStyleRule w2 = width( 2 );
		public final BreakpointStyleRule w3 = width( 3 );
		public final BreakpointStyleRule w4 = width( 4 );
		public final BreakpointStyleRule w5 = width( 5 );
		public final BreakpointStyleRule w6 = width( 6 );
		public final BreakpointStyleRule w7 = width( 7 );
		public final BreakpointStyleRule w8 = width( 8 );
		public final BreakpointStyleRule w9 = width( 9 );
		public final BreakpointStyleRule w10 = width( 10 );
		public final BreakpointStyleRule w11 = width( 11 );
		public final BreakpointStyleRule w12 = width( 12 );

		public final BreakpointStyleRule offset0 = offset( 0 );
		public final BreakpointStyleRule offset1 = offset( 1 );
		public final BreakpointStyleRule offset2 = offset( 2 );
		public final BreakpointStyleRule offset3 = offset( 3 );
		public final BreakpointStyleRule offset4 = offset( 4 );
		public final BreakpointStyleRule offset5 = offset( 5 );
		public final BreakpointStyleRule offset6 = offset( 6 );
		public final BreakpointStyleRule offset7 = offset( 7 );
		public final BreakpointStyleRule offset8 = offset( 8 );
		public final BreakpointStyleRule offset9 = offset( 9 );
		public final BreakpointStyleRule offset10 = offset( 10 );
		public final BreakpointStyleRule offset11 = offset( 11 );
		public final BreakpointStyleRule offset12 = offset( 12 );
		public final BreakpointStyleRule noOffset = offset0;

		public final BootstrapStyleRule auto = of( "col-auto" );

		private Column() {
			super( "col", null );
		}

		public BreakpointStyleRule width( int width ) {
			return new SimpleBreakpointStyleRule( "col", "" + width );
		}

		public BreakpointStyleRule offset( int width ) {
			return new SimpleBreakpointStyleRule( "offset", "" + width );
		}
	}
}
