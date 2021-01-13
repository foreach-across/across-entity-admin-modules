package com.foreach.across.modules.experimental.webutility.viewelements.createselect;

import com.foreach.across.modules.bootstrapui.elements.BootstrapUiElements;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.views.EntityViewElementBuilderFactory;
import com.foreach.across.modules.entity.views.EntityViewElementBuilderService;
import com.foreach.across.modules.entity.views.ViewElementMode;
import com.foreach.across.modules.web.ui.ViewElementBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Adds support for the {@link CreateSelectViewElementBuilder} that adds a plus button besides a control of property 'x'.
 * When clicking the plus button a modal opens where you can create a new instance of property 'x'.
 */
@Component
@RequiredArgsConstructor
public class CreateSelectViewElementBuilderFactory implements EntityViewElementBuilderFactory
{
	public final static String CREATE_SELECT = CreateSelectViewElementBuilderFactory.class.getName() + ".createSelectControl";

	private final EntityViewElementBuilderService entityViewElementBuilderService;

	@Override
	public boolean supports( String viewElementType ) {
		return viewElementType.equals( CREATE_SELECT );
	}

	/**
	 * Create a {@link CreateSelectViewElementBuilder} and pass in the original {@link ViewElementBuilder}
	 */
	@Override
	public ViewElementBuilder createBuilder( EntityPropertyDescriptor propertyDescriptor, ViewElementMode viewElementMode, String viewElementType ) {
		ViewElementBuilder originalViewElementBuilder = entityViewElementBuilderService.createElementBuilder( propertyDescriptor, viewElementMode,
		                                                                                                      BootstrapUiElements.SELECT );

		return new CreateSelectViewElementBuilder( originalViewElementBuilder );
	}
}
