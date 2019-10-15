package io.jenkins.plugins.appcenter.task.internal;

import hudson.ProxyConfiguration;
import hudson.model.TaskListener;
import hudson.util.Secret;
import io.jenkins.plugins.appcenter.AppCenterException;
import io.jenkins.plugins.appcenter.api.AppCenterServiceFactory;
import io.jenkins.plugins.appcenter.model.appcenter.ReleaseUploadBeginResponse;
import io.jenkins.plugins.appcenter.model.appcenter.SymbolsUploadBeginResponse;
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
public class CreateSymbolsUploadResourceTaskTest {

    @Rule
    public MockWebServer mockWebServer = new MockWebServer();

    @Mock
    TaskListener mockTaskListener;

    @Mock
    PrintStream mockLogger;

    @Mock
    ProxyConfiguration mockProxyConfig;

    private CreateSymbolsUploadResourceTask task;

    @Before
    public void setUp() {
        given(mockTaskListener.getLogger()).willReturn(mockLogger);
        final AppCenterServiceFactory factory = new AppCenterServiceFactory(Secret.fromString("secret-token"), mockWebServer.url("/").toString(), mockProxyConfig);
        task = new CreateSymbolsUploadResourceTask(mockTaskListener, factory);
    }

    @Test
    public void should_ReturnResponse_When_RequestIsSuccessful() throws Exception {
        // Given
        final CreateSymbolsUploadResourceTask.Request request = new CreateSymbolsUploadResourceTask.Request("owner-name", "app-name");
        final SymbolsUploadBeginResponse expected = new SymbolsUploadBeginResponse("string", "string");
        mockWebServer.enqueue(new MockResponse().setResponseCode(201).setBody("{\n" +
            "  \"symbol_upload_id\": \"string\",\n" +
            "  \"upload_url\": \"string\"\n" +
            "}"));

        // When
        final SymbolsUploadBeginResponse actual = task.execute(request).get();

        // Then
        assertThat(actual)
            .isEqualTo(expected);
    }

    @Test
    public void should_ReturnResponse_When_RequestIsSuccessful_NonAsciiCharactersInFileName() throws Exception {
        // Given
        final CreateSymbolsUploadResourceTask.Request request = new CreateSymbolsUploadResourceTask.Request("owner-name", "åþþ ñåmë");
        final SymbolsUploadBeginResponse expected = new SymbolsUploadBeginResponse("string", "string");
        mockWebServer.enqueue(new MockResponse().setResponseCode(201).setBody("{\n" +
            "  \"symbol_upload_id\": \"string\",\n" +
            "  \"upload_url\": \"string\"\n" +
            "}"));

        // When
        final SymbolsUploadBeginResponse actual = task.execute(request).get();

        // Then
        assertThat(actual)
            .isEqualTo(expected);
    }

    @Test
    public void should_ReturnException_When_RequestIsUnSuccessful() {
        // Given
        final CreateSymbolsUploadResourceTask.Request request = new CreateSymbolsUploadResourceTask.Request("owner-name", "app-name");
        mockWebServer.enqueue(new MockResponse().setResponseCode(500));

        // When
        final ThrowingRunnable throwingRunnable = () -> task.execute(request).get();

        // Then
        final ExecutionException exception = assertThrows(ExecutionException.class, throwingRunnable);
        assertThat(exception).hasCauseThat().isInstanceOf(AppCenterException.class);
        assertThat(exception).hasCauseThat().hasMessageThat().isEqualTo("Creating symbols upload resource unsuccessful: ");
        assertThat(exception).hasCauseThat().hasCauseThat().isInstanceOf(HttpException.class);
        assertThat(exception).hasCauseThat().hasCauseThat().hasMessageThat().isEqualTo("HTTP 500 Server Error");
    }
}