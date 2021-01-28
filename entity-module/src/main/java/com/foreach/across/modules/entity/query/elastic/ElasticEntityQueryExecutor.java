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
import com.foreach.across.modules.entity.query.EntityQueryCondition;
import com.foreach.across.modules.entity.query.EntityQueryExpression;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHitSupport;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.ElasticsearchPersistentEntity;
import org.springframework.data.elasticsearch.core.mapping.ElasticsearchPersistentProperty;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

@Slf4j
@ConditionalOnClass(ElasticsearchOperations.class)
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
		EntityQuery transformedQuery = transformExpression( query );
		CriteriaQuery criteriaQuery = EntityQueryElasticUtils.toCriteriaQuery( transformedQuery );
		SearchHits<?> hits = elasticsearchOperations.search( criteriaQuery, entityConfiguration.getEntityType() );
		Iterable<T> items = (Iterable<T>) SearchHitSupport.unwrapSearchHits( hits );
		return items;
	}

	@Override
	protected Iterable<T> executeQuery( EntityQuery query, Sort sort ) {
		EntityQuery transformedQuery = transformExpression( query );
		CriteriaQuery criteriaQuery = EntityQueryElasticUtils.toCriteriaQuery( transformedQuery );
		criteriaQuery.addSort( sort );
		SearchHits<?> hits = elasticsearchOperations.search( criteriaQuery, entityConfiguration.getEntityType() );
		return (Iterable<T>) SearchHitSupport.unwrapSearchHits( hits );
	}

	@Override
	protected Page<T> executeQuery( EntityQuery query, Pageable pageable ) {
		EntityQuery transformedQuery = transformExpression( query );
		CriteriaQuery criteriaQuery = EntityQueryElasticUtils.toCriteriaQuery( transformedQuery );
		criteriaQuery.setPageable( pageable );
		SearchHits<?> hits = elasticsearchOperations.search( criteriaQuery, entityConfiguration.getEntityType() );
		List<T> items = (List<T>) SearchHitSupport.unwrapSearchHits( hits );
		return new PageImpl<T>( items, pageable, hits.getTotalHits() );
	}

	@SuppressWarnings("unchecked")
	protected <TYPE extends EntityQueryExpression> TYPE transformExpression( TYPE original ) {
		if ( original instanceof EntityQuery ) {
			return (TYPE) transformEntityQuery( (EntityQuery) original );
		}

		if ( original instanceof EntityQueryCondition ) {
			return (TYPE) transformEntityQueryCondition( (EntityQueryCondition) original );
		}

		return original;
	}

	private EntityQueryExpression transformEntityQuery( EntityQuery original ) {
		EntityQuery newQuery = new EntityQuery( original.getOperand() );
		original.getExpressions()
		        .stream()
		        .map( this::transformExpression )
		        .forEach( newQuery::add );
		return newQuery;
	}

	private EntityQueryExpression transformEntityQueryCondition( EntityQueryCondition original ) {
		EntityPropertyDescriptor property = entityConfiguration.getPropertyRegistry().getProperty( original.getProperty() );
		TypeDescriptor propertyTypeDescriptor = property.getPropertyTypeDescriptor();
		Class<?> resolvedType = propertyTypeDescriptor.isArray() || propertyTypeDescriptor.isCollection()
				? propertyTypeDescriptor.getElementTypeDescriptor().getObjectType()
				: propertyTypeDescriptor.getObjectType();

		var mappingContext = elasticsearchOperations.getElasticsearchConverter().getMappingContext();
		if ( mappingContext.hasPersistentEntityFor( resolvedType ) ) {
			ElasticsearchPersistentEntity<?> persistentEntity = mappingContext.getPersistentEntity( resolvedType );
			ElasticsearchPersistentProperty idProperty = persistentEntity.getIdProperty();
			String referencedProperty = property.getName() + "." + idProperty.getName();
			try {
				Object[] args = transformArgumentsToIdValues( original, idProperty );
				return new EntityQueryCondition( referencedProperty, original.getOperand(), args );
			}
			catch ( IllegalAccessException | InvocationTargetException e ) {
				LOG.error( "An unexpected error occurred whilst trying to resolve the id property for " + resolvedType.getName(), e );
			}
		}
//
//		if ( entityRegistry.contains( resolvedType ) ) {
//			EntityConfiguration<?> propertyEntityConfiguration = entityRegistry.getEntityConfiguration( resolvedType );
//			Optional<EntityPropertyDescriptor> resolvedProperty =
//					propertyEntityConfiguration.getPropertyRegistry()
//					                           .getProperties()
//					                           .stream()
//					                           .filter( pd -> Objects.nonNull( pd.getPropertyTypeDescriptor() ) )
//					                           .filter( pd -> Objects.nonNull( pd.getPropertyTypeDescriptor().getAnnotation( Id.class ) ) )
//					                           .findFirst();
//			if ( resolvedProperty.isPresent() ) {
//				EntityPropertyDescriptor idProperty = resolvedProperty.get();
//				String referencedProperty = property.getName() + "." + idProperty.getName();
//				Object[] args = Arrays.stream( original.getArguments() ).map( idProperty::getPropertyValue ).toArray();
//				return new EntityQueryCondition( referencedProperty, original.getOperand(), args );
//			}
//		}
		return original;
	}

	private Object[] transformArgumentsToIdValues( EntityQueryCondition original,
	                                               ElasticsearchPersistentProperty idProperty ) throws IllegalAccessException, InvocationTargetException {
		Object[] args = new Object[original.getArguments().length];
		for ( int i = 0; i < original.getArguments().length; i++ ) {
			Object arg = original.getArguments()[i];
			args[i] = idProperty.getRequiredGetter().invoke( arg );
		}
		return args;
	}
}
