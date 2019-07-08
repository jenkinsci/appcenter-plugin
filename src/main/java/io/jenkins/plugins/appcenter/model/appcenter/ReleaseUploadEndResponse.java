package io.jenkins.plugins.appcenter.model.appcenter;

public final class ReleaseUploadEndResponse {
    public final int release_id;
    public final String release_url;

    ReleaseUploadEndResponse(int releaseId, String releaseUrl) {
        this.release_id = releaseId;
        this.release_url = releaseUrl;
    }

    @Override
    public String toString() {
        return "ReleaseUploadEndResponse{" +
            "release_id='" + release_id + '\'' +
            ", release_url='" + release_url + '\'' +
            '}';
    }
}