package io.jenkins.plugins.appcenter;

import okhttp3.ResponseBody;
import retrofit2.HttpException;
import retrofit2.Response;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.PrintStream;

import static java.util.Objects.requireNonNull;

public interface AppCenterLogger {

    PrintStream getLogger();

    default void log(String message) {
        getLogger().println(message);
    }

    default AppCenterException logFailure(@Nonnull String message) {
        requireNonNull(message, "message cannot be null.");
        return new AppCenterException(message);
    }

    default AppCenterException logFailure(@Nonnull String message, @Nonnull Throwable throwable) {
        requireNonNull(message, "message cannot be null.");
        requireNonNull(throwable, "throwable cannot be null.");

        // Error could be an HttPException or it could not be
        if (HttpException.class.isAssignableFrom(throwable.getClass())) {
            try {
                final HttpException httpException = (HttpException) throwable;
                final Response<?> response = requireNonNull(httpException.response(), "response cannot be null.");
                final ResponseBody responseBody = requireNonNull(response.errorBody(), "errorBody cannot be null.");
                final String json = responseBody.string();
                return logFailure(String.format("%1$s: %2$s: %3$s", message, httpException.getLocalizedMessage(), json));
            } catch (IOException e) {
                return new AppCenterException(message, e);
            }
        }

        return new AppCenterException(message, throwable);
    }
}