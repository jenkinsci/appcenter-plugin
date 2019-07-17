package io.jenkins.plugins.appcenter.model.appcenter;

import java.util.List;

public final class ReleaseDetailsUpdateRequest {
    public final String release_notes;
    public final boolean mandatory_update;
    public final List<DestinationId> destinations;
    public final BuildInfo build;
    public final boolean notify_testers;

    public ReleaseDetailsUpdateRequest(String releaseNotes, boolean mandatoryUpdate, List<DestinationId> destinations, BuildInfo build, boolean notifyTesters) {
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
}