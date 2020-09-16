package com.foreach.across.modules.experimental.export.ui.viewprocessors;

import com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistry;
import com.foreach.across.modules.entity.registry.properties.EntityPropertySelector;
import com.foreach.across.modules.entity.views.EntityView;
import com.foreach.across.modules.entity.views.processors.AbstractEntityFetchingViewProcessor;
import com.foreach.across.modules.entity.views.processors.EntityViewProcessorAdapter;
import com.foreach.across.modules.entity.views.request.EntityViewCommand;
import com.foreach.across.modules.entity.views.request.EntityViewRequest;
import com.foreach.across.modules.experimental.export.support.ExportMapper;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.http.*;
import org.springframework.validation.BindingResult;

import java.util.function.Function;

/**
 * Customizes the response for the page request to convert the fetched data and return it as a file.
 *
 * @param <T> type of the data rendered.
 */
@Setter
@Accessors(fluent = true)
public class ExportListViewProcessor<T> extends EntityViewProcessorAdapter
{
	private EntityPropertySelector propertiesToExport;
	private MediaType responseContentType;
	private Function<EntityViewRequest, String> fileNameResolver;
	private ExportMapper<T> export;

	@Override
	protected void doControl( EntityViewRequest entityViewRequest,
	                          EntityView entityView,
	                          EntityViewCommand command,
	                          BindingResult bindingResult,
	                          HttpMethod httpMethod ) {
		entityView.setShouldRender( false );
	}

	@Override
	public void postProcess( EntityViewRequest entityViewRequest, EntityView entityView ) {
		Iterable<T> items = entityView.getAttribute( AbstractEntityFetchingViewProcessor.DEFAULT_ATTRIBUTE_NAME, Iterable.class );

		EntityPropertyRegistry propertyRegistry = entityViewRequest.getEntityViewContext().getPropertyRegistry();
		byte[] file = export.convertToFile( entityViewRequest, propertyRegistry.select( propertiesToExport ), items );

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType( responseContentType );
		headers.setContentDispositionFormData( "attachment", fileNameResolver.apply( entityViewRequest ) );
		entityView.setResponseEntity( new ResponseEntity<>( file, headers, HttpStatus.OK ) );
	}
}
