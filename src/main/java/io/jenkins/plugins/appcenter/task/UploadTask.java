package io.jenkins.plugins.appcenter.task;

import io.jenkins.plugins.appcenter.AppCenterException;
import io.jenkins.plugins.appcenter.task.internal.CheckFileExistsTask;
import io.jenkins.plugins.appcenter.task.internal.CommitUploadResourceTask;
import io.jenkins.plugins.appcenter.task.internal.CreateUploadResourceTask;
import io.jenkins.plugins.appcenter.task.internal.DistributeResourceTask;
import io.jenkins.plugins.appcenter.task.internal.UploadAppToResourceTask;
import io.jenkins.plugins.appcenter.task.request.UploadRequest;
import jenkins.security.MasterToSlaveCallable;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Singleton
public final class UploadTask extends MasterToSlaveCallable<Boolean, AppCenterException> {

    private final CheckFileExistsTask checkFileExists;
    private final CreateUploadResourceTask createUploadResource;
    private final UploadAppToResourceTask uploadAppToResource;
    private final CommitUploadResourceTask commitUploadResource;
    private final DistributeResourceTask distributeResource;
    private final UploadRequest request;

    @Inject
    UploadTask(final CheckFileExistsTask checkFileExists, final CreateUploadResourceTask createUploadResource, final UploadAppToResourceTask uploadAppToResource, final CommitUploadResourceTask commitUploadResource, final DistributeResourceTask distributeResource, final UploadRequest request) {
        this.checkFileExists = checkFileExists;
        this.createUploadResource = createUploadResource;
        this.uploadAppToResource = uploadAppToResource;
        this.commitUploadResource = commitUploadResource;
        this.distributeResource = distributeResource;
        this.request = request;
    }

    @Override
    public Boolean call() throws AppCenterException {
        final CompletableFuture<Boolean> future = new CompletableFuture<>();

        try {
            checkFileExists.execute(new CheckFileExistsTask.Request(request.pathToApp))
                .thenCompose(result -> createUploadResource.execute(new CreateUploadResourceTask.Request(request.ownerName, request.appName)))
                .thenCompose(releaseUploadBeginResponse -> uploadAppToResource.execute(new UploadAppToResourceTask.Request(releaseUploadBeginResponse.upload_url, releaseUploadBeginResponse.upload_id, request.pathToApp)))
                .thenCompose(uploadId -> commitUploadResource.execute(new CommitUploadResourceTask.Request(request.ownerName, request.appName, uploadId)))
                .thenCompose(releaseUploadEndResponse -> distributeResource.execute(new DistributeResourceTask.Request(request.ownerName, request.appName, request.destinationGroups, request.releaseNotes, releaseUploadEndResponse.release_id)))
                .whenComplete((releaseDetailsUpdateResponse, throwable) -> {
                    if (throwable != null) {
                        future.complete(false);
                    } else {
                        future.complete(true);
                    }
                })
                .get();
        } catch (InterruptedException | ExecutionException e) {
            throw new AppCenterException("Upload to AppCenter failed.", e);
        }

        return future.join();
    }
}