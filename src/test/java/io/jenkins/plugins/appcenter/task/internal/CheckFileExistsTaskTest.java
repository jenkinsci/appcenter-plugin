package io.jenkins.plugins.appcenter.task.internal;

import hudson.FilePath;
import io.jenkins.plugins.appcenter.AppCenterException;
import org.junit.Before;
import org.junit.Test;
import org.junit.function.ThrowingRunnable;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.concurrent.ExecutionException;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class CheckFileExistsTaskTest {

    @Mock
    FilePath mockFilePath;

    private CheckFileExistsTask task;


    @Before
    public void setUp() {
        task = new CheckFileExistsTask(mockFilePath);
    }

    @Test
    public void should_ReturnNull_When_FileExists() throws Exception {
        // Given
        given(mockFilePath.child(anyString())).willReturn(mockFilePath);
        given(mockFilePath.exists()).willReturn(true);
        final CheckFileExistsTask.Request request = new CheckFileExistsTask.Request("path-to-app");

        // When
        final Void result = task.execute(request).get();

        // Then
        assertThat(result)
            .isNull();
    }

    @Test
    public void should_ThrowExecutionException_When_FileDoesNotExists() throws Exception {
        // Given
        given(mockFilePath.child(anyString())).willReturn(mockFilePath);
        given(mockFilePath.exists()).willReturn(false);
        final CheckFileExistsTask.Request request = new CheckFileExistsTask.Request("path-to-app");

        // When
        final ThrowingRunnable throwingRunnable = () -> task.execute(request).get();

        // Then
        final ExecutionException exception = assertThrows(ExecutionException.class, throwingRunnable);
        assertThat(exception).hasCauseThat().isInstanceOf(AppCenterException.class);
        assertThat(exception).hasCauseThat().hasMessageThat().isEqualTo("File not found: path-to-app");
    }
}