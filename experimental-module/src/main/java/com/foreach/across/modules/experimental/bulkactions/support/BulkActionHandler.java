package com.foreach.across.modules.experimental.bulkactions.support;

import java.util.List;
import java.util.Map;

public interface BulkActionHandler
{
	void handle( String actionName, List<String> entityIds, Map<String, String[]> parameterMap );
}