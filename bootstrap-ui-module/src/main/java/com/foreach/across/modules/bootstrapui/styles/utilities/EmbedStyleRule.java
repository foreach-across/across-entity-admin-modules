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
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;

import static com.foreach.across.modules.bootstrapui.styles.BootstrapStyleRule.of;

/**
 * https://getbootstrap.com/docs/4.3/utilities/embed/
 *
 * @author Arne Vandamme
 * @since 2.3.0
 */
public class EmbedStyleRule
{
	public final Responsive responsive = new Responsive();
	public final BootstrapStyleRule responsiveItem = of( "embed-responsive-item" );

	public static class Responsive implements BootstrapStyleRule
	{
		public BootstrapStyleRule aspectRatio( @NonNull String aspectRatio ) {
			return suffix( StringUtils.replaceEach( aspectRatio, new String[] { ":", "/" }, new String[] { "by", "by" } ) );
		}

		@Override
		public String[] toCssClasses() {
			return new String[] { "embed-responsive" };
		}
	}
}
