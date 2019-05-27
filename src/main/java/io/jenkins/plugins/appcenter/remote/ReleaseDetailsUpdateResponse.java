package io.jenkins.plugins.appcenter.remote;

public class ReleaseDetailsUpdateResponse {
    public final String release_notes;

    public ReleaseDetailsUpdateResponse(String releaseNotes) {
        this.release_notes = releaseNotes;
    }

    @Override
    public String toString() {
        return "ReleaseDetailsUpdateResponse{" +
                "release_notes='" + release_notes + '\'' +
                '}';
    }
}
