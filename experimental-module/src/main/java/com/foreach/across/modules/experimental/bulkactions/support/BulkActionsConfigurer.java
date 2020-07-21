package com.foreach.across.modules.experimental.bulkactions.support;

import com.foreach.across.modules.bootstrapui.elements.CheckboxFormElement;
import com.foreach.across.modules.entity.views.context.EntityViewContext;
import com.foreach.across.modules.entity.views.request.EntityViewRequest;
import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.ViewElementPostProcessor;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public interface BulkActionsConfigurer<T>
{
	ViewElementPostProcessor<CheckboxFormElement> itemSelectorControlPostProcessor();

	BiFunction<T, ViewElementBuilderContext, Object> itemValueProvider();

	String itemControlName();

	Function<EntityViewRequest, String> submitUrlProvider();

	String formAttributeName();
}
