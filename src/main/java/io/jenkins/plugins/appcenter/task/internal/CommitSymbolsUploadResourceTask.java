package io.jenkins.plugins.appcenter.task.internal;

import hudson.model.TaskListener;
import io.jenkins.plugins.appcenter.AppCenterException;
import io.jenkins.plugins.appcenter.api.AppCenterServiceFactory;
import io.jenkins.plugins.appcenter.model.appcenter.ReleaseUploadEndResponse;
import io.jenkins.plugins.appcenter.model.appcenter.Status;
import io.jenkins.plugins.appcenter.model.appcenter.SymbolsUploadEndResponse;
import io.jenkins.plugins.appcenter.model.appcenter.UploadEndRequest;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.io.PrintStream;
import java.util.concurrent.CompletableFuture;

public final class CommitSymbolsUploadResourceTask extends CommitUploadResourceTask<CommitUploadResourceTask.Request, SymbolsUploadEndResponse> {

    @Inject
    CommitSymbolsUploadResourceTask(@Nonnull TaskListener taskListener, @Nonnull AppCenterServiceFactory factory) {
        super(taskListener, factory);
    }

    @Nonnull
    @Override
    public CompletableFuture<SymbolsUploadEndResponse> execute(@Nonnull Request request) {
        final PrintStream logger = taskListener.getLogger();
        logger.println("Committing resource.");

        final CompletableFuture<SymbolsUploadEndResponse> future = new CompletableFuture<>();
        final UploadEndRequest releaseUploadEndRequest = new UploadEndRequest(Status.COMMITTED);

        factory.createAppCenterService()
            .symbolsUploadEnd(request.ownerName, request.appName, request.uploadId, releaseUploadEndRequest)
            .whenComplete((symbolsUploadEndResponse, throwable) -> {
                if (throwable != null) {
                    final AppCenterException exception = new AppCenterException("Committing resource unsuccessful: ", throwable);
                    exception.printStackTrace(logger);
                    future.completeExceptionally(exception);
                } else {
                    logger.println("Committing resource successful.");
                    future.complete(symbolsUploadEndResponse);
                }
            });

        return future;
    }
}
