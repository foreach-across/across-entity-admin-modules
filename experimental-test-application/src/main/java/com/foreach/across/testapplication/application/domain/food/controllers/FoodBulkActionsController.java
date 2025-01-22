package com.foreach.across.testapplication.application.domain.food.controllers;

import com.foreach.across.modules.adminweb.annotations.AdminWebController;
import com.foreach.across.modules.entity.web.links.EntityViewLinks;
import com.foreach.across.testapplication.application.domain.food.Food;
import com.foreach.across.testapplication.application.domain.food.FoodBulkActionsHandler;
import com.foreach.across.testapplication.application.domain.food.controllers.dto.FoodBulkActionDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * @author Steven Gentens
 */
@AdminWebController
@RequiredArgsConstructor
@Slf4j
public class FoodBulkActionsController
{
	public static final String FOOD_BULK_ACTIONS = "/food/bulkActions";

	private final EntityViewLinks entityViewLinks;
	private final FoodBulkActionsHandler foodBulkActionsHandler;

	@PostMapping(value = FOOD_BULK_ACTIONS, consumes = { MediaType.APPLICATION_FORM_URLENCODED_VALUE })
	public String submitBulkAction( @ModelAttribute("bulkActions") FoodBulkActionDTO bulkAction, ModelMap model ) {
		try {
			foodBulkActionsHandler.executeBulkAction( bulkAction.getSelectedItems(), bulkAction.getAction() );
		}
		catch ( Exception e ) {
			LOG.error( "Unexpected error", e );
		}
		return "redirect:" + entityViewLinks.linkTo( Food.class )
		                                    .listView()
		                                    .withViewName( "controller" )
		                                    .toUriString();
	}
}
