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
 * https://getbootstrap.com/docs/4.3/utilities/spacing/
 *
 * @author Arne Vandamme
 * @since 2.3.0
 */
@SuppressWarnings("WeakerAccess")
public class MarginStyleRule extends SpacingStyleRule.WithNegative
{
	public final WithNegative top = new WithNegative( "mt" );
	public final WithNegative bottom = new WithNegative( "mb" );
	public final WithNegative left = new WithNegative( "ml" );
	public final WithNegative right = new WithNegative( "mr" );
	public final WithAuto horizontal = new WithAuto( "mx" );
	public final WithNegative vertical = new WithNegative( "my" );

	public MarginStyleRule() {
		super( "m" );
	}
}
