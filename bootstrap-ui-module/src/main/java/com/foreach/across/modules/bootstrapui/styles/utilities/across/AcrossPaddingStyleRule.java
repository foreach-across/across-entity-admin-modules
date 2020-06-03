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

/**
 * https://getbootstrap.com/docs/4.3/utilities/spacing/
 *
 * @author Steven Gentens
 * @since 3.0.0
 */
public class AcrossPaddingStyleRule extends AcrossSpacingStyleRule
{
	public final AcrossSpacingStyleRule top = new AcrossSpacingStyleRule( "pt" );
	public final AcrossSpacingStyleRule bottom = new AcrossSpacingStyleRule( "pb" );
	public final AcrossSpacingStyleRule left = new AcrossSpacingStyleRule( "pl" );
	public final AcrossSpacingStyleRule right = new AcrossSpacingStyleRule( "pr" );
	public final AcrossSpacingStyleRule horizontal = new AcrossSpacingStyleRule( "px" );
	public final AcrossSpacingStyleRule vertical = new AcrossSpacingStyleRule( "py" );

	public AcrossPaddingStyleRule() {
		super( "p" );
	}
}
