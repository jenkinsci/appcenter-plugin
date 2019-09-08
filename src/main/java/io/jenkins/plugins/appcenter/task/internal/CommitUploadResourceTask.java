package io.jenkins.plugins.appcenter.task.internal;

import io.jenkins.plugins.appcenter.AppCenterException;
import io.jenkins.plugins.appcenter.api.AppCenterServiceFactory;
import io.jenkins.plugins.appcenter.model.appcenter.ReleaseUploadEndRequest;
import io.jenkins.plugins.appcenter.model.appcenter.ReleaseUploadEndResponse;
import io.jenkins.plugins.appcenter.model.appcenter.Status;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.PrintStream;
import java.util.concurrent.CompletableFuture;

@Singleton
public final class CommitUploadResourceTask implements AppCenterTask<CommitUploadResourceTask.Request, ReleaseUploadEndResponse> {

    @Nonnull
    private final AppCenterServiceFactory factory;
    @Nonnull
    private final PrintStream logger;

    @Inject
    CommitUploadResourceTask(@Nonnull final PrintStream logger,
                             @Nonnull final AppCenterServiceFactory factory) {
        this.logger = logger;
        this.factory = factory;
    }

    @Nonnull
    @Override
    public CompletableFuture<ReleaseUploadEndResponse> execute(@Nonnull Request request) {
        logger.println("Committing resource.");

        final CompletableFuture<ReleaseUploadEndResponse> future = new CompletableFuture<>();
        final ReleaseUploadEndRequest releaseUploadEndRequest = new ReleaseUploadEndRequest(Status.committed);

        factory.createAppCenterService()
            .releaseUploadEnd(request.ownerName, request.appName, request.uploadId, releaseUploadEndRequest)
            .whenComplete((releaseUploadBeginResponse, throwable) -> {
                if (throwable != null) {
                    final AppCenterException exception = new AppCenterException("Committing resource unsuccessful: ", throwable);
                    logger.println(exception.getMessage());
                    future.completeExceptionally(exception);
                }

                logger.println("Committing resource successful.");
                future.complete(releaseUploadBeginResponse);
            });

        return future;
    }

    public static class Request {
        @Nonnull
        public final String ownerName;
        @Nonnull
        public final String appName;
        @Nonnull
        private final String uploadId;

        public Request(@Nonnull final String ownerName,
                       @Nonnull final String appName,
                       @Nonnull final String uploadId) {
            this.ownerName = ownerName;
            this.appName = appName;
            this.uploadId = uploadId;
        }
    }
}