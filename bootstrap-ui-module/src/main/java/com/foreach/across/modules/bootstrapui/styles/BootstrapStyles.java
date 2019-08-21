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

import com.foreach.across.modules.bootstrapui.styles.components.*;
import com.foreach.across.modules.bootstrapui.styles.content.*;
import com.foreach.across.modules.bootstrapui.styles.icons.FontAwesomeStyleRule;
import com.foreach.across.modules.bootstrapui.styles.layout.GridStyleRule;
import com.foreach.across.modules.bootstrapui.styles.utilities.*;

/**
 * Contains strong typed accessors for all common Bootstrap CSS classes.
 * Structure based on the documentation and css class format.
 * This can be a more explanatory approach for determining the css classes to use.
 * <p/>
 * Deliberately implemented as final instance fields, to have a fluent, readable
 * but not too "intrusive" effect.
 * Example: <code>BootstrapStyles.css.margin.horizontal.auto</code>
 * <p/>
 * https://getbootstrap.com/docs/4.3/utilities/
 * https://getbootstrap.com/docs/4.3/components/
 *
 * @author Arne Vandamme
 * @since 2.3.0
 */
@SuppressWarnings("WeakerAccess")
public class BootstrapStyles
{
	public final static BootstrapStyles css = new BootstrapStyles();

	// utilities
	public final BootstrapStyleRule clearfix = of( "clearfix" );
	public final BootstrapStyleRule close = of( "close" );
	public final BootstrapStyleRule fade = of( "fade" );
	public final BootstrapStyleRule show = of( "show" );
	public final BootstrapStyleRule active = of( "active" );
	public final BootstrapStyleRule disabled = of( "disabled" );
	public final BootstrapStyleRule slide = of( "slide" );
	public final BootstrapStyleRule lead = of( "lead" );
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

	// components
	public final AlertStyleRule alert = new AlertStyleRule();
	public final BadgeStyleRule badge = new BadgeStyleRule();
	public final BreadcrumbStyleRule breadcrumb = new BreadcrumbStyleRule();
	public final ButtonStyleRule button = new ButtonStyleRule();
	public final CardStyleRule card = new CardStyleRule();
	public final CarouselStyleRule carousel = new CarouselStyleRule();
	public final BootstrapStyleRule collapse = of( "collapse" );
	public final BootstrapStyleRule collapsed = of( "collapsed" );
	public final BootstrapStyleRule collapsing = of( "collapsing" );
	public final BootstrapStyleRule accordion = of( "accordion" );
	public final DropdownStyleRule dropdown = new DropdownStyleRule();
	public final BootstrapStyleRule dropUp = dropdown.direction.up;
	public final BootstrapStyleRule dropRight = dropdown.direction.right;
	public final BootstrapStyleRule dropLeft = dropdown.direction.left;
	public final FormStyleRule form = new FormStyleRule();
	public final FormStyleRule.Custom custom = form.custom;
	public final ValidationStyleRule validation = new ValidationStyleRule();
	public final ValidationStyleRule.State valid = validation.valid;
	public final ValidationStyleRule.State invalid = validation.invalid;
	public final InputGroupStyleRule inputGroup = new InputGroupStyleRule();
	public final JumbotronStyleRule jumbotron = new JumbotronStyleRule();
	public final ListGroupStyleRule listGroup = new ListGroupStyleRule();
	public final TabStyleRule tab = new TabStyleRule();
	public final MediaStyleRule media = new MediaStyleRule();
	public final ModalStyleRule modal = new ModalStyleRule();
	public final NavStyleRule nav = new NavStyleRule();
	public final NavbarStyleRule navbar = new NavbarStyleRule();
	public final PaginationStyleRule pagination = new PaginationStyleRule();
	public final PaginationStyleRule.Page page = pagination.page;
	public final ProgressStyleRule progress = new ProgressStyleRule();
	public final SpinnerStyleRule spinner = new SpinnerStyleRule();
	public final ToastStyleRule toast = new ToastStyleRule();

	// content
	public final HeadingStyleRule heading = new HeadingStyleRule();
	public final BootstrapStyleRule mark = of( "mark" );
	public final BootstrapStyleRule small = of( "small" );
	public final BootstrapStyleRule initialism = of( "initialism" );
	public final BlockQuoteStyleRule blockQuote = new BlockQuoteStyleRule();
	public final ListStyleRule list = new ListStyleRule();
	public final ImageStyleRule image = new ImageStyleRule();
	public final TableStyleRule table = new TableStyleRule();
	public final FigureStyleRule figure = new FigureStyleRule();

	// layout
	public final GridStyleRule grid = new GridStyleRule();

	// icon sets
	public final FontAwesomeStyleRule fa = new FontAwesomeStyleRule();

	public final BootstrapStyleRule of( String... cssClassNames ) {
		return BootstrapStyleRule.of( cssClassNames );
	}
}
