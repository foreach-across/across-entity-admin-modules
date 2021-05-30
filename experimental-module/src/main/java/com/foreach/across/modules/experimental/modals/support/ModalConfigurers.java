package com.foreach.across.modules.experimental.modals.support;

import com.foreach.across.modules.adminweb.ui.PageContentStructure;
import com.foreach.across.modules.entity.config.builders.EntityAssociationBuilder;
import com.foreach.across.modules.entity.config.builders.EntityConfigurationBuilder;
import com.foreach.across.modules.experimental.modals.support.configurers.CreateModalConfigurer;
import com.foreach.across.modules.experimental.modals.support.configurers.DeleteModalConfigurer;
import com.foreach.across.modules.experimental.modals.support.configurers.UpdateModalConfigurer;
import com.foreach.across.modules.experimental.modals.ui.processors.ModalActionCustomizationContext;
import com.foreach.across.modules.experimental.modals.ui.processors.ModalFormViewProcessor;
import com.foreach.across.modules.experimental.webutility.support.action.ActionAttribute;
import com.foreach.across.modules.experimental.webutility.support.action.RequestActionAttribute;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.function.Consumer;
import java.util.function.Function;

import static com.foreach.across.modules.experimental.webutility.support.action.ResponseContentHandlerAttribute.responseContentHandler;
import static com.foreach.across.modules.experimental.webutility.support.action.SimpleActionHandlerAttribute.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ModalConfigurers
{
	/**
	 * Identifies the request header that allows a form to be modified for a modal.
	 * The value of the origin header should be the id of the modal that is in use.
	 *
	 * @see ModalFormViewProcessor
	 */
	public static final String MODAL_ORIGIN_HEADER = "X-MODAL-ORIGIN";

	public final static ModalConfigurers modalConfigurers = new ModalConfigurers();
	public TAssociationModalConfigurers association = new TAssociationModalConfigurers();

	public <U extends EntityConfigurationBuilder<?>> Consumer<U> createViewAsModal() {
		return createViewAsModal( ( vp ) -> {
		} );
	}

	public <U extends EntityConfigurationBuilder<?>, T extends CreateModalConfigurer<T>> Consumer<U> createViewAsModal( Consumer<T> consumer ) {
		T configurer = CreateModalConfigurer.instance();
		consumer.accept( configurer );
		return ( u ) -> configurer.consume( u );
	}

	public <U extends EntityConfigurationBuilder<?>> Consumer<U> updateViewAsModal() {
		return updateViewAsModal( ( vp ) -> {
		} );
	}

	public <U extends EntityConfigurationBuilder<?>, T extends UpdateModalConfigurer<T>> Consumer<U> updateViewAsModal( Consumer<T> consumer ) {
		T configurer = UpdateModalConfigurer.instance();
		consumer.accept( configurer );
		return ( u ) -> configurer.consume( u );
	}

	public <U extends EntityConfigurationBuilder<?>> Consumer<U> deleteViewAsModal() {
		return deleteViewAsModal( ( vp ) -> {
		} );
	}

	public <U extends EntityConfigurationBuilder<?>, T extends DeleteModalConfigurer<T>> Consumer<U> deleteViewAsModal( Consumer<T> consumer ) {
		T configurer = DeleteModalConfigurer.instance();
		consumer.accept( configurer );
		return ( u ) -> configurer.consume( u );
	}

	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	public class TAssociationModalConfigurers
	{
		public <U extends EntityAssociationBuilder> Consumer<U> createViewAsModal() {
			return createViewAsModal( ( vp ) -> {
			} );
		}

		public <U extends EntityAssociationBuilder, T extends CreateModalConfigurer<T>> Consumer<U> createViewAsModal( Consumer<T> consumer ) {
			T configurer = CreateModalConfigurer.<T>instance()
					.modal( vp -> vp.action( reconfigureModalActionHandlers() ) )
					.submit( vp -> vp.action( reconfigureModalSubmissionAction() ) );
			consumer.accept( configurer );
			return ( u ) -> configurer.consume( u );
		}

		public <U extends EntityAssociationBuilder> Consumer<U> updateViewAsModal() {
			return updateViewAsModal( ( vp ) -> {
			} );
		}

		public <U extends EntityAssociationBuilder, T extends UpdateModalConfigurer<T>> Consumer<U> updateViewAsModal( Consumer<T> consumer ) {
			T configurer = UpdateModalConfigurer.<T>instance()
					.modal( vp -> vp.action( reconfigureModalActionHandlers() ) )
					.delete( vp -> vp.action( reconfigureModalActionHandlers() ) )
					.submit( vp -> vp.action( reconfigureModalSubmissionAction() ) );
			consumer.accept( configurer );
			return ( u ) -> configurer.consume( u );
		}

		public <U extends EntityAssociationBuilder> Consumer<U> deleteViewAsModal() {
			return deleteViewAsModal( ( vp ) -> {
			} );
		}

		public <U extends EntityAssociationBuilder, T extends DeleteModalConfigurer<T>> Consumer<U> deleteViewAsModal( Consumer<T> consumer ) {
			T configurer = DeleteModalConfigurer.<T>instance()
					.modal( vp -> vp.action( reconfigureModalActionHandlers() ) )
					.submit( vp -> vp.action( reconfigureModalSubmissionAction() ) );
			consumer.accept( configurer );
			return ( u ) -> configurer.consume( u );
		}

		private Function<ModalActionCustomizationContext<RequestActionAttribute>, ActionAttribute> reconfigureModalActionHandlers() {
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

		private Function<ModalActionCustomizationContext<RequestActionAttribute>, ActionAttribute> reconfigureModalSubmissionAction() {
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

	}
}
