package io.jenkins.plugins.appcenter.task.internal;

import hudson.model.TaskListener;
import io.jenkins.plugins.appcenter.AppCenterException;
import io.jenkins.plugins.appcenter.AppCenterLogger;
import io.jenkins.plugins.appcenter.api.AppCenterServiceFactory;
import io.jenkins.plugins.appcenter.model.appcenter.ReleaseUploadEndRequest;
import io.jenkins.plugins.appcenter.model.appcenter.Status;
import io.jenkins.plugins.appcenter.task.request.UploadRequest;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.PrintStream;
import java.util.concurrent.CompletableFuture;

@Singleton
public final class CommitUploadResourceTask implements AppCenterTask<UploadRequest>, AppCenterLogger {

    private static final long serialVersionUID = 1L;

    @Nonnull
    private final TaskListener taskListener;
    @Nonnull
    private final AppCenterServiceFactory factory;

    @Inject
    CommitUploadResourceTask(@Nonnull final TaskListener taskListener,
                             @Nonnull final AppCenterServiceFactory factory) {
        this.taskListener = taskListener;
        this.factory = factory;
    }

    @Nonnull
    @Override
    public CompletableFuture<UploadRequest> execute(@Nonnull UploadRequest request) {
        log("Committing resource.");

        final CompletableFuture<UploadRequest> future = new CompletableFuture<>();
        final ReleaseUploadEndRequest releaseUploadEndRequest = new ReleaseUploadEndRequest(Status.committed);

        factory.createAppCenterService()
            .releaseUploadEnd(request.ownerName, request.appName, request.uploadId, releaseUploadEndRequest)
            .whenComplete((releaseUploadBeginResponse, throwable) -> {
                if (throwable != null) {
                    final AppCenterException exception = logFailure("Committing resource unsuccessful: ", throwable);
                    future.completeExceptionally(exception);
                } else {
                    log("Committing resource successful.");
                    final UploadRequest uploadRequest = request.newBuilder()
                        .setReleaseId(releaseUploadBeginResponse.release_id)
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