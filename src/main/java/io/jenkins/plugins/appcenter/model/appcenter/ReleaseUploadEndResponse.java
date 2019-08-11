package io.jenkins.plugins.appcenter.model.appcenter;

import javax.annotation.Nonnull;
import java.util.Objects;

public final class ReleaseUploadEndResponse {
    public final int release_id;
    public final String release_url;

    public ReleaseUploadEndResponse(int releaseId, @Nonnull String releaseUrl) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReleaseUploadEndResponse that = (ReleaseUploadEndResponse) o;
        return release_id == that.release_id &&
            release_url.equals(that.release_url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(release_id, release_url);
    }
}