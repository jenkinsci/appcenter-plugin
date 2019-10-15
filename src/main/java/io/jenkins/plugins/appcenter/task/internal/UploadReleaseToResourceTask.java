package io.jenkins.plugins.appcenter.task.internal;

import hudson.FilePath;
import hudson.model.TaskListener;
import io.jenkins.plugins.appcenter.AppCenterException;
import io.jenkins.plugins.appcenter.api.AppCenterServiceFactory;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.io.File;
import java.io.PrintStream;
import java.util.concurrent.CompletableFuture;

public final class UploadReleaseToResourceTask extends UploadToResourceTask {

    @Inject
    UploadReleaseToResourceTask(@Nonnull TaskListener taskListener, @Nonnull FilePath filePath, @Nonnull AppCenterServiceFactory factory) {
        super(taskListener, filePath, factory);
    }

    @Nonnull
    @Override
    public CompletableFuture<String> execute(@Nonnull Request request) {
        final PrintStream logger = taskListener.getLogger();
        logger.println("Uploading file.");

        final CompletableFuture<String> future = new CompletableFuture<>();

        final File file = new File(filePath.child(request.pathToApp).getRemote());

        final RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        final MultipartBody.Part body = MultipartBody.Part.createFormData("ipa", file.getName(), requestFile);

        factory.createUploadService(request.uploadUrl)
            .uploadApp(request.uploadUrl, body)
            .whenComplete((responseBody, throwable) -> {
                if (throwable != null) {
                    final AppCenterException exception = new AppCenterException("Uploading file unsuccessful: ", throwable);

                    tryLoggingErrorBody(logger, throwable);

                    exception.printStackTrace(logger);

                    future.completeExceptionally(exception);
                } else {
                    logger.println("Upload app to resource successful.");
                    future.complete(request.uploadId);
                }
            });

        return future;
    }
}
