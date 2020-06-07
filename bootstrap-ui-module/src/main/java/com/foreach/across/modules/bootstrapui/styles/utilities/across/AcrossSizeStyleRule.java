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
import lombok.NonNull;

/**
 * https://getbootstrap.com/docs/4.3/utilities/sizing/
 *
 * @author Arne Vandamme * @author Steven Gentens * @since 3.0.0
 */
public class AcrossSizeStyleRule
{
	public final BootstrapStyleRule width25 = width( 25 );
	public final BootstrapStyleRule width50 = width( 50 );
	public final BootstrapStyleRule width75 = width( 75 );
	public final BootstrapStyleRule width100 = width( 100 );
	public final BootstrapStyleRule autoWidth = width( "auto" );

	public final BootstrapStyleRule height25 = height( 25 );
	public final BootstrapStyleRule height50 = height( 50 );
	public final BootstrapStyleRule height75 = height( 75 );
	public final BootstrapStyleRule height100 = height( 100 );
	public final BootstrapStyleRule autoHeight = height( "auto" );

	public final BootstrapStyleRule maxWidth100 = AcrossStyleRule.of( BootstrapStyles.css.size.maxWidth100 );
	public final BootstrapStyleRule maxHeight100 = AcrossStyleRule.of( BootstrapStyles.css.size.maxHeight100 );

	public BootstrapStyleRule width( int size ) {
		return width( "" + size );
	}

	public BootstrapStyleRule width( @NonNull String size ) {
		return AcrossStyleRule.of( BootstrapStyles.css.size.width( size ) );
	}

	public BootstrapStyleRule height( int size ) {
		return height( "" + size );
	}

	@SuppressWarnings("WeakerAccess")
	public BootstrapStyleRule height( @NonNull String size ) {
		return AcrossStyleRule.of( BootstrapStyles.css.size.height( size ) );
	}
}
