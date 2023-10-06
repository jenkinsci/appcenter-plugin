package io.jenkins.plugins.appcenter.task.internal;

import hudson.ProxyConfiguration;
import hudson.model.TaskListener;
import hudson.util.Secret;
import io.jenkins.plugins.appcenter.AppCenterException;
import io.jenkins.plugins.appcenter.api.AppCenterServiceFactory;
import io.jenkins.plugins.appcenter.task.request.UploadRequest;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.function.ThrowingRunnable;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.PrintStream;
import java.util.concurrent.ExecutionException;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class PollForReleaseTaskTest {

    @Rule
    public MockWebServer mockWebServer = new MockWebServer();

    @Mock
    TaskListener mockTaskListener;

    @Mock
    PrintStream mockLogger;

    @Mock
    ProxyConfiguration mockProxyConfig;

    private UploadRequest baseRequest;

    private PollForReleaseTask task;

    @Before
    public void setUp() {
        baseRequest = new UploadRequest.Builder()
            .setOwnerName("owner-name")
            .setAppName("app-name")
            .build();
        given(mockTaskListener.getLogger()).willReturn(mockLogger);
        final AppCenterServiceFactory factory = new AppCenterServiceFactory(Secret.fromString("secret-token"), mockWebServer.url("/").toString(), mockProxyConfig);
        task = new PollForReleaseTask(mockTaskListener, factory);
    }

    @Test
    public void should_RetryPolling_When_StatusIsStartedOrFinished() throws Exception {
        // Given
        final UploadRequest uploadRequest = baseRequest.newBuilder()
            .setUploadId("upload_id")
            .build();
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody("{\n" +
            " \"upload_status\": \"uploadStarted\" \n" +
            "}"));
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody("{\n" +
            " \"upload_status\": \"uploadFinished\" \n" +
            "}"));
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody("{\n" +
            " \"upload_status\": \"readyToBePublished\",\n" +
            " \"release_distinct_id\": 1234\n" +
            "}"));

        // When
        task.execute(uploadRequest).get();

        // Then
        assertThat(mockWebServer.getRequestCount())
            .isEqualTo(3);
    }

    @Test
    public void should_ReturnResponse_When_RequestIsSuccessful() throws Exception {
        // Given
        final UploadRequest uploadRequest = baseRequest.newBuilder()
            .setUploadId("upload_id")
            .build();
        final UploadRequest expected = uploadRequest.newBuilder().setReleaseId(1234).build();
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody("{\n" +
            " \"upload_status\": \"readyToBePublished\",\n" +
            " \"release_distinct_id\": 1234\n" +
            "}"));

        // When
        final UploadRequest actual = task.execute(uploadRequest).get();

        // Then
        assertThat(actual)
            .isEqualTo(expected);
    }

    @Test
    public void should_ReturnException_When_StatusIsMalware() {
        // Given
        final UploadRequest uploadRequest = baseRequest.newBuilder()
            .setUploadId("upload_id")
            .build();
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody("{\n" +
            " \"upload_status\": \"malwareDetected\",\n" +
            " \"error_details\": \"we found this does it belong to you?\"\n" +
            "}"));

        // When
        final ThrowingRunnable throwingRunnable = () -> task.execute(uploadRequest).get();

        // Then
        final ExecutionException exception = assertThrows(ExecutionException.class, throwingRunnable);
        assertThat(exception).hasCauseThat().isInstanceOf(AppCenterException.class);
        assertThat(exception).hasCauseThat().hasMessageThat().contains("Polling for app release successful however was rejected by server: we found this does it belong to you?");
    }

    @Test
    public void should_ReturnException_When_StatusIsError() {
        // Given
        final UploadRequest uploadRequest = baseRequest.newBuilder()
            .setUploadId("upload_id")
            .build();
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody("{\n" +
            " \"upload_status\": \"error\",\n" +
            " \"error_details\": \"error error error\"\n" +
            "}"));

        // When
        final ThrowingRunnable throwingRunnable = () -> task.execute(uploadRequest).get();

        // Then
        final ExecutionException exception = assertThrows(ExecutionException.class, throwingRunnable);
        assertThat(exception).hasCauseThat().isInstanceOf(AppCenterException.class);
        assertThat(exception).hasCauseThat().hasMessageThat().contains("Polling for app release successful however was rejected by server: error error error");
    }

    @Test
    public void should_ReturnException_When_StatusIsUnknown() {
        // Given
        final UploadRequest uploadRequest = baseRequest.newBuilder()
            .setUploadId("upload_id")
            .build();
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody("{\n" +
            " \"upload_status\": \"huggobilly\",\n" +
            " \"error_details\": \"hubally goobally hobbilly goobilly\"\n" +
            "}"));

        // When
        final ThrowingRunnable throwingRunnable = () -> task.execute(uploadRequest).get();

        // Then
        final ExecutionException exception = assertThrows(ExecutionException.class, throwingRunnable);
        assertThat(exception).hasCauseThat().isInstanceOf(AppCenterException.class);
        assertThat(exception).hasCauseThat().hasMessageThat().contains("Polling for app release unsuccessful");
    }

    @Test
    public void should_ReturnException_When_RequestIsUnSuccessful() {
        // Given
        final UploadRequest uploadRequest = baseRequest.newBuilder()
            .setUploadId("upload_id")
            .build();
        mockWebServer.enqueue(new MockResponse().setResponseCode(400));

        // When
        final ThrowingRunnable throwingRunnable = () -> task.execute(uploadRequest).get();

        // Then
        final ExecutionException exception = assertThrows(ExecutionException.class, throwingRunnable);
        assertThat(exception).hasCauseThat().isInstanceOf(AppCenterException.class);
        assertThat(exception).hasCauseThat().hasMessageThat().contains("Polling for app release unsuccessful");
    }
}