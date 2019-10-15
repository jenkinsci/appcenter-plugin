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
public abstract class CreateUploadResourceTask<T> implements AppCenterTask<Request, T> {

    protected static final long serialVersionUID = 1L;

    @Nonnull
    protected final TaskListener taskListener;
    @Nonnull
    protected final AppCenterServiceFactory factory;

    CreateUploadResourceTask(@Nonnull final TaskListener taskListener,
                             @Nonnull final AppCenterServiceFactory factory) {
        this.taskListener = taskListener;
        this.factory = factory;
    }

    public static class Request {
        @Nonnull
        protected final String ownerName;
        @Nonnull
        protected final String appName;

        public Request(@Nonnull final String ownerName,
                       @Nonnull final String appName) {
            this.ownerName = ownerName;
            this.appName = appName;
        }
    }
}