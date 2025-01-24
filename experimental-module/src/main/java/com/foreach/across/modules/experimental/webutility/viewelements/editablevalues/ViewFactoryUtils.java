package com.foreach.across.modules.experimental.webutility.viewelements.editablevalues;

import com.foreach.across.modules.entity.registry.properties.EntityPropertySelector;
import com.foreach.across.modules.entity.views.DispatchingEntityViewFactory;
import com.foreach.across.modules.entity.views.EntityViewFactory;
import com.foreach.across.modules.entity.views.EntityViewProcessor;
import com.foreach.across.modules.entity.views.ViewElementMode;
import com.foreach.across.modules.entity.views.processors.PropertyRenderingViewProcessor;
import com.foreach.across.modules.entity.views.processors.SortableTableRenderingViewProcessor;
import lombok.experimental.UtilityClass;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Optional;

@UtilityClass
class ViewFactoryUtils
{
	static <T extends EntityViewProcessor> Optional<T> getViewProcessorFromView( EntityViewFactory entityViewFactory, Class<T> processorType ) {
		return getViewProcessorFromView( entityViewFactory, processorType.getName(), processorType );
	}

	static <T extends EntityViewProcessor> Optional<T> getViewProcessorFromView( EntityViewFactory entityViewFactory,
	                                                                             String processorName,
	                                                                             Class<T> processorType ) {
		if ( entityViewFactory instanceof DispatchingEntityViewFactory factory ) {
			return factory.getProcessorRegistry()
			                                                           .getProcessor( processorName, processorType );
		}
		return Optional.empty();
	}

	static Optional<EntityPropertySelector> resolvePropertySelector( SortableTableRenderingViewProcessor viewProcessor ) {
		return tryGetPropertyValue( viewProcessor, "propertySelector" );
	}

	static Optional<EntityPropertySelector> resolvePropertySelector( PropertyRenderingViewProcessor viewProcessor ) {
		return tryGetPropertyValue( viewProcessor, "selector" );
	}

	static Optional<ViewElementMode> resolveViewElementMode( SortableTableRenderingViewProcessor viewProcessor ) {
		return tryGetPropertyValue( viewProcessor, "viewElementMode" );
	}

	static Optional<ViewElementMode> resolveViewElementMode( PropertyRenderingViewProcessor viewProcessor ) {
		return tryGetPropertyValue( viewProcessor, "viewElementMode" );
	}

	@SuppressWarnings("unchecked")
	private static <T> Optional<T> tryGetPropertyValue( EntityViewProcessor viewProcessor, String propertyName ) {
		T value = null;
		try {
			Field selector = viewProcessor.getClass().getDeclaredField( propertyName );
			ReflectionUtils.makeAccessible( selector );
			value = (T) selector.get( viewProcessor );
		}
		catch ( NoSuchFieldException | IllegalAccessException e ) {
		}
		return Optional.ofNullable( value );
	}
}
