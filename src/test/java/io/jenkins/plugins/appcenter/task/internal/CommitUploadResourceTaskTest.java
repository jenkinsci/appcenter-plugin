package io.jenkins.plugins.appcenter.task.internal;

import hudson.ProxyConfiguration;
import hudson.model.TaskListener;
import hudson.util.Secret;
import io.jenkins.plugins.appcenter.AppCenterException;
import io.jenkins.plugins.appcenter.api.AppCenterServiceFactory;
import io.jenkins.plugins.appcenter.model.appcenter.SymbolUploadBeginRequest;
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
public class CommitUploadResourceTaskTest {

    @Rule
    public MockWebServer mockWebServer = new MockWebServer();

    @Mock
    TaskListener mockTaskListener;

    @Mock
    PrintStream mockLogger;

    @Mock
    ProxyConfiguration mockProxyConfig;

    private UploadRequest baseRequest;

    private CommitUploadResourceTask task;

    @Before
    public void setUp() {
        baseRequest = new UploadRequest.Builder()
            .setOwnerName("owner-name")
            .setAppName("app-name")
            .setUploadId("upload-id")
            .build();
        given(mockTaskListener.getLogger()).willReturn(mockLogger);
        final AppCenterServiceFactory factory = new AppCenterServiceFactory(Secret.fromString("secret-token"), mockWebServer.url("/").toString(), mockProxyConfig);
        task = new CommitUploadResourceTask(mockTaskListener, factory);
    }

    @Test
    public void should_ReturnResponse_When_RequestIsSuccessful() throws Exception {
        // Given
        final UploadRequest expected = baseRequest.newBuilder().setReleaseId(0).build();
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody("{\n" +
            "  \"release_id\": 0,\n" +
            "  \"release_url\": \"string\"\n" +
            "}"));

        // When
        final UploadRequest actual = task.execute(baseRequest).get();

        // Then
        assertThat(actual)
            .isEqualTo(expected);
    }

    @Test
    public void should_ReturnResponse_When_DebugSymbolsAreFound() throws Exception {
        // Given
        final UploadRequest request = baseRequest.newBuilder()
            .setPathToDebugSymbols("path/to/mappings.txt")
            .setSymbolUploadRequest(new SymbolUploadBeginRequest(SymbolUploadBeginRequest.SymbolTypeEnum.AndroidProguard, null, "mappings.txt", "1", "1.0.0"))
            .setSymbolUploadId("string")
            .setSymbolUploadUrl("string")
            .build();
        final UploadRequest expected = request.newBuilder().setReleaseId(0).build();
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody("{\n" +
            "  \"release_id\": 0,\n" +
            "  \"release_url\": \"string\"\n" +
            "}"));
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody("{\n" +
            "  \"symbol_upload_id\": \"string\",\n" +
            "  \"app_id\": \"string\",\n" +
            "  \"user\": {\n" +
            "    \"email\": \"string\",\n" +
            "    \"display_name\": \"string\"\n" +
            "  },\n" +
            "  \"status\": \"created\",\n" +
            "  \"symbol_type\": \"AndroidProguard\",\n" +
            "  \"symbols_uploaded\": [\n" +
            "    {\n" +
            "      \"symbol_id\": \"string\",\n" +
            "      \"platform\": \"string\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"origin\": \"User\",\n" +
            "  \"file_name\": \"string\",\n" +
            "  \"file_size\": 0,\n" +
            "  \"timestamp\": \"2019-11-17T12:12:06.701Z\"\n" +
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
        mockWebServer.enqueue(new MockResponse().setResponseCode(400));

        // When
        final ThrowingRunnable throwingRunnable = () -> task.execute(baseRequest).get();

        // Then
        final ExecutionException exception = assertThrows(ExecutionException.class, throwingRunnable);
        assertThat(exception).hasCauseThat().isInstanceOf(AppCenterException.class);
        assertThat(exception).hasCauseThat().hasMessageThat().isEqualTo("Committing app resource unsuccessful: ");
        assertThat(exception).hasCauseThat().hasCauseThat().isInstanceOf(HttpException.class);
        assertThat(exception).hasCauseThat().hasCauseThat().hasMessageThat().isEqualTo("HTTP 400 Client Error");
    }
}