package com.foreach.across.modules.experimental.export.support.csv;

import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.registry.properties.EntityPropertySelector;
import com.foreach.across.modules.entity.support.EntityMessageCodeResolver;
import com.foreach.across.modules.entity.views.request.EntityViewRequest;
import com.foreach.across.modules.experimental.export.support.ExportMapper;
import com.foreach.across.modules.experimental.export.support.SimpleExportViewConfigurer;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@Getter(value = AccessLevel.PROTECTED)
public abstract class BaseCsvExportViewConfigurer<T, R> extends SimpleExportViewConfigurer<T, R>
{
	private static final String NEW_LINE = "\n";

	private boolean includeUtf8Bom = false;
	private boolean includeSeparatorIdentifier = false;
	private String separator = ",";

	public BaseCsvExportViewConfigurer() {
		contentType( MediaType.valueOf( "text/csv" ) );
	}

	protected void writeFileToOutputStream( OutputStream outputStream, EntityViewRequest entityViewRequest,
	                                        Collection<EntityPropertyDescriptor> propertiesToExport,
	                                        Iterable<T> data ) {
		try (OutputStreamWriter os = new OutputStreamWriter( outputStream, StandardCharsets.UTF_8 ); BufferedWriter writer = new BufferedWriter( os )) {
			// byte order marker so excel can recognise unicode characters
			if ( isIncludeUtf8Bom() ) {
				byte[] bom = { (byte) 0xEF, (byte) 0xBB, (byte) 0xBF };
				outputStream.write( bom );
			}

			// configure the separator that is used within the csv (required for excel)
			if ( isIncludeSeparatorIdentifier() ) {
				writer.write( "sep=" + getSeparator() );
				writer.write( NEW_LINE );
			}

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
			                                                                        .map( o -> Objects.isNull( o ) ? "" : o.toString() )
			                                                                        .collect( Collectors.joining( getSeparator() ) )
			                                    ).collect( Collectors.toList() );
			for ( String s : content ) {
				writer.write( s );
				writer.write( NEW_LINE );
			}

			writer.flush();
		}
		catch ( IOException e ) {
			LOG.error( "Unexpected exception whilst mapping data", e );
		}
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
	public BaseCsvExportViewConfigurer<T, R> shouldIncludeUtf8Bom( boolean includeUtf8Bom ) {
		this.includeUtf8Bom = includeUtf8Bom;
		return this;
	}

	/**
	 * Configures the separator that is used in the export. Defaults to {@code ,}.
	 *
	 * @param separator to use
	 * @return self
	 */
	public BaseCsvExportViewConfigurer<T, R> separator( String separator ) {
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
	public BaseCsvExportViewConfigurer<T, R> shouldIncludeSeparatorIdentifier( boolean includeSeparatorIdentifier ) {
		this.includeSeparatorIdentifier = includeSeparatorIdentifier;
		return this;
	}

	@Override
	public BaseCsvExportViewConfigurer<T, R> applyPaginationParameters( boolean applyPaginationParameters ) {
		super.applyPaginationParameters( applyPaginationParameters );
		return this;
	}

	@Override
	public BaseCsvExportViewConfigurer<T, R> propertiesToExport( EntityPropertySelector propertiesToExport ) {
		super.propertiesToExport( propertiesToExport );
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
	public BaseCsvExportViewConfigurer<T, R> contentType( MediaType contentType ) {
		super.contentType( contentType );
		return this;
	}

	@Override
	public BaseCsvExportViewConfigurer<T, R> fileName( Function<EntityViewRequest, String> resolver ) {
		super.fileName( resolver );
		return this;
	}

	@Override
	public BaseCsvExportViewConfigurer<T, R> fileName( String fileName ) {
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
	public BaseCsvExportViewConfigurer<T, R> converter( ExportMapper<T, R> converter ) {
		super.converter( converter );
		return this;
	}

}
