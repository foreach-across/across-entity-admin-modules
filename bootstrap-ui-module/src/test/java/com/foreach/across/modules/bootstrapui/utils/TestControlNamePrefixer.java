/*
 * Copyright 2014 the original author or authors
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

package com.foreach.across.modules.bootstrapui.utils;

import com.foreach.across.modules.bootstrapui.elements.HiddenFormElement;
import com.foreach.across.modules.bootstrapui.elements.TextboxFormElement;
import com.foreach.across.modules.web.ui.elements.ContainerViewElement;
import lombok.val;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @author Arne Vandamme
 * @since 2.1.0
 */
public class TestControlNamePrefixer
{
	@Test
	public void replaceSingleControlName() {
		val prefixer = new ControlNamePrefixer().prefixToAdd( "my" );

		ContainerViewElement container = new ContainerViewElement();
		HiddenFormElement control = new HiddenFormElement();
		container.addChild( control );

		TextboxFormElement textbox = new TextboxFormElement();
		textbox.setControlName( "mytext" );
		textbox.addChild( control );

		prefixer.accept( control );
		assertNull( control.getControlName() );

		control.setControlName( "ctl" );

		prefixer.accept( control );
		assertEquals( "my.ctl", control.getControlName() );

		prefixer.prefixToReplace( "my" ).prefixToAdd( "_your" ).accept( control );
		assertEquals( "_your.ctl", control.getControlName() );

		prefixer.prefixToReplace( "other" ).accept( control );
		assertEquals( "_your.ctl", control.getControlName() );

		prefixer.prefixToReplace( "your" ).prefixToAdd( "mine" ).accept( control );
		assertEquals( "_mine.ctl", control.getControlName() );

		prefixer.prefixToReplace( "" ).prefixToAdd( "yours" ).accept( control );
		assertEquals( "_yours.mine.ctl", control.getControlName() );

		prefixer.prefixToReplace( "_yours.mine." ).prefixToAdd( "" ).accept( control );
		assertEquals( "ctl", control.getControlName() );

		control.setControlName( "ctl" );
		prefixer.prefixToReplace( null ).prefixToAdd( "my" ).insertDotSeparator( false ).accept( control );
		assertEquals( "myctl", control.getControlName() );


		/*


		BootstrapElementUtils.replaceControlNamePrefix( "your", "mine", control );
		assertEquals( "_mine.ctl", control.getControlName() );

		BootstrapElementUtils.replaceControlNamePrefix( "", "yours.", control );
		assertEquals( "_yours.mine.ctl", control.getControlName() );

		BootstrapElementUtils.replaceControlNamePrefix( "_yours.mine.", "", control );
		assertEquals( "ctl", control.getControlName() );
		*/
	}
}
