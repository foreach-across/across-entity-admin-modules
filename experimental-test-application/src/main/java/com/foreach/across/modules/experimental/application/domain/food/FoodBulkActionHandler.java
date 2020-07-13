package com.foreach.across.modules.experimental.application.domain.food;

import com.foreach.across.modules.experimental.bulkactions.support.BulkActionHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class FoodBulkActionHandler implements BulkActionHandler
{
	public static final String DELETE_ACTION = "delete";

	@Override
	public void handle( String actionName, List<String> entityIds, Map<String, String[]> parameterMap ) {
		switch ( actionName ) {
			case DELETE_ACTION:
				handleDelete( entityIds, parameterMap );
				break;
		}
	}

	private void handleDelete( List<String> entityIds, Map<String, String[]> parameterMap ) {

	}
}
