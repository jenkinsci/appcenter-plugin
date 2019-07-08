package io.jenkins.plugins.appcenter.model.remote;

public final class ReleaseUploadEndRequest {
    public final Status status;

    public ReleaseUploadEndRequest(Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "ReleaseUploadEndRequest{" +
                "status=" + status +
                '}';
    }
}
