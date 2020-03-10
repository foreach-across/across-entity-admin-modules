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

import com.foreach.across.modules.bootstrapui.styles.utilities.*;

/**
 * Wrapper around {@link BootstrapStyleRule} that can prefix bootstrap css classes for oour own
 * implementation.
 *
 * @author Vanhoof Stijn
 * @since 3.0.0
 */
@SuppressWarnings("WeakerAccess")
public class AcrossBootstrapStyles
{
	public final static AcrossBootstrapStyles css = new AcrossBootstrapStyles();

	// utilities
	public final BootstrapStyleRule clearfix = AcrossBootstrapStyleRule.of( BootstrapStyles.css.clearfix );
	public final BootstrapStyleRule close = AcrossBootstrapStyleRule.of( BootstrapStyles.css.close );
	public final BootstrapStyleRule fade = AcrossBootstrapStyleRule.of( BootstrapStyles.css.fade );
	public final BootstrapStyleRule show = AcrossBootstrapStyleRule.of( BootstrapStyles.css.show );
	public final BootstrapStyleRule active = AcrossBootstrapStyleRule.of( BootstrapStyles.css.active );
	public final BootstrapStyleRule disabled = AcrossBootstrapStyleRule.of( BootstrapStyles.css.disabled );
	public final BootstrapStyleRule slide = AcrossBootstrapStyleRule.of( BootstrapStyles.css.slide );
	public final BootstrapStyleRule lead = AcrossBootstrapStyleRule.of( BootstrapStyles.css.lead );
	public final BorderStyleRule border = new BorderStyleRule();
	public final RoundedStyleRule rounded = new RoundedStyleRule();
	public final TextStyleRule text = new TextStyleRule();
	public final BackgroundStyleRule background = new BackgroundStyleRule();
	public final DisplayStyleRule display = new DisplayStyleRule();
	public final EmbedStyleRule embed = new EmbedStyleRule();
	public final FlexStyleRule flex = new FlexStyleRule();
	public final JustifyContentStyleRule justifyContent = new JustifyContentStyleRule();
	public final AlignStyleRule align = new AlignStyleRule();
	public final OrderStyleRule order = new OrderStyleRule();
	public final FloatStyleRule cssFloat = new FloatStyleRule();
	public final OverflowStyleRule overflow = new OverflowStyleRule();
	public final PositionStyleRule position = new PositionStyleRule();
	public final FixedStyleRule fixed = new FixedStyleRule();
	public final BootstrapStyleRule stickyTop = AcrossBootstrapStyleRule.of( BootstrapStyles.css.stickyTop );
	public final ScreenReaderOnlyStyleRule screenReaderOnly = new ScreenReaderOnlyStyleRule();
	public final ShadowStyleRule shadow = new ShadowStyleRule();
	public final SizeStyleRule size = new SizeStyleRule();
	public final BootstrapStyleRule stretchedLink = AcrossBootstrapStyleRule.of( BootstrapStyles.css.stretchedLink );
	public final FontStyleRule font = new FontStyleRule();
	public final BootstrapStyleRule visible = AcrossBootstrapStyleRule.of( BootstrapStyles.css.visible );
	public final BootstrapStyleRule invisible = AcrossBootstrapStyleRule.of( BootstrapStyles.css.invisible );
	public final PaddingStyleRule padding = new PaddingStyleRule();
	public final MarginStyleRule margin = new MarginStyleRule();
}
