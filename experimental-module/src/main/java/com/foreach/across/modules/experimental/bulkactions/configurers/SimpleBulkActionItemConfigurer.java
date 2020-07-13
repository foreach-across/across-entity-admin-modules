package com.foreach.across.modules.experimental.bulkactions.configurers;

import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.elements.builder.NodeViewElementBuilder;

import java.util.function.BiFunction;

public class SimpleBulkActionItemConfigurer<T> implements BulkActionItemConfigurer<T>
{
	private BiFunction<ViewElementBuilderContext, T, Object> identifierResolver = null;
	private ControlConfigurer<T> controlConfigurer = null;

	public SimpleBulkActionItemConfigurer<T> identifierResolver( BiFunction<ViewElementBuilderContext, T, Object> identifierResolver ) {
		this.identifierResolver = identifierResolver;
		return this;
	}

	public SimpleBulkActionItemConfigurer<T> controlConfigurer( ControlConfigurer<T> controlConfigurer ) {
		this.controlConfigurer = controlConfigurer;
		return this;
	}

	@Override
	public Object getIdentifier( ViewElementBuilderContext builderContext, T instance ) {
		if ( identifierResolver == null ) {
			throw new IllegalStateException( "Bulk action controls require a configured value" );
		}
		return identifierResolver.apply( builderContext, instance );
	}

	@Override
	public void configureControl( ViewElementBuilderContext builderContext, T instance, NodeViewElementBuilder builder ) {
		if ( controlConfigurer != null ) {
			controlConfigurer.configureControl( builderContext, instance, builder );
		}
	}

	@FunctionalInterface
	public interface ControlConfigurer<T>
	{
		void configureControl( ViewElementBuilderContext builderContext, T instance, NodeViewElementBuilder builder );
	}
}
