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
package com.foreach.across.modules.it.ehcache;

import com.foreach.across.config.AcrossContextConfigurer;
import com.foreach.across.core.AcrossContext;
import com.foreach.across.modules.ehcache.EhcacheModule;
import com.foreach.across.test.AcrossTestConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collection;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext
@ContextConfiguration(classes = ITEhcacheModule.Config.class)
public class ITEhcacheModule
{

	@Autowired
	private CacheManager cacheManager;

	@Test
	public void bootstrapModule() {
		assertNotNull( cacheManager );

		Collection<String> cacheNames = cacheManager.getCacheNames();
		assertNotNull( cacheNames );
		assertEquals( 1, cacheNames.size() );
		assertEquals( "oneElementCache", cacheNames.iterator().next() );

		Cache cache = cacheManager.getCache( "oneElementCache" );
		assertNotNull( cache );

		cache.put( "item1", "value1" );
		assertNotNull( cache.get( "item1" ) );

		cache.put( "item2", "value2" );
		assertNull( cache.get( "item1" ) );
		assertNotNull( cache.get( "item2" ) );
	}

	@Configuration
	@AcrossTestConfiguration
	protected static class Config implements AcrossContextConfigurer
	{
		@Override
		public void configure( AcrossContext context ) {
			EhcacheModule ehcacheModule = new EhcacheModule();
			ehcacheModule.setConfigLocation( new ClassPathResource( "test-ehcache.xml" ) );
			context.addModule( ehcacheModule );
		}
	}
}