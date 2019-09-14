package io.jenkins.plugins.appcenter;

import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import hudson.model.Node;
import hudson.model.Result;
import hudson.slaves.RetentionStrategy;
import io.jenkins.plugins.appcenter.api.MockWebServerUtil;
import okhttp3.mockwebserver.MockWebServer;
import org.jenkinci.plugins.mock_slave.MockSlave;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.TestBuilder;

import java.io.IOException;
import java.util.Collections;
import java.util.Objects;

import static com.google.common.truth.Truth.assertThat;

public class NodeTest {

    @ClassRule
    public static JenkinsRule jenkinsRule = new JenkinsRule();

    @Rule
    public MockWebServer mockWebServer = new MockWebServer();

    private Node slave;

    @Before
    public void setUp() throws Exception {
        slave = new MockSlave("test-slave", 1, Node.Mode.NORMAL, "", RetentionStrategy.Always.INSTANCE, Collections.emptyList());
        jenkinsRule.jenkins.addNode(slave);
    }

    @Test
    public void should_BuildFreeStyleProject_When_RunOnANode() throws Exception {
        // Given
        final AppCenterRecorder appCenterRecorder = new AppCenterRecorder("at-this-moment-you-should-be-with-us", "janes-addiction", "ritual-de-lo-habitual", "three/days/xiola.apk", "casey, niccoli");
        appCenterRecorder.setBaseUrl(mockWebServer.url("/").toString());

        final FreeStyleProject freeStyleProject = jenkinsRule.createFreeStyleProject();
        freeStyleProject.getBuildersList().add(new TestAppWriter());
        freeStyleProject.getPublishersList().add(appCenterRecorder);
        freeStyleProject.setAssignedNode(slave);

        MockWebServerUtil.enqueueSuccess(mockWebServer);

        // When
        final FreeStyleBuild freeStyleBuild = freeStyleProject.scheduleBuild2(0).get();

        // Then
        jenkinsRule.assertBuildStatus(Result.SUCCESS, freeStyleBuild);
        assertThat(freeStyleBuild.getBuiltOn()).isEqualTo(slave);
    }

    private static class TestAppWriter extends TestBuilder {
        public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener)
            throws InterruptedException, IOException {
            Objects.requireNonNull(build.getWorkspace()).child("three/days/xiola.apk").write("all of us with wings", "UTF-8");
            return true;
        }
    }
}