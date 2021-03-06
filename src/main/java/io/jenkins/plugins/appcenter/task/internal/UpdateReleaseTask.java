package io.jenkins.plugins.appcenter.task.internal;

import hudson.model.TaskListener;
import io.jenkins.plugins.appcenter.AppCenterException;
import io.jenkins.plugins.appcenter.AppCenterLogger;
import io.jenkins.plugins.appcenter.api.AppCenterServiceFactory;
import io.jenkins.plugins.appcenter.model.appcenter.UpdateReleaseUploadRequest;
import io.jenkins.plugins.appcenter.task.request.UploadRequest;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.PrintStream;
import java.util.concurrent.CompletableFuture;

import static java.util.Objects.requireNonNull;

@Singleton
public final class UpdateReleaseTask implements AppCenterTask<UploadRequest>, AppCenterLogger {

    private static final long serialVersionUID = 1L;

    @Nonnull
    private final TaskListener taskListener;
    @Nonnull
    private final AppCenterServiceFactory factory;

    @Inject
    UpdateReleaseTask(@Nonnull final TaskListener taskListener,
                      @Nonnull final AppCenterServiceFactory factory) {
        this.taskListener = taskListener;
        this.factory = factory;
    }

    @Nonnull
    @Override
    public CompletableFuture<UploadRequest> execute(@Nonnull UploadRequest request) {
        return updateRelease(request);
    }

    @Nonnull
    private CompletableFuture<UploadRequest> updateRelease(@Nonnull UploadRequest request) {
        final String uploadId = requireNonNull(request.uploadId, "uploadId cannot be null");

        log("Updating release.");

        final CompletableFuture<UploadRequest> future = new CompletableFuture<>();

        final UpdateReleaseUploadRequest updateReleaseUploadRequest = new UpdateReleaseUploadRequest(UpdateReleaseUploadRequest.StatusEnum.uploadFinished);

        factory.createAppCenterService()
            .updateReleaseUpload(request.ownerName, request.appName, uploadId, updateReleaseUploadRequest)
            .whenComplete((updateReleaseUploadResponse, throwable) -> {
                if (throwable != null) {
                    final AppCenterException exception = logFailure("Updating release unsuccessful", throwable);
                    future.completeExceptionally(exception);
                } else {
                    log("Updating release release successful.");
                    future.complete(request);
                }
            });

        return future;
    }

    @Override
    public PrintStream getLogger() {
        return taskListener.getLogger();
    }
}