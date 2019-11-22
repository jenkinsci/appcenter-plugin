package io.jenkins.plugins.appcenter.model.appcenter;

import javax.annotation.Nullable;
import java.util.Objects;

public final class DestinationError {
    @Nullable
    public final String code;

    @Nullable
    public final String message;

    @Nullable
    public final String id;

    @Nullable
    public final String name;

    public DestinationError(@Nullable String code, @Nullable String message, @Nullable String id, @Nullable String name) {
        this.code = code;
        this.message = message;
        this.id = id;
        this.name = name;
    }

    @Override
    public String toString() {
        return "DestinationError{" +
            "code='" + code + '\'' +
            ", message='" + message + '\'' +
            ", id='" + id + '\'' +
            ", name='" + name + '\'' +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DestinationError that = (DestinationError) o;
        return Objects.equals(code, that.code) &&
            Objects.equals(message, that.message) &&
            Objects.equals(id, that.id) &&
            Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, message, id, name);
    }
}