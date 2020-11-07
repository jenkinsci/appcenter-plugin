package io.jenkins.plugins.appcenter.task.internal;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobClientBuilder;
import hudson.FilePath;
import hudson.model.TaskListener;
import io.jenkins.plugins.appcenter.AppCenterException;
import io.jenkins.plugins.appcenter.AppCenterLogger;
import io.jenkins.plugins.appcenter.api.AppCenterServiceFactory;
import io.jenkins.plugins.appcenter.task.request.UploadRequest;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.File;
import java.io.PrintStream;
import java.net.URL;
import java.util.concurrent.CompletableFuture;

import static java.util.Objects.requireNonNull;

@Singleton
public final class UploadAppToResourceTask implements AppCenterTask<UploadRequest>, AppCenterLogger {

    private static final long serialVersionUID = 1L;

    @Nonnull
    private final TaskListener taskListener;
    @Nonnull
    private final FilePath filePath;
    @Nonnull
    private final AppCenterServiceFactory factory;

    @Inject
    UploadAppToResourceTask(@Nonnull final TaskListener taskListener,
                            @Nonnull final FilePath filePath,
                            @Nonnull final AppCenterServiceFactory factory) {
        this.taskListener = taskListener;
        this.filePath = filePath;
        this.factory = factory;
    }

    @Nonnull
    @Override
    public CompletableFuture<UploadRequest> execute(@Nonnull UploadRequest request) {
        if (request.symbolUploadUrl == null) {
            return uploadApp(request);
        } else {
            return uploadApp(request)
                .thenCompose(this::uploadSymbols);
        }
    }

    @Nonnull
    private CompletableFuture<UploadRequest> uploadApp(@Nonnull UploadRequest request) {
        final String pathToApp = request.pathToApp;
        final String uploadUrl = requireNonNull(request.uploadUrl, "uploadUrl cannot be null");

        log("Uploading app to resource.");

        final CompletableFuture<UploadRequest> future = new CompletableFuture<>();

        final File file = new File(filePath.child(pathToApp).getRemote());
        final RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        final MultipartBody.Part body = MultipartBody.Part.createFormData("ipa", file.getName(), requestFile);

        factory.createUploadService(uploadUrl)
            .uploadApp(uploadUrl, body)
            .whenComplete((responseBody, throwable) -> {
                if (throwable != null) {
                    final AppCenterException exception = logFailure("Upload app to resource unsuccessful", throwable);
                    future.completeExceptionally(exception);
                } else {
                    log("Upload app to resource successful.");
                    future.complete(request);
                }
            });

        return future;
    }

    @Nonnull
    private CompletableFuture<UploadRequest> uploadSymbols(@Nonnull UploadRequest request) {
        final String pathToDebugSymbols = request.pathToDebugSymbols;
        final String symbolUploadUrl = requireNonNull(request.symbolUploadUrl, "symbolUploadUrl cannot be null");

        final File file = new File(filePath.child(pathToDebugSymbols).getRemote());
        if (file.length() > (1024 * 1024) * 256) {
            return uploadSymbolsChunked(request, symbolUploadUrl, file);
        } else {
            return uploadSymbolsComplete(request, symbolUploadUrl, file);
        }
    }

    @Nonnull
    private CompletableFuture<UploadRequest> uploadSymbolsChunked(@Nonnull UploadRequest request, @Nonnull String symbolUploadUrl, @Nonnull File file) {
        log("Uploading symbols to resource chunked.");

        final CompletableFuture<UploadRequest> future = new CompletableFuture<>();
        CompletableFuture.supplyAsync(() -> {
            try {
                //Workaround for bug in Azure Blob Storage, as AppCenter returns the upload URL with a port attached
                //See https://github.com/Azure/azure-sdk-for-java/issues/15827
                final URL oldURL = new URL(symbolUploadUrl);
                final URL newURL = new URL(oldURL.getProtocol(), oldURL.getHost(), oldURL.getFile());
                final String symbolUploadUrlWithoutPort = newURL.toString();

                final BlobClient blobClient = new BlobClientBuilder().endpoint(symbolUploadUrlWithoutPort).buildClient();
                blobClient.uploadFromFile(file.getPath(), true);
            } catch (Exception e) {
                final AppCenterException exception = logFailure("Upload symbols to resource chunked unsuccessful: ", e);
                future.completeExceptionally(exception);
            }

            //null is returned because the return type is CompletableFuture<Void>
            return (Void) null;
        }).whenComplete((responseBody, throwable) -> {
            if (throwable != null) {
                final AppCenterException exception = logFailure("Upload symbols to resource chunked unsuccessful: ", throwable);
                future.completeExceptionally(exception);
            } else {
                log("Upload symbols to resource chunked successful.");
                future.complete(request);
            }
        });

        return future;
    }

    @Nonnull
    private CompletableFuture<UploadRequest> uploadSymbolsComplete(@Nonnull UploadRequest request, @Nonnull String symbolUploadUrl, @Nonnull File file) {
        log("Uploading all symbols at once to resource.");

        final CompletableFuture<UploadRequest> future = new CompletableFuture<>();
        final RequestBody requestFile = RequestBody.create(null, file);

        factory.createUploadService(symbolUploadUrl)
            .uploadSymbols(symbolUploadUrl, requestFile)
            .whenComplete((responseBody, throwable) -> {
                if (throwable != null) {
                    final AppCenterException exception = logFailure("Upload symbols to resource unsuccessful: ", throwable);
                    future.completeExceptionally(exception);
                } else {
                    log("Upload symbols to resource successful.");
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