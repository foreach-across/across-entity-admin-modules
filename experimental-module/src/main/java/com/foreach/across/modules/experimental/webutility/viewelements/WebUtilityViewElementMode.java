package com.foreach.across.modules.experimental.webutility.viewelements;

import com.foreach.across.modules.entity.views.ViewElementMode;
import com.foreach.across.modules.entity.views.bootstrapui.FormGroupElementBuilderFactory;
import com.foreach.across.modules.experimental.webutility.viewelements.editablevalues.EditableValueViewElementBuilderFactory;

import static com.foreach.across.modules.experimental.webutility.viewelements.refreshablevalues.RefreshableValueViewElementBuilderFactory.refreshableViewElementMode;

public interface WebUtilityViewElementMode
{
	/**
	 * {@link ViewElementMode} that can be used to configure a view to an editable value mode.
	 * This will indicate that the view renders read-only values, like the detail view, but properties switch to their control mode when accessed.
	 */
	static ViewElementMode EDITABLE_VALUE_VIEW() {
		return ViewElementMode.FORM_READ.withChildMode( FormGroupElementBuilderFactory.CONTROL_CHILD_MODE,
		                                                EditableValueViewElementBuilderFactory.VIEW_ELEMENT_MODE );
	}

	/**
	 * {@link ViewElementMode} that configures an element to support refreshing upon an editable value update.
	 */
	ViewElementMode REFRESHABLE_VALUE = refreshableViewElementMode( ViewElementMode.VALUE );
	/**
	 * {@link ViewElementMode} that configures an element within the {@link com.foreach.across.modules.entity.views.EntityView#LIST_VIEW_NAME} to support refreshing upon an editable value update.
	 */
	ViewElementMode REFRESHABLE_LIST_VALUE = refreshableViewElementMode( ViewElementMode.LIST_VALUE );
	/**
	 * {@link ViewElementMode} that configures an element to render a read-only value, but supports switching to a control value.
	 */
	ViewElementMode EDITABLE_VALUE = EditableValueViewElementBuilderFactory.VIEW_ELEMENT_MODE;
}
