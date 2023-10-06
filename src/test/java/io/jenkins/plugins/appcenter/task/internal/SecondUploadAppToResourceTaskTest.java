package io.jenkins.plugins.appcenter.task.internal;

import hudson.ProxyConfiguration;
import hudson.model.TaskListener;
import hudson.util.Secret;
import io.jenkins.plugins.appcenter.AppCenterException;
import io.jenkins.plugins.appcenter.api.AppCenterServiceFactory;
import io.jenkins.plugins.appcenter.task.request.UploadRequest;
import io.jenkins.plugins.appcenter.util.RemoteFileUtils;
import io.jenkins.plugins.appcenter.util.TestFileUtil;
import okhttp3.Headers;
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

import java.io.PrintStream;
import java.util.concurrent.ExecutionException;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class SecondUploadAppToResourceTaskTest {

    @Rule
    public MockWebServer mockWebServer = new MockWebServer();

    @Mock
    TaskListener mockTaskListener;

    @Mock
    PrintStream mockLogger;

    @Mock
    ProxyConfiguration mockProxyConfig;

    @Mock
    RemoteFileUtils mockRemoteFileUtils;

    private UploadRequest baseRequest;

    private UploadAppToResourceTask task;

    @Before
    public void setUp() {
        baseRequest = new UploadRequest.Builder().setUploadDomain(mockWebServer.url("upload").toString()).setPackageAssetId("package-asset-id").setToken("token").setChunkSize(4098).setUploadId("upload-id").setPathToApp("three/days/xiola.apk").build();
        given(mockTaskListener.getLogger()).willReturn(mockLogger);
        final AppCenterServiceFactory factory = new AppCenterServiceFactory(Secret.fromString("secret-token"), mockWebServer.url("/").toString(), mockProxyConfig);
        task = new UploadAppToResourceTask(mockTaskListener, factory, mockRemoteFileUtils);
    }

    @Test
    public void should_ReturnDebugSymbolUploadId_When_DebugSymbolsAreFound_ChunkedMode() throws Exception {
        // Given
        final UploadRequest request = baseRequest.newBuilder().setPathToDebugSymbols("string").setSymbolUploadUrl(mockWebServer.url("perry/casey/xiola").toString()).setSymbolUploadId("string").build();
        mockWebServer.enqueue(new MockResponse().setResponseCode(200));
        mockWebServer.enqueue(new MockResponse().setResponseCode(201).setHeaders(Headers.of("ETag", "0x8CB171BA9E94B0B", "Last-Modified", "Thu, 01 Jan 1970 00:00:00 GMT", "Content-MD5", "sQqNsWTgdUEFt6mb5y4/5Q==", "x-ms-request-server-encrypted", "false", "x-ms-version-id", "Thu, 01 Jan 1970 00:00:00 GMT")).setChunkedBody("", 1));
        given(mockRemoteFileUtils.getRemoteFile(anyString())).willReturn(TestFileUtil.createFileForTesting(), TestFileUtil.createLargeFileForTesting());// When
        final UploadRequest result = task.execute(request).get();
        // Then
        assertThat(result).isEqualTo(request);
    }
}
