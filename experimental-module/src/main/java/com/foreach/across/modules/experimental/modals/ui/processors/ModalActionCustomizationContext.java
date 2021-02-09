package com.foreach.across.modules.experimental.modals.ui.processors;

import com.foreach.across.modules.experimental.webutility.support.action.RequestActionAttribute;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.function.Function;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class ModalActionCustomizationContext
{
	private final String modalSelector;
	private final Function<String, String> modalSelectorProvider;
	private final RequestActionAttribute requestActionAttribute;
	private final ViewElementBuilderContext viewElementBuilderContext;

	/**
	 * The original action that is configured for the modal. If nothing is customized, this action will be used.
	 */
	public RequestActionAttribute action() {
		return requestActionAttribute;
	}

	/**
	 * {@link ViewElementBuilderContext} that is used to render the modal.
	 */
	public ViewElementBuilderContext builderContext() {
		return viewElementBuilderContext;
	}

	/**
	 * Creates a CSS3 selector for an element within the modal being configured.
	 * The given selector is automatically prefixed with the modal selector.
	 *
	 * @param target element within the modal to be selected
	 */
	public String modalTarget( String target ) {
		return modalSelectorProvider.apply( target );
	}

	/**
	 * Returns the CSS3 selector for the modal being configured.
	 */
	public String modalSelector() {
		return modalSelector;
	}

}
