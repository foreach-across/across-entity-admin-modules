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

package com.foreach.across.samples.entity.application.config;

import com.foreach.across.modules.web.AcrossWebModule;
import com.foreach.across.samples.entity.application.business.Group;
import com.foreach.across.samples.entity.application.repositories.GroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterRegistry;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StringToGroupConverter implements Converter<String, Group>
{
	private final GroupRepository groupRepository;

	@Override
	public Group convert( String name ) {
		try {
			return groupRepository.findById( Long.parseLong( name ) ).orElse( null );
		}
		catch ( NumberFormatException nfe ) {
			return groupRepository.findByName( name );
		}
	}

	@Autowired
	void registerToMvcConversionService( @Qualifier(AcrossWebModule.CONVERSION_SERVICE_BEAN) ConverterRegistry mvcConversionService ) {
		mvcConversionService.addConverter( this );
	}
}
