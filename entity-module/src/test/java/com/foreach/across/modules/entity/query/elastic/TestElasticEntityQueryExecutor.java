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

import com.foreach.across.modules.entity.config.builders.EntityPropertyDescriptorBuilder;
import com.foreach.across.modules.entity.query.EntityQuery;
import com.foreach.across.modules.entity.query.EntityQueryCondition;
import com.foreach.across.modules.entity.query.EntityQueryOps;
import com.foreach.across.modules.entity.registry.EntityConfigurationImpl;
import com.foreach.across.modules.entity.registry.properties.DefaultEntityPropertyRegistry;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.ToString;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
	private EntityConfigurationImpl<NestedTestEntity> entityConfiguration;

	private ElasticsearchOperations elasticsearchOperations;
	private MappingContext<ElasticsearchPersistentEntity<?>, ElasticsearchPersistentProperty> mappingContext;

	@BeforeEach
	@SuppressWarnings("unchecked")
	void setUp() {
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

		executor = new ElasticEntityQueryExecutor<>( elasticsearchOperations, entityConfiguration );
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
