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

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;

import static com.foreach.across.modules.bootstrapui.styles.BootstrapStyles.css;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * See https://getbootstrap.com/docs/4.3/utilities/
 *
 * @author Arne Vandamme
 * @since 2.3.0
 */
class TestBootstrapStyles
{
	@Test
	void clearfix() {
		assertStyle( css.clearfix ).is( "clearfix" );
	}

	@Test
	void close() {
		assertStyle( css.close ).is( "close" );
	}

	@Test
	void borders() {
		// additive
		assertStyle( css.border ).is( "border" );
		assertStyle( css.border.top ).is( "border-top" );
		assertStyle( css.border.right ).is( "border-right" );
		assertStyle( css.border.bottom ).is( "border-bottom" );
		assertStyle( css.border.left ).is( "border-left" );

		// subtractive
		assertStyle( css.border.none ).is( "border-0" );
		assertStyle( css.border.top.none ).is( "border-top-0" );
		assertStyle( css.border.right.none ).is( "border-right-0" );
		assertStyle( css.border.bottom.none ).is( "border-bottom-0" );
		assertStyle( css.border.left.none ).is( "border-left-0" );

		// color
		assertStyle( css.border.primary ).is( "border-primary" );
		assertStyle( css.border.secondary ).is( "border-secondary" );
		assertStyle( css.border.success ).is( "border-success" );
		assertStyle( css.border.danger ).is( "border-danger" );
		assertStyle( css.border.warning ).is( "border-warning" );
		assertStyle( css.border.info ).is( "border-info" );
		assertStyle( css.border.light ).is( "border-light" );
		assertStyle( css.border.dark ).is( "border-dark" );
		assertStyle( css.border.white ).is( "border-white" );

		// radius
		assertStyle( css.rounded ).is( "rounded" );
		assertStyle( css.rounded.top ).is( "rounded-top" );
		assertStyle( css.rounded.right ).is( "rounded-right" );
		assertStyle( css.rounded.bottom ).is( "rounded-bottom" );
		assertStyle( css.rounded.left ).is( "rounded-left" );
		assertStyle( css.rounded.circle ).is( "rounded-circle" );
		assertStyle( css.rounded.pill ).is( "rounded-pill" );
		assertStyle( css.rounded.none ).is( "rounded-0" );

		// sizes
		assertStyle( css.rounded.small ).is( "rounded-sm" );
		assertStyle( css.rounded.large ).is( "rounded-lg" );
	}

	@Test
	void colors() {
		// text color
		assertStyle( css.text.primary ).is( "text-primary" );
		assertStyle( css.text.secondary ).is( "text-secondary" );
		assertStyle( css.text.success ).is( "text-success" );
		assertStyle( css.text.danger ).is( "text-danger" );
		assertStyle( css.text.warning ).is( "text-warning" );
		assertStyle( css.text.info ).is( "text-info" );
		assertStyle( css.text.light ).is( "text-light" );
		assertStyle( css.text.dark ).is( "text-dark" );
		assertStyle( css.text.white ).is( "text-white" );
		assertStyle( css.text.body ).is( "text-body" );
		assertStyle( css.text.muted ).is( "text-muted" );

		// background color
		assertStyle( css.background.primary ).is( "bg-primary" );
		assertStyle( css.background.secondary ).is( "bg-secondary" );
		assertStyle( css.background.success ).is( "bg-success" );
		assertStyle( css.background.danger ).is( "bg-danger" );
		assertStyle( css.background.warning ).is( "bg-warning" );
		assertStyle( css.background.info ).is( "bg-info" );
		assertStyle( css.background.light ).is( "bg-light" );
		assertStyle( css.background.dark ).is( "bg-dark" );
		assertStyle( css.background.white ).is( "bg-white" );
		assertStyle( css.background.transparent ).is( "bg-transparent" );

		// background gradient
		assertStyle( css.background.gradient.primary ).is( "bg-gradient-primary" );
		assertStyle( css.background.gradient.secondary ).is( "bg-gradient-secondary" );
		assertStyle( css.background.gradient.success ).is( "bg-gradient-success" );
		assertStyle( css.background.gradient.danger ).is( "bg-gradient-danger" );
		assertStyle( css.background.gradient.warning ).is( "bg-gradient-warning" );
		assertStyle( css.background.gradient.info ).is( "bg-gradient-info" );
		assertStyle( css.background.gradient.light ).is( "bg-gradient-light" );
		assertStyle( css.background.gradient.dark ).is( "bg-gradient-dark" );
		assertStyle( css.background.gradient.white ).is( "bg-gradient-white" );
	}

	@Test
	void display() {
		assertStyle( css.display.none ).is( "d-none" );
		assertStyle( css.display.inline ).is( "d-inline" );
		assertStyle( css.display.inlineBlock ).is( "d-inline-block" );
		assertStyle( css.display.block ).is( "d-block" );
		assertStyle( css.display.table ).is( "d-table" );
		assertStyle( css.display.tableCell ).is( "d-table-cell" );
		assertStyle( css.display.tableRow ).is( "d-table-row" );
		assertStyle( css.display.flex ).is( "d-flex" );
		assertStyle( css.display.inlineFlex ).is( "d-inline-flex" );

		assertStyle( css.display.none.onSmall() ).is( "d-sm-none" );
		assertStyle( css.display.inline.onSmall() ).is( "d-sm-inline" );
		assertStyle( css.display.inlineBlock.onMedium() ).is( "d-md-inline-block" );
		assertStyle( css.display.block.onMedium() ).is( "d-md-block" );
		assertStyle( css.display.table.onLarge() ).is( "d-lg-table" );
		assertStyle( css.display.tableCell.onLarge() ).is( "d-lg-table-cell" );
		assertStyle( css.display.tableRow.onExtraLarge() ).is( "d-xl-table-row" );
		assertStyle( css.display.flex.onExtraLarge() ).is( "d-xl-flex" );
		assertStyle( css.display.inlineFlex.onExtraLarge() ).is( "d-xl-inline-flex" );

		// print
		assertStyle( css.display.none.onPrint() ).is( "d-print-none" );
		assertStyle( css.display.inline.onPrint() ).is( "d-print-inline" );
	}

	@Test
	void embed() {
		assertStyle( css.embed.responsive ).is( "embed-responsive" );
		assertStyle( css.embed.responsiveItem ).is( "embed-responsive-item" );

		// aspect ratio
		assertStyle( css.embed.responsive.aspectRatio( "21by9" ) ).is( "embed-responsive-21by9" );
		assertStyle( css.embed.responsive.aspectRatio( "16by9" ) ).is( "embed-responsive-16by9" );
		assertStyle( css.embed.responsive.aspectRatio( "4by3" ) ).is( "embed-responsive-4by3" );
		assertStyle( css.embed.responsive.aspectRatio( "1by1" ) ).is( "embed-responsive-1by1" );
		assertStyle( css.embed.responsive.aspectRatio( "1:1" ) ).is( "embed-responsive-1by1" );
		assertStyle( css.embed.responsive.aspectRatio( "16/9" ) ).is( "embed-responsive-16by9" );
		assertStyle( css.embed.responsive.aspectRatio( "4:3" ) ).is( "embed-responsive-4by3" );
		assertStyle( css.embed.responsive.aspectRatio( "4/3" ) ).is( "embed-responsive-4by3" );
	}

	@Test
	void flex() {
		assertStyle( css.flex.row ).is( "flex-row" );
		assertStyle( css.flex.row.reverse ).is( "flex-row-reverse" );
		assertStyle( css.flex.row.onSmall() ).is( "flex-sm-row" );
		assertStyle( css.flex.row.reverse.onSmall() ).is( "flex-sm-row-reverse" );
		assertStyle( css.flex.column.onLarge() ).is( "flex-lg-column" );
		assertStyle( css.flex.column.reverse.onMedium() ).is( "flex-md-column-reverse" );
		assertStyle( css.flex.row.onExtraLarge() ).is( "flex-xl-row" );
		assertStyle( css.flex.column.reverse.onExtraLarge() ).is( "flex-xl-column-reverse" );

		// justify content
		assertStyle( css.justifyContent.start ).is( "justify-content-start" );
		assertStyle( css.justifyContent.end ).is( "justify-content-end" );
		assertStyle( css.justifyContent.center ).is( "justify-content-center" );
		assertStyle( css.justifyContent.between ).is( "justify-content-between" );
		assertStyle( css.justifyContent.around ).is( "justify-content-around" );
		assertStyle( css.justifyContent.start.onSmall() ).is( "justify-content-sm-start" );
		assertStyle( css.justifyContent.end.onMedium() ).is( "justify-content-md-end" );
		assertStyle( css.justifyContent.center.onLarge() ).is( "justify-content-lg-center" );
		assertStyle( css.justifyContent.between.onExtraLarge() ).is( "justify-content-xl-between" );
		assertStyle( css.justifyContent.around.onMedium() ).is( "justify-content-md-around" );

		// align items
		assertStyle( css.align.items.start ).is( "align-items-start" );
		assertStyle( css.align.items.end ).is( "align-items-end" );
		assertStyle( css.align.items.center ).is( "align-items-center" );
		assertStyle( css.align.items.baseline ).is( "align-items-baseline" );
		assertStyle( css.align.items.stretch ).is( "align-items-stretch" );
		assertStyle( css.align.items.start.onSmall() ).is( "align-items-sm-start" );
		assertStyle( css.align.items.end.onMedium() ).is( "align-items-md-end" );
		assertStyle( css.align.items.center.onLarge() ).is( "align-items-lg-center" );
		assertStyle( css.align.items.baseline.onExtraLarge() ).is( "align-items-xl-baseline" );

		// align self
		assertStyle( css.align.self.start ).is( "align-self-start" );
		assertStyle( css.align.self.end ).is( "align-self-end" );
		assertStyle( css.align.self.center ).is( "align-self-center" );
		assertStyle( css.align.self.baseline ).is( "align-self-baseline" );
		assertStyle( css.align.self.stretch ).is( "align-self-stretch" );
		assertStyle( css.align.self.start.onSmall() ).is( "align-self-sm-start" );
		assertStyle( css.align.self.end.onMedium() ).is( "align-self-md-end" );
		assertStyle( css.align.self.center.onLarge() ).is( "align-self-lg-center" );
		assertStyle( css.align.self.baseline.onExtraLarge() ).is( "align-self-xl-baseline" );

		// fill
		assertStyle( css.flex.fill ).is( "flex-fill" );
		assertStyle( css.flex.fill.onSmall() ).is( "flex-sm-fill" );
		assertStyle( css.flex.fill.onMedium() ).is( "flex-md-fill" );
		assertStyle( css.flex.fill.onLarge() ).is( "flex-lg-fill" );
		assertStyle( css.flex.fill.onExtraLarge() ).is( "flex-xl-fill" );

		// grow and shrink
		assertStyle( css.flex.grow.enabled ).is( "flex-grow-1" );
		assertStyle( css.flex.grow.disabled ).is( "flex-grow-0" );
		assertStyle( css.flex.shrink.enabled ).is( "flex-shrink-1" );
		assertStyle( css.flex.shrink.disabled ).is( "flex-shrink-0" );
		assertStyle( css.flex.grow.enabled.onSmall() ).is( "flex-sm-grow-1" );
		assertStyle( css.flex.grow.disabled.onMedium() ).is( "flex-md-grow-0" );
		assertStyle( css.flex.shrink.enabled.onLarge() ).is( "flex-lg-shrink-1" );
		assertStyle( css.flex.shrink.disabled.onExtraLarge() ).is( "flex-xl-shrink-0" );

		// wrap
		assertStyle( css.flex.wrap ).is( "flex-wrap" );
		assertStyle( css.flex.wrap.reverse ).is( "flex-wrap-reverse" );
		assertStyle( css.flex.wrap.none ).is( "flex-nowrap" );
		assertStyle( css.flex.nowrap ).is( "flex-nowrap" );
		assertStyle( css.flex.wrap.onSmall() ).is( "flex-sm-wrap" );
		assertStyle( css.flex.wrap.reverse.onMedium() ).is( "flex-md-wrap-reverse" );
		assertStyle( css.flex.wrap.none.onLarge() ).is( "flex-lg-nowrap" );
		assertStyle( css.flex.nowrap.onExtraLarge() ).is( "flex-xl-nowrap" );

		// order
		assertStyle( css.order.first ).is( "order-first" );
		assertStyle( css.order.last ).is( "order-last" );
		assertStyle( css.order.position( 5 ) ).is( "order-5" );
		assertStyle( css.order.first.onSmall() ).is( "order-sm-first" );
		assertStyle( css.order.last.onMedium() ).is( "order-md-last" );
		assertStyle( css.order.position( 11 ).onLarge() ).is( "order-lg-11" );

		// align content
		assertStyle( css.align.content.start ).is( "align-content-start" );
		assertStyle( css.align.content.end ).is( "align-content-end" );
		assertStyle( css.align.content.center ).is( "align-content-center" );
		assertStyle( css.align.content.between ).is( "align-content-between" );
		assertStyle( css.align.content.around ).is( "align-content-around" );
		assertStyle( css.align.content.stretch ).is( "align-content-stretch" );
		assertStyle( css.align.content.start.onSmall() ).is( "align-content-sm-start" );
		assertStyle( css.align.content.end.onMedium() ).is( "align-content-md-end" );
		assertStyle( css.align.content.center.onLarge() ).is( "align-content-lg-center" );
		assertStyle( css.align.content.between.onExtraLarge() ).is( "align-content-xl-between" );
		assertStyle( css.align.content.around.onSmall() ).is( "align-content-sm-around" );
		assertStyle( css.align.content.stretch.onMedium() ).is( "align-content-md-stretch" );
	}

	@Test
	void floatProperty() {
		assertStyle( css.cssFloat.left ).is( "float-left" );
		assertStyle( css.cssFloat.right ).is( "float-right" );
		assertStyle( css.cssFloat.none ).is( "float-none" );
		assertStyle( css.cssFloat.left.onMedium() ).is( "float-md-left" );
		assertStyle( css.cssFloat.right.onLarge() ).is( "float-lg-right" );
		assertStyle( css.cssFloat.none.onExtraLarge() ).is( "float-xl-none" );
	}

	@Test
	void overflow() {
		assertStyle( css.overflow.auto ).is( "overflow-auto" );
		assertStyle( css.overflow.hidden ).is( "overflow-hidden" );
	}

	@Test
	void position() {
		assertStyle( css.position.cssStatic ).is( "position-static" );
		assertStyle( css.position.relative ).is( "position-relative" );
		assertStyle( css.position.absolute ).is( "position-absolute" );
		assertStyle( css.position.fixed ).is( "position-fixed" );
		assertStyle( css.position.sticky ).is( "position-sticky" );

		// fixed top & bottom
		assertStyle( css.fixed.top ).is( "fixed-top" );
		assertStyle( css.fixed.bottom ).is( "fixed-bottom" );

		// sticky top
		assertStyle( css.stickyTop ).is( "sticky-top" );
	}

	@Test
	void screenReader() {
		assertStyle( css.screenReaderOnly ).is( "sr-only" );
		assertStyle( css.screenReaderOnly.focusable ).is( "sr-only-focusable" );
	}

	@Test
	void shadows() {
		assertStyle( css.shadow ).is( "shadow" );
		assertStyle( css.shadow.none ).is( "shadow-none" );
		assertStyle( css.shadow.small ).is( "shadow-sm" );
		assertStyle( css.shadow.large ).is( "shadow-lg" );
	}

	@Test
	void sizing() {
		assertStyle( css.size.width25 ).is( "w-25" );
		assertStyle( css.size.width50 ).is( "w-50" );
		assertStyle( css.size.width75 ).is( "w-75" );
		assertStyle( css.size.width100 ).is( "w-100" );
		assertStyle( css.size.autoWidth ).is( "w-auto" );
		assertStyle( css.size.width( 33 ) ).is( "w-33" );

		assertStyle( css.size.height25 ).is( "h-25" );
		assertStyle( css.size.height50 ).is( "h-50" );
		assertStyle( css.size.height75 ).is( "h-75" );
		assertStyle( css.size.height100 ).is( "h-100" );
		assertStyle( css.size.autoHeight ).is( "h-auto" );
		assertStyle( css.size.height( 33 ) ).is( "h-33" );

		assertStyle( css.size.maxWidth100 ).is( "mw-100" );
		assertStyle( css.size.maxHeight100 ).is( "mh-100" );
	}

	@Test
	void stretchedLink() {
		assertStyle( css.stretchedLink ).is( "stretched-link" );
	}

	@Test
	void text() {
		assertStyle( css.text.justify ).is( "text-justify" );

		// left, right, center
		assertStyle( css.text.left ).is( "text-left" );
		assertStyle( css.text.center ).is( "text-center" );
		assertStyle( css.text.right ).is( "text-right" );
		assertStyle( css.text.left.onSmall() ).is( "text-sm-left" );
		assertStyle( css.text.center.onMedium() ).is( "text-md-center" );
		assertStyle( css.text.right.onLarge() ).is( "text-lg-right" );
		assertStyle( css.text.right.onExtraLarge() ).is( "text-xl-right" );

		// wrap
		assertStyle( css.text.wrap ).is( "text-wrap" );
		assertStyle( css.text.nowrap ).is( "text-nowrap" );
		assertStyle( css.text.wrap.none ).is( "text-nowrap" );

		// truncate
		assertStyle( css.text.truncate ).is( "text-truncate" );

		// word break
		assertStyle( css.text.breakWord ).is( "text-break" );

		// transforms
		assertStyle( css.text.lowerCase ).is( "text-lowercase" );
		assertStyle( css.text.upperCase ).is( "text-uppercase" );
		assertStyle( css.text.capitalize ).is( "text-capitalize" );

		// font weight and italic
		assertStyle( css.font.weight.bold ).is( "font-weight-bold" );
		assertStyle( css.font.weight.bolder ).is( "font-weight-bolder" );
		assertStyle( css.font.weight.normal ).is( "font-weight-normal" );
		assertStyle( css.font.weight.light ).is( "font-weight-light" );
		assertStyle( css.font.weight.lighter ).is( "font-weight-lighter" );
		assertStyle( css.font.italic ).is( "font-italic" );

		// monospace
		assertStyle( css.text.monospace ).is( "text-monospace" );

		// reset color
		assertStyle( css.text.reset ).is( "text-reset" );

		// text decoration
		assertStyle( css.text.noDecoration ).is( "text-decoration-none" );
	}

	@Test
	void verticalAlign() {
		assertStyle( css.align.baseline ).is( "align-baseline" );
		assertStyle( css.align.top ).is( "align-top" );
		assertStyle( css.align.middle ).is( "align-middle" );
		assertStyle( css.align.bottom ).is( "align-bottom" );
		assertStyle( css.align.text.top ).is( "align-text-top" );
		assertStyle( css.align.text.bottom ).is( "align-text-bottom" );
	}

	@Test
	void visibility() {
		assertStyle( css.visible ).is( "visible" );
		assertStyle( css.invisible ).is( "invisible" );
	}

	@Test
	void spacing() {
		// padding
		assertStyle( css.padding.none ).is( "p-0" );
		assertStyle( css.padding.s1 ).is( "p-1" );
		assertStyle( css.padding.s2 ).is( "p-2" );
		assertStyle( css.padding.s3 ).is( "p-3" );
		assertStyle( css.padding.s4 ).is( "p-4" );
		assertStyle( css.padding.s5 ).is( "p-5" );
		assertStyle( css.padding.extraSmall ).is( "p-1" );
		assertStyle( css.padding.small ).is( "p-2" );
		assertStyle( css.padding.medium ).is( "p-3" );
		assertStyle( css.padding.large ).is( "p-4" );
		assertStyle( css.padding.extraLarge ).is( "p-5" );
		assertStyle( css.padding.size( 6 ) ).is( "p-6" );

		assertStyle( css.padding.none.onSmall() ).is( "p-sm-0" );
		assertStyle( css.padding.s1.onLarge() ).is( "p-lg-1" );
		assertStyle( css.padding.extraLarge.onMedium() ).is( "p-md-5" );

		assertStyle( css.padding.top.s1 ).is( "pt-1" );
		assertStyle( css.padding.bottom.none ).is( "pb-0" );
		assertStyle( css.padding.left.medium ).is( "pl-3" );
		assertStyle( css.padding.right.s3 ).is( "pr-3" );
		assertStyle( css.padding.horizontal.large ).is( "px-4" );
		assertStyle( css.padding.vertical.s5 ).is( "py-5" );
		assertStyle( css.padding.vertical.s3.onSmall() ).is( "py-sm-3" );
		assertStyle( css.padding.bottom.none.onMedium() ).is( "pb-md-0" );

		// margin
		assertStyle( css.margin.none ).is( "m-0" );
		assertStyle( css.margin.s1 ).is( "m-1" );
		assertStyle( css.margin.s2 ).is( "m-2" );
		assertStyle( css.margin.s3 ).is( "m-3" );
		assertStyle( css.margin.s4 ).is( "m-4" );
		assertStyle( css.margin.s5 ).is( "m-5" );
		assertStyle( css.margin.extraSmall ).is( "m-1" );
		assertStyle( css.margin.small ).is( "m-2" );
		assertStyle( css.margin.medium ).is( "m-3" );
		assertStyle( css.margin.large ).is( "m-4" );
		assertStyle( css.margin.extraLarge ).is( "m-5" );
		assertStyle( css.margin.size( 6 ) ).is( "m-6" );

		assertStyle( css.margin.none.onSmall() ).is( "m-sm-0" );
		assertStyle( css.margin.s1.onLarge() ).is( "m-lg-1" );
		assertStyle( css.margin.extraLarge.onMedium() ).is( "m-md-5" );

		assertStyle( css.margin.top.s1 ).is( "mt-1" );
		assertStyle( css.margin.bottom.none ).is( "mb-0" );
		assertStyle( css.margin.left.medium ).is( "ml-3" );
		assertStyle( css.margin.right.s3 ).is( "mr-3" );
		assertStyle( css.margin.horizontal.large ).is( "mx-4" );
		assertStyle( css.margin.vertical.s5 ).is( "my-5" );
		assertStyle( css.margin.vertical.s3.onSmall() ).is( "my-sm-3" );
		assertStyle( css.margin.bottom.none.onMedium() ).is( "mb-md-0" );

		assertStyle( css.margin.negative.medium ).is( "m-n3" );
		assertStyle( css.margin.top.negative.s1 ).is( "mt-n1" );
		assertStyle( css.margin.vertical.negative.s2.onMedium() ).is( "my-md-n2" );
		assertStyle( css.margin.horizontal.auto ).is( "mx-auto" );
		assertStyle( css.margin.horizontal.auto.onLarge() ).is( "mx-lg-auto" );
	}

	private StyleMatcher assertStyle( @NonNull BootstrapStyleRule rule ) {
		return new StyleMatcher( rule );
	}

	@RequiredArgsConstructor
	static class StyleMatcher
	{
		private final BootstrapStyleRule o;

		public StyleMatcher is( String... css ) {
			assertThat( o.toCssClasses() ).containsExactly( css );
			return this;
		}
	}
}