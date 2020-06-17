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

package com.foreach.across.modules.bootstrapui;

import com.foreach.across.modules.bootstrapui.elements.icons.IconSetRegistry;
import com.foreach.across.modules.bootstrapui.elements.icons.SimpleIconSet;

import static com.foreach.across.modules.web.ui.elements.HtmlViewElement.Functions.css;
import static com.foreach.across.modules.web.ui.elements.HtmlViewElements.html;

/**
 * Register the default Font Awesome icon sets.
 * For now the free solid and brand icons of Font Awesome 5 are implemented.
 *
 * @author Stijn Vanhoof
 * @since 3.0.0
 */
@SuppressWarnings("WeakerAccess")
public class BootstrapUiModuleIcons
{
	public static final String ICON_SET_FONT_AWESOME_REGULAR = "font-awesome-regular";
	public static final String ICON_SET_FONT_AWESOME_SOLID = "font-awesome-solid";
	public static final String ICON_SET_FONT_AWESOME_BRANDS = "font-awesome-brands";

	public static void registerFontAwesomeIconSets() {
		IconSetRegistry.addIconSet( ICON_SET_FONT_AWESOME_REGULAR, createFontAwesomeIconSet( "far" ) );
		IconSetRegistry.addIconSet( ICON_SET_FONT_AWESOME_SOLID, createFontAwesomeIconSet( "fas" ) );
		IconSetRegistry.addIconSet( ICON_SET_FONT_AWESOME_BRANDS, createFontAwesomeIconSet( "fab" ) );
	}

	private static SimpleIconSet createFontAwesomeIconSet( String cssPrefix ) {
		SimpleIconSet simpleiconset = new SimpleIconSet();
		simpleiconset.setDefaultIconResolver( ( iconName ) -> html.i( css( cssPrefix + " fa-" + iconName ) ) );
		return simpleiconset;
	}
}
