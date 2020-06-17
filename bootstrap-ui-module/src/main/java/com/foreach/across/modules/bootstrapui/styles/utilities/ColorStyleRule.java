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

package com.foreach.across.modules.bootstrapui.styles.utilities;

import com.foreach.across.modules.bootstrapui.styles.BootstrapStyleRule;

import static com.foreach.across.modules.bootstrapui.styles.BootstrapStyleRule.appendOnSet;
import static com.foreach.across.modules.bootstrapui.styles.BootstrapStyleRule.of;

/**
 * Builds a Bootstrap color css class. Optionally takes some additional classes which
 * should be added as well (eg "alert" should always be added before the "alert-primary").
 *
 * @author Arne Vandamme
 * @since 3.0.0
 */
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

	public ColorStyleRule( String prefix, String... additionalCss ) {
		primary = appendOnSet( of( additionalCss ), prefix + "-primary" );
		secondary = appendOnSet( of( additionalCss ), prefix + "-secondary" );
		success = appendOnSet( of( additionalCss ), prefix + "-success" );
		danger = appendOnSet( of( additionalCss ), prefix + "-danger" );
		warning = appendOnSet( of( additionalCss ), prefix + "-warning" );
		info = appendOnSet( of( additionalCss ), prefix + "-info" );
		light = appendOnSet( of( additionalCss ), prefix + "-light" );
		dark = appendOnSet( of( additionalCss ), prefix + "-dark" );
		white = appendOnSet( of( additionalCss ), prefix + "-white" );
	}
}
