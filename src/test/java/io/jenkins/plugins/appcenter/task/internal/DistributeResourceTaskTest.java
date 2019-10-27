package io.jenkins.plugins.appcenter.task.internal;

import hudson.ProxyConfiguration;
import hudson.model.TaskListener;
import hudson.util.Secret;
import io.jenkins.plugins.appcenter.AppCenterException;
import io.jenkins.plugins.appcenter.api.AppCenterServiceFactory;
import io.jenkins.plugins.appcenter.task.request.UploadRequest;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.function.ThrowingRunnable;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import retrofit2.HttpException;

import java.io.PrintStream;
import java.util.concurrent.ExecutionException;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class DistributeResourceTaskTest {

    @Rule
    public MockWebServer mockWebServer = new MockWebServer();

    @Mock
    TaskListener mockTaskListener;

    @Mock
    PrintStream mockLogger;

    @Mock
    ProxyConfiguration mockProxyConfig;

    private UploadRequest baseRequest;

    private DistributeResourceTask task;

    @Before
    public void setUp() {
        baseRequest = new UploadRequest.Builder()
            .setOwnerName("owner-name")
            .setAppName("app-name")
            .setDestinationGroups("group1, group2")
            .setReleaseNotes("release-notes")
            .setNotifyTesters(true)
            .setReleaseId(0)
            .build();
        given(mockTaskListener.getLogger()).willReturn(mockLogger);
        final AppCenterServiceFactory factory = new AppCenterServiceFactory(Secret.fromString("secret-token"), mockWebServer.url("/").toString(), mockProxyConfig);
        task = new DistributeResourceTask(mockTaskListener, factory);
    }

    @Test
    public void should_ReturnResponse_When_RequestIsSuccessful() throws Exception {
        // Given
        final UploadRequest expected = baseRequest;
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody("{\n" +
            "  \"release_notes\": \"string\"\n" +
            "}"));

        // When
        final UploadRequest actual = task.execute(baseRequest).get();

        // Then
        assertThat(actual)
            .isEqualTo(expected);
    }

    @Test
    public void should_SetReleaseNotes_When_RequestIsSuccessful() throws Exception {
        // Given
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody("{\n" +
            "  \"release_notes\": \"string\"\n" +
            "}"));

        final UploadRequest actual = task.execute(baseRequest).get();

        // When
        final RecordedRequest recordedRequest = mockWebServer.takeRequest();

        // Then
        assertThat(recordedRequest.getBody().readUtf8())
            .contains("\"release_notes\":\"release-notes\"");
    }

    @Test
    public void should_NotifyTesters_When_Configured() throws Exception {
        // Given
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody("{\n" +
            "  \"release_notes\": \"string\"\n" +
            "}"));

        final UploadRequest actual = task.execute(baseRequest).get();

        // When
        final RecordedRequest recordedRequest = mockWebServer.takeRequest();

        // Then
        assertThat(recordedRequest.getBody().readUtf8())
            .contains("\"notify_testers\":true");
    }

    @Test
    public void should_NotNotifyTesters_When_NotConfigured() throws Exception {
        // Given
        final UploadRequest request = baseRequest.newBuilder().setNotifyTesters(false).build();
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody("{\n" +
            "  \"release_notes\": \"string\"\n" +
            "}"));

        final UploadRequest actual = task.execute(request).get();

        // When
        final RecordedRequest recordedRequest = mockWebServer.takeRequest();

        // Then
        assertThat(recordedRequest.getBody().readUtf8())
            .contains("\"notify_testers\":false");
    }

    @Test
    public void should_ReturnException_When_RequestIsUnSuccessful() {
        // Given
        mockWebServer.enqueue(new MockResponse().setResponseCode(500));

        // When
        final ThrowingRunnable throwingRunnable = () -> task.execute(baseRequest).get();

        // Then
        final ExecutionException exception = assertThrows(ExecutionException.class, throwingRunnable);
        assertThat(exception).hasCauseThat().isInstanceOf(AppCenterException.class);
        assertThat(exception).hasCauseThat().hasMessageThat().isEqualTo("Distributing resource unsuccessful: ");
        assertThat(exception).hasCauseThat().hasCauseThat().isInstanceOf(HttpException.class);
        assertThat(exception).hasCauseThat().hasCauseThat().hasMessageThat().isEqualTo("HTTP 500 Server Error");
    }
}