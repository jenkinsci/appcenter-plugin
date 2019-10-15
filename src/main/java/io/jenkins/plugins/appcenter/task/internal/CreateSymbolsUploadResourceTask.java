package io.jenkins.plugins.appcenter.task.internal;

import hudson.model.TaskListener;
import io.jenkins.plugins.appcenter.AppCenterException;
import io.jenkins.plugins.appcenter.api.AppCenterServiceFactory;
import io.jenkins.plugins.appcenter.model.appcenter.SymbolsUploadBeginRequest;
import io.jenkins.plugins.appcenter.model.appcenter.SymbolsUploadBeginResponse;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.io.PrintStream;
import java.util.concurrent.CompletableFuture;

public final class CreateSymbolsUploadResourceTask extends CreateUploadResourceTask<SymbolsUploadBeginResponse> {

    @Inject
    CreateSymbolsUploadResourceTask(@Nonnull TaskListener taskListener, @Nonnull AppCenterServiceFactory factory) {
        super(taskListener, factory);
    }

    @Nonnull
    @Override
    public CompletableFuture<SymbolsUploadBeginResponse> execute(@Nonnull Request request) {
        final PrintStream logger = taskListener.getLogger();
        logger.println("Creating a symbols upload resource.");

        final CompletableFuture<SymbolsUploadBeginResponse> future = new CompletableFuture<>();

        // TODO: only apple supported currently for symbols uploads
        SymbolsUploadBeginRequest symbolsUploadBeginRequest = new SymbolsUploadBeginRequest("Apple");

        factory.createAppCenterService()
            .symbolsUploadBegin(request.ownerName, request.appName, symbolsUploadBeginRequest)
            .whenComplete((symbolsUploadBeginResponse, throwable) -> {
                if (throwable != null) {
                    final AppCenterException exception = new AppCenterException("Creating symbols upload resource unsuccessful: ", throwable);
                    exception.printStackTrace(logger);
                    future.completeExceptionally(exception);
                } else {
                    logger.println("Creating symbols upload resource successful.");
                    future.complete(symbolsUploadBeginResponse);
                }
            });

        return future;
    }

}
