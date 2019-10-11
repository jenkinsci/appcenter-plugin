package io.jenkins.plugins.appcenter.task.internal;

import hudson.FilePath;
import hudson.model.TaskListener;
import io.jenkins.plugins.appcenter.AppCenterException;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.io.PrintStream;
import java.util.concurrent.CompletableFuture;

import static io.jenkins.plugins.appcenter.task.internal.CheckFileExistsTask.Request;

@Singleton
public final class CheckFileExistsTask implements AppCenterTask<Request, Boolean> {

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
        final PrintStream logger = taskListener.getLogger();
        final CompletableFuture<Boolean> future = new CompletableFuture<>();

        try {

            FilePath[] remoteFiles = filePath.list(request.pathToApp);

            if (remoteFiles.length != 0) {
                logger.println(String.format("File found: %s", remoteFiles[0]));
                future.complete(true);
            } else {
                final AppCenterException exception = new AppCenterException(String.format("No file(s) found: %s", request.pathToApp));
                exception.printStackTrace(logger);
                future.completeExceptionally(exception);
            }
        } catch (IOException | InterruptedException e) {
            future.completeExceptionally(e);
        }

        return future;
    }

    public static class Request {
        @Nonnull
        private final String pathToApp;

        public Request(@Nonnull final String pathToApp) {
            this.pathToApp = pathToApp;
        }
    }
}