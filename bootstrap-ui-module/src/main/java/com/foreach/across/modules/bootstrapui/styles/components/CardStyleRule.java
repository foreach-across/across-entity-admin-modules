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

import static com.foreach.across.modules.bootstrapui.styles.BootstrapStyleRule.of;

/**
 * https://getbootstrap.com/docs/4.3/components/card/
 *
 * @author Arne Vandamme
 * @since 2.3.0
 */
public class CardStyleRule implements BootstrapStyleRule
{
	public final BootstrapStyleRule body = of( "card-body" );
	public final BootstrapStyleRule title = of( "card-title" );
	public final BootstrapStyleRule subTitle = of( "card-subtitle" );
	public final BootstrapStyleRule text = of( "card-text" );
	public final BootstrapStyleRule link = of( "card-link" );
	public final Image image = new Image();
	public final BootstrapStyleRule header = of( "card-header" );
	public final BootstrapStyleRule footer = of( "card-footer" );
	public final BootstrapStyleRule group = of( "card-group" );
	public final BootstrapStyleRule deck = of( "card-deck" );
	public final BootstrapStyleRule columns = of( "card-columns" );

	@Override
	public String[] toCssClasses() {
		return new String[] { "card" };
	}

	@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
	public static class Image
	{
		public final BootstrapStyleRule top = of( "card-img-top" );
		public final BootstrapStyleRule bottom = of( "card-img-bottom" );
		public final BootstrapStyleRule overlay = of( "card-img-overlay" );

	}
}
