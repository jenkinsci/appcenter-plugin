package io.jenkins.plugins.appcenter;

import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import hudson.model.Result;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.BuildWatcher;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.TestBuilder;

import java.io.IOException;
import java.util.Objects;

import static com.google.common.truth.Truth.assertThat;

public class FreestyleTest {

    @ClassRule
    public static BuildWatcher buildWatcher = new BuildWatcher();

    @ClassRule
    public static JenkinsRule jenkinsRule = new JenkinsRule();

    @Rule
    public MockWebServer mockWebServer = new MockWebServer();

    private FreeStyleProject freeStyleProject;

    @Before
    public void setUp() throws IOException {
        freeStyleProject = jenkinsRule.createFreeStyleProject();
        freeStyleProject.getBuildersList().add(new TestBuilder() {
            public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener)
                    throws InterruptedException, IOException {
                Objects.requireNonNull(build.getWorkspace()).child("path/to/app.apk").write("little tiny robots", "UTF-8");
                return true;
            }
        });
    }

    @Test
    public void should_SetBuildResultFailure_When_ReleaseUploadBegin_Returns_500() throws Exception {
        // Given
        mockWebServer.enqueue(new MockResponse().setResponseCode(500));

        final AppCenterRecorder appCenterRecorder = new AppCenterRecorder("token", "owner_name", "app_name", "Collaborators", "path/to/app.apk");
        appCenterRecorder.setBaseUrl(mockWebServer.url("/").url());
        freeStyleProject.getPublishersList().add(appCenterRecorder);

        // When
        final FreeStyleBuild freeStyleBuild = freeStyleProject.scheduleBuild2(0).get();

        // Then
        jenkinsRule.assertBuildStatus(Result.FAILURE, freeStyleBuild);
    }

    @Test
    public void should_SetBuildResultFailure_When_ReleaseUploadBegin_Returns_400() throws Exception {
        // Given
        mockWebServer.enqueue(new MockResponse().setResponseCode(400));

        final AppCenterRecorder appCenterRecorder = new AppCenterRecorder("token", "owner_name", "app_name", "Collaborators", "path/to/app.apk");
        appCenterRecorder.setBaseUrl(mockWebServer.url("/").url());
        freeStyleProject.getPublishersList().add(appCenterRecorder);

        // When
        final FreeStyleBuild freeStyleBuild = freeStyleProject.scheduleBuild2(0).get();

        // Then
        jenkinsRule.assertBuildStatus(Result.FAILURE, freeStyleBuild);
    }

    @Test
    public void should_SetBuildResultFailure_When_UploadApp_Returns_500() throws Exception {
        // Given
        mockWebServer.enqueue(new MockResponse().setResponseCode(201).setBody("{\n" +
                "  \"upload_id\": \"string\",\n" +
                "  \"upload_url\": \"" + mockWebServer.url("/").toString() + "\",\n" +
                "  \"asset_id\": \"string\",\n" +
                "  \"asset_domain\": \"string\",\n" +
                "  \"asset_token\": \"string\"\n" +
                "}"));
        mockWebServer.enqueue(new MockResponse().setResponseCode(500));

        final AppCenterRecorder appCenterRecorder = new AppCenterRecorder("token", "owner_name", "app_name", "Collaborators", "path/to/app.apk");
        appCenterRecorder.setBaseUrl(mockWebServer.url("/").url());
        freeStyleProject.getPublishersList().add(appCenterRecorder);

        // When
        final FreeStyleBuild freeStyleBuild = freeStyleProject.scheduleBuild2(0).get();

        // Then
        jenkinsRule.assertBuildStatus(Result.FAILURE, freeStyleBuild);
        final RecordedRequest releaseBeginRequest = mockWebServer.takeRequest();
        assertThat(releaseBeginRequest.getMethod()).isEqualTo("POST");
        final RecordedRequest uploadRequest = mockWebServer.takeRequest();
        assertThat(uploadRequest.getMethod()).isEqualTo("POST");
    }

    @Test
    public void should_SetBuildResultFailure_When_ReleaseUploadEnd_Returns_400() throws Exception {
        // Given
        mockWebServer.enqueue(new MockResponse().setResponseCode(201).setBody("{\n" +
                "  \"upload_id\": \"string\",\n" +
                "  \"upload_url\": \"" + mockWebServer.url("/").toString() + "\",\n" +
                "  \"asset_id\": \"string\",\n" +
                "  \"asset_domain\": \"string\",\n" +
                "  \"asset_token\": \"string\"\n" +
                "}"));
        mockWebServer.enqueue(new MockResponse().setResponseCode(200));
        mockWebServer.enqueue(new MockResponse().setResponseCode(400));

        final AppCenterRecorder appCenterRecorder = new AppCenterRecorder("token", "owner_name", "app_name", "Collaborators", "path/to/app.apk");
        appCenterRecorder.setBaseUrl(mockWebServer.url("/").url());
        freeStyleProject.getPublishersList().add(appCenterRecorder);

        // When
        final FreeStyleBuild freeStyleBuild = freeStyleProject.scheduleBuild2(0).get();

        // Then
        jenkinsRule.assertBuildStatus(Result.FAILURE, freeStyleBuild);
        final RecordedRequest releaseBeginRequest = mockWebServer.takeRequest();
        assertThat(releaseBeginRequest.getMethod()).isEqualTo("POST");
        final RecordedRequest uploadRequest = mockWebServer.takeRequest();
        assertThat(uploadRequest.getMethod()).isEqualTo("POST");
        final RecordedRequest releaseEndRequest = mockWebServer.takeRequest();
        assertThat(releaseEndRequest.getMethod()).isEqualTo("PATCH");
    }

    @Test
    public void should_SetBuildResultFailure_When_ReleaseDetailsUpdate_Returns_400() throws Exception {
        // Given
        mockWebServer.enqueue(new MockResponse().setResponseCode(201).setBody("{\n" +
                "  \"upload_id\": \"string\",\n" +
                "  \"upload_url\": \"" + mockWebServer.url("/").toString() + "\",\n" +
                "  \"asset_id\": \"string\",\n" +
                "  \"asset_domain\": \"string\",\n" +
                "  \"asset_token\": \"string\"\n" +
                "}"));
        mockWebServer.enqueue(new MockResponse().setResponseCode(200));
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody("{\n" +
                "  \"release_id\": 0,\n" +
                "  \"release_url\": \"string\"\n" +
                "}"));
        mockWebServer.enqueue(new MockResponse().setResponseCode(400));

        final AppCenterRecorder appCenterRecorder = new AppCenterRecorder("token", "owner_name", "app_name", "Collaborators", "path/to/app.apk");
        appCenterRecorder.setBaseUrl(mockWebServer.url("/").url());
        freeStyleProject.getPublishersList().add(appCenterRecorder);

        // When
        final FreeStyleBuild freeStyleBuild = freeStyleProject.scheduleBuild2(0).get();

        // Then
        jenkinsRule.assertBuildStatus(Result.FAILURE, freeStyleBuild);
        final RecordedRequest releaseBeginRequest = mockWebServer.takeRequest();
        assertThat(releaseBeginRequest.getMethod()).isEqualTo("POST");
        final RecordedRequest uploadRequest = mockWebServer.takeRequest();
        assertThat(uploadRequest.getMethod()).isEqualTo("POST");
        final RecordedRequest releaseEndRequest = mockWebServer.takeRequest();
        assertThat(releaseEndRequest.getMethod()).isEqualTo("PATCH");
        final RecordedRequest releaseDetailsRequest = mockWebServer.takeRequest();
        assertThat(releaseDetailsRequest.getMethod()).isEqualTo("PATCH");
    }

    @Test
    public void should_SetBuildResultSuccess_When_AppCenterAcceptsAllRequests() throws Exception {
        // Given
        mockWebServer.enqueue(new MockResponse().setResponseCode(201).setBody("{\n" +
                "  \"upload_id\": \"string\",\n" +
                "  \"upload_url\": \"" + mockWebServer.url("/").toString() + "\",\n" +
                "  \"asset_id\": \"string\",\n" +
                "  \"asset_domain\": \"string\",\n" +
                "  \"asset_token\": \"string\"\n" +
                "}"));
        mockWebServer.enqueue(new MockResponse().setResponseCode(200));
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody("{\n" +
                "  \"release_id\": 0,\n" +
                "  \"release_url\": \"string\"\n" +
                "}"));
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody("{\n" +
                "  \"release_notes\": \"string\"\n" +
                "}"));

        final AppCenterRecorder appCenterRecorder = new AppCenterRecorder("token", "owner_name", "app_name", "Collaborators", "path/to/app.apk");
        appCenterRecorder.setBaseUrl(mockWebServer.url("/").url());
        freeStyleProject.getPublishersList().add(appCenterRecorder);

        // When
        final FreeStyleBuild freeStyleBuild = freeStyleProject.scheduleBuild2(0).get();

        // Then
        jenkinsRule.assertBuildStatus(Result.SUCCESS, freeStyleBuild);
    }
}
