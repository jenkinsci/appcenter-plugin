package io.jenkins.plugins.appcenter;

import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import hudson.model.Result;
import io.jenkins.plugins.appcenter.api.MockWebServerUtil;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.TestBuilder;

import java.io.IOException;
import java.util.Objects;

public class EnvInterpolationTest {

    @ClassRule
    public static JenkinsRule jenkinsRule = new JenkinsRule();

    @Rule
    public MockWebServer mockWebServer = new MockWebServer();

    private FreeStyleProject freeStyleProject;

    @Before
    public void setUp() throws IOException {
        MockWebServerUtil.enqueueSuccess(mockWebServer);
        freeStyleProject = jenkinsRule.createFreeStyleProject();
        freeStyleProject.getBuildersList().add(new TestBuilder() {
            public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener)
                throws InterruptedException, IOException {
                Objects.requireNonNull(build.getWorkspace()).child("path/to/app-42.apk").write("little tiny robots", "UTF-8");
                return true;
            }
        });
    }

    @Test
    public void should_InterpolateEnv_InAppPath() throws Exception {
        // Given
        final AppCenterRecorder appCenterRecorder = new AppCenterRecorder("token", "owner_name", "app_name", "Collaborators", "path/to/app-${BUILD_NUMBER}.apk");
        appCenterRecorder.setBaseUrl(mockWebServer.url("/").toString());

        freeStyleProject.getPublishersList().add(appCenterRecorder);
        freeStyleProject.updateNextBuildNumber(42);

        // When
        final FreeStyleBuild freeStyleBuild = freeStyleProject.scheduleBuild2(0).get();

        // Then
        jenkinsRule.assertBuildStatus(Result.SUCCESS, freeStyleBuild);
    }

    @Test
    public void should_LeaveUnchangedWhenNotInEnv_InAppPath() throws Exception {
        // Given
        final AppCenterRecorder appCenterRecorder = new AppCenterRecorder("token", "owner_name", "app_name", "Collaborators", "path/to/app-${NOTINENV}.apk");
        appCenterRecorder.setBaseUrl(mockWebServer.url("/").url().toString());

        freeStyleProject.getPublishersList().add(appCenterRecorder);
        freeStyleProject.updateNextBuildNumber(42);

        // When
        final FreeStyleBuild freeStyleBuild = freeStyleProject.scheduleBuild2(0).get();

        // Then
        jenkinsRule.assertBuildStatus(Result.FAILURE, freeStyleBuild);
        jenkinsRule.assertLogContains("File not found: path/to/app-${NOTINENV}.apk", freeStyleBuild);
    }
}