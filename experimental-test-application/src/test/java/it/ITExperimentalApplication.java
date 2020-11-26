package it;

import com.foreach.across.test.support.config.ResetDatabaseConfigurer;
import com.foreach.across.test.support.config.TestDataSourceConfigurer;
import com.foreach.across.testapplication.ExperimentalModuleTestApplication;
import com.foreach.across.testapplication.application.domain.drink.DrinkRepository;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.model.ContainerNetwork;
import io.github.wimdeblauwe.testcontainers.cypress.CypressContainer;
import io.github.wimdeblauwe.testcontainers.cypress.CypressTestResults;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.util.ReflectionTestUtils;
import org.testcontainers.Testcontainers;
import org.testcontainers.containers.BindMode;
import org.testcontainers.dockerclient.DockerClientConfigUtils;
import org.testcontainers.utility.MountableFile;

import java.net.InetAddress;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Actual bootstrap with an embedded web server on a random port.
 * Not using mock mvc so content repository servlets can correctly start.
 */
@Slf4j
@SpringBootTest(
		classes = {ExperimentalModuleTestApplication.class, ResetDatabaseConfigurer.class, TestDataSourceConfigurer.class},
		webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
class ITExperimentalApplication {
	@LocalServerPort
	private int port;

	@Test
	@SneakyThrows
	void runCypressTests(@Autowired DrinkRepository drinkRepository) {
		assertThat(drinkRepository.findAll()).isEmpty();

		try (CypressContainer container = new DockerInDockerSupportingCypressContainer(port)
				.withAutoCleanReports(false)
				.withLocalServerPort(port)) {
			container.start();

			CypressTestResults testResults = container.getTestResults();

			if (testResults.getNumberOfFailingTests() > 0) {
				fail("There was a failure running the Cypress tests!\n\n" + testResults);
			}
		}
	}

	public static class DockerInDockerSupportingCypressContainer extends CypressContainer {

		private final int port;
		private final String hostname;

		@SneakyThrows
		public DockerInDockerSupportingCypressContainer(int port) {
			this.port = port;
			this.hostname = InetAddress.getLocalHost().getHostName();
		}

		/***
		 * After the CypressContainer is started, we have containerId.
		 * We can then use this containerId to connect it to the maven network.
		 */
		@Override
		@SneakyThrows
		protected void doStart() {
			super.doStart();

			if (DockerClientConfigUtils.IN_A_CONTAINER) {
				ContainerNetwork network = mavenContainer().getNetworkSettings().getNetworks().values()
						.stream()
						.findFirst()
						.orElseThrow(() -> new RuntimeException("Cannot find a network"));
				String ip = network.getIpAddress();

				// Connect the cypress container to the maven container network
				dockerClient.connectToNetworkCmd().withContainerId(getContainerId()).withNetworkId(network.getNetworkID()).exec();

				System.out.println("======== Connecting cypress container to maven container ==============");
				System.out.println(hostname + " -> " + ip + ":" + port + " /  container connecting to " + network.getNetworkID());
				System.out.println("==============================================================================");
			}
		}

		@Override
		@SneakyThrows
		protected void configure() {
			if (!DockerClientConfigUtils.IN_A_CONTAINER) {
				Testcontainers.exposeHostPorts(port);
				super.configure();
			} else {
				ContainerNetwork network = mavenContainer().getNetworkSettings().getNetworks().values()
						.stream()
						.findFirst()
						.orElseThrow(() -> new RuntimeException("Cannot find a network"));
				String ip = network.getIpAddress();

				addEnv("CYPRESS_baseUrl", "http://" + ip + ":" + port);
				configureBindMountForDockerInDocker();
				// When you want to test your container, make it wait
//				this.withCreateContainerCmdModifier((cmd) -> {
//					cmd.withEntrypoint(new String[]{"tail", "-f", "/dev/null"});
//				});
				withCreateContainerCmdModifier(cmd -> {
					String buildEntryPoint = ReflectionTestUtils.invokeMethod(this, "buildEntryPoint");
					cmd.withEntrypoint("bash", "-c", buildEntryPoint);
				});
			}
		}

		private InspectContainerResponse mavenContainer() {
			return dockerClient.inspectContainerCmd(hostname).exec();
		}

		/***
		 * When using Docker out of Docker, the classPathResource will resolve to the location inside the maven container.
		 * This would mount the wrong path inside the CypressContainer (it needs to use the host path).
		 * This helper method will attempt to find the correct host location from the maven container and use this one instead.
		 */
		@SneakyThrows
		private void configureBindMountForDockerInDocker() {
			final MountableFile mountableFile = MountableFile.forClasspathResource("e2e");

			List<InspectContainerResponse.Mount> mounts = mavenContainer().getMounts();
			if (mounts != null) {
				String pathInMavenContainer = mountableFile.getResolvedPath();
				Optional<String> first = mounts.stream().filter(m -> StringUtils.equalsIgnoreCase("/usr/src/app", m.getDestination().getPath())).map(m -> m.getSource()).findFirst();
				if (first.isPresent()) {
					String hostPath = StringUtils.replace(pathInMavenContainer, "/usr/src/app", first.get());
					this.withFileSystemBind(hostPath, "/e2e", BindMode.READ_WRITE);
				}
			}
		}
	}
}
