package com.foreach.across.testapplication.application.domain.food.processors;

import com.foreach.across.modules.bootstrapui.elements.ButtonViewElement;
import com.foreach.across.modules.bootstrapui.elements.FormControlElementSupport;
import com.foreach.across.modules.bootstrapui.elements.FormGroupElement;
import com.foreach.across.modules.bootstrapui.elements.FormViewElement;
import com.foreach.across.modules.entity.registry.properties.EntityPropertySelector;
import com.foreach.across.modules.entity.views.EntityView;
import com.foreach.across.modules.entity.views.EntityViewElementBuilderHelper;
import com.foreach.across.modules.entity.views.helpers.EntityViewElementBatch;
import com.foreach.across.modules.entity.views.processors.ExtensionViewProcessorAdapter;
import com.foreach.across.modules.entity.views.request.EntityViewCommand;
import com.foreach.across.modules.entity.views.request.EntityViewRequest;
import com.foreach.across.modules.experimental.bulkactions.ui.viewprocessors.BulkActionViewProcessor;
import com.foreach.across.modules.experimental.entitycontrols.domain.EntityControlFactory;
import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.elements.ContainerViewElement;
import com.foreach.across.modules.web.ui.elements.support.ContainerViewElementUtils;
import com.foreach.across.testapplication.application.domain.food.Food;
import com.foreach.across.testapplication.application.domain.food.FoodActionType;
import com.foreach.across.testapplication.application.domain.food.FoodBulkActionsHandler;
import liquibase.util.StringUtils;
import lombok.*;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import static com.foreach.across.modules.bootstrapui.ui.factories.BootstrapViewElements.bootstrap;

/**
 * @author Steven Gentens
 */
@RequiredArgsConstructor
public class FoodBulkActionViewProcessor extends ExtensionViewProcessorAdapter<FoodBulkActionViewProcessor.BulkActionsHolder>
{
	private final EntityControlFactory entityControlFactory;
	private final ConversionService conversionService;
	private final FoodBulkActionsHandler bulkActionsHandler;

	@Setter
	private Supplier<String> controlName = this::controlPrefix;

	@Override
	protected BulkActionsHolder createExtension( EntityViewRequest entityViewRequest, EntityViewCommand command, WebDataBinder dataBinder ) {
		return new BulkActionsHolder();
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
		String prefix = StringUtils.isEmpty( controlName.get() ) ? "" : controlName.get() + ".";

		controls.values().stream()
		        .filter( c -> FormGroupElement.class.isAssignableFrom( c.getClass() ) )
		        .map( FormGroupElement.class::cast )
		        .map( FormGroupElement::getControl )
		        .filter( c -> FormControlElementSupport.class.isAssignableFrom( c.getClass() ) )
		        .map( FormControlElementSupport.class::cast )
		        .forEach( c -> {
			        c.setName( prefix + c.getName() );
			        c.setControlName( prefix + c.getControlName() );
			        c.setHtmlId( prefix + c.getHtmlId() );
		        } );
		;
		ContainerViewElementUtils.find( container, BulkActionViewProcessor.BULK_ACTION_FORM_NAME, FormViewElement.class )
		                         .ifPresent(
				                         fve -> fve.addChildren( controls.values() )
				                                   .addChild( bootstrap.builders.button()
				                                                                .type( ButtonViewElement.Type.BUTTON_SUBMIT )
				                                                                .text( "Submit" )
				                                                                .build( builderContext ) )
		                         );
	}

	@Override
	protected void doPost( BulkActionsHolder extension, BindingResult bindingResult, EntityView entityView, EntityViewRequest entityViewRequest ) {
		TypeDescriptor sourceType = TypeDescriptor.collection( Set.class, TypeDescriptor.valueOf( String.class ) );
		TypeDescriptor targetType = TypeDescriptor.collection( Set.class, TypeDescriptor.valueOf( Food.class ) );
		if ( conversionService.canConvert( sourceType, targetType ) ) {
			Set<Food> asEntities = (Set<Food>) conversionService.convert( extension.getSelectedItems(), sourceType, targetType );
			bulkActionsHandler.executeBulkAction( asEntities, extension.getAction() );
		}
	}

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	static class BulkActionsHolder
	{
		private FoodActionType action;
		private Set<String> selectedItems = new HashSet<>();
	}
}
