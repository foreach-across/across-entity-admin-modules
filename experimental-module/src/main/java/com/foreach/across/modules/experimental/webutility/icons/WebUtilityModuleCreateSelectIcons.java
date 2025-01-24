package com.foreach.across.modules.experimental.webutility.icons;

import com.foreach.across.modules.bootstrapui.elements.icons.IconSet;
import com.foreach.across.modules.web.ui.elements.HtmlViewElement;

public class WebUtilityModuleCreateSelectIcons
{
	public final static String ADD_ITEM = "create-select-add-item";

	public HtmlViewElement addItem() {
		return IconSet.iconSet( WebUtilityModuleIcons.ICON_SET ).icon( ADD_ITEM );
	}
}
