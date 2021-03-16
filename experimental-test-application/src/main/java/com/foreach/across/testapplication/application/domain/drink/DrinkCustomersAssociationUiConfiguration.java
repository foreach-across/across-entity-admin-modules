package com.foreach.across.testapplication.application.domain.drink;

import com.foreach.across.modules.entity.config.EntityConfigurer;
import com.foreach.across.modules.entity.config.builders.EntitiesConfigurationBuilder;
import com.foreach.across.modules.entity.registry.EntityAssociation;
import com.foreach.across.modules.experimental.modals.support.ModalConfigurers;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DrinkCustomersAssociationUiConfiguration implements EntityConfigurer
{
	@Override
	public void configure( EntitiesConfigurationBuilder entities ) {
		entities.withType( Drink.class )
		        .association(
				        ab -> ab.name( "customer.drink" )
				                .associationType( EntityAssociation.Type.EMBEDDED )
				                .and( ModalConfigurers.modalConfigurers.association.createViewAsModal() )
				                .and( ModalConfigurers.modalConfigurers.association.updateViewAsModal() )
				                .and( ModalConfigurers.modalConfigurers.association.deleteViewAsModal() )
				                .listView(
						                lvb -> lvb.entityQueryFilter(
								                eqf -> eqf.showProperties( "name" )
						                )
				                )
		        );
	}
}
