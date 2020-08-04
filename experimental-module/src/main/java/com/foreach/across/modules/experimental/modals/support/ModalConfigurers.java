package com.foreach.across.modules.experimental.modals.support;

import com.foreach.across.modules.adminweb.ui.PageContentStructure;
import com.foreach.across.modules.entity.config.builders.EntityConfigurationBuilder;
import com.foreach.across.modules.entity.web.links.EntityViewLinkBuilder;
import com.foreach.across.modules.experimental.modals.ui.processors.*;
import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import org.springframework.http.HttpMethod;

import java.util.Map;
import java.util.function.Consumer;

import static com.foreach.across.modules.entity.views.util.EntityViewElementUtils.currentEntity;
import static com.foreach.across.modules.experimental.modals.support.action.RequestActionAttribute.requestAction;
import static com.foreach.across.modules.experimental.modals.support.action.SimpleActionHandlerAttribute.*;

public class ModalConfigurers
{
	/**
	 * Identifies the request header that allows a form to be modified for a modal.
	 * The value of the origin header should be the id of the modal that is in use.
	 *
	 * @see ModalFormViewProcessor
	 */
	public static final String MODAL_ORIGIN_HEADER = "X-MODAL-ORIGIN";

	public static <U extends EntityConfigurationBuilder<?>> Consumer<U> createViewAsModal() {
		String modalId = "createModal";
		String modalSelector = "#" + modalId;
		return configuration ->
				configuration.listView( lvb -> lvb.viewProcessor(
						vp -> vp.createBean( ModalCreateButtonListViewProcessor.class )
						        .configure(
								        mcblvp -> mcblvp.modalId( modalId )
								                        .url( ( linkBuilder, ctx ) -> linkBuilder.createView().toUriString() )
								                        .partial( "content" )
						        )
				) ).createFormView( fvb -> fvb.viewProcessor(
						vp -> vp.createBean( SubmitAndRefreshTableViewProcessor.class )
						        .configure(
								        mfvp -> mfvp.modalSelector( modalSelector )
								                    .elementName( "btn-save" )
								                    .url( ( linkBuilder, ctx ) -> linkBuilder.createView().toUriString() )
						        )
				                    ).viewProcessor( vp -> vp.createBean( ModalCancelViewProcessor.class )
				                                             .configure( mfvp -> mfvp.modalSelector( modalSelector )
				                                                                     .elementName( "btn-cancel" ) ) )
				);
	}

	public static <U extends EntityConfigurationBuilder<?>> Consumer<U> updateViewAsModal() {
		String modalId = "updateModal";
		String modalSelector = "#" + modalId;
		return configuration ->
				configuration.listView( lvb -> lvb.viewProcessor(
						vp -> vp.createBean( ModalItemActionViewProcessor.class )
						        .withName( "updateModalItemActionViewProcessor" )
						        .configure(
								        mclvp -> mclvp.modalId( modalId )
								                      .url( ( linkBuilder, ctx ) -> linkBuilder.forInstance( currentEntity( ctx ) )
								                                                               .updateView()
								                                                               .toUriString() )
								                      .partial( "content" )
								                      .actionRole( "edit" )
						        )
				) ).updateFormView(
						fvb -> fvb.viewProcessor(
								vp -> vp.createBean( SubmitAndRefreshTableViewProcessor.class )
								        .configure(
										        mfvp -> mfvp.modalSelector( modalSelector )
										                    .elementName( "btn-save" )
										                    .url( ( linkBuilder, ctx ) -> linkBuilder.forInstance( currentEntity( ctx ) )
										                                                             .updateView()
										                                                             .toUriString() )
								        )
						).viewProcessor(
								vp -> vp.provideBean( new ModalFormViewProcessor<>()
								{
									@Override
									protected void configureViewElement( ViewElement element,
									                                     EntityViewLinkBuilder linkBuilder,
									                                     ViewElementBuilderContext builderContext ) {
										String url = linkBuilder.forInstance( currentEntity( builderContext ) ).deleteView().toUriString();
										element.set( requestAction()
												             .url( url )
												             .method( HttpMethod.GET )
												             .partial( "content" )
												             .requestConfig( Map.of( "headers", Map.of( MODAL_ORIGIN_HEADER, modalId ) ) )
												             .success(
														             clearHandler( modalSelector + " .modal-title" ),
														             clearHandler( modalSelector + " .modal-footer" ),
														             clearHandler( modalSelector + " .modal-body" ),
														             requestContentHandler()
																             .source( "." + PageContentStructure.CSS_BODY_SECTION )
																             .target( modalSelector + " .modal-body" ),
														             requestContentHandler()
																             .source( ".page-header" )
																             .target( modalSelector + " .modal-title" ),
														             moveHandler()
																             .source( modalSelector + " .modal-body .em-form-actions" )
																             .target( modalSelector + " .modal-footer" ),
														             initializeFormElements( modalSelector )
												             ) );
									}
								}.elementName( "btn-delete" )
								 .modalSelector( modalSelector ) )
						).viewProcessor( vp -> vp.createBean( ModalCancelViewProcessor.class )
						                         .configure( mfvp -> mfvp.elementName( "btn-cancel" )
						                                                 .modalSelector( modalSelector ) ) )
				);
	}

	public static <U extends EntityConfigurationBuilder<?>> Consumer<U> deleteViewAsModal() {
		String modalId = "deleteModal";
		String modalSelector = "#" + modalId;
		return configuration ->
				configuration.listView( lvb -> lvb.viewProcessor(
						vp -> vp.createBean( ModalItemActionViewProcessor.class )
						        .withName( "deleteModalItemActionViewProcessor" )
						        .configure(
								        mclvp -> mclvp.modalId( modalId )
								                      .url( ( linkBuilder, ctx ) -> linkBuilder.forInstance( currentEntity( ctx ) )
								                                                               .deleteView()
								                                                               .toUriString() )
								                      .partial( "content" )
								                      .actionRole( "delete" )
						        )
				) ).deleteFormView( fvb -> fvb.viewProcessor(
						vp -> vp.createBean( SubmitAndRefreshTableViewProcessor.class )
						        .configure(
								        mfvp -> mfvp.modalSelector( modalSelector )
								                    .elementName( "btn-delete" )
								                    .url( ( linkBuilder, ctx ) -> linkBuilder.forInstance( currentEntity( ctx ) )
								                                                             .deleteView()
								                                                             .toUriString() )
						        ) ).viewProcessor( vp -> vp.createBean( ModalCancelViewProcessor.class )
				                                           .configure( mfvp -> mfvp.modalSelector( modalSelector )
				                                                                   .elementName( "btn-cancel" ) ) )
				);
	}
}
