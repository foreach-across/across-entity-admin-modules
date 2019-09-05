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

/**
 * https://getbootstrap.com/docs/4.3/utilities/spacing/
 *
 * @author Arne Vandamme
 * @since 2.3.0
 */
public class PaddingStyleRule extends SpacingStyleRule
{
	public final SpacingStyleRule top = new SpacingStyleRule( "pt" );
	public final SpacingStyleRule bottom = new SpacingStyleRule( "pb" );
	public final SpacingStyleRule left = new SpacingStyleRule( "pl" );
	public final SpacingStyleRule right = new SpacingStyleRule( "pr" );
	public final SpacingStyleRule horizontal = new SpacingStyleRule( "px" );
	public final SpacingStyleRule vertical = new SpacingStyleRule( "py" );

	public PaddingStyleRule() {
		super( "p" );
	}
}
