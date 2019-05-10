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

package com.foreach.across.modules.bootstrapui.styles;

import lombok.RequiredArgsConstructor;

import static com.foreach.across.modules.bootstrapui.styles.BootstrapStyleRule.of;

/**
 * @author Arne Vandamme
 * @since 2.3.0
 */
@RequiredArgsConstructor
public class ColorStyleRule
{
	public final BootstrapStyleRule primary;
	public final BootstrapStyleRule secondary;
	public final BootstrapStyleRule success;
	public final BootstrapStyleRule danger;
	public final BootstrapStyleRule warning;
	public final BootstrapStyleRule info;
	public final BootstrapStyleRule light;
	public final BootstrapStyleRule dark;
	public final BootstrapStyleRule white;

	public ColorStyleRule( String prefix ) {
		primary = of( prefix + "-primary" );
		secondary = of( prefix + "-secondary" );
		success = of( prefix + "-success" );
		danger = of( prefix + "-danger" );
		warning = of( prefix + "-warning" );
		info = of( prefix + "-info" );
		light = of( prefix + "-light" );
		dark = of( prefix + "-dark" );
		white = of( prefix + "-white" );
	}
}
