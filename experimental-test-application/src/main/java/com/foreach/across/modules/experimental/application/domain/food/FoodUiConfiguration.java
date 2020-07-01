package com.foreach.across.modules.experimental.application.domain.food;

import com.foreach.across.modules.experimental.bulkactions.configurars.ListViewBulkActions;
import com.foreach.across.modules.experimental.bulkactions.domain.BulkAction;
import com.foreach.across.modules.experimental.bulkactions.domain.BulkActionSet;
import com.foreach.across.modules.experimental.bulkactions.domain.SimpleBulkIdentifier;
import com.foreach.across.modules.experimental.bulkactions.support.BulkActionHandler;
import com.foreach.across.modules.experimental.bulkactions.support.BulkIdentifierBuilder;
import com.foreach.across.modules.entity.config.EntityConfigurer;
import com.foreach.across.modules.entity.config.builders.EntitiesConfigurationBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

import static com.foreach.across.modules.experimental.application.domain.food.FoodBulkActionHandler.DELETE_ACTION;
import static com.foreach.across.modules.entity.views.util.EntityViewElementUtils.currentEntity;

@Configuration
@RequiredArgsConstructor
public class FoodUiConfiguration implements EntityConfigurer {
    private final FoodBulkActionHandler foodBulkActionHandler;

    @Override
    public void configure(EntitiesConfigurationBuilder entities) {
        entities.withType(Food.class)
                .attribute(BulkActionHandler.class, foodBulkActionHandler)
                .attribute(BulkActionSet.class, BulkActionSet.of(
                        BulkAction.of(DELETE_ACTION, "bulk-delete-entity")
                ))
                .attribute(
                        BulkIdentifierBuilder.class,
                        context -> {
                            Food food = currentEntity(context, Food.class);
                            return SimpleBulkIdentifier.of(
                                    food.getId().toString(),
                                    Map.of("data-allowed-bulk-actions", new String[]{DELETE_ACTION})
                            );
                        }
                )
                .listView(lvb -> lvb.viewProcessor(ListViewBulkActions::configureBulkActions));
    }
}
