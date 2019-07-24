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

import com.foreach.across.modules.entity.query.EntityQuery;
import com.foreach.across.modules.entity.query.EntityQueryFacade;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.EntityModel;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Represents auto-suggest data for a registered entity. Data is fetched using an {@link EntityQuery}
 * and transformed using the {@link EntityModel}.
 * <p/>
 * The queries to be executed can be specified using either a supplier of the {@link EntityQuery} instance
 * or simply as EQL statement. The EQL statement can hold a {@code \{0\} token}
 * where the actual search string will be inserted.
 *
 * @author Arne Vandamme
 * @since 3.4.0
 */
@Setter
@Getter
@Accessors(chain = true)
public class EntityQueryAutoSuggestData implements AutoSuggestDataSet, AutoSuggestDataSet.ResultTransformer
{
	private String dataSetId;
	private EntityConfiguration<?> entityConfiguration;
	private EntityQueryFacade<?> entityQueryFacade;
	private ConversionService conversionService;

	private BiFunction<String, String, EntityQuery> suggestionsEntityQuerySupplier;
	private Function<String, EntityQuery> prefetchEntityQuerySupplier;

	@Setter(value = AccessLevel.PACKAGE)
	private Consumer<EntityQueryAutoSuggestData> initializer;

	public EntityQueryAutoSuggestData setSuggestionsEql( @NonNull String eql ) {
		suggestionsEntityQuerySupplier = ( search, controlName ) -> {
			String actual = replaceSearchParameter( eql, search );
			return EntityQuery.parse( actual );
		};
		return this;
	}

	public EntityQueryAutoSuggestData setPrefetchEql( @NonNull String eql ) {
		prefetchEntityQuerySupplier = ( search ) -> {
			String actual = replaceSearchParameter( eql, search );
			return EntityQuery.parse( actual );
		};
		return this;
	}

	private String replaceSearchParameter( @NonNull String eql, String search ) {
		return StringUtils.replace( eql, "{0}",
		                            StringUtils.replace(
				                            StringUtils.replace( search, "%", "\\%" ),
				                            "'", "\\'"
		                            ) );
	}

	@Override
	public Object prefetch( String search ) {
		if ( prefetchEntityQuerySupplier == null ) {
			return Collections.emptyList();
		}
		initializeIfNecessary();
		EntityQuery query = prefetchEntityQuerySupplier.apply( search );
		return findResults( query );
	}

	@Override
	public boolean isPrefetchSupported() {
		return prefetchEntityQuerySupplier != null;
	}

	@Override
	public Object suggestions( String search, String controlName ) {
		if ( suggestionsEntityQuerySupplier == null ) {
			return Collections.emptyList();
		}
		initializeIfNecessary();
		EntityQuery query = suggestionsEntityQuerySupplier.apply( search, controlName );
		return findResults( query );
	}

	private List<Object> findResults( EntityQuery query ) {
		if ( query == null ) {
			return Collections.emptyList();
		}
		EntityQuery executableQuery = entityQueryFacade.convertToExecutableQuery( query );
		return entityQueryFacade.findAll( executableQuery )
		                        .stream()
		                        .map( this::transformToResult )
		                        .collect( Collectors.toList() );
	}

	@Override
	@SuppressWarnings("unchecked")
	public Result transformToResult( Object entity ) {
		initializeIfNecessary();
		EntityModel entityModel = entityConfiguration.getEntityModel();
		String id = convertId( entityModel.getId( entity ) );
		String label = entityModel.getLabel( entity );

		return new SimpleAutoSuggestDataSet.Result( id, label );
	}

	private void initializeIfNecessary() {
		if ( initializer != null ) {
			initializer.accept( this );
		}
	}

	private String convertId( Object value ) {
		return Optional.ofNullable( conversionService ).orElseGet( DefaultConversionService::getSharedInstance ).convert( value, String.class );
	}

}
