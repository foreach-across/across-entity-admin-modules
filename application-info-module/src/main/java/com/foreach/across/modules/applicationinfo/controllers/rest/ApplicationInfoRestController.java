package com.foreach.across.modules.applicationinfo.controllers.rest;

import com.foreach.across.modules.applicationinfo.business.AcrossApplicationInfo;
import com.foreach.across.modules.applicationinfo.controllers.ApplicationInfoController;
import com.foreach.across.modules.debugweb.mvc.DebugWebController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author niels
 * @since 24/07/2015
 */
@DebugWebController
public class ApplicationInfoRestController
{
	public static final String PATH_REST_PREFIX = "/api";

	@Autowired
	private AcrossApplicationInfo applicationInfo;

	@RequestMapping( value = PATH_REST_PREFIX + ApplicationInfoController.PATH, produces = MediaType.APPLICATION_JSON_VALUE )
	@ResponseBody
	public ResponseEntity<AcrossApplicationInfo> getApplicationInfoAsJson() {
		return new ResponseEntity<>( applicationInfo, HttpStatus.OK );
	}
}
