package com.foreach.across.modules.experimental.webutility.icons;

import com.foreach.across.modules.bootstrapui.elements.icons.IconSet;
import com.foreach.across.modules.bootstrapui.elements.icons.IconSetRegistry;
import com.foreach.across.modules.bootstrapui.elements.icons.SimpleIconSet;
import com.foreach.across.modules.experimental.webutility.WebUtilityModule;

import static com.foreach.across.modules.bootstrapui.BootstrapUiModuleIcons.ICON_SET_FONT_AWESOME_SOLID;

public class WebUtilityModuleIcons
{
	public static final String ICON_SET = WebUtilityModule.NAME;
	public static final WebUtilityModuleIcons webUtilityModuleIcons = new WebUtilityModuleIcons();

	public final WebUtilityModuleComponentIcons components = new WebUtilityModuleComponentIcons();

	public static void registerIconSet() {
		SimpleIconSet iconSet = new SimpleIconSet();

		iconSet.add( WebUtilityModuleCreateSelectIcons.ADD_ITEM, ( imageName ) -> IconSet.iconSet( ICON_SET_FONT_AWESOME_SOLID ).icon( "plus" ) );

		IconSetRegistry.addIconSet( ICON_SET, iconSet );
	}
}
