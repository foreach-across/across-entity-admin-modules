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

package com.foreach.across.modules.bootstrapui.styles.icons;

import com.foreach.across.modules.bootstrapui.styles.BootstrapStyleRule;

import static com.foreach.across.modules.bootstrapui.styles.BootstrapStyleRule.of;

/**
 * FontAwesome 5 css rules.
 *
 * @author Arne Vandamme
 * @since 3.0.0
 */
public class FontAwesomeStyleRule
{
	public BootstrapStyleRule solid( String iconName ) {
		return of( "fas", "fa-" + iconName );
	}

	public BootstrapStyleRule brands( String iconName ) {
		return of( "fab", "fa-" + iconName );
	}

	public BootstrapStyleRule regular( String iconName ) {
		return of( "far", "fa-" + iconName );
	}

	public BootstrapStyleRule light( String iconName ) {
		return of( "fal", "fa-" + iconName );
	}

	public BootstrapStyleRule duotone( String iconName ) {
		return of( "fad", "fa-" + iconName );
	}
}
