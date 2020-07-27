package com.foreach.across.modules.experimental.modals.support;

import com.foreach.across.modules.bootstrapui.elements.ButtonViewElement;
import com.foreach.across.modules.entity.config.builders.EntityViewFactoryBuilder;
import com.foreach.across.modules.entity.views.EntityView;
import com.foreach.across.modules.entity.views.context.EntityViewContext;
import com.foreach.across.modules.entity.views.processors.EntityViewProcessorAdapter;
import com.foreach.across.modules.entity.views.request.EntityViewRequest;
import com.foreach.across.modules.entity.views.support.EntityMessages;
import com.foreach.across.modules.experimental.modals.web.components.ModalViewElementBuilder;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.elements.ContainerViewElement;
import com.foreach.across.modules.web.ui.elements.support.ContainerViewElementUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;
import java.util.function.Consumer;

import static com.foreach.across.modules.web.ui.elements.HtmlViewElement.Functions.data;
import static com.foreach.across.modules.web.ui.elements.TextViewElement.text;

public class ModalConfigurers
{
	public static Consumer<EntityViewFactoryBuilder> linkCreateToModal() {
		return vfb -> vfb.viewProcessor( vp -> vp.provideBean( new EntityViewProcessorAdapter()
		{
			@Override
			protected void postRender( EntityViewRequest entityViewRequest,
			                           EntityView entityView,
			                           ContainerViewElement container,
			                           ViewElementBuilderContext builderContext ) {

				ContainerViewElementUtils.find( container, "btn-create", ButtonViewElement.class )
				                         .ifPresent(
						                         btn -> {
							                         String modalId = "createModal";
							                         btn.set( data( "toggle", "modal" ), data( "target", StringUtils.join( "#", modalId ) ) );
							                         registerModal( entityViewRequest.getEntityViewContext(), container, modalId, builderContext );
						                         }
				                         );

			}

			private void registerModal( EntityViewContext entityViewContext,
			                            ContainerViewElement container,
			                            String modalId,
			                            ViewElementBuilderContext builderContext ) {
				EntityMessages entityMessages = new EntityMessages(
						entityViewContext.getMessageCodeResolver().prefixedResolver( "views[" + EntityView.CREATE_VIEW_NAME + "]" ) );

				ModalViewElementBuilder modal = new ModalViewElementBuilder()
						.name( modalId )
						.centered( true )
						.body( text(
								"Odi et amo, quare id faciam, fortasse requiris? Nescio, sed fieri sentio et excrucior." ) );

				Optional.ofNullable(
						StringUtils.defaultIfEmpty( entityMessages.withNameSingular( EntityMessages.PAGE_TITLE_CREATE,
						                                                             entityViewContext.getEntityLabel() ),
						                            null ) )
				        .ifPresent( modal::title );

				container.addChild( modal.build( builderContext ) );
			}
		} ) );
	}
}
