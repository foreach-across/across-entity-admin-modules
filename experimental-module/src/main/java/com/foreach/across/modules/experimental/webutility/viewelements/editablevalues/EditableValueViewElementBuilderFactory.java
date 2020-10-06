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
import com.foreach.across.modules.entity.web.EntityModuleWebResources;
import com.foreach.across.modules.entity.web.EntityViewModel;
import com.foreach.across.modules.experimental.entitycontrols.domain.EntityControlFactory;
import com.foreach.across.modules.experimental.webutility.viewelements.refreshablevalues.RefreshableValueViewElementBuilderFactory;
import com.foreach.across.modules.web.resource.WebResource;
import com.foreach.across.modules.web.resource.WebResourceRegistry;
import com.foreach.across.modules.web.resource.WebResourceRule;
import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.ViewElementBuilder;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.elements.HtmlViewElement;
import lombok.*;
import lombok.experimental.Accessors;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import static com.foreach.across.modules.bootstrapui.attributes.BootstrapAttributes.attribute;
import static com.foreach.across.modules.bootstrapui.ui.factories.BootstrapViewElements.bootstrap;
import static com.foreach.across.modules.web.resource.WebResource.CSS;
import static com.foreach.across.modules.web.resource.WebResource.JAVASCRIPT_PAGE_END;
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

	private static final String VALUE_CHILD_MODE = "value";
	private static final String CONTROL_CHILD_MODE = "control";
	private static final String EDITABLE_VALUE_ROLE = "data-em-editable-value-role";

	private final EntityViewElementBuilderService entityViewElementBuilderService;
	private final EntityControlFactory entityControlFactory;

	@Override
	public boolean supports( String viewElementType ) {
		return ELEMENT_TYPE.equals( viewElementType );
	}

	@Override
	public ViewElementBuilder createBuilder( EntityPropertyDescriptor propertyDescriptor, ViewElementMode viewElementMode, String viewElementType ) {
		ViewElementMode valueMode = viewElementMode.getChildMode( VALUE_CHILD_MODE, ViewElementMode.VALUE );
		ViewElementMode controlMode = viewElementMode.getChildMode( CONTROL_CHILD_MODE, ViewElementMode.CONTROL );
		if ( !propertyDescriptor.isWritable() ) {
			return html.builders
					.span()
					.css( "editable-value-wrapper" )
					.name( propertyDescriptor.getName() )
					.add(
							html.builders.span()
							             .add( entityViewElementBuilderService.createElementBuilder( propertyDescriptor, valueMode ) ) );
		}
		boolean showActions = ( (SimpleEntityPropertyDescriptor) propertyDescriptor ).findAttribute( "showEditableActions" )
		                                                                             .map( e -> (boolean) e )
		                                                                             .orElse( true );
		return html.builders
				.span()
				.attribute( "show-actions", showActions )
				.name( "editableValue-" + propertyDescriptor.getName() )
				.add(
						// add value mode
						html.builders.span( attribute.of( EDITABLE_VALUE_ROLE ).withValue( VALUE_CHILD_MODE ) )
						             .with( attributeIfDifferent( valueMode, ViewElementMode.VALUE ) )
						             .add( entityViewElementBuilderService.createElementBuilder( propertyDescriptor, valueMode ) ) )
				.add(
						// add control mode
						html.builders.span( attribute.of( EDITABLE_VALUE_ROLE ).withValue( "control-container" ) )
						             .add(
								             bootstrap.builders.script( attribute.of( EDITABLE_VALUE_ROLE ).withValue( CONTROL_CHILD_MODE ) )
								                               .name( "editableValue-" + propertyDescriptor.getName() + "-control" )
								                               .with( attributeIfDifferent( valueMode, ViewElementMode.CONTROL ) )
								                               .type( MediaType.TEXT_HTML )
								                               .add( entityViewElementBuilderService.createElementBuilder( propertyDescriptor, controlMode ) )
						             )
				)
				.postProcessor(
						// register the controller property for hooking up the javascript
						( builderContext, wrapper ) -> {
							EntityPropertyDescriptor property = EntityViewElementUtils.currentPropertyDescriptor( builderContext );
							EntityViewContext entityViewContext = builderContext.getAttribute( EntityViewModel.VIEW_CONTEXT, EntityViewContext.class );
							Object entity = EntityViewElementUtils.currentEntity( builderContext );

							if ( !entityViewContext.holdsEntity() && entityViewContext.isForAssociation() ) {
								entityViewContext = entityViewContext.getParentContext();
							}

							if ( entity != null && entityViewContext != null && property != null ) {
								wrapper.set(
										new EditableValueSettings()
												.propertyId( entityControlFactory.resolveEntityPropertyId( builderContext ).orElseThrow() )
												.targetUrl( resolveTargetUrl( entityViewContext, entity ) )
								);
							}

							tryRegisterWebResources( builderContext );
						}
				);
	}

	private void tryRegisterWebResources( ViewElementBuilderContext builderContext ) {
		WebResourceRegistry attribute = builderContext.getAttribute( WebResourceRegistry.class );
		if ( attribute != null ) {
			attribute.apply(
					WebResourceRule.add(
							WebResource.javascript( "@static:/experimental/web/editable-value.js" ) )
					               .withKey( "experimental-web-utilities-editable-value-js" )
					               .after( EntityModuleWebResources.NAME )
					               .toBucket( JAVASCRIPT_PAGE_END ),
					WebResourceRule.add(
							WebResource.css( "@static:/experimental/web/editable-value.css" ) )
					               .withKey( "experimental-web-utilities-editable-value-css" )
					               .toBucket( CSS )
			);
		}
	}

	private String resolveTargetUrl( EntityViewContext entityViewContext, Object entity ) {
		return entityViewContext.getLinkBuilder()
		                        .forInstance( entity )
		                        .updateView()
		                        .withViewName( REQUIRED_VIEW )
		                        .toUriString();
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
		private String targetUrl;

		@Override
		public void applyTo( HtmlViewElement target ) {
			target.setAttribute( "data-em-editable-value", this );
		}
	}
}

