package com.foreach.across.modules.experimental.modals.ui.processors;

import com.foreach.across.modules.entity.web.links.EntityViewLinkBuilder;
import com.foreach.across.modules.experimental.webutility.support.action.ActionAttribute;
import com.foreach.across.modules.experimental.webutility.support.action.SimpleActionAttribute;
import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import lombok.AccessLevel;
import lombok.Setter;

import java.util.function.Function;

import static com.foreach.across.modules.experimental.webutility.support.action.SimpleActionAttribute.simpleAction;
import static com.foreach.across.modules.experimental.webutility.support.action.SimpleActionHandlerAttribute.closeModalHandler;

/**
 * Configures the referenced {@link ViewElement} to close a given modal on click.
 * The customization is only applied if a {@link com.foreach.across.modules.experimental.modals.support.ModalConfigurers#MODAL_ORIGIN_HEADER} header
 * is present that references the id of the modal. The modal corresponding to the id will be closed.
 */
public class ModalCancelViewProcessor extends ModalFormViewProcessor<ModalCancelViewProcessor>
{

	@Setter(AccessLevel.PROTECTED)
	private Function<ModalActionCustomizationContext<SimpleActionAttribute>, ActionAttribute> actionCustomizer = ModalActionCustomizationContext::action;

	/**
	 * Supports customizing the default action attribute that is registered to fetch the modal content.
	 * Offers more advanced configuration as well as overriding of previously configured methods.
	 * </p>
	 * The customizer should return the final action that should be used. Overrides applied through the customizer always take precedence.
	 */
	public ModalCancelViewProcessor action( Function<ModalActionCustomizationContext<SimpleActionAttribute>, ActionAttribute> actionCustomizer ) {
		this.actionCustomizer = actionCustomizer;
		return this;
	}

	@Override
	protected void configureViewElement( ViewElement element, EntityViewLinkBuilder linkBuilder, ViewElementBuilderContext builderContext ) {
		SimpleActionAttribute action = simpleAction().handlers( closeModalHandler( getModalSelector() ) );
		ModalActionCustomizationContext<SimpleActionAttribute> ctx =
				new ModalActionCustomizationContext<>( getModalSelector(), ( x ) -> getModalSelector() + " " + x, action, builderContext );
		element.set( actionCustomizer.apply( ctx ) );
	}
}
