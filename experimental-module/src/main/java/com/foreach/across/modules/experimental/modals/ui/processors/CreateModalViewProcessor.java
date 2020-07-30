package com.foreach.across.modules.experimental.modals.ui.processors;

import com.foreach.across.modules.bootstrapui.elements.ButtonViewElement;
import com.foreach.across.modules.entity.views.EntityView;
import com.foreach.across.modules.entity.views.context.EntityViewContext;
import com.foreach.across.modules.entity.views.request.EntityViewRequest;
import com.foreach.across.modules.entity.views.support.EntityMessages;
import com.foreach.across.modules.entity.web.links.EntityViewLinkBuilder;
import com.foreach.across.modules.experimental.modals.support.action.RequestActionAttribute;
import com.foreach.across.modules.experimental.modals.support.action.RequestActionHandlerAttribute;
import com.foreach.across.modules.experimental.modals.ui.components.ModalViewElementBuilder;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.elements.ContainerViewElement;
import com.foreach.across.modules.web.ui.elements.support.ContainerViewElementUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;

import static com.foreach.across.modules.experimental.modals.support.ModalLoadAttribute.modalLoadAttribute;
import static com.foreach.across.modules.web.ui.elements.HtmlViewElement.Functions.data;
import static com.foreach.across.modules.web.ui.elements.TextViewElement.text;

public class CreateModalViewProcessor extends AbstractModalViewProcessor
{
	public static final String CREATE_MODAL_ID = "createModal";

	@Override
	protected void postRender( EntityViewRequest entityViewRequest,
	                           EntityView entityView,
	                           ContainerViewElement container,
	                           ViewElementBuilderContext builderContext ) {
		EntityViewLinkBuilder linkBuilder = entityViewRequest.getEntityViewContext().getLinkBuilder();
		ContainerViewElementUtils.find( container, "btn-create", ButtonViewElement.class )
		                         .ifPresent(
				                         btn -> {
					                         String modalSelector = "#" + CREATE_MODAL_ID;
					                         btn.set( data( "toggle", "modal" ), data( "target", modalSelector ) )
					                            .set(
							                            modalLoadAttribute()
									                            .target( modalSelector )
									                            .content(
											                            new RequestActionAttribute()
													                            .url( linkBuilder.createView().withPartial( "::body" ).toUriString() )
													                            .success(
															                            new RequestActionHandlerAttribute()
																	                            .type( RequestActionHandlerAttribute.Type.REQUEST_CONTENT )
																	                            .target( modalTarget( " .modal-body" ) ),
															                            new RequestActionHandlerAttribute()
																	                            .type( RequestActionHandlerAttribute.Type.CLEAR )
																	                            .target( modalTarget( " .modal-footer" ) ),
															                            new RequestActionHandlerAttribute()
																	                            .type( RequestActionHandlerAttribute.Type.MOVE )
																	                            .source( modalTarget( " .modal-body .em-form-actions" ) )
																	                            .target( modalTarget( " .modal-footer" ) )
													                            )
									                            )
					                            );
					                         registerModal( entityViewRequest.getEntityViewContext(), container, builderContext );
				                         }
		                         );
	}

	private String modalTarget( String target ) {
		return "#" + CREATE_MODAL_ID + target;
	}

	private void registerModal( EntityViewContext entityViewContext,
	                            ContainerViewElement container,
	                            ViewElementBuilderContext builderContext ) {
		EntityMessages entityMessages = new EntityMessages(
				entityViewContext.getMessageCodeResolver().prefixedResolver( "views[" + EntityView.CREATE_VIEW_NAME + "]" ) );

		ModalViewElementBuilder modal = new ModalViewElementBuilder()
				.name( CREATE_MODAL_ID )
				.centered( true )
				.body( text( "Odi et amo, quare id faciam, fortasse requiris? Nescio, sed fieri sentio et excrucior." ) )
				.footer();

		Optional.ofNullable(
				StringUtils.defaultIfEmpty( entityMessages.withNameSingular( EntityMessages.PAGE_TITLE_CREATE, entityViewContext.getEntityLabel() ), null )
		).ifPresent( modal::title );

		container.addChild( modal.build( builderContext ) );
	}
}
