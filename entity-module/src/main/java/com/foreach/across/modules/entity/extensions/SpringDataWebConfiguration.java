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

package com.foreach.across.modules.entity.extensions;

import com.foreach.across.core.annotations.ModuleConfiguration;
import com.foreach.across.core.annotations.OrderInModule;
import com.foreach.across.core.context.bootstrap.AcrossBootstrapConfigurer;
import com.foreach.across.modules.entity.converters.EntityConverter;
import com.foreach.across.modules.entity.registry.EntityRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.data.rest.RepositoryRestMvcAutoConfiguration;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebAutoConfiguration;
import org.springframework.boot.autoconfigure.hateoas.HypermediaAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * Ensure Spring Data Web Support is enabled in the Across context.
 *
 * @author Arne Vandamme
 * @since 3.0.0
 */
@ModuleConfiguration(AcrossBootstrapConfigurer.CONTEXT_POSTPROCESSOR_MODULE)
@Import({ RepositoryRestMvcAutoConfiguration.class, HypermediaAutoConfiguration.class, SpringDataWebAutoConfiguration.class })
@OrderInModule
@RequiredArgsConstructor
class SpringDataWebConfiguration extends WebMvcConfigurerAdapter
{
	private final EntityRegistry entityRegistry;

	/**
	 * The generic {@link com.foreach.across.modules.entity.converters.EntityConverter} is registered
	 * very late in order to take precedence over the (most likely present) {@link org.springframework.data.repository.support.DomainClassConverter}).
	 */
	@Override
	public void addFormatters( FormatterRegistry registry ) {
		if ( !( registry instanceof FormattingConversionService ) ) {
			return;
		}

		registry.addConverter( new EntityConverter<>( (FormattingConversionService) registry, entityRegistry ) );
	}
}
