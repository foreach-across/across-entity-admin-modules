package com.foreach.across.modules.experimental.export.support;

import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.registry.properties.EntityPropertySelector;
import com.foreach.across.modules.entity.views.request.EntityViewRequest;
import org.springframework.http.MediaType;

import java.util.Collection;

public interface ExportViewConfigurer<T>
{
	/**
	 * Defines which properties should be exported of the fetched items.
	 */
	EntityPropertySelector getPropertiesToExport();

	/**
	 * Configures whether the current paging parameters should be taken into account when fetching the items to export.
	 */
	boolean applyPaginationParameters();

	/**
	 * Configures the {@link java.net.http.HttpResponse} content type header.
	 */
	MediaType getResponseContentType();

	/**
	 * Configures a file name for the data written to the response based on the current request.
	 */
	String resolveFileName( EntityViewRequest request );

	/**
	 * Converts the fetched items to a file. The file will subsequently be written as a byte[] to the response.
	 *
	 * @param entityViewRequest  current request
	 * @param propertiesToExport properties which should be exported
	 * @param toExport           items to export
	 * @return a byte[] representing the file
	 */
	byte[] converter( EntityViewRequest entityViewRequest, Collection<EntityPropertyDescriptor> propertiesToExport, Iterable<T> toExport );
}
