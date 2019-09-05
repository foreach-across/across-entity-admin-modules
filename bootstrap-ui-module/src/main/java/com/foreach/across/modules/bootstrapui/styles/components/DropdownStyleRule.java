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

package com.foreach.across.modules.bootstrapui.styles.components;

import com.foreach.across.modules.bootstrapui.styles.BootstrapStyleRule;
import com.foreach.across.modules.bootstrapui.styles.utilities.BreakpointStyleRule;
import com.foreach.across.modules.bootstrapui.styles.utilities.SimpleBreakpointStyleRule;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import static com.foreach.across.modules.bootstrapui.styles.BootstrapStyleRule.of;

/**
 * https://getbootstrap.com/docs/4.3/components/dropdowns/
 *
 * @author Arne Vandamme
 * @since 2.3.0
 */
public class DropdownStyleRule implements BootstrapStyleRule
{
	public final Toggle toggle = new Toggle();
	public final Menu menu = new Menu();
	public final Item item = new Item();
	public final BootstrapStyleRule divider = of( "dropdown-divider" );
	public final BootstrapStyleRule header = of( "dropdown-header" );
	public final Direction direction = new Direction();

	@Override
	public String[] toCssClasses() {
		return new String[] { "dropdown" };
	}

	@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
	public static class Toggle implements BootstrapStyleRule
	{
		public final BootstrapStyleRule split = of( "dropdown-toggle-split" );

		@Override
		public String[] toCssClasses() {
			return new String[] { "dropdown-toggle" };
		}
	}

	@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
	public static class Item implements BootstrapStyleRule
	{
		public final BootstrapStyleRule text = of( "dropdown-item-text" );

		@Override
		public String[] toCssClasses() {
			return new String[] { "dropdown-item" };
		}
	}

	@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
	public static class Menu implements BootstrapStyleRule
	{
		public final BreakpointStyleRule right = new SimpleBreakpointStyleRule( "dropdown-menu", "right", "dropdown-menu" );
		public final BreakpointStyleRule left = new SimpleBreakpointStyleRule( "dropdown-menu", "left", "dropdown-menu" );

		@Override
		public String[] toCssClasses() {
			return new String[] { "dropdown-menu" };
		}
	}

	@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
	public static class Direction
	{
		public final BootstrapStyleRule up = of( "dropup" );
		public final BootstrapStyleRule right = of( "dropright" );
		public final BootstrapStyleRule left = of( "dropleft" );
	}
}
