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

/**
 * https://getbootstrap.com/docs/4.3/components/progress/
 *
 * @author Arne Vandamme
 * @since 3.0.0
 */
public class ProgressStyleRule implements BootstrapStyleRule
{
	public final Bar bar = new Bar();

	@Override
	public String[] toCssClasses() {
		return new String[] { "progress" };
	}

	@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
	public static class Bar implements BootstrapStyleRule
	{
		public final BootstrapStyleRule striped = appendOnSet( this, "progress-bar-striped" );
		public final BootstrapStyleRule animated = appendOnSet( striped, "progress-bar-animated" );

		@Override
		public String[] toCssClasses() {
			return new String[] { "progress-bar" };
		}
	}
}
