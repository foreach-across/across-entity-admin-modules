package com.foreach.across.modules.experimental.export.support;

import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.views.request.EntityViewRequest;

import java.util.Collection;

@FunctionalInterface
public interface ExportMapper<T>
{
	/**
	 * Converts properties of a given amount of data to a byte[] representing a file.
	 *
	 * @param entityViewRequest  that resulted in this data being fetched
	 * @param propertiesToExport properties of the data that should be present in the export
	 * @param data               that should be exported
	 * @return a byte[] holding the converted content
	 */
	byte[] convertToFile( EntityViewRequest entityViewRequest, Collection<EntityPropertyDescriptor> propertiesToExport, Iterable<T> data );
}
