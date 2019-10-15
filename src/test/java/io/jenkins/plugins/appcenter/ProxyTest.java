package io.jenkins.plugins.appcenter;

import com.google.common.net.HttpHeaders;
import hudson.ProxyConfiguration;
import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import hudson.model.Result;
import io.jenkins.plugins.appcenter.util.MockWebServerUtil;
import io.jenkins.plugins.appcenter.util.TestUtil;
import okhttp3.Credentials;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import java.io.IOException;

import static com.google.common.truth.Truth.assertThat;

public class ProxyTest {

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
        freeStyleProject.getBuildersList().add(TestUtil.createFileForFreeStyle("three/days/xiola.ipa"));
        freeStyleProject.getBuildersList().add(TestUtil.createFileForFreeStyle("three/days/symbols.dsym"));

        final AppCenterRecorder appCenterRecorder = new AppCenterRecorder("at-this-moment-you-should-be-with-us", "janes-addiction", "ritual-de-lo-habitual", "three/days/xiola.ipa", "three/days/symbols.dsym", "casey, niccoli");
        appCenterRecorder.setBaseUrl(mockWebServer.url("/").toString()); // Notice this is *not* set to the proxy address
        freeStyleProject.getPublishersList().add(appCenterRecorder);
    }

    @Test
    public void should_SendRequestsDirectly_When_NoProxyConfigurationFound() throws Exception {
        // Given
        jenkinsRule.jenkins.proxy = null;
        MockWebServerUtil.enqueueSuccess(mockWebServer);

        // When
        final FreeStyleBuild freeStyleBuild = freeStyleProject.scheduleBuild2(0).get();

        // Then
        jenkinsRule.assertBuildStatus(Result.SUCCESS, freeStyleBuild);
        assertThat(proxyWebServer.getRequestCount()).isEqualTo(0);
        assertThat(mockWebServer.getRequestCount()).isEqualTo(7);
    }

    @Test
    public void should_SendRequestsToProxy_When_ProxyConfigurationFound() throws Exception {
        // Given
        jenkinsRule.jenkins.proxy = new ProxyConfiguration(proxyWebServer.getHostName(), proxyWebServer.getPort());
        MockWebServerUtil.enqueueSuccess(proxyWebServer);

        // When
        final FreeStyleBuild freeStyleBuild = freeStyleProject.scheduleBuild2(0).get();

        // Then
        jenkinsRule.assertBuildStatus(Result.SUCCESS, freeStyleBuild);
        assertThat(proxyWebServer.getRequestCount()).isEqualTo(7);
        assertThat(mockWebServer.getRequestCount()).isEqualTo(0);
    }


    @Test
    public void should_SendProxyAuthorizationHeader_When_ProxyCredentialsConfigured() throws Exception {
        // Given
        final String userName = "user";
        final String password = "password";
        jenkinsRule.jenkins.proxy = new ProxyConfiguration(proxyWebServer.getHostName(), proxyWebServer.getPort(), userName, password);

        MockWebServerUtil.enqueueProxyAuthRequired(proxyWebServer); // first request rejected and proxy authentication requested
        MockWebServerUtil.enqueueSuccess(proxyWebServer);

        // When
        final FreeStyleBuild freeStyleBuild = freeStyleProject.scheduleBuild2(0).get();

        // Then
        jenkinsRule.assertBuildStatus(Result.SUCCESS, freeStyleBuild);
        assertThat(proxyWebServer.getRequestCount()).isEqualTo(8);
        assertThat(mockWebServer.getRequestCount()).isEqualTo(0);
        // proxy auth is performed on second request
        assertThat(proxyWebServer.takeRequest().getHeader(HttpHeaders.PROXY_AUTHORIZATION)).isNull();
        assertThat(proxyWebServer.takeRequest().getHeader(HttpHeaders.PROXY_AUTHORIZATION)).isEqualTo(Credentials.basic(userName, password));
    }

    @Test
    public void should_SendAllRequestsDirectly_When_NoProxyHostConfigured() throws Exception {
        // Given
        final String noProxyHost = mockWebServer.url("/").url().getHost();
        jenkinsRule.jenkins.proxy = new ProxyConfiguration(proxyWebServer.getHostName(), proxyWebServer.getPort(), null, null, noProxyHost);

        MockWebServerUtil.enqueueSuccess(mockWebServer);

        // When
        final FreeStyleBuild freeStyleBuild = freeStyleProject.scheduleBuild2(0).get();

        // Then
        jenkinsRule.assertBuildStatus(Result.SUCCESS, freeStyleBuild);
        assertThat(proxyWebServer.getRequestCount()).isEqualTo(0);
        assertThat(mockWebServer.getRequestCount()).isEqualTo(7);
    }

    @Test
    public void should_SendUploadRequestsDirectly_When_NoProxyHostConfiguredForAppCenterAPI() throws Exception {
        // Given
        final String noProxyHost = mockWebServer.url("/").url().getHost();
        jenkinsRule.jenkins.proxy = new ProxyConfiguration(proxyWebServer.getHostName(), proxyWebServer.getPort(), null, null, noProxyHost);

        MockWebServerUtil.enqueueUploadViaProxy(mockWebServer, proxyWebServer);

        // When
        final FreeStyleBuild freeStyleBuild = freeStyleProject.scheduleBuild2(0).get();

        // Then
        jenkinsRule.assertBuildStatus(Result.SUCCESS, freeStyleBuild);
        assertThat(proxyWebServer.getRequestCount()).isEqualTo(2);
        assertThat(mockWebServer.getRequestCount()).isEqualTo(5);
    }
}