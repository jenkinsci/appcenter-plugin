package io.jenkins.plugins.appcenter.task.internal;

import io.jenkins.plugins.appcenter.AppCenterException;
import io.jenkins.plugins.appcenter.api.AppCenterServiceFactory;
import io.jenkins.plugins.appcenter.model.appcenter.DestinationId;
import io.jenkins.plugins.appcenter.model.appcenter.ReleaseDetailsUpdateRequest;
import io.jenkins.plugins.appcenter.model.appcenter.ReleaseDetailsUpdateResponse;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.PrintStream;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.jenkins.plugins.appcenter.task.internal.DistributeResourceTask.Request;

@Singleton
public final class DistributeResourceTask implements AppCenterTask<Request, ReleaseDetailsUpdateResponse> {

    @Nonnull
    private final PrintStream logger;
    @Nonnull
    private final AppCenterServiceFactory factory;

    @Inject
    DistributeResourceTask(@Nonnull final PrintStream logger,
                           @Nonnull final AppCenterServiceFactory factory) {
        this.logger = logger;
        this.factory = factory;
    }

    @Nonnull
    @Override
    public CompletableFuture<ReleaseDetailsUpdateResponse> execute(@Nonnull Request request) {
        logger.println("Distributing resource.");

        final CompletableFuture<ReleaseDetailsUpdateResponse> future = new CompletableFuture<>();

        final String releaseNotes = "";
        final boolean mandatoryUpdate = false;
        final List<DestinationId> destinations = Stream.of(request.destinationGroups.split(","))
            .map(String::trim)
            .map(name -> new DestinationId(name, null))
            .collect(Collectors.toList());
        final boolean notifyTesters = false;
        final ReleaseDetailsUpdateRequest releaseDetailsUpdateRequest = new ReleaseDetailsUpdateRequest(releaseNotes, mandatoryUpdate, destinations, null, notifyTesters);

        factory.createAppCenterService()
            .releaseDetailsUpdate(request.ownerName, request.appName, request.releaseId, releaseDetailsUpdateRequest)
            .whenComplete((releaseUploadBeginResponse, throwable) -> {
                if (throwable != null) {
                    final AppCenterException exception = new AppCenterException("Distributing resource unsuccessful: ", throwable);
                    logger.println(exception.getMessage());
                    future.completeExceptionally(exception);
                }

                logger.println("Distributing resource successful.");
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
        private final String destinationGroups;
        private final int releaseId;

        public Request(@Nonnull final String ownerName,
                       @Nonnull final String appName,
                       @Nonnull final String destinationGroups,
                       final int releaseId) {
            this.ownerName = ownerName;
            this.appName = appName;
            this.destinationGroups = destinationGroups;
            this.releaseId = releaseId;
        }
    }
}