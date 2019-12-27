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
import org.apache.commons.lang3.ArrayUtils;

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
		primary = of( ArrayUtils.add( additionalCss, prefix + "-primary" ) );
		secondary = of( ArrayUtils.add( additionalCss, prefix + "-secondary" ) );
		success = of( ArrayUtils.add( additionalCss, prefix + "-success" ) );
		danger = of( ArrayUtils.add( additionalCss, prefix + "-danger" ) );
		warning = of( ArrayUtils.add( additionalCss, prefix + "-warning" ) );
		info = of( ArrayUtils.add( additionalCss, prefix + "-info" ) );
		light = of( ArrayUtils.add( additionalCss, prefix + "-light" ) );
		dark = of( ArrayUtils.add( additionalCss, prefix + "-dark" ) );
		white = of( ArrayUtils.add( additionalCss, prefix + "-white" ) );
	}
}
