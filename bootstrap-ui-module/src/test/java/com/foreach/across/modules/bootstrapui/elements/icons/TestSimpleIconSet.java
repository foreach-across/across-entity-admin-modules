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

package com.foreach.across.modules.bootstrapui.elements.icons;

import com.foreach.across.modules.web.ui.elements.AbstractNodeViewElement;
import com.foreach.across.test.support.AbstractViewElementTemplateTest;
import org.junit.Before;
import org.junit.Test;

import static com.foreach.across.modules.bootstrapui.elements.icons.IconSet.iconSet;
import static com.foreach.across.modules.web.ui.elements.HtmlViewElement.Functions.css;
import static com.foreach.across.modules.web.ui.elements.HtmlViewElements.i;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Stijn Vanhoof
 */
public class TestSimpleIconSet extends AbstractViewElementTemplateTest
{
	private String customFontAwesomeIconSetName = "custom-fontawesome-solid";

	@Before
	public void setup() {
		SimpleIconSet customFontAwesomeIconSetSimpleIconSet = new SimpleIconSet();
		customFontAwesomeIconSetSimpleIconSet.setDefaultIconResolver( ( iconName ) -> i( css( "fas fa-" + iconName ) ) );
		IconSetRegistry.addIconSet( customFontAwesomeIconSetName, customFontAwesomeIconSetSimpleIconSet );
	}

	@Test
	public void defaultIcon() {
		AbstractNodeViewElement icon = iconSet( customFontAwesomeIconSetName ).icon( "edit" );
		renderAndExpect( icon, "<i class=\"fas fa-edit\"></i>" );
	}

	@Test(expected = IllegalArgumentException.class)
	public void noDefaultIconResolverProvided() {
		SimpleIconSet noDefaultIconSet = new SimpleIconSet();
		IconSetRegistry.addIconSet( "no-default", noDefaultIconSet );

		iconSet( "unknown" );
	}

	@Test
	public void customIcon() {
		AbstractNodeViewElement icon = iconSet( customFontAwesomeIconSetName ).icon( "save" );
		renderAndExpect( icon, "<i class=\"fas fa-save\"></i>" );

		IconSetRegistry.getIconSet( customFontAwesomeIconSetName ).add( "save", ( iconName ) -> i( css( "fas fa-floppy" ) ) );
		IconSetRegistry.getIconSet( customFontAwesomeIconSetName ).add( "edit", ( iconName ) -> i( css( "fas fa-edit" ) ) );
		icon = iconSet( customFontAwesomeIconSetName ).icon( "save" );
		renderAndExpect( icon, "<i class=\"fas fa-floppy\"></i>" );
	}

	@Test
	public void overrideIcon() {
		IconSetRegistry.getIconSet( customFontAwesomeIconSetName ).add( "icon-override-1", ( iconName ) -> i( css( "icon-override-v1" ) ) );
		IconSetRegistry.getIconSet( customFontAwesomeIconSetName ).add( "icon-override-1", ( iconName ) -> i( css( "icon-override-v2" ) ) );
		AbstractNodeViewElement icon = iconSet( customFontAwesomeIconSetName ).icon( "icon-override-1" );
		renderAndExpect( icon, "<i class=\"icon-override-v2\"></i>" );
	}

	@Test
	public void removeIcon() {
		IconSetRegistry.getIconSet( customFontAwesomeIconSetName ).add( "icon-remove", ( iconName ) -> i( css( "icon-remove" ) ) );
		AbstractNodeViewElement icon = IconSetRegistry.getIconSet( customFontAwesomeIconSetName ).icon( "icon-remove" );
		renderAndExpect( icon, "<i class=\"icon-remove\"></i>" );

		IconSetRegistry.getIconSet( customFontAwesomeIconSetName ).remove( "icon-remove" );
		icon = iconSet( customFontAwesomeIconSetName ).icon( "icon-remove" );
		renderAndExpect( icon, "<i class=\"fas fa-icon-remove\"></i>" );
	}

	@Test
	public void tryToRemoveIconFromOtherIconSet() {
		SimpleIconSet otherIconSEt = new SimpleIconSet();
		otherIconSEt.setDefaultIconResolver( ( iconName ) -> i( css( "other-icons" ) ) );
		IconSetRegistry.addIconSet( "other-icons", otherIconSEt );

		IconSetRegistry.getIconSet( customFontAwesomeIconSetName ).add( "icon-remove", ( iconName ) -> i( css( "icon-remove" ) ) );
		AbstractNodeViewElement icon = iconSet( customFontAwesomeIconSetName ).icon( "icon-remove" );
		renderAndExpect( icon, "<i class=\"icon-remove\"></i>" );
		IconSetRegistry.getIconSet( "other-icons" ).remove( "icon-remove" );
		icon = iconSet( customFontAwesomeIconSetName ).icon( "icon-remove" );
		renderAndExpect( icon, "<i class=\"icon-remove\"></i>" );
	}

	@Test
	public void getAllRegisteredIcons(){
		SimpleIconSet withAllIcons = new SimpleIconSet();
		withAllIcons.setDefaultIconResolver( ( iconName ) -> i( css( "fas fa-" + iconName ) ) );
		IconSetRegistry.addIconSet( "with-all-icons", withAllIcons );

		IconSetRegistry.getIconSet( "with-all-icons").add( "save", ( iconName ) -> i( css( "fas fa-floppy" ) ) );
		IconSetRegistry.getIconSet( "with-all-icons" ).add( "edit", ( iconName ) -> i( css( "fas fa-edit" ) ) );

		assertThat(iconSet("with-all-icons").getAllRegisteredIcons()).hasSize( 2 );
	}

	@Test
	public void removeAllIcons(){
		SimpleIconSet withAllIcons = new SimpleIconSet();
		withAllIcons.setDefaultIconResolver( ( iconName ) -> i( css( "fas fa-" + iconName ) ) );
		IconSetRegistry.addIconSet( "remove-all-icons", withAllIcons );

		IconSetRegistry.getIconSet( "remove-all-icons").add( "save", ( iconName ) -> i( css( "fas fa-floppy" ) ) );
		IconSetRegistry.getIconSet( "remove-all-icons" ).add( "edit", ( iconName ) -> i( css( "fas fa-edit" ) ) );

		IconSetRegistry.getIconSet( "remove-all-icons" ).removeAll();
		assertThat(iconSet("remove-all-icons").getAllRegisteredIcons()).hasSize( 0 );
	}
}
