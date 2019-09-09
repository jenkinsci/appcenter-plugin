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
import javax.inject.Singleton;
import java.io.File;
import java.io.PrintStream;
import java.util.concurrent.CompletableFuture;

import static io.jenkins.plugins.appcenter.task.internal.UploadAppToResourceTask.Request;

@Singleton
public final class UploadAppToResourceTask implements AppCenterTask<Request, String> {

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
    public CompletableFuture<String> execute(@Nonnull Request request) {
        final PrintStream logger = taskListener.getLogger();
        logger.println("Uploading app to resource.");

        final CompletableFuture<String> future = new CompletableFuture<>();

        final File file = new File(filePath.child(request.pathToApp).getRemote());
        final RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        final MultipartBody.Part body = MultipartBody.Part.createFormData("ipa", file.getName(), requestFile);

        factory.createUploadService(request.uploadUrl)
            .uploadApp(request.uploadUrl, body)
            .whenComplete((responseBody, throwable) -> {
                if (throwable != null) {
                    final AppCenterException exception = new AppCenterException("Upload app to resource unsuccessful: ", throwable);
                    logger.println(exception.getMessage());
                    future.completeExceptionally(exception);
                }

                logger.println("Upload app to resource successful.");
                future.complete(request.uploadId);
            });

        return future;
    }

    public static class Request {
        @Nonnull
        private final String uploadUrl;
        @Nonnull
        private final String uploadId;
        @Nonnull
        private final String pathToApp;

        public Request(@Nonnull final String uploadUrl,
                       @Nonnull final String uploadId,
                       @Nonnull final String pathToApp) {
            this.uploadUrl = uploadUrl;
            this.uploadId = uploadId;
            this.pathToApp = pathToApp;
        }
    }
}