package com.foreach.across.modules.experimental.modals.ui.processors;

import com.foreach.across.modules.entity.web.links.EntityViewLinkBuilder;
import com.foreach.across.modules.experimental.webutility.support.action.ActionAttribute;
import com.foreach.across.modules.experimental.webutility.support.action.RequestActionAttribute;
import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.http.HttpMethod;

import java.util.function.BiFunction;
import java.util.function.Function;

import static com.foreach.across.modules.experimental.webutility.support.action.RequestActionAttribute.requestAction;
import static com.foreach.across.modules.experimental.webutility.support.action.RequestActionHandlerAttribute.requestActionHandler;
import static com.foreach.across.modules.experimental.webutility.support.action.ResponseContentHandlerAttribute.responseContentHandler;
import static com.foreach.across.modules.experimental.webutility.support.action.SimpleActionHandlerAttribute.*;

/**
 * Configures the referenced {@link ViewElement} to submit a given modal on click.
 * The customization is only applied if a {@link com.foreach.across.modules.experimental.modals.support.ModalConfigurers#MODAL_ORIGIN_HEADER} header
 * is present that references the id of the modal.
 * </p>
 * If a validation error occurs after submission, the body of the modal will be refreshed to show the updated state.
 * If the form within the modal is succesfully submitted, the modal will close and refresh the table on the current view.
 */
@Accessors(fluent = true, chain = true)
public class ModalSubmitAndRefreshTableViewProcessor extends ModalFormViewProcessor<ModalSubmitAndRefreshTableViewProcessor>
{
	@NonNull
	private BiFunction<EntityViewLinkBuilder, ViewElementBuilderContext, String> url;

	@Setter(AccessLevel.PROTECTED)
	private Function<ModalActionCustomizationContext<RequestActionAttribute>, ActionAttribute> actionCustomizer = ModalActionCustomizationContext::action;

	public ModalSubmitAndRefreshTableViewProcessor url( BiFunction<EntityViewLinkBuilder, ViewElementBuilderContext, String> url ) {
		this.url = url;
		return self();
	}

	// SELF referencing , how? :(
	public ModalSubmitAndRefreshTableViewProcessor action( Function<ModalActionCustomizationContext<RequestActionAttribute>, ActionAttribute> actionCustomizer ) {
		this.actionCustomizer = actionCustomizer;
		return self();
	}

	@Override
	protected void configureViewElement( ViewElement element, EntityViewLinkBuilder linkBuilder, ViewElementBuilderContext builderContext ) {
		RequestActionAttribute actionAttribute = requestAction()
				.url( url.apply( linkBuilder, builderContext ) )
				.method( HttpMethod.POST )
				.partial( "::body" )
				.form( getModalSelector() + " .modal-body form" )
				.success(
						clearHandler( getModalSelector() + " .modal-body" ),
						responseContentHandler()
								.target( getModalSelector() + " .modal-body" ),
						removeHandler( getModalSelector() + " .modal-body .em-form-actions" ),
						initializeFormElements( getModalSelector() + " .modal-body" )
				)
				.redirect(
						requestActionHandler()
								.form( "form.em-list-form" )
								.partial( "::itemsTable" )
								.target( ".exm-table-refresh-target" ),
						closeModalHandler( getModalSelector() ),
						initializeFormElements( ".em-sortableTable-panel" )
				);

		ModalActionCustomizationContext<RequestActionAttribute> customizationContext =
				new ModalActionCustomizationContext<>( getModalSelector(), ( selector ) -> getModalSelector() + " " + selector, actionAttribute,
				                                       builderContext );
		element.set( actionCustomizer.apply( customizationContext ) );
	}
}
