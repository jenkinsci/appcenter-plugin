package io.jenkins.plugins.appcenter.task.internal;

import hudson.FilePath;
import hudson.model.TaskListener;
import io.jenkins.plugins.appcenter.AppCenterException;
import io.jenkins.plugins.appcenter.model.appcenter.SymbolType;
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
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class PrerequisitesTaskTest {

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

    private UploadRequest baseRequest;

    private PrerequisitesTask task;

    @Before
    public void setUp() {
        baseRequest = new UploadRequest.Builder()
            .setPathToApp("path/to/*.apk")
            .build();
        given(mockTaskListener.getLogger()).willReturn(mockLogger);
        task = new PrerequisitesTask(mockTaskListener, mockFilePath, mockParserFactory);
    }

    @Test
    public void should_ReturnModifiedRequest_When_FileExists() throws Exception {
        // Given
        final String pathToApp = String.join(File.separator, "path", "to", "app.apk");
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
    public void should_ReturnModifiedRequest_When_DebugSymbolsExists_Android() throws Exception {
        // Given
        final UploadRequest request = baseRequest.newBuilder().setPathToDebugSymbols("path/to/*.txt").build();
        final String pathToApp = String.join(File.separator, "path", "to", "app.apk");
        final String pathToDebugSymbols = String.join(File.separator, "path", "to", "mapping.txt");
        final FilePath[] files = {new FilePath(new File(pathToApp))};
        final FilePath[] debugSymbols = {new FilePath(new File(pathToDebugSymbols))};
        given(mockFilePath.list(anyString())).willReturn(files, debugSymbols);
        given(mockFilePath.child(anyString())).willReturn(mockFilePath);
        given(mockFilePath.getRemote()).willReturn(pathToDebugSymbols);
        given(mockParserFactory.androidParser(any(File.class))).willReturn(mockAndroidParser);
        given(mockAndroidParser.fileName()).willReturn("app.apk");
        given(mockAndroidParser.versionCode()).willReturn("1");
        given(mockAndroidParser.versionName()).willReturn("1.0.0");
        final SymbolUploadBeginRequest symbolUploadBeginRequest = new SymbolUploadBeginRequest(SymbolType.AndroidProguard, null, "app.apk", "1", "1.0.0");
        final UploadRequest expected = baseRequest.newBuilder().setPathToApp(pathToApp).setPathToDebugSymbols(pathToDebugSymbols).setSymbolUploadRequest(symbolUploadBeginRequest).build();

        // When
        final UploadRequest result = task.execute(request).get();

        // Then
        assertThat(result)
            .isEqualTo(expected);
    }

    @Test
    public void should_ReturnModifiedRequest_When_DebugSymbolsExists_IOS() throws Exception {
        // Given
        final UploadRequest request = baseRequest.newBuilder().setPathToDebugSymbols("path/to/*.zip").build();
        final String pathToApp = String.join(File.separator, "path", "to", "app.ipa");
        final String pathToDebugSymbols = String.join(File.separator, "path", "to", "symbols.zip");
        final FilePath[] files = {new FilePath(new File(pathToApp))};
        final FilePath[] debugSymbols = {new FilePath(new File(pathToDebugSymbols))};
        given(mockFilePath.list(anyString())).willReturn(files, debugSymbols);
        given(mockFilePath.child(anyString())).willReturn(mockFilePath);
        given(mockFilePath.getRemote()).willReturn(pathToApp);
        final SymbolUploadBeginRequest symbolUploadBeginRequest = new SymbolUploadBeginRequest(SymbolType.Apple, null, "app.ipa", "", "");
        final UploadRequest expected = baseRequest.newBuilder().setPathToApp(pathToApp).setPathToDebugSymbols(pathToDebugSymbols).setSymbolUploadRequest(symbolUploadBeginRequest).build();

        // When
        final UploadRequest result = task.execute(request).get();

        // Then
        assertThat(result)
            .isEqualTo(expected);
    }

    @Test
    public void should_ReturnModifiedRequest_When_DebugSymbolsExists_MACOS_AppZip() throws Exception {
        // Given
        final UploadRequest request = baseRequest.newBuilder().setPathToDebugSymbols("path/to/*.zip").build();
        final String pathToApp = String.join(File.separator, "path", "to", "app.app.zip");
        final String pathToDebugSymbols = String.join(File.separator, "path", "to", "symbols.zip");
        final FilePath[] files = {new FilePath(new File(pathToApp))};
        final FilePath[] debugSymbols = {new FilePath(new File(pathToDebugSymbols))};
        given(mockFilePath.list(anyString())).willReturn(files, debugSymbols);
        given(mockFilePath.child(anyString())).willReturn(mockFilePath);
        given(mockFilePath.getRemote()).willReturn(pathToApp);
        final SymbolUploadBeginRequest symbolUploadBeginRequest = new SymbolUploadBeginRequest(SymbolType.Apple, null, "app.app.zip", "", "");
        final UploadRequest expected = baseRequest.newBuilder().setPathToApp(pathToApp).setPathToDebugSymbols(pathToDebugSymbols).setSymbolUploadRequest(symbolUploadBeginRequest).build();

        // When
        final UploadRequest result = task.execute(request).get();

        // Then
        assertThat(result)
            .isEqualTo(expected);
    }

    @Test
    public void should_ReturnModifiedRequest_When_DebugSymbolsExists_MACOS_Pkg() throws Exception {
        // Given
        final UploadRequest request = baseRequest.newBuilder().setPathToDebugSymbols("path/to/*.zip").build();
        final String pathToApp = String.join(File.separator, "path", "to", "app.pkg");
        final String pathToDebugSymbols = String.join(File.separator, "path", "to", "symbols.zip");
        final FilePath[] files = {new FilePath(new File(pathToApp))};
        final FilePath[] debugSymbols = {new FilePath(new File(pathToDebugSymbols))};
        given(mockFilePath.list(anyString())).willReturn(files, debugSymbols);
        given(mockFilePath.child(anyString())).willReturn(mockFilePath);
        given(mockFilePath.getRemote()).willReturn(pathToApp);
        final SymbolUploadBeginRequest symbolUploadBeginRequest = new SymbolUploadBeginRequest(SymbolType.Apple, null, "app.pkg", "", "");
        final UploadRequest expected = baseRequest.newBuilder().setPathToApp(pathToApp).setPathToDebugSymbols(pathToDebugSymbols).setSymbolUploadRequest(symbolUploadBeginRequest).build();

        // When
        final UploadRequest result = task.execute(request).get();

        // Then
        assertThat(result)
            .isEqualTo(expected);
    }

    @Test
    public void should_ReturnModifiedRequest_When_DebugSymbolsExists_MACOS_Dmg() throws Exception {
        // Given
        final UploadRequest request = baseRequest.newBuilder().setPathToDebugSymbols("path/to/*.zip").build();
        final String pathToApp = String.join(File.separator, "path", "to", "app.dmg");
        final String pathToDebugSymbols = String.join(File.separator, "path", "to", "symbols.zip");
        final FilePath[] files = {new FilePath(new File(pathToApp))};
        final FilePath[] debugSymbols = {new FilePath(new File(pathToDebugSymbols))};
        given(mockFilePath.list(anyString())).willReturn(files, debugSymbols);
        given(mockFilePath.child(anyString())).willReturn(mockFilePath);
        given(mockFilePath.getRemote()).willReturn(pathToApp);
        final SymbolUploadBeginRequest symbolUploadBeginRequest = new SymbolUploadBeginRequest(SymbolType.Apple, null, "app.dmg", "", "");
        final UploadRequest expected = baseRequest.newBuilder().setPathToApp(pathToApp).setPathToDebugSymbols(pathToDebugSymbols).setSymbolUploadRequest(symbolUploadBeginRequest).build();

        // When
        final UploadRequest result = task.execute(request).get();

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