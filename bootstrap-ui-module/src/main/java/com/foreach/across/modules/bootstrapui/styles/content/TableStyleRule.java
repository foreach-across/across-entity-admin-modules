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

package com.foreach.across.modules.bootstrapui.styles.content;

import com.foreach.across.modules.bootstrapui.styles.BootstrapStyleRule;
import com.foreach.across.modules.bootstrapui.styles.utilities.BreakpointStyleRule;
import com.foreach.across.modules.bootstrapui.styles.utilities.ColorStyleRule;
import com.foreach.across.modules.bootstrapui.styles.utilities.SimpleBreakpointStyleRule;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import static com.foreach.across.modules.bootstrapui.styles.BootstrapStyleRule.appendOnSet;
import static com.foreach.across.modules.bootstrapui.styles.BootstrapStyleRule.of;

/**
 * https://getbootstrap.com/docs/4.3/content/tables/
 *
 * @author Arne Vandamme
 * @since 3.0.0
 */
public class TableStyleRule extends ColorStyleRule implements BootstrapStyleRule
{
	public final Head head = new Head();
	public final BootstrapStyleRule striped = appendOnSet( this, "table-striped" );
	public final BootstrapStyleRule bordered = appendOnSet( this, "table-bordered" );
	public final BootstrapStyleRule borderless = appendOnSet( this, "table-borderless" );
	public final BootstrapStyleRule hover = appendOnSet( this, "table-hover" );
	public final BootstrapStyleRule small = appendOnSet( this, "table-sm" );
	public final BootstrapStyleRule active = of( "table-active" );
	public final BreakpointStyleRule reponsive = new SimpleBreakpointStyleRule( "table-responsive", null );

	public TableStyleRule() {
		super( "table" );
	}

	@Override
	public String[] toCssClasses() {
		return new String[] { "table" };
	}

	@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
	public static class Head
	{
		public final BootstrapStyleRule dark = of( "thead-dark" );
		public final BootstrapStyleRule light = of( "thead-light" );
	}
}
