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

package com.foreach.across.testmodules.elastic.domain.jpa.country;

import com.foreach.across.core.annotations.Exposed;
import com.foreach.across.modules.hibernate.aop.EntityInterceptorAdapter;
import com.foreach.across.modules.hibernate.jpa.AcrossHibernateJpaModule;
import com.foreach.across.testmodules.elastic.domain.elastic.country.ElasticCountry;
import com.foreach.across.testmodules.elastic.domain.elastic.country.ElasticCountryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Exposed
@ConditionalOnClass(AcrossHibernateJpaModule.class)
public class SyncCountryToElasticInterceptor extends EntityInterceptorAdapter<Country>
{
	private final ElasticCountryRepository elasticCountryRepository;

	@Override
	public boolean handles( Class<?> entityClass ) {
		return Country.class.isAssignableFrom( entityClass );
	}

	@Override
	public void afterCreate( Country entity ) {
		ElasticCountry elasticCustomer = convert( entity );
		elasticCountryRepository.save( elasticCustomer );
	}

	private ElasticCountry convert( Country entity ) {
		ElasticCountry elasticCustomer = new ElasticCountry();
		copyOver( entity, elasticCustomer );
		return elasticCustomer;
	}

	private void copyOver( Country entity, ElasticCountry elasticCountry ) {
		elasticCountry.setId( entity.getId() );
		elasticCountry.setName( entity.getName() );
	}

	@Override
	public void afterUpdate( Country entity ) {
		ElasticCountry elasticCountry = elasticCountryRepository.findById( entity.getId() )
		                                                        .orElseGet( ElasticCountry::new );
		copyOver( entity, elasticCountry );
		elasticCountryRepository.save( elasticCountry );
	}

	@Override
	public void afterDelete( Country entity ) {
		elasticCountryRepository.findById( entity.getId() )
		                        .ifPresent( elasticCountryRepository::delete );
	}
}
