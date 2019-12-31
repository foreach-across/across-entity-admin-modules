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
import com.foreach.across.modules.bootstrapui.styles.utilities.ColorStyleRule;
import com.foreach.across.modules.web.ui.elements.HtmlViewElement;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import static com.foreach.across.modules.bootstrapui.styles.BootstrapStyleRule.of;

/**
 * https://getbootstrap.com/docs/4.3/components/list-group/
 *
 * @author Arne Vandamme
 * @since 3.0.0
 */
public class ListGroupStyleRule implements BootstrapStyleRule
{
	public final BootstrapStyleRule flush = of( "list-group-flush" );
	public final Item item = new Item();
	public final Horizontal horizontal = new Horizontal();

	@Override
	public String[] toCssClasses() {
		return new String[] { "list-group" };
	}

	public static class Item extends ColorStyleRule implements BootstrapStyleRule
	{
		public final Action action = new Action();

		private Item() {
			super( "list-group-item", "list-group-item" );
		}

		@Override
		public String[] toCssClasses() {
			return new String[] { "list-group-item" };
		}

		public static class Action extends ColorStyleRule implements BootstrapStyleRule
		{
			private Action() {
				super( "list-group-item", "list-group-item", "list-group-item-action" );
			}

			@Override
			public String[] toCssClasses() {
				return new String[] { "list-group-item", "list-group-item-action" };
			}

			@Override
			public void removeFrom( HtmlViewElement target ) {
				target.removeCssClass( "list-group-item-action" );
			}
		}
	}

	@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
	public static class Horizontal implements BootstrapStyleRule
	{
		public final BootstrapStyleRule small = of( "list-group-horizontal-sm" );
		public final BootstrapStyleRule medium = of( "list-group-horizontal-md" );
		public final BootstrapStyleRule large = of( "list-group-horizontal-lg" );
		public final BootstrapStyleRule extraLarge = of( "list-group-horizontal-xl" );

		@Override
		public String[] toCssClasses() {
			return new String[] { "list-group-horizontal" };
		}
	}
}
