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

package testmodules.solr.config;

import org.apache.solr.client.solrj.SolrClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.solr.core.SolrOperations;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.repository.config.EnableSolrRepositories;
import testmodules.solr.SolrTestModule;

import static org.mockito.Mockito.mock;

/**
 * @author Arne Vandamme
 */
@Configuration
@EnableSolrRepositories(basePackageClasses = SolrTestModule.class)
public class SolrConfig
{
	@Bean
	public SolrClient solrClient() {
		return mock( SolrClient.class );
	}

	@Bean
	public SolrOperations solrTemplate( SolrClient solrClient ) {
		return new SolrTemplate( solrClient );
	}

}
