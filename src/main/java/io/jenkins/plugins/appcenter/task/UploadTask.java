package io.jenkins.plugins.appcenter.task;

import io.jenkins.plugins.appcenter.AppCenterException;
import io.jenkins.plugins.appcenter.task.internal.*;
import io.jenkins.plugins.appcenter.task.request.UploadRequest;
import jenkins.security.MasterToSlaveCallable;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Singleton
public final class UploadTask extends MasterToSlaveCallable<Boolean, AppCenterException> {

    private final CheckFileExistsTask checkFileExists;

    private final UploadToResourceTask uploadAppToResource;

    private final CreateReleaseUploadResourceTask createReleaseUploadResource;
    private final CommitReleaseUploadResourceTask commitReleaseUploadResource;

    private final CreateSymbolsUploadResourceTask createSymbolsUploadResource;
    private final CommitSymbolsUploadResourceTask commitSymbolsUploadResource;

    private final DistributeResourceTask distributeResource;
    private final UploadRequest request;

    @Inject
    UploadTask(final CheckFileExistsTask checkFileExists, final CreateReleaseUploadResourceTask createReleaseUploadResource, final UploadToResourceTask uploadAppToResource, final CommitReleaseUploadResourceTask commitReleaseUploadResource, CreateSymbolsUploadResourceTask createSymbolsUploadResource, CommitSymbolsUploadResourceTask commitSymbolsUploadResource, final DistributeResourceTask distributeResource, final UploadRequest request) {
        this.checkFileExists = checkFileExists;
        this.createReleaseUploadResource = createReleaseUploadResource;
        this.uploadAppToResource = uploadAppToResource;
        this.commitReleaseUploadResource = commitReleaseUploadResource;
        this.createSymbolsUploadResource = createSymbolsUploadResource;
        this.commitSymbolsUploadResource = commitSymbolsUploadResource;
        this.distributeResource = distributeResource;
        this.request = request;
    }

    @Override
    public Boolean call() throws AppCenterException {
        final CompletableFuture<Boolean> future = new CompletableFuture<>();

        try {
            uploadRelease(future);
        } catch (InterruptedException | ExecutionException e) {
            throw new AppCenterException("Upload to AppCenter failed.", e);
        }

        return future.join();
    }

    private void uploadRelease(CompletableFuture<Boolean> future) throws ExecutionException, InterruptedException {
        checkFileExists.execute(new CheckFileExistsTask.Request(request.pathToApp))
            .thenCompose(result -> createReleaseUploadResource.execute(new CreateReleaseUploadResourceTask.Request(request.ownerName, request.appName)))
            .thenCompose(releaseUploadBeginResponse -> uploadAppToResource.execute(new UploadToResourceTask.Request(releaseUploadBeginResponse.upload_url, releaseUploadBeginResponse.upload_id, request.pathToApp)))
            .thenCompose(uploadId -> commitReleaseUploadResource.execute(new CommitUploadResourceTask.Request(request.ownerName, request.appName, uploadId)))
            .thenCompose(releaseUploadEndResponse -> distributeResource.execute(new DistributeResourceTask.Request(request.ownerName, request.appName, request.destinationGroups, request.releaseNotes, request.notifyTesters, releaseUploadEndResponse.release_id)))
            .whenComplete((releaseDetailsUpdateResponse, throwable) -> continueUploadingSymbolsIfAppUploaded(future, throwable))
            .get();
    }

    private void continueUploadingSymbolsIfAppUploaded(CompletableFuture<Boolean> future, Throwable releaseUploadThrowable) {

        if (releaseUploadThrowable != null) {
            future.complete(false);
            return;
        }

        // TODO: only apple supported for debug symbol upload for now
        // redundant check for now, but if more than apple will be supported, the check for an ipa ending can be removed
        if(!request.pathToApp.endsWith(".ipa") || request.pathToDebugSymbols.equals("")) {
            future.complete(true);
            return;
        }

        try {
            uploadSymbols(future);
        } catch (InterruptedException | ExecutionException e) {

            // as the release has been uploaded successfully at this point and the symbol upload is optional, we just log that there was an issue with the symbol at this point
            future.complete(true);
        }
    }

    private void uploadSymbols(CompletableFuture<Boolean> future) throws ExecutionException, InterruptedException {
        checkFileExists.execute(new CheckFileExistsTask.Request(request.pathToDebugSymbols))
            .thenCompose(result -> createSymbolsUploadResource.execute(new CreateSymbolsUploadResourceTask.Request(request.ownerName, request.appName)))
            .thenCompose(symbolsUploadBeginResponse -> uploadAppToResource.execute(new UploadToResourceTask.Request(symbolsUploadBeginResponse.upload_url, symbolsUploadBeginResponse.symbol_upload_id, request.pathToDebugSymbols)))
            .thenCompose(uploadId -> commitSymbolsUploadResource.execute(new CommitUploadResourceTask.Request(request.ownerName, request.appName, uploadId)))
            .whenComplete((symbolsUploadEndResponse, throwable) -> {
                // as the symbols upload is optional, we just complete here with a success
                future.complete(true);
            })
            .get();
    }
}