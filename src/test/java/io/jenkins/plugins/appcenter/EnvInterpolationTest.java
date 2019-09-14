package io.jenkins.plugins.appcenter;

import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import hudson.model.Result;
import io.jenkins.plugins.appcenter.util.MockWebServerUtil;
import io.jenkins.plugins.appcenter.util.TestUtil;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import java.io.IOException;

public class EnvInterpolationTest {

    @ClassRule
    public static JenkinsRule jenkinsRule = new JenkinsRule();

    @Rule
    public MockWebServer mockWebServer = new MockWebServer();

    private FreeStyleProject freeStyleProject;

    @Before
    public void setUp() throws IOException {
        freeStyleProject = jenkinsRule.createFreeStyleProject();
        freeStyleProject.getBuildersList().add(TestUtil.createFileForFreeStyle("path/to/app-42.apk"));
    }

    @Test
    public void should_InterpolateEnv_InAppPath() throws Exception {
        // Given
        MockWebServerUtil.enqueueSuccess(mockWebServer);
        final AppCenterRecorder appCenterRecorder = new AppCenterRecorder("at-this-moment-you-should-be-with-us", "janes-addiction", "ritual-de-lo-habitual", "path/to/app-${BUILD_NUMBER}.apk", "casey, niccoli");
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
        final AppCenterRecorder appCenterRecorder = new AppCenterRecorder("at-this-moment-you-should-be-with-us", "janes-addiction", "ritual-de-lo-habitual", "path/to/app-${NOTINENV}.apk", "casey, niccoli");
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