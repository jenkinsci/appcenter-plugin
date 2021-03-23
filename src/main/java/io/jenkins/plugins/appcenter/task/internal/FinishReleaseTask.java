package io.jenkins.plugins.appcenter.task.internal;

import hudson.model.TaskListener;
import io.jenkins.plugins.appcenter.AppCenterException;
import io.jenkins.plugins.appcenter.AppCenterLogger;
import io.jenkins.plugins.appcenter.api.AppCenterServiceFactory;
import io.jenkins.plugins.appcenter.model.appcenter.SymbolUploadEndRequest;
import io.jenkins.plugins.appcenter.task.request.UploadRequest;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.PrintStream;
import java.util.concurrent.CompletableFuture;

import static java.util.Objects.requireNonNull;

@Singleton
public final class FinishReleaseTask implements AppCenterTask<UploadRequest>, AppCenterLogger {

    private static final long serialVersionUID = 1L;

    @Nonnull
    private final TaskListener taskListener;
    @Nonnull
    private final AppCenterServiceFactory factory;

    @Inject
    FinishReleaseTask(@Nonnull final TaskListener taskListener,
                      @Nonnull final AppCenterServiceFactory factory) {
        this.taskListener = taskListener;
        this.factory = factory;
    }

    @Nonnull
    @Override
    public CompletableFuture<UploadRequest> execute(@Nonnull UploadRequest request) {


        if (request.symbolUploadId == null) {
            return finishRelease(request);
        } else {
            return finishRelease(request)
                .thenCompose(this::finishSymbolRelease);
        }
    }

    @Nonnull
    private CompletableFuture<UploadRequest> finishRelease(@Nonnull UploadRequest request) {
        final String uploadDomain = requireNonNull(request.uploadDomain, "uploadDomain cannot be null");
        final String packageAssetId = requireNonNull(request.packageAssetId, "packageAssetId cannot be null");
        final String token = requireNonNull(request.token, "token cannot be null");

        log("Finishing release.");

        final CompletableFuture<UploadRequest> future = new CompletableFuture<>();

        final String url = getUrl(uploadDomain, packageAssetId, token);

        factory.createAppCenterService()
            .finishRelease(url)
            .whenComplete((finishReleaseResponse, throwable) -> {
                if (throwable != null) {
                    final AppCenterException exception = logFailure("Finishing release unsuccessful", throwable);
                    future.completeExceptionally(exception);
                } else {
                    log("Finishing release successful.");
                    future.complete(request);
                }
            });

        return future;
    }

    @Nonnull
    private String getUrl(@Nonnull String uploadDomain, @Nonnull String packageAssetId, @Nonnull String token) {
        return String.format("%1$s/upload/finished/%2$s?token=%3$s", uploadDomain, packageAssetId, token);
    }

    @Nonnull
    private CompletableFuture<UploadRequest> finishSymbolRelease(@Nonnull UploadRequest request) {
        final String symbolUploadId = requireNonNull(request.symbolUploadId, "symbolUploadId cannot be null");

        log("Finishing symbol release.");

        final CompletableFuture<UploadRequest> future = new CompletableFuture<>();
        final SymbolUploadEndRequest symbolUploadEndRequest = new SymbolUploadEndRequest(SymbolUploadEndRequest.StatusEnum.committed);

        factory.createAppCenterService()
            .symbolUploadsComplete(request.ownerName, request.appName, symbolUploadId, symbolUploadEndRequest)
            .whenComplete((symbolUploadEndResponse, throwable) -> {
                if (throwable != null) {
                    final AppCenterException exception = logFailure("Finishing symbol release unsuccessful: ", throwable);
                    future.completeExceptionally(exception);
                } else {
                    log("Finishing symbol release successful.");
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