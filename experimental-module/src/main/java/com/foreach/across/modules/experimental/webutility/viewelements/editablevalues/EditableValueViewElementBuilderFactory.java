package com.foreach.across.modules.experimental.webutility.viewelements.editablevalues;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.registry.properties.SimpleEntityPropertyDescriptor;
import com.foreach.across.modules.entity.views.EntityViewElementBuilderFactory;
import com.foreach.across.modules.entity.views.EntityViewElementBuilderService;
import com.foreach.across.modules.entity.views.ViewElementMode;
import com.foreach.across.modules.entity.views.context.EntityViewContext;
import com.foreach.across.modules.entity.views.util.EntityViewElementUtils;
import com.foreach.across.modules.entity.web.EntityViewModel;
import com.foreach.across.modules.experimental.webutility.resource.WebUtilityModuleWebResources;
import com.foreach.across.modules.experimental.webutility.support.WebUtilityModuleAttributes;
import com.foreach.across.modules.experimental.webutility.viewelements.EditableValuesUtils;
import com.foreach.across.modules.experimental.webutility.viewelements.refreshablevalues.RefreshableValueViewElementBuilderFactory;
import com.foreach.across.modules.web.resource.WebResourceRegistry;
import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.ViewElementBuilder;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.ViewElementBuilderContextHolder;
import com.foreach.across.modules.web.ui.elements.HtmlViewElement;
import lombok.*;
import lombok.experimental.Accessors;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.NoSuchElementException;

import static com.foreach.across.modules.bootstrapui.attributes.BootstrapAttributes.attribute;
import static com.foreach.across.modules.bootstrapui.styles.BootstrapStyles.css;
import static com.foreach.across.modules.bootstrapui.ui.factories.BootstrapViewElements.bootstrap;
import static com.foreach.across.modules.web.ui.elements.HtmlViewElements.html;

/**
 * Renders an editable value for a property. An editable value is visualized as
 * text (the readonly value), but will be converted into the actual control when clicked.
 * The actual control will be included in the markup as a script snippet of type text/html.
 * <p/>
 * Two child modes can be specified on the requested {@link ViewElementMode}.
 * If omitted, the readonly value will be the {@link ViewElementMode#VALUE} and the
 * control the {@link ViewElementMode#CONTROL}.
 * <p/>
 * Client-side code is initalized based on the {@code data-em-editable-value} value.
 *
 * @see EditableValueControlProcessor
 */
@SuppressWarnings("rawtypes")
@Component
@RequiredArgsConstructor
public class EditableValueViewElementBuilderFactory implements EntityViewElementBuilderFactory<ViewElementBuilder>
{
	public static final String ELEMENT_TYPE = EditableValueViewElementBuilderFactory.class.getName();
	public static final String REQUIRED_VIEW = "editableValues";

	/**
	 * Base mode which can be handled by this builder factory.
	 */
	public static final ViewElementMode VIEW_ELEMENT_MODE = ViewElementMode.of( "EDITABLE_VALUE" );
	public static final ViewElementMode LIST_VIEW_ELEMENT_MODE = ViewElementMode.of( "LIST_EDITABLE_VALUE" );

	private static final String VALUE_CHILD_MODE = "value";
	private static final String CONTROL_CHILD_MODE = "control";
	private static final String EDITABLE_VALUE_ROLE = "data-em-editable-value-role";

	private final EntityViewElementBuilderService entityViewElementBuilderService;
	private final EditableValuesUtils editableValuesUtils;

	@Override
	public boolean supports( String viewElementType ) {
		return ELEMENT_TYPE.equals( viewElementType );
	}

	@Override
	public ViewElementBuilder createBuilder( EntityPropertyDescriptor propertyDescriptor, ViewElementMode viewElementMode, String viewElementType ) {
		ViewElementMode valueMode = viewElementMode.getChildMode( VALUE_CHILD_MODE, ViewElementMode.VALUE );
		ViewElementMode controlMode = viewElementMode.getChildMode( CONTROL_CHILD_MODE, ViewElementMode.CONTROL );
		ViewElementBuilder valueBuilder = entityViewElementBuilderService.createElementBuilder( propertyDescriptor, valueMode );

		ViewElementBuilderContextHolder.getViewElementBuilderContext()
		                               .ifPresent( bc -> {
			                               tryRegisterWebResources( bc );
			                               valueBuilder.build( bc );
		                               } );

		if ( !propertyDescriptor.isWritable() ) {
			return html.builders
					.span()
					.css( "editable-value-wrapper" )
					.name( propertyDescriptor.getName() )
					.add(
							html.builders.span()
							             .add( valueBuilder ) );
		}
		boolean showActions = ( (SimpleEntityPropertyDescriptor) propertyDescriptor ).findAttribute( "showEditableActions" )
		                                                                             .map( e -> (boolean) e )
		                                                                             .orElse( true );

		ViewElementBuilder controlBuilder = entityViewElementBuilderService.createElementBuilder( propertyDescriptor, controlMode );
		ViewElementBuilderContextHolder.getViewElementBuilderContext()
		                               .ifPresent( controlBuilder::build );
		return html.builders
				.span()
				.attribute( "show-actions", showActions )
				.name( "editableValue-" + propertyDescriptor.getName() )
				.add(
						// add value mode
						html.builders.span( css.of( "editable-value-value-wrapper" ) )
						             .attribute( "title", "Click to edit" )
						             .add(
								             html.builders.span( attribute.of( EDITABLE_VALUE_ROLE ).withValue( VALUE_CHILD_MODE ) )
								                          .with( attributeIfDifferent( valueMode, ViewElementMode.VALUE ) )
								                          .add( valueBuilder )
								                          .add( html.builders.span( css.of( "cta-item cta-edit" ) ) )
						             )
				)
				.add(
						// add control mode
						html.builders.span( attribute.of( EDITABLE_VALUE_ROLE ).withValue( "control-container" ) )
						             .add(
								             bootstrap.builders.script( attribute.of( EDITABLE_VALUE_ROLE ).withValue( CONTROL_CHILD_MODE ) )
								                               .name( "editableValue-" + propertyDescriptor.getName() + "-control" )
								                               .with( attributeIfDifferent( valueMode, ViewElementMode.CONTROL ) )
								                               .type( MediaType.TEXT_HTML )
								                               .add( controlBuilder )
						             )
				)
				.postProcessor(
						// register the controller property for hooking up the javascript
						( builderContext, wrapper ) -> {
							EntityPropertyDescriptor property = EntityViewElementUtils.currentPropertyDescriptor( builderContext );
							EntityViewContext entityViewContext = builderContext.getAttribute( EntityViewModel.VIEW_CONTEXT, EntityViewContext.class );
							Object entity = EntityViewElementUtils.currentEntity( builderContext );

							if ( entity != null && entityViewContext != null && property != null ) {
								wrapper.set(
										new EditableValueSettings()
												.propertyId( editableValuesUtils.resolveEntityPropertyId( builderContext )
												                                .orElseThrow( () -> new NoSuchElementException( "No value present" ) ) )
												.propertyReferenceId( editableValuesUtils.resolvePropertyReferenceId( builderContext ) )
												.targetUrl( resolveTargetUrl( entityViewContext, entity ) )
												.multiValueProperty( isMultiValueControl( propertyDescriptor, controlMode ) )
												.includeActions(
														getAttributeOrDefault( propertyDescriptor, WebUtilityModuleAttributes.EditableValue.INCLUDE_ACTIONS,
														                       true ) )
								);
							}

							tryRegisterWebResources( builderContext );
						}
				);
	}

	private boolean isMultiValueControl( EntityPropertyDescriptor propertyDescriptor, ViewElementMode controlMode ) {
		if ( controlMode.isForMultiple() ) {
			return true;
		}
		TypeDescriptor propertyTypeDescriptor = propertyDescriptor.getPropertyTypeDescriptor();
		if ( propertyTypeDescriptor != null && ( propertyTypeDescriptor.isCollection() || propertyTypeDescriptor.isArray() ) ) {
			return true;
		}
		Class<?> propertyType = propertyDescriptor.getPropertyType();
		if ( propertyType != null && ( propertyType.isArray() || Collection.class.isAssignableFrom( propertyType ) ) ) {
			return true;
		}
		return false;
	}

	private void tryRegisterWebResources( ViewElementBuilderContext builderContext ) {
		WebResourceRegistry attribute = builderContext.getAttribute( WebResourceRegistry.class );
		if ( attribute != null ) {
			attribute.addPackage( WebUtilityModuleWebResources.NAME );
		}
	}

	private String resolveTargetUrl( EntityViewContext entityViewContext, Object entity ) {
		return entityViewContext.getLinkBuilder()
		                        .forInstance( entity )
		                        .updateView()
		                        .withViewName( REQUIRED_VIEW )
		                        .toUriString();
	}

	private <T> T getAttributeOrDefault( EntityPropertyDescriptor propertyDescriptor, String attributeName, T defaultValue ) {
		return propertyDescriptor.hasAttribute( attributeName )
				? (T) propertyDescriptor.getAttribute( attributeName )
				: defaultValue;
	}

	private ViewElement.WitherSetter<HtmlViewElement> attributeIfDifferent( ViewElementMode viewElementMode, ViewElementMode defaultMode ) {
		return node -> {
			if ( !defaultMode.equals( viewElementMode ) ) {
				node.setAttribute( RefreshableValueViewElementBuilderFactory.ATTR_VIEW_ELEMENT_MODE, viewElementMode.toString() );
			}
		};
	}

	@Setter
	@Accessors(chain = true, fluent = true)
	@NoArgsConstructor(access = AccessLevel.PACKAGE)
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@SuppressWarnings("unused")
	static class EditableValueSettings implements ViewElement.WitherSetter<HtmlViewElement>
	{
		@NonNull
		@JsonProperty
		private String propertyId;

		@NonNull
		@JsonProperty
		private String propertyReferenceId;

		@NonNull
		@JsonProperty
		private String targetUrl;

		@JsonProperty
		private boolean multiValueProperty;

		@JsonProperty
		private boolean includeActions;

		@Override
		public void applyTo( HtmlViewElement target ) {
			target.setAttribute( "data-em-editable-value", this );
		}
	}
}

