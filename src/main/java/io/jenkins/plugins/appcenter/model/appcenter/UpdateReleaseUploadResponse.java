package io.jenkins.plugins.appcenter.model.appcenter;

import javax.annotation.Nonnull;
import java.util.Objects;

public final class UpdateReleaseUploadResponse {
    @Nonnull
    public final String id;
    @Nonnull
    public final StatusEnum upload_status;

    public UpdateReleaseUploadResponse(@Nonnull String id,
                                       @Nonnull StatusEnum upload_status) {

        this.id = id;
        this.upload_status = upload_status;
    }

    @Override
    public String toString() {
        return "UpdateReleaseUploadResponse{" +
            "id='" + id + '\'' +
            ", upload_status=" + upload_status +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UpdateReleaseUploadResponse that = (UpdateReleaseUploadResponse) o;
        return id.equals(that.id) && upload_status == that.upload_status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, upload_status);
    }

    public enum StatusEnum {
        uploadStarted,
        uploadFinished,
        uploadCanceled,
        readyToBePublished,
        malwareDetected,
        error
    }
}