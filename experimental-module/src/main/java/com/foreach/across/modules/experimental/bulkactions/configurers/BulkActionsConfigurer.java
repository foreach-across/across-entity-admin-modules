package com.foreach.across.modules.experimental.bulkactions.configurers;

import com.foreach.across.modules.entity.views.request.EntityViewRequest;

import java.util.function.BiFunction;

public interface BulkActionsConfigurer<T>
{
	BiFunction<EntityViewRequest, T, String> submitUrlConfigurer();

	BulkActionItemConfigurer<T> itemConfigurer();
}
