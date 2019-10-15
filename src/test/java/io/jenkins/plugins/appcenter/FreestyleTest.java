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

public class FreestyleTest {

    @ClassRule
    public static JenkinsRule jenkinsRule = new JenkinsRule();

    @Rule
    public MockWebServer mockWebServer = new MockWebServer();

    private FreeStyleProject freeStyleProject;

    @Before
    public void setUp() throws IOException {
        freeStyleProject = jenkinsRule.createFreeStyleProject();
        freeStyleProject.getBuildersList().add(TestUtil.createFileForFreeStyle("three/days/xiola.ipa", "I am the app file"));
        freeStyleProject.getBuildersList().add(TestUtil.createFileForFreeStyle("three/days/symbols.zip", "I am the symbols file"));

        final AppCenterRecorder appCenterRecorder = new AppCenterRecorder("at-this-moment-you-should-be-with-us", "janes-addiction", "ritual-de-lo-habitual", "three/days/xiola.ipa", "three/days/symbols.zip", "casey, niccoli");
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
}