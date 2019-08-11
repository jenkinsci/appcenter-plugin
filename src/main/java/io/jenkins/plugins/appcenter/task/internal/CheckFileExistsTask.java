package io.jenkins.plugins.appcenter.task.internal;

import hudson.FilePath;
import io.jenkins.plugins.appcenter.AppCenterException;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

import static io.jenkins.plugins.appcenter.task.internal.CheckFileExistsTask.Request;

public final class CheckFileExistsTask implements AppCenterTask<Request, Void> {

    @Nonnull
    private final FilePath filePath;

    @Inject
    public CheckFileExistsTask(@Nonnull final FilePath filePath) {
        this.filePath = filePath;
    }

    @Override
    public CompletableFuture<Void> execute(Request request) {
        final CompletableFuture<Void> future = new CompletableFuture<>();

        final FilePath remotablePath = filePath.child(request.pathToApp);
        try {
            if (remotablePath.exists()) {
                future.complete(null);
            } else {
                future.completeExceptionally(new AppCenterException(String.format("File not found: %s", request.pathToApp)));
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