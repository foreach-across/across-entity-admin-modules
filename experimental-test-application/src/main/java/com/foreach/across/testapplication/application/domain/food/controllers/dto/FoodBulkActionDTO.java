package com.foreach.across.testapplication.application.domain.food.controllers.dto;

import com.foreach.across.testapplication.application.domain.food.Food;
import com.foreach.across.testapplication.application.domain.food.FoodActionType;
import lombok.Data;

import java.util.Set;

/**
 * @author Steven Gentens
 */
@Data
public class FoodBulkActionDTO
{
	private Set<Food> selectedItems;
	private FoodActionType action;
}
