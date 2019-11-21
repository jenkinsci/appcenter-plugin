package io.jenkins.plugins.appcenter.model.appcenter;

import javax.annotation.Nullable;
import java.util.Objects;

public final class ReleaseUploadBeginRequest {

    @Nullable
    public final Integer release_id;
    @Nullable
    public final String build_version;
    @Nullable
    public final String build_number;

    public ReleaseUploadBeginRequest(@Nullable Integer releaseId, @Nullable String buildVersion, @Nullable String buildNumber) {
        this.release_id = releaseId;
        this.build_version = buildVersion;
        this.build_number = buildNumber;
    }

    @Override
    public String toString() {
        return "ReleaseUploadBeginRequest{" +
            "release_id=" + release_id +
            ", build_version='" + build_version + '\'' +
            ", build_number='" + build_number + '\'' +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReleaseUploadBeginRequest that = (ReleaseUploadBeginRequest) o;
        return Objects.equals(release_id, that.release_id) &&
            Objects.equals(build_version, that.build_version) &&
            Objects.equals(build_number, that.build_number);
    }

    @Override
    public int hashCode() {
        return Objects.hash(release_id, build_version, build_number);
    }
}