package com.foreach.across.modules.experimental.bulkactions.support;

import com.foreach.across.modules.bootstrapui.elements.CheckboxFormElement;
import com.foreach.across.modules.entity.views.request.EntityViewRequest;
import com.foreach.across.modules.entity.web.links.EntityViewLinkBuilder;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.ViewElementPostProcessor;

import java.util.function.BiFunction;
import java.util.function.Function;

public interface BulkActionsConfigurer<T>
{
	ViewElementPostProcessor<CheckboxFormElement> itemSelectorControlPostProcessor();

	BiFunction<T, ViewElementBuilderContext, Object> itemValueProvider();

	BiFunction<EntityViewLinkBuilder, ViewElementBuilderContext, String> ajaxUrlProvider();

	String itemControlName();

	Function<EntityViewRequest, String> submitUrlProvider();

	String formAttributeName();
}
