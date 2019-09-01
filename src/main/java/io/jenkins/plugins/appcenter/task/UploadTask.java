package io.jenkins.plugins.appcenter.task;

import hudson.FilePath;
import hudson.model.TaskListener;
import io.jenkins.plugins.appcenter.AppCenterException;
import io.jenkins.plugins.appcenter.api.AppCenterServiceFactory;
import io.jenkins.plugins.appcenter.task.internal.CheckFileExistsTask;
import io.jenkins.plugins.appcenter.task.internal.CommitUploadResourceTask;
import io.jenkins.plugins.appcenter.task.internal.CreateUploadResourceTask;
import io.jenkins.plugins.appcenter.task.internal.DistributeResourceTask;
import io.jenkins.plugins.appcenter.task.internal.UploadAppToResourceTask;
import io.jenkins.plugins.appcenter.task.request.UploadRequest;
import jenkins.security.MasterToSlaveCallable;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public final class UploadTask extends MasterToSlaveCallable<Boolean, AppCenterException> {

    private final UploadRequest request;

    private final CheckFileExistsTask checkFileExists;
    private final CreateUploadResourceTask createUploadResource;
    private final UploadAppToResourceTask uploadAppToResource;
    private final CommitUploadResourceTask commitUploadResource;
    private final DistributeResourceTask distributeResource;

    public UploadTask(final FilePath filePath, final TaskListener taskListener, final AppCenterServiceFactory factory, final UploadRequest request) {
        this.request = request;

        // TODO: Introduce proper DI
        this.checkFileExists = new CheckFileExistsTask(filePath);
        this.createUploadResource = new CreateUploadResourceTask(taskListener, factory);
        this.uploadAppToResource = new UploadAppToResourceTask(taskListener, filePath, factory);
        this.commitUploadResource = new CommitUploadResourceTask(taskListener, factory);
        this.distributeResource = new DistributeResourceTask(taskListener, factory);
    }

    @Override
    public Boolean call() throws AppCenterException {
        final CompletableFuture<Boolean> future = new CompletableFuture<>();

        try {
            checkFileExists.execute(new CheckFileExistsTask.Request(request.pathToApp))
                .thenCompose(aVoid -> createUploadResource.execute(new CreateUploadResourceTask.Request(request.ownerName, request.appName)))
                .thenCompose(releaseUploadBeginResponse -> uploadAppToResource.execute(new UploadAppToResourceTask.Request(releaseUploadBeginResponse.upload_url, releaseUploadBeginResponse.upload_id, request.pathToApp)))
                .thenCompose(uploadId -> commitUploadResource.execute(new CommitUploadResourceTask.Request(request.ownerName, request.appName, uploadId)))
                .thenCompose(releaseUploadEndResponse -> distributeResource.execute(new DistributeResourceTask.Request(request.ownerName, request.appName, request.destinationGroups, releaseUploadEndResponse.release_id)))
                .whenComplete((releaseDetailsUpdateResponse, throwable) -> {
                    if (throwable != null) {
                        future.complete(false);
                    }

                    future.complete(true);
                })
                .get();
        } catch (InterruptedException | ExecutionException e) {
            throw new AppCenterException("Upload to AppCenter failed.");
        }

        return future.join();
    }
}