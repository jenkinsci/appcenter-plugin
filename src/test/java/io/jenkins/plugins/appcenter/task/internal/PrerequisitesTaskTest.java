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

    private UploadRequest fullUploadRequest;

    private PrerequisitesTask task;

    @Before
    public void setUp() {
        fullUploadRequest = new UploadRequest.Builder()
            .setPathToApp("path/to/app")
            .setPathToDebugSymbols("path/to/debug-symbols")
            .setPathToReleaseNotes("path/to/release-notes")
            .build();
        given(mockTaskListener.getLogger()).willReturn(mockLogger);
        task = new PrerequisitesTask(mockTaskListener, mockFilePath, mockParserFactory);
    }

    @Test
    public void should_ReturnModifiedRequest_When_FileExists() throws Exception {
        // Given
        final UploadRequest uploadRequest = fullUploadRequest.newBuilder().setPathToDebugSymbols("").setPathToReleaseNotes("").build();
        final String pathToApp = String.join(File.separator, "path", "to", "app.apk");
        final FilePath[] files = {new FilePath(new File(pathToApp))};
        given(mockFilePath.list(anyString())).willReturn(files);
        final UploadRequest expected = uploadRequest.newBuilder()
            .setPathToApp(pathToApp)
            .build();

        // When
        final UploadRequest result = task.execute(uploadRequest).get();

        // Then
        assertThat(result)
            .isEqualTo(expected);
    }

    @Test
    public void should_ReturnModifiedRequest_When_DebugSymbolsExists_Android() throws Exception {
        // Given
        final String pathToApp = String.join(File.separator, "path", "to", "app.apk");
        final String pathToDebugSymbols = String.join(File.separator, "path", "to", "mapping.txt");
        final String pathToReleaseNotes = String.join(File.separator, "path", "to", "release-notes.md");
        final FilePath[] files = {new FilePath(new File(pathToApp))};
        final FilePath[] debugSymbols = {new FilePath(new File(pathToDebugSymbols))};
        final FilePath[] releaseNotes = {new FilePath(new File(pathToReleaseNotes))};
        given(mockFilePath.list(anyString())).willReturn(files, debugSymbols, releaseNotes);
        given(mockFilePath.child(anyString())).willReturn(mockFilePath);
        given(mockFilePath.getRemote()).willReturn(pathToDebugSymbols);
        given(mockParserFactory.androidParser(any(File.class))).willReturn(mockAndroidParser);
        given(mockAndroidParser.versionCode()).willReturn("1");
        given(mockAndroidParser.versionName()).willReturn("1.0.0");
        final SymbolUploadBeginRequest symbolUploadBeginRequest = new SymbolUploadBeginRequest(AndroidProguard, null, "mapping.txt", "1", "1.0.0");
        final UploadRequest expected = fullUploadRequest.newBuilder()
            .setPathToApp(pathToApp)
            .setPathToDebugSymbols(pathToDebugSymbols)
            .setSymbolUploadRequest(symbolUploadBeginRequest)
            .setPathToReleaseNotes(pathToReleaseNotes)
            .build();

        // When
        final UploadRequest result = task.execute(fullUploadRequest).get();

        // Then
        assertThat(result)
            .isEqualTo(expected);
    }

    @Test
    public void should_ReturnModifiedRequest_When_DebugSymbolsExists_Android_Breakpad() throws Exception {
        // Given
        final String pathToApp = String.join(File.separator, "path", "to", "app.apk");
        final String pathToDebugSymbols = String.join(File.separator, "path", "to", "breakpad-symbols.zip");
        final String pathToReleaseNotes = String.join(File.separator, "path", "to", "release-notes.md");
        final FilePath[] files = {new FilePath(new File(pathToApp))};
        final FilePath[] debugSymbols = {new FilePath(new File(pathToDebugSymbols))};
        final FilePath[] releaseNotes = {new FilePath(new File(pathToReleaseNotes))};
        given(mockFilePath.list(anyString())).willReturn(files, debugSymbols, releaseNotes);
        given(mockFilePath.child(anyString())).willReturn(mockFilePath);
        given(mockFilePath.getRemote()).willReturn(pathToDebugSymbols);
        given(mockParserFactory.androidParser(any(File.class))).willReturn(mockAndroidParser);
        given(mockAndroidParser.versionCode()).willReturn("1");
        given(mockAndroidParser.versionName()).willReturn("1.0.0");
        final SymbolUploadBeginRequest symbolUploadBeginRequest = new SymbolUploadBeginRequest(Breakpad, null, "breakpad-symbols.zip", "1", "1.0.0");
        final UploadRequest expected = fullUploadRequest.newBuilder()
            .setPathToApp(pathToApp)
            .setPathToDebugSymbols(pathToDebugSymbols)
            .setSymbolUploadRequest(symbolUploadBeginRequest)
            .setPathToReleaseNotes(pathToReleaseNotes)
            .build();

        // When
        final UploadRequest result = task.execute(fullUploadRequest).get();

        // Then
        assertThat(result)
            .isEqualTo(expected);
    }

    @Test
    public void should_ReturnModifiedRequest_When_DebugSymbolsExists_IOS() throws Exception {
        // Given
        final String pathToApp = String.join(File.separator, "path", "to", "app.ipa");
        final String pathToDebugSymbols = String.join(File.separator, "path", "to", "symbols.zip");
        final String pathToReleaseNotes = String.join(File.separator, "path", "to", "release-notes.md");
        final FilePath[] files = {new FilePath(new File(pathToApp))};
        final FilePath[] debugSymbols = {new FilePath(new File(pathToDebugSymbols))};
        final FilePath[] releaseNotes = {new FilePath(new File(pathToReleaseNotes))};
        given(mockFilePath.list(anyString())).willReturn(files, debugSymbols, releaseNotes);
        given(mockFilePath.child(anyString())).willReturn(mockFilePath);
        given(mockFilePath.getRemote()).willReturn(pathToApp);
        final SymbolUploadBeginRequest symbolUploadBeginRequest = new SymbolUploadBeginRequest(Apple, null, "app.ipa", "", "");
        final UploadRequest expected = fullUploadRequest.newBuilder()
            .setPathToApp(pathToApp)
            .setPathToDebugSymbols(pathToDebugSymbols)
            .setSymbolUploadRequest(symbolUploadBeginRequest)
            .setPathToReleaseNotes(pathToReleaseNotes)
            .build();

        // When
        final UploadRequest result = task.execute(fullUploadRequest).get();

        // Then
        assertThat(result)
            .isEqualTo(expected);
    }

    @Test
    public void should_ReturnModifiedRequest_When_DebugSymbolsExists_MACOS_AppZip() throws Exception {
        // Given
        final String pathToApp = String.join(File.separator, "path", "to", "app.app.zip");
        final String pathToDebugSymbols = String.join(File.separator, "path", "to", "symbols.zip");
        final String pathToReleaseNotes = String.join(File.separator, "path", "to", "release-notes.md");
        final FilePath[] files = {new FilePath(new File(pathToApp))};
        final FilePath[] debugSymbols = {new FilePath(new File(pathToDebugSymbols))};
        final FilePath[] releaseNotes = {new FilePath(new File(pathToReleaseNotes))};
        given(mockFilePath.list(anyString())).willReturn(files, debugSymbols, releaseNotes);
        given(mockFilePath.child(anyString())).willReturn(mockFilePath);
        given(mockFilePath.getRemote()).willReturn(pathToApp);
        final SymbolUploadBeginRequest symbolUploadBeginRequest = new SymbolUploadBeginRequest(Apple, null, "app.app.zip", "", "");
        final UploadRequest expected = fullUploadRequest.newBuilder()
            .setPathToApp(pathToApp)
            .setPathToDebugSymbols(pathToDebugSymbols)
            .setSymbolUploadRequest(symbolUploadBeginRequest)
            .setPathToReleaseNotes(pathToReleaseNotes)
            .build();

        // When
        final UploadRequest result = task.execute(fullUploadRequest).get();

        // Then
        assertThat(result)
            .isEqualTo(expected);
    }

    @Test
    public void should_ReturnModifiedRequest_When_DebugSymbolsExists_MACOS_Pkg() throws Exception {
        // Given
        final String pathToApp = String.join(File.separator, "path", "to", "app.pkg");
        final String pathToDebugSymbols = String.join(File.separator, "path", "to", "symbols.zip");
        final String pathToReleaseNotes = String.join(File.separator, "path", "to", "release-notes.md");
        final FilePath[] files = {new FilePath(new File(pathToApp))};
        final FilePath[] debugSymbols = {new FilePath(new File(pathToDebugSymbols))};
        final FilePath[] releaseNotes = {new FilePath(new File(pathToReleaseNotes))};
        given(mockFilePath.list(anyString())).willReturn(files, debugSymbols, releaseNotes);
        given(mockFilePath.child(anyString())).willReturn(mockFilePath);
        given(mockFilePath.getRemote()).willReturn(pathToApp);
        final SymbolUploadBeginRequest symbolUploadBeginRequest = new SymbolUploadBeginRequest(Apple, null, "app.pkg", "", "");
        final UploadRequest expected = fullUploadRequest.newBuilder()
            .setPathToApp(pathToApp)
            .setPathToDebugSymbols(pathToDebugSymbols)
            .setSymbolUploadRequest(symbolUploadBeginRequest)
            .setPathToReleaseNotes(pathToReleaseNotes)
            .build();

        // When
        final UploadRequest result = task.execute(fullUploadRequest).get();

        // Then
        assertThat(result)
            .isEqualTo(expected);
    }

    @Test
    public void should_ReturnModifiedRequest_When_DebugSymbolsExists_MACOS_Dmg() throws Exception {
        // Given
        final String pathToApp = String.join(File.separator, "path", "to", "app.dmg");
        final String pathToDebugSymbols = String.join(File.separator, "path", "to", "symbols.zip");
        final String pathToReleaseNotes = String.join(File.separator, "path", "to", "release-notes.md");
        final FilePath[] files = {new FilePath(new File(pathToApp))};
        final FilePath[] debugSymbols = {new FilePath(new File(pathToDebugSymbols))};
        final FilePath[] releaseNotes = {new FilePath(new File(pathToReleaseNotes))};
        given(mockFilePath.list(anyString())).willReturn(files, debugSymbols, releaseNotes);
        given(mockFilePath.child(anyString())).willReturn(mockFilePath);
        given(mockFilePath.getRemote()).willReturn(pathToApp);
        final SymbolUploadBeginRequest symbolUploadBeginRequest = new SymbolUploadBeginRequest(Apple, null, "app.dmg", "", "");
        final UploadRequest expected = fullUploadRequest.newBuilder()
            .setPathToApp(pathToApp)
            .setPathToDebugSymbols(pathToDebugSymbols)
            .setSymbolUploadRequest(symbolUploadBeginRequest)
            .setPathToReleaseNotes(pathToReleaseNotes)
            .build();

        // When
        final UploadRequest result = task.execute(fullUploadRequest).get();

        // Then
        assertThat(result)
            .isEqualTo(expected);
    }

    @Test
    public void should_ThrowExecutionException_When_DebugSymbolsDoesNotExists() throws Exception {
        // Given
        final String pathToApp = String.join(File.separator, "path", "to", "app.apk");
        final String pathToReleaseNotes = String.join(File.separator, "path", "to", "release-notes.md");
        final FilePath[] files = {new FilePath(new File(pathToApp))};
        final FilePath[] debugSymbols = {};
        final FilePath[] releaseNotes = {new FilePath(new File(pathToReleaseNotes))};
        given(mockFilePath.list(anyString())).willReturn(files, debugSymbols, releaseNotes);

        // When
        final ThrowingRunnable throwingRunnable = () -> task.execute(fullUploadRequest).get();

        // Then
        final ExecutionException exception = assertThrows(ExecutionException.class, throwingRunnable);
        assertThat(exception).hasCauseThat().isInstanceOf(AppCenterException.class);
        assertThat(exception).hasCauseThat().hasMessageThat().isEqualTo(String.format("No symbols found matching pattern: %s", fullUploadRequest.pathToDebugSymbols));
    }

    @Test
    public void should_ThrowExecutionException_When_MultipleDebugSymbolsExists() throws Exception {
        // Given
        final String pathToApp = String.join(File.separator, "path", "to", "app.apk");
        final String pathToDebugSymbols = String.join(File.separator, "path", "to", "debug.zip");
        final String pathToAnotherDebugSymbols = String.join(File.separator, "path", "to", "more-debug.zip");
        final String pathToReleaseNotes = String.join(File.separator, "path", "to", "release-notes.md");
        final FilePath[] files = {new FilePath(new File(pathToApp))};
        final FilePath[] debugSymbols = {new FilePath(new File(pathToDebugSymbols)), new FilePath(new File(pathToAnotherDebugSymbols))};
        final FilePath[] releaseNotes = {new FilePath(new File(pathToReleaseNotes))};
        given(mockFilePath.list(anyString())).willReturn(files, debugSymbols, releaseNotes);

        // When
        final ThrowingRunnable throwingRunnable = () -> task.execute(fullUploadRequest).get();

        // Then
        final ExecutionException exception = assertThrows(ExecutionException.class, throwingRunnable);
        assertThat(exception).hasCauseThat().isInstanceOf(AppCenterException.class);
        assertThat(exception).hasCauseThat().hasMessageThat().isEqualTo(String.format("Multiple symbols found matching pattern: %s", fullUploadRequest.pathToDebugSymbols));
    }

    @Test
    public void should_ReturnModifiedRequest_When_ReleaseNotesExists() throws Exception {
        // Given
        final UploadRequest uploadRequest = fullUploadRequest.newBuilder().setPathToDebugSymbols("").build();
        final String pathToApp = String.join(File.separator, "path", "to", "app.apk");
        final String pathToReleaseNotes = String.join(File.separator, "path", "to", "release-notes.md");
        final FilePath[] files = {new FilePath(new File(pathToApp))};
        final FilePath[] releaseNotes = {new FilePath(new File(pathToReleaseNotes))};
        given(mockFilePath.list(anyString())).willReturn(files, releaseNotes);
        final UploadRequest expected = uploadRequest.newBuilder()
            .setPathToApp(pathToApp)
            .setPathToReleaseNotes(pathToReleaseNotes)
            .build();

        // When
        final UploadRequest result = task.execute(uploadRequest).get();

        // Then
        assertThat(result)
            .isEqualTo(expected);
    }

    @Test
    public void should_ThrowExecutionException_When_ReleaseNotesDoesNotExists() throws Exception {
        // Given
        final UploadRequest uploadRequest = fullUploadRequest.newBuilder().setPathToDebugSymbols("").build();
        final String pathToApp = String.join(File.separator, "path", "to", "app.apk");
        final FilePath[] files = {new FilePath(new File(pathToApp))};
        final FilePath[] releaseNotes = {};
        given(mockFilePath.list(anyString())).willReturn(files, releaseNotes);

        // When
        final ThrowingRunnable throwingRunnable = () -> task.execute(uploadRequest).get();

        // Then
        final ExecutionException exception = assertThrows(ExecutionException.class, throwingRunnable);
        assertThat(exception).hasCauseThat().isInstanceOf(AppCenterException.class);
        assertThat(exception).hasCauseThat().hasMessageThat().isEqualTo(String.format("No release notes found matching pattern: %s", fullUploadRequest.pathToReleaseNotes));
    }

    @Test
    public void should_ThrowExecutionException_When_MultipleReleaseNotesExists() throws Exception {
        // Given
        final UploadRequest uploadRequest = fullUploadRequest.newBuilder().setPathToDebugSymbols("").build();
        final String pathToApp = String.join(File.separator, "path", "to", "app.apk");
        final String pathToReleaseNotes = String.join(File.separator, "path", "to", "release-notes.md");
        final String pathToAnotherReleaseNotes = String.join(File.separator, "path", "to", "more-release-notes.md");
        final FilePath[] files = {new FilePath(new File(pathToApp))};
        final FilePath[] releaseNotes = {new FilePath(new File(pathToReleaseNotes)), new FilePath(new File(pathToAnotherReleaseNotes))};
        given(mockFilePath.list(anyString())).willReturn(files, releaseNotes);

        // When
        final ThrowingRunnable throwingRunnable = () -> task.execute(uploadRequest).get();

        // Then
        final ExecutionException exception = assertThrows(ExecutionException.class, throwingRunnable);
        assertThat(exception).hasCauseThat().isInstanceOf(AppCenterException.class);
        assertThat(exception).hasCauseThat().hasMessageThat().isEqualTo(String.format("Multiple release notes found matching pattern: %s", fullUploadRequest.pathToReleaseNotes));
    }
}