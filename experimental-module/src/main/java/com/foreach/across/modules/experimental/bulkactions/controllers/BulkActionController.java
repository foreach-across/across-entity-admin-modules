package com.foreach.across.modules.experimental.bulkactions.controllers;

import com.foreach.across.modules.adminweb.annotations.AdminWebController;
import com.foreach.across.modules.entity.registry.EntityRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;

@AdminWebController
@RequiredArgsConstructor
@RequestMapping("/entities/{entityName:.+}/bulk/{actionName:.+}")
public class BulkActionController
{
	private final EntityRegistry entityRegistry;

//	@PostMapping(consumes = { MediaType.APPLICATION_FORM_URLENCODED_VALUE })
//	public ResponseEntity updateResponse( @PathVariable String entityName,
//	                                      @PathVariable String actionName,
//	                                      @RequestParam(name = "selectedIds") List<String> identifiers,
//	                                      HttpServletRequest servletRequest ) {
//		EntityConfiguration<?> entityConfiguration = entityRegistry.getEntityConfiguration( entityName );
//		BulkActionHandler bulkActionHandler = entityConfiguration.getAttribute( BulkActionHandler.class );
//		Map<String, String[]> params = servletRequest.getParameterMap();
//		if ( bulkActionHandler != null ) {
//			bulkActionHandler.handle( actionName, identifiers, params );
//		}
//		return ResponseEntity.ok().build();
//	}
}
