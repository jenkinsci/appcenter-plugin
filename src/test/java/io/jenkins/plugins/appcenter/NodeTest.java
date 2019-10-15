package io.jenkins.plugins.appcenter;

import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import hudson.model.Node;
import hudson.model.Result;
import hudson.slaves.RetentionStrategy;
import io.jenkins.plugins.appcenter.util.MockWebServerUtil;
import io.jenkins.plugins.appcenter.util.TestUtil;
import okhttp3.mockwebserver.MockWebServer;
import org.jenkinci.plugins.mock_slave.MockSlave;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import java.util.Collections;

import static com.google.common.truth.Truth.assertThat;

public class NodeTest {

    @ClassRule
    public static JenkinsRule jenkinsRule = new JenkinsRule();

    @Rule
    public MockWebServer mockWebServer = new MockWebServer();

    private FreeStyleProject freeStyleProject;

    private Node slave;

    @Before
    public void setUp() throws Exception {
        freeStyleProject = jenkinsRule.createFreeStyleProject();
        freeStyleProject.getBuildersList().add(TestUtil.createFileForFreeStyle("three/days/xiola.ipa"));
        freeStyleProject.getBuildersList().add(TestUtil.createFileForFreeStyle("three/days/symbols.dsym"));

        final AppCenterRecorder appCenterRecorder = new AppCenterRecorder("at-this-moment-you-should-be-with-us", "janes-addiction", "ritual-de-lo-habitual", "three/days/xiola.ipa", "three/days/symbols.dsym", "casey, niccoli");
        appCenterRecorder.setBaseUrl(mockWebServer.url("/").toString());
        freeStyleProject.getPublishersList().add(appCenterRecorder);

        slave = new MockSlave("test-slave", 1, Node.Mode.NORMAL, "", RetentionStrategy.Always.INSTANCE, Collections.emptyList());
        jenkinsRule.jenkins.addNode(slave);
        freeStyleProject.setAssignedNode(slave);
    }

    @Test
    public void should_BuildFreeStyleProject_When_RunOnANode() throws Exception {
        // Given
        MockWebServerUtil.enqueueSuccess(mockWebServer);

        // When
        final FreeStyleBuild freeStyleBuild = freeStyleProject.scheduleBuild2(0).get();

        // Then
        jenkinsRule.assertBuildStatus(Result.SUCCESS, freeStyleBuild);
        assertThat(freeStyleBuild.getBuiltOn()).isEqualTo(slave);
    }
}