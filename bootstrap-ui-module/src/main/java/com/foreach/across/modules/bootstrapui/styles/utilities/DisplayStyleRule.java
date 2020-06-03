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

/**
 * See https://getbootstrap.com/docs/4.3/utilities/display/
 *
 * @author Arne Vandamme
 * @since 3.0.0
 */
public class DisplayStyleRule
{
	public final DisplayPropertyStyleRule none = new DisplayPropertyStyleRule( "none" );
	public final DisplayPropertyStyleRule inline = new DisplayPropertyStyleRule( "inline" );
	public final DisplayPropertyStyleRule inlineBlock = new DisplayPropertyStyleRule( "inline-block" );
	public final DisplayPropertyStyleRule block = new DisplayPropertyStyleRule( "block" );
	public final DisplayPropertyStyleRule table = new DisplayPropertyStyleRule( "table" );
	public final DisplayPropertyStyleRule tableCell = new DisplayPropertyStyleRule( "table-cell" );
	public final DisplayPropertyStyleRule tableRow = new DisplayPropertyStyleRule( "table-row" );
	public final DisplayPropertyStyleRule flex = new DisplayPropertyStyleRule( "flex" );
	public final DisplayPropertyStyleRule inlineFlex = new DisplayPropertyStyleRule( "inline-flex" );

	public static class DisplayPropertyStyleRule extends SimpleBreakpointStyleRule
	{
		private DisplayPropertyStyleRule( String display ) {
			super( "d", display );
		}

		public BootstrapStyleRule onPrint() {
			return on( "print" );
		}
	}
}
