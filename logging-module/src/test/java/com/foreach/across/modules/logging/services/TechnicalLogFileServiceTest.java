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
package com.foreach.across.modules.logging.services;

import com.foreach.across.modules.logging.LoggingModuleSettings;
import com.foreach.across.modules.logging.business.FileStrategy;
import com.foreach.across.modules.logging.business.LogType;
import com.foreach.common.test.MockedLoader;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = MockedLoader.class, classes = TechnicalLogFileServiceTest.Config.class)
@DirtiesContext
public class TechnicalLogFileServiceTest
{
	@Autowired
	private TechnicalLogFileService technicalLogFileService;

	@Autowired
	private LoggingModuleSettings loggingModuleSettings;

	@Before
	public void resetMocks() {
		reset( loggingModuleSettings );
	}

	@Test
	public void technicalLogFileServiceSupportsOnlyTechnical() {
		when( loggingModuleSettings.getTechnicalFileStrategy() ).thenReturn( FileStrategy.LOGBACK );
		assertFalse( technicalLogFileService.supports( LogType.FUNCTIONAL ) );
		assertTrue( technicalLogFileService.supports( LogType.TECHNICAL ) );
	}

	@Test
	public void technicalLogFileServiceSupportsLogbackStrategy() {
		when( loggingModuleSettings.getTechnicalFileStrategy() ).thenReturn( FileStrategy.LOGBACK );
		assertTrue( technicalLogFileService.supports( LogType.TECHNICAL ) );
		when( loggingModuleSettings.getTechnicalFileStrategy() ).thenReturn( FileStrategy.NONE );
		assertFalse( technicalLogFileService.supports( LogType.TECHNICAL ) );
	}

	@Test
	public void technicalLogFileServiceLogsToTechnicalLog() {
		assertTrue( true ); //TODO Technical logging test not implemented yet, unsure how to properly mock the logger.
	}

	@Configuration
	static class Config
	{
		@Bean
		public TechnicalLogFileService technicalLogFileService() {
			return new TechnicalLogFileService();
		}
	}
}
