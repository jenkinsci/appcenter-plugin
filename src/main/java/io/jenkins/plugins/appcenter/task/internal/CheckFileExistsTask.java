package io.jenkins.plugins.appcenter.task.internal;

import hudson.FilePath;
import hudson.model.TaskListener;
import io.jenkins.plugins.appcenter.AppCenterException;
import io.jenkins.plugins.appcenter.AppCenterLogger;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.io.PrintStream;
import java.util.concurrent.CompletableFuture;

import static io.jenkins.plugins.appcenter.task.internal.CheckFileExistsTask.Request;

@Singleton
public final class CheckFileExistsTask implements AppCenterTask<Request, Boolean>, AppCenterLogger {

    private static final long serialVersionUID = 1L;

    @Nonnull
    private final TaskListener taskListener;
    @Nonnull
    private final FilePath filePath;

    @Inject
    CheckFileExistsTask(@Nonnull TaskListener taskListener, @Nonnull final FilePath filePath) {
        this.taskListener = taskListener;
        this.filePath = filePath;
    }

    @Nonnull
    @Override
    public CompletableFuture<Boolean> execute(@Nonnull Request request) {
        final CompletableFuture<Boolean> future = new CompletableFuture<>();

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
                future.complete(true);
            }
        } catch (IOException | InterruptedException e) {
            future.completeExceptionally(e);
        }

        return future;
    }

    @Override
    public PrintStream getLogger() {
        return taskListener.getLogger();
    }

    public static class Request {
        @Nonnull
        private final String pathToApp;

        public Request(@Nonnull final String pathToApp) {
            this.pathToApp = pathToApp;
        }
    }
}