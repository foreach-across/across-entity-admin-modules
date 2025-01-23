package com.foreach.across.modules.experimental.webutility.viewelements.refreshablevalues;

import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.views.EntityViewElementBuilderFactory;
import com.foreach.across.modules.entity.views.EntityViewElementBuilderService;
import com.foreach.across.modules.entity.views.ViewElementMode;
import com.foreach.across.modules.experimental.webutility.viewelements.EditableValuesUtils;
import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.ViewElementBuilder;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.elements.HtmlViewElement;
import com.foreach.across.modules.web.ui.elements.NodeViewElement;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static com.foreach.across.modules.web.ui.elements.HtmlViewElements.html;

/**
 * Wraps any control with a {@code data-em-property-id} which identifies the property that
 * is being contained in the wrapper. Optionally with a {@code data-em-ve-mode} which contains
 * the {@link ViewElementMode} of the property value, if it was different from {@link ViewElementMode#VALUE}.
 */
@Component
@RequiredArgsConstructor
public class RefreshableValueViewElementBuilderFactory implements EntityViewElementBuilderFactory<ViewElementBuilder>
{
	public static final String ELEMENT_TYPE = RefreshableValueViewElementBuilderFactory.class.getName();

	/**
	 * Base mode which can be handled by this builder factory.
	 */
	public static final ViewElementMode VIEW_ELEMENT_MODE = ViewElementMode.of( "REFRESHABLE" );

	/**
	 * Child mode that should be present (mode which will be wrapped with the property identifier).
	 */
	public static final String TARGET_CHILD_MODE = "target";

	public static final String ATTR_VIEW_ELEMENT_MODE = "data-em-ve-mode";
	static final String ATTR_PROPERTY_ID = "data-em-property-id";

	private final EntityViewElementBuilderService entityViewElementBuilderService;
	private final EditableValuesUtils editableValuesUtils;

	@Override
	public boolean supports( String viewElementType ) {
		return ELEMENT_TYPE.equals( viewElementType );
	}

	@Override
	public ViewElementBuilder createBuilder( EntityPropertyDescriptor propertyDescriptor,
	                                         ViewElementMode viewElementMode,
	                                         String viewElementType ) {
		ViewElementMode targetMode = viewElementMode.getChildMode( TARGET_CHILD_MODE, ViewElementMode.VALUE );

		return html.builders.span()
		                    .add( entityViewElementBuilderService.createElementBuilder( propertyDescriptor, targetMode ) )
		                    .postProcessor( ( ( builderContext, element ) -> attachRefreshableDataAttributes( element, builderContext, targetMode ) ) );
	}

	private void attachRefreshableDataAttributes( NodeViewElement wrapper, ViewElementBuilderContext builderContext, ViewElementMode viewElementMode ) {
		editableValuesUtils.resolveEntityPropertyId( builderContext ).ifPresent( id -> wrapper.setAttribute( ATTR_PROPERTY_ID, id ) );

		if ( !ViewElementMode.VALUE.equals( viewElementMode ) ) {
			wrapper.setAttribute( ATTR_VIEW_ELEMENT_MODE, viewElementMode.toString() );
		}
	}

	public static ViewElementMode refreshableViewElementMode( @NonNull ViewElementMode targetMode ) {
		return VIEW_ELEMENT_MODE.withChildMode( TARGET_CHILD_MODE, targetMode );
	}

	public static ViewElement.WitherSetter<HtmlViewElement> propertyId( String propertyId ) {
		return node -> node.setAttribute( ATTR_PROPERTY_ID, propertyId );
	}
}
