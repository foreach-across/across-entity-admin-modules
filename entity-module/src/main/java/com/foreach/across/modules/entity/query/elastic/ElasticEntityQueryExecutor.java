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

package com.foreach.across.modules.entity.query.elastic;

import com.foreach.across.modules.entity.query.AbstractEntityQueryExecutor;
import com.foreach.across.modules.entity.query.EntityQuery;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHitSupport;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;

import java.util.List;

public class ElasticEntityQueryExecutor<T> extends AbstractEntityQueryExecutor<T>
{
	private final ElasticsearchOperations elasticsearchOperations;
	private final EntityConfiguration<T> entityConfiguration;

	public ElasticEntityQueryExecutor(
			ElasticsearchOperations elasticsearchOperations,
			EntityConfiguration<T> entityConfiguration ) {
		this.elasticsearchOperations = elasticsearchOperations;
		this.entityConfiguration = entityConfiguration;
	}

	@Override
	protected Iterable<T> executeQuery( EntityQuery query ) {
		CriteriaQuery criteriaQuery = EntityQueryElasticUtils.toCriteriaQuery( query );
		SearchHits<?> hits = elasticsearchOperations.search( criteriaQuery, entityConfiguration.getEntityType() );
		Iterable<T> items = (Iterable<T>) SearchHitSupport.unwrapSearchHits( hits );
		return items;
	}

	@Override
	protected Iterable<T> executeQuery( EntityQuery query, Sort sort ) {
		CriteriaQuery criteriaQuery = EntityQueryElasticUtils.toCriteriaQuery( query );
		criteriaQuery.addSort( sort );
		SearchHits<?> hits = elasticsearchOperations.search( criteriaQuery, entityConfiguration.getEntityType() );
		return (Iterable<T>) SearchHitSupport.unwrapSearchHits( hits );
	}

	@Override
	protected Page<T> executeQuery( EntityQuery query, Pageable pageable ) {
		CriteriaQuery criteriaQuery = EntityQueryElasticUtils.toCriteriaQuery( query );
		criteriaQuery.setPageable( pageable );
		SearchHits<?> hits = elasticsearchOperations.search( criteriaQuery, entityConfiguration.getEntityType() );
		List<T> items = (List<T>) SearchHitSupport.unwrapSearchHits( hits );
		return new PageImpl<T>( items, pageable, hits.getTotalHits() );
	}
}
