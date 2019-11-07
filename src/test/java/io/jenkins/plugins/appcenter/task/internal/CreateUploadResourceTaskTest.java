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
import retrofit2.HttpException;

import java.io.PrintStream;
import java.util.concurrent.ExecutionException;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class CreateUploadResourceTaskTest {

    @Rule
    public MockWebServer mockWebServer = new MockWebServer();

    @Mock
    TaskListener mockTaskListener;

    @Mock
    PrintStream mockLogger;

    @Mock
    ProxyConfiguration mockProxyConfig;

    private UploadRequest baseRequest;

    private CreateUploadResourceTask task;

    @Before
    public void setUp() {
        baseRequest = new UploadRequest.Builder()
            .setOwnerName("owner-name")
            .setAppName("app-name")
            .build();
        given(mockTaskListener.getLogger()).willReturn(mockLogger);
        final AppCenterServiceFactory factory = new AppCenterServiceFactory(Secret.fromString("secret-token"), mockWebServer.url("/").toString(), mockProxyConfig);
        task = new CreateUploadResourceTask(mockTaskListener, factory);
    }

    @Test
    public void should_ReturnResponse_When_RequestIsSuccessful() throws Exception {
        // Given
        final UploadRequest expected = baseRequest.newBuilder().setUploadId("string").setUploadUrl("string").build();
        mockWebServer.enqueue(new MockResponse().setResponseCode(201).setBody("{\n" +
            "  \"upload_id\": \"string\",\n" +
            "  \"upload_url\": \"string\",\n" +
            "  \"asset_id\": \"string\",\n" +
            "  \"asset_domain\": \"string\",\n" +
            "  \"asset_token\": \"string\"\n" +
            "}"));

        // When
        final UploadRequest actual = task.execute(baseRequest).get();

        // Then
        assertThat(actual)
            .isEqualTo(expected);
    }

    @Test
    public void should_ReturnResponse_When_RequestIsSuccessful_NonAsciiCharactersInFileName() throws Exception {
        // Given
        final UploadRequest request = baseRequest.newBuilder().setAppName("åþþ ñåmë").build();
        final UploadRequest expected = request.newBuilder().setUploadId("string").setUploadUrl("string").build();
        mockWebServer.enqueue(new MockResponse().setResponseCode(201).setBody("{\n" +
            "  \"upload_id\": \"string\",\n" +
            "  \"upload_url\": \"string\",\n" +
            "  \"asset_id\": \"string\",\n" +
            "  \"asset_domain\": \"string\",\n" +
            "  \"asset_token\": \"string\"\n" +
            "}"));

        // When
        final UploadRequest actual = task.execute(request).get();

        // Then
        assertThat(actual)
            .isEqualTo(expected);
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
        assertThat(exception).hasCauseThat().hasMessageThat().isEqualTo("Create upload resource for app unsuccessful: ");
        assertThat(exception).hasCauseThat().hasCauseThat().isInstanceOf(HttpException.class);
        assertThat(exception).hasCauseThat().hasCauseThat().hasMessageThat().isEqualTo("HTTP 500 Server Error");
    }
}