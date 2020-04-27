package com.foreach.across;

import com.atlassian.bamboo.specs.api.BambooSpec;
import com.atlassian.bamboo.specs.api.builders.BambooKey;
import com.atlassian.bamboo.specs.api.builders.applink.ApplicationLink;
import com.atlassian.bamboo.specs.api.builders.permission.PlanPermissions;
import com.atlassian.bamboo.specs.api.builders.plan.Plan;
import com.atlassian.bamboo.specs.api.builders.plan.PlanIdentifier;
import com.atlassian.bamboo.specs.api.builders.plan.branches.BranchCleanup;
import com.atlassian.bamboo.specs.api.builders.plan.branches.PlanBranchManagement;
import com.atlassian.bamboo.specs.api.builders.plan.configuration.AllOtherPluginsConfiguration;
import com.atlassian.bamboo.specs.api.builders.plan.configuration.ConcurrentBuilds;
import com.atlassian.bamboo.specs.api.builders.plan.dependencies.Dependencies;
import com.atlassian.bamboo.specs.api.builders.plan.dependencies.DependenciesConfiguration;
import com.atlassian.bamboo.specs.api.builders.project.Project;
import com.atlassian.bamboo.specs.api.builders.repository.VcsChangeDetection;
import com.atlassian.bamboo.specs.api.builders.repository.VcsRepository;
import com.atlassian.bamboo.specs.api.builders.repository.VcsRepositoryIdentifier;
import com.atlassian.bamboo.specs.api.builders.trigger.RepositoryBasedTrigger;
import com.atlassian.bamboo.specs.builders.repository.bitbucket.server.BitbucketServerRepository;
import com.atlassian.bamboo.specs.builders.repository.viewer.BitbucketServerRepositoryViewer;
import com.atlassian.bamboo.specs.builders.trigger.RepositoryPollingTrigger;
import com.atlassian.bamboo.specs.util.BambooServer;

import static com.foreach.across.Stages.*;

@BambooSpec
public class BuildPlan {
    private final static String PROJECT_KEY = "EXPERIMENTALMODULE";
    private static final String PLAN_NAME = "Experimental module - Build";
    private static final String PLAN_KEY = "EXPERIMENTALMODULEBUILD";
    private static final String DOCKER_COMPOSE = "docker-compose --no-ansi";
    private static final String LINKED_REPOSITORY_NAME = "Across - Experimental Module - develop";

    public static void main(String... argv) {
        //By default credentials are read from the '.credentials' file.
        BambooServer bambooServer = new BambooServer("https://bamboo.projects.foreach.be");

        final Project project = new Project().key(new BambooKey(PROJECT_KEY)).name("Foreach - Experimental Module");

        bambooServer.publish(repository());
        Plan plan = createPlan(project);
        plan.enabled(true);
        bambooServer.publish(plan);
        bambooServer.publish(planPermissions());
    }

    private static VcsRepository repository() {
        return new BitbucketServerRepository()
                .name(LINKED_REPOSITORY_NAME)
                .repositoryViewer(new BitbucketServerRepositoryViewer())
                .server(new ApplicationLink()
                        .name("Foreach Bitbucket")
                        .id("b3787b0e-3974-384b-89fb-c04f5e71b578"))
                .projectKey("FE")
                .repositorySlug("Across ExperimentalModule")
                .sshPublicKey(
                        "ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQCl+1HFUMvoIosZ1KQQPPiH3C1tpvGkK9WMQpQATki9Wu1rL8AupIqYe2dD7rCsoNmWZ4wauoMb569tqMvSh1ToIJOZQWjXte7K9eyeBeXa0I2mp7HVoZl9Sz6eY/+ViSYPhBWlLAzLyAek/vPeJZ1BvrC5UAxZSsBlUBPSlRR6gJWJ7SBDCNBawuDmxTxXYaLSbjbPeFwTLk3MrDHWhuXMStnI7txVxv5tvsu4tLQNtjbl0Mz573fTwCbeyYQSHnlSXPhrWuNcCgovkoGKE8MDH1TNH9EgcpMQCirx0lEsqYlG6KWdvT/trpYAs8L3Yi350AsZ7Y3FWL20ks4uP4nf https://bamboo.projects.foreach.be")
                .sshPrivateKey(
                        "BAMSCRT@0@0@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")
                .sshCloneUrl("ssh://git@bitbucket.foreach.be:7999/fe/across-experimentalmodule.git")
                .changeDetection(new VcsChangeDetection());
    }

    private static Plan createPlan(Project project) {
        return new Plan(project, BuildPlan.PLAN_NAME, new BambooKey(BuildPlan.PLAN_KEY))
                .pluginConfigurations(new ConcurrentBuilds().useSystemWideDefault(false), new AllOtherPluginsConfiguration())
                .stages(unitTests(), integrationTests(), deploySnapshot())
                .linkedRepositories(LINKED_REPOSITORY_NAME)
                .triggers(
                        new RepositoryPollingTrigger().description("Automatic build")
                                // Do not trigger builds when across-bamboo-specs is committed
                                .triggeringRepositoriesType(RepositoryBasedTrigger.TriggeringRepositoriesType.SELECTED)
                                .selectedTriggeringRepositories(new VcsRepositoryIdentifier()
                                        .name(LINKED_REPOSITORY_NAME))
                )
                .planBranchManagement(new PlanBranchManagement()
                        .createForVcsBranch()
                        .delete(new BranchCleanup()
                                .whenRemovedFromRepositoryAfterDays(10)
                                .whenInactiveInRepositoryAfterDays(10))
                        .notificationLikeParentPlan())
                .forceStopHungBuilds()
                .dependencies(new Dependencies().configuration(new DependenciesConfiguration().requireAllStagesPassing(true)));
    }

    private static PlanPermissions planPermissions() {
        return new PlanPermissions(new PlanIdentifier(PROJECT_KEY, PLAN_KEY));
    }

}

