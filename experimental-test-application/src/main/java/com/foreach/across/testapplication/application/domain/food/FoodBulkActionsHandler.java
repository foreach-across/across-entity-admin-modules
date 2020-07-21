package com.foreach.across.testapplication.application.domain.food;

import com.foreach.across.core.annotations.Exposed;
import com.foreach.across.testapplication.application.domain.food.Food;
import com.foreach.across.testapplication.application.domain.food.FoodAction;
import com.foreach.across.testapplication.application.domain.food.FoodActionType;
import com.foreach.across.testapplication.application.domain.food.FoodRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collection;

/**
 * @author Steven Gentens
 */
@Component
@RequiredArgsConstructor
@Exposed
public class FoodBulkActionsHandler
{
	private final FoodRepository foodRepository;

	public void executeBulkAction( Collection<Food> foodItems, FoodActionType action ) {
		FoodAction actionToSet = getFoodAction( action );
		foodItems.forEach( f -> f.setCurrentAction( actionToSet ) );
		foodRepository.saveAll( foodItems );
	}

	private FoodAction getFoodAction( FoodActionType actionType ) {
		FoodAction actionToSet;
		switch ( actionType ) {
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
				throw new IllegalStateException( "Unknown or missing action type" );
		}
		return actionToSet;
	}
}
