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

public class FreestyleTest {

    @ClassRule
    public static JenkinsRule jenkinsRule = new JenkinsRule();

    @Rule
    public MockWebServer mockWebServer = new MockWebServer();

    private FreeStyleProject freeStyleProject;

    @Before
    public void setUp() throws IOException {
        freeStyleProject = jenkinsRule.createFreeStyleProject();
        freeStyleProject.getBuildersList().add(new TestAppWriter());
        final AppCenterRecorder appCenterRecorder = new AppCenterRecorder("token", "owner_name", "app_name", "Collaborators", "path/to/app.apk");
        appCenterRecorder.setBaseUrl(mockWebServer.url("/").toString());
        freeStyleProject.getPublishersList().add(appCenterRecorder);
    }

    @Test
    public void should_SetBuildResultFailure_When_UploadTaskFails() throws Exception {
        // Given
        MockWebServerUtil.enqueueFailure(mockWebServer);

        // When
        final FreeStyleBuild freeStyleBuild = freeStyleProject.scheduleBuild2(0).get();

        // Then
        jenkinsRule.assertBuildStatus(Result.FAILURE, freeStyleBuild);
    }

    @Test
    public void should_SetBuildResultSuccess_When_AppCenterAcceptsAllRequests() throws Exception {
        // Given
        MockWebServerUtil.enqueueSuccess(mockWebServer);

        // When
        final FreeStyleBuild freeStyleBuild = freeStyleProject.scheduleBuild2(0).get();

        // Then
        jenkinsRule.assertBuildStatus(Result.SUCCESS, freeStyleBuild);
    }

    @Test
    public void should_SetBuildResultSuccess_When_RunOnANode() throws Exception {
        // Given
        MockWebServerUtil.enqueueSuccess(mockWebServer);
        final Node slave = new MockSlave("test-slave", 1, Node.Mode.NORMAL, "", RetentionStrategy.Always.INSTANCE, Collections.emptyList());
        jenkinsRule.jenkins.addNode(slave);
        freeStyleProject.setAssignedNode(slave);

        // When
        final FreeStyleBuild freeStyleBuild = freeStyleProject.scheduleBuild2(0).get();

        // Then
        jenkinsRule.assertBuildStatus(Result.SUCCESS, freeStyleBuild);
        assertThat(freeStyleBuild.getBuiltOn()).isEqualTo(slave);
    }

    private static class TestAppWriter extends TestBuilder {
        public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener)
            throws InterruptedException, IOException {
            Objects.requireNonNull(build.getWorkspace()).child("path/to/app.apk").write("little tiny robots", "UTF-8");
            return true;
        }
    }
}