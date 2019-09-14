package io.jenkins.plugins.appcenter.model.appcenter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

public final class DestinationId {
    public final String name;
    public final String id;

    public DestinationId(@Nonnull String name, @Nullable String id) {
        this.name = name;
        this.id = id;
    }

    @Override
    public String toString() {
        return "DestinationId{" +
            "name='" + name + '\'' +
            ", id='" + id + '\'' +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DestinationId that = (DestinationId) o;
        return name.equals(that.name) &&
            Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, id);
    }
}