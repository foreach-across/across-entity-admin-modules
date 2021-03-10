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

import com.foreach.across.modules.entity.EntityAttributes;
import com.foreach.across.modules.entity.config.builders.EntityPropertyDescriptorBuilder;
import com.foreach.across.modules.entity.query.EntityQuery;
import com.foreach.across.modules.entity.query.EntityQueryCondition;
import com.foreach.across.modules.entity.query.EntityQueryOps;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.EntityConfigurationImpl;
import com.foreach.across.modules.entity.registry.EntityRegistry;
import com.foreach.across.modules.entity.registry.properties.DefaultEntityPropertyRegistry;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistry;
import com.foreach.across.modules.entity.registry.properties.MutableEntityPropertyDescriptor;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.ToString;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.convert.ElasticsearchConverter;
import org.springframework.data.elasticsearch.core.mapping.ElasticsearchPersistentEntity;
import org.springframework.data.elasticsearch.core.mapping.ElasticsearchPersistentProperty;
import org.springframework.data.mapping.context.MappingContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestElasticEntityQueryExecutor
{
	private ElasticEntityQueryExecutor<NestedTestEntity> executor;
	private EntityRegistry entityRegistry;
	private EntityConfigurationImpl<NestedTestEntity> entityConfiguration;

	private ElasticsearchOperations elasticsearchOperations;
	private MappingContext<ElasticsearchPersistentEntity<?>, ElasticsearchPersistentProperty> mappingContext;

	@BeforeEach
	@SuppressWarnings("unchecked")
	void setUp() {
		entityRegistry = mock( EntityRegistry.class );
		entityConfiguration = new EntityConfigurationImpl<>( NestedTestEntity.class );
		DefaultEntityPropertyRegistry propertyRegistry = new DefaultEntityPropertyRegistry();
		propertyRegistry.register(
				new EntityPropertyDescriptorBuilder( "entity" )
						.propertyType( TestEntity.class )
						.build()
		);
		propertyRegistry.register(
				new EntityPropertyDescriptorBuilder( "name" )
						.propertyType( String.class )
						.build()
		);
		entityConfiguration.setPropertyRegistry( propertyRegistry );

		elasticsearchOperations = mock( ElasticsearchOperations.class );
		ElasticsearchConverter elasticsearchConverter = mock( ElasticsearchConverter.class );
		when( elasticsearchOperations.getElasticsearchConverter() ).thenReturn( elasticsearchConverter );
		mappingContext = mock( MappingContext.class );
		when( elasticsearchConverter.getMappingContext() ).thenReturn( (MappingContext) mappingContext );

		executor = new ElasticEntityQueryExecutor<>( elasticsearchOperations, entityRegistry, entityConfiguration );
	}

	@Test
	@SneakyThrows
	void convertQueryForDomainObjects() {
		TestEntity testEntity = new TestEntity();
		testEntity.setId( RandomStringUtils.randomAlphanumeric( 10 ) );
		testEntity.setName( RandomStringUtils.randomAlphanumeric( 10 ) );
		EntityQuery originalQuery = EntityQuery.and( new EntityQueryCondition( "entity", EntityQueryOps.EQ, testEntity ),
		                                             new EntityQueryCondition( "name", EntityQueryOps.CONTAINS, "Joan" ) );
		assertThat( originalQuery.toString() )
				.isEqualTo( "entity = TestElasticEntityQueryExecutor.TestEntity(id=" + testEntity.getId() + ") and name contains 'Joan'" );

		when( mappingContext.hasPersistentEntityFor( TestEntity.class ) ).thenReturn( true );
		ElasticsearchPersistentEntity persistentEntity = mock( ElasticsearchPersistentEntity.class );
		when( mappingContext.getPersistentEntity( TestEntity.class ) ).thenReturn( persistentEntity );
		ElasticsearchPersistentProperty persistentProperty = mock( ( ElasticsearchPersistentProperty.class ) );
		when( persistentEntity.getIdProperty() ).thenReturn( persistentProperty );
		when( persistentProperty.getRequiredGetter() ).thenReturn( TestEntity.class.getDeclaredMethod( "getId" ) );
		when( persistentProperty.getName() ).thenReturn( "id" );

		EntityQuery transformedQuery = executor.transformExpression( originalQuery );
		assertThat( transformedQuery.toString() )
				.isEqualTo( "entity.id = '" + testEntity.getId() + "' and name contains 'Joan'" );
	}

	@Test
	@SneakyThrows
	void convertSortForDomainObjects() {
		when( entityRegistry.contains( TestEntity.class ) ).thenReturn( true );
		EntityConfiguration typeConfiguration = mock( EntityConfiguration.class );
		when( entityRegistry.getEntityConfiguration( TestEntity.class ) ).thenReturn( typeConfiguration );
		MutableEntityPropertyDescriptor labelDescriptor = new EntityPropertyDescriptorBuilder( EntityPropertyRegistry.LABEL )
				.attribute( EntityAttributes.LABEL_TARGET_PROPERTY, "name" )
				.build();
		EntityPropertyRegistry typePropertyRegistry = mock( EntityPropertyRegistry.class );
		when( typeConfiguration.getPropertyRegistry() ).thenReturn( typePropertyRegistry );
		when( typePropertyRegistry.getProperty( EntityPropertyRegistry.LABEL ) ).thenReturn( labelDescriptor );
		when( mappingContext.hasPersistentEntityFor( TestEntity.class ) ).thenReturn( true );
		ElasticsearchPersistentEntity persistentEntity = mock( ElasticsearchPersistentEntity.class );
		when( mappingContext.getPersistentEntity( TestEntity.class ) ).thenReturn( persistentEntity );
		ElasticsearchPersistentProperty persistentProperty = mock( ( ElasticsearchPersistentProperty.class ) );
		when( persistentEntity.getPersistentProperty( "name" ) ).thenReturn( persistentProperty );
		when( persistentProperty.getName() ).thenReturn( "name" );

		Sort originalSort = Sort.by( Sort.Order.desc( "entity" ), Sort.Order.asc( "name" ) );
		Sort transformedSort = executor.transformSort( originalSort );
		assertThat( originalSort.toString() ).isEqualTo( "entity: DESC,name: ASC" );
		assertThat( transformedSort.toString() ).isEqualTo( "entity.name: DESC,name: ASC" );

		originalSort = Sort.by( Sort.Order.desc( "name" ), Sort.Order.asc( "entity" ) );
		transformedSort = executor.transformSort( originalSort );
		assertThat( originalSort.toString() ).isEqualTo( "name: DESC,entity: ASC" );
		assertThat( transformedSort.toString() ).isEqualTo( "name: DESC,entity.name: ASC" );
	}

	@Data
	@ToString(of = "id")
	public class TestEntity
	{
		private String id;
		private String name;
	}

	@Data
	@ToString(of = "id")
	public class NestedTestEntity
	{
		private String id;
		private String name;
		private TestEntity entity;
	}
}
