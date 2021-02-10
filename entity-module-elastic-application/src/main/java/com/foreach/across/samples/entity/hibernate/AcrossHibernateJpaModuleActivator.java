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

package com.foreach.across.samples.entity.hibernate;

import com.foreach.across.config.AcrossContextConfigurer;
import com.foreach.across.core.AcrossContext;
import com.foreach.across.core.context.ModuleDependencyResolver;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;

/**
 * Configures the AcrossHibernateJpaModule if it's present on the classpath to also support jpa entities in combination with elastic entities.
 */
@Configuration
@ConditionalOnClass(name = "com.foreach.across.modules.hibernate.jpa.AcrossHibernateJpaModule")
public class AcrossHibernateJpaModuleActivator implements AcrossContextConfigurer
{
	@Override
	public void configure( AcrossContext context ) {
		ModuleDependencyResolver moduleDependencyResolver = context.getModuleDependencyResolver();
		if ( moduleDependencyResolver != null ) {
			moduleDependencyResolver.resolveModule( "AcrossHibernateJpaModule", true )
			                        .ifPresent( context::addModule );
		}
	}
}