package com.foreach.across.modules.experimental.export.support;

import com.foreach.across.modules.entity.support.EntityMessageCodeResolver;
import com.foreach.across.modules.entity.views.request.EntityViewRequest;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@Getter(value = AccessLevel.PROTECTED)
public class CsvExportViewConfigurer<T> extends SimpleExportViewConfigurer<T>
{
	private static final String NEW_LINE = "\n";

	private boolean includeUtf8Bom = false;
	private boolean includeSeparatorIdentifier = false;
	private String separator = ",";

	public CsvExportViewConfigurer() {
		contentType( MediaType.valueOf( "text/csv" ) );
		converter( ( entityViewRequest, propertiesToExport, data ) -> {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			OutputStreamWriter os = new OutputStreamWriter( bos, StandardCharsets.UTF_8 );
			BufferedWriter writer = new BufferedWriter( os );

			// byte order marker so excel can recognise unicode characters
			if ( isIncludeUtf8Bom() ) {
				byte[] bom = { (byte) 0xEF, (byte) 0xBB, (byte) 0xBF };
				bos.writeBytes( bom );
			}

			try {
				// configure the separator that is used within the csv (required for excel)
				if ( isIncludeSeparatorIdentifier() ) {
					writer.write( "sep=" + getSeparator() );
				}

				writer.write( NEW_LINE );

				EntityMessageCodeResolver entityMessageCodeResolver = entityViewRequest.getEntityViewContext()
				                                                                       .getMessageCodeResolver()
				                                                                       .prefixedResolver( "views[" + entityViewRequest.getViewName() + "]" );

				String headers = propertiesToExport.stream()
				                                   .map( pd -> entityMessageCodeResolver
						                                   .getMessageWithFallback( "properties." + pd.getName(), pd.getDisplayName() ) )
				                                   .collect( Collectors.joining( getSeparator() ) );
				writer.write( headers );
				writer.write( NEW_LINE );

				List<String> content = StreamSupport.stream( data.spliterator(), false )
				                                    .map( instance -> propertiesToExport.stream()
				                                                                        .map( pd -> pd.getPropertyValue( instance ) )
				                                                                        .map( Object::toString )
				                                                                        .collect( Collectors.joining( getSeparator() ) )
				                                    ).collect( Collectors.toList() );
				for ( String s : content ) {
					writer.write( s );
					writer.write( NEW_LINE );
				}

				writer.flush();
				writer.close();
			}
			catch ( IOException e ) {
				e.printStackTrace();
				LOG.error( "Unexpected exception whilst mapping data", e );
			}

			return bos.toByteArray();
		} );
	}

	/**
	 * Whether a Byte Order Marker should be included at the start of the outputted file.
	 * Some software requires it to correctly identify a file as {@link StandardCharsets#UTF_8}.
	 * </p>
	 * Default is {@code false}.
	 *
	 * @param includeUtf8Bom whether the BOM should be added
	 * @return self
	 */
	public CsvExportViewConfigurer<T> shouldIncludeUtf8Bom( boolean includeUtf8Bom ) {
		this.includeUtf8Bom = includeUtf8Bom;
		return this;
	}

	/**
	 * Configures the separator that is used in the export. Defaults to {@code ,}.
	 *
	 * @param separator to use
	 * @return self
	 */
	public CsvExportViewConfigurer<T> separator( String separator ) {
		this.separator = separator;
		return this;
	}

	/**
	 * Configures whether a separator identifier should be added at the start of the file.
	 * It is the equivalent of printing {@code sep=} followed by the separator as the first line in the file.
	 * Can be used for tools that support defining separators different from {@code ,}.
	 * </p>
	 * Default is {@code false}.
	 *
	 * @param includeSeparatorIdentifier whether the separator identifier should be included
	 * @return self
	 */
	public CsvExportViewConfigurer<T> shouldIncludeSeparatorIdentifier( boolean includeSeparatorIdentifier ) {
		this.includeSeparatorIdentifier = includeSeparatorIdentifier;
		return this;
	}

	@Override
	public CsvExportViewConfigurer<T> applyPaginationParameters( boolean applyPaginationParameters ) {
		super.applyPaginationParameters( applyPaginationParameters );
		return this;
	}

	/**
	 * Optional. Configures the content type that should be used for the file response.
	 * Defaults to {@code MediaType.valueOf( "text/csv" )}
	 *
	 * @param contentType to use.
	 * @return self
	 */
	@Override
	public CsvExportViewConfigurer<T> contentType( MediaType contentType ) {
		super.contentType( contentType );
		return this;
	}

	@Override
	public CsvExportViewConfigurer<T> fileName( Function<EntityViewRequest, String> resolver ) {
		super.fileName( resolver );
		return this;
	}

	@Override
	public CsvExportViewConfigurer<T> fileName( String fileName ) {
		super.fileName( fileName );
		return this;
	}

	/**
	 * Optional. Overrides the default conversion of the data to a csv format.
	 *
	 * @param converter to use
	 * @return self
	 */
	@Override
	public CsvExportViewConfigurer<T> converter( ExportMapper<T> converter ) {
		super.converter( converter );
		return this;
	}

}
