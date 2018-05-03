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

package com.foreach.across.modules.entity.views.processors;

import com.foreach.across.modules.entity.views.request.EntityViewCommand;
import com.foreach.across.modules.entity.views.request.EntityViewRequest;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class TestExtensionViewProcessorAdapter
{
	@Test
	public void validateExtensionShouldHaveControlPrefixIncluded() {
		String extensionName = "ext35844";
		ExtensionViewProcessorAdapter<String> adapter = new ExtensionViewProcessorAdapter<String>()
		{
			@Override
			protected String extensionName() {
				return extensionName;
			}

			@Override
			protected String createExtension( EntityViewRequest entityViewRequest, EntityViewCommand command, WebDataBinder dataBinder ) {
				return "foo";
			}
		};
		Errors errors = mock( Errors.class );
		EntityViewCommand entityViewCommand = new EntityViewCommand();
		EntityViewRequest entityViewRequest = new EntityViewRequest();
		errors.pushNestedPath( adapter.controlPrefix() );
		adapter.validateCommandObject( entityViewRequest, entityViewCommand, errors, HttpMethod.POST );

		verify( errors ).pushNestedPath( eq( "extensions[" + extensionName + "]" ) );
		verify( errors ).popNestedPath();
	}
}
