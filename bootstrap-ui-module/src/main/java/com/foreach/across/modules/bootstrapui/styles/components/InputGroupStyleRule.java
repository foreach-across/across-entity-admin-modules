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

import static com.foreach.across.modules.bootstrapui.styles.BootstrapStyleRule.of;

/**
 * https://getbootstrap.com/docs/4.3/components/input-group/
 *
 * @author Arne Vandamme
 * @since 2.3.0
 */
public class InputGroupStyleRule implements BootstrapStyleRule
{
	public final BootstrapStyleRule text = of( "input-group-text" );
	public final BootstrapStyleRule prepend = of( "input-group-prepend" );
	public final BootstrapStyleRule append = of( "input-group-append" );
	public final BootstrapStyleRule large = of( "input-group-lg" );
	public final BootstrapStyleRule small = of( "input-group-sm" );

	@Override
	public String[] toCssClasses() {
		return new String[] { "input-group" };
	}
}
