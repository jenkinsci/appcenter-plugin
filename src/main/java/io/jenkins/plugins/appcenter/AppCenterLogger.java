package io.jenkins.plugins.appcenter;

import java.io.PrintStream;

public interface AppCenterLogger {

    PrintStream getLogger();

    default void log(String message) {
        getLogger().println(message);
    }

    default AppCenterException logFailure(String message) {
        return logFailure(message, null);
    }

    default AppCenterException logFailure(String message, Throwable throwable) {
        final AppCenterException exception;

        if (throwable == null) {
            exception = new AppCenterException(message);
        } else {
            exception = new AppCenterException(message, throwable);
        }

        exception.printStackTrace(getLogger());

        return exception;
    }
}