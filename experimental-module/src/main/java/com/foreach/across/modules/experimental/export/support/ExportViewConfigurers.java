package com.foreach.across.modules.experimental.export.support;

import com.foreach.across.modules.entity.config.builders.EntityListViewFactoryBuilder;
import com.foreach.across.modules.entity.registry.properties.EntityPropertySelector;
import com.foreach.across.modules.entity.views.processors.PropertyRenderingViewProcessor;
import com.foreach.across.modules.experimental.export.ui.viewprocessors.ExportListViewProcessor;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Optional;
import java.util.function.Consumer;

@UtilityClass
@Slf4j
public class ExportViewConfigurers
{

	/**
	 * Configures a given view to result in an export of the fetched data.
	 *
	 * @see #configureExportView(ExportViewConfigurer)
	 * @see CsvExportViewConfigurer
	 */
	public <T> Consumer<EntityListViewFactoryBuilder> configureCsvExportView( CsvExportViewConfigurer<T> configurer ) {
		return configureExportView( configurer );
	}

	/**
	 * Configures a given view to result in an export of the fetched data. How the data is exported is defined by an {@link ExportViewConfigurer}.
	 * If the configurer does not define any properties to be selected, the properties configured on the current view will be used.
	 * If the properties which should be rendered on this view can not be resolved, all readable properties are configured instead.
	 */
	public <T> Consumer<EntityListViewFactoryBuilder> configureExportView( ExportViewConfigurer<T> configurer ) {
		return lvb -> {
			if ( configurer.applyPaginationParameters() ) {
				lvb.pageSize( Integer.MAX_VALUE );
			}

			lvb.viewProcessor(
					vp -> vp.provideBean( new ExportListViewProcessor<T>() )
					        .configure( evp -> evp.responseContentType( configurer.getResponseContentType() )
					                              .fileNameResolver( configurer::resolveFileName )
					                              .propertiesToExport( configurer.getPropertiesToExport() )
					                              .export( (ExportMapper<T>) configurer::converter ) )
			).postProcess(
					( view, registry ) -> {
						Optional<EntityPropertySelector> propertySelector = Optional.ofNullable( configurer.getPropertiesToExport() );
						if ( propertySelector.isEmpty() ) {
							propertySelector = registry.getProcessor( PropertyRenderingViewProcessor.class.getName(), PropertyRenderingViewProcessor.class )
							                           .flatMap( ExportViewConfigurers::resolveEntityPropertySelector );
						}
						Optional<ExportListViewProcessor> exportViewProcessor =
								registry.getProcessor( ExportListViewProcessor.class.getName(), ExportListViewProcessor.class );
						if ( exportViewProcessor.isPresent() ) {
							exportViewProcessor.get().propertiesToExport(
									propertySelector.orElseGet( () -> EntityPropertySelector.of( EntityPropertySelector.READABLE ) )
							);
						}
					}
			);
		};
	}

	private static Optional<EntityPropertySelector> resolveEntityPropertySelector( PropertyRenderingViewProcessor viewProcessor ) {
		EntityPropertySelector value = null;
		try {
			Field selector = PropertyRenderingViewProcessor.class.getDeclaredField( "selector" );
			ReflectionUtils.makeAccessible( selector );
			value = (EntityPropertySelector) selector.get( viewProcessor );
		}
		catch ( NoSuchFieldException | IllegalAccessException e ) {
			LOG.debug( "Unable to resolve properties to select via PropertyRenderingViewProcessor" );
		}
		return Optional.ofNullable( value );
	}
}
