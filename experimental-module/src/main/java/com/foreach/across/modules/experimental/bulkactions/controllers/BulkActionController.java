package com.foreach.across.modules.experimental.bulkactions.controllers;

import com.foreach.across.modules.adminweb.annotations.AdminWebController;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.EntityRegistry;
import com.foreach.across.modules.experimental.bulkactions.support.BulkActionHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@AdminWebController
@RequiredArgsConstructor
@RequestMapping("/entities/{entityName:.+}/bulk/{actionName:.+}")
public class BulkActionController
{
	private final EntityRegistry entityRegistry;

	@PostMapping(consumes = { MediaType.APPLICATION_FORM_URLENCODED_VALUE })
	public ResponseEntity updateResponse( @PathVariable String entityName,
	                                      @PathVariable String actionName,
	                                      @RequestParam(name = "selectedIds") List<String> identifiers,
	                                      HttpServletRequest servletRequest ) {
		EntityConfiguration<?> entityConfiguration = entityRegistry.getEntityConfiguration( entityName );
		BulkActionHandler bulkActionHandler = entityConfiguration.getAttribute( BulkActionHandler.class );
		Map<String, String[]> params = servletRequest.getParameterMap();
		if ( bulkActionHandler != null ) {
			bulkActionHandler.handle( actionName, identifiers, params );
		}
		return ResponseEntity.ok().build();
	}
}
