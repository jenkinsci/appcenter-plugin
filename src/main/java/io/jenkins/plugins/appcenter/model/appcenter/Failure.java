package io.jenkins.plugins.appcenter.model.appcenter;

import javax.annotation.Nonnull;
import java.util.Objects;

public final class Failure {
    @Nonnull
    public final String message;

    public Failure(@Nonnull String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "Failure{" +
            "message='" + message + '\'' +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Failure failure = (Failure) o;
        return message.equals(failure.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(message);
    }
}