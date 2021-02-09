package com.foreach.across.modules.experimental.modals.support;

import com.foreach.across.modules.adminweb.ui.PageContentStructure;
import com.foreach.across.modules.entity.config.builders.EntityAssociationBuilder;
import com.foreach.across.modules.entity.config.builders.EntityViewProcessorConfigurer;
import com.foreach.across.modules.entity.views.EntityViewProcessor;
import com.foreach.across.modules.entity.web.links.EntityViewLinkBuilder;
import com.foreach.across.modules.experimental.modals.ui.processors.*;
import com.foreach.across.modules.experimental.webutility.support.action.ActionAttribute;
import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.google.common.collect.ImmutableMap;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpMethod;

import java.util.function.Consumer;
import java.util.function.Function;

import static com.foreach.across.modules.entity.views.util.EntityViewElementUtils.currentEntity;
import static com.foreach.across.modules.experimental.modals.support.ModalConfigurers.MODAL_ORIGIN_HEADER;
import static com.foreach.across.modules.experimental.webutility.support.action.RequestActionAttribute.requestAction;
import static com.foreach.across.modules.experimental.webutility.support.action.ResponseContentHandlerAttribute.responseContentHandler;
import static com.foreach.across.modules.experimental.webutility.support.action.SimpleActionHandlerAttribute.*;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class AssociationModalConfigurers
{
	// how can we do deduplication of code?

	/**
	 * Creates a bootstrap modal with id {@code createModal} that is linked to the create button on the listView.
	 */
	public <U extends EntityAssociationBuilder> Consumer<U> createViewAsModal() {
		String modalId = "createModal";
		String modalSelector = "#" + modalId;
		return configuration ->
				configuration
						.listView( lvb -> lvb.viewProcessor(
								vp -> vp.createBean( ModalCreateButtonListViewProcessor.class )
								        .configure(
										        mcblvp -> mcblvp.modalId( modalId )
										                        .url( ( linkBuilder, ctx ) -> linkBuilder.createView().toUriString() )
										                        .partial( "content" )
										                        .action( reconfigureModalActionHandlers() )
								        )
						) )
						.createFormView( fvb -> fvb.viewProcessor(
								vp -> vp.createBean( ModalSubmitAndRefreshTableViewProcessor.class )
								        .configure(
										        mfvp -> mfvp.modalSelector( modalSelector )
										                    .elementName( "btn-save" )
										                    .url( ( linkBuilder, ctx ) -> linkBuilder.createView().toUriString() )
										                    .action( reconfigureModalSubmissionAction() )
								        )
						                 ).viewProcessor( customizeFormViewCancelButton( modalSelector ) )
						);
	}

	/**
	 * Creates a bootstrap modal with id {@code updateModal} that is linked to the update action for each row on the listView.
	 */
	public <U extends EntityAssociationBuilder> Consumer<U> updateViewAsModal() {
		String modalId = "updateModal";
		String modalSelector = "#" + modalId;
		return configuration ->
				configuration
						.listView( lvb -> lvb.viewProcessor(
								vp -> vp.createBean( ModalItemActionViewProcessor.class )
								        .withName( "updateModalItemActionViewProcessor" )
								        .configure(
										        mclvp -> mclvp.modalId( modalId )
										                      .url( ( linkBuilder, ctx ) -> linkBuilder.forInstance( currentEntity( ctx ) )
										                                                               .updateView()
										                                                               .toUriString() )
										                      .partial( "content" )
										                      .actionRole( "edit" )
										                      .action( reconfigureModalActionHandlers() )
								        )
						) )
						.updateFormView(
								fvb -> fvb.viewProcessor(
										vp -> vp.createBean( ModalSubmitAndRefreshTableViewProcessor.class )
										        .configure(
												        mfvp -> mfvp.modalSelector( modalSelector )
												                    .elementName( "btn-save" )
												                    .url( ( linkBuilder, ctx ) -> linkBuilder.forInstance( currentEntity( ctx ) )
												                                                             .updateView()
												                                                             .toUriString() )
												                    .action( reconfigureModalSubmissionAction() )
										        )
								).viewProcessor(
										vp -> vp.provideBean( new ModalFormViewProcessor()
										{
											private String modalTarget( String target ) {
												return getModalSelector() + " " + target;
											}

											@Override
											protected void configureViewElement( ViewElement element,
											                                     EntityViewLinkBuilder linkBuilder,
											                                     ViewElementBuilderContext builderContext ) {
												String url = linkBuilder.forInstance( currentEntity( builderContext ) ).deleteView().toUriString();
												element.set( requestAction()
														             .url( url )
														             .method( HttpMethod.GET )
														             .partial( "content" )
														             .requestConfig(
																             ImmutableMap.of( "headers", ImmutableMap.of( MODAL_ORIGIN_HEADER, modalId ) ) )
														             .success(
																             clearHandler( modalTarget( ".modal-title" ) ),
																             clearHandler( modalTarget( ".modal-footer" ) ),
																             clearHandler( modalTarget( ".modal-body" ) ),
																             responseContentHandler()
																		             .source( "." + PageContentStructure.CSS_BODY_SECTION )
																		             .target( modalTarget( ".modal-body" ) ),
																             moveHandler()
																		             .source( ".tab-pane-header h4" )
																		             .target( modalTarget( ".modal-title" ) ),
																             removeHandler( modalTarget( ".modal-body .tab-pane-header" ) ),
																             moveHandler()
																		             .source( modalTarget( ".modal-body .em-form-actions" ) )
																		             .target( modalTarget( ".modal-footer" ) ),
																             initializeFormElements( getModalSelector() )
														             ) );
											}
										}.elementName( "btn-delete" )
										 .modalSelector( modalSelector ) )
								).viewProcessor( customizeFormViewCancelButton( modalSelector ) )
						);
	}

	/**
	 * Creates a bootstrap modal with id {@code updateModal} that is linked to the delete action for each row on the listView.
	 */
	public <U extends EntityAssociationBuilder> Consumer<U> deleteViewAsModal() {
		String modalId = "deleteModal";
		String modalSelector = "#" + modalId;
		return configuration ->
				configuration
						.listView( lvb -> lvb.viewProcessor(
								vp -> vp.createBean( ModalItemActionViewProcessor.class )
								        .withName( "deleteModalItemActionViewProcessor" )
								        .configure(
										        mclvp -> mclvp.modalId( modalId )
										                      .url( ( linkBuilder, ctx ) -> linkBuilder.forInstance( currentEntity( ctx ) )
										                                                               .deleteView()
										                                                               .toUriString() )
										                      .partial( "content" )
										                      .actionRole( "delete" )
										                      .action( reconfigureModalActionHandlers() )
								        )
						) )
						.deleteFormView( fvb -> fvb.viewProcessor(
								vp -> vp.createBean( ModalSubmitAndRefreshTableViewProcessor.class )
								        .configure(
										        mfvp -> mfvp.modalSelector( modalSelector )
										                    .elementName( "btn-delete" )
										                    .url( ( linkBuilder, ctx ) -> linkBuilder.forInstance( currentEntity( ctx ) )
										                                                             .deleteView()
										                                                             .toUriString() )
										                    .action( reconfigureModalSubmissionAction() )
								        ) ).viewProcessor( customizeFormViewCancelButton( modalSelector ) )
						);
	}

	private Function<ModalActionCustomizationContext, ActionAttribute> reconfigureModalSubmissionAction() {
		return ( ctx ) ->
				ctx.action()
				   .success(
						   clearHandler( ctx.modalTarget( ".modal-body" ) ),
						   responseContentHandler()
								   .target( ctx.modalTarget( ".modal-body" ) ),
						   removeHandler( ctx.modalTarget( ".modal-body .em-form-actions" ) ),
						   removeHandler( ctx.modalTarget( ".modal-body .tab-pane-header" ) ),
						   initializeFormElements( ctx.modalTarget( ".modal-body" ) )
				   );
	}

	private Function<ModalActionCustomizationContext, ActionAttribute> reconfigureModalActionHandlers() {
		return ( ctx ) ->
				ctx.action()
				   .success(
						   clearHandler( ctx.modalTarget( ".modal-title" ) ),
						   clearHandler( ctx.modalTarget( ".modal-footer" ) ),
						   clearHandler( ctx.modalTarget( ".modal-body" ) ),
						   responseContentHandler()
								   .source( "." + PageContentStructure.CSS_BODY_SECTION )
								   .target( ctx.modalTarget( ".modal-body" ) ),
						   moveHandler()
								   .source( ctx.modalTarget( ".tab-pane-header h4" ) )
								   .target( ctx.modalTarget( ".modal-title" ) ),
						   removeHandler( ctx.modalTarget( ".modal-body .tab-pane-header" ) ),
						   moveHandler()
								   .source( ctx.modalTarget( ".modal-body .em-form-actions" ) )
								   .target( ctx.modalTarget( ".modal-footer" ) ),
						   initializeFormElements( ctx.modalSelector() )
				   );
	}

	private Consumer<EntityViewProcessorConfigurer<? extends EntityViewProcessor>> customizeFormViewCancelButton( String modalSelector ) {
		return vp -> vp.createBean( ModalCancelViewProcessor.class )
		               .configure( mfvp -> mfvp.elementName( "btn-cancel" ).modalSelector( modalSelector ) );
	}
}
