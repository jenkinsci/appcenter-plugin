package io.jenkins.plugins.appcenter.task.internal;

import hudson.model.TaskListener;
import io.jenkins.plugins.appcenter.AppCenterException;
import io.jenkins.plugins.appcenter.api.AppCenterServiceFactory;
import io.jenkins.plugins.appcenter.model.appcenter.ReleaseUploadEndResponse;
import io.jenkins.plugins.appcenter.model.appcenter.Status;
import io.jenkins.plugins.appcenter.model.appcenter.UploadEndRequest;

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
        final UploadEndRequest releaseUploadEndRequest = new UploadEndRequest(Status.COMMITTED);

        factory.createAppCenterService()
            .releaseUploadEnd(request.ownerName, request.appName, request.uploadId, releaseUploadEndRequest)
            .whenComplete((releaseUploadEndResponse, throwable) -> {
                if (throwable != null) {
                    final AppCenterException exception = new AppCenterException("Committing resource unsuccessful: ", throwable);
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
