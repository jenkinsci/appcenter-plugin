package io.jenkins.plugins.appcenter.task.internal;

import hudson.model.TaskListener;
import io.jenkins.plugins.appcenter.AppCenterException;
import io.jenkins.plugins.appcenter.AppCenterLogger;
import io.jenkins.plugins.appcenter.api.AppCenterServiceFactory;
import io.jenkins.plugins.appcenter.model.appcenter.SymbolUploadBeginRequest;
import io.jenkins.plugins.appcenter.task.request.UploadRequest;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.PrintStream;
import java.util.concurrent.CompletableFuture;

@Singleton
public final class CreateUploadResourceTask implements AppCenterTask<UploadRequest>, AppCenterLogger {

    private static final long serialVersionUID = 1L;

    @Nonnull
    private final TaskListener taskListener;
    @Nonnull
    private final AppCenterServiceFactory factory;

    @Inject
    CreateUploadResourceTask(@Nonnull final TaskListener taskListener,
                             @Nonnull final AppCenterServiceFactory factory) {
        this.taskListener = taskListener;
        this.factory = factory;
    }

    @Nonnull
    @Override
    public CompletableFuture<UploadRequest> execute(@Nonnull UploadRequest request) {
        if (request.symbolUploadRequest == null) {
            return createUploadResourceForApp(request);
        } else {
            return createUploadResourceForApp(request)
                .thenCompose(this::createUploadResourceForDebugSymbols);
        }
    }

    @Nonnull
    private CompletableFuture<UploadRequest> createUploadResourceForApp(@Nonnull UploadRequest request) {
        log("Creating an upload resource for app.");

        final CompletableFuture<UploadRequest> future = new CompletableFuture<>();

        // TODO: Pass in the release_id as an optional parameter from the UI. Don't use it if  not available
        //  final ReleaseUploadBeginRequest releaseUploadBeginRequest = new ReleaseUploadBeginRequest(upload.getReleaseId());
        //  using the overloaded releaseUploadBegin method.
        factory.createAppCenterService()
            .releaseUploadBegin(request.ownerName, request.appName)
            .whenComplete((releaseUploadBeginResponse, throwable) -> {
                if (throwable != null) {
                    final AppCenterException exception = logFailure("Create upload resource for app unsuccessful: ", throwable);
                    future.completeExceptionally(exception);
                } else {
                    log("Create upload resource for app successful.");
                    final UploadRequest uploadRequest = request.newBuilder()
                        .setUploadUrl(releaseUploadBeginResponse.upload_url)
                        .setUploadId(releaseUploadBeginResponse.upload_id)
                        .build();
                    future.complete(uploadRequest);
                }
            });

        return future;
    }

    @Nonnull
    private CompletableFuture<UploadRequest> createUploadResourceForDebugSymbols(@Nonnull UploadRequest request) {
        log("Creating an upload resource for debug symbols.");

        final CompletableFuture<UploadRequest> future = new CompletableFuture<>();

        final SymbolUploadBeginRequest symbolUploadRequest = request.symbolUploadRequest;

        factory.createAppCenterService()
            .symbolUploadBegin(request.ownerName, request.appName, symbolUploadRequest)
            .whenComplete((symbolsUploadBeginResponse, throwable) -> {
                if (throwable != null) {
                    final AppCenterException exception = logFailure("Create upload resource for debug symbols unsuccessful: ", throwable);
                    future.completeExceptionally(exception);
                } else {
                    log("Create upload resource for debug symbols successful.");
                    final UploadRequest uploadRequest = request.newBuilder()
                        .setSymbolUploadUrl(symbolsUploadBeginResponse.upload_url)
                        .setSymbolUploadId(symbolsUploadBeginResponse.symbol_upload_id)
                        .build();
                    future.complete(uploadRequest);
                }
            });

        return future;
    }

    @Override
    public PrintStream getLogger() {
        return taskListener.getLogger();
    }
}