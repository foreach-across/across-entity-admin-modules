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

/**
 * @author Stijn Vanhoof
 */

import com.foreach.across.modules.bootstrapui.styles.utilities.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class AcrossBootstrapStyleRule implements BootstrapStyleRule
{
	private final BootstrapStyleRule bootstrapStyleRule;

	public static BootstrapStyleRule of( String... css ) {
		return new AcrossBootstrapStyleRule( BootstrapStyleRule.of( css ) );
	}

	@Override
	public String[] toCssClasses() {
		return bootstrapStyleRule.prefix( "axu-" ).toCssClasses();
	}
}
