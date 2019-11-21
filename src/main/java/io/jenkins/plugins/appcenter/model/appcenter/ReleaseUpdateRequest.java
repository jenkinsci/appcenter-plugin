package io.jenkins.plugins.appcenter.model.appcenter;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

public final class ReleaseUpdateRequest {

    @Nullable
    public final String release_notes;

    @Nullable
    public final Boolean mandatory_update;

    @Nullable
    public final List<DestinationId> destinations;

    @Nullable
    public final BuildInfo build;

    @Nullable
    public final Boolean notify_testers;

    public ReleaseUpdateRequest(@Nullable String releaseNotes, @Nullable Boolean mandatoryUpdate, @Nullable List<DestinationId> destinations, @Nullable BuildInfo build, @Nullable Boolean notifyTesters) {
        this.release_notes = releaseNotes;
        this.mandatory_update = mandatoryUpdate;
        this.destinations = destinations;
        this.build = build;
        this.notify_testers = notifyTesters;
    }

    @Override
    public String toString() {
        return "ReleaseUpdateRequest{" +
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
        ReleaseUpdateRequest that = (ReleaseUpdateRequest) o;
        return Objects.equals(release_notes, that.release_notes) &&
            Objects.equals(mandatory_update, that.mandatory_update) &&
            Objects.equals(destinations, that.destinations) &&
            Objects.equals(build, that.build) &&
            Objects.equals(notify_testers, that.notify_testers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(release_notes, mandatory_update, destinations, build, notify_testers);
    }
}