package com.foreach.across.modules.experimental.export.support.csv;

import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

/**
 * WIP to support streaming files to the response.
 * When the response is returned to the client, it also passes through the {@link org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBodyReturnValueHandler}.
 * This handler can sadly not resolve the generic parameter of the {@link org.springframework.http.ResponseEntity} and as such it is not recognized as a {@link StreamingResponseBody}.
 *
 * @param <T>
 */
public class StreamingCsvExportViewConfigurer<T> extends BaseCsvExportViewConfigurer<T, StreamingResponseBody>
{
	public StreamingCsvExportViewConfigurer() {
		super();
		converter( ( entityViewRequest, propertiesToExport, data ) -> out -> writeFileToOutputStream( out, entityViewRequest, propertiesToExport, data ) );
	}
}
