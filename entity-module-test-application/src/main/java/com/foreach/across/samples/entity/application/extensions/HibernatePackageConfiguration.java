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

package com.foreach.across.samples.entity.application.extensions;

import com.blazebit.persistence.Criteria;
import com.blazebit.persistence.CriteriaBuilderFactory;
import com.blazebit.persistence.integration.view.spring.EnableEntityViews;
import com.blazebit.persistence.spi.CriteriaBuilderConfiguration;
import com.blazebit.persistence.view.EntityViewManager;
import com.blazebit.persistence.view.spi.EntityViewConfiguration;
import com.foreach.across.core.annotations.Exposed;
import com.foreach.across.core.annotations.ModuleConfiguration;
import com.foreach.across.modules.hibernate.jpa.AcrossHibernateJpaModule;
import com.foreach.across.modules.hibernate.provider.HibernatePackageConfigurer;
import com.foreach.across.modules.hibernate.provider.HibernatePackageRegistry;
import com.foreach.across.samples.entity.application.business.Group;
import org.springframework.context.annotation.Bean;

import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

/**
 * @author Arne Vandamme
 * @since 2.0.0
 */
@ModuleConfiguration(AcrossHibernateJpaModule.NAME)
@EnableEntityViews(basePackages = { "com.foreach.across.samples.entity.application.view" })
public class HibernatePackageConfiguration implements HibernatePackageConfigurer
{
	@PersistenceUnit
	private EntityManagerFactory entityManagerFactory;

	@Bean
	@Exposed
	public CriteriaBuilderFactory createCriteriaBuilderFactory() {
		CriteriaBuilderConfiguration config = Criteria.getDefault();
		return config.createCriteriaBuilderFactory( entityManagerFactory );
	}

	@Bean
	@Exposed
	public EntityViewManager createEntityViewManager( CriteriaBuilderFactory cbf, EntityViewConfiguration entityViewConfiguration ) {
		return entityViewConfiguration.createEntityViewManager( cbf );
	}

	@Override
	public void configureHibernatePackage( HibernatePackageRegistry hibernatePackageRegistry ) {
		hibernatePackageRegistry.addPackageToScan( Group.class );
	}
}
