package io.jenkins.plugins.appcenter.task.internal;

import hudson.model.TaskListener;
import io.jenkins.plugins.appcenter.api.AppCenterServiceFactory;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public abstract class CommitUploadResourceTask<T, R> implements AppCenterTask<T, R> {

    private static final long serialVersionUID = 1L;

    @Nonnull
    protected final TaskListener taskListener;
    @Nonnull
    protected final AppCenterServiceFactory factory;

    CommitUploadResourceTask(@Nonnull final TaskListener taskListener,
                             @Nonnull final AppCenterServiceFactory factory) {
        this.taskListener = taskListener;
        this.factory = factory;
    }

    public static class Request {
        @Nonnull
        protected final String ownerName;
        @Nonnull
        protected final String appName;
        @Nonnull
        protected final String uploadId;

        public Request(@Nonnull final String ownerName,
                       @Nonnull final String appName,
                       @Nonnull final String uploadId) {
            this.ownerName = ownerName;
            this.appName = appName;
            this.uploadId = uploadId;
        }
    }
}