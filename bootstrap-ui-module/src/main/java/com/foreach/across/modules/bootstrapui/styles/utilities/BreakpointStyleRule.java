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

package com.foreach.across.modules.bootstrapui.styles.utilities;

import com.foreach.across.modules.bootstrapui.styles.BootstrapStyleRule;

/**
 * Interface for adding the responsive breakpoints in a css class name.
 * Fluent naming to indicate a breakpoint means that class is active on
 * all devices <strong>starting from</strong> that breakpoint.
 * <p/>
 * For example: {@link #onSmallAndUp()} means that class will have impact on
 * all devices that match the small media query. As this is defined by a minimum
 * width, larger devices will match as well.
 * <p/>
 * See the Bootstrap documentation for details on the responsive utilities.
 *
 * @author Arne Vandamme
 * @since 2.3.0
 */
public interface BreakpointStyleRule extends BootstrapStyleRule
{
	default BootstrapStyleRule onSmallAndUp() {
		return on( "sm" );
	}

	default BootstrapStyleRule onMediumAndUp() {
		return on( "md" );
	}

	default BootstrapStyleRule onLargeAndUp() {
		return on( "lg" );
	}

	default BootstrapStyleRule onExtraLargeAndUp() {
		return on( "xl" );
	}

	BootstrapStyleRule on( String breakpoint );
}
