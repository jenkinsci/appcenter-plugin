package io.jenkins.plugins.appcenter.task.internal;

import hudson.FilePath;
import hudson.model.TaskListener;
import io.jenkins.plugins.appcenter.api.AppCenterServiceFactory;
import retrofit2.HttpException;

import javax.annotation.Nonnull;
import javax.inject.Singleton;
import java.io.IOException;
import java.io.PrintStream;

@Singleton
public abstract class UploadToResourceTask  implements AppCenterTask<UploadToResourceTask.Request, String> {

    private static final long serialVersionUID = 1L;

    @Nonnull
    protected final TaskListener taskListener;
    @Nonnull
    protected final FilePath filePath;
    @Nonnull
    protected final AppCenterServiceFactory factory;

    UploadToResourceTask(@Nonnull final TaskListener taskListener,
                            @Nonnull final FilePath filePath,
                            @Nonnull final AppCenterServiceFactory factory) {
        this.taskListener = taskListener;
        this.filePath = filePath;
        this.factory = factory;
    }

    protected void tryLoggingErrorBody(PrintStream logger, Throwable throwable) {
        HttpException httpException = (HttpException) throwable;

        try {
            logger.println(httpException.response().errorBody().string());
            logger.println(httpException.response().message());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static class Request {
        @Nonnull
        protected final String uploadUrl;
        @Nonnull
        protected final String uploadId;
        @Nonnull
        protected final String pathToApp;

        public Request(@Nonnull final String uploadUrl,
                       @Nonnull final String uploadId,
                       @Nonnull final String pathToApp) {
            this.uploadUrl = uploadUrl;
            this.uploadId = uploadId;
            this.pathToApp = pathToApp;
        }
    }
}
