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
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import static com.foreach.across.modules.bootstrapui.styles.BootstrapStyleRule.appendOnSet;
import static com.foreach.across.modules.bootstrapui.styles.BootstrapStyleRule.of;

/**
 * https://getbootstrap.com/docs/4.3/components/modal/
 *
 * @author Arne Vandamme
 * @since 3.0.0
 */
public class ModalStyleRule implements BootstrapStyleRule
{
	public final Dialog dialog = new Dialog();
	public final BootstrapStyleRule content = of( "modal-content" );
	public final BootstrapStyleRule header = of( "modal-header" );
	public final BootstrapStyleRule title = of( "modal-title" );
	public final BootstrapStyleRule body = of( "modal-body" );
	public final BootstrapStyleRule footer = of( "modal-footer" );
	public final BootstrapStyleRule small = of( "modal-sm" );
	public final BootstrapStyleRule large = of( "modal-lg" );
	public final BootstrapStyleRule extraLarge = of( "modal-xl" );

	@Override
	public String[] toCssClasses() {
		return new String[] { "modal" };
	}

	@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
	public static class Dialog implements BootstrapStyleRule
	{
		public final BootstrapStyleRule scrollable = of( "modal-dialog-scrollable" );
		public final BootstrapStyleRule centered = of( "modal-dialog-centered" );
		public final BootstrapStyleRule small = appendOnSet( this, "modal-sm" );
		public final BootstrapStyleRule large = appendOnSet( this, "modal-lg" );
		public final BootstrapStyleRule extraLarge = appendOnSet( this, "modal-xl" );

		@Override
		public String[] toCssClasses() {
			return new String[] { "modal-dialog" };
		}
	}
}
