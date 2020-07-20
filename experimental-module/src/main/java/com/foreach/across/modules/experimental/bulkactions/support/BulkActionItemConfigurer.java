package com.foreach.across.modules.experimental.bulkactions.support;

import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.elements.builder.NodeViewElementBuilder;

public interface BulkActionItemConfigurer<T>
{
	Object getIdentifier( ViewElementBuilderContext builderContext, T instance );

	void configureControl( ViewElementBuilderContext builderContext, T instance, NodeViewElementBuilder builder );

	String controlName();
}
