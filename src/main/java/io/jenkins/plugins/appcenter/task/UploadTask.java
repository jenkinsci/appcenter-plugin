package io.jenkins.plugins.appcenter.task;

import io.jenkins.plugins.appcenter.AppCenterException;
import io.jenkins.plugins.appcenter.task.internal.*;
import io.jenkins.plugins.appcenter.task.request.UploadRequest;
import jenkins.security.MasterToSlaveCallable;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.concurrent.CompletableFuture;

@Singleton
public final class UploadTask extends MasterToSlaveCallable<Boolean, AppCenterException> {

    private final PrerequisitesTask prerequisitesTask;
    private final CreateUploadResourceTask createUploadResource;
    private final SetMetadataTask setMetadataTask;
    private final UploadAppToResourceTask uploadAppToResource;
    private final FinishReleaseTask finishReleaseTask;
    private final UpdateReleaseTask updateReleaseTask;
    private final PollForReleaseTask pollForReleaseTask;
    private final DistributeResourceTask distributeResource;
    private final UploadRequest originalRequest;

    @Inject
    UploadTask(final PrerequisitesTask prerequisitesTask,
               final CreateUploadResourceTask createUploadResource,
               final SetMetadataTask setMetadataTask,
               final UploadAppToResourceTask uploadAppToResource,
               final FinishReleaseTask finishReleaseTask,
               final UpdateReleaseTask updateReleaseTask,
               final PollForReleaseTask pollForReleaseTask,
               final DistributeResourceTask distributeResource,
               final UploadRequest request) {
        this.prerequisitesTask = prerequisitesTask;
        this.createUploadResource = createUploadResource;
        this.setMetadataTask = setMetadataTask;
        this.uploadAppToResource = uploadAppToResource;
        this.finishReleaseTask = finishReleaseTask;
        this.updateReleaseTask = updateReleaseTask;
        this.pollForReleaseTask = pollForReleaseTask;
        this.distributeResource = distributeResource;
        this.originalRequest = request;
    }

    @Override
    public Boolean call() {
        final CompletableFuture<Boolean> future = new CompletableFuture<>();

        prerequisitesTask.execute(originalRequest)
            .thenCompose(createUploadResource::execute)
            .thenCompose(setMetadataTask::execute)
            .thenCompose(uploadAppToResource::execute)
            .thenCompose(finishReleaseTask::execute)
            .thenCompose(updateReleaseTask::execute)
            .thenCompose(pollForReleaseTask::execute)
            .thenCompose(distributeResource::execute)
            .whenComplete((uploadRequest, throwable) -> {
                if (throwable != null) {
                    future.completeExceptionally(throwable);
                } else {
                    future.complete(true);
                }
            })
            .join();

        return future.join();
    }
}