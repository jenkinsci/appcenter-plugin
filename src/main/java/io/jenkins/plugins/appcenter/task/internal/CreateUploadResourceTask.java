package io.jenkins.plugins.appcenter.task.internal;

import hudson.model.TaskListener;
import io.jenkins.plugins.appcenter.AppCenterException;
import io.jenkins.plugins.appcenter.AppCenterLogger;
import io.jenkins.plugins.appcenter.api.AppCenterServiceFactory;
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
        log("Creating an upload resource.");

        final CompletableFuture<UploadRequest> future = new CompletableFuture<>();

        // TODO: Pass in the release_id as an optional parameter from the UI. Don't use it if  not available
        //  final ReleaseUploadBeginRequest releaseUploadBeginRequest = new ReleaseUploadBeginRequest(upload.getReleaseId());
        //  using the overloaded releaseUploadBegin method.
        factory.createAppCenterService()
            .releaseUploadBegin(request.ownerName, request.appName)
            .whenComplete((releaseUploadBeginResponse, throwable) -> {
                if (throwable != null) {
                    final AppCenterException exception = logFailure("Create upload resource unsuccessful: ", throwable);
                    future.completeExceptionally(exception);
                } else {
                    log("Create upload resource successful.");
                    final UploadRequest uploadRequest = request.newBuilder()
                        .setUploadUrl(releaseUploadBeginResponse.upload_url)
                        .setUploadId(releaseUploadBeginResponse.upload_id)
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