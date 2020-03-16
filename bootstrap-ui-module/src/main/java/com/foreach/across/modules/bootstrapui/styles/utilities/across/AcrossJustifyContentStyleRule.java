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

import com.foreach.across.modules.bootstrapui.styles.utilities.BreakpointStyleRule;

/**
 * https://getbootstrap.com/docs/4.3/utilities/flex/#justify-content
 *
 * @author Steven Gentens
 * @since 3.0.0
 */
public class AcrossJustifyContentStyleRule
{
	public final BreakpointStyleRule start = new AcrossSimpleBreakpointStyleRule( "justify-content", "start" );
	public final BreakpointStyleRule end = new AcrossSimpleBreakpointStyleRule( "justify-content", "end" );
	public final BreakpointStyleRule center = new AcrossSimpleBreakpointStyleRule( "justify-content", "center" );
	public final BreakpointStyleRule between = new AcrossSimpleBreakpointStyleRule( "justify-content", "between" );
	public final BreakpointStyleRule around = new AcrossSimpleBreakpointStyleRule( "justify-content", "around" );
}
