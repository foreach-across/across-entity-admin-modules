package com.foreach.across.modules.experimental.bulkactions.support;

import java.util.Map;

public interface BulkIdentifier
{
	String getId();

	Map<String, Object> getAttributes();
}
