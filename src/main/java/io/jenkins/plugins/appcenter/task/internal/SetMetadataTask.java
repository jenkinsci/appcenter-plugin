package io.jenkins.plugins.appcenter.task.internal;

import hudson.model.TaskListener;
import io.jenkins.plugins.appcenter.AppCenterException;
import io.jenkins.plugins.appcenter.AppCenterLogger;
import io.jenkins.plugins.appcenter.api.AppCenterServiceFactory;
import io.jenkins.plugins.appcenter.task.request.UploadRequest;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.File;
import java.io.PrintStream;
import java.util.concurrent.CompletableFuture;

@Singleton
public final class SetMetadataTask implements AppCenterTask<UploadRequest>, AppCenterLogger {

    private static final long serialVersionUID = 1L;

    @Nonnull
    private final TaskListener taskListener;
    @Nonnull
    private final AppCenterServiceFactory factory;

    @Inject
    SetMetadataTask(@Nonnull final TaskListener taskListener,
                    @Nonnull final AppCenterServiceFactory factory) {
        this.taskListener = taskListener;
        this.factory = factory;
    }

    @Nonnull
    @Override
    public CompletableFuture<UploadRequest> execute(@Nonnull UploadRequest request) {
        return setMetadata(request);
    }

    @Nonnull
    private CompletableFuture<UploadRequest> setMetadata(@Nonnull UploadRequest request) {
        log("Setting metadata.");

        final CompletableFuture<UploadRequest> future = new CompletableFuture<>();

        final String url = getUrl(request);

        factory.createAppCenterService()
            .setMetaData(url)
            .whenComplete((setMetadataResponse, throwable) -> {
                if (throwable != null) {
                    final AppCenterException exception = logFailure("Setting metadata unsuccessful", throwable);
                    future.completeExceptionally(exception);
                } else {
                    log("Setting metadata successful.");

                    final UploadRequest uploadRequest = request.newBuilder()
                        .setChunkSize(setMetadataResponse.chunk_size)
                        .build();
                    future.complete(uploadRequest);
                }
            });

        return future;
    }

    @Nonnull
    private String getUrl(@Nonnull UploadRequest request) {
        final File file = new File(request.pathToApp);
        final String fileName = getFileName(file);
        final long fileSize = getFileSize(file);
        final String contentType = getContentType(request.pathToApp);

        return String.format("%1$s/upload/set_metadata/%2$s?file_name=%3$s&file_size=%4$d&token=%5$s&content_type=%6$s", request.uploadDomain, request.packageAssetId, fileName, fileSize, request.token, contentType);
    }

    @Nonnull
    private String getFileName(@Nonnull File file) {
        // TODO: Move to Prerequisite Task
        return file.getName();
    }

    private long getFileSize(@Nonnull File file) {
        // TODO: Move to Prerequisite Task
        return file.length();
    }

    @Nonnull
    private String getContentType(@Nonnull String pathToApp) {
        // TODO: Move to Prerequisite Task
        if (pathToApp.endsWith(".apk") || pathToApp.endsWith(".aab")) return "application/vnd.android.package-archive";
        if (pathToApp.endsWith(".msi")) return "application/x-msi";
        if (pathToApp.endsWith(".plist")) return "application/xml";
        if (pathToApp.endsWith(".aetx")) return "application/c-x509-ca-cert";
        if (pathToApp.endsWith(".cer")) return "application/pkix-cert";
        if (pathToApp.endsWith("xap")) return "application/x-silverlight-app";
        if (pathToApp.endsWith(".appx")) return "application/x-appx";
        if (pathToApp.endsWith(".appxbundle")) return "application/x-appxbundle";
        if (pathToApp.endsWith(".appxupload") || pathToApp.endsWith(".appxsym")) return "application/x-appxupload";
        if (pathToApp.endsWith(".msix")) return "application/x-msix";
        if (pathToApp.endsWith(".msixbundle")) return "application/x-msixbundle";
        if (pathToApp.endsWith(".msixupload") || pathToApp.endsWith(".msixsym")) return "application/x-msixupload";

        // Otherwise
        return "application/octet-stream";
    }

    @Override
    public PrintStream getLogger() {
        return taskListener.getLogger();
    }
}