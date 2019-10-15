package io.jenkins.plugins.appcenter.task.internal;

import hudson.ProxyConfiguration;
import hudson.model.TaskListener;
import hudson.util.Secret;
import io.jenkins.plugins.appcenter.AppCenterException;
import io.jenkins.plugins.appcenter.api.AppCenterServiceFactory;
import io.jenkins.plugins.appcenter.model.appcenter.ReleaseUploadEndResponse;
import io.jenkins.plugins.appcenter.model.appcenter.SymbolsUploadEndResponse;
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
public class CommitSymbolsUploadResourceTaskTest {

    @Rule
    public MockWebServer mockWebServer = new MockWebServer();

    @Mock
    TaskListener mockTaskListener;

    @Mock
    PrintStream mockLogger;

    @Mock
    ProxyConfiguration mockProxyConfig;

    private CommitSymbolsUploadResourceTask task;

    @Before
    public void setUp() {
        given(mockTaskListener.getLogger()).willReturn(mockLogger);
        final AppCenterServiceFactory factory = new AppCenterServiceFactory(Secret.fromString("secret-token"), mockWebServer.url("/").toString(), mockProxyConfig);
        task = new CommitSymbolsUploadResourceTask(mockTaskListener, factory);
    }

    @Test
    public void should_ReturnResponse_When_RequestIsSuccessful() throws Exception {
        // Given
        final CommitSymbolsUploadResourceTask.Request request = new CommitSymbolsUploadResourceTask.Request("owner-name", "app-name", "upload-id");
        final SymbolsUploadEndResponse expected = new SymbolsUploadEndResponse("string");
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody("{\n" +
            "  \"symbol_upload_id\": \"string\"\n" +
            "}"));

        // When
        final SymbolsUploadEndResponse actual = task.execute(request).get();

        // Then
        assertThat(actual)
            .isEqualTo(expected);
    }

    @Test
    public void should_ReturnException_When_RequestIsUnSuccessful() {
        // Given
        final CommitUploadResourceTask.Request request = new CommitUploadResourceTask.Request("owner-name", "app-name", "upload-id");
        mockWebServer.enqueue(new MockResponse().setResponseCode(400));

        // When
        final ThrowingRunnable throwingRunnable = () -> task.execute(request).get();

        // Then
        final ExecutionException exception = assertThrows(ExecutionException.class, throwingRunnable);
        assertThat(exception).hasCauseThat().isInstanceOf(AppCenterException.class);
        assertThat(exception).hasCauseThat().hasMessageThat().isEqualTo("Committing resource unsuccessful: ");
        assertThat(exception).hasCauseThat().hasCauseThat().isInstanceOf(HttpException.class);
        assertThat(exception).hasCauseThat().hasCauseThat().hasMessageThat().isEqualTo("HTTP 400 Client Error");
    }
}