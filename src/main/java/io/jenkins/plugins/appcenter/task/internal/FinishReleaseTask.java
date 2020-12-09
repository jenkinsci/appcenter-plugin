package io.jenkins.plugins.appcenter.task.internal;

import hudson.model.TaskListener;
import io.jenkins.plugins.appcenter.AppCenterException;
import io.jenkins.plugins.appcenter.AppCenterLogger;
import io.jenkins.plugins.appcenter.api.AppCenterServiceFactory;
import io.jenkins.plugins.appcenter.task.request.UploadRequest;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.PrintStream;
import java.util.concurrent.CompletableFuture;

@Singleton
public final class FinishReleaseTask implements AppCenterTask<UploadRequest>, AppCenterLogger {

    private static final long serialVersionUID = 1L;

    @Nonnull
    private final TaskListener taskListener;
    @Nonnull
    private final AppCenterServiceFactory factory;

    @Inject
    FinishReleaseTask(@Nonnull final TaskListener taskListener,
                      @Nonnull final AppCenterServiceFactory factory) {
        this.taskListener = taskListener;
        this.factory = factory;
    }

    @Nonnull
    @Override
    public CompletableFuture<UploadRequest> execute(@Nonnull UploadRequest request) {
        return finishRelease(request);
    }

    @Nonnull
    private CompletableFuture<UploadRequest> finishRelease(@Nonnull UploadRequest request) {
        log("Finishing release.");

        final CompletableFuture<UploadRequest> future = new CompletableFuture<>();

        final String url = getUrl(request);

        factory.createAppCenterService()
            .finishRelease(url)
            .whenComplete((finishReleaseResponse, throwable) -> {
                if (throwable != null) {
                    final AppCenterException exception = logFailure("Finishing release unsuccessful", throwable);
                    future.completeExceptionally(exception);
                } else {
                    log("Finishing release successful.");
                    future.complete(request);
                }
            });

        return future;
    }

    @Nonnull
    private String getUrl(@Nonnull UploadRequest request) {
        return String.format("%1$s/upload/finished/%2$s?token=%3$s", request.uploadDomain, request.packageAssetId, request.token);
    }

    @Override
    public PrintStream getLogger() {
        return taskListener.getLogger();
    }
}