package io.jenkins.plugins.appcenter.task.internal;

import hudson.FilePath;
import hudson.model.TaskListener;
import io.jenkins.plugins.appcenter.AppCenterException;
import io.jenkins.plugins.appcenter.AppCenterLogger;
import io.jenkins.plugins.appcenter.api.AppCenterServiceFactory;
import io.jenkins.plugins.appcenter.model.appcenter.BuildInfo;
import io.jenkins.plugins.appcenter.model.appcenter.DestinationId;
import io.jenkins.plugins.appcenter.model.appcenter.ReleaseUpdateRequest;
import io.jenkins.plugins.appcenter.task.request.UploadRequest;
import org.apache.commons.lang.StringUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
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
    private final FilePath filePath;
    @Nonnull
    private final AppCenterServiceFactory factory;

    @Inject
    DistributeResourceTask(@Nonnull final TaskListener taskListener,
                           @Nonnull final FilePath filePath,
                           @Nonnull final AppCenterServiceFactory factory) {
        this.taskListener = taskListener;
        this.filePath = filePath;
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

        final ReleaseUpdateRequest releaseDetailsUpdateRequest = new ReleaseUpdateRequest(releaseNotes, mandatoryUpdate, destinations, createBuildInfo(request), notifyTesters);

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
        final String releaseNotesFromFile = parseReleaseNotesFromFile(request.pathToReleaseNotes);
        final String separator = (!request.releaseNotes.isEmpty() && !releaseNotesFromFile.isEmpty()) ? "\n\n" : "";
        final String combinedReleaseNotes = request.releaseNotes + separator + releaseNotesFromFile;

        return StringUtils.left(combinedReleaseNotes, 5000);
    }

    @Nonnull
    private String parseReleaseNotesFromFile(@Nonnull String pathToReleaseNotes) {
        if (pathToReleaseNotes.isEmpty()) return "";

        final FilePath releaseNotesFilePath = filePath.child(pathToReleaseNotes);
        try {
            return releaseNotesFilePath.readToString();
        } catch (IOException | InterruptedException e) {
            log(String.format("Unable to read release note file due to: %1$s", e));
            return "";
        }

    }

    @Nullable
    private BuildInfo createBuildInfo(@Nonnull UploadRequest request) {
        if (request.branchName == null && request.commitHash == null) return null;
        return new BuildInfo(request.branchName, request.commitHash, null);
    }

    @Override
    public PrintStream getLogger() {
        return taskListener.getLogger();
    }
}