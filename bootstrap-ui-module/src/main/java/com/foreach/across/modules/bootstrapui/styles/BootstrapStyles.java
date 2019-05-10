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

import static com.foreach.across.modules.bootstrapui.styles.BootstrapStyleRule.of;

/**
 * https://getbootstrap.com/docs/4.3/utilities/
 *
 * @author Arne Vandamme
 * @since 2.3.0
 */
@SuppressWarnings("WeakerAccess")
public class BootstrapStyles
{
	public final static BootstrapStyles css = new BootstrapStyles();

	public final BootstrapStyleRule clearfix = of( "clearfix" );
	public final BootstrapStyleRule close = of( "close" );
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
	public final BootstrapStyleRule stickyTop = of( "sticky-top" );
	public final ScreenReaderOnlyStyleRule screenReaderOnly = new ScreenReaderOnlyStyleRule();
	public final ShadowStyleRule shadow = new ShadowStyleRule();
	public final SizeStyleRule size = new SizeStyleRule();
	public final BootstrapStyleRule stretchedLink = of( "stretched-link" );
	public final FontStyleRule font = new FontStyleRule();
	public final BootstrapStyleRule visible = of( "visible" );
	public final BootstrapStyleRule invisible = of( "invisible" );
	public final PaddingStyleRule padding = new PaddingStyleRule();
	public final MarginStyleRule margin = new MarginStyleRule();
}
