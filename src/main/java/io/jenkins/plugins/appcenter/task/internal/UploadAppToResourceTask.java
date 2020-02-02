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

import static java.util.Objects.requireNonNull;

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
        if (request.symbolUploadUrl == null) {
            return uploadApp(request);
        } else {
            return uploadApp(request)
                .thenCompose(this::uploadSymbols);
        }
    }

    @Nonnull
    private CompletableFuture<UploadRequest> uploadApp(@Nonnull UploadRequest request) {
        final String pathToApp = request.pathToApp;
        final String uploadUrl = requireNonNull(request.uploadUrl, "uploadUrl cannot be null");

        log("Uploading app to resource.");

        final CompletableFuture<UploadRequest> future = new CompletableFuture<>();

        final File file = new File(filePath.child(pathToApp).getRemote());
        final RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        final MultipartBody.Part body = MultipartBody.Part.createFormData("ipa", file.getName(), requestFile);

        factory.createUploadService(uploadUrl)
            .uploadApp(uploadUrl, body)
            .whenComplete((responseBody, throwable) -> {
                if (throwable != null) {
                    final AppCenterException exception = logFailure("Upload app to resource unsuccessful", throwable);
                    future.completeExceptionally(exception);
                } else {
                    log("Upload app to resource successful.");
                    future.complete(request);
                }
            });

        return future;
    }

    @Nonnull
    private CompletableFuture<UploadRequest> uploadSymbols(@Nonnull UploadRequest request) {
        final String pathToDebugSymbols = request.pathToDebugSymbols;
        final String symbolUploadUrl = requireNonNull(request.symbolUploadUrl, "symbolUploadUrl cannot be null");

        log("Uploading symbols to resource.");

        final CompletableFuture<UploadRequest> future = new CompletableFuture<>();

        final File file = new File(filePath.child(pathToDebugSymbols).getRemote());
        final RequestBody requestFile = RequestBody.create(null, file);

        factory.createUploadService(symbolUploadUrl)
            .uploadSymbols(symbolUploadUrl, requestFile)
            .whenComplete((responseBody, throwable) -> {
                if (throwable != null) {
                    final AppCenterException exception = logFailure("Upload symbols to resource unsuccessful: ", throwable);
                    future.completeExceptionally(exception);
                } else {
                    log("Upload symbols to resource successful.");
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