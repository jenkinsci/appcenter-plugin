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
        final AppCenterRecorder appCenterRecorder = new AppCenterRecorder("at-this-moment-you-should-be-with-us", "janes-addiction", "ritual-de-lo-habitual", "three/days/xiola.apk", "casey, niccoli");
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

    private static class TestAppWriter extends TestBuilder {
        public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener)
            throws InterruptedException, IOException {
            Objects.requireNonNull(build.getWorkspace()).child("three/days/xiola.apk").write("all of us with wings", "UTF-8");
            return true;
        }
    }
}