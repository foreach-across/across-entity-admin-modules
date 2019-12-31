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

/**
 * See https://getbootstrap.com/docs/4.3/content/
 *
 * @author Arne Vandamme
 * @since 3.0.0
 */
class TestBootstrapStylesContent
{
	@Test
	void typography() {
		assertStyle( css.heading.h1 ).is( "h1" );
		assertStyle( css.heading.h2 ).is( "h2" );
		assertStyle( css.heading.h3 ).is( "h3" );
		assertStyle( css.heading.h4 ).is( "h4" );
		assertStyle( css.heading.h5 ).is( "h5" );
		assertStyle( css.heading.h6 ).is( "h6" );

		// display heading
		assertStyle( css.heading.display1 ).is( "display-1" );
		assertStyle( css.heading.display2 ).is( "display-2" );
		assertStyle( css.heading.display3 ).is( "display-3" );
		assertStyle( css.heading.display4 ).is( "display-4" );

		// inline text elements
		assertStyle( css.mark ).is( "mark" );
		assertStyle( css.small ).is( "small" );

		// abbreviations
		assertStyle( css.initialism ).is( "initialism" );

		// blockquote
		assertStyle( css.blockQuote ).is( "blockquote" );
		assertStyle( css.blockQuote.footer ).is( "blockquote-footer" );

		// lists
		assertStyle( css.list.unstyled ).is( "list-unstyled" );
		assertStyle( css.list.inline ).is( "list-inline" );
		assertStyle( css.list.inline.item ).is( "list-inline-item" );
	}

	@Test
	void images() {
		assertStyle( css.image.fluid ).is( "img-fluid" );
		assertStyle( css.image.thumbnail ).is( "img-thumbnail" );
	}

	@Test
	void table() {
		assertStyle( css.table ).is( "table" );
		assertStyle( css.table.head.dark ).is( "thead-dark" );
		assertStyle( css.table.head.light ).is( "thead-light" );
		assertStyle( css.table.striped ).is( "table", "table-striped" ).removes( "table-striped" );
		assertStyle( css.table.bordered ).is( "table", "table-bordered" ).removes( "table-bordered" );
		assertStyle( css.table.borderless ).is( "table", "table-borderless" ).removes( "table-borderless" );
		assertStyle( css.table.hover ).is( "table", "table-hover" ).removes( "table-hover" );
		assertStyle( css.table.small ).is( "table", "table-sm" ).removes( "table-sm" );

		// contextual classes
		assertStyle( css.table.active ).is( "table-active" );
		assertStyle( css.table.primary ).is( "table-primary" );
		assertStyle( css.table.secondary ).is( "table-secondary" );
		assertStyle( css.table.success ).is( "table-success" );
		assertStyle( css.table.danger ).is( "table-danger" );
		assertStyle( css.table.warning ).is( "table-warning" );
		assertStyle( css.table.info ).is( "table-info" );
		assertStyle( css.table.light ).is( "table-light" );
		assertStyle( css.table.dark ).is( "table-dark" );

		// reponsive
		assertStyle( css.table.reponsive ).is( "table-responsive" );
		assertStyle( css.table.reponsive.onSmallAndUp() ).is( "table-responsive-sm" );
		assertStyle( css.table.reponsive.onMediumAndUp() ).is( "table-responsive-md" );
		assertStyle( css.table.reponsive.onLargeAndUp() ).is( "table-responsive-lg" );
		assertStyle( css.table.reponsive.onExtraLargeAndUp() ).is( "table-responsive-xl" );
	}

	@Test
	void figure() {
		assertStyle( css.figure ).is( "figure" );
		assertStyle( css.figure.image ).is( "figure-img" );
		assertStyle( css.figure.caption ).is( "figure-caption" );
	}
}