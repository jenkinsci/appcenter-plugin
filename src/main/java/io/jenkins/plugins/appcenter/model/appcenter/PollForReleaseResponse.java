package io.jenkins.plugins.appcenter.model.appcenter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

public final class PollForReleaseResponse {
    @Nonnull
    public final String id;
    @Nonnull
    public final StatusEnum upload_status;
    @Nullable
    public final String error_details;
    @Nullable
    public final Integer release_distinct_id;
    @Nullable
    public final String release_url;

    public PollForReleaseResponse(@Nonnull String id,
                                  @Nonnull StatusEnum upload_status,
                                  @Nullable String error_details,
                                  @Nullable Integer release_distinct_id,
                                  @Nullable String release_url) {

        this.id = id;
        this.upload_status = upload_status;
        this.error_details = error_details;
        this.release_distinct_id = release_distinct_id;
        this.release_url = release_url;
    }

    @Override
    public String toString() {
        return "PollForReleaseResponse{" +
            "id='" + id + '\'' +
            ", upload_status=" + upload_status +
            ", error_details='" + error_details + '\'' +
            ", release_distinct_id=" + release_distinct_id +
            ", release_url='" + release_url + '\'' +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PollForReleaseResponse that = (PollForReleaseResponse) o;
        return id.equals(that.id) && upload_status == that.upload_status && Objects.equals(error_details, that.error_details) && Objects.equals(release_distinct_id, that.release_distinct_id) && Objects.equals(release_url, that.release_url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, upload_status, error_details, release_distinct_id, release_url);
    }

    public enum StatusEnum {
        uploadStarted,
        uploadFinished,
        readyToBePublished,
        malwareDetected,
        error
    }
}