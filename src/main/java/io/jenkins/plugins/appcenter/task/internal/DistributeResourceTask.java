package io.jenkins.plugins.appcenter.task.internal;

import hudson.model.TaskListener;
import io.jenkins.plugins.appcenter.AppCenterException;
import io.jenkins.plugins.appcenter.AppCenterLogger;
import io.jenkins.plugins.appcenter.api.AppCenterServiceFactory;
import io.jenkins.plugins.appcenter.model.appcenter.DestinationId;
import io.jenkins.plugins.appcenter.model.appcenter.ReleaseDetailsUpdateRequest;
import io.jenkins.plugins.appcenter.task.request.UploadRequest;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.PrintStream;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        log("Distributing resource.");

        final CompletableFuture<UploadRequest> future = new CompletableFuture<>();

        final String releaseNotes = request.releaseNotes;
        final boolean mandatoryUpdate = false;
        final List<DestinationId> destinations = Stream.of(request.destinationGroups.split(","))
            .map(String::trim)
            .map(name -> new DestinationId(name, null))
            .collect(Collectors.toList());
        final boolean notifyTesters = request.notifyTesters;
        final ReleaseDetailsUpdateRequest releaseDetailsUpdateRequest = new ReleaseDetailsUpdateRequest(releaseNotes, mandatoryUpdate, destinations, null, notifyTesters);

        factory.createAppCenterService()
            .releaseDetailsUpdate(request.ownerName, request.appName, request.releaseId, releaseDetailsUpdateRequest)
            .whenComplete((releaseUploadBeginResponse, throwable) -> {
                if (throwable != null) {
                    final AppCenterException exception = logFailure("Distributing resource unsuccessful: ", throwable);
                    future.completeExceptionally(exception);
                } else {
                    log("Distributing resource successful.");
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