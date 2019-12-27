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
 * https://getbootstrap.com/docs/4.3/components/carousel/
 *
 * @author Arne Vandamme
 * @since 3.0.0
 */
public class CarouselStyleRule implements BootstrapStyleRule
{
	public final BootstrapStyleRule inner = of( "carousel-inner" );
	public final BootstrapStyleRule item = of( "carousel-item" );
	public final BootstrapStyleRule indicators = of( "carousel-indicators" );
	public final BootstrapStyleRule caption = of( "carousel-caption" );
	public final BootstrapStyleRule fade = of( "carousel-fade" );
	public final Control control = new Control();

	@Override
	public String[] toCssClasses() {
		return new String[] { "carousel" };
	}

	@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
	public static class Control
	{
		public final WithIcon previous = new WithIcon( "carousel-control-prev" );
		public final WithIcon next = new WithIcon( "carousel-control-next" );

		public static class WithIcon implements BootstrapStyleRule
		{
			public final BootstrapStyleRule icon;

			private final String direction;

			private WithIcon( String direction ) {
				this.direction = direction;
				icon = of( direction + "-icon" );
			}

			@Override
			public String[] toCssClasses() {
				return new String[] { direction };
			}
		}
	}
}
