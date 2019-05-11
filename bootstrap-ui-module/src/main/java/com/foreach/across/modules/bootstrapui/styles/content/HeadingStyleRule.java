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

import static com.foreach.across.modules.bootstrapui.styles.BootstrapStyleRule.of;

/**
 * https://getbootstrap.com/docs/4.3/content/typography/
 *
 * @author Arne Vandamme
 * @since 2.3.0
 */
public class HeadingStyleRule
{
	public final BootstrapStyleRule h1 = of( "h1" );
	public final BootstrapStyleRule h2 = of( "h2" );
	public final BootstrapStyleRule h3 = of( "h3" );
	public final BootstrapStyleRule h4 = of( "h4" );
	public final BootstrapStyleRule h5 = of( "h5" );
	public final BootstrapStyleRule h6 = of( "h6" );

	// display headings
	public final BootstrapStyleRule display1 = of( "display-1" );
	public final BootstrapStyleRule display2 = of( "display-2" );
	public final BootstrapStyleRule display3 = of( "display-3" );
	public final BootstrapStyleRule display4 = of( "display-4" );
}
