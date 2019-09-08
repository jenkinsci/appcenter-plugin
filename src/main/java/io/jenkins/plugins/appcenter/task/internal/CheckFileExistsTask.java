package io.jenkins.plugins.appcenter.task.internal;

import hudson.FilePath;
import io.jenkins.plugins.appcenter.AppCenterException;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.io.PrintStream;
import java.util.concurrent.CompletableFuture;

import static io.jenkins.plugins.appcenter.task.internal.CheckFileExistsTask.Request;

@Singleton
public final class CheckFileExistsTask implements AppCenterTask<Request, Void> {

    @Nonnull
    private final PrintStream logger;
    @Nonnull
    private final FilePath filePath;

    @Inject
    CheckFileExistsTask(@Nonnull PrintStream logger, @Nonnull final FilePath filePath) {
        this.logger = logger;
        this.filePath = filePath;
    }

    @Nonnull
    @Override
    public CompletableFuture<Void> execute(@Nonnull Request request) {
        final CompletableFuture<Void> future = new CompletableFuture<>();

        final FilePath remotablePath = filePath.child(request.pathToApp);
        try {
            if (remotablePath.exists()) {
                logger.println(String.format("File found: %s", request.pathToApp));
                future.complete(null);
            } else {
                final AppCenterException exception = new AppCenterException(String.format("File not found: %s", request.pathToApp));
                logger.println(exception.getMessage());
                future.completeExceptionally(exception);
            }
        } catch (IOException | InterruptedException e) {
            future.completeExceptionally(e);
        }

        return future;
    }

    public static class Request {
        @Nonnull
        public final String pathToApp;

        public Request(@Nonnull final String pathToApp) {
            this.pathToApp = pathToApp;
        }
    }
}