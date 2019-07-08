package io.jenkins.plugins.appcenter.task;

import hudson.FilePath;
import hudson.model.TaskListener;
import io.jenkins.plugins.appcenter.AppCenterException;
import io.jenkins.plugins.appcenter.remote.AppCenterServiceFactory;
import io.jenkins.plugins.appcenter.remote.DestinationId;
import io.jenkins.plugins.appcenter.remote.ReleaseDetailsUpdateRequest;
import io.jenkins.plugins.appcenter.remote.ReleaseDetailsUpdateResponse;
import io.jenkins.plugins.appcenter.remote.ReleaseUploadBeginResponse;
import io.jenkins.plugins.appcenter.remote.ReleaseUploadEndRequest;
import io.jenkins.plugins.appcenter.remote.ReleaseUploadEndResponse;
import io.jenkins.plugins.appcenter.remote.Status;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public final class UploadTask extends AppCenterTask {

    private final TaskListener taskListener;
    private final String ownerName;
    private final String appName;
    private final String distributionGroup;
    private final FilePath pathToApp;

    public UploadTask(final FilePath filePath, final TaskListener taskListener, final AppCenterServiceFactory factory) {
        super(factory);

        this.taskListener = taskListener;
        this.ownerName = factory.getOwnerName();
        this.appName = factory.getAppName();
        this.distributionGroup = factory.getDistributionGroup();
        this.pathToApp = filePath.child(factory.getPathToApp());
    }

    @Override
    protected Boolean execute() throws ExecutionException, InterruptedException, AppCenterException, IOException {

        if (!pathToApp.exists()) {
            throw new AppCenterException(String.format("File not found: %s", pathToApp.getRemote()));
        }

        return createUploadResource()
                .thenCompose(releaseUploadBeginResponse -> uploadAppToResource(releaseUploadBeginResponse.upload_url, releaseUploadBeginResponse.upload_id))
                .thenCompose(this::commitUploadResource)
                .thenCompose(releaseUploadEndResponse -> distributeResource(releaseUploadEndResponse.release_id))
                .thenCompose(releaseDetailsUpdateResponse -> CompletableFuture.completedFuture(true))
                .get();
    }

    private CompletableFuture<ReleaseUploadBeginResponse> createUploadResource() {
        final PrintStream logger = taskListener.getLogger();
        logger.println("Creating an upload resource.");

        // TODO: Pass in the release_id as an optional parameter from the UI. Don't use it if  not available
        //  final ReleaseUploadBeginRequest releaseUploadBeginRequest = new ReleaseUploadBeginRequest(upload.getReleaseId());
        //  using the overloaded releaseUploadBegin method.
        return appCenterService.releaseUploadBegin(ownerName, appName)
                .whenComplete((releaseUploadBeginResponse, throwable) -> {
                    if (throwable != null) {
                        logger.println("Upload resource unsuccessful.");
                        logger.println(throwable);
                    } else {
                        logger.println("Upload resource successful.");
                    }
                });
    }

    private CompletableFuture<String> uploadAppToResource(@Nonnull final String uploadUrl, @Nonnull final String uploadId) {
        final PrintStream logger = taskListener.getLogger();
        logger.println("Uploading app to resource.");

        final File file = new File(pathToApp.getRemote());
        final RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        final MultipartBody.Part body = MultipartBody.Part.createFormData("ipa", file.getName(), requestFile);

        return uploadService.uploadApp(uploadUrl, body)
                .whenComplete((responseBody, throwable) -> {
                    if (throwable != null) {
                        logger.println("Upload app unsuccessful.");
                        logger.println(throwable);
                    } else {
                        logger.println("Upload app successful.");
                    }
                })
                .thenCompose(aVoid -> CompletableFuture.completedFuture(uploadId));
    }

    private CompletableFuture<ReleaseUploadEndResponse> commitUploadResource(@Nonnull final String uploadId) {
        final PrintStream logger = taskListener.getLogger();
        logger.println("Committing resource.");

        final ReleaseUploadEndRequest releaseUploadEndRequest = new ReleaseUploadEndRequest(Status.committed);
        return appCenterService.releaseUploadEnd(ownerName, appName, uploadId, releaseUploadEndRequest)
                .whenComplete((releaseUploadBeginResponse, throwable) -> {
                    if (throwable != null) {
                        logger.println("Committing resource unsuccessful.");
                        logger.println(throwable);
                    } else {
                        logger.println("Committing resource successful.");
                    }
                });
    }

    private CompletableFuture<ReleaseDetailsUpdateResponse> distributeResource(final int releaseId) {
        final PrintStream logger = taskListener.getLogger();
        logger.println("Distributing resource.");

        final String releaseNotes = "";
        final boolean mandatoryUpdate = false;
        final List<DestinationId> destinations = Collections.singletonList(new DestinationId(distributionGroup, null));
        final boolean notifyTesters = false;
        final ReleaseDetailsUpdateRequest releaseDetailsUpdateRequest = new ReleaseDetailsUpdateRequest(releaseNotes, mandatoryUpdate, destinations, null, notifyTesters);

        return appCenterService.releaseDetailsUpdate(ownerName, appName, releaseId, releaseDetailsUpdateRequest)
                .whenComplete((releaseUploadBeginResponse, throwable) -> {
                    if (throwable != null) {
                        logger.println("Distributing resource unsuccessful.");
                        logger.println(throwable);
                    } else {
                        logger.println("Distributing resource successful.");
                    }
                });
    }
}
