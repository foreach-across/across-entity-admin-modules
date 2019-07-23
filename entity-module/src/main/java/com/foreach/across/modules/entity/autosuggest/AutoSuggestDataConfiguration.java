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

import com.foreach.across.core.support.ReadableAttributes;
import com.foreach.across.core.support.WritableAttributes;
import com.foreach.across.modules.entity.config.AttributeRegistrar;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Arne Vandamme
 * @since 3.4.0
 */
@Accessors(chain = true)
@Data
@RequiredArgsConstructor
public class AutoSuggestDataConfiguration
{
	@NonNull
	private final String dataSetId;
	private String suggestionsEql;
	private String prefetchEql;

	public static <U extends ReadableAttributes> Registrar<U> autoSuggestData() {
		return new Registrar<U>();
	}

	public static class Registrar<T extends ReadableAttributes> implements AttributeRegistrar<T>
	{
		private String dataSetId;
		private String suggestionsEql;
		private String prefetchEql;

		public <U extends T> Registrar<U> dataSetId( String dataSetId ) {
			this.dataSetId = dataSetId;
			return self();
		}

		public <U extends T> Registrar<U> suggestionsEql( String suggestionsEql ) {
			this.suggestionsEql = suggestionsEql;
			return self();
		}

		public <U extends T> Registrar<U> prefetchEql( String prefetchEql ) {
			this.prefetchEql = prefetchEql;
			return self();
		}

		@SuppressWarnings("unchecked")
		private <U extends T> Registrar<U> self() {
			return (Registrar<U>) this;
		}

		@Override
		public void accept( @NonNull T owner, WritableAttributes attributes ) {
			String actualDataSetId = resolveDataSetId( owner );

			AutoSuggestDataConfiguration configuration = new AutoSuggestDataConfiguration( actualDataSetId );
			configuration.setSuggestionsEql( suggestionsEql );
			configuration.setPrefetchEql( prefetchEql );

			attributes.setAttribute( AutoSuggestDataConfiguration.class, configuration );
		}

		private String resolveDataSetId( T owner ) {
			if ( StringUtils.isNotEmpty( dataSetId ) ) {
				return dataSetId;
			}

			if ( owner instanceof EntityConfiguration ) {
				return ( (EntityConfiguration) owner ).getName() + "-autoSuggestData";
			}

			if ( owner instanceof EntityPropertyDescriptor ) {
				EntityPropertyDescriptor descriptor = (EntityPropertyDescriptor) owner;
				return /* descriptor.getPropertyRegistry().getId() + */ descriptor.getName() + "-autoSuggestData";
			}

			throw new IllegalArgumentException( "Only entity configurations or properties can have autosuggest data attributes configured" );
		}
	}
}
