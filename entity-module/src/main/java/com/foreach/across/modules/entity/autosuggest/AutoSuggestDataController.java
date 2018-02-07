/*
 * Copyright 2014 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.foreach.across.modules.entity.autosuggest;

import com.foreach.across.modules.adminweb.annotations.AdminWebController;
import com.foreach.across.modules.web.template.ClearTemplate;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collections;

import static com.foreach.across.modules.entity.autosuggest.AutoSuggestDataController.DEFAULT_REQUEST_MAPPING;

/**
 * API controller for fetching auto-suggest data.
 * Designed so it can be extended if it needs to be exposed under a different (for example non-admin web) path.
 *
 * @author Arne Vandamme
 * @see AutoSuggestDataEndpoint
 * @since 3.0.0
 */
@AdminWebController
@RequestMapping(DEFAULT_REQUEST_MAPPING)
@RequiredArgsConstructor
public class AutoSuggestDataController
{
	public static final String DEFAULT_REQUEST_MAPPING = "/api/entityModule/auto-suggest";

	private final AutoSuggestDataEndpoint endpoint;

	@ClearTemplate
	@GetMapping(path = "/query", produces = "application/json")
	public Object suggestions( @RequestParam("dataset") String dataSetId,
	                           @RequestParam("query") String query,
	                           @RequestParam(value = "controlName", required = false) String controlName ) {
		val dataSet = endpoint.getDataSet( dataSetId );

		if ( dataSet == null ) {
			return ResponseEntity.notFound();
		}

		Object data = dataSet.getDataSet().suggestions( query, controlName );

		return convertToResponseEntity( data );
	}

	@ClearTemplate
	@GetMapping(path = "/prefetch", produces = "application/json")
	public Object prefetch( @RequestParam("dataset") String dataSetId,
	                        @RequestParam(value = "controlName", required = false) String controlName ) {
		val dataSet = endpoint.getDataSet( dataSetId );

		if ( dataSet == null ) {
			return ResponseEntity.notFound();
		}

		Object data = dataSet.getDataSet().prefetch( controlName );

		return convertToResponseEntity( data );
	}

	private Object convertToResponseEntity( Object data ) {
		if ( data != null ) {
			if ( data instanceof ResponseEntity ) {
				return data;
			}
			else {
				return new ResponseEntity<>( data, HttpStatus.OK );
			}
		}

		return new ResponseEntity<>( Collections.emptyList(), HttpStatus.OK );
	}
}
