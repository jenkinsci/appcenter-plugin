package io.jenkins.plugins.appcenter.model.appcenter;

import javax.annotation.Nonnull;
import java.util.Objects;

public final class ErrorDetails {

    @Nonnull
    public final CodeEnum code;

    @Nonnull
    public final String message;

    public ErrorDetails(@Nonnull CodeEnum code, @Nonnull String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String toString() {
        return "ErrorDetails{" +
            "code=" + code +
            ", message='" + message + '\'' +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ErrorDetails that = (ErrorDetails) o;
        return code == that.code &&
            message.equals(that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, message);
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