package io.jenkins.plugins.appcenter.task.internal;

import hudson.FilePath;
import hudson.model.TaskListener;
import io.jenkins.plugins.appcenter.AppCenterException;
import io.jenkins.plugins.appcenter.model.appcenter.SymbolUploadBeginRequest;
import io.jenkins.plugins.appcenter.task.request.UploadRequest;
import io.jenkins.plugins.appcenter.util.AndroidParser;
import io.jenkins.plugins.appcenter.util.ParserFactory;
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
import static io.jenkins.plugins.appcenter.model.appcenter.SymbolUploadBeginRequest.SymbolTypeEnum.AndroidProguard;
import static io.jenkins.plugins.appcenter.model.appcenter.SymbolUploadBeginRequest.SymbolTypeEnum.Apple;
import static io.jenkins.plugins.appcenter.model.appcenter.SymbolUploadBeginRequest.SymbolTypeEnum.Breakpad;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class SecondPrerequisitesTaskTest {

    @Mock
    TaskListener mockTaskListener;

    @Mock
    PrintStream mockLogger;

    @Mock
    FilePath mockFilePath;

    @Mock
    ParserFactory mockParserFactory;

    @Mock
    AndroidParser mockAndroidParser;

    private UploadRequest fullUploadRequest;

    private PrerequisitesTask task;

    @Before
    public void setUp() {
        fullUploadRequest = new UploadRequest.Builder().setPathToApp("path/to/app").setPathToDebugSymbols("path/to/debug-symbols").setPathToReleaseNotes("path/to/release-notes").build();
        task = new PrerequisitesTask(mockTaskListener, mockFilePath, mockParserFactory);
    }

    @Test
    public void should_ThrowExecutionException_When_FileDoesNotExists() throws Exception {
        // Given
        final FilePath[] files = {};
        given(mockFilePath.list(anyString())).willReturn(files);
        // When
        final ThrowingRunnable throwingRunnable = () -> task.execute(fullUploadRequest).get();
        // Then
        final ExecutionException exception = assertThrows(ExecutionException.class, throwingRunnable);
        assertThat(exception).hasCauseThat().isInstanceOf(AppCenterException.class);
        assertThat(exception).hasCauseThat().hasMessageThat().isEqualTo(String.format("No file found matching pattern: %s", fullUploadRequest.pathToApp));
    }

    @Test
    public void should_ThrowExecutionException_When_MultipleFilesExists() throws Exception {
        // Given
        final String pathToApp = "path/to/app.apk";
        final String pathToAnotherApp = "path/to/sample.apk";
        final FilePath[] files = { new FilePath(new File(pathToApp)), new FilePath(new File(pathToAnotherApp)) };
        given(mockFilePath.list(anyString())).willReturn(files);
        // When
        final ThrowingRunnable throwingRunnable = () -> task.execute(fullUploadRequest).get();
        // Then
        final ExecutionException exception = assertThrows(ExecutionException.class, throwingRunnable);
        assertThat(exception).hasCauseThat().isInstanceOf(AppCenterException.class);
        assertThat(exception).hasCauseThat().hasMessageThat().isEqualTo(String.format("Multiple files found matching pattern: %s", fullUploadRequest.pathToApp));
    }
}
