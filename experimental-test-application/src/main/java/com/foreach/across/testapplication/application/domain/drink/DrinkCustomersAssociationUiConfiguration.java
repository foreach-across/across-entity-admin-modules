package com.foreach.across.testapplication.application.domain.drink;

import com.foreach.across.modules.entity.config.EntityConfigurer;
import com.foreach.across.modules.entity.config.builders.EntitiesConfigurationBuilder;
import com.foreach.across.modules.entity.registry.EntityAssociation;
import com.foreach.across.modules.experimental.modals.support.ModalConfigurers;
import com.foreach.across.modules.experimental.modals.ui.processors.ModalSubmitAndRefreshTableViewProcessor;
import org.springframework.context.annotation.Configuration;

import static com.foreach.across.modules.experimental.webutility.support.action.RequestActionHandlerAttribute.requestActionHandler;
import static com.foreach.across.modules.experimental.webutility.support.action.SimpleActionHandlerAttribute.closeModalHandler;
import static com.foreach.across.modules.experimental.webutility.support.action.SimpleActionHandlerAttribute.initializeFormElements;

@Configuration
public class DrinkCustomersAssociationUiConfiguration implements EntityConfigurer
{
	@Override
	public void configure( EntitiesConfigurationBuilder entities ) {
		entities.withType( Drink.class )
		        .association(
				        ab -> ab.name( "customer.drink" )
				                .associationType( EntityAssociation.Type.EMBEDDED )
				                .and( ModalConfigurers.association.createViewAsModal() )
				                .and( ModalConfigurers.association.updateViewAsModal() )
				                .and( ModalConfigurers.association.deleteViewAsModal() )
				                .listView(
						                lvb -> lvb.entityQueryFilter(
								                eqf -> eqf.showProperties( "name" )
						                )
				                )
				                .createOrUpdateFormView(
						                fvb -> fvb.viewProcessor(
								                vp -> vp.withType( ModalSubmitAndRefreshTableViewProcessor.class )
								                        .skipIfMissing()
								                        .configure(
										                        msartvp -> msartvp.action(
												                        ctx -> ctx.action()
												                                  .redirect(
														                                  requestActionHandler()
																                                  .copyOriginalRequestParameters( true )
																                                  .form( "form.em-list-form" )
																                                  .partial( "::itemsTable" )
																                                  .target( ".exm-table-refresh-target" ),
														                                  closeModalHandler( ctx.modalSelector() ),
														                                  initializeFormElements( ".em-sortableTable-panel" )
												                                  )
										                        )
								                        )
						                )
				                )
		        );
	}
}
