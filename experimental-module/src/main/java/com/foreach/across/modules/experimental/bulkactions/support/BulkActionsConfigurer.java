package com.foreach.across.modules.experimental.bulkactions.support;

import com.foreach.across.modules.entity.views.request.EntityViewRequest;

import java.util.function.BiFunction;
import java.util.function.Supplier;

public interface BulkActionsConfigurer<T>
{
	BiFunction<EntityViewRequest, T, String> submitUrlConfigurer();

	BulkActionItemConfigurer<T> itemConfigurer();

	Supplier<String> controlNameConfigurer();
}
