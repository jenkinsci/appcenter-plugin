package io.jenkins.plugins.appcenter.model;

public final class ReleaseUploadBeginRequest {
    public final int release_id;

    public ReleaseUploadBeginRequest(int releaseId) {
        this.release_id = releaseId;
    }

    @Override
    public String toString() {
        return "ReleaseUploadBeginRequest{" +
                "release_id='" + release_id + '\'' +
                '}';
    }
}
