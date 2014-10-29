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
package com.foreach.across.modules.it.properties.definingmodule.config;

import com.foreach.across.core.annotations.Exposed;
import com.foreach.across.modules.it.properties.definingmodule.registry.RevisionPropertyRegistry;
import com.foreach.across.modules.it.properties.definingmodule.repositories.RevisionPropertiesRepository;
import com.foreach.across.modules.properties.config.AbstractEntityPropertiesConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Arne Vandamme
 */
@Configuration
public class RevisionPropertiesConfig extends AbstractEntityPropertiesConfiguration
{
	@Override
	protected String originalTableName() {
		return "revision_properties";
	}

	@Override
	public String propertiesId() {
		return "DefiningModule.RevisionProperties";
	}

	@Override
	public String keyColumnName() {
		return "revision_id";
	}

	@Bean
	public RevisionPropertiesRepository revisionPropertiesRepository() {
		return new RevisionPropertiesRepository( this );
	}

	@Bean
	@Exposed
	public RevisionPropertyRegistry revisionPropertyRegistry() {
		return new RevisionPropertyRegistry( this );
	}

}
