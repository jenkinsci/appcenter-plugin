package io.jenkins.plugins.appcenter.task.request;

import io.jenkins.plugins.appcenter.model.appcenter.SymbolUploadBeginRequest;

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
    @Nonnull
    public final String pathToReleaseNotes;
    public final boolean notifyTesters;
    public final boolean mandatoryUpdate;
    @Nonnull
    public final String buildVersion;
    @Nonnull
    public final String pathToDebugSymbols;

    // Properties above this line are expected to be set by plugin configuration before a run they should be nonnull.
    // Properties below this line are expected to be set during a run as these values will come from AppCenter during
    // execution they should be nullable prior to being set.

    @Nullable
    public final String uploadId;
    @Nullable
    public final String uploadDomain;
    @Nullable
    public final String token;
    @Nullable
    public final String packageAssetId;
    @Nullable
    public final Integer chunkSize;
    @Nullable
    public final Integer releaseId;
    @Nullable
    public final SymbolUploadBeginRequest symbolUploadRequest;
    @Nullable
    public final String symbolUploadUrl;
    @Nullable
    public final String symbolUploadId;
    @Nullable
    public final String commitHash;
    @Nullable
    public final String branchName;

    @Override
    public String toString() {
        return "UploadRequest{" +
            "ownerName='" + ownerName + '\'' +
            ", appName='" + appName + '\'' +
            ", pathToApp='" + pathToApp + '\'' +
            ", destinationGroups='" + destinationGroups + '\'' +
            ", releaseNotes='" + releaseNotes + '\'' +
            ", pathToReleaseNotes='" + pathToReleaseNotes + '\'' +
            ", notifyTesters=" + notifyTesters +
            ", mandatoryUpdate=" + mandatoryUpdate +
            ", buildVersion='" + buildVersion + '\'' +
            ", pathToDebugSymbols='" + pathToDebugSymbols + '\'' +
            ", uploadId='" + uploadId + '\'' +
            ", uploadDomain='" + uploadDomain + '\'' +
            ", token='" + token + '\'' +
            ", packageAssetId='" + packageAssetId + '\'' +
            ", chunkSize=" + chunkSize +
            ", releaseId=" + releaseId +
            ", symbolUploadRequest=" + symbolUploadRequest +
            ", symbolUploadUrl='" + symbolUploadUrl + '\'' +
            ", symbolUploadId='" + symbolUploadId + '\'' +
            ", commitHash='" + commitHash + '\'' +
            ", branchName='" + branchName + '\'' +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UploadRequest that = (UploadRequest) o;
        return notifyTesters == that.notifyTesters && mandatoryUpdate == that.mandatoryUpdate && ownerName.equals(that.ownerName) && appName.equals(that.appName) && pathToApp.equals(that.pathToApp) && destinationGroups.equals(that.destinationGroups) && releaseNotes.equals(that.releaseNotes) && pathToReleaseNotes.equals(that.pathToReleaseNotes) && buildVersion.equals(that.buildVersion) && pathToDebugSymbols.equals(that.pathToDebugSymbols) && Objects.equals(uploadId, that.uploadId) && Objects.equals(uploadDomain, that.uploadDomain) && Objects.equals(token, that.token) && Objects.equals(packageAssetId, that.packageAssetId) && Objects.equals(chunkSize, that.chunkSize) && Objects.equals(releaseId, that.releaseId) && Objects.equals(symbolUploadRequest, that.symbolUploadRequest) && Objects.equals(symbolUploadUrl, that.symbolUploadUrl) && Objects.equals(symbolUploadId, that.symbolUploadId) && Objects.equals(commitHash, that.commitHash) && Objects.equals(branchName, that.branchName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ownerName, appName, pathToApp, destinationGroups, releaseNotes, pathToReleaseNotes, notifyTesters, mandatoryUpdate, buildVersion, pathToDebugSymbols, uploadId, uploadDomain, token, packageAssetId, chunkSize, releaseId, symbolUploadRequest, symbolUploadUrl, symbolUploadId, commitHash, branchName);
    }

    private UploadRequest(Builder builder) {
        this.ownerName = builder.ownerName;
        this.appName = builder.appName;
        this.pathToApp = builder.pathToApp;
        this.destinationGroups = builder.destinationGroups;
        this.releaseNotes = builder.releaseNotes;
        this.pathToReleaseNotes = builder.pathToReleaseNotes;
        this.notifyTesters = builder.notifyTesters;
        this.mandatoryUpdate = builder.mandatoryUpdate;
        this.buildVersion = builder.buildVersion;
        this.pathToDebugSymbols = builder.pathToDebugSymbols;

        // Expected to be nullable until they are added during UploadTask.
        this.uploadId = builder.uploadId;
        this.uploadDomain = builder.uploadDomain;
        this.token = builder.token;
        this.packageAssetId = builder.packageAssetId;
        this.chunkSize = builder.chunkSize;
        this.releaseId = builder.releaseId;
        this.symbolUploadRequest = builder.symbolUploadRequest;
        this.symbolUploadUrl = builder.symbolUploadUrl;
        this.symbolUploadId = builder.symbolUploadId;
        this.commitHash = builder.commitHash;
        this.branchName = builder.branchName;
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
        @Nonnull
        private String pathToReleaseNotes;
        private boolean notifyTesters;
        private boolean mandatoryUpdate;
        @Nonnull
        private String buildVersion;
        @Nonnull
        private String pathToDebugSymbols;

        // Expected to be nullable until they are added during UploadTask.
        @Nullable
        private String uploadId;
        @Nullable
        private String uploadDomain;
        @Nullable
        private String token;
        @Nullable
        private String packageAssetId;
        @Nullable
        private Integer chunkSize;
        @Nullable
        private Integer releaseId;
        @Nullable
        private SymbolUploadBeginRequest symbolUploadRequest;
        @Nullable
        private String symbolUploadUrl;
        @Nullable
        private String symbolUploadId;
        @Nullable
        private String commitHash;
        @Nullable
        private String branchName;

        public Builder() {
            ownerName = "";
            appName = "";
            pathToApp = "";
            destinationGroups = "";
            releaseNotes = "";
            pathToReleaseNotes = "";
            notifyTesters = true;
            mandatoryUpdate = false;
            buildVersion = "";
            pathToDebugSymbols = "";
            commitHash = "";
            branchName = "";
        }

        Builder(@Nonnull final UploadRequest uploadRequest) {
            this.ownerName = uploadRequest.ownerName;
            this.appName = uploadRequest.appName;
            this.pathToApp = uploadRequest.pathToApp;
            this.destinationGroups = uploadRequest.destinationGroups;
            this.releaseNotes = uploadRequest.releaseNotes;
            this.pathToReleaseNotes = uploadRequest.pathToReleaseNotes;
            this.notifyTesters = uploadRequest.notifyTesters;
            this.mandatoryUpdate = uploadRequest.mandatoryUpdate;
            this.buildVersion = uploadRequest.buildVersion;
            this.pathToDebugSymbols = uploadRequest.pathToDebugSymbols;
            this.commitHash = uploadRequest.commitHash;
            this.branchName = uploadRequest.branchName;

            // Expected to be nullable until they are added during UploadTask.
            this.uploadId = uploadRequest.uploadId;
            this.uploadDomain = uploadRequest.uploadDomain;
            this.token = uploadRequest.token;
            this.packageAssetId = uploadRequest.packageAssetId;
            this.chunkSize = uploadRequest.chunkSize;
            this.releaseId = uploadRequest.releaseId;
            this.symbolUploadRequest = uploadRequest.symbolUploadRequest;
            this.symbolUploadUrl = uploadRequest.symbolUploadUrl;
            this.symbolUploadId = uploadRequest.symbolUploadId;
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

        public Builder setPathToReleaseNotes(@Nonnull String pathToReleaseNotes) {
            this.pathToReleaseNotes = pathToReleaseNotes;
            return this;
        }

        public Builder setNotifyTesters(boolean notifyTesters) {
            this.notifyTesters = notifyTesters;
            return this;
        }

        public Builder setMandatoryUpdate(boolean mandatoryUpdate) {
            this.mandatoryUpdate = mandatoryUpdate;
            return this;
        }

        public Builder setBuildVersion(@Nonnull String buildVersion) {
            this.buildVersion = buildVersion;
            return this;
        }

        public Builder setPathToDebugSymbols(@Nonnull String pathToDebugSymbols) {
            this.pathToDebugSymbols = pathToDebugSymbols;
            return this;
        }

        // Properties above this line are expected to be set by plugin configuration before a run.
        // Properties below this line are expected to be set during a run as these values will come
        // from AppCenter during execution they should be nullable prior to being set.
        public Builder setUploadId(@Nonnull String uploadId) {
            this.uploadId = uploadId;
            return this;
        }

        public Builder setUploadDomain(@Nonnull String uploadDomain) {
            this.uploadDomain = uploadDomain;
            return this;
        }

        public Builder setToken(@Nonnull String token) {
            this.token = token;
            return this;
        }

        public Builder setPackageAssetId(@Nonnull String packageAssetId) {
            this.packageAssetId = packageAssetId;
            return this;
        }

        public Builder setChunkSize(@Nonnull Integer chunkSize) {
            this.chunkSize = chunkSize;
            return this;
        }

        public Builder setReleaseId(Integer releaseId) {
            this.releaseId = releaseId;
            return this;
        }

        public Builder setSymbolUploadRequest(@Nonnull SymbolUploadBeginRequest symbolUploadRequest) {
            this.symbolUploadRequest = symbolUploadRequest;
            return this;
        }

        public Builder setSymbolUploadUrl(@Nonnull String symbolUploadUrl) {
            this.symbolUploadUrl = symbolUploadUrl;
            return this;
        }

        public Builder setSymbolUploadId(@Nonnull String symbolUploadId) {
            this.symbolUploadId = symbolUploadId;
            return this;
        }

        public Builder setCommitHash(@Nonnull String commitHash) {
            this.commitHash = commitHash;
            return this;
        }

        public Builder setBranchName(@Nonnull String branchName) {
            this.branchName = branchName;
            return this;
        }

        public UploadRequest build() {
            return new UploadRequest(this);
        }
    }
}