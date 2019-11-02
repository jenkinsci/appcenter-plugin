package io.jenkins.plugins.appcenter.task.internal;

import hudson.FilePath;
import hudson.model.TaskListener;
import io.jenkins.plugins.appcenter.AppCenterException;
import io.jenkins.plugins.appcenter.task.request.UploadRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.function.ThrowingRunnable;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.File;
import java.io.PrintStream;
import java.util.concurrent.ExecutionException;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class CheckFileExistsTaskTest {

    @Mock
    TaskListener mockTaskListener;

    @Mock
    PrintStream mockLogger;

    @Mock
    FilePath mockFilePath;

    private UploadRequest baseRequest;

    private CheckFileExistsTask task;

    @Before
    public void setUp() {
        baseRequest = new UploadRequest.Builder()
            .setPathToApp("path/to/*.apk")
            .build();
        given(mockTaskListener.getLogger()).willReturn(mockLogger);
        task = new CheckFileExistsTask(mockTaskListener, mockFilePath);
    }

    @Test
    public void should_ReturnTrue_When_FileExists() throws Exception {
        // Given
        final String pathToApp = String.join(File.separator, "path", "to", "app");
        final FilePath[] files = {new FilePath(new File(pathToApp))};
        given(mockFilePath.list(anyString())).willReturn(files);
        final UploadRequest expected = baseRequest.newBuilder().setPathToApp(pathToApp).build();

        // When
        final UploadRequest result = task.execute(baseRequest).get();

        // Then
        assertThat(result)
            .isEqualTo(expected);
    }

    @Test
    public void should_ThrowExecutionException_When_FileDoesNotExists() throws Exception {
        // Given
        final FilePath[] files = {};
        given(mockFilePath.list(anyString())).willReturn(files);

        // When
        final ThrowingRunnable throwingRunnable = () -> task.execute(baseRequest).get();

        // Then
        final ExecutionException exception = assertThrows(ExecutionException.class, throwingRunnable);
        assertThat(exception).hasCauseThat().isInstanceOf(AppCenterException.class);
        assertThat(exception).hasCauseThat().hasMessageThat().isEqualTo(String.format("No file found matching pattern: %s", baseRequest.pathToApp));
    }

    @Test
    public void should_ThrowExecutionException_When_MultipleFilesExists() throws Exception {
        // Given
        final String pathToApp = "path/to/app.apk";
        final String pathToAnotherApp = "path/to/sample.apk";
        final FilePath[] files = {new FilePath(new File(pathToApp)), new FilePath(new File(pathToAnotherApp))};
        given(mockFilePath.list(anyString())).willReturn(files);

        // When
        final ThrowingRunnable throwingRunnable = () -> task.execute(baseRequest).get();

        // Then
        final ExecutionException exception = assertThrows(ExecutionException.class, throwingRunnable);
        assertThat(exception).hasCauseThat().isInstanceOf(AppCenterException.class);
        assertThat(exception).hasCauseThat().hasMessageThat().isEqualTo(String.format("Multiple files found matching pattern: %s", baseRequest.pathToApp));
    }
}