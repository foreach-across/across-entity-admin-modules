package com.foreach.across.testapplication.application.domain.food;

import com.foreach.across.modules.bootstrapui.elements.ButtonViewElement;
import com.foreach.across.modules.bootstrapui.elements.FormViewElement;
import com.foreach.across.modules.bootstrapui.ui.factories.BootstrapViewElements;
import com.foreach.across.modules.entity.config.EntityConfigurer;
import com.foreach.across.modules.entity.config.builders.EntitiesConfigurationBuilder;
import com.foreach.across.modules.entity.views.EntityView;
import com.foreach.across.modules.entity.views.processors.ExtensionViewProcessorAdapter;
import com.foreach.across.modules.entity.views.request.EntityViewCommand;
import com.foreach.across.modules.entity.views.request.EntityViewRequest;
import com.foreach.across.modules.experimental.bulkactions.configurers.BulkActionsEntityConfigurer;
import com.foreach.across.modules.experimental.bulkactions.configurers.SimpleBulkActionItemConfigurer;
import com.foreach.across.modules.experimental.bulkactions.domain.BulkAction;
import com.foreach.across.modules.experimental.bulkactions.domain.BulkActionSet;
import com.foreach.across.modules.experimental.bulkactions.domain.SimpleBulkIdentifier;
import com.foreach.across.modules.experimental.bulkactions.support.BulkActionHandler;
import com.foreach.across.modules.experimental.bulkactions.support.BulkIdentifierBuilder;
import com.foreach.across.modules.experimental.bulkactions.viewprocessors.BulkActionViewProcessor;
import com.foreach.across.modules.experimental.entitycontrols.domain.EntityControlFactory;
import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.elements.ContainerViewElement;
import com.foreach.across.modules.web.ui.elements.support.ContainerViewElementUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;

import java.util.Arrays;
import java.util.Map;

import static com.foreach.across.modules.entity.views.util.EntityViewElementUtils.currentEntity;
import static com.foreach.across.testapplication.application.domain.food.FoodBulkActionHandler.DELETE_ACTION;
import static com.foreach.across.modules.web.ui.elements.HtmlViewElement.Functions.attribute;

@Configuration
@RequiredArgsConstructor
public class FoodUiConfiguration implements EntityConfigurer
{
	private final FoodBulkActionHandler foodBulkActionHandler;

	@Override
	public void configure( EntitiesConfigurationBuilder entities ) {
		entities.withType( Food.class )
		        .attribute( BulkActionHandler.class, foodBulkActionHandler )
		        .attribute( BulkActionSet.class, BulkActionSet.of(
				        BulkAction.of( DELETE_ACTION, "bulk-delete-entity" )
		        ) )
		        .attribute(
				        BulkIdentifierBuilder.class,
				        context -> {
					        Food food = currentEntity( context, Food.class );
					        return SimpleBulkIdentifier.of(
							        food.getId().toString(),
							        Map.of( "data-allowed-bulk-actions", new String[] { DELETE_ACTION } )
					        );
				        }
		        )
		        .listView(
				        lvb -> lvb.viewProcessor( vp -> vp.createBean( FoodBulkActionViewProcessor.class ).order( 1100 ) )
				                  .and( BulkActionsEntityConfigurer.configureBulkActions(
						                  new SimpleBulkActionItemConfigurer<Food>()
								                  .identifierResolver( ( ctx, instance ) -> instance.getId() )
								                  .controlConfigurer( ( ctx, instance, builder ) -> {
									                  if ( StringUtils.containsIgnoreCase( instance.getName(), "pizza" ) ) {
										                  builder.with( attribute( "supportedActions",
										                                           Arrays.asList( FoodActionType.BAKE_OVEN, FoodActionType.RESET ) ) );
									                  }
									                  else {
										                  builder.with( attribute( "supportedActions",
										                                           Arrays.asList( FoodActionType.BAKE_OVEN, FoodActionType.BAKE_STOVE,
										                                                          FoodActionType.RESET ) ) );
									                  }
								                  } )
				                  ) ) );
	}

	@RequiredArgsConstructor
	static class FoodBulkActionViewProcessor extends ExtensionViewProcessorAdapter<BulkActionsHolder>
	{
		private final EntityControlFactory entityControlFactory;

		@Override
		protected BulkActionsHolder createExtension( EntityViewRequest entityViewRequest, EntityViewCommand command, WebDataBinder dataBinder ) {
			BulkActionViewProcessor.SelectedItemsHolder itemsHolder = command.getExtension( BulkActionViewProcessor.class.getName(),
			                                                                                BulkActionViewProcessor.SelectedItemsHolder.class );
			BulkActionsHolder actionsHolder = new BulkActionsHolder();
			actionsHolder.setItemsHolder( itemsHolder );
			return actionsHolder;
		}

		@Override
		protected void postRender( BulkActionsHolder extension,
		                           EntityViewRequest entityViewRequest,
		                           EntityView entityView,
		                           ContainerViewElement container,
		                           ViewElementBuilderContext builderContext ) {
			Map<String, ViewElement> controls = entityControlFactory.createControlsForClass( BulkActionsHolder.class )
			                                                        .forInstance( extension )
			                                                        .showProperties( "action" )
			                                                        .build( builderContext );
			ContainerViewElementUtils.find( container, BulkActionViewProcessor.BULK_ACTION_FORM_NAME, FormViewElement.class )
			                         .ifPresent(
					                         fve -> fve.addChildren( controls.values() )
					                                   .addChild( BootstrapViewElements.bootstrap.builders.button()
					                                                                                      .type( ButtonViewElement.Type.BUTTON_SUBMIT )
					                                                                                      .text( "Submit" )
					                                                                                      .build( builderContext ) )
			                         );
		}

		@Override
		protected void doPost( BulkActionsHolder extension, BindingResult bindingResult, EntityView entityView, EntityViewRequest entityViewRequest ) {
			super.doPost( extension, bindingResult, entityView, entityViewRequest );
		}
	}

	@Getter
	@Setter
	static class BulkActionsHolder
	{
		private FoodActionType action;
		private BulkActionViewProcessor.SelectedItemsHolder itemsHolder;
	}
}
