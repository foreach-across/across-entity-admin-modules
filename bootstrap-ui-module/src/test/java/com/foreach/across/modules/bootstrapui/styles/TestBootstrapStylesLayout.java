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

import org.junit.jupiter.api.Test;

import static com.foreach.across.modules.bootstrapui.styles.BootstrapStyles.css;
import static com.foreach.across.modules.bootstrapui.styles.TestBootstrapStylesUtilities.assertStyle;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * https://getbootstrap.com/docs/4.3/layout/overview/
 *
 * @author Arne Vandamme
 * @since 2.3.0
 */
class TestBootstrapStylesLayout
{
	@Test
	void grid() {
		assertStyle( css.grid.container ).is( "container" );
		assertStyle( css.grid.container.fluid ).is( "container-fluid" );
		assertStyle( css.grid.row ).is( "row" );
		assertStyle( css.grid.row.noGutters ).is( "row", "no-gutters" );

		// column
		assertStyle( css.grid.column ).is( "col" );
		assertStyle( css.grid.column.onSmallAndUp() ).is( "col-sm" );
		assertStyle( css.grid.column.onMediumAndUp() ).is( "col-md" );
		assertStyle( css.grid.column.onLargeAndUp() ).is( "col-lg" );
		assertStyle( css.grid.column.onExtraLargeAndUp() ).is( "col-xl" );
		assertStyle( css.grid.column.w1 ).is( "col-1" );
		assertStyle( css.grid.column.w2 ).is( "col-2" );
		assertStyle( css.grid.column.w3 ).is( "col-3" );
		assertStyle( css.grid.column.w4 ).is( "col-4" );
		assertStyle( css.grid.column.w5 ).is( "col-5" );
		assertStyle( css.grid.column.w6 ).is( "col-6" );
		assertStyle( css.grid.column.w7 ).is( "col-7" );
		assertStyle( css.grid.column.w8 ).is( "col-8" );
		assertStyle( css.grid.column.w9 ).is( "col-9" );
		assertStyle( css.grid.column.w10 ).is( "col-10" );
		assertStyle( css.grid.column.w11 ).is( "col-11" );
		assertStyle( css.grid.column.w12 ).is( "col-12" );
		assertStyle( css.grid.column.width( 7 ) ).is( "col-7" );
		assertStyle( css.grid.column.w3.onMediumAndUp() ).is( "col-md-3" );
		assertStyle( css.grid.column.w12.onExtraLargeAndUp() ).is( "col-xl-12" );
		assertStyle( css.grid.column.auto ).is( "col-auto" );
		assertThat( css.grid.column1 ).isSameAs( css.grid.column.w1 );
		assertThat( css.grid.column2 ).isSameAs( css.grid.column.w2 );
		assertThat( css.grid.column3 ).isSameAs( css.grid.column.w3 );
		assertThat( css.grid.column4 ).isSameAs( css.grid.column.w4 );
		assertThat( css.grid.column5 ).isSameAs( css.grid.column.w5 );
		assertThat( css.grid.column6 ).isSameAs( css.grid.column.w6 );
		assertThat( css.grid.column7 ).isSameAs( css.grid.column.w7 );
		assertThat( css.grid.column8 ).isSameAs( css.grid.column.w8 );
		assertThat( css.grid.column9 ).isSameAs( css.grid.column.w9 );
		assertThat( css.grid.column10 ).isSameAs( css.grid.column.w10 );
		assertThat( css.grid.column11 ).isSameAs( css.grid.column.w11 );
		assertThat( css.grid.column12 ).isSameAs( css.grid.column.w12 );

		// offset
		assertStyle( css.grid.column.offset0 ).is( "offset-0" );
		assertStyle( css.grid.column.noOffset ).is( "offset-0" );
		assertStyle( css.grid.column.offset1 ).is( "offset-1" );
		assertStyle( css.grid.column.offset2 ).is( "offset-2" );
		assertStyle( css.grid.column.offset3 ).is( "offset-3" );
		assertStyle( css.grid.column.offset4 ).is( "offset-4" );
		assertStyle( css.grid.column.offset5 ).is( "offset-5" );
		assertStyle( css.grid.column.offset6 ).is( "offset-6" );
		assertStyle( css.grid.column.offset7 ).is( "offset-7" );
		assertStyle( css.grid.column.offset8 ).is( "offset-8" );
		assertStyle( css.grid.column.offset9 ).is( "offset-9" );
		assertStyle( css.grid.column.offset10 ).is( "offset-10" );
		assertStyle( css.grid.column.offset11 ).is( "offset-11" );
		assertStyle( css.grid.column.offset12 ).is( "offset-12" );
		assertStyle( css.grid.column.offset0.onMediumAndUp() ).is( "offset-md-0" );
		assertStyle( css.grid.column.noOffset.onLargeAndUp() ).is( "offset-lg-0" );
		assertStyle( css.grid.column.offset7.onExtraLargeAndUp() ).is( "offset-xl-7" );
		assertStyle( css.grid.column.offset( 3 ).onSmallAndUp() ).is( "offset-sm-3" );
	}
}