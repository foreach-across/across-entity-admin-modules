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

package com.foreach.across.modules.bootstrapui.styles.utilities.across;

import com.foreach.across.modules.bootstrapui.styles.AcrossStyleRule;
import com.foreach.across.modules.bootstrapui.styles.BootstrapStyleRule;
import com.foreach.across.modules.bootstrapui.styles.BootstrapStyles;

/**
 * https://getbootstrap.com/docs/4.3/utilities/position/
 *
 * @author Steven Gentens
 * @since 3.0.0
 */
public class AcrossPositionStyleRule
{
	public final BootstrapStyleRule cssStatic = AcrossStyleRule.of( BootstrapStyles.css.position.cssStatic );
	public final BootstrapStyleRule relative = AcrossStyleRule.of( BootstrapStyles.css.position.relative );
	public final BootstrapStyleRule absolute = AcrossStyleRule.of( BootstrapStyles.css.position.absolute );
	public final BootstrapStyleRule fixed = AcrossStyleRule.of( BootstrapStyles.css.position.fixed );
	public final BootstrapStyleRule sticky = AcrossStyleRule.of( BootstrapStyles.css.position.sticky );
}
