package io.jenkins.plugins.appcenter.model.appcenter;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReleaseUploadBeginRequest that = (ReleaseUploadBeginRequest) o;
        return release_id == that.release_id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(release_id);
    }
}