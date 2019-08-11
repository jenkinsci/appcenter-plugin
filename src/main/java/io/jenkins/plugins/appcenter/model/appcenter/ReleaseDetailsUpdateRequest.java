package io.jenkins.plugins.appcenter.model.appcenter;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Objects;

public final class ReleaseDetailsUpdateRequest {
    public final String release_notes;
    public final boolean mandatory_update;
    public final List<DestinationId> destinations;
    public final BuildInfo build;
    public final boolean notify_testers;

    public ReleaseDetailsUpdateRequest(@Nonnull String releaseNotes, boolean mandatoryUpdate, @Nonnull List<DestinationId> destinations, @Nonnull BuildInfo build,
                                       boolean notifyTesters) {
        this.release_notes = releaseNotes;
        this.mandatory_update = mandatoryUpdate;
        this.destinations = destinations;
        this.build = build;
        this.notify_testers = notifyTesters;
    }

    @Override
    public String toString() {
        return "ReleaseDetailsUpdateRequest{" +
            "release_notes='" + release_notes + '\'' +
            ", mandatory_update=" + mandatory_update +
            ", destinations=" + destinations +
            ", build=" + build +
            ", notify_testers=" + notify_testers +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReleaseDetailsUpdateRequest that = (ReleaseDetailsUpdateRequest) o;
        return mandatory_update == that.mandatory_update &&
            notify_testers == that.notify_testers &&
            release_notes.equals(that.release_notes) &&
            destinations.equals(that.destinations) &&
            build.equals(that.build);
    }

    @Override
    public int hashCode() {
        return Objects.hash(release_notes, mandatory_update, destinations, build, notify_testers);
    }
}