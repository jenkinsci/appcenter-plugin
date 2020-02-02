package io.jenkins.plugins.appcenter.task;

import io.jenkins.plugins.appcenter.AppCenterException;
import io.jenkins.plugins.appcenter.task.internal.CommitUploadResourceTask;
import io.jenkins.plugins.appcenter.task.internal.CreateUploadResourceTask;
import io.jenkins.plugins.appcenter.task.internal.DistributeResourceTask;
import io.jenkins.plugins.appcenter.task.internal.PrerequisitesTask;
import io.jenkins.plugins.appcenter.task.internal.UploadAppToResourceTask;
import io.jenkins.plugins.appcenter.task.request.UploadRequest;
import jenkins.security.MasterToSlaveCallable;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.concurrent.CompletableFuture;

@Singleton
public final class UploadTask extends MasterToSlaveCallable<Boolean, AppCenterException> {

    private final PrerequisitesTask prerequisitesTask;
    private final CreateUploadResourceTask createUploadResource;
    private final UploadAppToResourceTask uploadAppToResource;
    private final CommitUploadResourceTask commitUploadResource;
    private final DistributeResourceTask distributeResource;
    private final UploadRequest originalRequest;

    @Inject
    UploadTask(final PrerequisitesTask prerequisitesTask, final CreateUploadResourceTask createUploadResource, final UploadAppToResourceTask uploadAppToResource, final CommitUploadResourceTask commitUploadResource, final DistributeResourceTask distributeResource, final UploadRequest request) {
        this.prerequisitesTask = prerequisitesTask;
        this.createUploadResource = createUploadResource;
        this.uploadAppToResource = uploadAppToResource;
        this.commitUploadResource = commitUploadResource;
        this.distributeResource = distributeResource;
        this.originalRequest = request;
    }

    @Override
    public Boolean call() {
        final CompletableFuture<Boolean> future = new CompletableFuture<>();

        prerequisitesTask.execute(originalRequest)
            .thenCompose(createUploadResource::execute)
            .thenCompose(uploadAppToResource::execute)
            .thenCompose(commitUploadResource::execute)
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