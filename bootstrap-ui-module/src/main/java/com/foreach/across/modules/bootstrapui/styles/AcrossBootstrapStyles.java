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

import com.foreach.across.modules.bootstrapui.styles.utilities.across.*;

/**
 * Framework alternative for {@link BootstrapStyles} that offers customized bootstrap utility classes.
 * These utility classes are the same as their bootstrap variant, except for:
 * - having a prefix {@code axu}
 * - removal of {@code !important}
 *
 * @author Steven Gentens
 * @since 3.0.0
 */
@SuppressWarnings("WeakerAccess")
public class AcrossBootstrapStyles
{
	public final static AcrossBootstrapStyles css = new AcrossBootstrapStyles();

	// utilities
	public final AcrossBorderStyleRule border = new AcrossBorderStyleRule();
	public final AcrossRoundedStyleRule rounded = new AcrossRoundedStyleRule();
	public final AcrossTextStyleRule text = new AcrossTextStyleRule();
	public final AcrossBackgroundStyleRule background = new AcrossBackgroundStyleRule();
	public final AcrossDisplayStyleRule display = new AcrossDisplayStyleRule();
	public final AcrossFlexStyleRule flex = new AcrossFlexStyleRule();
	public final AcrossJustifyContentStyleRule justifyContent = new AcrossJustifyContentStyleRule();
	public final AcrossAlignStyleRule align = new AcrossAlignStyleRule();
	public final AcrossOrderStyleRule order = new AcrossOrderStyleRule();
	public final AcrossFloatStyleRule cssFloat = new AcrossFloatStyleRule();
	public final AcrossOverflowStyleRule overflow = new AcrossOverflowStyleRule();
	public final AcrossPositionStyleRule position = new AcrossPositionStyleRule();
	public final AcrossShadowStyleRule shadow = new AcrossShadowStyleRule();
	public final AcrossSizeStyleRule size = new AcrossSizeStyleRule();
	public final AcrossFontStyleRule font = new AcrossFontStyleRule();
	public final BootstrapStyleRule visible = AcrossBootstrapStyleRule.of( BootstrapStyles.css.visible );
	public final BootstrapStyleRule invisible = AcrossBootstrapStyleRule.of( BootstrapStyles.css.invisible );
	public final AcrossPaddingStyleRule padding = new AcrossPaddingStyleRule();
	public final AcrossMarginStyleRule margin = new AcrossMarginStyleRule();
}
