package io.jenkins.plugins.appcenter.model.appcenter;

import javax.annotation.Nonnull;
import java.util.Objects;

public final class ReleaseDetailsUpdateResponse {
    public final String release_notes;

    public ReleaseDetailsUpdateResponse(@Nonnull String releaseNotes) {
        this.release_notes = releaseNotes;
    }

    @Override
    public String toString() {
        return "ReleaseDetailsUpdateResponse{" +
            "release_notes='" + release_notes + '\'' +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReleaseDetailsUpdateResponse that = (ReleaseDetailsUpdateResponse) o;
        return release_notes.equals(that.release_notes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(release_notes);
    }
}