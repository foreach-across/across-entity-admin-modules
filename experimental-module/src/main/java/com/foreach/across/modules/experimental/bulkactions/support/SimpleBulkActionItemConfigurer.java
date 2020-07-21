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

public class SimpleBulkActionItemConfigurer<T> implements BulkActionsConfigurer<T>
{
	private BiConsumer<ViewElementBuilderContext, ViewElement> itemSelectorControlPostProcessor = null;
	private BiFunction<T, ViewElementBuilderContext, Object> itemValueProvider = null;
	private String controlName = null;
	private Function<EntityViewRequest, String> submitUrl = null;
	private String formAttributeName;

	public SimpleBulkActionItemConfigurer<T> itemSelectorControlPostProcessor( BiConsumer<ViewElementBuilderContext, ViewElement> itemSelectorPostProcessor ) {
		this.itemSelectorControlPostProcessor = itemSelectorPostProcessor;
		return this;
	}

	public SimpleBulkActionItemConfigurer<T> itemValue( BiFunction<T, ViewElementBuilderContext, Object> itemValueProvider ) {
		this.itemValueProvider = itemValueProvider;
		return this;
	}

	public SimpleBulkActionItemConfigurer<T> itemControlName( String controlName ) {
		this.controlName = controlName;
		return this;
	}

	public SimpleBulkActionItemConfigurer<T> formAttributeName( String formAttributeName ) {
		this.formAttributeName = formAttributeName;
		return this;
	}

	public SimpleBulkActionItemConfigurer<T> submitUrl( BiFunction<EntityViewRequest, EntityViewContext, String> submitUrlProvider ) {
		Function<EntityViewRequest, String> urlProvider = entityViewRequest -> submitUrlProvider.apply( entityViewRequest,
		                                                                                                entityViewRequest.getEntityViewContext() );
		this.submitUrl = urlProvider;
		return this;
	}

	@Override
	public ViewElementPostProcessor<CheckboxFormElement> itemSelectorControlPostProcessor() {
		return itemSelectorControlPostProcessor::accept;
	}

	@Override
	public BiFunction<T, ViewElementBuilderContext, Object> itemValueProvider() {
		return itemValueProvider;
	}

	@Override
	public String itemControlName() {
		return controlName;
	}

	@Override
	public Function<EntityViewRequest, String> submitUrlProvider() {
		return submitUrl;
	}

	@Override
	public String formAttributeName() {
		return formAttributeName;
	}
}
