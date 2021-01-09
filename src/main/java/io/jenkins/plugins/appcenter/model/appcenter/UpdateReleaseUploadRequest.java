package io.jenkins.plugins.appcenter.model.appcenter;

import javax.annotation.Nonnull;
import java.util.Objects;

public final class UpdateReleaseUploadRequest {
    @Nonnull
    public final StatusEnum upload_status;

    public UpdateReleaseUploadRequest(@Nonnull StatusEnum upload_status) {
        this.upload_status = upload_status;
    }

    @Override
    public String toString() {
        return "UpdateReleaseUploadRequest{" +
            "upload_status=" + upload_status +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UpdateReleaseUploadRequest that = (UpdateReleaseUploadRequest) o;
        return upload_status == that.upload_status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(upload_status);
    }

    public enum StatusEnum {
        uploadFinished,
        uploadCanceled
    }
}