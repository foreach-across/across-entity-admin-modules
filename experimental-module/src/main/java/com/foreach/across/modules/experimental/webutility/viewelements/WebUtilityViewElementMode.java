package com.foreach.across.modules.experimental.webutility.viewelements;

import com.foreach.across.modules.entity.views.ViewElementMode;
import com.foreach.across.modules.experimental.webutility.viewelements.editablevalues.EditableValueViewElementBuilderFactory;

import static com.foreach.across.modules.experimental.webutility.viewelements.refreshablevalues.RefreshableValueViewElementBuilderFactory.refreshableViewElementMode;

public interface WebUtilityViewElementMode
{
	ViewElementMode REFRESHABLE_VALUE = refreshableViewElementMode( ViewElementMode.VALUE );
	ViewElementMode REFRESHABLE_LIST_VALUE = refreshableViewElementMode( ViewElementMode.LIST_VALUE );
	ViewElementMode EDITABLE_VALUE = EditableValueViewElementBuilderFactory.VIEW_ELEMENT_MODE;
	ViewElementMode EDITABLE_LIST_VALUE = EditableValueViewElementBuilderFactory.LIST_VIEW_ELEMENT_MODE;
}
