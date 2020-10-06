package com.foreach.across.modules.experimental.webutility.viewelements.editablevalues;

import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.views.ViewElementMode;
import com.foreach.across.modules.entity.views.ViewElementTypeLookupStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Custom lookup strategy which takes precedence over the default and supports
 * the custom control types defined.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@RequiredArgsConstructor
public class EditableValueViewElementLookupStrategy implements ViewElementTypeLookupStrategy
{
	@Override
	public String findElementType( EntityPropertyDescriptor descriptor, ViewElementMode viewElementMode ) {
		if ( viewElementMode.matchesSingleTypeOf( EditableValueViewElementBuilderFactory.VIEW_ELEMENT_MODE ) ) {
			return EditableValueViewElementBuilderFactory.ELEMENT_TYPE;
		}

		return null;
	}
}
