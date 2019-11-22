package io.jenkins.plugins.appcenter.model.appcenter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

public final class ReleaseUpdateError {

    @Nonnull
    public final CodeEnum code;

    @Nonnull
    public final String message;

    @Nullable
    public final String release_notes;

    @Nullable
    public final Boolean mandatory_update;

    @Nullable
    public final List<DestinationError> destinations;

    public ReleaseUpdateError(@Nonnull CodeEnum code, @Nonnull String message, @Nullable String releaseNotes, @Nullable Boolean mandatoryUpdate, @Nullable List<DestinationError> destinations) {
        this.code = code;
        this.message = message;
        this.release_notes = releaseNotes;
        this.mandatory_update = mandatoryUpdate;
        this.destinations = destinations;
    }

    @Override
    public String toString() {
        return "ReleaseUpdateError{" +
            "code=" + code +
            ", message='" + message + '\'' +
            ", release_notes='" + release_notes + '\'' +
            ", mandatory_update=" + mandatory_update +
            ", destinations=" + destinations +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReleaseUpdateError that = (ReleaseUpdateError) o;
        return code == that.code &&
            message.equals(that.message) &&
            Objects.equals(release_notes, that.release_notes) &&
            Objects.equals(mandatory_update, that.mandatory_update) &&
            Objects.equals(destinations, that.destinations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, message, release_notes, mandatory_update, destinations);
    }

    public enum CodeEnum {
        BadRequest,
        Conflict,
        NotAcceptable,
        NotFound,
        InternalServerError,
        Unauthorized,
        TooManyRequests
    }
}