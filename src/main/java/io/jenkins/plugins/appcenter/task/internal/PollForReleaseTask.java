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
import java.util.concurrent.TimeUnit;

import static java.util.Objects.requireNonNull;

@Singleton
public final class PollForReleaseTask implements AppCenterTask<UploadRequest>, AppCenterLogger {

    private static final long serialVersionUID = 1L;

    @Nonnull
    private final TaskListener taskListener;
    @Nonnull
    private final AppCenterServiceFactory factory;

    @Inject
    PollForReleaseTask(@Nonnull final TaskListener taskListener,
                       @Nonnull final AppCenterServiceFactory factory) {
        this.taskListener = taskListener;
        this.factory = factory;
    }

    @Nonnull
    @Override
    public CompletableFuture<UploadRequest> execute(@Nonnull UploadRequest request) {
        return pollForRelease(request);
    }

    @Nonnull
    private CompletableFuture<UploadRequest> pollForRelease(@Nonnull UploadRequest request) {
        final String uploadId = requireNonNull(request.uploadId, "uploadId cannot be null");

        log("Polling for app release.");

        final CompletableFuture<UploadRequest> future = new CompletableFuture<>();

        poll(request, uploadId, future);

        return future;
    }

    private void poll(@Nonnull UploadRequest request, @Nonnull String uploadId, @Nonnull CompletableFuture<UploadRequest> future) {
        factory.createAppCenterService()
            .pollForRelease(request.ownerName, request.appName, uploadId)
            .whenComplete((pollForReleaseResponse, throwable) -> {
                if (throwable != null) {
                    final AppCenterException exception = logFailure("Polling for app release unsuccessful", throwable);
                    future.completeExceptionally(exception);
                } else {
                    switch (pollForReleaseResponse.upload_status) {
                        case uploadStarted:
                        case uploadFinished:
                            log("Polling for app release successful however not yet ready will try again.");
                            retryPolling(request, uploadId, future);
                            break;
                        case readyToBePublished:
                            log("Polling for app release successful.");
                            final UploadRequest uploadRequest = request.newBuilder()
                                .setReleaseId(pollForReleaseResponse.release_distinct_id)
                                .build();
                            future.complete(uploadRequest);
                            break;
                        case malwareDetected:
                        case error:
                            future.completeExceptionally(logFailure("Polling for app release successful however was rejected by server: " + pollForReleaseResponse.error_details));
                            break;
                        default:
                            future.completeExceptionally(logFailure("Polling for app release successful however unexpected enum returned from server: " + pollForReleaseResponse.upload_status));
                    }
                }
            });
    }

    private void retryPolling(@Nonnull UploadRequest request, @Nonnull String uploadId, @Nonnull CompletableFuture<UploadRequest> future) {
        try {
            TimeUnit.SECONDS.sleep(1L);
            poll(request, uploadId, future);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public PrintStream getLogger() {
        return taskListener.getLogger();
    }
}