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
 * https://getbootstrap.com/docs/4.3/utilities/flex/#order
 *
 * @author Arne Vandamme
 * @since 2.3.0
 */
@SuppressWarnings("WeakerAccess")
public class OrderStyleRule
{
	public final BreakpointStyleRule first = new SimpleBreakpointStyleRule( "order", "first" );
	public final BreakpointStyleRule last = new SimpleBreakpointStyleRule( "order", "last" );

	public BreakpointStyleRule position( int position ) {
		return new SimpleBreakpointStyleRule( "order", "" + position );
	}
}
