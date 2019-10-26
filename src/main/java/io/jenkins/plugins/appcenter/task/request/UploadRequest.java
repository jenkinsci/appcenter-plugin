package io.jenkins.plugins.appcenter.task.request;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;

public final class UploadRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @Nonnull
    public final String ownerName;
    @Nonnull
    public final String appName;
    @Nonnull
    public final String pathToApp;
    @Nonnull
    public final String destinationGroups;
    @Nonnull
    public final String releaseNotes;

    public final boolean notifyTesters;

    @Nullable
    public final String uploadUrl;
    @Nullable
    public final String uploadId;

    public final int releaseId;

    private UploadRequest(Builder builder) {
        this.ownerName = builder.ownerName;
        this.appName = builder.appName;
        this.pathToApp = builder.pathToApp;
        this.destinationGroups = builder.destinationGroups;
        this.releaseNotes = builder.releaseNotes;
        this.notifyTesters = builder.notifyTesters;

        // Expected to be nullable until they are added during UploadTask.
        this.uploadUrl = builder.uploadUrl;
        this.uploadId = builder.uploadId;
        this.releaseId = builder.releaseId;
    }

    public Builder newBuilder() {
        return new Builder(this);
    }

    public static final class Builder {

        @Nonnull
        private String ownerName;
        @Nonnull
        private String appName;
        @Nonnull
        private String pathToApp;
        @Nonnull
        private String destinationGroups;
        @Nonnull
        private String releaseNotes;
        private boolean notifyTesters;

        // Expected to be nullable until they are added during UploadTask.
        @Nullable
        private String uploadUrl;
        @Nullable
        private String uploadId;
        private int releaseId;

        public Builder() {
            ownerName = "";
            appName = "";
            pathToApp = "";
            destinationGroups = "";
            releaseNotes = "";
            notifyTesters = true;
        }

        Builder(@Nonnull final UploadRequest uploadRequest) {
            this.ownerName = uploadRequest.ownerName;
            this.appName = uploadRequest.appName;
            this.pathToApp = uploadRequest.pathToApp;
            this.destinationGroups = uploadRequest.destinationGroups;
            this.releaseNotes = uploadRequest.releaseNotes;
            this.notifyTesters = uploadRequest.notifyTesters;

            // Expected to be nullable until they are added during UploadTask.
            this.uploadUrl = uploadRequest.uploadUrl;
            this.uploadId = uploadRequest.uploadId;
            this.releaseId = uploadRequest.releaseId;
        }

        public Builder setOwnerName(@Nonnull String ownerName) {
            this.ownerName = ownerName;
            return this;
        }

        public Builder setAppName(@Nonnull String appName) {
            this.appName = appName;
            return this;
        }

        public Builder setPathToApp(@Nonnull String pathToApp) {
            this.pathToApp = pathToApp;
            return this;
        }

        public Builder setDestinationGroups(@Nonnull String destinationGroups) {
            this.destinationGroups = destinationGroups;
            return this;
        }

        public Builder setReleaseNotes(@Nonnull String releaseNotes) {
            this.releaseNotes = releaseNotes;
            return this;
        }

        public Builder setNotifyTesters(boolean notifyTesters) {
            this.notifyTesters = notifyTesters;
            return this;
        }

        public Builder setUploadUrl(@Nonnull String uploadUrl) {
            this.uploadUrl = uploadUrl;
            return this;
        }

        public Builder setUploadId(@Nonnull String uploadId) {
            this.uploadId = uploadId;
            return this;
        }

        public Builder setReleaseId(int releaseId) {
            this.releaseId = releaseId;
            return this;
        }

        public UploadRequest build() {
            return new UploadRequest(this);
        }
    }
}