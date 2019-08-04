package io.jenkins.plugins.appcenter;

import com.google.common.net.HttpHeaders;
import hudson.Launcher;
import hudson.ProxyConfiguration;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import hudson.model.Result;
import io.jenkins.plugins.appcenter.api.AppCenterServiceFactory;
import okhttp3.Credentials;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.BuildWatcher;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.TestBuilder;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;

import static com.google.common.truth.Truth.assertThat;

public class ProxyTest {

    @ClassRule
    public static BuildWatcher buildWatcher = new BuildWatcher();

    @ClassRule
    public static JenkinsRule jenkinsRule = new JenkinsRule();

    @Rule
    public MockWebServer mockWebServer = new MockWebServer();

    @Rule
    public MockWebServer proxyWebServer = new MockWebServer();

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
    public void should_SendRequestsDirectly_When_NoProxyConfigurationFound() throws Exception {
        // Given
        jenkinsRule.jenkins.proxy = null;

        final AppCenterRecorder appCenterRecorder = new AppCenterRecorder("token", "owner_name", "app_name", "Collaborators", "path/to/app.apk");
        appCenterRecorder.setBaseUrl(mockWebServer.url("/").url()); // Notice this is *not* set to the proxy address

        enqueueRequests(mockWebServer);
        freeStyleProject.getPublishersList().add(appCenterRecorder);

        // When
        final FreeStyleBuild freeStyleBuild = freeStyleProject.scheduleBuild2(0).get();

        // Then
        jenkinsRule.assertBuildStatus(Result.SUCCESS, freeStyleBuild);
        assertThat(proxyWebServer.getRequestCount()).isEqualTo(0);
        assertThat(mockWebServer.getRequestCount()).isEqualTo(4);
    }

    @Test
    public void should_SendRequestsToProxy_When_ProxyConfigurationFound() throws Exception {
        // Given
        jenkinsRule.jenkins.proxy = new ProxyConfiguration(proxyWebServer.getHostName(), proxyWebServer.getPort());

        final AppCenterRecorder appCenterRecorder = new AppCenterRecorder("token", "owner_name", "app_name", "Collaborators", "path/to/app.apk");
        appCenterRecorder.setBaseUrl(mockWebServer.url("/").url()); // Notice this is *not* set to the proxy address

        enqueueRequests(proxyWebServer);
        freeStyleProject.getPublishersList().add(appCenterRecorder);

        // When
        final FreeStyleBuild freeStyleBuild = freeStyleProject.scheduleBuild2(0).get();

        // Then
        jenkinsRule.assertBuildStatus(Result.SUCCESS, freeStyleBuild);
        assertThat(proxyWebServer.getRequestCount()).isEqualTo(4);
        assertThat(mockWebServer.getRequestCount()).isEqualTo(0);
    }


    @Test
    public void should_SendProxyAuthorizationHeader_When_ProxyCredentialsConfigured() throws Exception {
        // Given
        final String userName = "user";
        final String password = "password";

        jenkinsRule.jenkins.proxy = new ProxyConfiguration(proxyWebServer.getHostName(), proxyWebServer.getPort(), userName, password);

        final AppCenterRecorder appCenterRecorder = new AppCenterRecorder("token", "owner_name", "app_name", "Collaborators", "path/to/app.apk");
        appCenterRecorder.setBaseUrl(mockWebServer.url("/").url()); // Notice this is *not* set to the proxy address

        // first request rejected and proxy authentication requested
        proxyWebServer.enqueue(new MockResponse().setResponseCode(HttpURLConnection.HTTP_PROXY_AUTH));
        enqueueRequests(proxyWebServer);
        freeStyleProject.getPublishersList().add(appCenterRecorder);

        // When
        final FreeStyleBuild freeStyleBuild = freeStyleProject.scheduleBuild2(0).get();

        // Then
        jenkinsRule.assertBuildStatus(Result.SUCCESS, freeStyleBuild);
        assertThat(proxyWebServer.getRequestCount()).isEqualTo(5);
        assertThat(mockWebServer.getRequestCount()).isEqualTo(0);
        // proxy auth is performed on second request
        assertThat(proxyWebServer.takeRequest().getHeader(HttpHeaders.PROXY_AUTHORIZATION)).isNull();
        assertThat(proxyWebServer.takeRequest().getHeader(HttpHeaders.PROXY_AUTHORIZATION)).isEqualTo(Credentials.basic(userName, password));
    }

    @Test
    public void should_SendAllRequestsDirectly_When_NoProxyHostConfigured() throws Exception {
        // Given
        final String noProxyHost = String.join(",", new URL(AppCenterServiceFactory.APPCENTER_BASE_URL).getHost(), mockWebServer.url("/").url().getHost());

        jenkinsRule.jenkins.proxy = new ProxyConfiguration(proxyWebServer.getHostName(), proxyWebServer.getPort(), null, null, noProxyHost);

        final AppCenterRecorder appCenterRecorder = new AppCenterRecorder("token", "owner_name", "app_name", "Collaborators", "path/to/app.apk");
        appCenterRecorder.setBaseUrl(mockWebServer.url("/").url()); // Notice this is *not* set to the proxy address

        enqueueRequests(mockWebServer);
        freeStyleProject.getPublishersList().add(appCenterRecorder);

        // When
        final FreeStyleBuild freeStyleBuild = freeStyleProject.scheduleBuild2(0).get();

        // Then
        jenkinsRule.assertBuildStatus(Result.SUCCESS, freeStyleBuild);
        assertThat(proxyWebServer.getRequestCount()).isEqualTo(0);
        assertThat(mockWebServer.getRequestCount()).isEqualTo(4);
    }

    @Test
    public void should_SendUploadRequestsDirectly_When_NoProxyHostConfiguredForAppCenterAPI() throws Exception {
        // Given
        final String noProxyHost = new URL(AppCenterServiceFactory.APPCENTER_BASE_URL).getHost();

        jenkinsRule.jenkins.proxy = new ProxyConfiguration(proxyWebServer.getHostName(), proxyWebServer.getPort(), null, null, noProxyHost);

        final AppCenterRecorder appCenterRecorder = new AppCenterRecorder("token", "owner_name", "app_name", "Collaborators", "path/to/app.apk");
        appCenterRecorder.setBaseUrl(mockWebServer.url("/").url()); // Notice this is *not* set to the proxy address

        enqueueRequests(mockWebServer, proxyWebServer);
        freeStyleProject.getPublishersList().add(appCenterRecorder);

        // When
        final FreeStyleBuild freeStyleBuild = freeStyleProject.scheduleBuild2(0).get();

        // Then
        jenkinsRule.assertBuildStatus(Result.SUCCESS, freeStyleBuild);
        assertThat(proxyWebServer.getRequestCount()).isEqualTo(1);
        assertThat(mockWebServer.getRequestCount()).isEqualTo(3);
    }

    private void enqueueRequests(MockWebServer mockWebServer) {
        enqueueRequests(mockWebServer, mockWebServer);
    }

    private void enqueueRequests(MockWebServer mockWebServer, MockWebServer proxyWebServer) {
        mockWebServer.enqueue(new MockResponse().setResponseCode(HttpURLConnection.HTTP_CREATED).setBody("{\n" +
            "  \"upload_id\": \"string\",\n" +
            "  \"upload_url\": \"" + proxyWebServer.url("/").toString() + "\",\n" +
            "  \"asset_id\": \"string\",\n" +
            "  \"asset_domain\": \"string\",\n" +
            "  \"asset_token\": \"string\"\n" +
            "}"));
        proxyWebServer.enqueue(new MockResponse().setResponseCode(HttpURLConnection.HTTP_OK));
        mockWebServer.enqueue(new MockResponse().setResponseCode(HttpURLConnection.HTTP_OK).setBody("{\n" +
            "  \"release_id\": 0,\n" +
            "  \"release_url\": \"string\"\n" +
            "}"));
        mockWebServer.enqueue(new MockResponse().setResponseCode(HttpURLConnection.HTTP_OK).setBody("{\n" +
            "  \"release_notes\": \"string\"\n" +
            "}"));
    }
}