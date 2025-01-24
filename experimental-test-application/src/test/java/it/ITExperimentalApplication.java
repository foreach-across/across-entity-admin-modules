package it;

import com.foreach.across.test.support.config.ResetDatabaseConfigurer;
import com.foreach.across.test.support.config.TestDataSourceConfigurer;
import com.foreach.across.testapplication.ExperimentalModuleTestApplication;
import io.github.wimdeblauwe.testcontainers.cypress.CypressContainer;
import io.github.wimdeblauwe.testcontainers.cypress.CypressTest;
import io.github.wimdeblauwe.testcontainers.cypress.CypressTestResults;
import io.github.wimdeblauwe.testcontainers.cypress.CypressTestSuite;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DynamicContainer;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.testcontainers.Testcontainers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Actual bootstrap with an embedded web server on a random port.
 * Not using mock mvc so content repository servlets can correctly start.
 */
@Disabled
@Slf4j
@SpringBootTest(
		classes = { ExperimentalModuleTestApplication.class, ResetDatabaseConfigurer.class, TestDataSourceConfigurer.class },
		webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
class ITExperimentalApplication
{
	@LocalServerPort
	private int port;

	@TestFactory
	List<DynamicContainer> runCypressTests() throws InterruptedException, IOException, TimeoutException {

		Testcontainers.exposeHostPorts( port );

		try (CypressContainer container = new CypressContainer().withLocalServerPort( port )) {
			container.start();
			CypressTestResults testResults = container.getTestResults();

			return convertToJUnitDynamicTests( testResults ); // (2)
		}
	}

	@NotNull
	private List<DynamicContainer> convertToJUnitDynamicTests( CypressTestResults testResults ) {
		List<DynamicContainer> dynamicContainers = new ArrayList<>();
		List<CypressTestSuite> suites = testResults.getSuites();
		for ( CypressTestSuite suite : suites ) {
			createContainerFromSuite( dynamicContainers, suite );
		}
		return dynamicContainers;
	}

	private void createContainerFromSuite( List<DynamicContainer> dynamicContainers, CypressTestSuite suite ) {
		List<DynamicTest> dynamicTests = new ArrayList<>();
		for ( CypressTest test : suite.getTests() ) {
			dynamicTests.add( DynamicTest.dynamicTest( test.getDescription(), () -> {
				assertTrue( test.isSuccess(), "Test failed: %s".formatted( test.getDescription() ) );
			} ) );
		}
		dynamicContainers.add( DynamicContainer.dynamicContainer( suite.getTitle(), dynamicTests ) );
	}
}
