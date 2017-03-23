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

package it;

import com.foreach.across.samples.entity.EntityModuleTestApplication;
import com.foreach.cuke.core.cli.ConsoleRunner;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.embedded.EmbeddedWebApplicationContext;

import java.util.Properties;

import static org.junit.Assert.assertEquals;

/**
 * @author Arne Vandamme
 */
public class ITCucumberTests
{

    private static Integer webServerPort;

    @BeforeClass
    public static void bootApplicationIfRequired() {
        if (webServerPort == null) {
            System.out.println("Booting application for integration tests");

            SpringApplication application = new SpringApplication( EntityModuleTestApplication.class);
            application.setAdditionalProfiles("dev", "cucumber");
            EmbeddedWebApplicationContext webApplicationContext = (EmbeddedWebApplicationContext) application.run();

            // retrieve the actual webserver port
            webServerPort = webApplicationContext.getEmbeddedServletContainer().getPort();

            System.out.println("Application bootstrapped on webserver port " + webServerPort);
        }
    }

    @Test
    public void runFeatures() throws Exception {
        Properties systemProperties = new Properties();
        systemProperties.setProperty("webServerPort", webServerPort.toString());
        systemProperties.setProperty("browser", "chrome");

        ConsoleRunner consoleRunner = new ConsoleRunner("target/cucumber/desktop", "src/test/resources/features");
        consoleRunner.setProperties(systemProperties);
        consoleRunner.setTags(new String[]{"~@ignore"});
        consoleRunner.setNumberOfThreads(5);
        consoleRunner.setNumberOfRetries(3);

        consoleRunner.setGenerateHtmlReports(true);

        assertEquals(0, consoleRunner.start());
    }

}
