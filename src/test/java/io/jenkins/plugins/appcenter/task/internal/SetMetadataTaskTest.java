package io.jenkins.plugins.appcenter.task.internal;

import hudson.ProxyConfiguration;
import hudson.model.TaskListener;
import hudson.util.Secret;
import io.jenkins.plugins.appcenter.AppCenterException;
import io.jenkins.plugins.appcenter.api.AppCenterServiceFactory;
import io.jenkins.plugins.appcenter.task.request.UploadRequest;
import io.jenkins.plugins.appcenter.util.RemoteFileUtils;
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
public class SetMetadataTaskTest {

    @Rule
    public MockWebServer mockWebServer = new MockWebServer();

    @Mock
    TaskListener mockTaskListener;

    @Mock
    RemoteFileUtils remoteFileUtils;

    @Mock
    PrintStream mockLogger;

    @Mock
    ProxyConfiguration mockProxyConfig;

    private UploadRequest baseRequest;

    private SetMetadataTask task;

    @Before
    public void setUp() {
        baseRequest = new UploadRequest.Builder()
            .setOwnerName("owner-name")
            .setAppName("app-name")
            .setPathToApp("path-to-app")
            .build();
        given(mockTaskListener.getLogger()).willReturn(mockLogger);
        final AppCenterServiceFactory factory = new AppCenterServiceFactory(Secret.fromString("secret-token"), mockWebServer.url("/").toString(), mockProxyConfig);
        task = new SetMetadataTask(mockTaskListener, factory, remoteFileUtils);
    }

    @Test
    public void should_ReturnException_When_UploadDomainIsMissing() {
        // Given
        final UploadRequest uploadRequest = baseRequest.newBuilder()
            .build();

        // When
        final ThrowingRunnable throwingRunnable = () -> task.execute(uploadRequest).get();

        // Then
        final NullPointerException exception = assertThrows(NullPointerException.class, throwingRunnable);
        assertThat(exception).hasMessageThat().contains("uploadDomain cannot be null");
    }

    @Test
    public void should_ReturnException_When_PackageAssetIdIsMissing() {
        // Given
        final UploadRequest uploadRequest = baseRequest.newBuilder()
            .setUploadDomain("upload-domain")
            .build();

        // When
        final ThrowingRunnable throwingRunnable = () -> task.execute(uploadRequest).get();

        // Then
        final NullPointerException exception = assertThrows(NullPointerException.class, throwingRunnable);
        assertThat(exception).hasMessageThat().contains("packageAssetId cannot be null");
    }

    @Test
    public void should_ReturnException_When_TokenIsMissing() {
        // Given
        final UploadRequest uploadRequest = baseRequest.newBuilder()
            .setUploadDomain("upload-domain")
            .setPackageAssetId("package_asset_id")
            .build();

        // When
        final ThrowingRunnable throwingRunnable = () -> task.execute(uploadRequest).get();

        // Then
        final NullPointerException exception = assertThrows(NullPointerException.class, throwingRunnable);
        assertThat(exception).hasMessageThat().contains("token cannot be null");
    }

    @Test
    public void should_ReturnResponse_When_RequestIsSuccessful() throws Exception {
        // Given
        final UploadRequest uploadRequest = baseRequest.newBuilder()
            .setUploadDomain("upload-domain")
            .setPackageAssetId("package_asset_id")
            .setToken("token")
            .build();
        final UploadRequest expected = uploadRequest.newBuilder().setChunkSize(4098).build();
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody("{\n" +
            " \"chunk_size\": 4098\n" +
            "}"));

        // When
        final UploadRequest actual = task.execute(uploadRequest).get();

        // Then
        assertThat(actual)
            .isEqualTo(expected);
    }

    @Test
    public void should_ReturnException_When_RequestIsUnSuccessful() {
        // Given
        final UploadRequest uploadRequest = baseRequest.newBuilder()
            .setUploadDomain("upload-domain")
            .setPackageAssetId("package_asset_id")
            .setToken("token")
            .build();
        mockWebServer.enqueue(new MockResponse().setResponseCode(400));

        // When
        final ThrowingRunnable throwingRunnable = () -> task.execute(uploadRequest).get();

        // Then
        final ExecutionException exception = assertThrows(ExecutionException.class, throwingRunnable);
        assertThat(exception).hasCauseThat().isInstanceOf(AppCenterException.class);
        assertThat(exception).hasCauseThat().hasMessageThat().contains("Setting metadata unsuccessful");
    }
}