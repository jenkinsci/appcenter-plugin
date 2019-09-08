package io.jenkins.plugins.appcenter.task.internal;

import hudson.model.TaskListener;
import io.jenkins.plugins.appcenter.AppCenterException;
import io.jenkins.plugins.appcenter.api.AppCenterServiceFactory;
import io.jenkins.plugins.appcenter.model.appcenter.ReleaseUploadBeginResponse;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.PrintStream;
import java.util.concurrent.CompletableFuture;

import static io.jenkins.plugins.appcenter.task.internal.CreateUploadResourceTask.Request;

@Singleton
public final class CreateUploadResourceTask implements AppCenterTask<Request, ReleaseUploadBeginResponse> {

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
    public CompletableFuture<ReleaseUploadBeginResponse> execute(@Nonnull Request request) {
        final PrintStream logger = taskListener.getLogger();
        logger.println("Creating an upload resource.");

        final CompletableFuture<ReleaseUploadBeginResponse> future = new CompletableFuture<>();

        // TODO: Pass in the release_id as an optional parameter from the UI. Don't use it if  not available
        //  final ReleaseUploadBeginRequest releaseUploadBeginRequest = new ReleaseUploadBeginRequest(upload.getReleaseId());
        //  using the overloaded releaseUploadBegin method.
        factory.createAppCenterService()
            .releaseUploadBegin(request.ownerName, request.appName)
            .whenComplete((releaseUploadBeginResponse, throwable) -> {
                if (throwable != null) {
                    future.completeExceptionally(new AppCenterException("Create upload resource unsuccessful: ", throwable));
                }

                logger.println("Create upload resource successful.");
                future.complete(releaseUploadBeginResponse);
            });

        return future;
    }

    public static class Request {
        @Nonnull
        public final String ownerName;
        @Nonnull
        public final String appName;

        public Request(@Nonnull final String ownerName,
                       @Nonnull final String appName) {
            this.ownerName = ownerName;
            this.appName = appName;
        }
    }
}