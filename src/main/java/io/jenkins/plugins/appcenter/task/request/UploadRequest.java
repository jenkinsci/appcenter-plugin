package io.jenkins.plugins.appcenter.task.request;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.Objects;

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

    // Properties above this line are expected to be set by plugin configuration before a run they should be nonnull.
    // Properties below this line are expected to be set during a run as these values will come from AppCenter during
    // execution they should be nullable prior to being set.

    @Nullable
    public final String uploadUrl;
    @Nullable
    public final String uploadId;

    public final int releaseId;

    @Override
    public String toString() {
        return "UploadRequest{" +
            "ownerName='" + ownerName + '\'' +
            ", appName='" + appName + '\'' +
            ", pathToApp='" + pathToApp + '\'' +
            ", destinationGroups='" + destinationGroups + '\'' +
            ", releaseNotes='" + releaseNotes + '\'' +
            ", notifyTesters=" + notifyTesters +
            ", uploadUrl='" + uploadUrl + '\'' +
            ", uploadId='" + uploadId + '\'' +
            ", releaseId=" + releaseId +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UploadRequest that = (UploadRequest) o;
        return notifyTesters == that.notifyTesters &&
            releaseId == that.releaseId &&
            ownerName.equals(that.ownerName) &&
            appName.equals(that.appName) &&
            pathToApp.equals(that.pathToApp) &&
            destinationGroups.equals(that.destinationGroups) &&
            releaseNotes.equals(that.releaseNotes) &&
            Objects.equals(uploadUrl, that.uploadUrl) &&
            Objects.equals(uploadId, that.uploadId);
    }

    @Override
    public int hashCode() {
        int result = ownerName.hashCode();
        result = 31 * result + appName.hashCode();
        result = 31 * result + pathToApp.hashCode();
        result = 31 * result + destinationGroups.hashCode();
        result = 31 * result + releaseNotes.hashCode();
        result = 31 * result + (notifyTesters ? 1 : 0);
        result = 31 * result + (uploadUrl != null ? uploadUrl.hashCode() : 0);
        result = 31 * result + (uploadId != null ? uploadId.hashCode() : 0);
        result = 31 * result + releaseId;
        return result;
    }

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