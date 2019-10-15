package io.jenkins.plugins.appcenter.task.internal;

import hudson.model.TaskListener;
import io.jenkins.plugins.appcenter.AppCenterException;
import io.jenkins.plugins.appcenter.api.AppCenterServiceFactory;
import io.jenkins.plugins.appcenter.model.appcenter.*;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.io.PrintStream;
import java.util.concurrent.CompletableFuture;

public final class CommitReleaseUploadResourceTask extends CommitUploadResourceTask<CommitUploadResourceTask.Request, ReleaseUploadEndResponse> {

    @Inject
    CommitReleaseUploadResourceTask(@Nonnull TaskListener taskListener, @Nonnull AppCenterServiceFactory factory) {
        super(taskListener, factory);
    }

    @Nonnull
    @Override
    public CompletableFuture<ReleaseUploadEndResponse> execute(@Nonnull Request request) {
        final PrintStream logger = taskListener.getLogger();
        logger.println("Committing resource.");

        final CompletableFuture<ReleaseUploadEndResponse> future = new CompletableFuture<>();
        final UploadEndRequest releaseUploadEndRequest = new UploadEndRequest(Status.committed);

        factory.createAppCenterService()
            .releaseUploadEnd(request.ownerName, request.appName, request.uploadId, releaseUploadEndRequest)
            .whenComplete((releaseUploadEndResponse, throwable) -> {
                if (throwable != null) {
                    final AppCenterException exception = new AppCenterException("Committing resource unsuccessful: ", throwable);
                    logger.println(throwable.getMessage());
                    exception.printStackTrace(logger);
                    future.completeExceptionally(exception);
                } else {
                    logger.println("Committing resource successful.");
                    future.complete(releaseUploadEndResponse);
                }
            });

        return future;
    }
}
