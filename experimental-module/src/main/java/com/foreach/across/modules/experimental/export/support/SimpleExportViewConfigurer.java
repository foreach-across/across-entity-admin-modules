package com.foreach.across.modules.experimental.export.support;

import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.registry.properties.EntityPropertySelector;
import com.foreach.across.modules.entity.views.request.EntityViewRequest;
import org.springframework.http.MediaType;

import java.util.Collection;
import java.util.function.Function;

public class SimpleExportViewConfigurer<T, R> implements ExportViewConfigurer<T, R>
{
	private EntityPropertySelector propertiesToExport;
	private MediaType contentType;
	private boolean applyPaginationParameters = false;
	private Function<EntityViewRequest, String> resolveFileName;
	private ExportMapper<T, R> converter;

	/**
	 * Configures whether only the current page should be exported or all the data matching the current filter.
	 * Defaults to {@code false}, which means that all items will be fetched instead of only the current page.
	 *
	 * @param applyPaginationParameters whether the filtered data should be limited to the current page.
	 * @return self
	 */
	public SimpleExportViewConfigurer<T, R> applyPaginationParameters( boolean applyPaginationParameters ) {
		this.applyPaginationParameters = applyPaginationParameters;
		return this;
	}

	/**
	 * Configures which properties should be exported.
	 *
	 * @param propertiesToExport to export
	 * @return self
	 */
	public SimpleExportViewConfigurer<T, R> propertiesToExport( EntityPropertySelector propertiesToExport ) {
		this.propertiesToExport = propertiesToExport;
		return this;
	}

	/**
	 * Configures the content type that should be used in the file response;
	 *
	 * @param contentType to use.
	 * @return self
	 */
	public SimpleExportViewConfigurer<T, R> contentType( MediaType contentType ) {
		this.contentType = contentType;
		return this;
	}

	/**
	 * Configures the file name that should be used in the file response.
	 *
	 * @param resolver to define the file name
	 * @return self
	 */
	public SimpleExportViewConfigurer<T, R> fileName( Function<EntityViewRequest, String> resolver ) {
		this.resolveFileName = resolver;
		return this;
	}

	/**
	 * Configures the file name that should be used in the file response.
	 *
	 * @param fileName name of the file
	 * @return self
	 */
	public SimpleExportViewConfigurer<T, R> fileName( String fileName ) {
		this.resolveFileName = evr -> fileName;
		return this;
	}

	/**
	 * Configures how the retrieved data is transformed to the file data.
	 * The file data is written out as a {@code byte[]} which is subsequently written to the response.
	 *
	 * @param converter to use
	 * @return self
	 */
	public SimpleExportViewConfigurer<T, R> converter( ExportMapper<T, R> converter ) {
		this.converter = converter;
		return this;
	}

	@Override
	public boolean applyPaginationParameters() {
		return applyPaginationParameters;
	}

	@Override
	public EntityPropertySelector getPropertiesToExport() {
		return propertiesToExport;
	}

	@Override
	public MediaType getResponseContentType() {
		return contentType;
	}

	@Override
	public String resolveFileName( EntityViewRequest request ) {
		return resolveFileName.apply( request );
	}

	@Override
	public R converter( EntityViewRequest entityViewRequest, Collection<EntityPropertyDescriptor> propertiesToExport, Iterable<T> toExport ) {
		return converter.convertToFile( entityViewRequest, propertiesToExport, toExport );
	}
}

