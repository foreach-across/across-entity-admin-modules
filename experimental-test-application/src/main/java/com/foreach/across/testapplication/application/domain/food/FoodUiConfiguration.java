package com.foreach.across.testapplication.application.domain.food;

import com.foreach.across.modules.adminweb.AdminWeb;
import com.foreach.across.modules.entity.config.EntityConfigurer;
import com.foreach.across.modules.entity.config.builders.EntitiesConfigurationBuilder;
import com.foreach.across.modules.entity.config.builders.EntityViewFactoryBuilder;
import com.foreach.across.modules.entity.views.util.EntityViewElementUtils;
import com.foreach.across.modules.entity.web.EntityViewModel;
import com.foreach.across.modules.experimental.bulkactions.support.SimpleBulkActionItemConfigurer;
import com.foreach.across.modules.experimental.bulkactions.ui.viewprocessors.BulkActionViewProcessor;
import com.foreach.across.modules.experimental.modals.support.ModalConfigurers;
import com.foreach.across.testapplication.application.domain.food.processors.FoodBulkActionViewProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.Collections;
import java.util.function.Consumer;

import static com.foreach.across.modules.experimental.bulkactions.support.BulkActionsEntityConfigurers.configureBulkActions;
import static com.foreach.across.modules.web.ui.elements.HtmlViewElement.Functions.attribute;
import static com.foreach.across.testapplication.application.domain.food.controllers.FoodBulkActionsController.FOOD_BULK_ACTIONS;
import static org.apache.commons.lang3.StringUtils.containsIgnoreCase;
import static org.apache.commons.lang3.StringUtils.join;
import static org.springframework.util.ClassUtils.getShortName;

@Configuration
@RequiredArgsConstructor
public class FoodUiConfiguration implements EntityConfigurer
{
	private final AdminWeb adminWeb;

	@Override
	public void configure( EntitiesConfigurationBuilder entities ) {
		entities.withType( Food.class )
		        .and( ModalConfigurers.createViewAsModal() )
		        .and( ModalConfigurers.updateViewAsModal() )
		        .and( ModalConfigurers.deleteViewAsModal() )
		        .listView(
				        lvb -> lvb.viewProcessor( vp -> vp.createBean( FoodBulkActionViewProcessor.class )
				                                          .order( 1100 ) )
				                  .and( bulkActionsConfigurer( join( "extensions[", getShortName( FoodBulkActionViewProcessor.class ), "].selectedItems" ) ) )
		        )
		        .listView( "controller",
		                   lvb -> lvb.viewProcessor( vp -> vp.createBean( FoodBulkActionViewProcessor.class )
		                                                     .configure( bavp -> bavp.setControlName( () -> "" ) )
		                                                     .order( 1100 ) )

		                             .and( bulkActionsConfigurer( "selectedItems" ) )
		                             .and(
				                             vfb -> vfb.viewProcessor( vp -> vp.withType( BulkActionViewProcessor.class )
				                                                               .skipIfMissing()
				                                                               .configure( bavp -> bavp
						                                                               .submitUrlResolver( ( req ) -> adminWeb.path( FOOD_BULK_ACTIONS ) )
						                                                               .formAttributeProvider( () -> "bulkActions" ) ) )
		                             )
		        )
		;
	}

	private Consumer<EntityViewFactoryBuilder> bulkActionsConfigurer( String controlName ) {
		SimpleBulkActionItemConfigurer<Food> bulkActionsConfiguration =
				new SimpleBulkActionItemConfigurer<Food>()
						.itemSelectorControlPostProcessor( ( ctx, builder ) -> {
							Food instance = EntityViewElementUtils.currentEntity( ctx, Food.class );
							if ( instance.getCurrentAction() == FoodAction.STORED ) {
								if ( containsIgnoreCase( instance.getName(), "pizza" ) ) {
									builder.set( attribute( "supportedActions", Collections.singletonList( FoodActionType.BAKE_OVEN ) ) );
								}
								else {
									builder.set( attribute( "supportedActions", Arrays.asList( FoodActionType.BAKE_OVEN, FoodActionType.BAKE_STOVE ) ) );
								}
							}
							else {
								builder.set( attribute( "supportedActions", Collections.singletonList( FoodActionType.RESET ) ) );
							}
						} )
						.itemValue( ( instance, ctx ) -> instance.getId() )
						.itemControlName( controlName )
						.formAttributeName( EntityViewModel.VIEW_COMMAND + ".extensions[" + getShortName( FoodBulkActionViewProcessor.class ) + "]" );
		return configureBulkActions( bulkActionsConfiguration );
	}
}
