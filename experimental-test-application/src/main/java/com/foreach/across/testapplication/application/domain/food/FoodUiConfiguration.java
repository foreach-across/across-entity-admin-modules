package com.foreach.across.testapplication.application.domain.food;

import com.foreach.across.modules.bootstrapui.elements.*;
import com.foreach.across.modules.bootstrapui.ui.factories.BootstrapViewElements;
import com.foreach.across.modules.entity.config.EntityConfigurer;
import com.foreach.across.modules.entity.config.builders.EntitiesConfigurationBuilder;
import com.foreach.across.modules.entity.views.EntityView;
import com.foreach.across.modules.entity.views.ViewElementMode;
import com.foreach.across.modules.entity.views.processors.ExtensionViewProcessorAdapter;
import com.foreach.across.modules.entity.views.request.EntityViewCommand;
import com.foreach.across.modules.entity.views.request.EntityViewRequest;
import com.foreach.across.modules.experimental.bulkactions.support.BulkActionsEntityConfigurer;
import com.foreach.across.modules.experimental.bulkactions.support.SimpleBulkActionItemConfigurer;
import com.foreach.across.modules.experimental.bulkactions.viewprocessors.BulkActionViewProcessor;
import com.foreach.across.modules.experimental.entitycontrols.domain.EntityControlFactory;
import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.elements.ContainerViewElement;
import com.foreach.across.modules.web.ui.elements.support.ContainerViewElementUtils;
import lombok.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import static com.foreach.across.modules.web.ui.elements.HtmlViewElement.Functions.attribute;

@Configuration
@RequiredArgsConstructor
public class FoodUiConfiguration implements EntityConfigurer
{

	@Override
	public void configure( EntitiesConfigurationBuilder entities ) {
		entities.withType( Food.class )
		        .listView(
				        lvb -> lvb.viewProcessor( vp -> vp.createBean( FoodBulkActionViewProcessor.class ).order( 1100 ) )
				                  .and( BulkActionsEntityConfigurer.configureBulkActions(
						                  new SimpleBulkActionItemConfigurer<Food>()
								                  .identifierResolver( ( ctx, instance ) -> instance.getId() )
								                  .controlConfigurer( ( ctx, instance, builder ) -> {
									                  if ( instance.getCurrentAction() == FoodAction.STORED ) {
										                  if ( StringUtils.containsIgnoreCase( instance.getName(), "pizza" ) ) {
											                  builder.with(
													                  attribute( "supportedActions", Collections.singletonList( FoodActionType.BAKE_OVEN ) ) );
										                  }
										                  else {
											                  builder.with( attribute( "supportedActions",
											                                           Arrays.asList( FoodActionType.BAKE_OVEN, FoodActionType.BAKE_STOVE ) ) );
										                  }
									                  }
									                  else {
										                  builder.with( attribute( "supportedActions", Collections.singletonList( FoodActionType.RESET ) ) );
									                  }
								                  } )
				                  ) ) );
	}

	@RequiredArgsConstructor
	static class FoodBulkActionViewProcessor extends ExtensionViewProcessorAdapter<BulkActionsHolder>
	{
		private final EntityControlFactory entityControlFactory;
		private final ConversionService conversionService;
		private final FoodRepository foodRepository;

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
			                                                        .properties( props -> props.property( "action" ).viewElementType( ViewElementMode.CONTROL,
			                                                                                                                          BootstrapUiElements.MULTI_CHECKBOX ) )
			                                                        .build( builderContext );
			controls.values().stream()
			        .filter( c -> FormGroupElement.class.isAssignableFrom( c.getClass() ) )
			        .map( FormGroupElement.class::cast )
			        .map( FormGroupElement::getControl )
			        .filter( c -> FormControlElementSupport.class.isAssignableFrom( c.getClass() ) )
			        .map( FormControlElementSupport.class::cast )
			        .forEach( c -> {
				        String prefix = controlPrefix() + ".";
				        c.setName( prefix + c.getName() );
				        c.setControlName( prefix + c.getControlName() );
				        c.setHtmlId( prefix + c.getHtmlId() );
			        } );
			;
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
			FoodAction actionToSet = getFoodAction( extension );
			TypeDescriptor sourceType = TypeDescriptor.collection( Set.class, TypeDescriptor.valueOf( String.class ) );
			TypeDescriptor targetType = TypeDescriptor.collection( Set.class, TypeDescriptor.valueOf( Food.class ) );
			if ( conversionService.canConvert( sourceType, targetType ) ) {
				Set<Food> asEntities = (Set<Food>) conversionService.convert( extension.getItemsHolder().selectedItems(), sourceType, targetType );
				asEntities.forEach( f -> f.setCurrentAction( actionToSet ) );
				foodRepository.saveAll( asEntities );
			}
		}

		private FoodAction getFoodAction( BulkActionsHolder extension ) {
			FoodAction actionToSet;
			switch ( extension.getAction() ) {
				case RESET:
					actionToSet = FoodAction.STORED;
					break;
				case BAKE_OVEN:
					actionToSet = FoodAction.BAKING_OVEN;
					break;
				case BAKE_STOVE:
					actionToSet = FoodAction.BAKING_STOVE;
					break;
				default:
					throw new IllegalStateException( "Food action type is missing from the applied bulk action" );
			}
			return actionToSet;
		}
	}

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	static class BulkActionsHolder
	{
		private FoodActionType action;
		private BulkActionViewProcessor.SelectedItemsHolder itemsHolder;
	}
}
