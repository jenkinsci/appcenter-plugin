package io.jenkins.plugins.appcenter.task.internal;

import hudson.model.TaskListener;
import io.jenkins.plugins.appcenter.AppCenterException;
import io.jenkins.plugins.appcenter.AppCenterLogger;
import io.jenkins.plugins.appcenter.api.AppCenterServiceFactory;
import io.jenkins.plugins.appcenter.model.appcenter.DestinationId;
import io.jenkins.plugins.appcenter.model.appcenter.ReleaseUpdateRequest;
import io.jenkins.plugins.appcenter.task.request.UploadRequest;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.PrintStream;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

@Singleton
public final class DistributeResourceTask implements AppCenterTask<UploadRequest>, AppCenterLogger {

    private static final long serialVersionUID = 1L;

    @Nonnull
    private final TaskListener taskListener;
    @Nonnull
    private final AppCenterServiceFactory factory;

    @Inject
    DistributeResourceTask(@Nonnull final TaskListener taskListener,
                           @Nonnull final AppCenterServiceFactory factory) {
        this.taskListener = taskListener;
        this.factory = factory;
    }

    @Nonnull
    @Override
    public CompletableFuture<UploadRequest> execute(@Nonnull UploadRequest request) {
        final Integer releaseId = requireNonNull(request.releaseId, "releaseId cannot be null");

        log("Distributing resource.");

        final CompletableFuture<UploadRequest> future = new CompletableFuture<>();

        final String releaseNotes = parseReleaseNotes(request);
        final boolean mandatoryUpdate = false;
        final List<DestinationId> destinations = Stream.of(request.destinationGroups.split(","))
            .map(String::trim)
            .map(name -> new DestinationId(name, null))
            .collect(Collectors.toList());
        final boolean notifyTesters = request.notifyTesters;
        final ReleaseUpdateRequest releaseDetailsUpdateRequest = new ReleaseUpdateRequest(releaseNotes, mandatoryUpdate, destinations, null, notifyTesters);

        factory.createAppCenterService()
            .releasesUpdate(request.ownerName, request.appName, releaseId, releaseDetailsUpdateRequest)
            .whenComplete((releaseUploadBeginResponse, throwable) -> {
                if (throwable != null) {
                    final AppCenterException exception = logFailure("Distributing resource unsuccessful", throwable);
                    future.completeExceptionally(exception);
                } else {
                    log("Distributing resource successful.");
                    future.complete(request);
                }
            });

        return future;
    }

    @Nonnull
    private String parseReleaseNotes(@Nonnull UploadRequest request) {
        return request.releaseNotes;
    }

    @Override
    public PrintStream getLogger() {
        return taskListener.getLogger();
    }
}