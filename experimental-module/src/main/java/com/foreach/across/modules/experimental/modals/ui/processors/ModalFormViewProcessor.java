package com.foreach.across.modules.experimental.modals.ui.processors;

import com.foreach.across.modules.entity.views.EntityView;
import com.foreach.across.modules.entity.views.processors.EntityViewProcessorAdapter;
import com.foreach.across.modules.entity.views.request.EntityViewRequest;
import com.foreach.across.modules.entity.web.links.EntityViewLinkBuilder;
import com.foreach.across.modules.experimental.modals.support.ModalConfigurers;
import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.elements.ContainerViewElement;
import com.foreach.across.modules.web.ui.elements.support.ContainerViewElementUtils;
import lombok.Getter;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;

/**
 * Supports modifying a default {@link EntityView} based when the {@link com.foreach.across.modules.experimental.modals.support.ModalConfigurers#MODAL_ORIGIN_HEADER} header is present.
 * If the header is present, the view is customized to support the modal referenced by its header value.
 */
public abstract class ModalFormViewProcessor<T extends ModalFormViewProcessor> extends EntityViewProcessorAdapter
{
	@Getter
	private String elementName;

	@Getter
	private String modalSelector;

	public T modalSelector( @NonNull String modalSelector ) {
		this.modalSelector = modalSelector;
		return (T) this;
	}

	public T elementName( @NonNull String elementName ) {
		this.elementName = elementName;
		return (T) this;
	}

	@Override
	protected void postRender( EntityViewRequest entityViewRequest,
	                           EntityView entityView,
	                           ContainerViewElement container,
	                           ViewElementBuilderContext builderContext ) {
		String modalName = entityViewRequest.getWebRequest().getHeader( ModalConfigurers.MODAL_ORIGIN_HEADER );
		if ( StringUtils.isNotBlank( modalName ) ) {
			modalSelector( "#" + modalName );
			EntityViewLinkBuilder linkBuilder = entityViewRequest.getEntityViewContext().getLinkBuilder();
			ContainerViewElementUtils.find( container, elementName )
			                         .ifPresent(
					                         ve -> configureViewElement( ve, linkBuilder, builderContext )
			                         );
		}
	}

	protected abstract void configureViewElement( ViewElement element, EntityViewLinkBuilder linkBuilder, ViewElementBuilderContext builderContext );
}
