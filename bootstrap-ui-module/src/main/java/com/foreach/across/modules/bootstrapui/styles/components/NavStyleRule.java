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
 * https://getbootstrap.com/docs/4.3/components/navs/
 *
 * @author Arne Vandamme
 * @since 2.3.0
 */
public class NavStyleRule implements BootstrapStyleRule
{
	public final BootstrapStyleRule item = of( "nav-item" );
	public final BootstrapStyleRule link = of( "nav-link" );
	public final BootstrapStyleRule tabs = of( "nav", "nav-tabs" );
	public final BootstrapStyleRule pills = of( "nav", "nav-pills" );
	public final BootstrapStyleRule fill = of( "nav-fill" );
	public final BootstrapStyleRule justified = of( "nav-justified" );

	@Override
	public String[] toCssClasses() {
		return new String[] { "nav" };
	}
}
