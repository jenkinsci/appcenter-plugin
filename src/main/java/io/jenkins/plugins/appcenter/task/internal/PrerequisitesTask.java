package io.jenkins.plugins.appcenter.task.internal;

import hudson.FilePath;
import hudson.model.TaskListener;
import io.jenkins.plugins.appcenter.AppCenterException;
import io.jenkins.plugins.appcenter.AppCenterLogger;
import io.jenkins.plugins.appcenter.model.appcenter.SymbolType;
import io.jenkins.plugins.appcenter.model.appcenter.SymbolUploadBeginRequest;
import io.jenkins.plugins.appcenter.task.request.UploadRequest;
import io.jenkins.plugins.appcenter.util.AndroidParser;
import io.jenkins.plugins.appcenter.util.ParserFactory;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.concurrent.CompletableFuture;

@Singleton
public final class PrerequisitesTask implements AppCenterTask<UploadRequest>, AppCenterLogger {

    private static final long serialVersionUID = 1L;

    @Nonnull
    private final TaskListener taskListener;
    @Nonnull
    private final FilePath filePath;
    @Nonnull
    private final ParserFactory parserFactory;

    @Inject
    PrerequisitesTask(@Nonnull TaskListener taskListener, @Nonnull final FilePath filePath, @Nonnull final ParserFactory parserFactory) {
        this.taskListener = taskListener;
        this.filePath = filePath;
        this.parserFactory = parserFactory;
    }

    @Nonnull
    @Override
    public CompletableFuture<UploadRequest> execute(@Nonnull UploadRequest request) {
        if (request.pathToDebugSymbols.trim().isEmpty()) {
            return checkFileExists(request);
        } else {
            return checkFileExists(request)
                .thenCompose(this::checkSymbolsExist);
        }
    }

    @Nonnull
    private CompletableFuture<UploadRequest> checkFileExists(@Nonnull UploadRequest request) {
        final CompletableFuture<UploadRequest> future = new CompletableFuture<>();

        try {
            final FilePath[] listOfMatchingFilePaths = filePath.list(request.pathToApp);
            final int numberOfMatchingFiles = listOfMatchingFilePaths.length;
            if (numberOfMatchingFiles > 1) {
                final AppCenterException exception = logFailure(String.format("Multiple files found matching pattern: %s", request.pathToApp));
                future.completeExceptionally(exception);
            } else if (numberOfMatchingFiles < 1) {
                final AppCenterException exception = logFailure(String.format("No file found matching pattern: %s", request.pathToApp));
                future.completeExceptionally(exception);
            } else {
                log(String.format("File found matching pattern: %s", request.pathToApp));
                final String pathToApp = listOfMatchingFilePaths[0].getRemote();
                final UploadRequest uploadRequest = request.newBuilder()
                    .setPathToApp(pathToApp)
                    .build();
                future.complete(uploadRequest);
            }
        } catch (IOException | InterruptedException e) {
            future.completeExceptionally(e);
        }

        return future;
    }

    @Nonnull
    private CompletableFuture<UploadRequest> checkSymbolsExist(@Nonnull UploadRequest request) {
        final CompletableFuture<UploadRequest> future = new CompletableFuture<>();

        try {
            final FilePath[] listOfMatchingFilePaths = filePath.list(request.pathToDebugSymbols);
            final int numberOfMatchingFiles = listOfMatchingFilePaths.length;
            if (numberOfMatchingFiles > 1) {
                final AppCenterException exception = logFailure(String.format("Multiple symbols found matching pattern: %s", request.pathToDebugSymbols));
                future.completeExceptionally(exception);
            } else if (numberOfMatchingFiles < 1) {
                final AppCenterException exception = logFailure(String.format("No symbols found matching pattern: %s", request.pathToDebugSymbols));
                future.completeExceptionally(exception);
            } else {
                log(String.format("Symbols found matching pattern: %s", request.pathToDebugSymbols));
                final String pathToDebugSymbols = listOfMatchingFilePaths[0].getRemote();
                final UploadRequest uploadRequest = request.newBuilder()
                    .setPathToDebugSymbols(pathToDebugSymbols)
                    .setSymbolUploadRequest(symbolUploadRequest(request.pathToApp))
                    .build();
                future.complete(uploadRequest);
            }
        } catch (IOException | InterruptedException e) {
            future.completeExceptionally(e);
        }

        return future;
    }

    @Nonnull
    private SymbolUploadBeginRequest symbolUploadRequest(@Nonnull String pathToApp) throws IllegalStateException, IOException {
        if (pathToApp.endsWith(".apk")) return androidSymbolsUpload(pathToApp);
        if (pathToApp.endsWith(".ipa") || pathToApp.endsWith(".app.zip") || pathToApp.endsWith(".pkg") || pathToApp.endsWith(".dmg")) return appleSymbolsUpload(pathToApp);

        throw new IllegalStateException("Unable to determine application type and therefore debug symbol type");
    }

    @Nonnull
    private SymbolUploadBeginRequest androidSymbolsUpload(@Nonnull String pathToApp) throws IOException {
        final File file = new File(filePath.child(pathToApp).getRemote());
        final AndroidParser androidParser = parserFactory.androidParser(file);
        final String fileName = androidParser.fileName();
        final String versionCode = androidParser.versionCode();
        final String versionName = androidParser.versionName();

        return new SymbolUploadBeginRequest(SymbolType.AndroidProguard, null, fileName, versionCode, versionName);
    }

    @Nonnull
    private SymbolUploadBeginRequest appleSymbolsUpload(@Nonnull String pathToApp) {
        final File file = new File(filePath.child(pathToApp).getRemote());

        return new SymbolUploadBeginRequest(SymbolType.Apple, null, file.getName(), "", "");
    }

    @Override
    public PrintStream getLogger() {
        return taskListener.getLogger();
    }
}