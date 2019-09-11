package io.jenkins.plugins.appcenter.model.appcenter;

import javax.annotation.Nonnull;
import java.util.Objects;

public final class ReleaseUploadEndRequest {
    public final Status status;

    public ReleaseUploadEndRequest(@Nonnull Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "ReleaseUploadEndRequest{" +
            "status=" + status +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReleaseUploadEndRequest that = (ReleaseUploadEndRequest) o;
        return status == that.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(status);
    }
}
