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

import com.foreach.across.modules.bootstrapui.styles.AcrossBootstrapStyleRule;
import com.foreach.across.modules.bootstrapui.styles.BootstrapStyleRule;
import com.foreach.across.modules.bootstrapui.styles.BootstrapStyles;

/**
 * @author Steven Gentens
 * @since 3.0.0
 */
public class AcrossBackgroundStyleRule extends AcrossColorStyleRule
{
	public final BootstrapStyleRule transparent = AcrossBootstrapStyleRule.of( BootstrapStyles.css.background.transparent );
	public final AcrossColorStyleRule gradient = new AcrossColorStyleRule( "bg-gradient" );

	public AcrossBackgroundStyleRule() {
		super( "bg" );
	}
}
