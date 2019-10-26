package io.jenkins.plugins.appcenter.task.internal;

import hudson.FilePath;
import hudson.model.TaskListener;
import io.jenkins.plugins.appcenter.AppCenterException;
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

    private CheckFileExistsTask task;


    @Before
    public void setUp() {
        given(mockTaskListener.getLogger()).willReturn(mockLogger);
        task = new CheckFileExistsTask(mockTaskListener, mockFilePath);
    }

    @Test
    public void should_ReturnTrue_When_FileExists() throws Exception {
        // Given
        final String pathToApp = "path/to/*.apk";
        final FilePath[] files = {new FilePath(new File(pathToApp))};
        given(mockFilePath.list(anyString())).willReturn(files);
        final CheckFileExistsTask.Request request = new CheckFileExistsTask.Request(pathToApp);

        // When
        final Boolean result = task.execute(request).get();

        // Then
        assertThat(result)
            .isTrue();
    }

    @Test
    public void should_ThrowExecutionException_When_FileDoesNotExists() throws Exception {
        // Given
        final String pathToApp = "path/to/*.apk";
        final FilePath[] files = {};
        given(mockFilePath.list(anyString())).willReturn(files);
        final CheckFileExistsTask.Request request = new CheckFileExistsTask.Request(pathToApp);

        // When
        final ThrowingRunnable throwingRunnable = () -> task.execute(request).get();

        // Then
        final ExecutionException exception = assertThrows(ExecutionException.class, throwingRunnable);
        assertThat(exception).hasCauseThat().isInstanceOf(AppCenterException.class);
        assertThat(exception).hasCauseThat().hasMessageThat().isEqualTo(String.format("No file found matching pattern: %s", pathToApp));
    }

    @Test
    public void should_ThrowExecutionException_When_MultipleFilesExists() throws Exception {
        // Given
        final String pathToApp = "path/to/*.apk";
        final FilePath[] files = {new FilePath(new File("path/to/app.apk")), new FilePath(new File("path/to/sample.apk"))};
        given(mockFilePath.list(anyString())).willReturn(files);
        final CheckFileExistsTask.Request request = new CheckFileExistsTask.Request(pathToApp);

        // When
        final ThrowingRunnable throwingRunnable = () -> task.execute(request).get();

        // Then
        final ExecutionException exception = assertThrows(ExecutionException.class, throwingRunnable);
        assertThat(exception).hasCauseThat().isInstanceOf(AppCenterException.class);
        assertThat(exception).hasCauseThat().hasMessageThat().isEqualTo(String.format("Multiple files found matching pattern: %s", pathToApp));
    }
}