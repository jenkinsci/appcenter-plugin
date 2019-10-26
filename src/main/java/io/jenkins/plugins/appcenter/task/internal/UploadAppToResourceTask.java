package io.jenkins.plugins.appcenter.task.internal;

import hudson.FilePath;
import hudson.model.TaskListener;
import io.jenkins.plugins.appcenter.AppCenterException;
import io.jenkins.plugins.appcenter.AppCenterLogger;
import io.jenkins.plugins.appcenter.api.AppCenterServiceFactory;
import io.jenkins.plugins.appcenter.task.request.UploadRequest;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.File;
import java.io.PrintStream;
import java.util.concurrent.CompletableFuture;

@Singleton
public final class UploadAppToResourceTask implements AppCenterTask<UploadRequest>, AppCenterLogger {

    private static final long serialVersionUID = 1L;

    @Nonnull
    private final TaskListener taskListener;
    @Nonnull
    private final FilePath filePath;
    @Nonnull
    private final AppCenterServiceFactory factory;

    @Inject
    UploadAppToResourceTask(@Nonnull final TaskListener taskListener,
                            @Nonnull final FilePath filePath,
                            @Nonnull final AppCenterServiceFactory factory) {
        this.taskListener = taskListener;
        this.filePath = filePath;
        this.factory = factory;
    }

    @Nonnull
    @Override
    public CompletableFuture<UploadRequest> execute(@Nonnull UploadRequest request) {
        log("Uploading app to resource.");

        final CompletableFuture<UploadRequest> future = new CompletableFuture<>();

        final String pathToApp = request.pathToApp;
        final String uploadUrl = request.uploadUrl;


        if (uploadUrl == null) {
            final AppCenterException exception = logFailure("uploadUrl cannot be null");
            future.completeExceptionally(exception);
            return future;
        }

        final File file = new File(filePath.child(pathToApp).getRemote());
        final RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        final MultipartBody.Part body = MultipartBody.Part.createFormData("ipa", file.getName(), requestFile);

        factory.createUploadService(uploadUrl)
            .uploadApp(uploadUrl, body)
            .whenComplete((responseBody, throwable) -> {
                if (throwable != null) {
                    final AppCenterException exception = logFailure("Upload app to resource unsuccessful: ", throwable);
                    future.completeExceptionally(exception);
                } else {
                    log("Upload app to resource successful.");
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