package io.jenkins.plugins.appcenter.model.appcenter;

import javax.annotation.Nonnull;
import java.util.Objects;

public final class UploadEndRequest {
    public final Status status;

    public UploadEndRequest(@Nonnull Status status) {
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
        UploadEndRequest that = (UploadEndRequest) o;
        return status == that.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(status);
    }
}
