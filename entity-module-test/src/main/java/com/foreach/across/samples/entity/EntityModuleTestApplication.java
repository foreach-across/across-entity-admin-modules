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

package com.foreach.across.samples.entity;

import com.foreach.across.config.AcrossApplication;
import com.foreach.across.modules.adminweb.AdminWebModule;
import com.foreach.across.modules.debugweb.DebugWebModule;
import com.foreach.across.modules.entity.EntityModule;
import com.foreach.across.modules.hibernate.jpa.AcrossHibernateJpaModule;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

/**
 * @author Arne Vandamme
 * @since 2.0.0
 */
@AcrossApplication(modules = { AdminWebModule.NAME, EntityModule.NAME, DebugWebModule.NAME })
@Import(DataSourceAutoConfiguration.class)
public class EntityModuleTestApplication
{
	@Bean
	public AcrossHibernateJpaModule acrossHibernateJpaModule() {
		AcrossHibernateJpaModule hibernateModule = new AcrossHibernateJpaModule();
		hibernateModule.setHibernateProperty( "hibernate.hbm2ddl.auto", "update" );
		return hibernateModule;
	}

	public static void main( String[] args ) {
		SpringApplication.run( EntityModuleTestApplication.class, args );
	}
}
