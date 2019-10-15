package io.jenkins.plugins.appcenter.task.internal;

import hudson.FilePath;
import hudson.ProxyConfiguration;
import hudson.model.TaskListener;
import hudson.util.Secret;
import io.jenkins.plugins.appcenter.AppCenterException;
import io.jenkins.plugins.appcenter.api.AppCenterServiceFactory;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class UploadToResourceTaskTest {

    @Rule
    public MockWebServer mockWebServer = new MockWebServer();

    @Mock
    FilePath mockFilePath;

    @Mock
    TaskListener mockTaskListener;

    @Mock
    PrintStream mockLogger;

    @Mock
    ProxyConfiguration mockProxyConfig;

    private UploadToResourceTask task;

    @Before
    public void setUp() {
        given(mockTaskListener.getLogger()).willReturn(mockLogger);
        given(mockFilePath.child(anyString())).willReturn(mockFilePath);
        given(mockFilePath.getRemote()).willReturn("src/test/resources/three/days/xiola.ipa"); // Note: We cannot create a file in the workspace in this test so need to point to an actual file
        final AppCenterServiceFactory factory = new AppCenterServiceFactory(Secret.fromString("secret-token"), mockWebServer.url("/").toString(), mockProxyConfig);
        task = new UploadReleaseToResourceTask(mockTaskListener, mockFilePath, factory);
    }


    @Test
    public void should_ReturnUploadId_When_RequestIsSuccess() throws Exception {
        // Given
        final UploadToResourceTask.Request request = new UploadToResourceTask.Request(mockWebServer.url("upload").toString(), "upload-id", "three/days/xiola.ipa");
        mockWebServer.enqueue(new MockResponse().setResponseCode(200));

        // When
        final String result = task.execute(request).get();

        // Then
        assertThat(result)
            .isEqualTo("upload-id");
    }

    @Test
    public void should_ReturnException_When_RequestIsUnSuccessful() {
        // Given
        final UploadToResourceTask.Request request = new UploadToResourceTask.Request(mockWebServer.url("upload").toString(), "upload-id", "three/days/xiola.ipa");
        mockWebServer.enqueue(new MockResponse().setResponseCode(500));

        // When
        final ThrowingRunnable throwingRunnable = () -> task.execute(request).get();

        // Then
        final ExecutionException exception = assertThrows(ExecutionException.class, throwingRunnable);
        assertThat(exception).hasCauseThat().isInstanceOf(AppCenterException.class);
        assertThat(exception).hasCauseThat().hasMessageThat().isEqualTo("Uploading file unsuccessful: ");
        assertThat(exception).hasCauseThat().hasCauseThat().isInstanceOf(HttpException.class);
        assertThat(exception).hasCauseThat().hasCauseThat().hasMessageThat().isEqualTo("HTTP 500 Server Error");
    }

    @Test
    public void should_SendRequestToUploadUrl() throws Exception {
        // Given
        final UploadToResourceTask.Request request = new UploadToResourceTask.Request(mockWebServer.url("upload").toString(), "upload-id", "three/days/xiola.ipa");
        mockWebServer.enqueue(new MockResponse().setResponseCode(200));
        task.execute(request).get();

        // When
        final RecordedRequest recordedRequest = mockWebServer.takeRequest();

        // Then
        assertThat(recordedRequest.getPath())
            .isEqualTo("/upload");
    }

    @Test
    public void should_SendRequestAsPost() throws Exception {
        // Given
        final UploadToResourceTask.Request request = new UploadToResourceTask.Request(mockWebServer.url("upload").toString(), "upload-id", "three/days/xiola.ipa");
        mockWebServer.enqueue(new MockResponse().setResponseCode(200));
        task.execute(request).get();

        // When
        final RecordedRequest recordedRequest = mockWebServer.takeRequest();

        // Then
        assertThat(recordedRequest.getMethod())
            .isEqualTo("POST");
    }
}