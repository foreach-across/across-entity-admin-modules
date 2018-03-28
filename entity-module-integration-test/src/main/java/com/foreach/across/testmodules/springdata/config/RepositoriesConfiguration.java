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

package com.foreach.across.testmodules.springdata.config;

import com.foreach.across.modules.hibernate.jpa.repositories.config.EnableAcrossJpaRepositories;
import com.foreach.across.testmodules.springdata.repositories.ClientRepository;
import com.foreach.across.testmodules.springdata.repositories2.RepresentativeRepository;
import org.springframework.context.annotation.Configuration;

/**
 * @author Arne Vandamme
 * @since 3.0.0
 */
@SuppressWarnings("unused")
public class RepositoriesConfiguration
{
	@Configuration
	@EnableAcrossJpaRepositories(basePackageClasses = ClientRepository.class)
	public static class ClientConfig
	{
	}

	@Configuration
	@EnableAcrossJpaRepositories(basePackageClasses = RepresentativeRepository.class, transactionManagerRef = "otherTransactionManager")
	public static class RepresentativeConfig
	{
	}
}
