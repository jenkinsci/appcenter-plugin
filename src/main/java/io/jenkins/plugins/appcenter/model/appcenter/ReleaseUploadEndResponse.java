package io.jenkins.plugins.appcenter.model.appcenter;

import javax.annotation.Nullable;
import java.util.Objects;

public final class ReleaseUploadEndResponse {

    @Nullable
    public final Integer release_id;
    @Nullable
    public final String release_url;

    public ReleaseUploadEndResponse(@Nullable Integer releaseId, @Nullable String releaseUrl) {
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
        return Objects.equals(release_id, that.release_id) &&
            Objects.equals(release_url, that.release_url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(release_id, release_url);
    }
}