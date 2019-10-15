package io.jenkins.plugins.appcenter.task.internal;

import hudson.FilePath;
import hudson.model.TaskListener;
import io.jenkins.plugins.appcenter.AppCenterException;
import io.jenkins.plugins.appcenter.api.AppCenterServiceFactory;
import okhttp3.MediaType;
import okhttp3.RequestBody;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.io.File;
import java.io.PrintStream;
import java.util.concurrent.CompletableFuture;

public class UploadSymbolsToResourceTask extends UploadToResourceTask {

    @Inject
    UploadSymbolsToResourceTask(@Nonnull TaskListener taskListener, @Nonnull FilePath filePath, @Nonnull AppCenterServiceFactory factory) {
        super(taskListener, filePath, factory);
    }

    // according to this documentation: https://docs.microsoft.com/en-us/appcenter/diagnostics/ios-symbolication
    @Nonnull
    @Override
    public CompletableFuture<String> execute(@Nonnull Request request) {
        final PrintStream logger = taskListener.getLogger();
        logger.println("Uploading symbols file.");

        final CompletableFuture<String> future = new CompletableFuture<>();
        final File file = new File(filePath.child(request.pathToApp).getRemote());

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/octet-stream"), file);

        factory.createUploadService(request.uploadUrl)
            .uploadSymbols(request.uploadUrl, "BlockBlob", requestBody)
            .whenComplete((responseBody, throwable) -> {
                if (throwable != null) {
                    final AppCenterException exception = new AppCenterException("Uploading symbols file unsuccessful: ", throwable);

                    tryLoggingErrorBody(logger, throwable);

                    exception.printStackTrace(logger);

                    future.completeExceptionally(exception);
                } else {
                    logger.println("Upload symbols to resource successful.");
                    future.complete(request.uploadId);
                }
            });

        return future;
    }
}
