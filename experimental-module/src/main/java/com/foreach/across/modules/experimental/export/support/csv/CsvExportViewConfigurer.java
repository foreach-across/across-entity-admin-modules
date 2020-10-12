package com.foreach.across.modules.experimental.export.support.csv;

import java.io.ByteArrayOutputStream;

public class CsvExportViewConfigurer<T> extends BaseCsvExportViewConfigurer<T, byte[]>
{
	public CsvExportViewConfigurer() {
		super();
		converter( ( entityViewRequest, propertiesToExport, data ) -> {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			writeFileToOutputStream( bos, entityViewRequest, propertiesToExport, data );
			return bos.toByteArray();
		} );
	}
}
