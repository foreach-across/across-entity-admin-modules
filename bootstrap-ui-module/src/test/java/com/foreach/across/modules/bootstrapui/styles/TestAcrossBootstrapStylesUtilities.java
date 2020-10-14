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

import com.foreach.across.modules.web.ui.elements.HtmlViewElement;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;

import static com.foreach.across.modules.bootstrapui.styles.AcrossBootstrapStyles.css;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * See https://getbootstrap.com/docs/4.3/utilities/
 *
 * @author Steven Gentens
 * @since 3.0.0
 */
class TestAcrossBootstrapStylesUtilities
{
	@Test
	void borders() {
		// additive
		assertStyle( css.border ).is( "axu-border" );
		assertStyle( css.border.top ).is( "axu-border-top" );
		assertStyle( css.border.right ).is( "axu-border-right" );
		assertStyle( css.border.bottom ).is( "axu-border-bottom" );
		assertStyle( css.border.left ).is( "axu-border-left" );

		// subtractive
		assertStyle( css.border.none ).is( "axu-border-0" );
		assertStyle( css.border.top.none ).is( "axu-border-top-0" );
		assertStyle( css.border.right.none ).is( "axu-border-right-0" );
		assertStyle( css.border.bottom.none ).is( "axu-border-bottom-0" );
		assertStyle( css.border.left.none ).is( "axu-border-left-0" );

		// color
		assertStyle( css.border.primary ).is( "axu-border-primary" );
		assertStyle( css.border.secondary ).is( "axu-border-secondary" );
		assertStyle( css.border.success ).is( "axu-border-success" );
		assertStyle( css.border.danger ).is( "axu-border-danger" );
		assertStyle( css.border.warning ).is( "axu-border-warning" );
		assertStyle( css.border.info ).is( "axu-border-info" );
		assertStyle( css.border.light ).is( "axu-border-light" );
		assertStyle( css.border.dark ).is( "axu-border-dark" );
		assertStyle( css.border.white ).is( "axu-border-white" );

		// radius
		assertStyle( css.rounded ).is( "axu-rounded" );
		assertStyle( css.rounded.top ).is( "axu-rounded-top" );
		assertStyle( css.rounded.right ).is( "axu-rounded-right" );
		assertStyle( css.rounded.bottom ).is( "axu-rounded-bottom" );
		assertStyle( css.rounded.left ).is( "axu-rounded-left" );
		assertStyle( css.rounded.circle ).is( "axu-rounded-circle" );
		assertStyle( css.rounded.pill ).is( "axu-rounded-pill" );
		assertStyle( css.rounded.none ).is( "axu-rounded-0" );

		// sizes
		assertStyle( css.rounded.small ).is( "axu-rounded-sm" );
		assertStyle( css.rounded.large ).is( "axu-rounded-lg" );
	}

	@Test
	void colors() {
		// text color
		assertStyle( css.text.primary ).is( "axu-text-primary" );
		assertStyle( css.text.secondary ).is( "axu-text-secondary" );
		assertStyle( css.text.success ).is( "axu-text-success" );
		assertStyle( css.text.danger ).is( "axu-text-danger" );
		assertStyle( css.text.warning ).is( "axu-text-warning" );
		assertStyle( css.text.info ).is( "axu-text-info" );
		assertStyle( css.text.light ).is( "axu-text-light" );
		assertStyle( css.text.dark ).is( "axu-text-dark" );
		assertStyle( css.text.white ).is( "axu-text-white" );
		assertStyle( css.text.body ).is( "axu-text-body" );
		assertStyle( css.text.muted ).is( "axu-text-muted" );

		// background color
		assertStyle( css.background.primary ).is( "axu-bg-primary" );
		assertStyle( css.background.secondary ).is( "axu-bg-secondary" );
		assertStyle( css.background.success ).is( "axu-bg-success" );
		assertStyle( css.background.danger ).is( "axu-bg-danger" );
		assertStyle( css.background.warning ).is( "axu-bg-warning" );
		assertStyle( css.background.info ).is( "axu-bg-info" );
		assertStyle( css.background.light ).is( "axu-bg-light" );
		assertStyle( css.background.dark ).is( "axu-bg-dark" );
		assertStyle( css.background.white ).is( "axu-bg-white" );
		assertStyle( css.background.transparent ).is( "axu-bg-transparent" );

		// background gradient
		assertStyle( css.background.gradient.primary ).is( "axu-bg-gradient-primary" );
		assertStyle( css.background.gradient.secondary ).is( "axu-bg-gradient-secondary" );
		assertStyle( css.background.gradient.success ).is( "axu-bg-gradient-success" );
		assertStyle( css.background.gradient.danger ).is( "axu-bg-gradient-danger" );
		assertStyle( css.background.gradient.warning ).is( "axu-bg-gradient-warning" );
		assertStyle( css.background.gradient.info ).is( "axu-bg-gradient-info" );
		assertStyle( css.background.gradient.light ).is( "axu-bg-gradient-light" );
		assertStyle( css.background.gradient.dark ).is( "axu-bg-gradient-dark" );
		assertStyle( css.background.gradient.white ).is( "axu-bg-gradient-white" );
	}

	@Test
	void display() {
		assertStyle( css.display.none ).is( "axu-d-none" );
		assertStyle( css.display.inline ).is( "axu-d-inline" );
		assertStyle( css.display.inlineBlock ).is( "axu-d-inline-block" );
		assertStyle( css.display.block ).is( "axu-d-block" );
		assertStyle( css.display.table ).is( "axu-d-table" );
		assertStyle( css.display.tableCell ).is( "axu-d-table-cell" );
		assertStyle( css.display.tableRow ).is( "axu-d-table-row" );
		assertStyle( css.display.flex ).is( "axu-d-flex" );
		assertStyle( css.display.inlineFlex ).is( "axu-d-inline-flex" );

		assertStyle( css.display.none.onSmallAndUp() ).is( "axu-d-sm-none" );
		assertStyle( css.display.inline.onSmallAndUp() ).is( "axu-d-sm-inline" );
		assertStyle( css.display.inlineBlock.onMediumAndUp() ).is( "axu-d-md-inline-block" );
		assertStyle( css.display.block.onMediumAndUp() ).is( "axu-d-md-block" );
		assertStyle( css.display.table.onLargeAndUp() ).is( "axu-d-lg-table" );
		assertStyle( css.display.tableCell.onLargeAndUp() ).is( "axu-d-lg-table-cell" );
		assertStyle( css.display.tableRow.onExtraLargeAndUp() ).is( "axu-d-xl-table-row" );
		assertStyle( css.display.flex.onExtraLargeAndUp() ).is( "axu-d-xl-flex" );
		assertStyle( css.display.inlineFlex.onExtraLargeAndUp() ).is( "axu-d-xl-inline-flex" );

		// print
		assertStyle( css.display.none.onPrint() ).is( "axu-d-print-none" );
		assertStyle( css.display.inline.onPrint() ).is( "axu-d-print-inline" );
	}

	@Test
	void flex() {
		assertStyle( css.flex.row ).is( "axu-flex-row" );
		assertStyle( css.flex.row.reverse ).is( "axu-flex-row-reverse" );
		assertStyle( css.flex.row.onSmallAndUp() ).is( "axu-flex-sm-row" );
		assertStyle( css.flex.row.reverse.onSmallAndUp() ).is( "axu-flex-sm-row-reverse" );
		assertStyle( css.flex.column.onLargeAndUp() ).is( "axu-flex-lg-column" );
		assertStyle( css.flex.column.reverse.onMediumAndUp() ).is( "axu-flex-md-column-reverse" );
		assertStyle( css.flex.row.onExtraLargeAndUp() ).is( "axu-flex-xl-row" );
		assertStyle( css.flex.column.reverse.onExtraLargeAndUp() ).is( "axu-flex-xl-column-reverse" );

		// justify content
		assertStyle( css.justifyContent.start ).is( "axu-justify-content-start" );
		assertStyle( css.justifyContent.end ).is( "axu-justify-content-end" );
		assertStyle( css.justifyContent.center ).is( "axu-justify-content-center" );
		assertStyle( css.justifyContent.between ).is( "axu-justify-content-between" );
		assertStyle( css.justifyContent.around ).is( "axu-justify-content-around" );
		assertStyle( css.justifyContent.start.onSmallAndUp() ).is( "axu-justify-content-sm-start" );
		assertStyle( css.justifyContent.end.onMediumAndUp() ).is( "axu-justify-content-md-end" );
		assertStyle( css.justifyContent.center.onLargeAndUp() ).is( "axu-justify-content-lg-center" );
		assertStyle( css.justifyContent.between.onExtraLargeAndUp() ).is( "axu-justify-content-xl-between" );
		assertStyle( css.justifyContent.around.onMediumAndUp() ).is( "axu-justify-content-md-around" );

		// align items
		assertStyle( css.align.items.start ).is( "axu-align-items-start" );
		assertStyle( css.align.items.end ).is( "axu-align-items-end" );
		assertStyle( css.align.items.center ).is( "axu-align-items-center" );
		assertStyle( css.align.items.baseline ).is( "axu-align-items-baseline" );
		assertStyle( css.align.items.stretch ).is( "axu-align-items-stretch" );
		assertStyle( css.align.items.start.onSmallAndUp() ).is( "axu-align-items-sm-start" );
		assertStyle( css.align.items.end.onMediumAndUp() ).is( "axu-align-items-md-end" );
		assertStyle( css.align.items.center.onLargeAndUp() ).is( "axu-align-items-lg-center" );
		assertStyle( css.align.items.baseline.onExtraLargeAndUp() ).is( "axu-align-items-xl-baseline" );

		// align self
		assertStyle( css.align.self.start ).is( "axu-align-self-start" );
		assertStyle( css.align.self.end ).is( "axu-align-self-end" );
		assertStyle( css.align.self.center ).is( "axu-align-self-center" );
		assertStyle( css.align.self.baseline ).is( "axu-align-self-baseline" );
		assertStyle( css.align.self.stretch ).is( "axu-align-self-stretch" );
		assertStyle( css.align.self.start.onSmallAndUp() ).is( "axu-align-self-sm-start" );
		assertStyle( css.align.self.end.onMediumAndUp() ).is( "axu-align-self-md-end" );
		assertStyle( css.align.self.center.onLargeAndUp() ).is( "axu-align-self-lg-center" );
		assertStyle( css.align.self.baseline.onExtraLargeAndUp() ).is( "axu-align-self-xl-baseline" );

		// fill
		assertStyle( css.flex.fill ).is( "axu-flex-fill" );
		assertStyle( css.flex.fill.onSmallAndUp() ).is( "axu-flex-sm-fill" );
		assertStyle( css.flex.fill.onMediumAndUp() ).is( "axu-flex-md-fill" );
		assertStyle( css.flex.fill.onLargeAndUp() ).is( "axu-flex-lg-fill" );
		assertStyle( css.flex.fill.onExtraLargeAndUp() ).is( "axu-flex-xl-fill" );

		// grow and shrink
		assertStyle( css.flex.grow.enabled ).is( "axu-flex-grow-1" );
		assertStyle( css.flex.grow.disabled ).is( "axu-flex-grow-0" );
		assertStyle( css.flex.shrink.enabled ).is( "axu-flex-shrink-1" );
		assertStyle( css.flex.shrink.disabled ).is( "axu-flex-shrink-0" );
		assertStyle( css.flex.grow.enabled.onSmallAndUp() ).is( "axu-flex-sm-grow-1" );
		assertStyle( css.flex.grow.disabled.onMediumAndUp() ).is( "axu-flex-md-grow-0" );
		assertStyle( css.flex.shrink.enabled.onLargeAndUp() ).is( "axu-flex-lg-shrink-1" );
		assertStyle( css.flex.shrink.disabled.onExtraLargeAndUp() ).is( "axu-flex-xl-shrink-0" );

		// wrap
		assertStyle( css.flex.wrap ).is( "axu-flex-wrap" );
		assertStyle( css.flex.wrap.reverse ).is( "axu-flex-wrap-reverse" );
		assertStyle( css.flex.wrap.none ).is( "axu-flex-nowrap" );
		assertStyle( css.flex.nowrap ).is( "axu-flex-nowrap" );
		assertStyle( css.flex.wrap.onSmallAndUp() ).is( "axu-flex-sm-wrap" );
		assertStyle( css.flex.wrap.reverse.onMediumAndUp() ).is( "axu-flex-md-wrap-reverse" );
		assertStyle( css.flex.wrap.none.onLargeAndUp() ).is( "axu-flex-lg-nowrap" );
		assertStyle( css.flex.nowrap.onExtraLargeAndUp() ).is( "axu-flex-xl-nowrap" );

		// order
		assertStyle( css.order.first ).is( "axu-order-first" );
		assertStyle( css.order.last ).is( "axu-order-last" );
		assertStyle( css.order.position( 5 ) ).is( "axu-order-5" );
		assertStyle( css.order.first.onSmallAndUp() ).is( "axu-order-sm-first" );
		assertStyle( css.order.last.onMediumAndUp() ).is( "axu-order-md-last" );
		assertStyle( css.order.position( 11 ).onLargeAndUp() ).is( "axu-order-lg-11" );

		// align content
		assertStyle( css.align.content.start ).is( "axu-align-content-start" );
		assertStyle( css.align.content.end ).is( "axu-align-content-end" );
		assertStyle( css.align.content.center ).is( "axu-align-content-center" );
		assertStyle( css.align.content.between ).is( "axu-align-content-between" );
		assertStyle( css.align.content.around ).is( "axu-align-content-around" );
		assertStyle( css.align.content.stretch ).is( "axu-align-content-stretch" );
		assertStyle( css.align.content.start.onSmallAndUp() ).is( "axu-align-content-sm-start" );
		assertStyle( css.align.content.end.onMediumAndUp() ).is( "axu-align-content-md-end" );
		assertStyle( css.align.content.center.onLargeAndUp() ).is( "axu-align-content-lg-center" );
		assertStyle( css.align.content.between.onExtraLargeAndUp() ).is( "axu-align-content-xl-between" );
		assertStyle( css.align.content.around.onSmallAndUp() ).is( "axu-align-content-sm-around" );
		assertStyle( css.align.content.stretch.onMediumAndUp() ).is( "axu-align-content-md-stretch" );
	}

	@Test
	void floatProperty() {
		assertStyle( css.cssFloat.left ).is( "axu-float-left" );
		assertStyle( css.cssFloat.right ).is( "axu-float-right" );
		assertStyle( css.cssFloat.none ).is( "axu-float-none" );
		assertStyle( css.cssFloat.left.onMediumAndUp() ).is( "axu-float-md-left" );
		assertStyle( css.cssFloat.right.onLargeAndUp() ).is( "axu-float-lg-right" );
		assertStyle( css.cssFloat.none.onExtraLargeAndUp() ).is( "axu-float-xl-none" );
	}

	@Test
	void overflow() {
		assertStyle( css.overflow.auto ).is( "axu-overflow-auto" );
		assertStyle( css.overflow.hidden ).is( "axu-overflow-hidden" );
	}

	@Test
	void position() {
		assertStyle( css.position.cssStatic ).is( "axu-position-static" );
		assertStyle( css.position.relative ).is( "axu-position-relative" );
		assertStyle( css.position.absolute ).is( "axu-position-absolute" );
		assertStyle( css.position.fixed ).is( "axu-position-fixed" );
		assertStyle( css.position.sticky ).is( "axu-position-sticky" );
	}

	@Test
	void shadows() {
		assertStyle( css.shadow ).is( "axu-shadow" );
		assertStyle( css.shadow.none ).is( "axu-shadow-none" );
		assertStyle( css.shadow.small ).is( "axu-shadow-sm" );
		assertStyle( css.shadow.large ).is( "axu-shadow-lg" );
	}

	@Test
	void sizing() {
		assertStyle( css.size.width25 ).is( "axu-w-25" );
		assertStyle( css.size.width50 ).is( "axu-w-50" );
		assertStyle( css.size.width75 ).is( "axu-w-75" );
		assertStyle( css.size.width100 ).is( "axu-w-100" );
		assertStyle( css.size.autoWidth ).is( "axu-w-auto" );
		assertStyle( css.size.width( 33 ) ).is( "axu-w-33" );

		assertStyle( css.size.height25 ).is( "axu-h-25" );
		assertStyle( css.size.height50 ).is( "axu-h-50" );
		assertStyle( css.size.height75 ).is( "axu-h-75" );
		assertStyle( css.size.height100 ).is( "axu-h-100" );
		assertStyle( css.size.autoHeight ).is( "axu-h-auto" );
		assertStyle( css.size.height( 33 ) ).is( "axu-h-33" );

		assertStyle( css.size.maxWidth100 ).is( "axu-mw-100" );
		assertStyle( css.size.maxHeight100 ).is( "axu-mh-100" );
	}

	@Test
	void text() {
		assertStyle( css.text.justify ).is( "axu-text-justify" );

		// left, right, center
		assertStyle( css.text.left ).is( "axu-text-left" );
		assertStyle( css.text.center ).is( "axu-text-center" );
		assertStyle( css.text.right ).is( "axu-text-right" );
		assertStyle( css.text.left.onSmallAndUp() ).is( "axu-text-sm-left" );
		assertStyle( css.text.center.onMediumAndUp() ).is( "axu-text-md-center" );
		assertStyle( css.text.right.onLargeAndUp() ).is( "axu-text-lg-right" );
		assertStyle( css.text.right.onExtraLargeAndUp() ).is( "axu-text-xl-right" );

		// wrap
		assertStyle( css.text.wrap ).is( "axu-text-wrap" );
		assertStyle( css.text.nowrap ).is( "axu-text-nowrap" );
		assertStyle( css.text.wrap.none ).is( "axu-text-nowrap" );

		// truncate
		assertStyle( css.text.truncate ).is( "axu-text-truncate" );

		// word break
		assertStyle( css.text.breakWord ).is( "axu-text-break" );

		// transforms
		assertStyle( css.text.lowerCase ).is( "axu-text-lowercase" );
		assertStyle( css.text.upperCase ).is( "axu-text-uppercase" );
		assertStyle( css.text.capitalize ).is( "axu-text-capitalize" );

		// font weight and italic
		assertStyle( css.font.weight.bold ).is( "axu-font-weight-bold" );
		assertStyle( css.font.weight.bolder ).is( "axu-font-weight-bolder" );
		assertStyle( css.font.weight.normal ).is( "axu-font-weight-normal" );
		assertStyle( css.font.weight.light ).is( "axu-font-weight-light" );
		assertStyle( css.font.weight.lighter ).is( "axu-font-weight-lighter" );
		assertStyle( css.font.italic ).is( "axu-font-italic" );

		// monospace
		assertStyle( css.text.monospace ).is( "axu-text-monospace" );

		// reset color
		assertStyle( css.text.reset ).is( "axu-text-reset" );

		// text decoration
		assertStyle( css.text.noDecoration ).is( "axu-text-decoration-none" );
	}

	@Test
	void verticalAlign() {
		assertStyle( css.align.baseline ).is( "axu-align-baseline" );
		assertStyle( css.align.top ).is( "axu-align-top" );
		assertStyle( css.align.middle ).is( "axu-align-middle" );
		assertStyle( css.align.bottom ).is( "axu-align-bottom" );
		assertStyle( css.align.text.top ).is( "axu-align-text-top" );
		assertStyle( css.align.text.bottom ).is( "axu-align-text-bottom" );
	}

	@Test
	void visibility() {
		assertStyle( css.visible ).is( "axu-visible" );
		assertStyle( css.invisible ).is( "axu-invisible" );
	}

	@Test
	void spacing() {
		// padding
		assertStyle( css.padding.none ).is( "axu-p-0" );
		assertStyle( css.padding.s1 ).is( "axu-p-1" );
		assertStyle( css.padding.s2 ).is( "axu-p-2" );
		assertStyle( css.padding.s3 ).is( "axu-p-3" );
		assertStyle( css.padding.s4 ).is( "axu-p-4" );
		assertStyle( css.padding.s5 ).is( "axu-p-5" );
		assertStyle( css.padding.extraSmall ).is( "axu-p-1" );
		assertStyle( css.padding.small ).is( "axu-p-2" );
		assertStyle( css.padding.medium ).is( "axu-p-3" );
		assertStyle( css.padding.large ).is( "axu-p-4" );
		assertStyle( css.padding.extraLarge ).is( "axu-p-5" );
		assertStyle( css.padding.size( 6 ) ).is( "axu-p-6" );

		assertStyle( css.padding.none.onSmallAndUp() ).is( "axu-p-sm-0" );
		assertStyle( css.padding.s1.onLargeAndUp() ).is( "axu-p-lg-1" );
		assertStyle( css.padding.extraLarge.onMediumAndUp() ).is( "axu-p-md-5" );

		assertStyle( css.padding.top.s1 ).is( "axu-pt-1" );
		assertStyle( css.padding.bottom.none ).is( "axu-pb-0" );
		assertStyle( css.padding.left.medium ).is( "axu-pl-3" );
		assertStyle( css.padding.right.s3 ).is( "axu-pr-3" );
		assertStyle( css.padding.horizontal.large ).is( "axu-px-4" );
		assertStyle( css.padding.vertical.s5 ).is( "axu-py-5" );
		assertStyle( css.padding.vertical.s3.onSmallAndUp() ).is( "axu-py-sm-3" );
		assertStyle( css.padding.bottom.none.onMediumAndUp() ).is( "axu-pb-md-0" );

		// margin
		assertStyle( css.margin.none ).is( "axu-m-0" );
		assertStyle( css.margin.s1 ).is( "axu-m-1" );
		assertStyle( css.margin.s2 ).is( "axu-m-2" );
		assertStyle( css.margin.s3 ).is( "axu-m-3" );
		assertStyle( css.margin.s4 ).is( "axu-m-4" );
		assertStyle( css.margin.s5 ).is( "axu-m-5" );
		assertStyle( css.margin.extraSmall ).is( "axu-m-1" );
		assertStyle( css.margin.small ).is( "axu-m-2" );
		assertStyle( css.margin.medium ).is( "axu-m-3" );
		assertStyle( css.margin.large ).is( "axu-m-4" );
		assertStyle( css.margin.extraLarge ).is( "axu-m-5" );
		assertStyle( css.margin.size( 6 ) ).is( "axu-m-6" );

		assertStyle( css.margin.none.onSmallAndUp() ).is( "axu-m-sm-0" );
		assertStyle( css.margin.s1.onLargeAndUp() ).is( "axu-m-lg-1" );
		assertStyle( css.margin.extraLarge.onMediumAndUp() ).is( "axu-m-md-5" );

		assertStyle( css.margin.top.s1 ).is( "axu-mt-1" );
		assertStyle( css.margin.bottom.none ).is( "axu-mb-0" );
		assertStyle( css.margin.left.medium ).is( "axu-ml-3" );
		assertStyle( css.margin.right.s3 ).is( "axu-mr-3" );
		assertStyle( css.margin.horizontal.large ).is( "axu-mx-4" );
		assertStyle( css.margin.vertical.s5 ).is( "axu-my-5" );
		assertStyle( css.margin.vertical.s3.onSmallAndUp() ).is( "axu-my-sm-3" );
		assertStyle( css.margin.bottom.none.onMediumAndUp() ).is( "axu-mb-md-0" );

		assertStyle( css.margin.negative.medium ).is( "axu-m-n3" );
		assertStyle( css.margin.top.negative.s1 ).is( "axu-mt-n1" );
		assertStyle( css.margin.vertical.negative.s2.onMediumAndUp() ).is( "axu-my-md-n2" );

		assertStyle( css.margin.auto ).is( "axu-m-auto" );
		assertStyle( css.margin.auto.onLargeAndUp() ).is( "axu-m-lg-auto" );
		assertStyle( css.margin.top.auto ).is( "axu-mt-auto" );
		assertStyle( css.margin.right.auto.onMediumAndUp() ).is( "axu-mr-md-auto" );
		assertStyle( css.margin.horizontal.auto ).is( "axu-mx-auto" );
		assertStyle( css.margin.horizontal.auto.onLargeAndUp() ).is( "axu-mx-lg-auto" );
	}

	static StyleMatcher assertStyle( @NonNull BootstrapStyleRule rule ) {
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

		public StyleMatcher removes( String... css ) {
			HtmlViewElement html = mock( HtmlViewElement.class );
			o.removeFrom( html );
			verify( html ).removeCssClass( css );
			return this;
		}
	}
}
