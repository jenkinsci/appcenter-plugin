package io.jenkins.plugins.appcenter.task;

import hudson.FilePath;
import hudson.model.TaskListener;
import io.jenkins.plugins.appcenter.AppCenterException;
import io.jenkins.plugins.appcenter.api.AppCenterServiceFactory;
import io.jenkins.plugins.appcenter.model.appcenter.DestinationId;
import io.jenkins.plugins.appcenter.model.appcenter.ReleaseDetailsUpdateRequest;
import io.jenkins.plugins.appcenter.model.appcenter.ReleaseDetailsUpdateResponse;
import io.jenkins.plugins.appcenter.model.appcenter.ReleaseUploadBeginResponse;
import io.jenkins.plugins.appcenter.model.appcenter.ReleaseUploadEndRequest;
import io.jenkins.plugins.appcenter.model.appcenter.ReleaseUploadEndResponse;
import io.jenkins.plugins.appcenter.model.appcenter.Status;
import io.jenkins.plugins.appcenter.task.request.UploadRequest;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class UploadTask extends AppCenterTask {

    private final FilePath filePath;
    private final TaskListener taskListener;
    private final UploadRequest request;

    public UploadTask(final FilePath filePath, final TaskListener taskListener, final AppCenterServiceFactory factory, final UploadRequest request) {
        super(factory);

        this.filePath = filePath;
        this.taskListener = taskListener;
        this.request = request;
    }

    @Override
    protected Boolean execute() throws ExecutionException, InterruptedException, AppCenterException, IOException {

        final FilePath remotablePath = filePath.child(request.pathToApp);
        if (!remotablePath.exists()) {
            throw new AppCenterException(String.format("File not found: %s", request.pathToApp));
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
        return appCenterService.releaseUploadBegin(request.ownerName, request.appName)
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

        final File file = new File(filePath.child(request.pathToApp).getRemote());
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
        return appCenterService.releaseUploadEnd(request.ownerName, request.appName, uploadId, releaseUploadEndRequest)
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
        final List<DestinationId> destinations = Stream.of(request.destinationGroups.split(","))
            .map(String::trim)
            .map(name -> new DestinationId(name, null))
            .collect(Collectors.toList());
        final boolean notifyTesters = false;
        final ReleaseDetailsUpdateRequest releaseDetailsUpdateRequest = new ReleaseDetailsUpdateRequest(releaseNotes, mandatoryUpdate, destinations, null, notifyTesters);

        return appCenterService.releaseDetailsUpdate(request.ownerName, request.appName, releaseId,
            releaseDetailsUpdateRequest)
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